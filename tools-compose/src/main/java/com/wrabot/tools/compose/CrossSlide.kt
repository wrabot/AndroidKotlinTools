package com.wrabot.tools.compose

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.sign

@OptIn(ExperimentalTextApi::class)
@Preview
@Composable
fun CrossSlidePreview() {
    var state by remember { mutableStateOf(0) }
    val textMeasurer = rememberTextMeasurer()
    CrossSlide(state, Modifier.size(100.dp).clickable { state = (state + 1) % 4 }) {
        Canvas(Modifier.fillMaxSize()) {
            drawText(textMeasurer, it.toString(), Offset(size.width / 2, size.height / 2))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun <T : Comparable<T>> CrossSlide(
    targetState: T,
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) = AnimatedContent(
    targetState = targetState,
    modifier = modifier,
    transitionSpec = {
        when (this.targetState.compareTo(initialState).sign) {
            -1 -> slideInHorizontally { -it } with slideOutHorizontally { it }
            1 -> slideInHorizontally { it } with slideOutHorizontally { -it }
            else -> fadeIn() with fadeOut()
        }
    }
) {
    content(it)
}
