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

package com.wrabot.tools

import android.text.SpannableStringBuilder
import android.text.Spanned

fun <T : Any> T.println() = apply { println(this) }

fun spannableStringBuilder(text: CharSequence, vararg spans: Any) = SpannableStringBuilder(text).apply {
    setSpans(0, length, *spans)
}

fun SpannableStringBuilder.append(text: CharSequence, vararg spans: Any) = apply {
    val start = length
    append(text)
    setSpans(start, length, *spans)
}

fun SpannableStringBuilder.appendln(text: CharSequence, vararg spans: Any) = append(text, *spans).apply {
    appendln()
}

fun SpannableStringBuilder.setSpans(start: Int, end: Int, vararg spans: Any) = apply {
    spans.forEach {
        setSpan(it, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    }
}


