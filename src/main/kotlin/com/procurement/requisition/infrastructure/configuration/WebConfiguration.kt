package com.procurement.requisition.infrastructure.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.requisition.infrastructure.web",
        "com.procurement.requisition.infrastructure.handler"
    ]
)
class WebConfiguration
