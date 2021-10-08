package com.loror.mvvmbase

import android.app.Application
import com.alibaba.fastjson.JSON
import com.loror.lororUtil.http.api.ApiClient
import com.loror.lororUtil.http.api.JsonParser
import com.loror.lororUtil.http.api.TypeInfo
import com.loror.mvvm.utls.SharedPreferenceUtil


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferenceUtil.init(this)
        ApiClient.setJsonParser(object : JsonParser {

            override fun jsonToObject(json: String?, typeInfo: TypeInfo?): Any {
                return JSON.parseObject(json, typeInfo!!.type)
            }

            override fun objectToJson(`object`: Any): String {
                return JSON.toJSONString(`object`)
            }
        })
    }
}