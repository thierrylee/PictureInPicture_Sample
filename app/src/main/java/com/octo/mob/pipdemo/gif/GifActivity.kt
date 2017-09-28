package com.octo.mob.pipdemo.gif

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Rational
import android.view.Menu
import android.view.View
import com.bumptech.glide.Glide
import com.octo.mob.pipdemo.R
import com.octo.mob.pipdemo.extensions.isGifRunning
import com.octo.mob.pipdemo.extensions.startGif
import com.octo.mob.pipdemo.extensions.stopGif
import kotlinx.android.synthetic.main.activity_gif.*
import kotlinx.android.synthetic.main.activity_gif_content.*
import java.util.*

private enum class GifAction(val action: String) {
    View("View"),
    ViewRandom("ViewRandom"),
    Play("Play"),
    Pause("Pause")
    ;
}

private enum class GifState {
    Play,
    Pause
}

class GifActivity : AppCompatActivity() {

    private var gifStateBeforePause: GifState? = null

    object IntentBuilder {
        fun viewGif(context: Context, gifData: GifData): Intent = buildIntent(context, GifAction.View, gifData)
        fun randomGif(context: Context): Intent = buildIntent(context, GifAction.ViewRandom)
        fun play(context: Context): Intent = buildIntent(context, GifAction.Play)
        fun pause(context: Context): Intent = buildIntent(context, GifAction.Pause)

        private fun buildIntent(context: Context, gifAction: GifAction, gifData: GifData? = null): Intent {
            val intent = Intent(gifAction.action)
            intent.setClass(context, GifActivity::class.java)
            intent.putExtra("gif", gifData)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif)
        setSupportActionBar(toolbar)

        onNewIntent(intent)

        fab.setOnClickListener { goToPictureInPictureMode() }
        gifLayout.setOnClickListener { togglePlayPause() }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        var gifData : GifData? = null
        when (intent?.action) {
            GifAction.View.action -> gifData = intent.getParcelableExtra("gif")
            GifAction.ViewRandom.action -> {
                val gifDataList = GifCollection.getAllGifs()
                val randomIndex = Random().nextInt(gifDataList.size)
                gifData = gifDataList[randomIndex]
            }
            GifAction.Play.action -> startGif()
            GifAction.Pause.action -> stopGif()
        }

        gifData?.let {
            Glide.with(this)
                    .load(it.resId)
                    .into(gifView)
            updateOverlayVisibility(false)
            gifView.contentDescription = it.title
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onPause() {
        if (!isInPictureInPictureMode) {
            gifStateBeforePause = when (gifView.isGifRunning()) {
                true -> GifState.Play
                false -> GifState.Pause
            }
            stopGif()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        gifStateBeforePause?.let {
            when (it) {
                GifState.Play -> startGif()
                GifState.Pause -> stopGif()
            }
            gifStateBeforePause = null
        }
    }

    fun togglePlayPause() {
        val isGifRunning = gifView.isGifRunning()
        when (isGifRunning) {
            true -> gifView.stopGif()
            else -> gifView.startGif()
        }
        updateOverlayVisibility(isGifRunning)
    }

    fun startGif() {
        gifView.startGif()
        updateOverlayVisibility(false)
    }

    fun stopGif() {
        gifView.stopGif()
        updateOverlayVisibility(true)
    }

    fun updateOverlayVisibility(isVisible: Boolean) {
        pauseOverlay.visibility = when (isVisible) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

    private fun buildRandomizerAction(): RemoteAction {
        val randomizerIntent = IntentBuilder.randomGif(this)
        return RemoteAction(
                Icon.createWithResource(this, android.R.drawable.ic_menu_rotate),
                "Random",
                "Random",
                PendingIntent.getActivity(this, 0, randomizerIntent, 0)
        )
    }

    private fun buildPlayPauseAction(): RemoteAction {
        val isPlaying = gifView.isGifRunning()
        val playPauseIntent = when (isPlaying) {
            true -> IntentBuilder.pause(this)
            false -> IntentBuilder.play(this)
        }
        val playPauseIcon = Icon.createWithResource(this, when (isPlaying) {
            true -> android.R.drawable.ic_media_pause
            false -> android.R.drawable.ic_media_play
        })
        return RemoteAction(
                playPauseIcon,
                "Play/Pause",
                "Play/Pause",
                PendingIntent.getActivity(this, 0, playPauseIntent, 0))
    }

    fun buildPictureInPictureParams(): PictureInPictureParams {
        val imgWidth = gifView.measuredWidth
        val imgHeight = gifView.measuredHeight
        val aspectRatio: String = Integer.toString(imgWidth) + ":" + Integer.toString(imgHeight)

        return PictureInPictureParams.Builder()
                .setAspectRatio(Rational.parseRational(aspectRatio))
                .setActions(listOf(buildRandomizerAction(), buildPlayPauseAction()))
                .build()
    }

    fun goToPictureInPictureMode() {
        enterPictureInPictureMode(buildPictureInPictureParams())
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        if (isInPictureInPictureMode) {
            fab.hide()
            toolbar.visibility = View.GONE
        } else {
            fab?.show()
            toolbar.visibility = View.VISIBLE
        }
    }

}
