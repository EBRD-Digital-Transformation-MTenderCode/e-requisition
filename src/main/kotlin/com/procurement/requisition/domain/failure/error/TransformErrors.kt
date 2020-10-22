package com.procurement.requisition.domain.failure.error

import com.procurement.requisition.lib.fail.Failure

sealed class TransformErrors(number: String) : Failure.Error() {

    override val code: String = "TE-$number"

    class Parsing(val value: String, override val reason: Exception) :
        TransformErrors(number = "1") {

        override val description: String
            get() = "Error parsing $value."
    }

    class Mapping(override val description: String, override val reason: Exception?) :
        TransformErrors(number = "2")

    class Deserialization(override val description: String, override val reason: Exception) :
        TransformErrors(number = "3")

    class Serialization(override val description: String, override val reason: Exception) :
        TransformErrors(number = "4")
}
