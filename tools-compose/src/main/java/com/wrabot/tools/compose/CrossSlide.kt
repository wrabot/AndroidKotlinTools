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

package com.wrabot.tools.compose

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlin.math.sign

@Preview
@Composable
fun CrossSlidePreview() {
    var state by remember { mutableStateOf(0) }
    CrossSlide(state, Modifier.size(100.dp).clickable { state = (state + 1) % 4 }, "slide") {
        Box(contentAlignment = Alignment.Center) {
            Text(AnnotatedString(it.toString()))
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun Text(text: AnnotatedString) {
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(text)
    val size = with(LocalDensity.current) { textLayoutResult.size.toSize().toDpSize() }
    Canvas(Modifier.size(size)) { drawText(textLayoutResult) }
}

/**
 * [CrossSlide] is a container that slides its content when [targetState] changes.
 * Its [content] for different target states is defined in a mapping between a target state
 * and a composable function.
 *
 * When [targetState] changes, content for both new and previous targetState will be looked up
 * through the [content] lambda.
 * The [targetState] must be a comparable in order to decide which animation will be execute:
 * If [targetState] is greater than current state, the current state's content slide out to left
 * and the target state's content slide in from right
 * If [targetState] is lesser than current state, the current state's content slide in from left
 * and the target state's content slide out to right
 * If [targetState] equals current state, the current state's content fade out and
 * the target state's content fade in
 *
 * **IMPORTANT**: The targetState parameter for the [content] lambda should *always* be taken
 * into account in deciding what composable function to return as the content for that state.
 * This is critical to ensure a successful lookup of all the incoming and outgoing content during
 * content transform.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T : Comparable<T>> CrossSlide(
    targetState: T,
    modifier: Modifier = Modifier,
    label : String = "CrossSlide",
    content: @Composable (T) -> Unit
) = AnimatedContent(
    targetState = targetState,
    modifier = modifier,
    transitionSpec = {
        when (this.targetState.compareTo(initialState).sign) {
            -1 -> slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
            1 -> slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            else -> fadeIn() togetherWith fadeOut()
        }
    },
    label = label
) {
    content(it)
}
