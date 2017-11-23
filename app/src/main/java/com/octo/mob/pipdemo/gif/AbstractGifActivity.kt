package com.octo.mob.pipdemo.gif

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.octo.mob.pipdemo.GifData
import com.octo.mob.pipdemo.R
import com.octo.mob.pipdemo.extensions.isGifRunning
import com.octo.mob.pipdemo.extensions.pauseGif
import com.octo.mob.pipdemo.extensions.playGif
import kotlinx.android.synthetic.main.activity_gif.*
import kotlinx.android.synthetic.main.activity_gif_content.*

enum class GifAction(val action: String) {
    View("View"),
    ViewRandom("ViewRandom"),
    Play("Play"),
    Pause("Pause")
    ;
}

enum class GifState {
    Play,
    Pause
}

/**
 * Handles only Gif related tasks
 */
abstract class AbstractGifActivity : AppCompatActivity(), GifDisplay {

    protected lateinit var controller: GifController
    private var gifStateBeforePause: GifState? = null

    abstract protected fun onPause(gifStateBeforePause: GifState)
    abstract protected fun onResume(gifStateBeforePause: GifState?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDependencies()
        setupUi()
        onNewIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPause() {
        gifStateBeforePause = when (gifView.isGifRunning()) {
            true -> GifState.Play
            false -> GifState.Pause
        }
        onPause(gifStateBeforePause!!)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        onResume(gifStateBeforePause)
        gifStateBeforePause = null
    }

    override fun displayGif(gifData: GifData) {
        Glide.with(this)
                .load(gifData.resId)
                .into(gifView)
        gifView.contentDescription = gifData.title
    }

    override fun pauseGif() {
        gifView.pauseGif()
        updateOverlayVisibility(true)
    }

    override fun playGif() {
        gifView.playGif()
        updateOverlayVisibility(false)
    }

    private fun togglePlayPause() {
        val isGifRunning = gifView.isGifRunning()
        when (isGifRunning) {
            true -> pauseGif()
            else -> playGif()
        }
    }

    private fun updateOverlayVisibility(isVisible: Boolean) {
        pauseOverlay.visibility = when (isVisible) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

    private fun setupDependencies() {
        val presenter = GifPresenterImpl()
        presenter.display = this
        val interactor = GifInteractorImpl(presenter)
        controller = GifControllerImpl(interactor)
    }

    open protected fun setupUi() {
        setContentView(R.layout.activity_gif)
        setSupportActionBar(toolbar)
        gifLayout.setOnClickListener { togglePlayPause() }
    }
}