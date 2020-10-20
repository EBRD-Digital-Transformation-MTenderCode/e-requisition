package com.procurement.requisition.infrastructure.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    LoggerConfiguration::class,
    RepositoryConfiguration::class,
    ServiceConfiguration::class,
    TransformConfiguration::class,
    WebConfiguration::class,
)
class ApplicationConfiguration
