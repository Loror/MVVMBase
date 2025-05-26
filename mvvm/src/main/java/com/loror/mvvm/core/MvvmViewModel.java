package com.loror.mvvm.core;

import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.loror.mvvm.bean.MethodInfo;
import com.loror.mvvm.utls.ConfigUtil;
import com.loror.mvvm.utls.SignUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MvvmViewModel extends ViewModel {

    /**
     * 默认成功事件，{@link MvvmViewModel#success}触发
     */
    public static final int EVENT_SUCCESS = 0X9767;
    /**
     * 默认失败事件，{@link MvvmViewModel#failed}触发
     */
    public static final int EVENT_FAILED = 0X9768;

    /**
     * 内部控制loading事件
     */
    private static final int EVENT_SHOW_PROGRESS = 0X9769;
    private static final int EVENT_CLOSE_PROGRESS = 0X9770;

    {
        ConfigUtil.config(getClass());
        SignUtil.signConfig(this);
    }

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

    protected final MutableLiveData<LiveDataEvent> liveData = new MutableLiveData<>();
    protected final List<WeakReference<LifecycleOwner>> attached = new ArrayList<>();
    private boolean releaseWhenCleared = true;
    private boolean multiMode;

    @Override
    protected void onCleared() {
        if (releaseWhenCleared) {
            release();
        }
        super.onCleared();
    }

    /**
     * onCleared时解除liveData绑定
     */
    public void setReleaseWhenCleared(boolean releaseWhenCleared) {
        this.releaseWhenCleared = releaseWhenCleared;
    }

    /**
     * 支持多owner attach模式
     */
    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    /**
     * 清空监听
     */
    public void release() {
        for (WeakReference<LifecycleOwner> item : attached) {
            LifecycleOwner view = item.get();
            if (view != null) {
                liveData.removeObservers(view);
            }
        }
        attached.clear();
    }

    /**
     * 绑定LifeCycle
     */
    public void listenLifeCycle(LifecycleOwner view) {
        if (this instanceof LifecycleObserver) {
            view.getLifecycle().addObserver((LifecycleObserver) this);
        }
    }

    private Map<Integer, MethodInfo> getEvents(LifecycleOwner view) {
        Method[] methods = view.getClass().getDeclaredMethods();
        Map<Integer, MethodInfo> events = new HashMap<>();
        for (Method method : methods) {
            com.loror.mvvm.annotation.LiveDataEvent liveDataEvent = method.getAnnotation(com.loror.mvvm.annotation.LiveDataEvent.class);
            if (liveDataEvent != null) {
                if (method.getParameterTypes().length > 1) {
                    throw new IllegalArgumentException("LiveDataEvent的方法" + method.getName()
                            + "只能有不超过一个参数");
                }
                MethodInfo old = events.get(liveDataEvent.value());
                if (old != null) {
                    throw new IllegalStateException("方法" + method.getName() + "与" + old.getName()
                            + "使用了相同的code:" + liveDataEvent.value());
                }
                events.put(liveDataEvent.value(), new MethodInfo(method));
            }
        }
        return events;
    }

    /**
     * 绑定View，自动分发事件
     */
    public void attachView(LifecycleOwner view) {
        for (WeakReference<LifecycleOwner> item : attached) {
            if (item.get() == view) {
                return;
            }
        }
        attached.add(new WeakReference<>(view));
        Map<Integer, MethodInfo> events = getEvents(view);
        liveData.observe(view, liveDataEvent -> {
            if (multiMode) {
                for (WeakReference<LifecycleOwner> lifecycleOwnerWeakReference : attached) {
                    LifecycleOwner owner = lifecycleOwnerWeakReference.get();
                    if (owner != null) {
                        Map<Integer, MethodInfo> everyEvents = getEvents(owner);
                        MethodInfo method = everyEvents.get(liveDataEvent.getCode());
                        invokeEvent(method, liveDataEvent, owner);
                    }
                }
            } else {
                MethodInfo method = events.get(liveDataEvent.getCode());
                invokeEvent(method, liveDataEvent, view);
            }
        });
    }

    private void invokeEvent(MethodInfo method, LiveDataEvent liveDataEvent, LifecycleOwner view) {
        if (eventIntercept(liveDataEvent, view)) {
            return;
        }
        if (liveDataEvent.getCode() == EVENT_SUCCESS || liveDataEvent.getCode() == EVENT_FAILED) {
            if (method == null) {
                return;
            }
        } else if (liveDataEvent.getCode() == EVENT_SHOW_PROGRESS) {
            Object value = liveDataEvent.getData();
            if (view instanceof MvvmActivity) {
                if (value == null) {
                    ((MvvmActivity) view).showProgress();
                } else {
                    ((MvvmActivity) view).showProgress(String.valueOf(value));
                }
            } else if (view instanceof MvvmFragment) {
                if (value == null) {
                    ((MvvmFragment) view).showProgress();
                } else {
                    ((MvvmFragment) view).showProgress(String.valueOf(value));
                }
            }
            return;
        } else if (liveDataEvent.getCode() == EVENT_CLOSE_PROGRESS) {
            if (view instanceof MvvmActivity) {
                ((MvvmActivity) view).dismissProgress();
            } else if (view instanceof MvvmFragment) {
                ((MvvmFragment) view).dismissProgress();
            }
            return;
        } else {
            if (method == null) {
                Log.e("LiveDataEvent", "事件分发失败，code:" + liveDataEvent.getCode()
                        + " model:" + MvvmViewModel.this.getClass().getSimpleName());
                return;
            }
        }
        invoke(method, view, liveDataEvent.getData());
    }

    /**
     * 执行事件
     */
    private void invoke(MethodInfo methodInfo, Object view, Object data) {
        Method method = methodInfo.getMethod();
        Class<?>[] types = method.getParameterTypes();
        if (types.length == 1) {
            if (methodInfo.isAllowExact()) {
                data = methodInfo.exact(data, types[0]);
            }
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
     * LiveDataEvent事件拦截，返回true时拦截
     */
    protected boolean eventIntercept(LiveDataEvent event, LifecycleOwner view) {
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
        dismissProgress();
        dispatchLiveDataEvent(EVENT_FAILED, message);
    }

    /**
     * 成功，触发toast，与EVENT_SUCCESS
     */
    @CallSuper
    public void success(String message) {
        dismissProgress();
        dispatchLiveDataEvent(EVENT_SUCCESS, message);
    }

    /**
     * 显示loading,使用默认内容
     */
    public void showProgress() {
        showProgress(null);
    }

    /**
     * 显示loading，可携带loading提示内容
     */
    public void showProgress(String message) {
        liveData.setValue(new LiveDataEvent(EVENT_SHOW_PROGRESS, message));
    }

    /**
     * 隐藏loading
     */
    public void dismissProgress() {
        liveData.setValue(new LiveDataEvent(EVENT_CLOSE_PROGRESS, null));
    }

}
