package com.procurement.requisition.domain.model.relatedprocesses

class RelatedProcesses(values: List<RelatedProcess> = emptyList()) : List<RelatedProcess> by values {

    constructor(relatedProcess: RelatedProcess) : this(listOf(relatedProcess))

    operator fun plus(other: RelatedProcess) = RelatedProcesses(this as List<RelatedProcess> + other)
}
