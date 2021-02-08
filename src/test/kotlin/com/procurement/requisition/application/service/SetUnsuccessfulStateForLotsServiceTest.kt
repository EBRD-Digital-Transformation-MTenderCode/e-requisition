package com.procurement.requisition.application.service

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.requisition.application.service.model.command.SetUnsuccessfulStateForLotsCommand
import com.procurement.requisition.application.service.model.result.SetUnsuccessfulStateForLotsResult
import com.procurement.requisition.domain.extension.nowDefaultUTC
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.domain.model.Stage
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
import com.procurement.requisition.domain.model.tender.item.Items
import com.procurement.requisition.domain.model.tender.lot.Lot
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.lot.LotStatus
import com.procurement.requisition.domain.model.tender.lot.LotStatusDetails
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.domain.model.tender.lot.Variants
import com.procurement.requisition.domain.model.tender.target.Targets
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

internal class SetUnsuccessfulStateForLotsServiceTest {

    private lateinit var setUnsuccessfulStateForLotsService: SetUnsuccessfulStateForLotsService
    private lateinit var pcrManagement: PCRManagementService

    companion object {
        private val CPID = Cpid.generate(prefix = "ocds", country = "MD", timestamp = nowDefaultUTC())
        private val OCID = Ocid.SingleStage.generate(cpid = CPID, stage = Stage.PC, timestamp = nowDefaultUTC())
        private val LOT_ID = LotId.generate()
    }

    @BeforeEach
    fun init() {
        pcrManagement = mock()
        setUnsuccessfulStateForLotsService = SetUnsuccessfulStateForLotsService(pcrManagement)
    }

    @Test
    fun set_success() {
        val command = getCommand()
        whenever(pcrManagement.find(cpid = CPID, ocid = OCID)).thenReturn(generatePcr(LOT_ID).asSuccess())
        whenever(pcrManagement.update(cpid = eq(CPID), ocid = eq(OCID), pcr = any())).thenReturn(true.asSuccess())

        val actual = setUnsuccessfulStateForLotsService.set(command).orNull
        val expected = SetUnsuccessfulStateForLotsResult(
                tender = SetUnsuccessfulStateForLotsResult.Tender(
                    lots = listOf(
                        SetUnsuccessfulStateForLotsResult.Tender.Lot(
                            id = LOT_ID,
                            status = LotStatus.UNSUCCESSFUL,
                            statusDetails = LotStatusDetails.ALL_REJECTED
                        )
                    )
                )
            )


        assertEquals(expected, actual)
    }

    @Test
    fun set_noPcrFound_fail() {
        val command = getCommand()
        whenever(pcrManagement.find(cpid = CPID, ocid = OCID)).thenReturn(Result.Success(null))
        whenever(pcrManagement.update(cpid = eq(CPID), ocid = eq(OCID), pcr = any())).thenReturn(true.asSuccess())

        val actual = setUnsuccessfulStateForLotsService.set(command) as Result.Failure
        val expectedErrorCode = "VR.COM-17.16.1"
        val expectedErrorMessage = "PCR by cpid '$CPID' and ocid '$OCID' is not found."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedErrorMessage, actual.reason.description)
    }

    @Test
    fun set_lotNotFound_fail() {
        val command = getCommand()
        val unknownLotId = LotId.generate()
        whenever(pcrManagement.find(cpid = CPID, ocid = OCID)).thenReturn(generatePcr(unknownLotId).asSuccess())
        whenever(pcrManagement.update(cpid = eq(CPID), ocid = eq(OCID), pcr = any())).thenReturn(true.asSuccess())

        val actual = setUnsuccessfulStateForLotsService.set(command) as Result.Failure
        val expectedErrorCode = "VR.COM-17.16.2"
        val expectedErrorMessage = "Unknown lot(s) '$LOT_ID'."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedErrorMessage, actual.reason.description)
    }

    private fun getCommand() = SetUnsuccessfulStateForLotsCommand(
        cpid = CPID,
        ocid = OCID,
        tender = SetUnsuccessfulStateForLotsCommand.Tender(
            lots = listOf(
                SetUnsuccessfulStateForLotsCommand.Tender.Lot(
                    id = LOT_ID
                )
            )
        )
    )

    private fun generatePcr(
        expectedId: LotId
    ) = PCR(
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
            lots = Lots(
                listOf(
                    Lot(
                        id = expectedId,
                        statusDetails = LotStatusDetails.AWARDED,
                        status = LotStatus.UNSUCCESSFUL,
                        description = null,
                        classification = Classification(
                            id = ClassificationId(),
                            description = "string",
                            scheme = ClassificationScheme.CPC,
                            uri = null
                        ),
                        title = "string",
                        variants = Variants(emptyList()),
                        internalId = null
                    )
                )
            ),
            items = Items(emptyList()),
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