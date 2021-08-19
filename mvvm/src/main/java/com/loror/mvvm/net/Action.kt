package com.loror.mvvm.net

import android.text.TextUtils
import android.util.Log
import com.loror.lororUtil.http.api.Observer
import com.loror.lororUtil.http.api.ResultException
import java.lang.Exception
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class Action<T> : Observer<T> {

    companion object {
        const val UNKNOWN = 0B10000000000
        const val DNS_ERROR = 0B11000000000
        const val CONNECT_ERROR = 0B11100000000
        const val CONNECT_TIME_OUT = 0B11110000000
        const val REQUEST_CANCELED = 0B11111000000
        const val UNKNOWN_EXCEPTION = -1
    }

    final override fun failed(code: Int, e: Throwable?) {
        val message = Message()
        if (e is ResultException) {
            val responce = e.responce
            if (responce.throwable is UnknownHostException) {
                message.code = DNS_ERROR
                message.message = "DNS解析异常"
            } else if (responce.throwable is ConnectException) {
                message.code = CONNECT_ERROR
                message.message = "网络连接异常"
            } else if (responce.throwable is SocketTimeoutException) {
                message.code = CONNECT_TIME_OUT
                message.message = "网络连接超时"
            } else if (responce.throwable is SocketException) {
                message.code = REQUEST_CANCELED
                message.message = "网络请求取消"
            } else {
                message.code = UNKNOWN and code
                message.message = responce.toString()
            }
        } else {
            message.code = UNKNOWN_EXCEPTION
            message.message = Log.getStackTraceString(e)
            if (TextUtils.isEmpty(message.message)) {
                message.message = "操作异常"
            }
            e?.printStackTrace()
        }
        failed(message)
    }

    final override fun success(data: T) {
        try {
            result(data)
        } catch (e: Exception) {
            failed(0, e)
        } catch (e: Throwable) {
            failed(0, e)
        }
    }

    abstract fun failed(message: Message)

    abstract fun result(data: T)
}