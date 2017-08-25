package com.octo.mob.pipdemo.extensions

import android.widget.ImageView
import com.bumptech.glide.load.resource.gif.GifDrawable

fun ImageView.getGifDrawable() : GifDrawable? {
    var gifDrawable : GifDrawable? = null
    this.drawable?.let {
        if (it is GifDrawable){
            gifDrawable = it
        }
    }
    return gifDrawable
}

fun ImageView.isGifRunning() : Boolean = getGifDrawable()?.isRunning ?: true

fun ImageView.stopGif() = getGifDrawable()?.stop()

fun ImageView.viewGif() = getGifDrawable()?.start()