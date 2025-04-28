# 基于mvvm的Android快速开发框架

[![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## Studio中引入项目 

```
dependencies {
    implementation 'com.github.Loror:MVVMBase:1.3.16'
}

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

可参考demo使用，使用时需继承MvvmSignActivity/MvvmActivity，MvvmSignFragment/MvvmFragment，MvvmDialog，ConfigApplication，MvvmViewModel，MvvmModel使用。

## 主框架

* 注解LiveDataEvent
    * 基于liveData接收数据，可接收一个数据

* MvvmViewModel
    * 基于liveData分发数据
    * dispatchLiveDataEvent(int event, Object data) 分发数据，通过event匹配，发送数据类型与接收类型必须一致
    * showProgress(String message) 显示loading弹窗
    * dismissProgress() 关闭loading弹窗
    * success(String message) 成功时回调方法，同时会触发dismissProgress，请覆写该方法触发Toast等内容
    * failed(String message) 失败时回调方法，同时会触发dismissProgress，请覆写该方法触发Toast等内容

* 示例代码
* Activity中
```
    @LiveDataEvent(MvvmViewModel.EVENT_MESSAGE)
    fun success(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
```
* ViewModel中
```
    dispatchLiveDataEvent(MvvmViewModel.EVENT_MESSAGE, "发送消息")
```

* 注解Sign
    * 自动装载数据

* 注解Config
    * 配置自动装载数据、异常接收、默认loading弹窗等

* 示例代码
* Application中
```
    //配置自动装载全局对象
    @Config
    fun configGlobalData(): GlobalData {
        return globalData
    }

    //配置处理框架内部异常
    @Config
    fun handlerException(t: Throwable) {
        Log.e("App", "e:", t)
    }
    
    //配置loading弹窗样式
    @Config
    fun configProgress(activity: Activity): ProgressDialog {
        return ProgressViewDialog(activity)
    }
    
    @Config
    fun configProgress(fragment: Fragment): ProgressDialog {
        return ProgressViewDialog(fragment.requireContext())
    }
```

* Activity中
```
    @Sign
    private lateinit var binding: ActivityMainBinding

    @Sign
    private lateinit var viewModel: MainViewModel
```

</br>
内部已引入库LororUtil(https://github.com/Loror/LororUtil)  
</br>
推荐图片压缩框架Luban(https://github.com/Curzibn/Luban)
</br>
推荐下拉刷新框架SmartRefreshLayout(https://github.com/scwang90/SmartRefreshLayout)

License
-------

    Copyright 2021 Loror

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
