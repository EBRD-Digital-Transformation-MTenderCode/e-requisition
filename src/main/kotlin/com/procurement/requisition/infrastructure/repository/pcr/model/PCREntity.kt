package com.procurement.requisition.infrastructure.repository.pcr.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcesses
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.TenderEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.deserialization
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.serialization
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapIndexedOrEmpty

data class PCREntity(
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("token") @param:JsonProperty("token") val token: String,
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String,
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: TenderEntity,
    @field:JsonProperty("relatedProcesses") @param:JsonProperty("relatedProcesses") val relatedProcesses: List<RelatedProcessEntity>
)

fun PCR.serialization() = PCREntity(
    cpid = cpid.underlying,
    ocid = ocid.underlying,
    token = token.underlying,
    owner = owner,
    tender = tender.serialization(),
    relatedProcesses = relatedProcesses.map { it.serialization() }
)

fun PCREntity.deserialization(): Result<PCR, JsonErrors> {
    val cpid = Cpid.tryCreateOrNull(cpid)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "#/cpid",
                actualValue = cpid,
                expectedFormat = Cpid.pattern,
                reason = null
            )
        )
    val ocid = Ocid.SingleStage.tryCreateOrNull(ocid)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "#/ocid",
                actualValue = ocid,
                expectedFormat = Ocid.SingleStage.pattern,
                reason = null
            )
        )
    val token = Token.orNull(token)
        ?: return Result.failure(
            JsonErrors.DataFormatMismatch(
                path = "#/token",
                actualValue = token,
                expectedFormat = Token.pattern,
                reason = null
            )
        )
    val tender = tender.deserialization("#/tender")
        .onFailure { return it }
    val relatedProcesses = relatedProcesses
        .mapIndexedOrEmpty { idx, relatedProcess ->
            relatedProcess.deserialization(path = "#/relatedProcesses[$idx]").onFailure { return it }
        }
        .let { RelatedProcesses(it) }

    return PCR(
        cpid = cpid,
        ocid = ocid,
        token = token,
        owner = owner,
        tender = tender,
        relatedProcesses = relatedProcesses
    ).asSuccess()
}
