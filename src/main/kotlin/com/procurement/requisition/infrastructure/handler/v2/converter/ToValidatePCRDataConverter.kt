package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.OperationType
import com.procurement.requisition.application.service.model.command.ValidatePCRDataCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.MainProcurementCategory
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.requirement.EligibleEvidenceType
import com.procurement.requisition.domain.model.requirement.ExpectedValue
import com.procurement.requisition.domain.model.requirement.MaxValue
import com.procurement.requisition.domain.model.requirement.MinValue
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.v2.model.request.ValidatePCRDataRequest
import com.procurement.requisition.lib.failureIfBlank
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

fun ValidatePCRDataRequest.convert(): Result<ValidatePCRDataCommand, JsonErrors> {
    val tender = tender.convert()
        .onFailure { return it.repath(path = "/tender") }

    val pmd = pmd
        .asEnum(target = ProcurementMethodDetails, allowedElements = allowedProcurementMethodDetails)
        .onFailure { return it.repath(path = "/pmd") }

    val operationType = operationType
        .asEnum(target = OperationType, allowedElements = allowedOperationType)
        .onFailure { return it.repath(path = "/operationType") }

    val mdm = mdm.convert()
        .onFailure { return it.repath(path = "/mdm") }

    return ValidatePCRDataCommand(tender, country, pmd, operationType, mdm).asSuccess()
}

private val allowedProcurementMethodDetails = ProcurementMethodDetails.allowedElements
    .asSequence()
    .filter {
        when (it) {
            ProcurementMethodDetails.CF, ProcurementMethodDetails.TEST_CF,
            ProcurementMethodDetails.OF, ProcurementMethodDetails.TEST_OF -> true

            ProcurementMethodDetails.CD, ProcurementMethodDetails.TEST_CD,
            ProcurementMethodDetails.DA, ProcurementMethodDetails.TEST_DA,
            ProcurementMethodDetails.DC, ProcurementMethodDetails.TEST_DC,
            ProcurementMethodDetails.DCO, ProcurementMethodDetails.TEST_DCO,
            ProcurementMethodDetails.FA, ProcurementMethodDetails.TEST_FA,
            ProcurementMethodDetails.GPA, ProcurementMethodDetails.TEST_GPA,
            ProcurementMethodDetails.IP, ProcurementMethodDetails.TEST_IP,
            ProcurementMethodDetails.MC, ProcurementMethodDetails.TEST_MC,
            ProcurementMethodDetails.MV, ProcurementMethodDetails.TEST_MV,
            ProcurementMethodDetails.NP, ProcurementMethodDetails.TEST_NP,
            ProcurementMethodDetails.OP, ProcurementMethodDetails.TEST_OP,
            ProcurementMethodDetails.OT, ProcurementMethodDetails.TEST_OT,
            ProcurementMethodDetails.RFQ, ProcurementMethodDetails.TEST_RFQ,
            ProcurementMethodDetails.RT, ProcurementMethodDetails.TEST_RT,
            ProcurementMethodDetails.SV, ProcurementMethodDetails.TEST_SV -> false
        }
    }
    .toSet()

private val allowedOperationType = OperationType.allowedElements
    .asSequence()
    .filter {
        when (it) {
            OperationType.CREATE_PCR -> true

            OperationType.AWARD_CONSIDERATION,
            OperationType.COMPLETE_SOURCING,
            OperationType.CREATE_CONFIRMATION_RESPONSE_BY_SUPPLIER,
            OperationType.CREATE_RFQ,
            OperationType.NEXT_STEP_AFTER_SUPPLIERS_CONFIRMATION,
            OperationType.PCR_PROTOCOL,
            OperationType.SUBMIT_BID_IN_PCR,
            OperationType.TENDER_PERIOD_END_AUCTION_IN_PCR,
            OperationType.TENDER_PERIOD_END_IN_PCR,
            OperationType.WITHDRAW_BID,
            OperationType.WITHDRAW_PCR_PROTOCOL-> false
        }
    }
    .toSet()

/**
 * Tender
 */
