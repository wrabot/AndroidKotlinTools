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

package com.wrabot.tools.rx

import android.support.v7.widget.RecyclerView
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

abstract class RxRecyclerAdapter<T : Any, U : RecyclerView.ViewHolder>(private val observable: Observable<T>, initialValue: T) : RecyclerView.Adapter<U>() {
    var value = initialValue
        private set
    private var dispose: Disposable? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        dispose = observable.observeOn(AndroidSchedulers.mainThread()).bindToLifecycle(recyclerView).subscribe {
            value = it
            notifyDataSetChanged()
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        dispose?.dispose()
        dispose = null
    }
}
