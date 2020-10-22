package com.procurement.requisition.infrastructure.handler.pcr.create.model

import com.procurement.requisition.application.service.create.model.CreatedPCR
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.converter.asStringOrNull

fun CreatedPCR.convert() = CreatedPCRResponse(
    ocid = ocid.underlying,
    token = token.underlying,
    tender = tender.convert(),
    relatedProcesses = relatedProcesses.map { it.convert() }
)

/**
 * Tender
 */
fun CreatedPCR.Tender.convert() = CreatedPCRResponse.Tender(
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
fun CreatedPCR.Classification.convert() = CreatedPCRResponse.Classification(
    id = id,
    scheme = scheme.asString(),
    description = description
)

/**
 * Unit
 */
fun CreatedPCR.Unit.convert() = CreatedPCRResponse.Unit(id = id, name = name)

/**
 * Lot
 */
fun CreatedPCR.Tender.Lot.convert() = CreatedPCRResponse.Tender.Lot(
    id = id.underlying,
    internalId = internalId,
    title = title,
    description = description,
    status = status.asString(),
    statusDetails = statusDetails.asStringOrNull(),
    classification = classification.convert(),
    variants = variants.convert(),
)

fun CreatedPCR.Tender.Lot.Variant.convert() =
    CreatedPCRResponse.Tender.Lot.Variant(hasVariants = hasVariants, variantsDetails = variantsDetails)

/**
 * Item
 */
fun CreatedPCR.Tender.Item.convert() = CreatedPCRResponse.Tender.Item(
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
fun CreatedPCR.Tender.Target.convert() = CreatedPCRResponse.Tender.Target(
    id = id.underlying,
    title = title,
    relatesTo = relatesTo.asString(),
    relatedItem = relatedItem,
    observations = observations.map { it.convert() }
)

fun CreatedPCR.Tender.Target.Observation.convert() = CreatedPCRResponse.Tender.Target.Observation(
    id = id.underlying,
    period = period?.convert(),
    measure = measure.asString(),
    unit = unit.convert(),
    dimensions = dimensions.convert(),
    notes = notes,
    relatedRequirementId = relatedRequirementId,
)

fun CreatedPCR.Tender.Target.Observation.Period.convert() = CreatedPCRResponse.Tender.Target.Observation.Period(
    startDate = startDate.asString(),
    endDate = endDate.asString()
)

fun CreatedPCR.Tender.Target.Observation.Dimensions.convert() =
    CreatedPCRResponse.Tender.Target.Observation.Dimensions(requirementClassIdPR = requirementClassIdPR)

/**
 * Criterion
 */
fun CreatedPCR.Tender.Criterion.convert() = CreatedPCRResponse.Tender.Criterion(
    id = id.underlying,
    title = title,
    source = source.asString(),
    description = description,
    relatesTo = relatesTo?.asString(),
    relatedItem = relatedItem,
    requirementGroups = requirementGroups.map { it.convert() },
)

fun CreatedPCR.Tender.Criterion.RequirementGroup.convert() = CreatedPCRResponse.Tender.Criterion.RequirementGroup(
    id = id.underlying,
    description = description,
    requirements = requirements.toList(),
)

/**
 * Conversion
 */
fun CreatedPCR.Tender.Conversion.convert() = CreatedPCRResponse.Tender.Conversion(
    id = id.underlying,
    relatesTo = relatesTo.asString(),
    relatedItem = relatedItem,
    rationale = rationale,
    description = description,
    coefficients = coefficients.map { it.convert() }
)

fun CreatedPCR.Tender.Conversion.Coefficient.convert() =
    CreatedPCRResponse.Tender.Conversion.Coefficient(id = id.underlying, value = value, coefficient = coefficient)

/**
 * RelatedProcess
 */
fun CreatedPCR.RelatedProcess.convert() = CreatedPCRResponse.RelatedProcess(
    id = id.underlying,
    scheme = scheme.asString(),
    identifier = identifier,
    relationship = relationship.map { it.asString() },
    uri = uri
)

/**
 * Document
 */
fun CreatedPCR.Tender.Document.convert() = CreatedPCRResponse.Tender.Document(
    id = id.underlying,
    documentType = documentType.asString(),
    title = title,
    description = description,
    relatedLots = relatedLots.map { it.underlying }
)

/**
 * Value
 */
fun CreatedPCR.Tender.Value.convert() = CreatedPCRResponse.Tender.Value(currency = currency)
