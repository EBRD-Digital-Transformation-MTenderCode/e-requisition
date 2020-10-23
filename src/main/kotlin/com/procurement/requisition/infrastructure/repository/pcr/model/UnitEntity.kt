package com.procurement.requisition.infrastructure.repository.pcr.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.tender.unit.Unit
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess

data class UnitEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("name") @param:JsonProperty("name") val name: String
)

fun Unit.serialization() = UnitEntity(id = id, name = name)

fun UnitEntity.deserialization(path: String): Result<Unit, JsonErrors> =
    Unit(id = id, name = name).asSuccess()
