package com.procurement.requisition.infrastructure.convert.pcr.validate

import com.procurement.requisition.application.service.validate.ValidationPCRData
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.infrastructure.handler.pcr.validate.ValidatePCRDataParams

fun ValidatePCRDataParams.convert(): ValidationPCRData = ValidationPCRData(
    tender = ValidationPCRData.Tender(
        title = tender.title,
        description = tender.description,
        classification = tender.classification
            .let { classification ->
                Classification(
                    id = classification.id,
                    scheme = classification.scheme
                )
            },
        lots = tender.lots
            .map { lot ->
                ValidationPCRData.Tender.Lot(
                    id = lot.id,
                    internalId = lot.internalId,
                    title = lot.title,
                    description = lot.description,
                    classification = lot.classification
                        .let { classification ->
                            Classification(
                                id = classification.id,
                                scheme = classification.scheme
                            )
                        },
                    variants = lot.variants
                        .let { variant ->
                            ValidationPCRData.Tender.Lot.Variant(
                                hasVariants = variant.hasVariants,
                                variantsDetails = variant.variantsDetails
                            )
                        },
                )
            },
        items = tender.items
            ?.map { item ->
                ValidationPCRData.Tender.Item(
                    id = item.id,
                    internalId = item.internalId,
                    description = item.description,
                    quantity = item.quantity,
                    classification = item.classification
                        .let { classification ->
                            Classification(
                                id = classification.id,
                                scheme = classification.scheme
                            )
                        },
                    unit = item.unit
                        .let { unit ->
                            ValidationPCRData.Unit(
                                id = unit.id
                            )
                        },
                    relatedLot = item.relatedLot,
                )
            }
            .orEmpty(),
        targets = tender.targets
            ?.map { target ->
                ValidationPCRData.Tender.Target(
                    id = target.id,
                    title = target.title,
                    relatesTo = target.relatesTo,
                    relatedItem = target.relatedItem,
                    observations = target.observations
                        .map { observation ->
                            ValidationPCRData.Tender.Target.Observation(
                                id = observation.id,
                                period = observation.period
                                    ?.let { period ->
                                        ValidationPCRData.Tender.Target.Observation.Period(
                                            startDate = period.startDate,
                                            endDate = period.endDate
                                        )
                                    },
                                measure = observation.measure,
                                unit = observation.unit
                                    .let { unit ->
                                        ValidationPCRData.Unit(
                                            id = unit.id
                                        )
                                    },
                                dimensions = observation.dimensions
                                    .let { dimension ->
                                        ValidationPCRData.Tender.Target.Observation.Dimensions(
                                            requirementClassIdPR = dimension.requirementClassIdPR
                                        )
                                    },
                                notes = observation.notes,
                                relatedRequirementId = observation.relatedRequirementId,
                            )
                        },
                )
            }
            .orEmpty(),
        criteria = tender.criteria
            ?.map { criterion ->
                ValidationPCRData.Tender.Criterion(
                    id = criterion.id,
                    title = criterion.title,
                    source = criterion.source,
                    description = criterion.description,
                    relatesTo = criterion.relatesTo,
                    relatedItem = criterion.relatedItem,
                    requirementGroups = criterion.requirementGroups
                        .map { requirementGroup ->
                            ValidationPCRData.Tender.Criterion.RequirementGroup(
                                id = requirementGroup.id,
                                description = requirementGroup.description,
                                requirements = requirementGroup.requirements.toList(),
                            )
                        },
                )
            }
            .orEmpty(),
        conversions = tender.conversions
            ?.map { conversion ->
                ValidationPCRData.Tender.Conversion(
                    id = conversion.id,
                    relatesTo = conversion.relatesTo,
                    relatedItem = conversion.relatedItem,
                    rationale = conversion.rationale,
                    description = conversion.description,
                    coefficients = conversion.coefficients
                        .map { coefficient ->
                            ValidationPCRData.Tender.Conversion.Coefficient(
                                id = coefficient.id,
                                value = coefficient.value,
                                coefficient = coefficient.coefficient,
                            )
                        }
                )
            }
            .orEmpty(),
        procurementMethodModalities = tender.procurementMethodModalities?.toList().orEmpty(),
        awardCriteria = tender.awardCriteria,
        awardCriteriaDetails = tender.awardCriteriaDetails,
        documents = tender.documents
            ?.map { document ->
                ValidationPCRData.Tender.Document(
                    id = document.id,
                    documentType = document.documentType,
                    title = document.title,
                    description = document.description,
                    relatedLots = document.relatedLots?.toList().orEmpty()
                )
            }
            .orEmpty(),
    )
)