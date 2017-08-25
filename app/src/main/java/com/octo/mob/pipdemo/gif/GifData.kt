package com.octo.mob.pipdemo.gif

import com.octo.mob.pipdemo.R

data class GifData(val title: String, val resId: Int)

object GifCollection{
    fun getAllGifs() : List<GifData>{
        return listOf(
                GifData("Hello this is Dog", R.drawable.hello_dog),
                GifData("Hi Pusheen", R.drawable.hello_pusheen),
                GifData("OMG Cat", R.drawable.omgcat),
                GifData("Mind Blown", R.drawable.mind_blown)
        )
    }
}