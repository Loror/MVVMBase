package com.loror.mvvmbase.widget

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.loror.lororUtil.image.ImageUtil

class Image(var url: String?, @DrawableRes var place: Int = 0, @DrawableRes var error: Int = 0)

fun String.toImage(): Image {
    return Image(this)
}

@BindingAdapter("imageUrl")
fun imageUrl(view: ImageView, image: Image?) {
    image?.apply {
        ImageUtil.with(view.context)
            .from(url)
            .setDefaultImage(place)
            .setErrorImage(error)
            .loadTo(view)
    }
}