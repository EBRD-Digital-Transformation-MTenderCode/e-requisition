package com.procurement.requisition.infrastructure.configuration

import com.procurement.requisition.infrastructure.configuration.properties.OCDSProperties
import com.procurement.requisition.infrastructure.configuration.properties.UriProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.requisition.service",
        "com.procurement.requisition.application.service",
    ]
)
@EnableConfigurationProperties(value = [OCDSProperties::class, UriProperties::class])
class ServiceConfiguration
