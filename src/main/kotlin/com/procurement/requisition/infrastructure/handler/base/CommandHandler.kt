package com.procurement.requisition.infrastructure.handler.base

import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class CommandHandler
