package com.procurement.requisition.application.service.get.tender.state.model

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class GetTenderStateCommand(val cpid: Cpid, val ocid: Ocid)
