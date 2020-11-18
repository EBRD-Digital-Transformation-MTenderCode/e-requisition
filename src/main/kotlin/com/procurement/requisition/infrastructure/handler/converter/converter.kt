package com.procurement.requisition.infrastructure.handler.converter

import com.procurement.requisition.domain.extension.format
import com.procurement.requisition.domain.extension.tryParseLocalDateTime
import com.procurement.requisition.domain.failure.error.DataTimeError
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.model.Cpid
import com.procurement.requisition.domain.model.Ocid
import com.procurement.requisition.domain.model.Token
import com.procurement.requisition.domain.model.bid.BidId
import com.procurement.requisition.domain.model.document.DocumentId
import com.procurement.requisition.domain.model.relatedprocesses.RelatedProcessId
import com.procurement.requisition.domain.model.requirement.RequirementGroupId
import com.procurement.requisition.domain.model.requirement.response.RequirementResponseId
import com.procurement.requisition.domain.model.tender.TenderId
import com.procurement.requisition.domain.model.tender.conversion.ConversionId
import com.procurement.requisition.domain.model.tender.conversion.coefficient.CoefficientId
import com.procurement.requisition.domain.model.tender.criterion.CriterionId
import com.procurement.requisition.domain.model.tender.item.ItemId
import com.procurement.requisition.domain.model.tender.lot.LotId
import com.procurement.requisition.domain.model.tender.target.TargetId
import com.procurement.requisition.domain.model.tender.target.observation.ObservationId
import com.procurement.requisition.lib.enumerator.EnumElementProvider
import com.procurement.requisition.lib.enumerator.EnumElementProvider.Companion.keysAsStrings
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.Result.Companion.failure
import com.procurement.requisition.lib.functional.asSuccess
import java.time.LocalDateTime

fun <T> String.asEnum(
    target: EnumElementProvider<T>,
    allowedElements: Set<T> = target.allowedElements
): Result<T, JsonErrors.UnknownValue> where T : Enum<T>,
                                            T : EnumElementProvider.Element = target.orNull(this)
    ?.takeIf { it in allowedElements }
    ?.asSuccess()
    ?: failure(JsonErrors.UnknownValue(expectedValues = allowedElements.keysAsStrings(), actualValue = this))

fun <T> T.asStringOrNull(): String? where T : Enum<T>,
                                          T : EnumElementProvider.Element = takeIf { !it.isNeutralElement }?.key

fun <T> T.asString(): String where T : Enum<T>,
                                   T : EnumElementProvider.Element = key

fun String.asLocalDateTime(): Result<LocalDateTime, JsonErrors> = tryParseLocalDateTime()
    .mapFailure { failure ->
        when (failure) {
            is DataTimeError.InvalidFormat ->
                JsonErrors.DataFormatMismatch(
                    actualValue = failure.value,
                    expectedFormat = failure.pattern,
                    reason = failure.reason
                )

            is DataTimeError.InvalidDateTime ->
                JsonErrors.DateTimeInvalid(value = failure.value, reason = failure.reason)
        }
    }

fun LocalDateTime.asString() = format()

fun String.asCpid(): Result<Cpid, JsonErrors> = Cpid.tryCreateOrNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = Cpid.pattern))

fun String.asToken(): Result<Token, JsonErrors> = Token.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = Token.pattern))

fun String.asSingleStageOcid(): Result<Ocid, JsonErrors> = Ocid.SingleStage.tryCreateOrNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = Ocid.SingleStage.pattern))

fun String.asTenderId(): Result<TenderId, JsonErrors> = TenderId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = TenderId.pattern))

fun String.asLotId(): Result<LotId, JsonErrors> = LotId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = LotId.pattern))

fun String.asItemId(): Result<ItemId, JsonErrors> = ItemId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = ItemId.pattern))

fun String.asTargetId(): Result<TargetId, JsonErrors> = TargetId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = TargetId.pattern))

fun String.asObservationId(): Result<ObservationId, JsonErrors> = ObservationId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = ObservationId.pattern))

fun String.asCriterionId(): Result<CriterionId, JsonErrors> = CriterionId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = CriterionId.pattern))

fun String.asRequirementGroupId(): Result<RequirementGroupId, JsonErrors> = RequirementGroupId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = RequirementGroupId.pattern))

fun String.asRequirementResponseId(): Result<RequirementResponseId, JsonErrors> =
    RequirementResponseId.orNull(this)
        ?.asSuccess()
        ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = RequirementResponseId.pattern))

fun String.asConversionId(): Result<ConversionId, JsonErrors> = ConversionId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = ConversionId.pattern))

fun String.asCoefficientId(): Result<CoefficientId, JsonErrors> = CoefficientId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = CoefficientId.pattern))

fun String.asDocumentId(): Result<DocumentId, JsonErrors> = DocumentId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = DocumentId.pattern))

fun String.asRelatedProcessId(): Result<RelatedProcessId, JsonErrors> = RelatedProcessId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = RelatedProcessId.pattern))

fun String.asBidId(): Result<BidId, JsonErrors> = BidId.orNull(this)
    ?.asSuccess()
    ?: failure(JsonErrors.DataFormatMismatch(actualValue = this, expectedFormat = BidId.pattern))
