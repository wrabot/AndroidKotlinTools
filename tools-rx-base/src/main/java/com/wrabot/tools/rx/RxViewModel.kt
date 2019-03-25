package com.wrabot.tools.rx

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * ViewModel with a disposable automatically cleared
 */
@Suppress("unused")
open class RxViewModel : ViewModel() {
    @Suppress("MemberVisibilityCanBePrivate")
    val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

/**
 * AndroidViewModel with a disposable automatically cleared
 */
@Suppress("unused")
open class RxAndroidViewModel(application: Application) : AndroidViewModel(application) {
    @Suppress("MemberVisibilityCanBePrivate")
    val disposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
