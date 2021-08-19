package com.loror.rembercard.net

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.lang.reflect.Type

class Result {
    var code = 0
    var data: String? = null
    var msg: String? = null
    private var suitePhpEmptyObject = false

    fun setSuitePhpEmptyObject(suitePhpEmptyObject: Boolean) {
        this.suitePhpEmptyObject = suitePhpEmptyObject
    }

    fun obtainJSONObject(): JSONObject {
        if (suitePhpEmptyObject) {
            if ("[]" == data) {
                return JSONObject()
            }
        }
        return JSONObject.parseObject(data)
    }

    fun obtainJSONArray(): JSONArray {
        return JSONObject.parseArray(data)
    }

    fun <T> obtainObject(type: Class<T>?): T {
        return JSON.parseObject(data, type)
    }

    fun <T> obtainList(type: Class<T>?): List<T> {
        return JSON.parseArray(data, type)
    }

    fun obtainTypeObject(type: Type?): Any {
        return JSON.parseObject(data, type)
    }
}