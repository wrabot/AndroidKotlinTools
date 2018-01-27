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
