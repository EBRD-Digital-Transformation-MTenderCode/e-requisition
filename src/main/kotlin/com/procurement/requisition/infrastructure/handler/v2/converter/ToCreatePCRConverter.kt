package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.StateFE
import com.procurement.requisition.application.service.model.command.CreatePCRCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.requirement.EligibleEvidenceType
import com.procurement.requisition.domain.model.requirement.ExpectedValue
import com.procurement.requisition.domain.model.requirement.MaxValue
import com.procurement.requisition.domain.model.requirement.MinValue
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asDocumentId
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.v2.model.request.CreatePCRRequest
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

fun CreatePCRRequest.convert(): Result<CreatePCRCommand, JsonErrors> {
    val cpid = cpid.asCpid().onFailure { return it.repath(path = "/cpid") }
    val date = date.asLocalDateTime().onFailure { return it.repath(path = "/date") }
    val stateFE = stateFE.asEnum(target = StateFE).onFailure { return it.repath(path = "/stateFE") }
    val tender = tender.convert().onFailure { return it.repath("/tender") }

    return CreatePCRCommand(
        cpid = cpid,
        date = date,
        stateFE = stateFE,
        owner = owner,
        tender = tender
    ).asSuccess()
}

/**
 * Tender
 */
fun CreatePCRRequest.Tender.convert(): Result<CreatePCRCommand.Tender, JsonErrors> {
    val classification = classification.convert().onFailure { return it.repath(path = "/classification") }

    val lots = lots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/lots")) }
        .mapIndexedOrEmpty { idx, lot -> lot.convert().onFailure { return it.repath(path = "/lots[$idx]") } }

    val items = items
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/items")) }
        .mapIndexedOrEmpty { idx, item ->
            item.convert().onFailure { return it.repath(path = "/items[$idx]") }
        }

    val targets = targets
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/targets")) }
        .mapIndexedOrEmpty { idx, target ->
            target.convert().onFailure { return it.repath(path = "/targets[$idx]") }
        }

    val criteria = criteria
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "criteria")) }
        .mapIndexedOrEmpty { idx, criterion ->
            criterion.convert().onFailure { return it.repath(path = "/criteria[$idx]") }
        }

    val conversions = conversions
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "conversions")) }
        .mapIndexedOrEmpty { idx, conversion ->
            conversion.convert().onFailure { return it.repath(path = "/conversions[$idx]") }
        }

    val procurementMethodModalities = procurementMethodModalities
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/procurementMethodModalities")) }
        .mapIndexedOrEmpty { idx, procurementMethodModality ->
            procurementMethodModality.asEnum(target = ProcurementMethodModality)
                .onFailure { return it.repath(path = "/procurementMethodModalities[$idx]") }
        }

    val awardCriteria = awardCriteria.asEnum(target = AwardCriteria)
        .onFailure { return it.repath(path = "/awardCriteria") }

    val awardCriteriaDetails = awardCriteriaDetails.asEnum(target = AwardCriteriaDetails)
        .onFailure { return it.repath(path = "/awardCriteriaDetails") }

    val electronicAuctions = electronicAuctions?.details
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "electronicAuctions.details")) }
        ?.map { detail -> detail.convert() }
        ?.let { details -> CreatePCRCommand.Tender.ElectronicAuctions(details) }

    val documents = documents
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "documents")) }
        .mapIndexedOrEmpty { idx, document ->
            document.convert().onFailure { return it.repath(path = "/documents[$idx]") }
        }

    return CreatePCRCommand.Tender(
        title = title,
        description = description,
        classification = classification,
        lots = lots,
        items = items,
        targets = targets,
        criteria = criteria,
        conversions = conversions,
        procurementMethodModalities = procurementMethodModalities,
        awardCriteria = awardCriteria,
        awardCriteriaDetails = awardCriteriaDetails,
        electronicAuctions = electronicAuctions,
        documents = documents,
        value = value.let { CreatePCRCommand.Tender.Value(currency = it.currency) }
    ).asSuccess()
}

/**
 * Classification
 */
fun CreatePCRRequest.Classification.convert(): Result<Classification, JsonErrors> {
    val scheme = scheme.asEnum(target = ClassificationScheme)
        .onFailure { return it.repath(path = "/scheme") }
    return Classification(id = id, scheme = scheme, description = description).asSuccess()
}

/**
 * Unit
 */
fun CreatePCRRequest.Unit.convert(): Result<CreatePCRCommand.Unit, JsonErrors> =
    CreatePCRCommand.Unit(id = id, name = name).asSuccess()

/**
 * Lot
 */
fun CreatePCRRequest.Tender.Lot.convert(): Result<CreatePCRCommand.Tender.Lot, JsonErrors> {
    val classification = classification.convert().onFailure { return it.repath(path = "/classification") }
    val variants = variants
        .map { variant ->
            variant.convert().onFailure { return it.repath(path = "/variants") }
        }

    return CreatePCRCommand.Tender.Lot(
        id = id,
        internalId = internalId,
        title = title,
        description = description,
        classification = classification,
        variants = variants,
    ).asSuccess()
}

