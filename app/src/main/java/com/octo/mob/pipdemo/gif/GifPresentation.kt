package com.octo.mob.pipdemo.gif

import com.octo.mob.pipdemo.GifData

interface GifDisplay{
    fun displayGif(gifData: GifData)
    fun pauseGif()
    fun playGif()
}

class GifPresenterImpl : GifPresenter {

    lateinit var display: GifDisplay

    override fun presentGif(gifData: GifData) {
        display.displayGif(gifData)
    }

    override fun pauseGif() {
        display.pauseGif()
    }

    override fun playGif() {
        display.playGif()
    }
}