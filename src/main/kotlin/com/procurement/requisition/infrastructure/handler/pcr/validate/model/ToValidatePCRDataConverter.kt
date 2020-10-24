package com.procurement.requisition.infrastructure.handler.pcr.validate.model

import com.procurement.requisition.application.service.validate.model.ValidatePCRDataCommand
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.document.DocumentType
import com.procurement.requisition.domain.model.tender.ProcurementMethodModality
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.conversion.ConversionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
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

fun ValidatePCRDataRequest.convert(): Result<ValidatePCRDataCommand, Failure> = tender.convert("#/tender")
    .onFailure { return it }
    .let { ValidatePCRDataCommand(it).asSuccess() }

/**
 * Tender
 */
fun ValidatePCRDataRequest.Tender.convert(path: String): Result<ValidatePCRDataCommand.Tender, Failure> {
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
        awardCriteria = awardCriteria,
        awardCriteriaDetails = awardCriteriaDetails,
        documents = documents
    ).asSuccess()
}

/**
 * Classification
 */
fun ValidatePCRDataRequest.Classification.convert(path: String): Result<ValidatePCRDataCommand.Classification, Failure> {
    val scheme = scheme.asEnum(target = ClassificationScheme, path = "$path/scheme")
        .onFailure { return it }
    return ValidatePCRDataCommand.Classification(id = id, scheme = scheme).asSuccess()
}

/**
 * Unit
 */
fun ValidatePCRDataRequest.Unit.convert(path: String): Result<ValidatePCRDataCommand.Unit, Failure> =
    ValidatePCRDataCommand.Unit(id = this.id).asSuccess()

/**
 * Lot
 */
fun ValidatePCRDataRequest.Tender.Lot.convert(path: String): Result<ValidatePCRDataCommand.Tender.Lot, Failure> {
    if (!LotId.validate(id))
        return failure(
            JsonErrors.DataFormatMismatch(
                path = "$path/id",
                actualValue = id,
                expectedFormat = LotId.pattern,
                reason = null
            )
        )
    val classification = classification.convert(path = "$path/classification").onFailure { return it }
    val variants = variants.convert(path = "$path/variants").onFailure { return it }

    return ValidatePCRDataCommand.Tender.Lot(
        id = id,
        internalId = internalId,
        title = title,
        description = description,
        classification = classification,
        variants = variants,
    ).asSuccess()
}

fun ValidatePCRDataRequest.Tender.Lot.Variant.convert(path: String): Result<ValidatePCRDataCommand.Tender.Lot.Variant, Failure> =
    ValidatePCRDataCommand.Tender.Lot.Variant(hasVariants = hasVariants, variantsDetails = variantsDetails).asSuccess()

/**
 * Item
 */
