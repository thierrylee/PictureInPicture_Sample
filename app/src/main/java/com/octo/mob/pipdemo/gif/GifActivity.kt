package com.octo.mob.pipdemo.gif

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.util.Rational
import android.view.View
import com.octo.mob.pipdemo.GifData
import com.octo.mob.pipdemo.R
import com.octo.mob.pipdemo.extensions.isGifRunning
import com.octo.mob.pipdemo.service.GifService
import kotlinx.android.synthetic.main.activity_gif.*
import kotlinx.android.synthetic.main.activity_gif_content.*

class GifActivity : AbstractGifActivity() {

    private val receiver = RemoteActionBroadcastReceiver()

    object IntentBuilder {
        val GIF_EXTRA_KEY: String = "GIF"

        fun viewGif(context: Context, gifData: GifData): Intent = buildIntent(context, GifAction.View, gifData)
        fun randomGif(context: Context): Intent = buildIntent(context, GifAction.ViewRandom)
        fun play(context: Context): Intent = buildIntent(context, GifAction.Play)
        fun pause(context: Context): Intent = buildIntent(context, GifAction.Pause)

        private fun buildIntent(context: Context, gifAction: GifAction, gifData: GifData? = null): Intent {
            val intent = Intent(gifAction.action)
            intent.setClass(context, GifActivity::class.java)
            intent.putExtra(GIF_EXTRA_KEY, gifData)
            return intent
        }
    }

    override fun setupUi() {
        super.setupUi()
        fab.setOnClickListener { goToPictureInPictureMode() }
        val intentFilter = IntentFilter()
        intentFilter.addAction(GifAction.ViewRandom.action)
        intentFilter.addAction(GifAction.Play.action)
        intentFilter.addAction(GifAction.Pause.action)
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        when (intent?.action) {
            GifAction.View.action -> controller.loadGif(intent.getParcelableExtra(GifActivity.IntentBuilder.GIF_EXTRA_KEY))
            GifAction.ViewRandom.action -> controller.loadRandomGif()
            GifAction.Play.action -> controller.playGif()
            GifAction.Pause.action -> controller.pauseGif()
        }
    }

    override fun onPause(gifStateBeforePause: GifState) {
        if (!isInPictureInPictureMode) {
            pauseGif()
        }
    }

    override fun onResume(gifStateBeforePause: GifState?) {
        gifStateBeforePause?.let {
            when (it) {
                GifState.Play -> playGif()
                GifState.Pause -> pauseGif()
            }
        }
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration?) {
        if (isInPictureInPictureMode) {
            fab.hide()
            toolbar.visibility = View.GONE
        } else {
            fab.show()
            toolbar.visibility = View.VISIBLE
        }
    }

    public override fun onUserLeaveHint() {
        if (true) { // For instance, we could enter PiP only if GIF is playing
            goToPictureInPictureMode()
        }
    }

    private fun goToPictureInPictureMode() {
        enterPictureInPictureMode(buildPictureInPictureParams())
    }

    private fun buildPictureInPictureParams(): PictureInPictureParams {
        val imgWidth = gifView.drawable.intrinsicWidth
        val imgHeight = gifView.drawable.intrinsicHeight
        val aspectRatio: String = Integer.toString(imgWidth) + ":" + Integer.toString(imgHeight)

        return PictureInPictureParams.Builder()
                .setAspectRatio(Rational.parseRational(aspectRatio))
                .setActions(listOf(buildRandomizerAction(), buildPlayPauseAction()))
                .build()
    }

    private fun buildRandomizerAction(): RemoteAction {
        val randomizerIntent = IntentBuilder.randomGif(this)
        randomizerIntent.setClass(this, GifService::class.java)
        return RemoteAction(
                Icon.createWithResource(this, android.R.drawable.ic_menu_rotate),
                getString(R.string.content_description_random),
                getString(R.string.content_description_random),
                PendingIntent.getService(this, 0, randomizerIntent, PendingIntent.FLAG_UPDATE_CURRENT)
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
        val playPauseDescription = getString(when (isPlaying) {
            true -> R.string.content_description_pause
            false -> R.string.content_description_play
        })
        playPauseIntent.setClass(this, GifService::class.java)
        return RemoteAction(
                playPauseIcon,
                playPauseDescription,
                playPauseDescription,
                PendingIntent.getService(this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT))
    }

    private inner class RemoteActionBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            this@GifActivity.onNewIntent(intent)
        }
    }
}
