package com.procurement.requisition.infrastructure.handler

interface Action {
    val key: String
    val kind: Kind

    enum class Kind { COMMAND, QUERY }
}
