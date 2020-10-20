package com.procurement.requisition.domain.failure.error

import com.procurement.requisition.lib.fail.Failure

sealed class TransformErrors(number: String, reason: Exception?) :
    Failure.Error(reason = reason) {

    override val code: String = "TE-$number"

    class Parsing(val value: String, reason: Exception) :
        TransformErrors(number = "1", reason = reason) {

        override val description: String
            get() = "Error parsing $value."
    }

    class Mapping(override val description: String, reason: Exception?) :
        TransformErrors(number = "2", reason = reason)

    class Deserialization(override val description: String, reason: Exception) :
        TransformErrors(number = "3", reason = reason)

    class Serialization(override val description: String, reason: Exception) :
        TransformErrors(number = "4", reason = reason)
}
