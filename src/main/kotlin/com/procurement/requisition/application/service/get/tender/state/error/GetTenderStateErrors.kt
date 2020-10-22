package com.procurement.requisition.application.service.get.tender.state.error

import com.procurement.requisition.lib.fail.Failure

sealed class GetTenderStateErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    object TenderNotFound : GetTenderStateErrors(code = "VR.COM-17.4.1", description = "")
}
