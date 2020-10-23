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

package com.wrabot.tools.databinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter

/**
 * A ListAdapter adapter which uses data binding for items of different type and lambdas for callbacks.
 * Items must inherits from Item class
 */
@Suppress("unused")
open class MultiListAdapter : ListAdapter<MultiListAdapter.Item<out Any>, BindingHolder<ViewDataBinding>>(object : ItemCallback<Item<out Any>>() {
    override fun areItemsTheSame(oldItem: Item<out Any>, newItem: Item<out Any>) = newItem.isSameItem(oldItem)
    override fun areContentsTheSame(oldItem: Item<out Any>, newItem: Item<out Any>) = newItem.isSameContent(oldItem)
}) {
    @Suppress("MemberVisibilityCanBePrivate")
    open class Item<T>(val content: T, @LayoutRes val layoutId: Int, val variableId: Int) {
        var isSameItem: (T) -> Boolean = { content === it }
        var isSameContent: (T) -> Boolean = { content == it }
        var onClick: (T, Int, View) -> Unit = { _, _, _ -> }

        // internal methods to convert generic items
        internal fun isSameItem(oldItem: Item<out Any>) = getContent(oldItem)?.let(isSameItem) ?: false

        internal fun isSameContent(oldItem: Item<out Any>) = getContent(oldItem)?.let(isSameContent) ?: false

        // internal methods to useful for adapter
        internal fun onClick(position: Int, view: View) = onClick(content, position, view)

        internal fun bindTo(binding: ViewDataBinding) = binding.setVariable(variableId, content)

        // Gets the content of an item if the current item is compatible with the given item
        @Suppress("UNCHECKED_CAST")
        private fun getContent(item: Item<out Any>) =
                if (layoutId == item.layoutId && variableId == item.variableId) item.content as? T else null
    }

    override fun getItemViewType(position: Int) = getItem(position).layoutId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingHolder(inflate(viewType, parent)).apply {
        itemView.setOnClickListener {
            val item = currentList.getOrNull(adapterPosition) ?: return@setOnClickListener
            item.onClick(adapterPosition, it)
        }
    }

    override fun onBindViewHolder(holder: BindingHolder<ViewDataBinding>, position: Int) = with(holder.binding) {
        getItem(position).bindTo(this)
        executePendingBindings()
    }

    override fun onViewAttachedToWindow(holder: BindingHolder<ViewDataBinding>) = holder.onAttach()
    override fun onViewDetachedFromWindow(holder: BindingHolder<ViewDataBinding>) = holder.onDetach()

    private fun inflate(layoutId: Int, parent: ViewGroup) =
            DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), layoutId, parent, false)
}
