package com.procurement.requisition.application.service

import com.procurement.requisition.application.service.converter.convertToCreatedPCR
import com.procurement.requisition.application.service.model.StateFE
import com.procurement.requisition.application.service.model.command.CreatePCRCommand
import com.procurement.requisition.application.service.model.result.CreatePCRResult
import com.procurement.requisition.domain.extension.nowDefaultUTC
import com.procurement.requisition.domain.failure.incident.InvalidArgumentValueIncident
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.domain.model.Period
import com.procurement.requisition.domain.model.Stage
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.document.Document
import com.procurement.requisition.domain.model.document.DocumentReference
import com.procurement.requisition.domain.model.document.Documents
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcess
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessId
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessScheme
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcesses
import com.procurement.requisition.domain.model.relatedprocesses.Relationship
import com.procurement.requisition.domain.model.relatedprocesses.Relationships
import com.procurement.requisition.domain.model.requirement.EligibleEvidence
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementGroup
import com.procurement.requisition.domain.model.requirement.RequirementGroupId
import com.procurement.requisition.domain.model.requirement.RequirementGroups
import com.procurement.requisition.domain.model.requirement.RequirementId
import com.procurement.requisition.domain.model.requirement.RequirementStatus
import com.procurement.requisition.domain.model.requirement.Requirements
import com.procurement.requisition.domain.model.requirement.generateRequirementId
import com.procurement.requisition.domain.model.tender.ProcurementMethodModalities
import com.procurement.requisition.domain.model.tender.TargetRelatesTo
import com.procurement.requisition.domain.model.tender.Tender
import com.procurement.requisition.domain.model.tender.TenderId
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.domain.model.tender.Value
import com.procurement.requisition.domain.model.tender.conversion.Conversion
import com.procurement.requisition.domain.model.tender.conversion.ConversionId
import com.procurement.requisition.domain.model.tender.conversion.Conversions
import com.procurement.requisition.domain.model.tender.conversion.coefficient.Coefficient
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientId
import com.procurement.requisition.domain.model.tender.conversion.coefficient.Coefficients
import com.procurement.requisition.domain.model.tender.criterion.Criteria
import com.procurement.requisition.domain.model.tender.criterion.Criterion
import com.procurement.requisition.domain.model.tender.criterion.CriterionClassification
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatedItem
import com.procurement.requisition.domain.model.tender.criterion.CriterionRelatesTo
import com.procurement.requisition.domain.model.tender.criterion.CriterionSource
import com.procurement.requisition.domain.model.tender.item.Item
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.item.Items
import com.procurement.requisition.domain.model.tender.lot.Lot
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.domain.model.tender.lot.RelatedLots
import com.procurement.requisition.domain.model.tender.lot.Variant
import com.procurement.requisition.domain.model.tender.lot.Variants
import com.procurement.requisition.domain.model.tender.target.Target
import com.procurement.requisition.domain.model.tender.target.TargetId
import com.procurement.requisition.domain.model.tender.target.TargetRelatedItem
import com.procurement.requisition.domain.model.tender.target.Targets
import com.procurement.requisition.domain.model.tender.target.observation.Dimensions
import com.procurement.requisition.domain.model.tender.target.observation.Observation
import com.procurement.requisition.domain.model.tender.target.observation.ObservationId
import com.procurement.requisition.domain.model.tender.target.observation.Observations
import com.procurement.requisition.domain.model.tender.unit.Unit
import com.procurement.requisition.infrastructure.configuration.properties.UriProperties
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asFailure
import com.procurement.requisition.lib.functional.asSuccess
import org.springframework.stereotype.Service

