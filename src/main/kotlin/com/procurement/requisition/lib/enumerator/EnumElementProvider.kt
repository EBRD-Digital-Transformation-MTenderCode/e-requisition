package com.procurement.requisition.lib.enumerator

abstract class EnumElementProvider<T>(val info: EnumInfo<T>) where T : Enum<T>,
                                                                   T : EnumElementProvider.Element {

    interface Element {
        val key: String
        val isNeutralElement: Boolean
            get() = false
        val deprecated: Boolean
            get() = false
    }

    class EnumInfo<T>(
        val target: Class<T>,
        val values: Array<T>
    )

    companion object {
        inline fun <reified T : Enum<T>> info() = EnumInfo(target = T::class.java, values = enumValues())

        fun <T> Collection<T>.keysAsStrings(): List<String> where T : Enum<T>,
                                                                  T : Element = this
            .map { element -> element.key + if (element.deprecated) " (Deprecated)" else "" }
    }

    val allowedElements: Set<T> = info.values.toSet()

    private val elements: Map<String, T> = info.values.associateBy { it.key.toUpperCase() }

    fun orNull(key: String): T? = elements[key.toUpperCase()]

    fun orThrow(key: String): T = orNull(key)
        ?: throw EnumElementProviderException(
            enumType = info.target.canonicalName,
            value = key,
            values = allowedElements.joinToString { it.key }
        )

    operator fun contains(key: String): Boolean = orNull(key) != null
}

class EnumElementProviderException(enumType: String, value: String, values: String) :
    RuntimeException("Unknown value for enumType $enumType: $value, Allowed values are $values") {

    val code: String = "00.00"
}
