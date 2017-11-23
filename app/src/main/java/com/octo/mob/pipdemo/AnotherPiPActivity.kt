package com.octo.mob.pipdemo

import android.app.PictureInPictureParams
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

class AnotherPiPActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_anotherpip)
    }

    override fun onUserLeaveHint() {
        enterPictureInPictureMode(PictureInPictureParams.Builder().build())
    }

}