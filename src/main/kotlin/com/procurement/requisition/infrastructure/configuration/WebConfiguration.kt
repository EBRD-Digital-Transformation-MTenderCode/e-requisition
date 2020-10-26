package com.procurement.requisition.infrastructure.configuration

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.create.pcr.CreatePCRService
import com.procurement.requisition.application.service.create.request.CreateRequestsForEvPanelsService
import com.procurement.requisition.application.service.get.lot.GetActiveLotsService
import com.procurement.requisition.application.service.get.tender.state.GetTenderStateService
import com.procurement.requisition.application.service.relation.CreateRelationService
import com.procurement.requisition.application.service.set.SetLotsStatusUnsuccessfulService
import com.procurement.requisition.application.service.set.SetTenderUnsuccessfulService
import com.procurement.requisition.application.service.validate.ValidatePCRService
import com.procurement.requisition.infrastructure.handler.Handler
import com.procurement.requisition.infrastructure.handler.HandlerDescription
import com.procurement.requisition.infrastructure.handler.v1.HandlersV1
import com.procurement.requisition.infrastructure.handler.v1.create.request.CreateRequestsForEvPanelsHandler
import com.procurement.requisition.infrastructure.handler.v1.lot.GetActiveLotsHandler
import com.procurement.requisition.infrastructure.handler.v1.set.SetLotsStatusUnsuccessfulHandler
import com.procurement.requisition.infrastructure.handler.v1.set.SetTenderUnsuccessfulHandler
import com.procurement.requisition.infrastructure.handler.v2.HandlersV2
import com.procurement.requisition.infrastructure.handler.v2.pcr.create.CreatePCRHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.query.GetTenderStateHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.relation.CreateRelationHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.validate.ValidatePCRDataHandler
import com.procurement.requisition.infrastructure.web.v1.CommandsV1
import com.procurement.requisition.infrastructure.web.v2.CommandsV2
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.requisition.infrastructure.web",
        "com.procurement.requisition.infrastructure.handler"
    ]
)
class WebConfiguration(
    val logger: Logger,
    val transform: Transform,
    val createPCRService: CreatePCRService,
    val createRelationService: CreateRelationService,
    val createRequestsForEvPanelsService: CreateRequestsForEvPanelsService,
    val getActiveLotsService: GetActiveLotsService,
    val getTenderStateService: GetTenderStateService,
    val setLotsStatusUnsuccessfulService: SetLotsStatusUnsuccessfulService,
    val setTenderUnsuccessfulService: SetTenderUnsuccessfulService,
    val validatePCRService: ValidatePCRService,
) {

    @Bean
    fun handlersV1() = HandlersV1(
        listOf(
            HandlerDescription(CommandsV1.CommandType.CREATE_REQUESTS_FOR_EV_PANELS, createRequestsForEvPanelsHandler()),
            HandlerDescription(CommandsV1.CommandType.GET_ACTIVE_LOTS, getActiveLotsHandler()),
            HandlerDescription(CommandsV1.CommandType.SET_LOTS_UNSUCCESSFUL, setLotsStatusUnsuccessfulHandler()),
            HandlerDescription(CommandsV1.CommandType.SET_TENDER_UNSUCCESSFUL, setTenderUnsuccessfulHandler()),
        )
    )

    @Bean
    fun createRequestsForEvPanelsHandler(): Handler =
        CreateRequestsForEvPanelsHandler(
            logger = logger,
            transform = transform,
            createRequestsForEvPanelsService = createRequestsForEvPanelsService
        )

    @Bean
    fun getActiveLotsHandler(): Handler =
        GetActiveLotsHandler(logger = logger, transform = transform, getActiveLotsService = getActiveLotsService)

    @Bean
    fun setLotsStatusUnsuccessfulHandler(): Handler =
        SetLotsStatusUnsuccessfulHandler(logger = logger, transform = transform, setLotsStatusUnsuccessfulService = setLotsStatusUnsuccessfulService)
    
    @Bean
    fun setTenderUnsuccessfulHandler(): Handler =
        SetTenderUnsuccessfulHandler(logger = logger, transform = transform, setTenderUnsuccessfulService = setTenderUnsuccessfulService)

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
