package com.procurement.requisition.infrastructure.configuration

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.create.CreatePCRService
import com.procurement.requisition.application.service.get.tender.state.GetTenderStateService
import com.procurement.requisition.application.service.validate.ValidatePCRService
import com.procurement.requisition.infrastructure.handler.Handler
import com.procurement.requisition.infrastructure.handler.HandlerDescription
import com.procurement.requisition.infrastructure.handler.Handlers
import com.procurement.requisition.infrastructure.handler.model.ApiVersion
import com.procurement.requisition.infrastructure.handler.model.CommandType.CREATE_PCR
import com.procurement.requisition.infrastructure.handler.model.CommandType.GET_TENDER_STATE
import com.procurement.requisition.infrastructure.handler.model.CommandType.VALIDATE_PCR_DATA
import com.procurement.requisition.infrastructure.handler.pcr.create.CreatePCRHandler
import com.procurement.requisition.infrastructure.handler.pcr.query.GetTenderStateHandler
import com.procurement.requisition.infrastructure.handler.pcr.validate.ValidatePCRDataHandler
import com.procurement.requisition.infrastructure.service.HistoryRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.requisition.infrastructure.web.controller",
        "com.procurement.requisition.infrastructure.handler"
    ]
)
class WebConfiguration(
    val logger: Logger,
    val transform: Transform,
    val validatePCRService: ValidatePCRService,
    val createPCRService: CreatePCRService,
    val getTenderStateService: GetTenderStateService,
    val historyRepository: HistoryRepository
) {

    @Bean
    fun handlers() = Handlers(
        HandlerDescription(ApiVersion(2, 0, 0), VALIDATE_PCR_DATA, validatePcrDataHandler()),
        HandlerDescription(ApiVersion(2, 0, 0), CREATE_PCR, createPCRHandler()),
        HandlerDescription(ApiVersion(2, 0, 0), GET_TENDER_STATE, getTenderStateHandler())
    )

    @Bean
    fun validatePcrDataHandler(): Handler =
        ValidatePCRDataHandler(logger = logger, transform = transform, validatePCRService = validatePCRService)

    @Bean
    fun createPCRHandler(): Handler =
        CreatePCRHandler(
            logger = logger,
            transform = transform,
            historyRepository = historyRepository,
            createPCRService = createPCRService
        )

    @Bean
    fun getTenderStateHandler(): Handler =
        GetTenderStateHandler(logger = logger, transform = transform, getTenderStateService = getTenderStateService)
}
