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

@file:Suppress("unused", "DeprecatedCallableAddReplaceWith")

package com.wrabot.tools

import android.text.SpannableStringBuilder
import android.text.Spanned

/**
 * Creates SpannableStringBuilder from a [text] with several [spans].
 */
@Deprecated(message = "replace with text.toSpannable()+spans")
fun spannableStringBuilder(text: CharSequence, vararg spans: Any) = SpannableStringBuilder(text).apply {
    setSpans(0, length, *spans)
}

/**
 * Append a [text] with several [spans] to a SpannableStringBuilder
 */
@Deprecated(message = "replace with inSpans(spans) {append(text)}")
fun SpannableStringBuilder.append(text: CharSequence, vararg spans: Any) = apply {
    val start = length
    append(text)
    setSpans(start, length, *spans)
}

/**
 * Append a [text] with several [spans] and a new line to a SpannableStringBuilder
 */
@Deprecated(message = "replace with replace with inSpans(spans) {append(text).appendln()}")
fun SpannableStringBuilder.appendln(text: CharSequence, vararg spans: Any) = append(text, *spans).apply {
    appendln()
}

/**
 * Apply several [spans] to a SpannableStringBuilder
 */
@Deprecated(message = "replace with replace with setSpans(start, end, span)")
fun SpannableStringBuilder.setSpans(start: Int, end: Int, vararg spans: Any) = apply {
    spans.forEach {
        setSpan(it, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}


