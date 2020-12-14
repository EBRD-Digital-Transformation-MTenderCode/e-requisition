package com.procurement.requisition.application.service.model.command

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid

data class GetTenderStateCommand(val cpid: Cpid, val ocid: Ocid)
