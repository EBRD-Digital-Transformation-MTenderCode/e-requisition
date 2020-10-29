package com.procurement.requisition.infrastructure.repository.pcr.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcesses
import com.procurement.requisition.infrastructure.handler.converter.asCpid
import com.procurement.requisition.infrastructure.handler.converter.asSingleStageOcid
import com.procurement.requisition.infrastructure.handler.converter.asToken
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.TenderEntity
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.mappingToDomain
import com.procurement.requisition.infrastructure.repository.pcr.model.tender.mappingToEntity
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

fun PCR.mappingToEntity() = PCREntity(
    cpid = cpid.underlying,
    ocid = ocid.underlying,
    token = token.underlying,
    owner = owner,
    tender = tender.mappingToEntity(),
    relatedProcesses = relatedProcesses.map { it.mappingToEntity() }
)

fun PCREntity.mappingToDomain(): Result<PCR, JsonErrors> {
    val cpid = cpid.asCpid(path = "#/cpid").onFailure { return it }
    val ocid = ocid.asSingleStageOcid(path = "#/ocid").onFailure { return it }
    val token = token.asToken(path = "#/token").onFailure { return it }
    val tender = tender.mappingToDomain("#/tender").onFailure { return it }
    val relatedProcesses = relatedProcesses
        .mapIndexedOrEmpty { idx, relatedProcess ->
            relatedProcess.mappingToDomain(path = "#/relatedProcesses[$idx]").onFailure { return it }
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
