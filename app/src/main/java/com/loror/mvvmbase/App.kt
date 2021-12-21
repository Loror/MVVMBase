package com.loror.mvvmbase

import com.alibaba.fastjson.JSON
import com.loror.lororUtil.http.api.TypeInfo
import com.loror.mvvm.core.ConfigApplication
import com.loror.mvvm.utls.AutoSign
import com.loror.mvvm.utls.ConfigUtil
import com.loror.mvvmbase.net.ApiConfig
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class App : ConfigApplication() {

    override fun onCreate() {
        super.onCreate()
        ConfigUtil.config(ApiConfig::class.java)
    }

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

    override fun objectToJson(`object`: Any?): String {
        return JSON.toJSONString(`object`)
    }

    override fun getIdClass(): Class<*> {
        return R.id::class.java
    }

    override fun getHalfPixel(): Int {
        return 0
    }
}