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
import android.widget.Toast
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
        fun viewGif(context: Context, gifResId: Int): Intent {
            return buildIntent(context, GifAction.View, gifResId)
        }

        fun randomGif(context: Context): Intent {
            return buildIntent(context, GifAction.ViewRandom, 0)
        }

        fun play(context: Context): Intent {
            return buildIntent(context, GifAction.Play, 0)
        }

        fun pause(context: Context): Intent {
            return buildIntent(context, GifAction.Pause, 0)
        }

        private fun buildIntent(context: Context, gifAction: GifAction, gifResId: Int): Intent {
            val intent = Intent(gifAction.action)
            intent.setClass(context, GifActivity::class.java)
            intent.putExtra("gif", gifResId)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif)
        setSupportActionBar(toolbar)

        onNewIntent(intent)

        fab.setOnClickListener { goToPictureInPictureMode() }
        gifLayout.setOnClickListener {
            togglePlayPause()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        var gifResId = 0
        when (intent?.action) {
            GifAction.View.action -> {
                gifResId = intent.getIntExtra("gif", R.drawable.hello_dog)
            }
            GifAction.ViewRandom.action -> {
                val gifDataList = GifCollection.getAllGifs()
                val randomIndex = Random().nextInt(gifDataList.size)
                gifResId = gifDataList[randomIndex].resId
            }
            GifAction.Play.action -> {
                startGif()
            }
            GifAction.Pause.action -> {
                stopGif()
            }
        }

        if (gifResId > 0) {
            Glide.with(this)
                    .load(gifResId)
                    .into(gifView)
            updateOverlayVisibility(false)
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

    fun updateOverlayVisibility(isVisible : Boolean){
        pauseOverlay.visibility = when(isVisible){
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

    fun buildPictureInPictureParams(): PictureInPictureParams {
        // Create randomizer action
        val randomizerIntent = IntentBuilder.randomGif(this)
        val randomizerAction = RemoteAction(
                Icon.createWithResource(this, android.R.drawable.ic_menu_rotate),
                "Random",
                "Random",
                PendingIntent.getActivity(this, 0, randomizerIntent, 0)
        )

        // Create play/pause action
        val isPlaying = gifView.isGifRunning()
        val playPauseIntent = when (isPlaying) {
            true -> IntentBuilder.pause(this)
            false -> IntentBuilder.play(this)
        }
        val playPauseIcon = Icon.createWithResource(this, when (isPlaying) {
            true -> android.R.drawable.ic_media_pause
            false -> android.R.drawable.ic_media_play
        })
        val playPauseAction = RemoteAction(
                playPauseIcon,
                "Play/Pause",
                "Play/Pause",
                PendingIntent.getActivity(this, 0, playPauseIntent, 0))

        return PictureInPictureParams.Builder()
                .setAspectRatio(Rational.parseRational("3:4"))
                .setActions(listOf(randomizerAction, playPauseAction))
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
