package com.octo.mob.pipdemo.gif

import android.os.Parcel
import android.os.Parcelable
import com.octo.mob.pipdemo.R

data class GifData(val title: String, val resId: Int) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeInt(resId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GifData> {
        override fun createFromParcel(parcel: Parcel): GifData {
            return GifData(parcel)
        }

        override fun newArray(size: Int): Array<GifData?> {
            return arrayOfNulls(size)
        }
    }
}

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