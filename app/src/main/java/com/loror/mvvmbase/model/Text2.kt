package com.loror.mvvmbase.model

import androidx.databinding.BaseObservable

//Model
class Text2 : BaseObservable() {
    private var text2: String? = null

    fun setText2(text2: String?) {
        this.text2 = text2
        notifyChange()
    }

    fun getText2(): String? {
        return text2
    }
}