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
import androidx.viewbinding.ViewBinding

/**
 * A ListAdapter adapter which uses view binding for items of different type and lambdas for callbacks.
 * Items must inherits from Item class
 */
@Suppress("unused")
open class MultiListAdapter : ListAdapter<MultiListAdapter.Item<out Any, out ViewBinding>, BindingHolder<ViewBinding>>(object : ItemCallback<Item<out Any, out ViewBinding>>() {
    override fun areItemsTheSame(oldItem: Item<out Any, out ViewBinding>, newItem: Item<out Any, out ViewBinding>) = newItem.isSameItem(oldItem)
    override fun areContentsTheSame(oldItem: Item<out Any, out ViewBinding>, newItem: Item<out Any, out ViewBinding>) = newItem.isSameContent(oldItem)
}) {
    @Suppress("MemberVisibilityCanBePrivate")
    open class Item<T : Any, U : ViewBinding>(val content: T, val inflate: (LayoutInflater, ViewGroup, Boolean) -> U, val bind: T.(binding: U) -> Unit) {
        var isSameItem: (Item<T, U>) -> Boolean = { false }
        var isSameContent: (Item<T, U>) -> Boolean = { content == it.content }
        var onClick: (T, Int, View) -> Unit = { _, _, _ -> }

        @Suppress("UNCHECKED_CAST")
        internal fun isSameItem(oldItem: Item<out Any, out ViewBinding>) =
                (oldItem as? Item<T, U>)?.let(isSameItem) ?: false

        @Suppress("UNCHECKED_CAST")
        internal fun isSameContent(oldItem: Item<out Any, out ViewBinding>) =
                (oldItem as? Item<T, U>)?.let(isSameContent) ?: false

        @Suppress("UNCHECKED_CAST")
        internal fun bind(binding: ViewBinding) {
            (binding as? U)?.run { content.bind(this) }
        }

        internal fun onClick(position: Int, view: View) = onClick(content, position, view)
    }

    private val viewTypes = mutableListOf<(LayoutInflater, ViewGroup, Boolean) -> ViewBinding>()

    override fun getItemViewType(position: Int) = getItem(position).inflate.let {
        val index = viewTypes.indexOf(it)
        if (index >= 0) {
            index
        } else {
            viewTypes.add(it)
            viewTypes.size - 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            BindingHolder(viewTypes[viewType](LayoutInflater.from(parent.context), parent, false)).apply {
                itemView.setOnClickListener {
                    currentList.getOrNull(adapterPosition)?.onClick(adapterPosition, it)
                }
            }

    override fun onBindViewHolder(holder: BindingHolder<ViewBinding>, position: Int) =
            getItem(position).bind(holder.binding)
}
