package com.loror.mvvm.core;

import android.content.Context;
import android.content.Intent;

import com.loror.lororUtil.dataBus.DataBus;
import com.loror.lororUtil.dataBus.DataBusReceiver;
import com.loror.mvvm.utls.ConfigUtil;
import com.loror.mvvm.utls.SignUtil;

public class MvvmModel {

    private DataBusReceiver receiver;

    {
        ConfigUtil.config(getClass());
        SignUtil.signConfig(this);
    }

    /**
     * 通知监听数据
     */
    protected void listen(DataBusReceiver receiver) {
        DataBus.addReceiver(receiver);
    }

    /**
     * 通知监听
     */
    public void notice(Context context, String name) {
        notice(context, name, null);
    }

    /**
     * 通知监听
     */
    public void notice(Context context, String name, Intent data) {
        DataBus.notifyReceivers(name, data, context);
    }

    /**
     * 清除监听
     */
    public void clear() {
        if (receiver != null) {
            DataBus.removeReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        clear();
        super.finalize();
    }
}
