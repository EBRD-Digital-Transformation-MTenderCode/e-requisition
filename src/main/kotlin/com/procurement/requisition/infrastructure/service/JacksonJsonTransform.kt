package com.procurement.requisition.infrastructure.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.NullNode
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.error.TransformErrors
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.Result.Companion.success
import java.io.IOException

class JacksonJsonTransform(private val mapper: ObjectMapper) : Transform {

    /**
     * Parsing
     */
    override fun tryParse(value: String): Result<JsonNode, TransformErrors.Parsing> = try {
        success(mapper.readTree(value))
    } catch (expected: IOException) {
        failure(TransformErrors.Parsing(value = value, reason = expected))
    }

    /**
     * Mapping
     */
    override fun <R> tryMapping(value: JsonNode, target: Class<R>): Result<R, TransformErrors.Mapping> =
        try {
            if (value is NullNode)
                failure(TransformErrors.Mapping(description = "Object to map must not be null.", reason = null))
            else success(mapper.treeToValue(value, target))
        } catch (expected: Exception) {
            failure(TransformErrors.Mapping(description = "Error of mapping.", reason = expected))
        }

    override fun <R> tryMapping(
        value: JsonNode,
        typeRef: TypeReference<R>
    ): Result<R, TransformErrors.Mapping> = try {
        val parser = mapper.treeAsTokens(value)
        success(mapper.readValue(parser, typeRef))
    } catch (expected: Exception) {
        failure(TransformErrors.Mapping(description = "Error of mapping.", reason = expected))
    }

    /**
     * Deserialization
     */
    override fun <R> tryDeserialization(
        value: String,
        target: Class<R>
    ): Result<R, TransformErrors.Deserialization> = try {
        success(mapper.readValue(value, target))
    } catch (expected: Exception) {
        failure(
            TransformErrors.Deserialization(description = "Error of deserialization.", reason = expected)
        )
    }

    override fun <R> tryDeserialization(
        value: String,
        typeRef: TypeReference<R>
    ): Result<R, TransformErrors.Deserialization> = try {
        success(mapper.readValue(value, typeRef))
    } catch (expected: Exception) {
        failure(
            TransformErrors.Deserialization(description = "Error of deserialization.", reason = expected)
        )
    }

    /**
     * Serialization
     */
    override fun <R> trySerialization(value: R): Result<String, TransformErrors.Serialization> = try {
        success(mapper.writeValueAsString(value))
    } catch (expected: Exception) {
        failure(TransformErrors.Serialization(description = "Error of serialization.", reason = expected))
    }

    /**
     * ???
     */
    override fun tryToJson(value: JsonNode): Result<String, TransformErrors.Serialization> = try {
        success(mapper.writeValueAsString(value))
    } catch (expected: Exception) {
        failure(TransformErrors.Serialization(description = "Error of serialization.", reason = expected))
    }
}
