package com.procurement.requisition.domain.model.relatedprocesses

class RelatedProcesses(values: List<RelatedProcess> = emptyList()) : List<RelatedProcess> by values {

    constructor(relatedProcess: RelatedProcess) : this(listOf(relatedProcess))
}
