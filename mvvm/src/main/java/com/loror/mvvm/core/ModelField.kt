package com.loror.mvvm.core

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.Checkable
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.loror.lororUtil.image.ImageUtil
import com.loror.mvvm.utls.Logger
import java.lang.ref.WeakReference
import kotlin.reflect.KProperty

object Model {

    /**
     * 自定义代理其他view绑定行为
     * */
    var modelAction: ModelAction? = null
}

interface ModelAction {

    fun onSetValue(view: View, data: Any?): Boolean

    fun onGetValue(): Any?
}

fun <T> model(data: T, view: View): ModelField<T> = ModelField(data, view)

class ModelField<T>(private var data: T, view: View) {

    private val v = WeakReference(view)

    init {
        setView(view, data)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val view = v.get()
        if (view is EditText) {
            try {
                return view.text.toString() as T
            } catch (e: ClassCastException) {
                Logger.w("无法转换EditText内容", Log.getStackTraceString(e))
            }
        } else if (data is Boolean) {
            if (view is Checkable) {
                try {
                    return view.isChecked as T
                } catch (e: ClassCastException) {
                    Logger.w("无法转换Checkable状态", Log.getStackTraceString(e))
                }
            }
        } else if (Model.modelAction != null) {
            try {
                return Model.modelAction!!.onGetValue() as T
            } catch (e: ClassCastException) {
                Logger.w("无法转换modelAction状态", Log.getStackTraceString(e))
            }
        }
        return data
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        if (data is MutableList<*>) {
            val value = newValue as? Collection<*>
            (data as MutableList<*>).apply {
                clear()
                if (value != null) {
                    addAll(value as Collection<Nothing>)
                }
            }
        } else {
            data = newValue
        }
        v.get()?.apply {
            setView(this, data)
        }
    }

    private fun setView(view: View, data: T) {
        if (Model.modelAction?.onSetValue(view, data) == true) {
            return
        }
        if (data is Boolean) {
            when (view) {
                is Checkable -> {
                    view.isChecked = data
                }

                else -> {
                    view.visibility = if (data) View.VISIBLE else View.GONE
                }
            }
        } else {
            when (view) {
                is TextView -> {
                    view.text = data.toString()
                }

                is AbsListView -> {
                    (view.adapter as? BaseAdapter)?.notifyDataSetChanged()
                }

                is RecyclerView -> {
                    view.adapter?.notifyDataSetChanged()
                }

                is ImageView -> {
                    val url = data.toString()
                    if (!TextUtils.isEmpty(url) && url != "null") {
                        ImageUtil.with(view.context)
                            .setIsGif(true)
                            .from(url)
                            .loadTo(view)
                    } else {
                        ImageUtil.releaseTag(view)
                    }
                }

                else -> {
                    Logger.i("不支持的类型：${view.javaClass.name} -> $data")
                }
            }
        }
    }

}