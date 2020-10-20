package com.procurement.requisition.application.extension

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.application.service.Transform

fun <T> T.trySerialization(transform: Transform) = transform.trySerialization(this)

inline fun <reified T> JsonNode.tryMapping(transform: Transform) = transform.tryMapping(this, T::class.java)
