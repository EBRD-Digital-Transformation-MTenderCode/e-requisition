package com.procurement.requisition.application.service.create.model

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class StateFE(override val key: String) : EnumElementProvider.Element {

    EVALUATION("evaluation"),
    SUBMISSION("submission");

    override fun toString(): String = key

    companion object : EnumElementProvider<StateFE>(info = info())
}
