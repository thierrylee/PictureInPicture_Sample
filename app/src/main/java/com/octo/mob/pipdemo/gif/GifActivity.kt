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
import com.octo.mob.pipdemo.extensions.stopGif
import com.octo.mob.pipdemo.extensions.viewGif
import kotlinx.android.synthetic.main.activity_gif.*
import kotlinx.android.synthetic.main.activity_gif_content.*
import java.util.*

private enum class GifAction(val action: String) {
    View("View"),
    ViewRandom("ViewRandom")
    ;
}

class GifActivity : AppCompatActivity() {
    object IntentBuilder {

        fun viewGif(context: Context, gifResId: Int): Intent {
            return buildIntent(context, GifAction.View, gifResId)
        }

        fun randomGif(context: Context): Intent {
            return buildIntent(context, GifAction.ViewRandom, 0)
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
        }

        if (gifResId > 0) {
            Glide.with(this)
                    .load(gifResId)
                    .into(gifView)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onStop() {
        super.onStop()
        goToPictureInPictureMode()
    }

    override fun onPause() {
        if (!isInPictureInPictureMode) {
            gifView?.stopGif()
            updateOverlayVisibility()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        gifView?.viewGif()
        updateOverlayVisibility()
    }

    fun togglePlayPause() {
        when (gifView.isGifRunning()) {
            true -> gifView.stopGif()
            else -> gifView.viewGif()
        }
        updateOverlayVisibility()
    }

    fun updateOverlayVisibility() {
        pauseOverlay.visibility = if (gifView.isGifRunning()) View.GONE else View.VISIBLE
    }

    fun goToPictureInPictureMode() {
        val newIntent = IntentBuilder.randomGif(this)
        val remoteActions: List<RemoteAction> = listOf(
                RemoteAction(
                        Icon.createWithResource(this, android.R.drawable.ic_menu_rotate),
                        "Random",
                        "Random",
                        PendingIntent.getActivity(this, 0, newIntent, 0))
        )

        val pipParams: PictureInPictureParams = PictureInPictureParams.Builder()
                .setAspectRatio(Rational.parseRational("3:4"))
                .setActions(remoteActions)
                .build()
        enterPictureInPictureMode(pipParams)
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
