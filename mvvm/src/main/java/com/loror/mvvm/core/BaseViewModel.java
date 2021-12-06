package com.loror.mvvm.core;

import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseViewModel extends ViewModel {

    /**
     * 默认成功事件，{@link BaseViewModel#success}触发
     */
    public static final int EVENT_SUCCESS = 0X9767;
    /**
     * 默认失败事件，{@link BaseViewModel#failed}触发
     */
    public static final int EVENT_FAILED = 0X9768;

    /**
     * liveData传输数据
     */
    public static class LiveDataEvent {
        private final int code;
        private final Object data;

        public LiveDataEvent(int code, Object data) {
            this.code = code;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public Object getData() {
            return data;
        }
    }

    private final MutableLiveData<LiveDataEvent> liveData = new MutableLiveData<>();
    private final List<LifecycleOwner> attached = new ArrayList<>();

    @Override
    protected void onCleared() {
        for (LifecycleOwner view : attached) {
            liveData.removeObservers(view);
        }
        attached.clear();
        super.onCleared();
    }

    /**
     * 绑定LifeCycle
     */
    public void listenLifeCycle(LifecycleOwner view) {
        if (this instanceof LifecycleObserver) {
            view.getLifecycle().addObserver((LifecycleObserver) this);
        }
    }

    /**
     * 绑定View，自动分发事件
     */
    public void attachView(LifecycleOwner view) {
        if (attached.contains(view)) {
            return;
        }
        attached.add(view);
        Method[] methods = view.getClass().getDeclaredMethods();
        Map<Integer, Method> events = new HashMap<>();
        for (Method method : methods) {
            com.loror.mvvm.annotation.LiveDataEvent liveDataEvent = method.getAnnotation(com.loror.mvvm.annotation.LiveDataEvent.class);
            if (liveDataEvent != null) {
                if (method.getParameterTypes().length > 1) {
                    throw new IllegalArgumentException("LiveDataEvent的方法" + method.getName()
                            + "只能有不超过一个参数");
                }
                Method old = events.get(liveDataEvent.value());
                if (old != null) {
                    throw new IllegalStateException("方法" + method.getName() + "与" + old.getName()
                            + "使用了相同的code:" + liveDataEvent.value());
                }
                events.put(liveDataEvent.value(), method);
            }
        }
        liveData.observe(view, liveDataEvent -> {
            Method method = events.get(liveDataEvent.getCode());
            if (eventIntercept(liveDataEvent)) {
                return;
            }
            if (liveDataEvent.getCode() == EVENT_SUCCESS || liveDataEvent.getCode() == EVENT_FAILED) {
                if (method == null) {
                    return;
                }
            } else {
                if (method == null) {
                    Log.e("LiveDataEvent", "事件分发失败，code:" + liveDataEvent.getCode()
                            + " model:" + BaseViewModel.this.getClass().getSimpleName());
                    return;
                }
            }
            invoke(method, view, liveDataEvent.getData());
        });
    }

    /**
     * 执行事件
     */
    private void invoke(Method method, Object view, Object data) {
        Class<?>[] types = method.getParameterTypes();
        if (types.length == 1) {
            try {
                method.invoke(view, data);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            try {
                method.invoke(view);
            } catch (IllegalAccessException | InvocationTargetException e) {
                Log.e("执行事件失败", "e:", e);
            }
        }
    }

    /**
     * LiveDataEvent时间拦截，返回true时拦截
     */
    protected boolean eventIntercept(LiveDataEvent event) {
        return false;
    }

    /**
     * 分发事件到livedata
     */
    public void dispatchLiveDataEvent(int event, Object data) {
        liveData.setValue(new LiveDataEvent(event, data));
    }

    /**
     * 失败，触发toast，与EVENT_ERROR
     */
    @CallSuper
    public void failed(String message) {
        dispatchLiveDataEvent(EVENT_FAILED, message);
    }

    /**
     * 成功，触发toast，与EVENT_SUCCESS
     */
    @CallSuper
    public void success(String message) {
        dispatchLiveDataEvent(EVENT_SUCCESS, message);
    }

}
