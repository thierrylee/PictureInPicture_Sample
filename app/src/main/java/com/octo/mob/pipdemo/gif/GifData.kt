package com.octo.mob.pipdemo.gif

import com.octo.mob.pipdemo.R

data class GifData(val title: String, val resId: Int)

object GifCollection{
    fun getAllGifs() : List<GifData>{
        return listOf(
                GifData("Hello this is Dog", R.drawable.hello_dog),
                GifData("Hi Pusheen", R.drawable.hello_pusheen),
                GifData("OMG Cat", R.drawable.omgcat),
                GifData("Mind Blown", R.drawable.mind_blown),
                GifData("Fail fox", R.drawable.fail_fox),
                GifData("DJ Cat", R.drawable.dj_cat),
                GifData("Goal Cat", R.drawable.goal_cat),
                GifData("Supa Hot Fire", R.drawable.supa_hot_fire),
                GifData("OMG Cat 2", R.drawable.omgcat2),
                GifData("YPDP", R.drawable.ypdp)
        )
    }
}