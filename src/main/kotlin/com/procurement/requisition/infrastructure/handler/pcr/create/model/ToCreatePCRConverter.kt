package com.procurement.requisition.infrastructure.handler.pcr.create.model

import com.procurement.requisition.application.service.create.model.CreatePCR
import com.procurement.requisition.application.service.create.model.StateFE
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.document.DocumentId
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.target.observation.ObservationMeasure
import com.procurement.requisition.infrastructure.bind.classification.ClassificationScheme
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.failureIfEmpty
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

fun CreatePCRParams.convert(): Result<CreatePCR, Failure> {
    val cpid = Cpid.tryCreateOrNull(cpid)
        ?: return failure(
            JsonErrors.DataFormatMismatch(
                path = "#/cpid",
                actualValue = cpid,
                expectedFormat = Cpid.pattern,
                reason = null
            )
        )
    val date = date.asLocalDateTime(path = "#/date")
        .onFailure { return it }

    val stateFE = stateFE.asEnum(target = StateFE, path = "#/stateFE")
        .onFailure { return it }

    val tender = tender.convert("#/tender")
        .onFailure { return it }

    return CreatePCR(
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
fun CreatePCRParams.Tender.convert(path: String): Result<CreatePCR.Tender, Failure> {
    val classification = classification.convert(path = "$path/classification")
        .onFailure { return it }

    val lots = lots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/lots")) }
        .mapIndexedOrEmpty { idx, lot -> lot.convert(path = "$path/lots[$idx]").onFailure { return it } }

    val items = items
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/items")) }
        .mapIndexedOrEmpty { idx, item ->
            item.convert(path = "$path/items[$idx]").onFailure { return it }
        }

    val targets = targets
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/targets")) }
        .mapIndexedOrEmpty { idx, target ->
            target.convert(path = "$path/targets[$idx]").onFailure { return it }
        }

    val criteria = criteria
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/criteria")) }
        .mapIndexedOrEmpty { idx, criterion ->
            criterion.convert(path = "$path/criteria[$idx]").onFailure { return it }
        }

    val conversions = conversions
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/conversions")) }
        .mapIndexedOrEmpty { idx, conversion ->
            conversion.convert(path = "$path/conversions[$idx]").onFailure { return it }
        }

    val procurementMethodModalities = procurementMethodModalities
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/procurementMethodModalities")) }
        .mapIndexedOrEmpty { idx, procurementMethodModality ->
            procurementMethodModality.asEnum(
                target = ProcurementMethodModality,
                path = "$path/procurementMethodModalities[$idx]"
            )
                .onFailure { return it }
        }

    val awardCriteria = awardCriteria.asEnum(target = AwardCriteria, path = "$path/awardCriteria")
        .onFailure { return it }

    val awardCriteriaDetails =
        awardCriteriaDetails.asEnum(target = AwardCriteriaDetails, path = "$path/awardCriteriaDetails")
            .onFailure { return it }

    val documents = documents
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/documents")) }
        .mapIndexedOrEmpty { idx, document ->
            document.convert(path = "$path/documents[$idx]").onFailure { return it }
        }

    return CreatePCR.Tender(
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
        documents = documents,
        value = value.let { CreatePCR.Tender.Value(currency = it.currency) }
    ).asSuccess()
}

/**
 * Classification
 */
fun CreatePCRParams.Classification.convert(path: String): Result<Classification, Failure> {
    val scheme = scheme.asEnum(target = ClassificationScheme, path = "$path/scheme")
        .onFailure { return it }
    return Classification(id = id, scheme = scheme, description = description).asSuccess()
}

/**
 * Unit
 */
fun CreatePCRParams.Unit.convert(path: String): Result<CreatePCR.Unit, Failure> =
    CreatePCR.Unit(id = id, name = name).asSuccess()

/**
 * Lot
 */
fun CreatePCRParams.Tender.Lot.convert(path: String): Result<CreatePCR.Tender.Lot, Failure> {
    val classification = classification.convert(path = "$path/classification").onFailure { return it }
    val variants = variants.convert(path = "$path/variants").onFailure { return it }

    return CreatePCR.Tender.Lot(
        id = id,
        internalId = internalId,
        title = title,
        description = description,
        classification = classification,
        variants = variants,
    ).asSuccess()
}

fun CreatePCRParams.Tender.Lot.Variant.convert(path: String): Result<CreatePCR.Tender.Lot.Variant, Failure> =
    CreatePCR.Tender.Lot.Variant(hasVariants = hasVariants, variantsDetails = variantsDetails).asSuccess()

/**
 * Item
 */
fun CreatePCRParams.Tender.Item.convert(path: String): Result<CreatePCR.Tender.Item, Failure> {
    val classification = classification.convert("$path/classification").onFailure { return it }
    val unit = unit.convert("$path/unit").onFailure { return it }

    return CreatePCR.Tender.Item(
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
fun CreatePCRParams.Tender.Target.convert(path: String): Result<CreatePCR.Tender.Target, Failure> {
    val relatesTo = relatesTo.asEnum(target = TargetRelatesTo, path = "$path/relatesTo")
        .onFailure { return it }

    val observations = observations
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/observations")) }
        .mapIndexedOrEmpty { observationIdx, observation ->
            observation.convert("$path/observations[$observationIdx]").onFailure { return it }
        }

    return CreatePCR.Tender.Target(
        id = id,
        title = title,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        observations = observations
    ).asSuccess()
}

fun CreatePCRParams.Tender.Target.Observation.convert(path: String):
    Result<CreatePCR.Tender.Target.Observation, Failure> {

    val period = period?.convert(path = "$path/period")?.onFailure { return it }
    val measure = measure.asEnum(target = ObservationMeasure, path = "$path/measure")
        .onFailure { return it }
    val unit = unit.convert(path = "$path/unit").onFailure { return it }
    val dimensions = dimensions.convert(path = "$path/dimensions").onFailure { return it }

    return CreatePCR.Tender.Target.Observation(
        id = id,
        period = period,
        measure = measure,
        unit = unit,
        dimensions = dimensions,
        notes = notes,
        relatedRequirementId = relatedRequirementId,
    ).asSuccess()
}

fun CreatePCRParams.Tender.Target.Observation.Period.convert(path: String):
    Result<CreatePCR.Tender.Target.Observation.Period, Failure> {

    val startDate = endDate.asLocalDateTime(path = "$path/startDate")
        .onFailure { return it }

    val endDate = endDate.asLocalDateTime(path = "$path/endDate")
        .onFailure { return it }

    return CreatePCR.Tender.Target.Observation.Period(startDate = startDate, endDate = endDate).asSuccess()
}

fun CreatePCRParams.Tender.Target.Observation.Dimensions.convert(path: String):
    Result<CreatePCR.Tender.Target.Observation.Dimensions, Failure> =
    CreatePCR.Tender.Target.Observation.Dimensions(requirementClassIdPR = requirementClassIdPR).asSuccess()

/**
 * Criterion
 */
fun CreatePCRParams.Tender.Criterion.convert(path: String): Result<CreatePCR.Tender.Criterion, Failure> {
    val relatesTo = relatesTo?.asEnum(target = CriterionRelatesTo, path = "$path/relatesTo")
        ?.onFailure { return it }
    val requirementGroups = requirementGroups
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/requirementGroups")) }
        .mapIndexedOrEmpty { idx, requirementGroup ->
            requirementGroup.convert(path = "$path/requirementGroups[$idx]").onFailure { return it }
        }

    return CreatePCR.Tender.Criterion(
        id = id,
        title = title,
        description = description,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        requirementGroups = requirementGroups,
    ).asSuccess()
}

fun CreatePCRParams.Tender.Criterion.RequirementGroup.convert(path: String): Result<CreatePCR.Tender.Criterion.RequirementGroup, Failure> =
    CreatePCR.Tender.Criterion.RequirementGroup(
        id = id,
        description = description,
        requirements = requirements.toList(),
    ).asSuccess()

/**
 * Conversion
 */
fun CreatePCRParams.Tender.Conversion.convert(path: String): Result<CreatePCR.Tender.Conversion, Failure> {
    val relatesTo = relatesTo.asEnum(target = ConversionRelatesTo, path = "$path/relatesTo")
        .onFailure { return it }

    val coefficients = coefficients
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/coefficients")) }
        .mapIndexedOrEmpty { idx, coefficient ->
            coefficient.convert(path = "$path/coefficients[$idx]").onFailure { return it }
        }

    return CreatePCR.Tender.Conversion(
        id = id,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        rationale = rationale,
        description = description,
        coefficients = coefficients
    ).asSuccess()
}

fun CreatePCRParams.Tender.Conversion.Coefficient.convert(path: String): Result<CreatePCR.Tender.Conversion.Coefficient, Failure> =
    CreatePCR.Tender.Conversion.Coefficient(id = id, value = value, coefficient = coefficient).asSuccess()

/**
 * Document
 */
fun CreatePCRParams.Tender.Document.convert(path: String): Result<CreatePCR.Tender.Document, Failure> {
    val id = DocumentId.orNull(id)
        ?: return failure(
            JsonErrors.DataFormatMismatch(
                path = "$path/id",
                actualValue = id,
                expectedFormat = DocumentId.pattern,
                reason = null
            )
        )

    val documentType = documentType.asEnum(target = DocumentType, path = "$path/documentType")
        .onFailure { return it }

    return CreatePCR.Tender.Document(
        id = id,
        documentType = documentType,
        title = title,
        description = description,
        relatedLots = relatedLots
    ).asSuccess()
}
