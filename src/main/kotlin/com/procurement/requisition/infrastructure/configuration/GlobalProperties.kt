package com.procurement.requisition.infrastructure.configuration

import com.procurement.requisition.infrastructure.io.orThrow
import java.util.*

object GlobalProperties {

    val service = Service()

    class Service(
        val id: String = "22",
        val name: String = "e-requisition",
        val version: String = getGitProperties()
    )

    private fun getGitProperties(): String {
        val gitProps: Properties = try {
            GlobalProperties::class.java.getResourceAsStream("/git.properties")
                .use { stream ->
                    Properties().apply { load(stream) }
                }
        } catch (expected: Exception) {
            throw IllegalStateException(expected)
        }
        return gitProps.orThrow("git.commit.id.abbrev")
    }
}
