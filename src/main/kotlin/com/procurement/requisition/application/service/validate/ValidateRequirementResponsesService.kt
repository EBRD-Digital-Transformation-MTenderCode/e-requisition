package com.procurement.requisition.application.service.validate

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.service.validate.error.ValidateRequirementResponsesErrors
import com.procurement.requisition.application.service.validate.model.ValidateRequirementResponsesCommand
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementDataType
import com.procurement.requisition.domain.model.requirement.RequirementId
import com.procurement.requisition.domain.model.requirement.RequirementRsValue
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.criterion.Criteria
import com.procurement.requisition.domain.model.tender.criterion.Criterion
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Validated
import com.procurement.requisition.lib.functional.asValidatedError
import com.procurement.requisition.lib.isUnique
import com.procurement.requisition.lib.toSet
import org.springframework.stereotype.Service

@Service
class ValidateRequirementResponsesService(
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
) {

    fun validate(command: ValidateRequirementResponsesCommand): Validated<Failure> {

        val pcr = pcrRepository.getPCR(cpid = command.cpid, ocid = command.ocid)
            .onFailure { return it.reason.asValidatedError() }
            ?.let { json -> pcrDeserializer.build(json) }
            ?.onFailure { return it.reason.asValidatedError() }
            ?: return ValidateRequirementResponsesErrors.PCRNotFound(cpid = command.cpid, ocid = command.ocid)
                .asValidatedError()

        val criteria = pcr.tender.criteria

        val detail = command.bids.details[0]

        // VR.COM-17.9.2
        val receivedRequirementResponseIdsIsUnique = detail.requirementResponses.isUnique { it.id }
        if (!receivedRequirementResponseIdsIsUnique)
            return Validated.error(ValidateRequirementResponsesErrors.RequirementResponse.DuplicateId)

        // VR.COM-17.9.7
        val receivedRequirementIdsIsUnique = detail.requirementResponses.isUnique { it.requirement.id }
        if (!receivedRequirementIdsIsUnique)
            return Validated.error(ValidateRequirementResponsesErrors.RequirementResponse.Requirement.DuplicateId)

        val requirementResponsesChecker =
            RequirementResponsesChecker(detail.requirementResponses.map { it.requirement.id })

        criteria.ofTender()
            .forEach { criterion ->
                val checker = criterion.toChecker()
                requirementResponsesChecker.submitting(checker)
                checker.check().onFailure { return it }
            }

        criteria.ofLots(ids = detail.relatedLots)
            .forEach { criterion ->
                val checker = criterion.toChecker()
                requirementResponsesChecker.submitting(checker)
                checker.check().onFailure { return it }
            }

        val itemIds = if (command.isRequiresElectronicCatalogue)
            detail.items.map { it.id }
        else
            pcr.tender.items
                .asSequence()
                .filter { item -> item.relatedLot in detail.relatedLots }
                .map { item -> item.id }
                .toList()

        criteria.ofItems(ids = itemIds)
            .forEach { criterion ->
                val checker = criterion.toChecker()
                requirementResponsesChecker.submitting(checker)
                checker.check().onFailure { return it }
            }

        // VR.COM-17.9.6
        val notSubmitted = requirementResponsesChecker.getNotSubmitted()
        if (notSubmitted.isNotEmpty())
            return Validated.error(ValidateRequirementResponsesErrors.RequirementResponse.Unknown(ids = notSubmitted))

        // VR.COM-17.9.3
        checkRequirementResponsesDataType(criteria = criteria, requirementResponses = detail.requirementResponses)

        // VR.COM-17.9.8
        detail.requirementResponses.forEach { rr ->
            if (rr.period != null && rr.period.isInvalid())
                return Validated.error(ValidateRequirementResponsesErrors.RequirementResponse.InvalidPeriod())
        }

        return Validated.ok()
    }

    val ValidateRequirementResponsesCommand.isRequiresElectronicCatalogue: Boolean
        get() = tender != null && ProcurementMethodModality.REQUIRES_ELECTRONIC_CATALOGUE in tender.procurementMethodModalities

    fun Criteria.ofTender() = filter { it.relatesTo == null || it.relatesTo == CriterionRelatesTo.TENDER }

    fun Criteria.ofLots(ids: List<LotId>): List<Criterion> {
        val lotIds = ids.toSet { it.underlying }
        return asSequence()
            .filter { it.relatesTo == CriterionRelatesTo.LOT && it.relatedItem in lotIds }
            .toList()
    }

    fun Criteria.ofItems(ids: List<ItemId>): List<Criterion> {
        val itemIds = ids.toSet { it.underlying }
        return asSequence()
            .filter { it.relatesTo == CriterionRelatesTo.ITEM && it.relatedItem in itemIds }
            .toList()
    }

    fun Criterion.toChecker() = requirementGroups
        .map { requirementGroup ->
            val requirementIds = requirementGroup.requirements.map { it.id }
            RequirementGroupChecker(requirementIds = requirementIds)
        }
        .let { requirementGroups -> CriterionChecker(id, requirementGroups) }

    /**
     * VR.COM-17.9.4 - VR.COM-17.9.5
     */
    fun CriterionChecker.check(): Validated<ValidateRequirementResponsesErrors.RequirementResponse> =
        when (state) {

            // VR.COM-17.9.4
            CriterionChecker.State.EMPTY ->
                ValidateRequirementResponsesErrors.RequirementResponse.EmptyCriterion(criterionId = id)
                    .asValidatedError()

            // VR.COM-17.9.5
            CriterionChecker.State.MULTI_GROUP ->
                ValidateRequirementResponsesErrors.RequirementResponse.MultiGroup(criterionId = id).asValidatedError()

            CriterionChecker.State.OK -> Validated.ok()
        }

    /**
     * VR.COM-17.9.3
     */
    fun checkRequirementResponsesDataType(
        criteria: Criteria,
        requirementResponses: List<ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse>
    ): Validated<ValidateRequirementResponsesErrors.RequirementResponse.Requirement.InvalidDataType> {
        val allRequirementByIds: Map<RequirementId, Requirement> = criteria.asSequence()
            .flatMap { criterion -> criterion.requirementGroups }
            .flatMap { requirementGroup -> requirementGroup.requirements }
            .map { requirement -> requirement.id to requirement }
            .toMap()

        requirementResponses
            .forEachIndexed { idx, requirementResponse ->
                val requirement = allRequirementByIds.getValue(requirementResponse.requirement.id)
                requirementResponse.value
                    .validateDataType(path = "#/params/tender/requirementResponses[$idx]", requirement.dataType)
                    .onFailure { return it }
            }
        return Validated.ok()
    }

    fun RequirementRsValue.validateDataType(path: String, requirementDataType: RequirementDataType):
        Validated<ValidateRequirementResponsesErrors.RequirementResponse.Requirement.InvalidDataType> {
        val isInvalidType = when (this) {
            is RequirementRsValue.AsBoolean -> requirementDataType != RequirementDataType.BOOLEAN
            is RequirementRsValue.AsString -> requirementDataType != RequirementDataType.STRING
            is RequirementRsValue.AsNumber -> requirementDataType != RequirementDataType.NUMBER
            is RequirementRsValue.AsInteger -> requirementDataType != RequirementDataType.INTEGER
        }
        return if (isInvalidType)
            Validated.error(ValidateRequirementResponsesErrors.RequirementResponse.Requirement.InvalidDataType(path = path))
        else
            Validated.ok()
    }

    fun ValidateRequirementResponsesCommand.Bids.Detail.RequirementResponse.Period.isInvalid(): Boolean =
        !startDate.isBefore(endDate)
}

