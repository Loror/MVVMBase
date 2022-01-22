package com.loror.mvvmbase.model

import com.loror.lororUtil.http.Responce
import com.loror.mvvm.annotation.Service
import com.loror.mvvm.annotation.Sign
import com.loror.mvvm.net.Action
import com.loror.mvvm.net.Message
import com.loror.mvvmbase.service.ServiceTest
import com.loror.mvvmbase.viewModel.MainViewModel

@Service(intoPool = false)
class MainModel(private val viewModel: MainViewModel) : BaseModel() {

    @Sign
    lateinit var serviceTest: ServiceTest

    fun netBaidu() {
        serviceTest.baidu()
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