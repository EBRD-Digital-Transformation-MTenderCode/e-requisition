package com.procurement.requisition.infrastructure.configuration

import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.create.pcr.CreatePCRService
import com.procurement.requisition.application.service.create.request.CreateRequestsForEvPanelsService
import com.procurement.requisition.application.service.find.items.FindItemsByLotIdsService
import com.procurement.requisition.application.service.get.lot.GetActiveLotsService
import com.procurement.requisition.application.service.get.tender.owner.GetTenderOwnerService
import com.procurement.requisition.application.service.get.tender.state.GetTenderStateService
import com.procurement.requisition.application.service.relation.CreateRelationService
import com.procurement.requisition.application.service.set.SetLotsStatusUnsuccessfulService
import com.procurement.requisition.application.service.set.SetTenderStatusDetailsService
import com.procurement.requisition.application.service.set.SetTenderStatusUnsuccessfulService
import com.procurement.requisition.application.service.validate.CheckLotsStateService
import com.procurement.requisition.application.service.validate.CheckTenderStateService
import com.procurement.requisition.application.service.validate.ValidatePCRService
import com.procurement.requisition.infrastructure.handler.Handler
import com.procurement.requisition.infrastructure.handler.HandlerDescription
import com.procurement.requisition.infrastructure.handler.v1.HandlersV1
import com.procurement.requisition.infrastructure.handler.v1.create.request.CreateRequestsForEvPanelsHandler
import com.procurement.requisition.infrastructure.handler.v1.get.lot.GetActiveLotsHandler
import com.procurement.requisition.infrastructure.handler.v1.get.lot.tender.owner.GetTenderOwnerHandler
import com.procurement.requisition.infrastructure.handler.v1.set.SetLotsStatusUnsuccessfulHandler
import com.procurement.requisition.infrastructure.handler.v1.set.SetTenderStatusDetailsHandler
import com.procurement.requisition.infrastructure.handler.v1.set.SetTenderStatusUnsuccessfulHandler
import com.procurement.requisition.infrastructure.handler.v2.HandlersV2
import com.procurement.requisition.infrastructure.handler.v2.pcr.create.CreatePCRHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.query.find.item.FindItemsByLotIdsHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.query.get.state.GetTenderStateHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.relation.CreateRelationHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.validate.CheckLotsStateHandler
import com.procurement.requisition.infrastructure.handler.v2.pcr.validate.CheckTenderStateHandler
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
    val checkLotsStateService: CheckLotsStateService,
    val checkTenderStateService: CheckTenderStateService,
    val findItemsByLotIdsService: FindItemsByLotIdsService,
    val createPCRService: CreatePCRService,
    val createRelationService: CreateRelationService,
    val createRequestsForEvPanelsService: CreateRequestsForEvPanelsService,
    val getActiveLotsService: GetActiveLotsService,
    val getTenderOwnerService: GetTenderOwnerService,
    val getTenderStateService: GetTenderStateService,
    val setLotsStatusUnsuccessfulService: SetLotsStatusUnsuccessfulService,
    val setTenderStatusDetailsService: SetTenderStatusDetailsService,
    val setTenderStatusUnsuccessfulService: SetTenderStatusUnsuccessfulService,
    val validatePCRService: ValidatePCRService,
) {

    @Bean
    fun handlersV1() = HandlersV1(
        listOf(
            HandlerDescription(CommandsV1.CommandType.CREATE_REQUESTS_FOR_EV_PANELS, createRequestsForEvPanelsHandler()),
            HandlerDescription(CommandsV1.CommandType.GET_ACTIVE_LOTS, getActiveLotsHandler()),
            HandlerDescription(CommandsV1.CommandType.GET_TENDER_OWNER, getTenderOwnerHandler()),
            HandlerDescription(CommandsV1.CommandType.SET_LOTS_STATUS_UNSUCCESSFUL, setLotsStatusUnsuccessfulHandler()),
            HandlerDescription(CommandsV1.CommandType.SET_TENDER_STATUS_DETAILS, setTenderStatusDetailsHandler()),
            HandlerDescription(CommandsV1.CommandType.SET_TENDER_STATUS_UNSUCCESSFUL, setTenderStatusUnsuccessfulHandler()),
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
    fun getTenderOwnerHandler(): Handler =
        GetTenderOwnerHandler(logger = logger, transform = transform, getTenderOwnerService = getTenderOwnerService)

    @Bean
    fun setLotsStatusUnsuccessfulHandler(): Handler =
        SetLotsStatusUnsuccessfulHandler(logger = logger, transform = transform, setLotsStatusUnsuccessfulService = setLotsStatusUnsuccessfulService)

    @Bean
    fun setTenderStatusDetailsHandler(): Handler =
        SetTenderStatusDetailsHandler(logger = logger, transform = transform, setTenderStatusDetailsService = setTenderStatusDetailsService)

    @Bean
    fun setTenderStatusUnsuccessfulHandler(): Handler =
        SetTenderStatusUnsuccessfulHandler(logger = logger, transform = transform, setTenderStatusUnsuccessfulService = setTenderStatusUnsuccessfulService)

    @Bean
    fun handlersV2() = HandlersV2(
        listOf(
            HandlerDescription(CommandsV2.CommandType.CHECK_LOTS_STATE, checkTenderStateHandler()),
            HandlerDescription(CommandsV2.CommandType.FIND_ITEMS_BY_LOT_IDS, findItemsByLotIds()),
            HandlerDescription(CommandsV2.CommandType.CHECK_TENDER_STATE, checkTenderStateHandler()),
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
    fun checkLotsStateHandler(): Handler =
        CheckLotsStateHandler(logger = logger, transform = transform, checkLotsStateService = checkLotsStateService)

    @Bean
    fun checkTenderStateHandler(): Handler =
        CheckTenderStateHandler(logger = logger, transform = transform, checkTenderStateService = checkTenderStateService)

    @Bean
    fun findItemsByLotIds(): Handler =
        FindItemsByLotIdsHandler(logger = logger, transform = transform, findItemsByLotIdsService = findItemsByLotIdsService)

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
