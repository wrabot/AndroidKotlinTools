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
                    it.onNext(response.body()!!)
                    it.onComplete()
                }

                override fun onFailure(call: Call<T>, t: Throwable) = it.onError(t)
            })
        }
    }.retryWhen { it.flatMap(retry) }.observeOn(AndroidSchedulers.mainThread())
}
