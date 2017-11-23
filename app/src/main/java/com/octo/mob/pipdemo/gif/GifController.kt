package com.octo.mob.pipdemo.gif

import com.octo.mob.pipdemo.GifData

interface GifController{
    fun loadGif(gifData: GifData)
    fun loadRandomGif()
    fun pauseGif()
    fun playGif()
}

class GifControllerImpl(private val interactor: GifInteractor) : GifController{
    override fun loadGif(gifData: GifData) {
        interactor.loadGif(gifData)
    }

    override fun loadRandomGif() {
        interactor.loadRandomGif()
    }

    override fun pauseGif() {
        interactor.pauseGif()
    }

    override fun playGif() {
        interactor.playGif()
    }
}
