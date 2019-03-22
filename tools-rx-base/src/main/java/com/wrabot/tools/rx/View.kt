@file:Suppress("unused")

package com.wrabot.tools.rx

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import io.reactivex.Completable

fun View.animate(init: ViewPropertyAnimatorCompat.() -> Unit): Completable = Completable.create {
    with(ViewCompat.animate(this)) {
        withEndAction(it::onComplete)
        it.setCancellable(::cancel)
        init()
        start()
    }
}
