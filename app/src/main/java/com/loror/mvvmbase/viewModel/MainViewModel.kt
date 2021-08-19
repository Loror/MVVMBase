package com.loror.mvvmbase.viewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.loror.lororUtil.http.Responce
import com.loror.mvvm.core.BaseViewModel
import com.loror.mvvm.net.Action
import com.loror.mvvm.net.Message
import com.loror.mvvmbase.bean.Text2
import com.loror.mvvmbase.model.MainModel
import com.loror.rembercard.net.ApiServiceUtil

class MainViewModel : BaseViewModel(), LifecycleObserver {

    companion object {
        const val EVENT_SHOW_BACK = 1
    }

    private val model = MainModel(this)

    //Model
    val text1: ObservableField<String> = ObservableField()
    val text2: Text2 = Text2()

    private var times = 1

    //lifecycle注册
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun lifeCycleOnCreate() {
        text1.set("123")
        text2.setText2("456")
    }

    fun netBaidu() {
        model.netBaidu()
    }

    fun showBack() {
        dispatchLiveDataEvent(EVENT_SHOW_BACK, "回显次数：${times++}")
    }

    fun baiduFailed(message: String) {
        failed(message)
    }

    fun baiduSuccess(message: String) {
        success(message)
    }
}