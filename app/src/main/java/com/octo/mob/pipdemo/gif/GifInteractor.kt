package com.octo.mob.pipdemo.gif

import com.octo.mob.pipdemo.GifCollection
import com.octo.mob.pipdemo.GifData
import java.util.*

interface GifInteractor{
    fun loadGif(gifData: GifData)
    fun loadRandomGif()
    fun pauseGif()
    fun playGif()
}

interface GifPresenter{
    fun presentGif(gifData: GifData)
    fun pauseGif()
    fun playGif()
}

class GifInteractorImpl(private val presenter: GifPresenter) : GifInteractor{

    override fun loadGif(gifData: GifData) {
        presenter.presentGif(gifData)
        presenter.playGif()
    }

    override fun loadRandomGif() {
        val gifDataList = GifCollection.getAllGifs()
        val randomIndex = Random().nextInt(gifDataList.size)
        loadGif(gifDataList[randomIndex])
    }
    override fun pauseGif() {
        presenter.pauseGif()
    }

    override fun playGif() {
        presenter.playGif()
    }
}