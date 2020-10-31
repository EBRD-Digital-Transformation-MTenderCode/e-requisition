package com.procurement.requisition

import com.procurement.requisition.infrastructure.configuration.ApplicationConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfiguration::class])
class RequisitionApplication

fun main(args: Array<String>) {
    runApplication<RequisitionApplication>(*args)
}

