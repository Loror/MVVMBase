package com.loror.rembercard.net

import com.alibaba.fastjson.JSON
import com.loror.lororUtil.http.api.Observer
import com.loror.lororUtil.http.api.ResultException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class Action<T> : Observer<T> {
    override fun failed(code: Int, e: Throwable) {
        val message = Message()
        if (e is ResultException) {
            val responce = e.responce
            if (responce.throwable is UnknownHostException) {
                message.code = 1
                message.message = "DNS解析异常"
            } else if (responce.throwable is ConnectException) {
                message.code = 1
                message.message = "网络连接异常"
            } else if (responce.throwable is SocketTimeoutException) {
                message.code = 1
                message.message = "网络连接超时"
            } else if (responce.throwable is SocketException) {
                message.code = 1
                message.message = "网络请求取消"
            } else {
                val json = responce.toString()
                try {
                    val jsonObject = JSON.parseObject(json)
                    message.code = jsonObject.getIntValue("code")
                    message.message = jsonObject.getString("msg")
                } catch (t: Exception) {
                    message.code = code
                    message.message = json
                }
            }
        } else {
            message.code = -1
            message.message = e.toString() ?: "操作异常"
        }
        failed(message)
    }

    abstract fun failed(message: Message)
}