class RequirementResponsesChecker(requirementIds: List<RequirementId>) {

    private val requirementDescriptions: MutableMap<RequirementId, Boolean> = requirementIds.asSequence()
        .map { id -> id to false }
        .toMap(mutableMapOf())

    fun submitting(criterion: CriterionChecker) {
        requirementDescriptions.forEach { (id, submitted) ->
            if (!submitted) {
                if (criterion.submitResponse(id)) requirementDescriptions[id] = true
            }
        }
    }

    fun getNotSubmitted() = requirementDescriptions.asSequence()
        .filter { !it.value }
        .map { it.key }
        .toList()
}

class CriterionChecker(val id: CriterionId, private val requirementGroups: List<RequirementGroupChecker>) {

    fun submitResponse(id: RequirementId): Boolean {
        requirementGroups.forEach { group ->
            if (group.submitResponse(id)) return true
        }
        return false
    }

    val state: State
        get() {
            var completely = 0
            requirementGroups.forEach { group ->
                when (group.state) {
                    RequirementGroupChecker.State.EMPTY -> Unit

                    RequirementGroupChecker.State.PARTIALLY,
                    RequirementGroupChecker.State.COMPLETELY -> {
                        completely++
                        if (completely > 1) return State.MULTI_GROUP
                    }
                }
            }

            return if (completely == 0) State.EMPTY else State.OK
        }

    enum class State {
        EMPTY, //VR.COM-17.9.4
        MULTI_GROUP, //VR.COM-17.9.5
        OK
    }
}

class RequirementGroupChecker(requirementIds: List<RequirementId>) {

    private val map: MutableMap<RequirementId, Boolean> = requirementIds
        .asSequence()
        .map { it to false }
        .toMap(mutableMapOf())

    fun submitResponse(id: RequirementId): Boolean = if (id in map) {
        map[id] = true
        true
    } else
        false

    val state: State
        get() = when (map.values.fold(0) { acc, hasResponse -> if (hasResponse) acc + 1 else acc }) {
            0 -> State.EMPTY
            map.size -> State.COMPLETELY
            else -> State.PARTIALLY
        }

    enum class State { EMPTY, PARTIALLY, COMPLETELY }
}
