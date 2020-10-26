package com.procurement.requisition.infrastructure.configuration

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.create.CreatePCRService
import com.procurement.requisition.application.service.get.lot.GetActiveLotsService
import com.procurement.requisition.application.service.get.tender.state.GetTenderStateService
import com.procurement.requisition.application.service.relation.CreateRelationService
import com.procurement.requisition.application.service.validate.ValidatePCRService
import com.procurement.requisition.infrastructure.handler.Handler
import com.procurement.requisition.infrastructure.handler.HandlerDescription
import com.procurement.requisition.infrastructure.handler.v2.pcr.create.CreatePCRHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.query.GetTenderStateHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.relation.CreateRelationHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.validate.ValidatePCRDataHandler
import com.procurement.requisition.infrastructure.handler.v1.HandlersV1
import com.procurement.requisition.infrastructure.handler.v1.lot.GetActiveLotsHandler
import com.procurement.requisition.infrastructure.handler.v2.HandlersV2
import com.procurement.requisition.infrastructure.web.v1.CommandsV1
import com.procurement.requisition.infrastructure.web.v2.CommandsV2
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
    val createPCRService: CreatePCRService,
    val createRelationService: CreateRelationService,
    val getTenderStateService: GetTenderStateService,
    val validatePCRService: ValidatePCRService,
    val getActiveLotsService: GetActiveLotsService
) {

    @Bean
    fun handlersV1() = HandlersV1(
        listOf(
            HandlerDescription(CommandsV1.CommandType.GET_ACTIVE_LOTS, getActiveLotsHandler()),
        )
    )

    @Bean
    fun handlersV2() = HandlersV2(
        listOf(
            HandlerDescription(CommandsV2.CommandType.VALIDATE_PCR_DATA, validatePcrDataHandler()),
            HandlerDescription(CommandsV2.CommandType.CREATE_PCR, createPCRHandler()),
            HandlerDescription(CommandsV2.CommandType.GET_TENDER_STATE, getTenderStateHandler()),
            HandlerDescription(
                CommandsV2.CommandType.CREATE_RELATION_TO_CONTRACT_PROCESS_STAGE,
                createRelationHandler()
            )
        )
    )

    @Bean
    fun getActiveLotsHandler(): Handler =
        GetActiveLotsHandler(logger = logger, transform = transform, getActiveLotsService = getActiveLotsService)

    @Bean
    fun validatePcrDataHandler(): Handler =
        ValidatePCRDataHandler(logger = logger, transform = transform, validatePCRService = validatePCRService)

    @Bean
    fun createPCRHandler(): Handler =
        CreatePCRHandler(
            logger = logger,
            transform = transform,
            createPCRService = createPCRService
        )

    @Bean
    fun getTenderStateHandler(): Handler =
        GetTenderStateHandler(logger = logger, transform = transform, getTenderStateService = getTenderStateService)

    @Bean
    fun createRelationHandler(): Handler =
        CreateRelationHandler(
            logger = logger,
            transform = transform,
            createRelationService = createRelationService
        )
}
