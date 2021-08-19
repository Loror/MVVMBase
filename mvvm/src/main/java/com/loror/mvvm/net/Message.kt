package com.loror.mvvm.net

class Message {
    var code = 0
    var message: String? = null
        get() = if (field == null) "操作失败" else field
}