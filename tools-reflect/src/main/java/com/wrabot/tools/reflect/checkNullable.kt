/**
 * Copyright (c) 2017-present, Wilfrid Rabot.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.wrabot.tools.reflect

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
