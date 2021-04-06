package com.procurement.requisition.application.service.error

import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.lib.fail.Failure

sealed class CheckItemsDataForRfqErrors(
    override val code: String,
    override val description: String
) : Failure.Error() {

    override val reason: Exception? = null

    class PCRNotFound(cpid: Cpid, ocid: Ocid) :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.1",
            description = "PCR by cpid '${cpid.underlying}' and ocid '${ocid.underlying}' is not found."
        )

    class LotNotFound(lotId: LotId) :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.2",
            description = "Lot '$lotId' is not found."
        )

    class InvalidRelatedLot(itemId: ItemId, relatedLot: LotId) :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.3",
            description = "Cannot found related lot '$relatedLot' specified in item '$itemId'."
        )

    class ClassificationMismatch(itemId: ItemId, relatedLot: LotId) :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.4",
            description = "Classification in item '$itemId' mismatch with classification into related lot '${relatedLot}'."
        )

    class QuantityMismatch(receivedItem: ItemId, storedItem: ItemId) :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.5",
            description = "Quantity in received item ('$receivedItem') mismatch with quantity in stored item ('$storedItem')."
        )

    class UnitMismatch(receivedItem: ItemId, storedItem: ItemId) :
        CheckItemsDataForRfqErrors(
            code = "VR.COM-17.18.6",
            description = "Unit in received item ('$receivedItem') mismatch with quantity in stored item ('$storedItem')."
        )

}