fun CreatePCRRequest.Tender.Lot.Variant.convert(): Result<CreatePCRCommand.Tender.Lot.Variant, JsonErrors> =
    CreatePCRCommand.Tender.Lot.Variant(hasVariants = hasVariants, variantsDetails = variantsDetails).asSuccess()

/**
 * Item
 */
fun CreatePCRRequest.Tender.Item.convert(): Result<CreatePCRCommand.Tender.Item, JsonErrors> {
    val classification = classification.convert().onFailure { return it.repath(path = "/classification") }
    val unit = unit.convert().onFailure { return it.repath(path = "/unit") }

    return CreatePCRCommand.Tender.Item(
        id = id,
        internalId = internalId,
        description = description,
        quantity = quantity,
        classification = classification,
        unit = unit,
        relatedLot = relatedLot,
    ).asSuccess()
}

/**
 * Target
 */
fun CreatePCRRequest.Tender.Target.convert(): Result<CreatePCRCommand.Tender.Target, JsonErrors> {
    val relatesTo = relatesTo.asEnum(target = TargetRelatesTo)
        .onFailure { return it.repath(path = "/relatesTo") }

    val observations = observations
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "observations")) }
        .mapIndexedOrEmpty { observationIdx, observation ->
            observation.convert().onFailure { return it.repath(path = "/observations[$observationIdx]") }
        }

    return CreatePCRCommand.Tender.Target(
        id = id,
        title = title,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        observations = observations
    ).asSuccess()
}

fun CreatePCRRequest.Tender.Target.Observation.convert():
    Result<CreatePCRCommand.Tender.Target.Observation, JsonErrors> {

    val period = period?.convert()?.onFailure { return it.repath(path = "/period") }
    val unit = unit.convert().onFailure { return it.repath(path = "/unit") }
    val dimensions = dimensions?.convert()?.onFailure { return it.repath(path = "/dimensions") }

    return CreatePCRCommand.Tender.Target.Observation(
        id = id,
        period = period,
        measure = measure,
        unit = unit,
        dimensions = dimensions,
        notes = notes,
        relatedRequirementId = relatedRequirementId,
    ).asSuccess()
}

fun CreatePCRRequest.Tender.Target.Observation.Period.convert():
    Result<CreatePCRCommand.Tender.Target.Observation.Period, JsonErrors> {

    val startDate = startDate?.asLocalDateTime()?.onFailure { return it.repath(path = "/startDate") }
    val endDate = endDate?.asLocalDateTime()?.onFailure { return it.repath(path = "/endDate") }
    return CreatePCRCommand.Tender.Target.Observation.Period(startDate = startDate, endDate = endDate).asSuccess()
}

fun CreatePCRRequest.Tender.Target.Observation.Dimensions.convert():
    Result<CreatePCRCommand.Tender.Target.Observation.Dimensions, JsonErrors> =
    CreatePCRCommand.Tender.Target.Observation.Dimensions(requirementClassIdPR = requirementClassIdPR).asSuccess()

/**
 * Criterion
 */
val allowedRelatesTo = CriterionRelatesTo.allowedElements
    .asSequence()
    .filter {
        when (it) {
            CriterionRelatesTo.ITEM,
            CriterionRelatesTo.LOT -> true

            CriterionRelatesTo.AWARD,
            CriterionRelatesTo.TENDER,
            CriterionRelatesTo.TENDERER -> false
        }
    }
    .toSet()

fun CreatePCRRequest.Tender.Criterion.convert(): Result<CreatePCRCommand.Tender.Criterion, JsonErrors> {
    val relatesTo = relatesTo.asEnum(target = CriterionRelatesTo, allowedElements = allowedRelatesTo)
            .onFailure { return it.repath(path = "/relatesTo") }

    val requirementGroups = requirementGroups
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "requirementGroups")) }
        .mapIndexedOrEmpty { idx, requirementGroup ->
            requirementGroup.convert().onFailure { return it.repath(path = "/requirementGroups[$idx]") }
        }

    val classification = classification.convert().onFailure { return it.repath(path = "/classification") }

    return CreatePCRCommand.Tender.Criterion(
        id = id,
        title = title,
        description = description,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        requirementGroups = requirementGroups,
        classification = classification
    ).asSuccess()
}

/**
 * Classification
 */
fun CreatePCRRequest.Tender.Criterion.Classification.convert(): Result<CreatePCRCommand.Tender.Criterion.Classification, JsonErrors> {
    val scheme = scheme.asEnum(target = ClassificationScheme)
        .onFailure { return it.repath(path = "/scheme") }
    return CreatePCRCommand.Tender.Criterion.Classification(id = id, scheme = scheme).asSuccess()
}

