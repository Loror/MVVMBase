package com.loror.mvvmbase.viewModel

import androidx.lifecycle.LifecycleObserver
import com.loror.mvvm.annotation.Sign
import com.loror.mvvm.core.MvvmViewModel
import com.loror.mvvmbase.model.MainModel
import com.loror.mvvmbase.model.MainNetModel

class MainViewModel : MvvmViewModel(), LifecycleObserver {

    companion object {
        const val EVENT_CROSS = 10
    }

    @Sign
    private lateinit var netModel: MainNetModel

    @Sign
    private lateinit var model: MainModel

    private var times = 1

    init {
        setMultiMode(true)
    }

    fun netBaidu() {
        netModel.netBaidu()
    }

    fun showBack() {
        model.textShow.set("回显次数：${times++}")
    }

    fun baiduFailed(message: String) {
        failed(message)
    }

    fun baiduSuccess(message: String) {
        success(message)
    }

    fun cross() {
        dispatchLiveDataEvent(EVENT_CROSS, "通知fragment！")
    }
}