fun ValidatePCRDataRequest.Tender.convert(): Result<ValidatePCRDataCommand.Tender, JsonErrors> {
    val title = title.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/title")) }
    val description = description.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/description")) }

    val classification = classification.convert()
        .onFailure { return it.repath(path = "/classification") }
    val lots = lots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "lots")) }
        .mapIndexedOrEmpty { idx, lot ->
            lot.convert().onFailure { return it.repath(path = "/lots[$idx]") }
        }
    val items = items
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "items")) }
        .mapIndexedOrEmpty { idx, item ->
            item.convert().onFailure { return it.repath(path = "/items[$idx]") }
        }
    val targets = targets
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "targets")) }
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
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "procurementMethodModalities")) }
        .mapIndexedOrEmpty { idx, procurementMethodModality ->
            procurementMethodModality.asEnum(target = ProcurementMethodModality)
                .onFailure { return it.repath(path = "/procurementMethodModalities[$idx]") }
        }
    val mainProcurementCategory = mainProcurementCategory.asEnum(MainProcurementCategory)
        .onFailure { return it.repath(path = "/procurementMethodModalities") }

    val awardCriteria = awardCriteria.asEnum(target = AwardCriteria)
        .onFailure { return it.repath(path = "/awardCriteria") }
    val awardCriteriaDetails =
        awardCriteriaDetails.asEnum(target = AwardCriteriaDetails)
            .onFailure { return it.repath(path = "/awardCriteriaDetails") }

    val documents = documents
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "documents")) }
        .mapIndexedOrEmpty { idx, document ->
            document.convert().onFailure { return it.repath(path = "/documents[$idx]") }
        }
    val electronicAuctions = electronicAuctions?.convert()
        ?.onFailure { return it.repath(path = "/electronicAuctions") }


    return ValidatePCRDataCommand.Tender(
        title = title,
        description = description,
        classification = classification,
        lots = lots,
        items = items,
        targets = targets,
        criteria = criteria,
        conversions = conversions,
        procurementMethodModalities = procurementMethodModalities,
        mainProcurementCategory = mainProcurementCategory,
        awardCriteria = awardCriteria,
        awardCriteriaDetails = awardCriteriaDetails,
        documents = documents,
        electronicAuctions = electronicAuctions
    ).asSuccess()
}

private fun ValidatePCRDataRequest.Tender.ElectronicAuctions.convert(): Result<ValidatePCRDataCommand.Tender.ElectronicAuctions, JsonErrors> {
    val details = details
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "details")) }
        .map { detail -> ValidatePCRDataCommand.Tender.ElectronicAuctions.Detail(detail.id) }

    return ValidatePCRDataCommand.Tender.ElectronicAuctions(details).asSuccess()
}

/**
 * Classification
 */
fun ValidatePCRDataRequest.Classification.convert(): Result<ValidatePCRDataCommand.Classification, JsonErrors> {
    val scheme = scheme.asEnum(target = ClassificationScheme)
        .onFailure { return it.repath(path = "/scheme") }
    return ValidatePCRDataCommand.Classification(id = id, scheme = scheme).asSuccess()
}

/**
 * Unit
 */
fun ValidatePCRDataRequest.Unit.convert(): Result<ValidatePCRDataCommand.Unit, JsonErrors> =
    ValidatePCRDataCommand.Unit(id = this.id).asSuccess()

/**
 * Lot
 */
fun ValidatePCRDataRequest.Tender.Lot.convert(): Result<ValidatePCRDataCommand.Tender.Lot, JsonErrors> {
    val internalId = internalId.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/internalId")) }
    val title = title.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/title")) }
    val description = description.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/description")) }

    val classification = classification.convert().onFailure { return it.repath(path = "/classification") }
    val variants = variants.map { variant ->
        variant.convert().onFailure { return it.repath(path = "/variants") }
    }

    return ValidatePCRDataCommand.Tender.Lot(
        id = id,
        internalId = internalId,
        title = title,
        description = description,
        classification = classification,
        variants = variants,
    ).asSuccess()
}

fun ValidatePCRDataRequest.Tender.Lot.Variant.convert(): Result<ValidatePCRDataCommand.Tender.Lot.Variant, JsonErrors> {
    val variantsDetails = variantsDetails.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/variantsDetails")) }
    return ValidatePCRDataCommand.Tender.Lot.Variant(hasVariants = hasVariants, variantsDetails = variantsDetails).asSuccess()
}

/**
 * Item
 */
