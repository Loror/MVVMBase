package com.loror.mvvm.widget

import android.content.Context
import android.text.Html
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.loror.mvvm.R

/**
 * Created by Loror on 2017/10/26.
 * 支持部分文字颜色不同的TextView
 */
class ColorTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var specialTextColor: Int
    private var delay: Runnable? = null

    init {
        specialTextColor = currentTextColor
        attrs?.let {
            val array = context.obtainStyledAttributes(it, R.styleable.ColorTextView)
            specialTextColor =
                array.getColor(R.styleable.ColorTextView_specialTextColor, currentTextColor)
            array.recycle()
        }
        delay?.run()
        delay = null
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        if (text != null && text.javaClass == String::class.java) {
            val run = Runnable {
                super.setText(Html.fromHtml(text.toString()
                    .replace("||", "<br>")
                    .replace("((", "<font color=\"$specialTextColor\">")
                    .replace("))", "</font>")), type)
            }
            if (specialTextColor != 0) {
                run.run()
            } else {
                delay = run
            }
            return
        }
        super.setText(text, type)
    }
}
