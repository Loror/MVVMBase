package com.loror.mvvmbase.model

import com.loror.lororUtil.http.Responce
import com.loror.mvvm.net.Action
import com.loror.mvvm.net.Message
import com.loror.mvvmbase.viewModel.MainViewModel
import com.loror.rembercard.net.ApiServiceUtil

class MainModel(private val viewModel: MainViewModel) {

    fun netBaidu() {
        ApiServiceUtil.serviceApi.baidu()
            .subscribe(object : Action<Responce>() {

                override fun failed(message: Message) {
                    viewModel.baiduFailed("访问百度失败:" + message.message)
                }

                override fun result(data: Responce) {
                    viewModel.baiduSuccess("访问百度成功")
                }
            })
    }
}