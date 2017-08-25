package com.octo.mob.pipdemo.gif

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.octo.mob.pipdemo.R
import kotlinx.android.synthetic.main.listitem_gif_data.view.*

class GifDataAdapter(private val listener: OnClickGifDataListener) : RecyclerView.Adapter<GifDataViewHolder>() {

    var gifDataList: List<GifData>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return gifDataList?.size ?: 0
    }

    override fun onBindViewHolder(holder: GifDataViewHolder?, position: Int) {
        val gifData = gifDataList?.get(position)
        gifData?.let {
            holder?.bind(it, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GifDataViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.listitem_gif_data, null)
        return GifDataViewHolder(view)
    }
}

class GifDataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(gifData: GifData, listener: OnClickGifDataListener) {
        itemView.textView.text = gifData.title
        itemView.setOnClickListener { listener.onClickGifData(gifData) }
    }
}
