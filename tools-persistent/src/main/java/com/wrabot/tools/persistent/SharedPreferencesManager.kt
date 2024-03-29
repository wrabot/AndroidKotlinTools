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

@file:Suppress("unused")

package com.wrabot.tools.persistent

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import java.util.*
import kotlin.reflect.KProperty

/**
 * A shared preferences manager.
 *
 * It maps shared preferences fields to kotlin properties
 * It must be extended with the custom fields in shared preferences
 *
 * @code
 * class MyPreferencesManager(sharedPreferences: SharedPreferences) : SharedPreferencesManager(sharedPreferences) {
 *      var myString by StringDelegate()
 *      var myInt by IntDelegate()
 *      var myBool by BooleanDelegate()
 * }
 * @code
 *
 * @property sharedPreferences the underlying shared preferences.
 * @constructor Creates a shared preferences manager.
 */
@Suppress("MemberVisibilityCanBePrivate")
open class SharedPreferencesManager(val sharedPreferences: SharedPreferences) {

    /**
     * Clear shared preferences with optional exceptions.
     *
     * @property exceptions the property names to excluded from clear.
     */
    fun clear(vararg exceptions: String) = sharedPreferences.edit().apply {
        if (exceptions.isEmpty()) {
            clear()
        } else {
            sharedPreferences.all.keys.minus(exceptions.toSet()).forEach { remove(it) }
        }
    }.apply()

    inner class StringDelegate(defaultValue: String = "") :
        PreferenceDelegate<String>({ key, value -> getString(key, value)!! }, defaultValue, Editor::putString)

    inner class BooleanDelegate(defaultValue: Boolean = false) :
        PreferenceDelegate<Boolean>(SharedPreferences::getBoolean, defaultValue, Editor::putBoolean)

    inner class IntDelegate(defaultValue: Int = 0) :
        PreferenceDelegate<Int>(SharedPreferences::getInt, defaultValue, Editor::putInt)

    inner class LongDelegate(defaultValue: Long = 0) :
        PreferenceDelegate<Long>(SharedPreferences::getLong, defaultValue, Editor::putLong)

    inner class FloatDelegate(defaultValue: Float = 0f) :
        PreferenceDelegate<Float>(SharedPreferences::getFloat, defaultValue, Editor::putFloat)

    inner class StringSetDelegate(defaultValue: Set<String> = Collections.emptySet()) :
        PreferenceDelegate<Set<String>>({ key, value -> getStringSet(key, value)!! }, defaultValue, Editor::putStringSet)

    open inner class PreferenceDelegate<T>(
        private val get: SharedPreferences.(String, T) -> T,
        private var defaultValue: T,
        private val put: Editor.(String, T) -> Editor
    ) {
        var onModified: ((old: T, new: T) -> Boolean)? = null
        operator fun getValue(thisRef: Any?, property: KProperty<*>) = sharedPreferences.get(property.name, defaultValue)
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            if (onModified?.invoke(sharedPreferences.get(property.name, defaultValue), value) != false) {
                sharedPreferences.edit().apply { put(property.name, value) }.apply()
            }
        }
    }
}
