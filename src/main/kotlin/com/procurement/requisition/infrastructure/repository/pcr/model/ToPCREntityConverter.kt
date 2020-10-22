package com.procurement.requisition.infrastructure.repository.pcr.model

import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.domain.model.Period
import com.procurement.requisition.domain.model.document.Document
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcess
import com.procurement.requisition.domain.model.requirement.RequirementGroup
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.domain.model.tender.Tender
import com.procurement.requisition.domain.model.tender.Value
import com.procurement.requisition.domain.model.tender.conversion.Conversion
import com.procurement.requisition.domain.model.tender.conversion.coefficient.Coefficient
import com.procurement.requisition.domain.model.tender.criterion.Criterion
import com.procurement.requisition.domain.model.tender.item.Item
import com.procurement.requisition.domain.model.tender.lot.Lot
import com.procurement.requisition.domain.model.tender.lot.Variant
import com.procurement.requisition.domain.model.tender.target.Target
import com.procurement.requisition.domain.model.tender.target.observation.Dimensions
import com.procurement.requisition.domain.model.tender.target.observation.Observation
import com.procurement.requisition.domain.model.tender.unit.Unit
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.infrastructure.handler.converter.asStringOrNull

fun PCR.convertToPCREntity() = PCREntity(
    ocid = ocid.underlying,
    token = token.underlying,
    tender = tender.convert(),
    relatedProcesses = relatedProcesses.map { it.convert() }
)

/**
 * Tender
 */
fun Tender.convert() = PCREntity.Tender(
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
fun Classification.convert() = PCREntity.Classification(
    id = id,
    scheme = scheme.asString(),
    description = description
)

/**
 * Unit
 */
fun Unit.convert() = PCREntity.Unit(id = id, name = name)

/**
 * Lot
 */
fun Lot.convert() = PCREntity.Tender.Lot(
    id = id.underlying,
    internalId = internalId,
    title = title,
    description = description,
    status = status.asString(),
    statusDetails = statusDetails.asStringOrNull(),
    classification = classification.convert(),
    variants = variants.convert(),
)

fun Variant.convert() =
    PCREntity.Tender.Lot.Variant(hasVariants = hasVariants, variantsDetails = variantsDetails)

/**
 * Item
 */
fun Item.convert() = PCREntity.Tender.Item(
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
fun Target.convert() = PCREntity.Tender.Target(
    id = id.underlying,
    title = title,
    relatesTo = relatesTo.asString(),
    relatedItem = relatedItem,
    observations = observations.map { it.convert() }
)

fun Observation.convert() = PCREntity.Tender.Target.Observation(
    id = id.underlying,
    period = period?.convert(),
    measure = measure.asString(),
    unit = unit.convert(),
    dimensions = dimensions.convert(),
    notes = notes,
    relatedRequirementId = relatedRequirementId,
)

fun Period.convert() = PCREntity.Tender.Target.Observation.Period(
    startDate = startDate.asString(),
    endDate = endDate.asString()
)

fun Dimensions.convert() =
    PCREntity.Tender.Target.Observation.Dimensions(requirementClassIdPR = requirementClassIdPR)

/**
 * Criterion
 */
fun Criterion.convert() = PCREntity.Tender.Criterion(
    id = id.underlying,
    title = title,
    source = source.asString(),
    description = description,
    relatesTo = relatesTo?.asString(),
    relatedItem = relatedItem,
    requirementGroups = requirementGroups.map { it.convert() },
)

fun RequirementGroup.convert() = PCREntity.Tender.Criterion.RequirementGroup(
    id = id.underlying,
    description = description,
    requirements = requirements.toList(),
)

/**
 * Conversion
 */
fun Conversion.convert() = PCREntity.Tender.Conversion(
    id = id.underlying,
    relatesTo = relatesTo.asString(),
    relatedItem = relatedItem,
    rationale = rationale,
    description = description,
    coefficients = coefficients.map { it.convert() }
)

fun Coefficient.convert() =
    PCREntity.Tender.Conversion.Coefficient(id = id.underlying, value = value, coefficient = coefficient)

/**
 * RelatedProcess
 */
fun RelatedProcess.convert() = PCREntity.RelatedProcess(
    id = id.underlying,
    scheme = scheme.asString(),
    identifier = identifier,
    relationship = relationship.map { it.asString() },
    uri = uri
)

/**
 * Document
 */
fun Document.convert() = PCREntity.Tender.Document(
    id = id.underlying,
    documentType = documentType.asString(),
    title = title,
    description = description,
    relatedLots = relatedLots.map { it.underlying }
)

/**
 * Value
 */
fun Value.convert() = PCREntity.Tender.Value(currency = currency)
