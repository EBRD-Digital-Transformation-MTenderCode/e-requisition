package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.infrastructure.web.dto.ApiVersion

data class HandlerDescription(val version: ApiVersion, val action: Action, val handler: Handler)

class Handlers(private val items: Map<ApiVersion, Map<Action, Handler>>) {

    constructor(vararg descriptions: HandlerDescription) : this(
        items = mutableMapOf<ApiVersion, MutableMap<Action, Handler>>()
            .apply {
                descriptions.forEach { description ->
                    val actions = computeIfAbsent(description.version) { mutableMapOf() }
                    if (description.action in actions)
                        throw IllegalStateException("Duplicate handler: version '${description.version}', action '${description.action.key}'")
                    else
                        actions[description.action] = description.handler
                }
            }
    )

    operator fun get(version: ApiVersion, action: Action): Handler? = items[version]?.let { it[action] }
}
