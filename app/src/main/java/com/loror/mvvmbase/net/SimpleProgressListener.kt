package com.loror.rembercard.net

import com.loror.lororUtil.http.ProgressListener

abstract class SimpleProgressListener : ProgressListener {
    override fun failed() {
        transing(-2f, 0, 0)
    }

    override fun finish(result: String) {
        transing(-1f, 0, 0)
    }
}