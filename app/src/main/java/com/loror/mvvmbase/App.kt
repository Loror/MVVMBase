package com.loror.mvvmbase

import android.app.Application
import com.alibaba.fastjson.JSON
import com.loror.lororUtil.http.api.ApiClient
import com.loror.lororUtil.http.api.JsonParser
import com.loror.lororUtil.http.api.TypeInfo
import com.loror.mvvm.utls.AutoSign
import com.loror.mvvm.utls.SharedPreferenceUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPreferenceUtil.init(this)
        ApiClient.setJsonParser(object : JsonParser {

            override fun jsonToObject(json: String?, typeInfo: TypeInfo?): Any {
                val classType = typeInfo!!.type
                if (classType === JSONObject::class.java) {
                    return try {
                        JSONObject(json!!)
                    } catch (e: JSONException) {
                        throw IllegalArgumentException("错误的json格式：$json")
                    }
                }
                if (classType === JSONArray::class.java) {
                    return try {
                        JSONArray(json)
                    } catch (e: JSONException) {
                        throw IllegalArgumentException("错误的json(array)格式：$json")
                    }
                }
                return when (classType) {
                    com.alibaba.fastjson.JSONObject::class.java -> JSON.parseObject(json)
                    com.alibaba.fastjson.JSONArray::class.java -> JSON.parseArray(json)
                    else -> {
                        val bean = JSON.parseObject<Any>(json, classType)
                        AutoSign.sign(bean)
                        bean
                    }
                }
            }

            override fun objectToJson(`object`: Any): String {
                return JSON.toJSONString(`object`)
            }
        })
    }
}