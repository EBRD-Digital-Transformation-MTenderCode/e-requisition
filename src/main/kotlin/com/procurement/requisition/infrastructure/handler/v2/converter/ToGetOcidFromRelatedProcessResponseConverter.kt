package com.procurement.requisition.infrastructure.handler.v2.converter

import com.procurement.requisition.application.service.model.result.GetOcidFromRelatedProcessResult
import com.procurement.requisition.infrastructure.handler.v2.model.response.GetOcidFromRelatedProcessResponse

fun GetOcidFromRelatedProcessResult.convert() = GetOcidFromRelatedProcessResponse(ocid.underlying)

