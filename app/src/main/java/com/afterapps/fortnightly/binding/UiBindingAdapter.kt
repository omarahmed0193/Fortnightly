package com.afterapps.fortnightly.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("imgUrl")
fun bindImgUrlToImageView(imageView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        imageView.load(it) {
            crossfade(true)
        }
    }
}