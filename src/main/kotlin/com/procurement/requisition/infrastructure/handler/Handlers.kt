package com.procurement.requisition.infrastructure.handler

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.lib.console.ConsoleColor
import org.springframework.stereotype.Component

@Component
class Handlers(
    val logger: Logger,
    handlers: List<Handler>
) {

    private val items: Map<ApiVersion, Map<Action, Handler>> =
        mutableMapOf<ApiVersion, MutableMap<Action, Handler>>()
            .apply {
                handlers.forEach { handler ->
                    val actions = computeIfAbsent(handler.version) { mutableMapOf() }
                    if (handler.action in actions)
                        throw IllegalStateException("Duplicate handler for action '${handler.action.key}' version '${handler.version.underlying}'.")
                    else {
                        actions[handler.action] = handler
                    }
                }
                showRegisteredHandlers(this)
            }

    private fun showRegisteredHandlers(items: Map<ApiVersion, Map<Action, Handler>>) {
        val countHandler = items.values.fold(0) { acc, actions -> acc + actions.size }
        val headerStr = "Registered $countHandler handlers:"
        println(ConsoleColor.GREEN_BOLD + headerStr + ConsoleColor.RESET)

        items.keys
            .sortedBy { it }
            .forEach { version ->
                val versionStr = "  version: ${version.underlying}"
                println(ConsoleColor.BLUE + versionStr + ConsoleColor.RESET)

                items[version]?.keys
                    ?.sortedBy { it.key }
                    ?.forEach { action ->
                        println(ConsoleColor.GREEN + "    \u2713" + ConsoleColor.YELLOW + " ${action.key}" + ConsoleColor.CYAN + " (${action.kind.name.toLowerCase()})" + ConsoleColor.RESET)
                    }
            }
    }

    operator fun get(version: ApiVersion, action: Action): Handler? = items[version]?.let { it[action] }
}
