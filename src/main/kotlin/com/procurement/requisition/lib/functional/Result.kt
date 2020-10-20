package com.procurement.requisition.lib.functional

// Using Exception
/*fun <T> T.asSuccess(): Result<T> = Result.success(this)
fun <T, E : Exception> E.asFailure(): Result<T> = Result.failure(this)

sealed class Result<out T> {

    companion object {
        fun <T> success(value: T): Result<T> = Success(value)
        fun <T> failure(fail: Exception): Result<T> = Failure(fail)
    }

    abstract val isSuccess: Boolean
    abstract val isFailure: Boolean

//    inline fun doOnError(block: (Exception) -> Unit): Result<T> = when (this) {
//        is Success -> this
//        is Failure -> {
//            block(exception)
//            this
//        }
//    }

//    inline fun onFailure(f: (Exception) -> Nothing): T = when (this) {
//        is Success -> this.value
//        is Failure -> f(this.exception)
//    }

    inline fun onFailure(f: (Failure) -> Nothing): T = when (this) {
        is Success -> this.value
        is Failure -> f(this)
    }

    val orNull: T?
        get() = when (this) {
            is Success -> value
            is Failure -> null
        }

    val asOption: Option<T>
        get() = when (this) {
            is Success -> Option.pure(value)
            is Failure -> Option.none()
        }

    infix fun <R : Exception> orThrow(builder: (Exception) -> R): T = when (this) {
        is Success -> value
        is Failure -> throw builder(reason)
    }

    infix fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue
    }

    infix fun orElse(defaultValue: () -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue()
    }

    infix fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    infix fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    infix fun <R : Exception> mapFailure(transform: (Exception) -> R): Result<T> = when (this) {
        is Success -> this
        is Failure -> Failure(transform(reason))
    }

    class Success<out T> internal constructor(val value: T) : Result<T>() {
        override val isSuccess: Boolean = true
        override val isFailure: Boolean = false

        override fun toString(): String = value.toString()
    }

    class Failure internal constructor(val reason: Exception) : Result<Nothing>() {
        override val isSuccess: Boolean = false
        override val isFailure: Boolean = true

        override fun toString(): String = reason.toString()
    }
}*/

// Failure
/*fun <T> T.asSuccess(): Result<T> = Result.success(this)
fun <T, E : com.procurement.requisition.lib.fail.Failure> E.asFailure(): Result<T> = Result.failure(this)

sealed class Result<out T> {

    companion object {
        fun <T> success(value: T): Result<T> = Success(value)
        fun <T> failure(fail: com.procurement.requisition.lib.fail.Failure): Result<T> = Failure(fail)
    }

    abstract val isSuccess: Boolean
    abstract val isFailure: Boolean

    inline fun onFailure(f: (Failure) -> Nothing): T = when (this) {
        is Success -> this.value
        is Failure -> f(this)
    }

    val orNull: T?
        get() = when (this) {
            is Success -> value
            is Failure -> null
        }

    val asOption: Option<T>
        get() = when (this) {
            is Success -> Option.pure(value)
            is Failure -> Option.none()
        }

    infix fun <R : com.procurement.requisition.lib.fail.Failure> orThrow(block: (com.procurement.requisition.lib.fail.Failure) -> R): T =
        when (this) {
            is Success -> value
            is Failure -> throw block(reason)
        }

    infix fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue
    }

    infix fun orElse(defaultValue: () -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue()
    }

    infix fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    infix fun <R> flatMap(transform: (T) -> Result<R>): Result<R> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    infix fun <R : com.procurement.requisition.lib.fail.Failure> mapFailure(transform: (com.procurement.requisition.lib.fail.Failure) -> R): Result<T> =
        when (this) {
            is Success -> this
            is Failure -> Failure(transform(reason))
        }

    class Success<out T> internal constructor(val value: T) : Result<T>() {
        override val isSuccess: Boolean = true
        override val isFailure: Boolean = false

        override fun toString(): String = value.toString()
    }

    class Failure internal constructor(val reason: com.procurement.requisition.lib.fail.Failure) : Result<Nothing>() {
        override val isSuccess: Boolean = false
        override val isFailure: Boolean = true

        override fun toString(): String = reason.toString()
    }
}

//infix fun <T, E : com.procurement.requisition.lib.fail.Failure> T.validate(rule: ValidationRule<T, E>): Result<T> = when (val result = rule.test(this)) {
//    is ValidationResult.Ok -> Result.success(this)
//    is ValidationResult.Fail -> Result.failure(result.error)
//}
//
//infix fun <T, E : Failure> Result<T>.validate(rule: ValidationRule<T, E>): Result<T> = when (this) {
//    is Result.Success -> {
//        val result = rule.test(value)
//        if (result.isError) Result.failure(
//            result.error
//        ) else Result.success(value)
//    }
//    is Result.Failure -> this
//}*/

