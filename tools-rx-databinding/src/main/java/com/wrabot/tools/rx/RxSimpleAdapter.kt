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

import android.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.trello.rxlifecycle2.kotlin.bindToLifecycle
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

@Suppress("unused")
open class RxSimpleAdapter<T : Any, U : ViewDataBinding>(
        observable: Observable<List<T>>,
        initialValue: List<T>,
        private val inflate: (LayoutInflater, ViewGroup, Boolean) -> U,
        private val set: U.(T) -> Unit,
        private val id: (T.() -> Long)? = null

) : RxRecyclerAdapter<List<T>, BindingHolder<U>>(observable, initialValue) {
    @Suppress("MemberVisibilityCanBePrivate")
    val clicks: PublishSubject<T> = PublishSubject.create()


    init {
        @Suppress("LeakingThis")
        setHasStableIds(id != null)
    }

    override fun getItemId(position: Int) = id?.invoke(value[position]) ?: 0

    override fun getItemCount(): Int = value.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingHolder(inflate(LayoutInflater.from(parent.context), parent, false)).apply {
        itemView.clicks()
                .filter { adapterPosition in value.indices }
                .map { value[adapterPosition] }
                .bindToLifecycle(parent).subscribe(clicks)
    }

    override fun onBindViewHolder(holder: BindingHolder<U>, position: Int) {
        holder.binding.set(value[position])
        holder.binding.executePendingBindings()
    }
}
