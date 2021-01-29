package com.procurement.requisition.application.service

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.requisition.application.service.model.OperationTypeGetOcidFromRelatedProcess
import com.procurement.requisition.application.service.model.command.GetOcidFromRelatedProcessCommand
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
import com.procurement.requisition.domain.model.relatedprocesses.Relationship
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
import com.procurement.requisition.domain.model.tender.lot.Lots
import com.procurement.requisition.domain.model.tender.target.Targets
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.util.*

internal class GetOcidFromRelatedProcessServiceTest {

    companion object {
        val CPID = Cpid.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892")!!
        val OCID = Ocid.SingleStage.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892-EV-1580458791896")!!
        val OPERATION_TYPE = OperationTypeGetOcidFromRelatedProcess.COMPLETE_SOURCING

        @JvmStatic
        fun incorrectRelationships() = listOf(
            Arguments.of(Relationship.PARENT),
            Arguments.of(Relationship.X_PRE_AWARD_CATALOG_REQUEST)
        )
    }

    private lateinit var getOcidFromRelatedProcessService: GetOcidFromRelatedProcessService
    private lateinit var pcrManagement: PCRManagementService

    @BeforeEach
    fun init() {
        pcrManagement = mock()
        getOcidFromRelatedProcessService = GetOcidFromRelatedProcessService(pcrManagement)
    }

    @Test
    fun get_success() {
        val command = getCommand()
        val expectedOcid = Ocid.SingleStage.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892-EV-1580458791800")!!
        val expectedRelationship = Relationship.X_FRAMEWORK
        val pcr = generatePcr(command, expectedOcid, expectedRelationship)
        whenever(pcrManagement.find(pcr.cpid, pcr.ocid)).thenReturn(pcr.asSuccess())
        val actual = getOcidFromRelatedProcessService.get(command).orNull!!.ocid

        assertEquals(expectedOcid, actual)
    }

    @ParameterizedTest
    @MethodSource("incorrectRelationships")
    fun get_incorrectRelationship_fail(incorrectRelationship: Relationship) {
        val command = getCommand()
        val ocid = Ocid.SingleStage.tryCreateOrNull("ocds-b3wdp1-MD-1580458690892-EV-1580458791800")!!
        val pcr = generatePcr(command, ocid, incorrectRelationship)
        whenever(pcrManagement.find(pcr.cpid, pcr.ocid)).thenReturn(pcr.asSuccess())
        val actual = getOcidFromRelatedProcessService.get(command) as Result.Failure
        val expectedErrorCode ="VR.COM-17.15.2"
        val expectedMessage = "No relatedProcesses with relationship 'x_framework' found."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedMessage, actual.reason.description)
    }

    @Test
    fun get_pcrNotFound_fail() {
        val command = getCommand()
        whenever(pcrManagement.find(command.cpid, command.ocid)).thenReturn(null.asSuccess())
        val actual = getOcidFromRelatedProcessService.get(command)as Result.Failure

        val expectedErrorCode ="VR.COM-17.15.1"
        val expectedMessage = "PCR by cpid '${command.cpid}' and ocid '${command.ocid}' is not found."

        assertEquals(expectedErrorCode, actual.reason.code)
        assertEquals(expectedMessage, actual.reason.description)
    }

    private fun generatePcr(
        command: GetOcidFromRelatedProcessCommand,
        expectedOcid: Ocid,
        expectedRelationship: Relationship
    ) = PCR(
        cpid = command.cpid,
        ocid = command.ocid,
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
        relatedProcesses = listOf(RelatedProcess(
            id = RelatedProcessId.generate(),
            scheme = RelatedProcessScheme.OCID,
            identifier = expectedOcid.toString(),
            uri = "string",
            relationship = listOf(expectedRelationship).let { Relationships(it) }
        )).let { RelatedProcesses(it) }
    )

    private fun getCommand() = GetOcidFromRelatedProcessCommand(
        cpid = CPID,
        ocid = OCID,
        operationType = OPERATION_TYPE
    )
}