fun ValidatePCRDataRequest.Tender.Item.convert(): Result<ValidatePCRDataCommand.Tender.Item, JsonErrors> {
    val internalId = internalId.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/internalId")) }
    val description = description.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/description")) }

    val classification = classification.convert().onFailure { return it.repath(path = "/classification") }
    val unit = unit.convert().onFailure { return it.repath(path = "/unit") }
    return ValidatePCRDataCommand.Tender.Item(
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
fun ValidatePCRDataRequest.Tender.Target.convert(): Result<ValidatePCRDataCommand.Tender.Target, JsonErrors> {
    val relatesTo = relatesTo.asEnum(target = TargetRelatesTo)
        .onFailure { return it.repath(path = "/relatesTo") }

    val observations = observations
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "observations")) }
        .mapIndexedOrEmpty { observationIdx, observation ->
            observation.convert().onFailure { return it.repath(path = "/observations[$observationIdx]") }
        }

    return ValidatePCRDataCommand.Tender.Target(
        id = id,
        title = title,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        observations = observations
    ).asSuccess()
}

fun ValidatePCRDataRequest.Tender.Target.Observation.convert():
    Result<ValidatePCRDataCommand.Tender.Target.Observation, JsonErrors> {

    val period = period?.convert()?.onFailure { return it.repath(path = "/period") }
    val unit = unit.convert().onFailure { return it.repath(path = "/unit") }
    val dimensions = dimensions?.convert()?.onFailure { return it.repath(path = "/dimensions") }

    return ValidatePCRDataCommand.Tender.Target.Observation(
        id = id,
        period = period,
        measure = measure,
        unit = unit,
        dimensions = dimensions,
        notes = notes,
        relatedRequirementId = relatedRequirementId,
    ).asSuccess()
}

fun ValidatePCRDataRequest.Tender.Target.Observation.Period.convert():
    Result<ValidatePCRDataCommand.Tender.Target.Observation.Period, JsonErrors> {

    val startDate = startDate?.asLocalDateTime()?.onFailure { return it.repath(path = "/startDate") }
    val endDate = endDate?.asLocalDateTime()?.onFailure { return it.repath(path = "/endDate") }
    return ValidatePCRDataCommand.Tender.Target.Observation.Period(startDate = startDate, endDate = endDate).asSuccess()
}

fun ValidatePCRDataRequest.Tender.Target.Observation.Dimensions.convert():
    Result<ValidatePCRDataCommand.Tender.Target.Observation.Dimensions, JsonErrors> =
    ValidatePCRDataCommand.Tender.Target.Observation.Dimensions(requirementClassIdPR = requirementClassIdPR).asSuccess()

/**
 * Criterion
 */
fun ValidatePCRDataRequest.Tender.Criterion.convert(): Result<ValidatePCRDataCommand.Tender.Criterion, JsonErrors> {
    val title = title.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/title")) }
    val description = description.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/description")) }

    val relatesTo = relatesTo.asEnum(target = CriterionRelatesTo)
        .onFailure { return it.repath(path = "/relatesTo") }

    val requirementGroups = requirementGroups
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "requirementGroups")) }
        .mapIndexedOrEmpty { idx, requirementGroup ->
            requirementGroup.convert().onFailure { return it.repath(path = "/requirementGroups[$idx]") }
        }

    val classification = classification.convert()

    return ValidatePCRDataCommand.Tender.Criterion(
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
fun ValidatePCRDataRequest.CriterionClassification.convert(): ValidatePCRDataCommand.CriterionClassification =
    ValidatePCRDataCommand.CriterionClassification(id = id, scheme = scheme)

fun ValidatePCRDataRequest.Tender.Criterion.RequirementGroup.convert(): Result<ValidatePCRDataCommand.Tender.Criterion.RequirementGroup, JsonErrors> {
    val description = description.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/description")) }

    val requirements = requirements
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "requirements")) }
        .mapIndexed { idx, requirement ->
            requirement.convert().onFailure { return it.repath(path = "/requirements[$idx]") }
        }

    return ValidatePCRDataCommand.Tender.Criterion.RequirementGroup(
        id = id,
        description = description,
        requirements = requirements,
    ).asSuccess()
}

/**
 * Requirement
 */
