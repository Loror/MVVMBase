package com.loror.mvvm.net

import com.loror.lororUtil.http.ProgressListener

abstract class SimpleProgressListener : ProgressListener {

    override fun finish(success: Boolean) {
        if (success) {
            transing(-1f, 0, 0)
        } else {
            transing(-2f, 0, 0)
        }
    }
}