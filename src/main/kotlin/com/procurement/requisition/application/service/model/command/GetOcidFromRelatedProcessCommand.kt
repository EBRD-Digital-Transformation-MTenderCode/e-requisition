package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.lib.enumerator.EnumElementProvider
import com.procurement.requisition.application.service.model.OperationType as ParentOperationType

data class GetOcidFromRelatedProcessCommand(
    val cpid: Cpid,
    val ocid: Ocid,
    val operationType: OperationType
){
    enum class OperationType(val base: ParentOperationType) : EnumElementProvider.Element {

        COMPLETE_SOURCING(ParentOperationType.COMPLETE_SOURCING);

        override val key: String
            get() = base.key

        override val deprecated: Boolean
            get() = base.deprecated

        companion object : EnumElementProvider<OperationType>(info = info())
    }
}