fun ValidatePCRDataRequest.Tender.Criterion.RequirementGroup.Requirement.convert():
    Result<ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement, JsonErrors> {
    val title = title.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/title")) }
    val description = title.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/description")) }

    val period = period?.convert()?.onFailure { return it.repath(path = "/period") }
    val dataType = dataType.asEnum(target = DynamicValue.DataType)
        .onFailure { return it.repath(path = "/dataType") }

    val eligibleEvidences = eligibleEvidences
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/eligibleEvidences")) }
        .mapIndexedOrEmpty { idx, eligibleEvidence ->
            eligibleEvidence.convert().onFailure { return it.repath(path = "/eligibleEvidences[$idx]") }
        }

    return ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement(
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
fun ValidatePCRDataRequest.Tender.Criterion.RequirementGroup.Requirement.Period.convert():
    Result<ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement.Period, JsonErrors> {
    val startDate = startDate.asLocalDateTime().onFailure { return it.repath(path = "/startDate") }
    val endDate = endDate.asLocalDateTime().onFailure { return it.repath(path = "/endDate") }
    return ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement.Period(
        startDate = startDate,
        endDate = endDate
    ).asSuccess()
}

/**
 * Requirement.eligibleEvidence
 */
fun ValidatePCRDataRequest.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence.convert():
    Result<ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence, JsonErrors> {
    val type = type.asEnum(target = EligibleEvidenceType)
        .onFailure { return it.repath(path = "/type") }

    return ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence(
        id = id,
        title = title,
        description = description,
        type = type,
        relatedDocument = relatedDocument?.let {
            ValidatePCRDataCommand.Tender.Criterion.RequirementGroup.Requirement.EligibleEvidence.RelatedDocument(it.id)
        }
    ).asSuccess()
}

/**
 * Conversion
 */
fun ValidatePCRDataRequest.Tender.Conversion.convert(): Result<ValidatePCRDataCommand.Tender.Conversion, JsonErrors> {
    val rationale = rationale.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/rationale")) }
    val description = description.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/description")) }

    val relatesTo = relatesTo.asEnum(target = ConversionRelatesTo)
        .onFailure { return it.repath(path = "/relatesTo") }

    val coefficients = coefficients
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "coefficients")) }
        .mapIndexedOrEmpty { idx, coefficient ->
            coefficient.convert().onFailure { return it.repath(path = "/coefficients[$idx]") }
        }

    return ValidatePCRDataCommand.Tender.Conversion(
        id = id,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        rationale = rationale,
        description = description,
        coefficients = coefficients
    ).asSuccess()
}

fun ValidatePCRDataRequest.Tender.Conversion.Coefficient.convert(): Result<ValidatePCRDataCommand.Tender.Conversion.Coefficient, JsonErrors> =
    ValidatePCRDataCommand.Tender.Conversion.Coefficient(id = id, value = value, coefficient = coefficient).asSuccess()

/**
 * Document
 */
fun ValidatePCRDataRequest.Tender.Document.convert(): Result<ValidatePCRDataCommand.Tender.Document, JsonErrors> {
    val title = title.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/title")) }
    val description = description.failureIfBlank { return failure(JsonErrors.EmptyString().repath(path = "/description")) }

    val documentType = documentType.asEnum(target = DocumentType)
        .onFailure { return it.repath(path = "/documentType") }
    val relatedLots = relatedLots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "relatedLots")) }
        .orEmpty()

    return ValidatePCRDataCommand.Tender.Document(
        id = id,
        documentType = documentType,
        title = title,
        description = description,
        relatedLots = relatedLots
    ).asSuccess()
}


/**
 * Mdm
 */
fun ValidatePCRDataRequest.Mdm.convert(): Result<ValidatePCRDataCommand.Mdm, JsonErrors> {
    val criteria = criteria
        .failureIfEmpty { return failure(JsonErrors.EmptyArray().repath(path = "/criteria")) }
        .map { criterion -> criterion.convert() }

    return ValidatePCRDataCommand.Mdm(criteria = criteria).asSuccess()
}

/**
 * Mdm.Criterion
 */
fun ValidatePCRDataRequest.Mdm.Criterion.convert(): ValidatePCRDataCommand.Mdm.Criterion {
    val classification = classification.convert()

    return ValidatePCRDataCommand.Mdm.Criterion(id = id, classification = classification)
}
