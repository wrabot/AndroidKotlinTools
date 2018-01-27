package com.wrabot.tools

import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

/**
 * Check recursively that only nullable [value] members are null.
 */
@Suppress("unused")
fun checkNullable(value: Any) = checkNullable(value, null, "")

private fun checkNullable(nullableValue: Any?, itemType: KType?, path: String) {
    try {
        val value = nullableValue ?: if (itemType?.isMarkedNullable != false) return else throw NullPointerException()
        when (value) {
            is Iterable<*> -> value.forEachIndexed { index, item -> checkNullable(item, itemType?.arguments?.getOrNull(0)?.type, "$path[$index]") }
            is Number, is String, is Enum<*> -> Unit
            else -> value::class.memberProperties.forEach { checkNullable(it.getter.call(value), it.returnType, "$path.${it.name}") }
        }
    } catch (error: NullableCheckerException) {
        throw error
    } catch (error: Throwable) {
        throw NullableCheckerException(path, error)
    }
}

class NullableCheckerException(message: String, cause: Throwable? = null) : Exception(message, cause)
