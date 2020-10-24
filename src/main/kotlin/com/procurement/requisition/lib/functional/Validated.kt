package com.procurement.requisition.lib.functional

fun <E> E.asValidationFailure(): Validated<E> = Validated.error(this)

sealed class Validated<out E> {

    companion object {
        fun <E> ok(): Validated<E> = Success
        fun <E> error(value: E): Validated<E> = Error(value)
    }

    abstract val isOk: Boolean
    abstract val isError: Boolean

    inline fun onFailure(f: (Error<@UnsafeVariance E>) -> Nothing): Unit = when (this) {
        is Success -> Unit
        is Error -> f(this)
    }

    fun <R> map(transform: (E) -> R): Validated<R> = when (this) {
        is Success -> this
        is Error -> Error(transform(reason))
    }

    fun <R> flatMap(transform: (E) -> Validated<R>): Validated<R> = when (this) {
        is Success -> this
        is Error -> transform(this.reason)
    }

    object Success : Validated<Nothing>() {
        override val isOk: Boolean = true
        override val isError: Boolean = !isOk
    }

    class Error<E>(val reason: E) : Validated<E>() {
        override val isOk: Boolean = false
        override val isError: Boolean = !isOk
    }
}
