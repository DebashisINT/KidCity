package com.kcteam.features.orderhistory

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.location.UserLocationDataEntity
import kotlinx.android.synthetic.main.inflate_route_activity_item.view.*


/**
 * Created by Kinsuk on 01-11-2017.
 */
class DayWiseAdapter(context: Context, userLocationDataEntity: List<UserLocationDataEntity>) : RecyclerView.Adapter<DayWiseAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<UserLocationDataEntity>

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

        fun bindItems(context: Context, userLocationDataEntity: List<UserLocationDataEntity>) {
            if (adapterPosition == 0) {
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.green_round))
            } else if (adapterPosition == userLocationDataEntity.size - 1) {
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.red_round))
            } else if (adapterPosition % 2 == 0) {
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.navy_blue_round))
            } else {
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.navy_blue_round))
            }

            if (userLocationDataEntity[adapterPosition].isUploaded)
                itemView.sync_icon.visibility = View.VISIBLE
            else
                itemView.sync_icon.visibility = View.GONE

            itemView.shop_tv.text = userLocationDataEntity[adapterPosition].shops
//            if (userLocationDataEntity.get(adapterPosition).shops == "0" || userLocationDataEntity.get(adapterPosition).shops == "1") {
//                itemView.shop_visited_tv.setText(context.getString(R.string.shop_visited))
//            } else {
//                itemView.shop_visited_tv.setText(context.getString(R.string.no_of_shop_visited))
//            }

            if (TextUtils.isEmpty(userLocationDataEntity[adapterPosition].meeting))
                itemView.meeting_tv.text = "0"
            else
                itemView.meeting_tv.text = userLocationDataEntity[adapterPosition].meeting

            itemView.location_name_tv.text = userLocationDataEntity[adapterPosition].locationName
            itemView.distance_tv.text = userLocationDataEntity[adapterPosition].distance
            itemView.time_log.text = userLocationDataEntity[adapterPosition].time
            itemView.meridiem.text = userLocationDataEntity[adapterPosition].meridiem
            if (adapterPosition == (userLocationDataEntity.size - 1))
                itemView.vertival_dot_IV.visibility = View.GONE

            if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].battery_percentage)) {
                itemView.tv_battery_percentage.text = userLocationDataEntity[adapterPosition].battery_percentage + "%"
                itemView.tv_battery_percentage.setTextColor(context.resources.getColor(R.color.navy_blue_dot))
            }
            else {
                itemView.tv_battery_percentage.text = "N.A."
                itemView.tv_battery_percentage.setTextColor(context.resources.getColor(R.color.login_txt_color))
            }

            if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].network_status)) {
                itemView.tv_network_status.text = userLocationDataEntity[adapterPosition].network_status

                if (itemView.tv_network_status.text.toString().trim().equals("Online", ignoreCase = true))
                    itemView.tv_network_status.setTextColor(context.resources.getColor(R.color.navy_blue_dot))
                else
                    itemView.tv_network_status.setTextColor(context.resources.getColor(R.color.maroon))
            }
            else {
                itemView.tv_battery_percentage.text = "N.A."
                itemView.tv_network_status.setTextColor(context.resources.getColor(R.color.login_txt_color))
            }

            itemView.tv_lat_long.text = userLocationDataEntity[adapterPosition].latitude + ", " + userLocationDataEntity[adapterPosition].longitude
        }
    }

    open fun updateList(newlist: List<UserLocationDataEntity>) {
        userLocationDataEntity = newlist
        notifyDataSetChanged()
    }


}