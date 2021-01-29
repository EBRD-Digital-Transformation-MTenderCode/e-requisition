package com.procurement.requisition.lib.enumerator

abstract class EnumSubsetElementProvider<T, R>(val info: EnumInfo<T>) where R : Enum<R>,
                                                                            R : EnumElementProvider.Element,
                                                                            T : Enum<T>,
                                                                            T : EnumSubsetElementProvider.Element<R> {

    @Target(AnnotationTarget.PROPERTY)
    annotation class DeprecatedElement

    @Target(AnnotationTarget.PROPERTY)
    annotation class ExcludedElement

    interface Element<R> where R : Enum<R>, R : EnumElementProvider.Element {
        val base: R
        val isNeutralElement: Boolean
            get() = false
    }

    class EnumInfo<T>(
        val target: Class<T>,
        val values: Array<T>
    )

    companion object {
        inline fun <reified T : Enum<T>> info() = EnumInfo(target = T::class.java, values = enumValues())

        fun <T, R> Collection<T>.keysAsStrings(): List<String> where R : Enum<R>,
                                                                     R : EnumElementProvider.Element,
                                                                     T : Enum<T>,
                                                                     T : Element<R> = this
            .map { element -> element.base.key + if (element.base.isDeprecated()) " (Deprecated)" else "" }

        private fun <E : Enum<E>> Enum<E>.isNotExcluded(): Boolean = this.findAnnotation<ExcludedElement, E>() == null
        private fun <E : Enum<E>> Enum<E>.isDeprecated(): Boolean = this.findAnnotation<DeprecatedElement, E>() != null
        private inline fun <reified A : Annotation, E : Enum<E>> Enum<E>.findAnnotation(): A? = this::class.java
            .declaredFields
            .find { field -> field.name == this.name }
            ?.getAnnotation(A::class.java)
    }

    val allowedElements: Set<T> = info.values.filter { element -> element.base.isNotExcluded() }.toSet()

    private val elements: Map<String, T> = info.values.associateBy { it.base.key.toUpperCase() }

    fun orNull(key: String): T? = elements[key.toUpperCase()]

    fun orThrow(key: String): T = orNull(key)
        ?: throw EnumElementProviderException(
            enumType = info.target.canonicalName,
            value = key,
            values = allowedElements.joinToString { it.base.key }
        )

    operator fun contains(key: String): Boolean = orNull(key) != null
}

