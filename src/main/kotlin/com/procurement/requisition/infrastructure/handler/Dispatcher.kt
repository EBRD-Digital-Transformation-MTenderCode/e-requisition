package com.procurement.requisition.infrastructure.handler

data class HandlerDescription(val action: Action, val handler: Handler)

abstract class Dispatcher(descriptions: List<HandlerDescription>) {

    private val handlers: Map<Action, Handler>

    init {
        handlers = mutableMapOf<Action, Handler>()
            .apply {
                descriptions.forEach { description ->
                    if (this.containsKey(description.action))
                        throw IllegalStateException("Duplicate handler for action '${description.action.key}'")
                    else
                        this[description.action] = description.handler
                }
            }
    }

    operator fun get(action: Action): Handler? = handlers[action]
}
