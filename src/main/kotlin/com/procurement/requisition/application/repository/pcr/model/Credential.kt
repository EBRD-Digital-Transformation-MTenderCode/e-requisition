package com.procurement.requisition.application.repository.pcr.model

import com.procurement.requisition.domain.model.Token

data class Credential(val token: Token, val owner: String)
