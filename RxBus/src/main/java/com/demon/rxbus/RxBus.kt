package com.demon.rxbus;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @author DeMon
 * @date 2018/9/7
 * @description RxBus，RxJava+Rxlifecycle消息传递
 */
public class RxBus {
    private volatile static RxBus mDefaultInstance;
    private final Subject<Object> mBus;
    private final Map<Class<?>, Object> mStickyEventMap;

    private RxBus() {
        mBus = PublishSubject.create().toSerialized();
        mStickyEventMap = new ConcurrentHashMap<>();
    }

    public static RxBus getInstance() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    /**
     * 发送事件
     */
    public void post(Object event) {
        mBus.onNext(event);
    }

    public <T> Observable<T> toObservable(LifecycleOwner owner, final Class<T> eventType) {
        return toObservable(owner, eventType, Lifecycle.Event.ON_DESTROY);
    }

    /**
     * 使用Rxlifecycle解决RxJava引起的内存泄漏
     */
    public <T> Observable<T> toObservable(LifecycleOwner owner, Class<T> eventType, Lifecycle.Event event) {
        LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(owner);
        return mBus.ofType(eventType)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i("RxBus", "RxBus取消订阅");
                    }
                })
                .compose(provider.<T>bindUntilEvent(event))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 判断是否有订阅者
     */
    public boolean hasObservers() {
        return mBus.hasObservers();
    }

    public void reset() {
        mDefaultInstance = null;
    }


    /**
     * Stciky 相关
     */

    /**
     * 发送一个新Sticky事件
     */
    public void postSticky(Object event) {
        synchronized (mStickyEventMap) {
            mStickyEventMap.put(event.getClass(), event);
        }
        post(event);
    }

    public <T> Observable<T> toObservableSticky(LifecycleOwner owner, final Class<T> eventType) {
        return toObservableSticky(owner, eventType, Lifecycle.Event.ON_DESTROY);
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     * 使用Rxlifecycle解决RxJava引起的内存泄漏
     */
    public <T> Observable<T> toObservableSticky(LifecycleOwner owner, final Class<T> eventType, Lifecycle.Event e) {
        synchronized (mStickyEventMap) {
            LifecycleProvider<Lifecycle.Event> provider = AndroidLifecycle.createLifecycleProvider(owner);
            Observable<T> observable = mBus.ofType(eventType)
                    .doOnDispose(new Action() {
                        @Override
                        public void run() throws Exception {
                            Log.i("RxBus", "RxBus取消订阅");
                        }
                    })
                    .compose(provider.<T>bindUntilEvent(e))
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            final Object event = mStickyEventMap.get(eventType);

            if (event != null) {
                return observable.mergeWith(Observable.create(new ObservableOnSubscribe<T>() {
                    @Override
                    public void subscribe(ObservableEmitter<T> subscriber) throws Exception {
                        subscriber.onNext(eventType.cast(event));
                    }
                }));
            } else {
                return observable;
            }
        }
    }

    /**
     * 根据eventType获取Sticky事件
     */
    public <T> T getStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.get(eventType));
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    public <T> T removeStickyEvent(Class<T> eventType) {
        synchronized (mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType));
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    public void removeAllStickyEvents() {
        synchronized (mStickyEventMap) {
            mStickyEventMap.clear();
        }
    }

}
