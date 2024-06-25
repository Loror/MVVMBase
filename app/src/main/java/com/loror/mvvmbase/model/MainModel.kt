package com.loror.mvvmbase.model

import androidx.databinding.ObservableField
import com.loror.mvvm.annotation.Service
import com.loror.mvvmbase.common.bean.Text2
import com.loror.mvvmbase.common.widget.toImage

@Service
class MainModel {
    //Model
    val text1: ObservableField<String> = ObservableField()
    val text2: Text2 = Text2()
    val textShow: ObservableField<String> = ObservableField("回显")

    //    val url = "https://iconfont.alicdn.com/t/083f67b8-b930-4a31-8f42-060ce61942f0.png"
    val url = "http://gips3.baidu.com/it/u=1022347589,1106887837&fm=3028&app=3028&f=JPEG&fmt=auto?w=960&h=1280".toImage()
}
