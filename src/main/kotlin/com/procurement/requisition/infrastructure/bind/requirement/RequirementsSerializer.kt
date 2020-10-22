package com.procurement.requisition.infrastructure.bind.requirement

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.procurement.requisition.domain.model.requirement.ExpectedValue
import com.procurement.requisition.domain.model.requirement.MaxValue
import com.procurement.requisition.domain.model.requirement.MinValue
import com.procurement.requisition.domain.model.requirement.NoneValue
import com.procurement.requisition.domain.model.requirement.RangeValue
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.infrastructure.handler.converter.asString
import java.math.BigDecimal

class RequirementsSerializer : JsonSerializer<List<Requirement>>() {

    companion object {

        fun serialize(requirements: List<Requirement>): ArrayNode {
            fun BigDecimal.jsonFormat() = BigDecimal("%.3f".format(this))

            val serializedRequirements = JsonNodeFactory.withExactBigDecimals(true).arrayNode()

            requirements.map { requirement ->
                val requirementNode = JsonNodeFactory.withExactBigDecimals(true).objectNode()

                requirementNode.put("id", requirement.id)
                requirementNode.put("title", requirement.title)
                requirementNode.put("dataType", requirement.dataType.toString())

                requirement.description?.let { requirementNode.put("description", it) }

                requirement.period?.let {
                    requirementNode.putObject("period")
                        .put("startDate", it.startDate.asString())
                        .put("endDate", it.endDate.asString())
                }


                when (requirement.value) {
                    is ExpectedValue.AsString -> {
                        requirementNode.put("expectedValue", requirement.value.value)
                    }
                    is ExpectedValue.AsBoolean -> {
                        requirementNode.put("expectedValue", requirement.value.value)
                    }
                    is ExpectedValue.AsNumber -> {
                        requirementNode.put("expectedValue", requirement.value.toString())
                    }
                    is ExpectedValue.AsInteger -> {
                        requirementNode.put("expectedValue", requirement.value.value)
                    }
                    is RangeValue.AsNumber -> {
                        requirementNode.put("minValue", requirement.value.minValue.jsonFormat())
                        requirementNode.put("maxValue", requirement.value.maxValue.jsonFormat())
                    }
                    is RangeValue.AsInteger -> {
                        requirementNode.put("minValue", requirement.value.minValue)
                        requirementNode.put("maxValue", requirement.value.maxValue)
                    }
                    is MinValue.AsNumber -> {
                        requirementNode.put("minValue", requirement.value.toString())
                    }
                    is MinValue.AsInteger -> {
                        requirementNode.put("minValue", requirement.value.value)
                    }
                    is MaxValue.AsNumber -> {
                        requirementNode.put("maxValue", requirement.value.toString())
                    }
                    is MaxValue.AsInteger -> {
                        requirementNode.put("maxValue", requirement.value.value)
                    }
                    is NoneValue -> Unit
                }

                requirementNode
            }.also { it.forEach { requirement -> serializedRequirements.add(requirement) } }

            return serializedRequirements
        }
    }

    override fun serialize(
        requirements: List<Requirement>,
        jsonGenerator: JsonGenerator,
        provider: SerializerProvider
    ) =
        jsonGenerator.writeTree(serialize(requirements))
}