// Using Type + Failure last
/*
fun <T, E : Failure> T.asSuccess(): Result<T, E> = Result.success(this)
fun <T, E : Failure> E.asFailure(): Result<T, E> = Result.failure(this)

sealed class Result<out T, out E : Failure> {

    companion object {
        fun <T, E : Failure> success(value: T): Result<T, E> = Success(value)
        fun <T, E : Failure> failure(fail: E): Result<T, E> = Fail(fail)
    }

    abstract val isSuccess: Boolean
    abstract val isFailure: Boolean

    inline fun doOnError(block: (E) -> Unit): Result<T, E> = when (this) {
        is Success<T> -> this
        is Fail<E> -> {
            block(reason)
            this
        }
    }

    inline fun onFailure(f: (E) -> Nothing): T = when (this) {
        is Success<T> -> value
        is Fail<E> -> f(reason)
    }

    val asOption: Option<T>
        get() = when (this) {
            is Success -> Option.pure(value)
            is Fail -> Option.none()
        }

    val orNull: T?
        get() = when (this) {
            is Success -> value
            is Fail -> null
        }

    infix fun orThrow(block: (E) -> Nothing): T = when (this) {
        is Success -> value
        is Fail -> throw block(reason)
    }

    infix fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Fail -> defaultValue
    }

    infix fun orElse(defaultValue: () -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Fail -> defaultValue()
    }

    infix fun <R> map(transform: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success(transform(value))
        is Fail -> this
    }

    infix fun <R> flatMap(transform: (T) -> Result<R, @UnsafeVariance E>): Result<R, E> = when (this) {
        is Success -> transform(value)
        is Fail -> this
    }

    infix fun <R : Failure> mapFailure(transform: (E) -> R): Result<T, R> = when (this) {
        is Success -> this
        is Fail -> Fail(transform(reason))
    }

    class Success<out T> internal constructor(val value: T) : Result<T, Nothing>() {
        override val isSuccess: Boolean = true
        override val isFailure: Boolean = false

        override fun toString(): String = "Success($value)"
    }

    class Fail<out E : Failure> internal constructor(val reason: E) : Result<Nothing, E>() {
        override val isSuccess: Boolean = false
        override val isFailure: Boolean = true

        override fun toString(): String = "Failure($reason)"
    }
}

//infix fun <T, E : Failure> T.validate(rule: ValidationRule<T, E>): Result<T, E> = when (val result = rule.test(this)) {
//    is ValidationResult.Ok -> Result.success(this)
//    is ValidationResult.Fail -> Result.failure(result.error)
//}
//
//infix fun <T, E : Failure> Result<T, E>.validate(rule: ValidationRule<T, E>): Result<T, E> = when (this) {
//    is Result.Success -> {
//        val result = rule.test(value)
//        if (result.isError)
//            Result.failure(result.error)
//        else
//            Result.success(value)
//    }
//    is Result.Fail -> this
//}
*/


fun <T, E> T.asSuccess(): Result<T, E> = Result.success(this)
fun <T, E> E.asFailure(): Result<T, E> = Result.failure(this)

sealed class Result<out T, out E> {

    companion object {
        fun <T, E> success(value: T): Result<T, E> = Success(value)
        fun <T, E> failure(fail: E): Result<T, E> = Failure(fail)
    }

    abstract val isSuccess: Boolean
    abstract val isFailure: Boolean

    inline fun doOnError(block: (E) -> Unit): Result<T, E> = when (this) {
        is Success<T> -> this
        is Failure<E> -> {
            block(reason)
            this
        }
    }

    inline fun onFailure(f: (Failure<E>) -> Nothing): T = when (this) {
        is Success<T> -> value
        is Failure<E> -> f(this)
    }

    val asOption: Option<T>
        get() = when (this) {
            is Success -> Option.pure(value)
            is Failure -> Option.none()
        }

    val orNull: T?
        get() = when (this) {
            is Success -> value
            is Failure -> null
        }

    infix fun orThrow(block: (E) -> Nothing): T = when (this) {
        is Success -> value
        is Failure -> throw block(reason)
    }

    infix fun getOrElse(defaultValue: @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue
    }

    infix fun orElse(defaultValue: () -> @UnsafeVariance T): T = when (this) {
        is Success -> value
        is Failure -> defaultValue()
    }

    infix fun <R> map(transform: (T) -> R): Result<R, E> = when (this) {
        is Success -> Success(transform(value))
        is Failure -> this
    }

    infix fun <R> flatMap(transform: (T) -> Result<R, @UnsafeVariance E>): Result<R, E> = when (this) {
        is Success -> transform(value)
        is Failure -> this
    }

    infix fun <R> mapFailure(transform: (E) -> R): Result<T, R> = when (this) {
        is Success -> this
        is Failure -> Failure(transform(reason))
    }

    fun forEach(block: (T) -> Unit): Unit = when (this) {
        is Success -> block(value)
        is Failure -> Unit
    }

    class Success<out T> internal constructor(val value: T) : Result<T, Nothing>() {
        override val isSuccess: Boolean = true
        override val isFailure: Boolean = false

        override fun toString(): String = "Success($value)"
    }

    class Failure<out E> internal constructor(val reason: E) : Result<Nothing, E>() {
        override val isSuccess: Boolean = false
        override val isFailure: Boolean = true

        override fun toString(): String = "Failure($reason)"
    }
}