package com.procurement.requisition.application.service.create.model

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

fun PCR.convertToCreatedPCR() = CreatedPCR(
    ocid = ocid,
    token = token,
    tender = tender.convert(),
    relatedProcesses = relatedProcesses.map { it.convert() }
)

/**
 * Tender
 */
fun Tender.convert() = CreatedPCR.Tender(
    id = id,
    title = title,
    status = status,
    statusDetails = statusDetails,
    date = date,
    description = description,
    classification = classification.convert(),
    lots = lots.map { it.convert() },
    items = items.map { it.convert() },
    targets = targets.map { it.convert() },
    criteria = criteria.map { it.convert() },
    conversions = conversions.map { it.convert() },
    procurementMethodModalities = procurementMethodModalities.map { it },
    awardCriteria = awardCriteria,
    awardCriteriaDetails = awardCriteriaDetails,
    documents = documents.map { it.convert() },
    value = value.convert()
)

/**
 * Classification
 */
fun Classification.convert() =
    CreatedPCR.Classification(id = id, scheme = scheme, description = description)

/**
 * Unit
 */
fun com.procurement.requisition.domain.model.tender.unit.Unit.convert() = CreatedPCR.Unit(
    id = id,
    name = name
)

/**
 * Lot
 */
fun Lot.convert() = CreatedPCR.Tender.Lot(
    id = id,
    internalId = internalId,
    title = title,
    description = description,
    status = status,
    statusDetails = statusDetails,
    classification = classification.convert(),
    variants = variants.convert(),
)

fun Variant.convert() =
    CreatedPCR.Tender.Lot.Variant(hasVariants = hasVariants, variantsDetails = variantsDetails)

/**
 * Item
 */
fun Item.convert() = CreatedPCR.Tender.Item(
    id = id,
    internalId = internalId,
    description = description,
    quantity = quantity,
    classification = classification.convert(),
    unit = unit.convert(),
    relatedLot = relatedLot,
)

/**
 * Target
 */
fun Target.convert() = CreatedPCR.Tender.Target(
    id = id,
    title = title,
    relatesTo = relatesTo,
    relatedItem = relatedItem,
    observations = observations.map { it.convert() }
)

fun Observation.convert() = CreatedPCR.Tender.Target.Observation(
    id = id,
    period = period?.convert(),
    measure = measure,
    unit = unit.convert(),
    dimensions = dimensions.convert(),
    notes = notes,
    relatedRequirementId = relatedRequirementId,
)

fun Period.convert() = CreatedPCR.Tender.Target.Observation.Period(
    startDate = startDate,
    endDate = endDate
)

fun Dimensions.convert() =
    CreatedPCR.Tender.Target.Observation.Dimensions(requirementClassIdPR = requirementClassIdPR)

/**
 * Criterion
 */
fun Criterion.convert() = CreatedPCR.Tender.Criterion(
    id = id,
    title = title,
    source = source,
    description = description,
    relatesTo = relatesTo,
    relatedItem = relatedItem,
    requirementGroups = requirementGroups.map { it.convert() },
)

fun RequirementGroup.convert() = CreatedPCR.Tender.Criterion.RequirementGroup(
    id = id,
    description = description,
    requirements = requirements.toList(),
)

/**
 * Conversion
 */
fun Conversion.convert() = CreatedPCR.Tender.Conversion(
    id = id,
    relatesTo = relatesTo,
    relatedItem = relatedItem,
    rationale = rationale,
    description = description,
    coefficients = coefficients.map { it.convert() }
)

fun Coefficient.convert() =
    CreatedPCR.Tender.Conversion.Coefficient(id = id, value = value, coefficient = coefficient)

/**
 * RelatedProcess
 */
fun RelatedProcess.convert() = CreatedPCR.RelatedProcess(
    id = id,
    scheme = scheme,
    identifier = identifier,
    relationship = relationship.map { it },
    uri = uri
)

/**
 * Document
 */
fun Document.convert() = CreatedPCR.Tender.Document(
    id = id,
    documentType = documentType,
    title = title,
    description = description,
    relatedLots = relatedLots.map { it }
)

/**
 * Value
 */
fun Value.convert() = CreatedPCR.Tender.Value(currency = currency)