@Service
class CreatePCRService(
    val uriProperties: UriProperties,
    private val pcrManagement: PCRManagementService,
) {

    fun create(command: CreatePCRCommand): Result<CreatePCRResult, Failure> {

        val lotsMapping: Map<String, LotId> = command.tender.lots
            .asSequence()
            .map { lot -> lot.id to LotId.generate() }
            .toMap()

        val itemsMapping: Map<String, ItemId> = command.tender.items
            .asSequence()
            .map { item -> item.id to ItemId.generate() }
            .toMap()

        val requirementsMapping: Map<String, RequirementId> = command.tender.criteria
            .asSequence()
            .flatMap { criterion -> criterion.requirementGroups.asSequence() }
            .flatMap { requirementGroup -> requirementGroup.requirements }
            .map { requirement -> requirement.id to generateRequirementId() }
            .toMap()

        val tender = Tender(
            id = TenderId.generate(),
            status = tenderStatus(command.stateFE),
            statusDetails = tenderStatusDetails(command.stateFE),
            date = command.date,
            title = command.tender.title,
            description = command.tender.description,
            classification = command.tender.classification,
            lots = lots(command, lotsMapping),
            items = items(command, lotsMapping, itemsMapping),
            targets = targets(command, lotsMapping, itemsMapping, requirementsMapping),
            criteria = criteria(command, lotsMapping, itemsMapping, requirementsMapping).onFailure { return it },
            conversions = conversions(command, requirementsMapping),
            procurementMethodModalities =
            ProcurementMethodModalities(command.tender.procurementMethodModalities.toList()),
            awardCriteria = command.tender.awardCriteria,
            awardCriteriaDetails = command.tender.awardCriteriaDetails,
            documents = documents(command, lotsMapping),
            value = value(command)
        )
        val ocid: Ocid = Ocid.SingleStage.generate(cpid = command.cpid, stage = Stage.PC, timestamp = nowDefaultUTC())

        val relatedProcesses = relatedProcesses(command, uriProperties, ocid)
        val electronicAuctions = command.tender.electronicAuctions?.relatedIds(lotsMapping)

        val pcr = PCR(
            cpid = command.cpid,
            ocid = ocid,
            token = Token.generate(),
            owner = command.owner,
            tender = tender,
            relatedProcesses = relatedProcesses
        )

        pcrManagement.create(cpid = command.cpid, ocid = ocid, pcr = pcr)
            .onFailure { return it }

        return pcr.convertToCreatedPCR(electronicAuctions).asSuccess()
    }
}

fun CreatePCRCommand.Tender.ElectronicAuctions.relatedIds(lotsMapping: Map<String, LotId>): CreatePCRResult.Tender.ElectronicAuctions =
    this.details
        .map { detail ->
            val relatedLot = lotsMapping.getValue(detail.relatedLot)
            CreatePCRResult.Tender.ElectronicAuctions.Detail(id = detail.id, relatedLot = relatedLot)
        }
        .let { details -> CreatePCRResult.Tender.ElectronicAuctions(details) }

fun tenderStatus(stageFE: StateFE): TenderStatus = when (stageFE) {
    StateFE.EVALUATION -> TenderStatus.ACTIVE
    StateFE.SUBMISSION -> TenderStatus.ACTIVE
}

fun tenderStatusDetails(stageFE: StateFE): TenderStatusDetails = when (stageFE) {
    StateFE.EVALUATION -> TenderStatusDetails.TENDERING
    StateFE.SUBMISSION -> TenderStatusDetails.CLARIFICATION
}

fun targetRelatedItem(
    relatesTo: TargetRelatesTo,
    relatedItem: TargetRelatedItem,
    lotsMapping: Map<String, LotId>,
    itemsMapping: Map<String, ItemId>
): String = when (relatesTo) {
    TargetRelatesTo.LOT -> lotsMapping.getValue(relatedItem).underlying
    TargetRelatesTo.ITEM -> itemsMapping.getValue(relatedItem).underlying
}

fun criteriaRelatedItem(
    relatesTo: CriterionRelatesTo,
    relatedItem: CriterionRelatedItem,
    lotsMapping: Map<String, LotId>,
    itemsMapping: Map<String, ItemId>
): Result<String, InvalidArgumentValueIncident> = when (relatesTo) {
    CriterionRelatesTo.AWARD -> InvalidArgumentValueIncident(
        name = "relatesTo",
        value = relatesTo,
        expectedValue = listOf(CriterionRelatesTo.ITEM, CriterionRelatesTo.LOT)
    ).asFailure()

    CriterionRelatesTo.ITEM -> itemsMapping.getValue(relatedItem).underlying.asSuccess()
    CriterionRelatesTo.LOT -> lotsMapping.getValue(relatedItem).underlying.asSuccess()

    CriterionRelatesTo.TENDER -> InvalidArgumentValueIncident(
        name = "relatesTo",
        value = relatesTo,
        expectedValue = listOf(CriterionRelatesTo.ITEM, CriterionRelatesTo.LOT)
    ).asFailure()

    CriterionRelatesTo.TENDERER -> InvalidArgumentValueIncident(
        name = "relatesTo",
        value = relatesTo,
        expectedValue = listOf(CriterionRelatesTo.ITEM, CriterionRelatesTo.LOT)
    ).asFailure()
}

