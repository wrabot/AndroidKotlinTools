package com.wrabot.tools

import android.content.SharedPreferences
import java.util.*
import kotlin.reflect.KProperty

@Suppress("unused")
open class SharedPreferencesManager(val sharedPreferences: SharedPreferences) {
    fun clear() = sharedPreferences.edit().clear().apply()

    inner class StringDelegate(defaultValue: String = "")
        : PreferenceDelegate<String>(SharedPreferences::getString, defaultValue, SharedPreferences.Editor::putString)

    inner class BooleanDelegate(defaultValue: Boolean = false)
        : PreferenceDelegate<Boolean>(SharedPreferences::getBoolean, defaultValue, SharedPreferences.Editor::putBoolean)

    inner class IntDelegate(defaultValue: Int = 0)
        : PreferenceDelegate<Int>(SharedPreferences::getInt, defaultValue, SharedPreferences.Editor::putInt)

    inner class LongDelegate(defaultValue: Long = 0)
        : PreferenceDelegate<Long>(SharedPreferences::getLong, defaultValue, SharedPreferences.Editor::putLong)

    inner class FloatDelegate(defaultValue: Float = 0f)
        : PreferenceDelegate<Float>(SharedPreferences::getFloat, defaultValue, SharedPreferences.Editor::putFloat)

    inner class StringSetDelegate(defaultValue: Set<String> = Collections.emptySet())
        : PreferenceDelegate<Set<String>>(SharedPreferences::getStringSet, defaultValue, SharedPreferences.Editor::putStringSet)

    open inner class PreferenceDelegate<T>(
            private val get: SharedPreferences.(String, T) -> T,
            private var defaultValue: T,
            private val put: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
    ) {
        var onModified: ((old: T, new: T) -> Boolean)? = null
        operator fun getValue(thisRef: Any?, property: KProperty<*>) = sharedPreferences.get(property.name, defaultValue)
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            if (onModified?.invoke(sharedPreferences.get(property.name, defaultValue), value) != false)
                sharedPreferences.edit().put(property.name, value).apply()
        }
    }
}
