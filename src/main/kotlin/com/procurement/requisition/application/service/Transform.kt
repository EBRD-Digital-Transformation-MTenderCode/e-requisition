package com.procurement.requisition.application.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.requisition.domain.failure.error.TransformErrors
import com.procurement.requisition.lib.functional.Result

interface Transform {

    /**
     * Parsing
     */
    fun tryParse(value: String): Result<JsonNode, TransformErrors.Parsing>

    /**
     * Mapping
     */
    fun <R> tryMapping(value: JsonNode, target: Class<R>): Result<R, TransformErrors.Mapping>
    fun <R> tryMapping(value: JsonNode, typeRef: TypeReference<R>): Result<R, TransformErrors.Mapping>

    /**
     * Deserialization
     */
    fun <R> tryDeserialization(value: String, target: Class<R>): Result<R, TransformErrors.Deserialization>
    fun <R> tryDeserialization(value: String, typeRef: TypeReference<R>): Result<R, TransformErrors.Deserialization>

    /**
     * Serialization
     */
    fun <R> trySerialization(value: R): Result<String, TransformErrors.Serialization>

    /**
     * ???
     */
    fun tryToJson(value: JsonNode): Result<String, TransformErrors.Serialization>
}
