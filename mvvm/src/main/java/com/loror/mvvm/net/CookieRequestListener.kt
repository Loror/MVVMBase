package com.loror.mvvm.net

import android.util.Log
import com.loror.lororUtil.http.Cookies
import com.loror.lororUtil.http.HttpClient
import com.loror.lororUtil.http.api.ApiRequest
import com.loror.lororUtil.http.api.ApiResult
import com.loror.lororUtil.http.api.OnRequestListener

class CookieRequestListener : OnRequestListener {
    private val cookies = Cookies()
    override fun onRequestBegin(client: HttpClient, request: ApiRequest) {
        if (cookies.keys().size > 0) {
            request.params.addCookies(cookies)
        }
    }

    override fun onRequestEnd(client: HttpClient, result: ApiResult) {
        if (result.isMock) {
            return
        }
        val setCookies = result.responce.cookies
        if (setCookies != null) {
            for (setCookie in setCookies) {
                cookies.addCookie(setCookie.name, setCookie.value)
                Log.e("RESULT_", "cookie:$setCookie")
            }
        }
    }
}