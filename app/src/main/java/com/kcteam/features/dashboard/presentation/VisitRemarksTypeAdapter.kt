package com.kcteam.features.dashboard.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.MeetingTypeEntity
import com.kcteam.app.domain.VisitRemarksEntity
import kotlinx.android.synthetic.main.inflate_month_item.view.*

/**
 * Created by Saikat on 17-01-2020.
 */
class VisitRemarksTypeAdapter(context: Context, private val list: ArrayList<VisitRemarksEntity>?, private val listener: OnItemClickListener) :
        RecyclerView.Adapter<VisitRemarksTypeAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, list, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_month_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: ArrayList<VisitRemarksEntity>?, listener: OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

            itemView.tv_shop_area.visibility = View.GONE
            itemView.tv_shop_name.text = list?.get(adapterPosition)?.name

            if (adapterPosition == list?.size!! - 1)
                itemView.view.visibility = View.GONE
            else
                itemView.view.visibility = View.VISIBLE
        }
    }

    interface OnItemClickListener {
        fun onItemClick(adapterPosition: Int)
    }
}