fun CreatePCRRequest.Tender.Criterion.RequirementGroup.convert(): Result<CreatePCRCommand.Tender.Criterion.RequirementGroup, JsonErrors> {
    val requirements = requirements
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "requirements")) }
        .mapIndexed { idx, requirement ->
            requirement.convert().onFailure { return it.repath(path = "/requirements[$idx]") }
        }

    return CreatePCRCommand.Tender.Criterion.RequirementGroup(
        id = id,
        description = description,
        requirements = requirements,
    ).asSuccess()
}

/**
 * Requirement
 */
fun CreatePCRRequest.Tender.Criterion.RequirementGroup.Requirement.convert():
    Result<CreatePCRCommand.Tender.Criterion.RequirementGroup.Requirement, JsonErrors> {

    val period = period?.convert()?.onFailure { return it.repath(path = "/period") }
    val dataType = dataType.asEnum(target = DynamicValue.DataType)
        .onFailure { return it.repath(path = "/dataType") }

    val eligibleEvidences = eligibleEvidences
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "eligibleEvidences")) }
        .mapIndexedOrEmpty { idx, eligibleEvidence ->
            eligibleEvidence.convert().onFailure { return it.repath(path = "/eligibleEvidences[$idx]") }
        }

    return CreatePCRCommand.Tender.Criterion.RequirementGroup.Requirement(
        id = id,
        title = title,
        description = description,
        period = period,
        dataType = dataType,
        expectedValue = expectedValue?.let { ExpectedValue(it) },
        minValue = minValue?.let { MinValue(it) },
        maxValue = maxValue?.let { MaxValue(it) },
        eligibleEvidences = eligibleEvidences
    ).asSuccess()
}

/**
 * Requirement.Period
 */
fun CreatePCRRequest.Tender.Criterion.RequirementGroup.Requirement.Period.convert():
    Result<CreatePCRCommand.Tender.Criterion.RequirementGroup.Requirement.Period, JsonErrors> {
    val startDate = startDate.asLocalDateTime().onFailure { return it.repath(path = "/startDate") }
    val endDate = endDate.asLocalDateTime().onFailure { return it.repath(path = "/endDate") }
    return CreatePCRCommand.Tender.Criterion.RequirementGroup.Requirement.Period(
        startDate = startDate,
        endDate = endDate
    ).asSuccess()
}

/**
 * Requirement.eligibleEvidence
 */

fun CreatePCRRequest.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence.convert():
    Result<CreatePCRCommand.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence, JsonErrors> {
    val type = type.asEnum(target = EligibleEvidenceType)
        .onFailure { return it.repath(path = "/type") }

    val relatedDocument = relatedDocument?.convert()
        ?.onFailure { return it.repath(path = "/relatedDocument") }

    return CreatePCRCommand.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence(
        id = id,
        title = title,
        description = description,
        type = type,
        relatedDocument = relatedDocument
    ).asSuccess()
}

fun CreatePCRRequest.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence.DocumentReference.convert():
    Result<CreatePCRCommand.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence.DocumentReference, JsonErrors> {
    val id = id.asDocumentId().onFailure { return it.repath(path = "/id") }

    return CreatePCRCommand.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence.DocumentReference(
        id = id
    ).asSuccess()
}
/**
 * Conversion
 */
fun CreatePCRRequest.Tender.Conversion.convert(): Result<CreatePCRCommand.Tender.Conversion, JsonErrors> {
    val relatesTo = relatesTo.asEnum(target = ConversionRelatesTo)
        .onFailure { return it.repath(path = "/relatesTo") }

    val coefficients = coefficients
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/coefficients")) }
        .mapIndexedOrEmpty { idx, coefficient ->
            coefficient.convert().onFailure { return it.repath(path = "/coefficients[$idx]") }
        }

    return CreatePCRCommand.Tender.Conversion(
        id = id,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        rationale = rationale,
        description = description,
        coefficients = coefficients
    ).asSuccess()
}

fun CreatePCRRequest.Tender.Conversion.Coefficient.convert(): Result<CreatePCRCommand.Tender.Conversion.Coefficient, JsonErrors> =
    CreatePCRCommand.Tender.Conversion.Coefficient(id = id, value = value, coefficient = coefficient).asSuccess()

/**
 * Document
 */
fun CreatePCRRequest.Tender.Document.convert(): Result<CreatePCRCommand.Tender.Document, JsonErrors> {
    val id = id.asDocumentId().onFailure { return it.repath(path = "/id") }
    val documentType = documentType.asEnum(target = DocumentType)
        .onFailure { return it.repath(path = "/documentType") }
    val relatedLots = relatedLots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/relatedLots")) }
        ?.toList()
        .orEmpty()

    return CreatePCRCommand.Tender.Document(
        id = id,
        documentType = documentType,
        title = title,
        description = description,
        relatedLots = relatedLots
    ).asSuccess()
}

/**
 * ElectronicAuction
 */
fun CreatePCRRequest.Tender.ElectronicAuctions.Detail.convert(): CreatePCRCommand.Tender.ElectronicAuctions.Detail =
    CreatePCRCommand.Tender.ElectronicAuctions.Detail(id = id, relatedLot = relatedLot)
