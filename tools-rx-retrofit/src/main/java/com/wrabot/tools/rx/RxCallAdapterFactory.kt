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

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import retrofit2.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A call adaptor factory for RX.
 *
 * It converts Call to RX Single with a retry.
 *
 * @property retry function which converts a throwable to observable when retrying.
 */
@Suppress("unused")
class RxCallAdapterFactory(private val retry: (throwable: Throwable) -> Observable<out Any>) : CallAdapter.Factory() {
    // create a share single
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Share

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<Any, Any> =
            SingleCallAdapter(getParameterUpperBound(0, returnType as ParameterizedType), annotations.find { it is Share } == null)

    inner class SingleCallAdapter<T>(private val responseType: Type, private val share: Boolean) : CallAdapter<T, Any> {
        override fun responseType() = responseType
        override fun adapt(call: Call<T>) = call.toObservable().let { if (share) it.share() else it }.firstOrError()!!
    }

    private fun <T> Call<T>.toObservable() = Observable.create<T> {
        // cloning call avoid "already executed" errors
        clone().apply {
            it.setDisposable(object : Disposable {
                override fun dispose() = cancel()
                override fun isDisposed() = isCanceled
            })
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    response.body()?.apply { it.onNext(this) }
                    it.onComplete()
                }

                override fun onFailure(call: Call<T>, t: Throwable) = it.onError(t)
            })
        }
    }.retryWhen { it.flatMap(retry) }.observeOn(AndroidSchedulers.mainThread())
}
