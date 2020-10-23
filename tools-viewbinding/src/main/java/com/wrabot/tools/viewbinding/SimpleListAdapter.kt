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

package com.wrabot.tools.viewbinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * A ListAdapter adapter which uses data binding for items and lambdas for callbacks.
 * @param inflate the item binding inflater (ie ItemBinding::inflate)
 * @param bind to bind the item to the binding
 */
@Suppress("unused")
open class SimpleListAdapter<T : Any, U : ViewBinding>(
        private val inflate: (LayoutInflater, ViewGroup, Boolean) -> U,
        private val bind: T.(binding: U) -> Unit,
        isSame: (T, T) -> Boolean = { _, _ -> false },
        isSameContent: (T, T) -> Boolean = { oldItem, newItem -> oldItem == newItem }
) : ListAdapter<T, SimpleListAdapter.BindingHolder<U>>(object : ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = isSame(oldItem, newItem)
    override fun areContentsTheSame(oldItem: T, newItem: T) = isSameContent(oldItem, newItem)
}) {
    class BindingHolder<out T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

    var onClick: (T, Int, View) -> Unit = { _, _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            BindingHolder(inflate(LayoutInflater.from(parent.context), parent, false)).apply {
                itemView.setOnClickListener {
                    val item = currentList.getOrNull(adapterPosition) ?: return@setOnClickListener
                    onClick(item, adapterPosition, it)
                }
            }

    override fun onBindViewHolder(holder: BindingHolder<U>, position: Int) =
            getItem(position).bind(holder.binding)
}