fun ValidatePCRDataRequest.Tender.Item.convert(path: String): Result<ValidatePCRDataCommand.Tender.Item, Failure> {
    if (!ItemId.validate(id))
        return failure(
            JsonErrors.DataFormatMismatch(
                path = "$path/id",
                actualValue = id,
                expectedFormat = ItemId.pattern,
                reason = null
            )
        )
    val classification = classification.convert("$path/classification").onFailure { return it }
    val unit = unit.convert("$path/unit").onFailure { return it }
    if (!LotId.validate(relatedLot))
        return failure(
            JsonErrors.DataFormatMismatch(
                path = "$path/relatedLot",
                actualValue = relatedLot,
                expectedFormat = LotId.pattern,
                reason = null
            )
        )

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
fun ValidatePCRDataRequest.Tender.Target.convert(path: String): Result<ValidatePCRDataCommand.Tender.Target, Failure> {
    val relatesTo = relatesTo.asEnum(target = TargetRelatesTo, path = "$path/relatesTo")
        .onFailure { return it }

    val observations = observations
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/observations")) }
        .mapIndexedOrEmpty { observationIdx, observation ->
            observation.convert("$path/observations[$observationIdx]").onFailure { return it }
        }

    return ValidatePCRDataCommand.Tender.Target(
        id = id,
        title = title,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        observations = observations
    ).asSuccess()
}

fun ValidatePCRDataRequest.Tender.Target.Observation.convert(path: String):
    Result<ValidatePCRDataCommand.Tender.Target.Observation, Failure> {

    val period = period?.convert(path = "$path/period")?.onFailure { return it }
    val measure = measure.asEnum(target = ObservationMeasure, path = "$path/measure")
        .onFailure { return it }
    val unit = unit.convert(path = "$path/unit").onFailure { return it }
    val dimensions = dimensions.convert(path = "$path/dimensions").onFailure { return it }

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

fun ValidatePCRDataRequest.Tender.Target.Observation.Period.convert(path: String):
    Result<ValidatePCRDataCommand.Tender.Target.Observation.Period, Failure> {

    val startDate = startDate.asLocalDateTime(path = "$path/startDate")
        .onFailure { return it }

    val endDate = endDate.asLocalDateTime(path = "$path/endDate")
        .onFailure { return it }

    return ValidatePCRDataCommand.Tender.Target.Observation.Period(startDate = startDate, endDate = endDate).asSuccess()
}

fun ValidatePCRDataRequest.Tender.Target.Observation.Dimensions.convert(path: String):
    Result<ValidatePCRDataCommand.Tender.Target.Observation.Dimensions, Failure> =
    ValidatePCRDataCommand.Tender.Target.Observation.Dimensions(requirementClassIdPR = requirementClassIdPR).asSuccess()

/**
 * Criterion
 */
fun ValidatePCRDataRequest.Tender.Criterion.convert(path: String): Result<ValidatePCRDataCommand.Tender.Criterion, Failure> {
    val relatesTo = relatesTo?.asEnum(target = CriterionRelatesTo, path = "$path/relatesTo")
        ?.onFailure { return it }

    val requirementGroups = requirementGroups
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/requirementGroups")) }
        .mapIndexedOrEmpty { idx, requirementGroup ->
            requirementGroup.convert(path = "$path/requirementGroups[$idx]").onFailure { return it }
        }

    return ValidatePCRDataCommand.Tender.Criterion(
        id = id,
        title = title,
        description = description,
        relatesTo = relatesTo,
        relatedItem = relatedItem,
        requirementGroups = requirementGroups,
    ).asSuccess()
}

fun ValidatePCRDataRequest.Tender.Criterion.RequirementGroup.convert(path: String): Result<ValidatePCRDataCommand.Tender.Criterion.RequirementGroup, Failure> =
    ValidatePCRDataCommand.Tender.Criterion.RequirementGroup(
        id = id,
        description = description,
        requirements = requirements.toList(),
    ).asSuccess()

/**
 * Conversion
 */
fun ValidatePCRDataRequest.Tender.Conversion.convert(path: String): Result<ValidatePCRDataCommand.Tender.Conversion, Failure> {
    val relatesTo = relatesTo.asEnum(target = ConversionRelatesTo, path = "$path/relatesTo")
        .onFailure { return it }

    val coefficients = coefficients
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/coefficients")) }
        .mapIndexedOrEmpty { idx, coefficient ->
            coefficient.convert(path = "$path/coefficients[$idx]").onFailure { return it }
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

fun ValidatePCRDataRequest.Tender.Conversion.Coefficient.convert(path: String): Result<ValidatePCRDataCommand.Tender.Conversion.Coefficient, Failure> =
    ValidatePCRDataCommand.Tender.Conversion.Coefficient(id = id, value = value, coefficient = coefficient).asSuccess()

/**
 * Document
 */
fun ValidatePCRDataRequest.Tender.Document.convert(path: String): Result<ValidatePCRDataCommand.Tender.Document, Failure> {
    val documentType = documentType.asEnum(target = DocumentType, path = "$path/documentType")
        .onFailure { return it }

    val relatedLots = relatedLots
        .failureIfEmpty { return failure(JsonErrors.EmptyArray(path = "$path/relatedLots")) }
        .mapIndexedOrEmpty { idx, relatedLot ->
            if (LotId.validate(relatedLot))
                relatedLot
            else
                return failure(
                    JsonErrors.DataFormatMismatch(
                        path = "$path/relatedLots[$idx]",
                        actualValue = relatedLot,
                        expectedFormat = LotId.pattern,
                        reason = null
                    )
                )
        }

    return ValidatePCRDataCommand.Tender.Document(
        id = id,
        documentType = documentType,
        title = title,
        description = description,
        relatedLots = relatedLots
    ).asSuccess()
}
