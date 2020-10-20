package com.procurement.requisition.domain.model.tender.unit

import com.procurement.requisition.domain.model.EntityBase

data class Unit(override val id: UnitId, val name: String) : EntityBase<UnitId>()
