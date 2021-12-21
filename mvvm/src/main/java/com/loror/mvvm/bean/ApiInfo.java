package com.loror.mvvm.bean;

import com.loror.lororUtil.http.api.ApiClient;
import com.loror.lororUtil.http.api.MultiOnRequestListener;

public class ApiInfo {

    private ApiClient apiClient;
    private MultiOnRequestListener multiOnRequestListener;
    private Class<?> type;

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public MultiOnRequestListener getMultiOnRequestListener() {
        return multiOnRequestListener;
    }

    public void setMultiOnRequestListener(MultiOnRequestListener multiOnRequestListener) {
        this.multiOnRequestListener = multiOnRequestListener;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
