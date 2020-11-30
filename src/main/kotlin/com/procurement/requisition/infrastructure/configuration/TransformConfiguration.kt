package com.procurement.requisition.infrastructure.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.infrastructure.bind.configuration
import com.procurement.requisition.infrastructure.service.JacksonJsonTransform
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class TransformConfiguration {

    @Bean
    fun transform(): Transform = JacksonJsonTransform(mapper = ObjectMapper().apply { configuration() })
}
