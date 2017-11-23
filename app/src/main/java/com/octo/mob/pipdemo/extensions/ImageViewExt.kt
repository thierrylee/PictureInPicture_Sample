package com.octo.mob.pipdemo.extensions

import android.widget.ImageView
import com.bumptech.glide.load.resource.gif.GifDrawable

fun ImageView.getGifDrawable(): GifDrawable? {
    this.drawable?.let {
        if (it is GifDrawable) {
            return it
        }
    }
    return null
}

fun ImageView.isGifRunning(): Boolean = getGifDrawable()?.isRunning ?: true

fun ImageView.pauseGif() = getGifDrawable()?.stop()

fun ImageView.playGif() = getGifDrawable()?.start()