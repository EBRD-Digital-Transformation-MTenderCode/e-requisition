package com.procurement.requisition.infrastructure.handler.v1

import com.procurement.requisition.application.extension.trySerialization
import com.procurement.requisition.application.service.Logger
import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.application.service.get.lot.auction.GetLotsAuctionService
import com.procurement.requisition.application.service.get.lot.auction.model.GetLotsAuctionCommand
import com.procurement.requisition.domain.failure.incident.InternalServerError
import com.procurement.requisition.domain.model.ProcurementMethodDetails
import com.procurement.requisition.infrastructure.api.Action
import com.procurement.requisition.infrastructure.handler.base.CommandHandler
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.model.CommandDescriptor
import com.procurement.requisition.infrastructure.api.v1.ApiResponseV1
import com.procurement.requisition.infrastructure.handler.v1.base.AbstractHandlerV1
import com.procurement.requisition.infrastructure.handler.v1.converter.convert
import com.procurement.requisition.infrastructure.handler.Actions
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

@CommandHandler
class GetLotsAuctionHandler(
    override val logger: Logger,
    override val transform: Transform,
    private val getLotsAuctionService: GetLotsAuctionService
) : AbstractHandlerV1() {

    override val action: Action = Actions.GET_LOTS_AUCTION

    companion object {
        val allowedPmd = ProcurementMethodDetails.allowedElements
            .filter {
                when (it) {
                    ProcurementMethodDetails.DCO, ProcurementMethodDetails.TEST_DCO,
                    ProcurementMethodDetails.GPA, ProcurementMethodDetails.TEST_GPA,
                    ProcurementMethodDetails.MC, ProcurementMethodDetails.TEST_MC,
                    ProcurementMethodDetails.MV, ProcurementMethodDetails.TEST_MV,
                    ProcurementMethodDetails.OT, ProcurementMethodDetails.TEST_OT,
                    ProcurementMethodDetails.RFQ, ProcurementMethodDetails.TEST_RFQ,
                    ProcurementMethodDetails.RT, ProcurementMethodDetails.TEST_RT,
                    ProcurementMethodDetails.SV, ProcurementMethodDetails.TEST_SV -> true

                    ProcurementMethodDetails.CD, ProcurementMethodDetails.TEST_CD,
                    ProcurementMethodDetails.CF, ProcurementMethodDetails.TEST_CF,
                    ProcurementMethodDetails.DA, ProcurementMethodDetails.TEST_DA,
                    ProcurementMethodDetails.DC, ProcurementMethodDetails.TEST_DC,
                    ProcurementMethodDetails.FA, ProcurementMethodDetails.TEST_FA,
                    ProcurementMethodDetails.IP, ProcurementMethodDetails.TEST_IP,
                    ProcurementMethodDetails.NP, ProcurementMethodDetails.TEST_NP,
                    ProcurementMethodDetails.OF, ProcurementMethodDetails.TEST_OF,
                    ProcurementMethodDetails.OP, ProcurementMethodDetails.TEST_OP -> false
                }
            }
            .toSet()
    }

    override fun execute(descriptor: CommandDescriptor): Result<String, Failure> {

        val context = getContext(descriptor.body.asJsonNode)
            .onFailure { failure -> return failure }

        val cpid = context.cpid
            .onFailure { return it }

        val ocid = context.ocid
            .onFailure { return it }

        context.pmd
            .map { it.asEnum(ProcurementMethodDetails, allowedPmd) }
            .onFailure { return it }

        val command = GetLotsAuctionCommand(cpid = cpid, ocid = ocid)

        return getLotsAuctionService.get(command)
            .flatMap { result ->
                ApiResponseV1.Success(version = descriptor.version, id = descriptor.id, result = result.convert())
                    .trySerialization(transform)
                    .mapFailure { failure ->
                        InternalServerError(description = failure.description, reason = failure.reason)
                    }
            }
    }
}
