package com.loror.mvvm.utls

import android.content.Context
import android.os.Build
import android.view.View
import androidx.fragment.app.Fragment
import com.loror.mvvm.widget.OnSafeClickListener

fun Context.getCompatColor(res: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.getColor(res)
    } else {
        this.resources.getColor(res)
    }
}

fun Fragment.getCompatColor(res: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.requireContext().getColor(res)
    } else {
        this.resources.getColor(res)
    }
}

fun String.mix(transform: ((String) -> String)): String {
    return transform(this)
}

fun String.httpsPrefix(): String {
    if (this.trim().isEmpty() || "/" == this) {
        return ""
    }
    return if (this.startsWith("http")) this else "https:$this"
}

fun String.ossResize(h: Int, w: Int): String {
    if (this.contains("oss")) {
        if (this.contains("x-oss-process=")) {
            return this
        }
        if (this.contains("?")) {
            return "${this}&x-oss-process=image/resize,h_$h,w_$w"
        } else {
            return "${this}?x-oss-process=image/resize,h_$h,w_$w"
        }
    }
    return this
}

fun Long.fillZero(): String {
    if (this < 10) {
        return "0${this}"
    }
    return "$this"
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.setOnSafeClickListener(l: View.OnClickListener) {
    this.setOnClickListener(OnSafeClickListener(l))
}
