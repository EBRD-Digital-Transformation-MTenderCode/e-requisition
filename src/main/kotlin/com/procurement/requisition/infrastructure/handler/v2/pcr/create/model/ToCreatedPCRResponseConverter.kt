package com.procurement.requisition.infrastructure.handler.v2.pcr.create.model

import com.procurement.requisition.application.service.create.pcr.model.CreatePCRResult
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.converter.asStringOrNull
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion.serialization

fun CreatePCRResult.convert() = CreatedPCRResponse(
    ocid = ocid.underlying,
    token = token.underlying,
    tender = tender.convert(),
    relatedProcesses = relatedProcesses.map { it.convert() }
)

/**
 * Tender
 */
fun CreatePCRResult.Tender.convert() = CreatedPCRResponse.Tender(
    id = id.underlying,
    title = title,
    status = status.asString(),
    statusDetails = statusDetails.asString(),
    date = date.asString(),
    description = description,
    classification = classification.convert(),
    lots = lots.map { it.convert() },
    items = items.map { it.convert() },
    targets = targets.map { it.convert() },
    criteria = criteria.map { it.convert() },
    conversions = conversions.map { it.convert() },
    procurementMethodModalities = procurementMethodModalities.map { it.asString() },
    awardCriteria = awardCriteria.asString(),
    awardCriteriaDetails = awardCriteriaDetails.asString(),
    documents = documents.map { it.convert() },
    value = value.convert()
)

/**
 * Classification
 */
fun CreatePCRResult.Classification.convert() = CreatedPCRResponse.Classification(
    id = id,
    scheme = scheme.asString(),
    description = description
)

/**
 * Unit
 */
fun CreatePCRResult.Unit.convert() = CreatedPCRResponse.Unit(id = id, name = name)

/**
 * Lot
 */
fun CreatePCRResult.Tender.Lot.convert() = CreatedPCRResponse.Tender.Lot(
    id = id.underlying,
    internalId = internalId,
    title = title,
    description = description,
    status = status.asString(),
    statusDetails = statusDetails.asStringOrNull(),
    classification = classification.convert(),
    variants = variants.map { it.convert() },
)

fun CreatePCRResult.Tender.Lot.Variant.convert() =
    CreatedPCRResponse.Tender.Lot.Variant(hasVariants = hasVariants, variantsDetails = variantsDetails)

/**
 * Item
 */
fun CreatePCRResult.Tender.Item.convert() = CreatedPCRResponse.Tender.Item(
    id = id.underlying,
    internalId = internalId,
    description = description,
    quantity = quantity,
    classification = classification.convert(),
    unit = unit.convert(),
    relatedLot = relatedLot.underlying,
)

/**
 * Target
 */
fun CreatePCRResult.Tender.Target.convert() = CreatedPCRResponse.Tender.Target(
    id = id.underlying,
    title = title,
    relatesTo = relatesTo.asString(),
    relatedItem = relatedItem,
    observations = observations.map { it.convert() }
)

fun CreatePCRResult.Tender.Target.Observation.convert() = CreatedPCRResponse.Tender.Target.Observation(
    id = id.underlying,
    period = period?.convert(),
    measure = measure,
    unit = unit.convert(),
    dimensions = dimensions?.convert(),
    notes = notes,
    relatedRequirementId = relatedRequirementId,
)

fun CreatePCRResult.Tender.Target.Observation.Period.convert() = CreatedPCRResponse.Tender.Target.Observation.Period(
    startDate = startDate?.asString(),
    endDate = endDate?.asString()
)

fun CreatePCRResult.Tender.Target.Observation.Dimensions.convert() =
    CreatedPCRResponse.Tender.Target.Observation.Dimensions(requirementClassIdPR = requirementClassIdPR)

/**
 * Criterion
 */
fun CreatePCRResult.Tender.Criterion.convert() = CreatedPCRResponse.Tender.Criterion(
    id = id.underlying,
    title = title,
    source = source.asString(),
    description = description,
    relatesTo = relatesTo?.asString(),
    relatedItem = relatedItem,
    requirementGroups = requirementGroups.map { it.convert() },
)

fun CreatePCRResult.Tender.Criterion.RequirementGroup.convert() = CreatedPCRResponse.Tender.Criterion.RequirementGroup(
    id = id.underlying,
    description = description,
    requirements = requirements.map { it.serialization() },
)

/**
 * Conversion
 */
fun CreatePCRResult.Tender.Conversion.convert() = CreatedPCRResponse.Tender.Conversion(
    id = id.underlying,
    relatesTo = relatesTo.asString(),
    relatedItem = relatedItem,
    rationale = rationale,
    description = description,
    coefficients = coefficients.map { it.convert() }
)

fun CreatePCRResult.Tender.Conversion.Coefficient.convert() =
    CreatedPCRResponse.Tender.Conversion.Coefficient(id = id.underlying, value = value, coefficient = coefficient)

/**
 * RelatedProcess
 */
fun CreatePCRResult.RelatedProcess.convert() = CreatedPCRResponse.RelatedProcess(
    id = id.underlying,
    scheme = scheme.asString(),
    identifier = identifier,
    relationship = relationship.map { it.asString() },
    uri = uri
)

/**
 * Document
 */
fun CreatePCRResult.Tender.Document.convert() = CreatedPCRResponse.Tender.Document(
    id = id.underlying,
    documentType = documentType.asString(),
    title = title,
    description = description,
    relatedLots = relatedLots.map { it.underlying }
)

/**
 * Value
 */
fun CreatePCRResult.Tender.Value.convert() = CreatedPCRResponse.Tender.Value(currency = currency)
