package com.kcteam.features.nearbyshops.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.domain.BeatEntity
import kotlinx.android.synthetic.main.inflate_beat_item.view.*

class BeatAdapter(private val mContext: Context, private val onItemClick: (BeatEntity) -> Unit) : RecyclerView.Adapter<BeatAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    private val mList: ArrayList<BeatEntity> by lazy {
        ArrayList<BeatEntity>()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_beat_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems() {
            itemView.apply {
                tv_beat_name.text = mList[adapterPosition].name

                val drawable = TextDrawable.builder()
                        .buildRoundRect(mList[adapterPosition].name?.trim()?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)
                iv_beat.setImageDrawable(drawable)

                setOnClickListener {
                    onItemClick(mList[adapterPosition])
                }
            }
        }
    }

    fun updateAdapter(list: ArrayList<BeatEntity>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }
}