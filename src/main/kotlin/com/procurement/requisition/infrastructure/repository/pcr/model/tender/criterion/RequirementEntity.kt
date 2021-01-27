package com.procurement.requisition.infrastructure.repository.pcr.model.tender.criterion

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.requisition.domain.extension.asString
import com.procurement.requisition.domain.failure.error.JsonErrors
import com.procurement.requisition.domain.failure.error.repath
import com.procurement.requisition.domain.model.DynamicValue
import com.procurement.requisition.domain.model.document.DocumentReference
import com.procurement.requisition.domain.model.requirement.EligibleEvidence
import com.procurement.requisition.domain.model.requirement.EligibleEvidenceType
import com.procurement.requisition.domain.model.requirement.ExpectedValue
import com.procurement.requisition.domain.model.requirement.MaxValue
import com.procurement.requisition.domain.model.requirement.MinValue
import com.procurement.requisition.domain.model.requirement.Requirement
import com.procurement.requisition.domain.model.requirement.RequirementStatus
import com.procurement.requisition.infrastructure.handler.converter.asDocumentId
import com.procurement.requisition.infrastructure.handler.converter.asEnum
import com.procurement.requisition.infrastructure.handler.converter.asLocalDateTime
import com.procurement.requisition.infrastructure.handler.converter.asString
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.asSuccess
import com.procurement.requisition.lib.mapOrEmpty

data class RequirementEntity(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period? = null,

    @field:JsonProperty("dataType") @param:JsonProperty("dataType") val dataType: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("expectedValue") @param:JsonProperty("expectedValue") val expectedValue: DynamicValue?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("minValue") @param:JsonProperty("minValue") val minValue: DynamicValue?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("maxValue") @param:JsonProperty("maxValue") val maxValue: DynamicValue?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("eligibleEvidences") @param:JsonProperty("eligibleEvidences") val eligibleEvidences: List<EligibleEvidence>?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("datePublished") @param:JsonProperty("datePublished") val datePublished: String?

) {

    data class EligibleEvidence(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
        @field:JsonProperty("type") @param:JsonProperty("type") val type: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatedDocument") @param:JsonProperty("relatedDocument") val relatedDocument: DocumentReference?
    ) {
        data class DocumentReference(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String
        )
    }

    data class Period(
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: String,
        @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: String
    )
}

fun Requirement.serialization() = RequirementEntity(
    id = id,
    title = title,
    description = description,
    period = period?.let {
        RequirementEntity.Period(startDate = it.startDate.asString(), endDate = it.endDate.asString())
    },
    dataType = dataType.asString(),
    expectedValue = expectedValue?.value,
    minValue = minValue?.value,
    maxValue = maxValue?.value,
    eligibleEvidences = eligibleEvidences.map { it.serialization() },
    status = status?.asString(),
    datePublished = datePublished?.asString()
)

fun EligibleEvidence.serialization() =
    RequirementEntity.EligibleEvidence(
        id = this.id,
        title = this.title,
        type = this.type.asString(),
        description = this.description,
        relatedDocument = this.relatedDocument?.serialization()
    )

fun DocumentReference.serialization() =
    RequirementEntity.EligibleEvidence.DocumentReference(
        id = this.id.underlying
    )


fun RequirementEntity.deserialization(): Result<Requirement, JsonErrors> {
    val period = period?.deserialization()?.onFailure { return it.repath(path = "/period") }

    val dataType = dataType.asEnum(target = DynamicValue.DataType)
        .onFailure { return it.repath(path = "/dataType") }

    val eligibleEvidences = eligibleEvidences.mapOrEmpty {
        it.deserialization().onFailure { return it.repath(path = "/eligibleEvidences") }
    }

    val status = status?.asEnum(target = RequirementStatus)
        ?.onFailure { return it.repath(path = "/status") }

    val datePublished = datePublished?.asLocalDateTime()
        ?.onFailure { return it.repath(path = "/status") }

    return Requirement(
        id = id,
        title = title,
        description = description,
        period = period,
        dataType = dataType,
        expectedValue = expectedValue?.let { ExpectedValue(it) },
        minValue = minValue?.let { MinValue(it) },
        maxValue = maxValue?.let { MaxValue(it) },
        eligibleEvidences = eligibleEvidences,
        status = status,
        datePublished = datePublished
    ).asSuccess()
}

fun RequirementEntity.Period.deserialization(): Result<Requirement.Period, JsonErrors> {
    val startDate = startDate.asLocalDateTime().onFailure { return it.repath(path = "/startDate") }
    val endDate = endDate.asLocalDateTime().onFailure { return it.repath(path = "/endDate") }
    return Requirement.Period(startDate = startDate, endDate = endDate).asSuccess()
}

fun RequirementEntity.EligibleEvidence.deserialization(): Result<EligibleEvidence, JsonErrors> {

    val type = type.asEnum(target = EligibleEvidenceType)
        .onFailure { return it.repath(path = "/type") }

    val relatedDocument = relatedDocument?.deserialization()
        ?.onFailure { return it.repath(path = "/relatedDocument") }

    return EligibleEvidence(
        id = id,
        title = title,
        type = type,
        description = description,
        relatedDocument = relatedDocument
    ).asSuccess()

}

fun RequirementEntity.EligibleEvidence.DocumentReference.deserialization(): Result<DocumentReference, JsonErrors> {
    val id = id.asDocumentId().onFailure { return it.repath(path = "/id") }

    return DocumentReference(id = id).asSuccess()
}
