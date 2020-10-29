package com.procurement.requisition.infrastructure.handler.converter

import com.procurement.requisition.domain.extension.format
import com.procurement.requisition.domain.extension.tryParseLocalDateTime
import com.procurement.requisition.domain.failure.error.DataTimeParseError
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
    path: String,
    allowedElements: Set<T> = target.allowedElements
): Result<T, JsonErrors.UnknownValue> where T : Enum<T>,
                                            T : EnumElementProvider.Element = target.orNull(this)
    ?.takeIf { it in allowedElements }
    ?.asSuccess()
    ?: failure(
        JsonErrors.UnknownValue(
            path = path,
            expectedValues = allowedElements.keysAsStrings(),
            actualValue = this,
            reason = null
        )
    )

fun <T> T.asStringOrNull(): String? where T : Enum<T>,
                                          T : EnumElementProvider.Element = takeIf { !it.isNeutralElement }?.key

fun <T> T.asString(): String where T : Enum<T>,
                                   T : EnumElementProvider.Element = key

fun String.asLocalDateTime(path: String): Result<LocalDateTime, JsonErrors> = tryParseLocalDateTime()
    .mapFailure { failure ->
        when (failure) {
            is DataTimeParseError.InvalidFormat -> JsonErrors.DataFormatMismatch(
                path = path,
                actualValue = failure.value,
                expectedFormat = failure.pattern,
                reason = failure.reason
            )

            is DataTimeParseError.InvalidDateTime -> JsonErrors.DateTimeInvalid(
                path = path,
                value = failure.value,
                reason = failure.reason
            )
        }
    }

fun LocalDateTime.asString() = format()

fun String.asCpid(path: String): Result<Cpid, JsonErrors> = Cpid.tryCreateOrNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = Cpid.pattern)
    )

fun String.asToken(path: String): Result<Token, JsonErrors> = Token.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = Token.pattern)
    )

fun String.asSingleStageOcid(path: String): Result<Ocid, JsonErrors> = Ocid.SingleStage.tryCreateOrNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = Ocid.SingleStage.pattern)
    )

fun String.asTenderId(path: String): Result<TenderId, JsonErrors> = TenderId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = TenderId.pattern)
    )

fun String.asLotId(path: String): Result<LotId, JsonErrors> = LotId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = LotId.pattern)
    )

fun String.asItemId(path: String): Result<ItemId, JsonErrors> = ItemId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = ItemId.pattern)
    )

fun String.asTargetId(path: String): Result<TargetId, JsonErrors> = TargetId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = TargetId.pattern)
    )

fun String.asObservationId(path: String): Result<ObservationId, JsonErrors> = ObservationId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = ObservationId.pattern)
    )

fun String.asCriterionId(path: String): Result<CriterionId, JsonErrors> = CriterionId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = CriterionId.pattern)
    )

fun String.asRequirementGroupId(path: String): Result<RequirementGroupId, JsonErrors> = RequirementGroupId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = RequirementGroupId.pattern)
    )

fun String.asRequirementResponseId(path: String): Result<RequirementResponseId, JsonErrors> =
    RequirementResponseId.orNull(this)
        ?.asSuccess()
        ?: failure(
            JsonErrors.DataFormatMismatch(
                path = path,
                actualValue = this,
                expectedFormat = RequirementResponseId.pattern
            )
        )

fun String.asConversionId(path: String): Result<ConversionId, JsonErrors> = ConversionId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = ConversionId.pattern)
    )

fun String.asCoefficientId(path: String): Result<CoefficientId, JsonErrors> = CoefficientId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = CoefficientId.pattern)
    )

fun String.asDocumentId(path: String): Result<DocumentId, JsonErrors> = DocumentId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = DocumentId.pattern)
    )

fun String.asRelatedProcessId(path: String): Result<RelatedProcessId, JsonErrors> = RelatedProcessId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = RelatedProcessId.pattern)
    )

fun String.asBidId(path: String): Result<BidId, JsonErrors> = BidId.orNull(this)
    ?.asSuccess()
    ?: failure(
        JsonErrors.DataFormatMismatch(path = path, actualValue = this, expectedFormat = BidId.pattern)
    )
