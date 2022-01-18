package com.demon.rxbus

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Lifecycle
import com.trello.rxlifecycle4.LifecycleProvider
import com.trello.lifecycle4.android.lifecycle.AndroidLifecycle
import kotlin.Throws
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import com.demon.rxbus.RxBus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.core.ObservableEmitter
import kotlin.jvm.Volatile
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import java.util.concurrent.ConcurrentHashMap

/**
 * @author DeMon
 * @date 2018/9/7
 * @description RxBus，RxJava+Rxlifecycle消息传递
 */
class RxBus private constructor() {
    private val mBus: Subject<Any> = PublishSubject.create<Any>().toSerialized()
    private val mStickyEventMap: MutableMap<Class<*>, Any>

    /**
     * 发送事件
     */
    fun post(event: Any) {
        mBus.onNext(event)
    }

    fun <T : Any> toObservable(owner: LifecycleOwner?, eventType: Class<T>): Observable<T> {
        return toObservable(owner, eventType, Lifecycle.Event.ON_DESTROY)
    }

    /**
     * 使用Rxlifecycle解决RxJava引起的内存泄漏
     */
    fun <T : Any> toObservable(owner: LifecycleOwner?, eventType: Class<T>, event: Lifecycle.Event): Observable<T> {
        val provider = AndroidLifecycle.createLifecycleProvider(owner)
        return mBus.ofType(eventType)
            .doOnDispose { Log.i("RxBus", "RxBus取消订阅") }
            .compose(provider.bindUntilEvent(event))
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 判断是否有订阅者
     */
    fun hasObservers(): Boolean {
        return mBus.hasObservers()
    }

    fun reset() {
        mDefaultInstance = null
    }
    /**
     * Stciky 相关
     */
    /**
     * 发送一个新Sticky事件
     */
    fun postSticky(event: Any) {
        synchronized(mStickyEventMap) { mStickyEventMap.put(event.javaClass, event) }
        post(event)
    }

    fun <T : Any> toObservableSticky(owner: LifecycleOwner?, eventType: Class<T>): Observable<T> {
        return toObservableSticky(owner, eventType, Lifecycle.Event.ON_DESTROY)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     * 使用Rxlifecycle解决RxJava引起的内存泄漏
     */
    fun <T : Any> toObservableSticky(owner: LifecycleOwner?, eventType: Class<T>, e: Lifecycle.Event): Observable<T> {
        synchronized(mStickyEventMap) {
            val provider = AndroidLifecycle.createLifecycleProvider(owner)
            val observable = mBus.ofType(eventType)
                .doOnDispose { Log.i("RxBus", "RxBus取消订阅") }
                .compose(provider.bindUntilEvent(e))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
            val event = mStickyEventMap[eventType]
            return if (event != null) {
                observable.mergeWith(Observable.create { subscriber -> subscriber.onNext(eventType.cast(event)) })
            } else {
                observable
            }
        }
    }

    /**
     * 根据eventType获取Sticky事件
     */
    fun <T : Any> getStickyEvent(eventType: Class<T>): T {
        synchronized(mStickyEventMap) { return eventType.cast(mStickyEventMap[eventType]) }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    fun <T : Any> removeStickyEvent(eventType: Class<T>): T {
        synchronized(mStickyEventMap) { return eventType.cast(mStickyEventMap.remove(eventType)) }
    }

    /**
     * 移除所有的Sticky事件
     */
    fun removeAllStickyEvents() {
        synchronized(mStickyEventMap) { mStickyEventMap.clear() }
    }

    companion object {
        @Volatile
        private var mDefaultInstance: RxBus? = null

        @JvmStatic
        fun getInstance(): RxBus {
            return mDefaultInstance ?: synchronized(RxBus::class.java) {
                mDefaultInstance ?: RxBus().also { mDefaultInstance = it }
            }
        }

    }

    init {
        mStickyEventMap = ConcurrentHashMap()
    }
}