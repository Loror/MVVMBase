package com.loror.mvvm.bean;

import com.loror.lororUtil.http.api.ApiClient;
import com.loror.lororUtil.http.api.MultiOnRequestListener;
import com.loror.lororUtil.http.api.OnRequestListener;

public class ApiInfo {

    private final ApiClient apiClient;
    private MultiOnRequestListener multiOnRequestListener;
    private final Class<?> type;

    public ApiInfo(ApiClient apiClient, Class<?> type) {
        this.apiClient = apiClient;
        this.type = type;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public MultiOnRequestListener getMultiOnRequestListener() {
        return multiOnRequestListener;
    }

    /**
     * 注意：如果直接通过ApiClient设置了OnRequestListener，这里的OnRequestListener将失效
     */
    public void addOnRequestListener(OnRequestListener onRequestListener) {
        if (onRequestListener == null) {
            return;
        }
        if (this.multiOnRequestListener == null) {
            this.multiOnRequestListener = new MultiOnRequestListener();
        }
        this.multiOnRequestListener.addOnRequestListener(onRequestListener);
    }

    public Class<?> getType() {
        return type;
    }

}
