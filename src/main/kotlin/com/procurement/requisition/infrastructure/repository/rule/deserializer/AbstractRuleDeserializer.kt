package com.procurement.requisition.infrastructure.repository.rule.deserializer

import com.procurement.requisition.application.service.Transform
import com.procurement.requisition.domain.failure.incident.DatabaseIncident
import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result
import com.procurement.requisition.lib.functional.flatMap

inline fun <reified T> String.deserialization(transform: Transform): Result<T, Failure> =
    transform.tryDeserialization(this, T::class.java)
        .mapFailure { failure ->
            DatabaseIncident.Data(
                description = failure.description + " Json: '$this'.",
                reason = failure.reason
            )
        }

inline fun <reified T> Result<String, Failure>.deserialization(transform: Transform): Result<T, Failure> =
    flatMap { json ->
        json.deserialization(transform)
    }

fun <T, R> Result<T, Failure>.converting(converter: T.() -> Result<R, Failure>): Result<R, Failure> =
    this.flatMap { entity ->
        entity.converter()
            .mapFailure { failure ->
                DatabaseIncident.Data(description = failure.description, reason = failure.reason)
            }
    }

