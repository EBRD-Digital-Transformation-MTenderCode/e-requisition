package com.procurement.requisition.lib

import com.procurement.requisition.lib.fail.Failure
import com.procurement.requisition.lib.functional.Result

inline fun <T, V> Collection<T>.isUnique(selector: (T) -> V): Boolean {
    val unique = HashSet<V>()
    forEach { item ->
        if (!unique.add(selector(item))) return false
    }
    return true
}

inline fun <T, V> Collection<T>.setBy(selector: (T) -> V): Set<V> {
    val collections = LinkedHashSet<V>()
    forEach {
        collections.add(selector(it))
    }
    return collections
}

inline fun <T, R> Collection<T>.mapIfNotEmpty(transform: (T) -> R): List<R>? =
    if (this.isNotEmpty())
        this.map(transform)
    else
        null

inline fun <T, R> Collection<T>?.mapOrEmpty(transform: (T) -> R): List<R> = this?.map(transform) ?: emptyList()

inline fun <T, C : Collection<T>, E : RuntimeException> C?.errorIfEmpty(exceptionBuilder: () -> E): C? =
    if (this != null && this.isEmpty())
        throw exceptionBuilder()
    else
        this

fun <T> T?.toList(): List<T> = if (this != null) listOf(this) else emptyList()

inline fun <T, V> Collection<T>.toSetBy(selector: (T) -> V): Set<V> {
    val collections = LinkedHashSet<V>()
    forEach {
        collections.add(selector(it))
    }
    return collections
}

fun <T, R, E : Failure> List<T>.mapResult(block: (T) -> Result<R, E>): Result<List<R>, E> {
    val r = mutableListOf<R>()
    for (element in this) {
        when (val result = block(element)) {
            is Result.Success -> r.add(result.value)
            is Result.Failure -> return result
        }
    }
    return Result.success(r)
}

fun <T> getUnknownElements(received: Iterable<T>, known: Iterable<T>) =
    getNewElements(received = received, known = known)

fun <T> getNewElements(received: Iterable<T>, known: Iterable<T>): Set<T> =
    received.asSet().subtract(known.asSet())

fun <T> getMissingElements(received: Iterable<T>, known: Iterable<T>): Set<T> =
    known.asSet().subtract(received.asSet())

fun <T> getElementsForUpdate(received: Set<T>, known: Set<T>) = known.intersect(received)

inline fun <T, V> Collection<T>.getDuplicate(selector: (T) -> V): T? {
    val unique = HashSet<V>()
    this.forEach { item ->
        if (!unique.add(selector(item)))
            return item
    }
    return null
}

private fun <T> Iterable<T>.asSet(): Set<T> = when (this) {
    is Set -> this
    else -> this.toSet()
}