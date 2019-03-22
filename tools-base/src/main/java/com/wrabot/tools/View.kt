@file:Suppress("unused")

package com.wrabot.tools

import android.view.View

/**
 * Hide all the [views] (@see View.GONE).
 */
fun hide(vararg views: View) = views.forEach { it.visibility = View.GONE }

/**
 * Show all the [views] (ie View.VISIBLE).
 */
fun show(vararg views: View) = views.forEach { it.visibility = View.VISIBLE }
