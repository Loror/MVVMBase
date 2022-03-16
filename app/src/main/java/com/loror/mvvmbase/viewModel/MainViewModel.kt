package com.loror.mvvmbase.viewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.loror.mvvm.annotation.Sign
import com.loror.mvvm.core.MvvmViewModel
import com.loror.mvvmbase.bean.DispatchTest
import com.loror.mvvmbase.bean.Text2
import com.loror.mvvmbase.model.MainModel
import com.loror.mvvmbase.widget.toImage

class MainViewModel : MvvmViewModel(), LifecycleObserver {

    companion object {
        const val EVENT_SHOW_BACK = 1
    }

    @Sign
    private lateinit var model: MainModel

    //Model
    val text1: ObservableField<String> = ObservableField()
    val text2: Text2 = Text2()

    //    val url = "https://iconfont.alicdn.com/t/083f67b8-b930-4a31-8f42-060ce61942f0.png"
    val url = "https://iconfont.alicdn.com/t/083f67b8-b930-4a31-8f42-060ce61942f0.png".toImage()

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
        dispatchLiveDataEvent(EVENT_SHOW_BACK, DispatchTest("1", "${times++}"))
    }

    fun baiduFailed(message: String) {
        failed(message)
    }

    fun baiduSuccess(message: String) {
        success(message)
    }
}