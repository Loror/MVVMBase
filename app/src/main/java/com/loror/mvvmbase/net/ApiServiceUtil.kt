package com.loror.rembercard.net

import android.util.Log
import com.loror.lororUtil.http.HttpClient
import com.loror.lororUtil.http.api.*
import com.loror.mvvmbase.net.ServiceApi

/**
 * Date: 2019/3/18 15:41
 * Description: ${DESCRIPTION}
 */
object ApiServiceUtil {

    /**
     * 获取ApiService
     */
    val serviceApi: ServiceApi
        get() = ApiClient()
            .setOnRequestListener(
                MultiOnRequestListener()
                    .addOnRequestListener(CookieRequestListener())
                    .addOnRequestListener(object : OnRequestListener {
                        override fun onRequestBegin(client: HttpClient, request: ApiRequest) {
                            client.timeOut = 15000
                            client.readTimeOut = 50000
                        }

                        override fun onRequestEnd(client: HttpClient, result: ApiResult) {
                            if (result.isMock) {
                                return
                            }
                            val responce = result.responce
                            val url = result.url
                            val params = result.params
                            Log.e("RESULT_", "url:$url")
                            Log.e("RESULT_", "param:$params")
                            val resultStr = responce.toString()
                            Log.e("RESULT_", "response:" + responce.code + " = " + resultStr)
                        }
                    })
            )
            .create(ServiceApi::class.java)
}