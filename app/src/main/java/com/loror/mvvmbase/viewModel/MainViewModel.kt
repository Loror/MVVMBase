package com.loror.mvvmbase.viewModel

import androidx.lifecycle.LifecycleObserver
import com.loror.mvvm.annotation.Sign
import com.loror.mvvm.core.MvvmViewModel
import com.loror.mvvmbase.model.MainModel
import com.loror.mvvmbase.model.MainNetModel

class MainViewModel : MvvmViewModel(), LifecycleObserver {

    @Sign
    private lateinit var netModel: MainNetModel

    @Sign
    private lateinit var model: MainModel

    private var times = 1

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
}