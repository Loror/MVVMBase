package com.loror.mvvm.net

import com.loror.lororUtil.annotation.MocKType
import com.loror.lororUtil.http.HttpClient
import com.loror.lororUtil.http.api.ApiRequest
import com.loror.lororUtil.http.api.ApiResult
import com.loror.lororUtil.http.api.OnRequestListener
import com.loror.mvvm.utls.L

class LogRequestListener(private val debug: Boolean) : OnRequestListener {

    override fun onRequestBegin(client: HttpClient, request: ApiRequest) {

    }

    override fun onRequestEnd(client: HttpClient, result: ApiResult) {
        if (result.isMock && result.mockType != MocKType.NET) {
            return
        }
        var method = ""
        when (result.apiRequest.type) {
            1 -> {
                method = "GET"
            }
            2 -> {
                method = "POST"
            }
            3 -> {
                method = "PUT"
            }
            4 -> {
                method = "DELETE"
            }
        }
        if (debug) {
            val responce = result.responce
            val url = result.url
            val params = result.apiRequest.params
            val resultStr = if (responce.inputStream == null) responce.toString() else "stream"
            L.e(
                "RESULT_",
                "url:$url $method\nparam:$params\nresponse:${responce.code} ${resultStr}"
            )
        }
    }

}