fun lots(createPCR: CreatePCRCommand, lotsMapping: Map<String, LotId>) = createPCR.tender.lots
    .map { lot ->
        Lot(
            id = lotsMapping.getValue(lot.id),
            internalId = lot.internalId,
            title = lot.title,
            description = lot.description,
            status = LotStatus.ACTIVE,
            statusDetails = LotStatusDetails.NONE,
            classification = lot.classification,
            variants = lot.variants
                .map { variant ->
                    Variant(
                        hasVariants = variant.hasVariants,
                        variantsDetails = variant.variantsDetails
                    )
                }
                .let { Variants(it) }
        )
    }
    .let { Lots(it) }

fun items(createPCR: CreatePCRCommand, lotsMapping: Map<String, LotId>, itemsMapping: Map<String, ItemId>) =
    createPCR.tender.items
        .map { item ->
            Item(
                id = itemsMapping.getValue(item.id),
                internalId = item.internalId,
                description = item.description,
                quantity = item.quantity,
                classification = item.classification,
                unit = item.unit
                    .let { unit ->
                        Unit(id = unit.id, name = unit.name)
                    },
                relatedLot = lotsMapping.getValue(item.relatedLot),
            )
        }
        .let { Items(it) }

fun criteria(
    createPCR: CreatePCRCommand,
    lotsMapping: Map<String, LotId>,
    itemsMapping: Map<String, ItemId>,
    requirementsMapping: Map<String, RequirementId>
): Result<Criteria, InvalidArgumentValueIncident> = createPCR.tender.criteria
    .map { criterion ->
        Criterion(
            id = CriterionId.generate(),
            title = criterion.title,
            source = CriterionSource.TENDERER,
            description = criterion.description,
            classification = criterion.classification.let { classification ->
              CriterionClassification(
                  id = classification.id,
                  scheme = classification.scheme.key
              )
            },
            requirementGroups = criterion.requirementGroups
                .map { requirementGroup ->
                    RequirementGroup(
                        id = RequirementGroupId.generate(),
                        description = requirementGroup.description,
                        requirements = requirementGroup.requirements
                            .map { requirement ->
                                Requirement(
                                    id = requirementsMapping.getValue(requirement.id),
                                    title = requirement.title,
                                    description = requirement.description,
                                    period = requirement.period
                                        ?.let { period ->
                                            Requirement.Period(
                                                startDate = period.startDate,
                                                endDate = period.endDate
                                            )
                                        },
                                    dataType = requirement.dataType,
                                    expectedValue = requirement.expectedValue,
                                    minValue = requirement.minValue,
                                    maxValue = requirement.maxValue,
                                    eligibleEvidences = requirement.eligibleEvidences.map { eligibleEvidence ->
                                        EligibleEvidence(
                                            id = eligibleEvidence.id,
                                            title = eligibleEvidence.title,
                                            type = eligibleEvidence.type,
                                            description = eligibleEvidence.description,
                                            relatedDocument = eligibleEvidence.relatedDocument
                                                ?.let { relatedDocument ->
                                                    DocumentReference(
                                                        id = relatedDocument.id
                                                    )
                                                }
                                        )
                                    },
                                    status = RequirementStatus.ACTIVE, // FR.COM-17.2.46
                                    datePublished = createPCR.date // FR.COM-17.2.47
                                )
                            }
                            .let { Requirements(it) },
                    )
                }
                .let { RequirementGroups(it) },
            relatesTo = criterion.relatesTo,
            relatedItem = criterion.relatesTo
                .let { relatesTo ->
                    criteriaRelatedItem(
                        relatesTo = relatesTo,
                        relatedItem = criterion.relatedItem!!,
                        lotsMapping = lotsMapping,
                        itemsMapping = itemsMapping
                    )
                        .onFailure { return it }
                },
        )
    }
    .let { Criteria(it).asSuccess() }

