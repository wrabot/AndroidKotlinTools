@file:Suppress("unused")

package com.wrabot.tools

/**
 * Gets enum value for [name] or a [default] value if [name] is not found.
 */
inline fun <reified T : Enum<*>> enumValueOrDefault(name: String, default: T? = null): T? =
        T::class.java.enumConstants?.firstOrNull { it.name == name } ?: default
