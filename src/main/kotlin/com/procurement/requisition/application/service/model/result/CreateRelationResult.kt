package com.procurement.requisition.application.service.model.result

import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessId
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessScheme
import com.procurement.requisition.domain.model.relatedprocesses.Relationship

data class CreateRelationResult(
    val relatedProcesses: List<RelatedProcess>
) {

    data class RelatedProcess(
        val id: RelatedProcessId,
        val scheme: RelatedProcessScheme,
        val identifier: String,
        val relationship: List<Relationship>,
        val uri: String
    )
}
