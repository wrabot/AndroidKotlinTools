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

@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.wrabot.tools.compose

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * [BackStack] is a container that holds a state with initial value and may memorize previous
 * states in a back stack.
 *
 * [current] current state which can be read as a mutable state by a composable and can be be
 * written without modifying the back stack.
 * [next] adds [current] to the back stack and modify current with new value.
 * [clear] clears the back stack.
 * [hasBack] returns whether the back stack is empty.
 * [back] restores the last state into [current]. Several state can be skipped.
 */
class BackStack<T : Any>(initial: T) {
    private var backStack = mutableListOf<T>()
    var current by mutableStateOf<T>(initial)

    fun hasBack() = backStack.isNotEmpty()
    fun clear() = backStack.clear()

    fun clearTo(state: T, inclusive: Boolean = false) {
        while (backStack.lastOrNull() != state) backStack.removeLastOrNull()
        if (inclusive) backStack.removeLastOrNull()
    }

    fun back(skip: Int = 0) {
        repeat(skip) { backStack.removeLastOrNull() ?: return }
        current = backStack.removeLastOrNull() ?: return
    }

    fun next(newState: T) {
        backStack.add(current)
        current = newState
    }
}
