package com.procurement.requisition.infrastructure.configuration

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.infrastructure.service.CustomLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfiguration {
    @Bean
    fun logger(): Logger = CustomLogger()
}
