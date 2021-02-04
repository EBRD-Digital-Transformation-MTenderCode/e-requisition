package com.procurement.requisition.application.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.requisition.application.service.model.command.GetItemsByLotIdsCommand
import com.procurement.requisition.application.service.model.result.GetItemsByLotIdsResult
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.award.AwardCriteria
import com.procurement.requisition.domain.model.award.AwardCriteriaDetails
import com.procurement.requisition.domain.model.classification.ClassificationId
import com.procurement.requisition.domain.model.classification.ClassificationScheme
import com.procurement.requisition.domain.model.document.Documents
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcess
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessId
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessScheme
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcesses
import com.procurement.requisition.domain.model.relatedprocesses.Relationships
import com.procurement.requisition.domain.model.tender.Classification
import com.procurement.requisition.domain.model.tender.ProcurementMethodModalities
import com.procurement.requisition.domain.model.tender.Tender
import com.procurement.requisition.domain.model.tender.TenderId
import com.procurement.requisition.domain.model.tender.TenderStatus
import com.procurement.requisition.domain.model.tender.TenderStatusDetails
import com.procurement.requisition.domain.model.tender.Value
import com.procurement.requisition.domain.model.tender.conversion.Conversions
import com.procurement.requisition.domain.model.tender.criterion.Criteria
import com.procurement.requisition.domain.model.tender.item.Item
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.item.Items
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.domain.model.tender.target.Targets
import com.procurement.requisition.domain.model.tender.unit.Unit
import com.procurement.requisition.domain.model.tender.unit.UnitId
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

internal class GetItemsByLotIdsServiceTest {

    companion object {
        val CPID = Cpid.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892")!!
        val OCID = Ocid.SingleStage.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892-EV-1580458791896")!!
        val LOT_ID = LotId.generate()
        val UNKNOWN_LOT_ID = LotId.generate()
        val ITEM_ID = ItemId.generate()
    }

    private lateinit var pcrManagement: PCRManagementService
    private lateinit var getItemsByLotIdsService: GetItemsByLotIdsService

    @BeforeEach
    fun init() {
        pcrManagement = mock()
        getItemsByLotIdsService = GetItemsByLotIdsService(pcrManagement)
    }

    @Test
    fun get_success() {
        val command = getCommand()
        val pcr = generatePcr()
        whenever(pcrManagement.find(cpid = command.cpid, ocid = command.ocid))
            .thenReturn(pcr.asSuccess())
        val actual = getItemsByLotIdsService.get(command).orNull!!
        val expected = GetItemsByLotIdsResult(
            listOf(
                GetItemsByLotIdsResult.Item(
                    id = ITEM_ID,
                    description = "string",
                    internalId = null,
                    classification = GetItemsByLotIdsResult.Item.Classification(
                        id = ClassificationId(),
                        scheme = ClassificationScheme.CPC,
                        description = "string"
                    ),
                    quantity = BigDecimal.ONE,
                    unit = GetItemsByLotIdsResult.Item.Unit(
                        id = UnitId(),
                        name = "string"
                    ),
                    relatedLot = LOT_ID
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun get_noPcrFound_fail() {
        val command = getCommand()
        whenever(pcrManagement.find(cpid = command.cpid, ocid = command.ocid))
            .thenReturn(null.asSuccess())
        val actual = getItemsByLotIdsService.get(command) as Result.Failure
        val expectedErrorCode = "400.22.17.01"
        val expectedDescription = "PCR by cpid '$CPID' and ocid '$OCID' is not found."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedDescription, actual.reason.description)
    }

    @Test
    fun get_noItemsFoundForLot_fail() {
        val command = getCommandWithUnknownLot()
        val pcr = generatePcr()
        whenever(pcrManagement.find(cpid = command.cpid, ocid = command.ocid))
            .thenReturn(pcr.asSuccess())
        val actual = getItemsByLotIdsService.get(command) as Result.Failure
        val expectedErrorCode = "400.22.17.02"
        val expectedDescription = "No items found for lot(s) '$UNKNOWN_LOT_ID'."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedDescription, actual.reason.description)
    }

    private fun getCommand() = GetItemsByLotIdsCommand(
        cpid = CPID,
        ocid = OCID,
        lots = listOf(GetItemsByLotIdsCommand.Lot(id = LOT_ID))
    )

    private fun getCommandWithUnknownLot() = GetItemsByLotIdsCommand(
        cpid = CPID,
        ocid = OCID,
        lots = listOf(GetItemsByLotIdsCommand.Lot(id = UNKNOWN_LOT_ID))
    )

    private fun generatePcr() = PCR(
        cpid = CPID,
        ocid = OCID,
        token = Token.generate(),
        owner = "owner",
        tender = Tender(
            id = TenderId.orNull(UUID.randomUUID().toString())!!,
            status = TenderStatus.ACTIVE,
            statusDetails = TenderStatusDetails.AGGREGATED,
            date = LocalDateTime.now(),
            title = "string",
            description = "string",
            classification = Classification(
                id = ClassificationId(),
                scheme = ClassificationScheme.CPC,
                description = "string"
            ),
            lots = Lots(emptyList()),
            items = Items(
                listOf(
                    Item(
                        id = ITEM_ID,
                        description = "string",
                        internalId = null,
                        classification = Classification(
                            id = ClassificationId(),
                            scheme = ClassificationScheme.CPC,
                            description = "string"
                        ),
                        quantity = BigDecimal.ONE,
                        unit = Unit(
                            id = UnitId(),
                            name = "string"
                        ),
                        relatedLot = LOT_ID
                    ),
                    Item(
                        id = ItemId.generate(),
                        description = "string",
                        internalId = null,
                        classification = Classification(
                            id = ClassificationId(),
                            scheme = ClassificationScheme.CPC,
                            description = "string"
                        ),
                        quantity = BigDecimal.ONE,
                        unit = Unit(
                            id = UnitId(),
                            name = "string"
                        ),
                        relatedLot = LotId.generate()
                    )
                )
            ),
            targets = Targets(emptyList()),
            criteria = Criteria(emptyList()),
            conversions = Conversions(emptyList()),
            procurementMethodModalities = ProcurementMethodModalities(emptyList()),
            awardCriteria = AwardCriteria.RATED_CRITERIA,
            awardCriteriaDetails = AwardCriteriaDetails.AUTOMATED,
            documents = Documents(emptyList()),
            value = Value(amount = null, currency = "string")
        ),
        relatedProcesses = listOf(
            RelatedProcess(
                id = RelatedProcessId.generate(),
                scheme = RelatedProcessScheme.OCID,
                identifier = "string",
                uri = "string",
                relationship = Relationships(emptyList())
            )
        ).let { RelatedProcesses(it) }
    )
}