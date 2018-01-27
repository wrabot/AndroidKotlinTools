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
