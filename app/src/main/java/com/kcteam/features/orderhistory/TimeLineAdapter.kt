package com.kcteam.features.orderhistory

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.orderhistory.model.LocationData

import kotlinx.android.synthetic.main.inflate_route_activity_item.view.*

class TimeLineAdapter(context: Context, userLocationDataEntity: List<LocationData>) : RecyclerView.Adapter<TimeLineAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<LocationData>

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        this.userLocationDataEntity = userLocationDataEntity
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bindItems(context, userLocationDataEntity)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_route_activity_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<LocationData>) {
            if (adapterPosition == 0) {
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.green_round))
            } else if (adapterPosition == userLocationDataEntity.size - 1) {
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.red_round))
            } else if (adapterPosition % 2 == 0) {
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.navy_blue_round))
            } else {
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.navy_blue_round))
            }

            itemView.sync_icon.visibility = View.VISIBLE

            itemView.shop_tv.text = userLocationDataEntity[adapterPosition].shops_covered

            if (TextUtils.isEmpty(userLocationDataEntity[adapterPosition].meeting_attended))
                itemView.meeting_tv.text = "0"
            else
                itemView.meeting_tv.text = userLocationDataEntity[adapterPosition].meeting_attended

            itemView.location_name_tv.text = userLocationDataEntity[adapterPosition].location_name
            itemView.distance_tv.text = userLocationDataEntity[adapterPosition].distance_covered

            val str = userLocationDataEntity[adapterPosition].last_update_time
            val time = str.split(" ")[0]
            val meridiem = str.split(" ")[1]

            itemView.time_log.text = time
            itemView.meridiem.text = meridiem

            if (adapterPosition == (userLocationDataEntity.size - 1))
                itemView.vertival_dot_IV.visibility = View.GONE
        }
    }
}