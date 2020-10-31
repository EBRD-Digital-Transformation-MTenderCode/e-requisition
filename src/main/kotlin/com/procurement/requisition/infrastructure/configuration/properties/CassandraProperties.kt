package com.procurement.requisition.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cassandra")
data class CassandraProperties(
    var contactPoints: String? = null,
    var keyspaceName: String? = null,
    var username: String? = null,
    var password: String? = null
) {
    fun getContactPoints(): Array<String> =
        this.contactPoints!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
}
