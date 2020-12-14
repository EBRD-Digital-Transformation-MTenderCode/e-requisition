package com.procurement.requisition.application.service

import com.procurement.requisition.application.repository.pcr.PCRDeserializer
import com.procurement.requisition.application.repository.pcr.PCRRepository
import com.procurement.requisition.application.repository.pcr.PCRSerializer
import com.procurement.requisition.application.repository.pcr.model.Credential
import com.procurement.requisition.application.repository.pcr.model.TenderState
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.PCR
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.flatMap
import org.springframework.stereotype.Service

interface PCRManagementService {
    fun create(cpid: Cpid, ocid: Ocid, pcr: PCR): Result<Boolean, Failure>
    fun find(cpid: Cpid, ocid: Ocid): Result<PCR?, Failure>
    fun findCredential(cpid: Cpid, ocid: Ocid): Result<Credential?, Failure>
    fun findState(cpid: Cpid, ocid: Ocid): Result<TenderState?, Failure>
    fun update(cpid: Cpid, ocid: Ocid, pcr: PCR): Result<Boolean, Failure>
}

@Service
class PCRManagementServiceImpl(
    private val pcrRepository: PCRRepository,
    private val pcrDeserializer: PCRDeserializer,
    private val pcrSerializer: PCRSerializer,
) : PCRManagementService {

    override fun create(cpid: Cpid, ocid: Ocid, pcr: PCR): Result<Boolean, Failure> {
        val json = pcrSerializer.build(pcr).onFailure { return it }
        val state = TenderState(status = pcr.tender.status, statusDetails = pcr.tender.statusDetails)
        val credential = Credential(token = pcr.token, owner = pcr.owner)
        return pcrRepository.saveNew(cpid = cpid, ocid = ocid, credential = credential, state = state, data = json)
    }

    override fun find(cpid: Cpid, ocid: Ocid): Result<PCR?, Failure> =
        pcrRepository
            .getPCR(cpid = cpid, ocid = ocid)
            .flatMap { json ->
                json?.let { pcrDeserializer.build(it) }
                    ?: Result.success(null)
            }

    override fun findCredential(cpid: Cpid, ocid: Ocid): Result<Credential?, Failure> = pcrRepository
        .getCredential(cpid = cpid, ocid = ocid)

    override fun findState(cpid: Cpid, ocid: Ocid): Result<TenderState?, Failure> = pcrRepository
        .getTenderState(cpid = cpid, ocid = ocid)

    override fun update(cpid: Cpid, ocid: Ocid, pcr: PCR): Result<Boolean, Failure> {
        val json = pcrSerializer.build(pcr).onFailure { return it }
        val state = TenderState(status = pcr.tender.status, statusDetails = pcr.tender.statusDetails)
        return pcrRepository.update(cpid = cpid, ocid = ocid, state = state, data = json)
    }
}
