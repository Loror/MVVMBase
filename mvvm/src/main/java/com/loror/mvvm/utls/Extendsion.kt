package com.loror.mvvm.utls

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.TypedValue
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

fun Context.getCompatDrawable(res: Int): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.getDrawable(res)!!
    } else {
        this.resources.getDrawable(res)
    }
}

fun Fragment.getCompatDrawable(res: Int): Drawable {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.requireContext().getDrawable(res)!!
    } else {
        this.resources.getDrawable(res)
    }
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

/**
 * 获取dp值
 * */
val Int.dp
    get() = this.toFloat().dp.toInt()

/**
 * 获取dp值
 * */
val Float.dp
    get() = getTypedValue(TypedValue.COMPLEX_UNIT_DIP, this)

/**
 * 获取sp值
 * */
val Float.sp
    get() = getTypedValue(TypedValue.COMPLEX_UNIT_SP, this)

private fun getTypedValue(unit: Int, value: Float): Float {
    return TypedValue.applyDimension(unit, value, Resources.getSystem().displayMetrics)
}

fun View.setOnSafeClickListener(l: View.OnClickListener) {
    this.setOnClickListener(OnSafeClickListener(l))
}

fun View.setOnSafeClickListener(space: Int, l: View.OnClickListener) {
    this.setOnClickListener(OnSafeClickListener(space, l))
}
