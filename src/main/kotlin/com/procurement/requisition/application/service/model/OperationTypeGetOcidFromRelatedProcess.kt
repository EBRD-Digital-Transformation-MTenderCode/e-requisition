package com.procurement.requisition.application.service.model

import com.procurement.requisition.lib.enumerator.EnumElementProvider

enum class OperationTypeGetOcidFromRelatedProcess(val base: OperationType) : EnumElementProvider.Element {

    COMPLETE_SOURCING(OperationType.COMPLETE_SOURCING);

    override val key: String
        get() = base.key

    companion object : EnumElementProvider<OperationTypeGetOcidFromRelatedProcess>(info = info()){
        private val valuesByBaseKey = values().associateBy { it.base.key }

        fun valueOf(text: String): OperationTypeGetOcidFromRelatedProcess? = valuesByBaseKey[text]
    }
}
