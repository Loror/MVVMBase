package com.loror.mvvmbase.viewModel

import androidx.databinding.ObservableField
import androidx.lifecycle.*
import com.loror.lororUtil.http.Responce
import com.loror.lororUtil.http.api.Observer
import com.loror.mvvmbase.model.Text2
import com.loror.mvvmbase.util.MutableLiveData
import com.loror.rembercard.net.Action
import com.loror.rembercard.net.ApiServiceUtil
import com.loror.rembercard.net.Message

class MainViewModel : ViewModel(), LifecycleObserver {

    //Model
    val text1: ObservableField<String> = ObservableField()
    val text2: Text2 = Text2()

    val liveData: MutableLiveData<String> = MutableLiveData()

    //lifecycle注册
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun lifeCycleOnCreate() {
        text1.set("123")
        text2.setText2("456")
    }

    fun netBaidu() {
        ApiServiceUtil.serviceApi.baidu()
            .subscribe(object : Action<Responce>() {
                override fun success(data: Responce?) {
                    liveData.setValue("访问百度成功")
                }

                override fun failed(message: Message) {
                    liveData.setValue("访问百度失败:" + message.message)
                }
            })
    }
}