package com.procurement.requisition.application.service.model

import com.procurement.requisition.lib.enumerator.EnumSubsetElementProvider

enum class OperationTypeGetOcidFromRelatedProcess(override val base: OperationType) : EnumSubsetElementProvider.Element<OperationType> {

    COMPLETE_SOURCING(OperationType.COMPLETE_SOURCING);

    companion object : EnumSubsetElementProvider<OperationTypeGetOcidFromRelatedProcess, OperationType>(info = info())
}