fun conversions(createPCR: CreatePCRCommand, requirementsMapping: Map<String, RequirementId>) =
    createPCR.tender.conversions
        .map { conversion ->
            Conversion(
                id = ConversionId.generate(),
                relatesTo = conversion.relatesTo,
                relatedItem = requirementsMapping.getValue(conversion.relatedItem),
                rationale = conversion.rationale,
                description = conversion.description,
                coefficients = conversion.coefficients
                    .map { coefficient ->
                        Coefficient(
                            id = CoefficientId.generate(),
                            value = coefficient.value,
                            coefficient = coefficient.coefficient
                        )
                    }
                    .let { Coefficients(it) },
            )
        }
        .let { Conversions(it) }

fun targets(
    createPCR: CreatePCRCommand,
    lotsMapping: Map<String, LotId>,
    itemsMapping: Map<String, ItemId>,
    requirementsMapping: Map<String, RequirementId>
) = createPCR.tender.targets
    .map { target ->
        Target(
            id = TargetId.generate(),
            title = target.title,
            relatesTo = target.relatesTo,
            relatedItem = targetRelatedItem(
                relatesTo = target.relatesTo,
                relatedItem = target.relatedItem,
                lotsMapping = lotsMapping,
                itemsMapping = itemsMapping
            ),
            observations = target.observations
                .map { observation ->
                    Observation(
                        id = ObservationId.generate(),
                        period = observation.period
                            ?.let { period ->
                                Period(
                                    startDate = period.startDate,
                                    endDate = period.endDate
                                )
                            },
                        measure = observation.measure,
                        unit = observation.unit
                            .let { unit ->
                                Unit(id = unit.id, name = unit.name)
                            },
                        dimensions = observation.dimensions
                            ?.let { dimension ->
                                Dimensions(requirementClassIdPR = dimension.requirementClassIdPR)
                            },
                        notes = observation.notes,
                        relatedRequirementId = observation.relatedRequirementId
                            ?.let { requirementsMapping.getValue(it) },
                    )
                }
                .let { Observations(it) },
        )
    }
    .let { Targets(it) }

fun documents(createPCR: CreatePCRCommand, lotsMapping: Map<String, LotId>) = createPCR.tender.documents
    .map { document ->
        Document(
            id = document.id,
            documentType = document.documentType,
            title = document.title,
            description = document.description,
            relatedLots = document.relatedLots
                .map { lotId -> lotsMapping.getValue(lotId) }
                .let { RelatedLots(it) },
        )
    }
    .let { Documents(it) }

fun value(createPCR: CreatePCRCommand) = createPCR.tender.value
    .let { value ->
        Value(
            amount = null,
            currency = value.currency
        )
    }

fun relatedProcesses(createPCR: CreatePCRCommand, uriProperties: UriProperties, ocid: Ocid): RelatedProcesses {
    val relatedProcessFA = RelatedProcess(
        id = RelatedProcessId.generate(),
        scheme = RelatedProcessScheme.OCID,
        identifier = createPCR.cpid.underlying,
        relationship = Relationships(Relationship.PARENT),
        uri = uri(uriProperties.tender, createPCR.cpid),
    )

    val relatedProcessFE = RelatedProcess(
        id = RelatedProcessId.generate(),
        scheme = RelatedProcessScheme.OCID,
        identifier = ocid.underlying,
        relationship = Relationships(Relationship.X_FRAMEWORK),
        uri = uriByOcid(uriProperties.tender, createPCR.cpid, ocid),
    )

    return RelatedProcesses(listOf(relatedProcessFA, relatedProcessFE))
}

fun uri(prefix: String, cpid: Cpid) = "$prefix/${cpid.underlying}/${cpid.underlying}"

fun uriByOcid(prefix: String, cpid: Cpid, ocid: Ocid) = "$prefix/${cpid.underlying}/${ocid.underlying}"
