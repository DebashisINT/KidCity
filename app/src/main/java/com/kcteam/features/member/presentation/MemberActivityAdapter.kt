package com.kcteam.features.member.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.member.model.TeamLocDataModel
import kotlinx.android.synthetic.main.inflate_route_activity_item.view.*

/**
 * Created by Saikat on 30-Mar-20.
 */
class MemberActivityAdapter(private val context: Context, private val teamLocList: ArrayList<TeamLocDataModel>?) : RecyclerView.Adapter<MemberActivityAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bindItems(context, teamLocList!!)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_route_activity_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return teamLocList?.size!!
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, teamLocList: ArrayList<TeamLocDataModel>) {
            try {
                if (adapterPosition == 0) {
                    itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.green_round))
                } else if (adapterPosition == teamLocList.size - 1) {
                    itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.red_round))
                } else if (adapterPosition % 2 == 0) {
                    itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.navy_blue_round))
                } else {
                    itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.navy_blue_round))
                }

                itemView.sync_icon.visibility = View.GONE

                itemView.shop_tv.text = teamLocList[adapterPosition].shops_covered

                if (!TextUtils.isEmpty(teamLocList[adapterPosition].location_name))
                    itemView.location_name_tv.text = teamLocList[adapterPosition].location_name
                else
                    itemView.location_name_tv.text = "Unknown"

                itemView.distance_tv.text = teamLocList[adapterPosition].distance_covered

                if (!TextUtils.isEmpty(teamLocList[adapterPosition].last_update_time)) {
                    val str = teamLocList[adapterPosition].last_update_time
                    itemView.time_log.text = str.split(" ")[0]
                    itemView.meridiem.text = str.split(" ")[1]
                }

                if (adapterPosition == (teamLocList.size - 1))
                    itemView.vertival_dot_IV.visibility = View.GONE

                itemView.meeting_tv.text = teamLocList[adapterPosition].meetings_attended

                if (!TextUtils.isEmpty(teamLocList[adapterPosition].battery_percentage)) {
                    itemView.tv_battery_percentage.text = teamLocList[adapterPosition].battery_percentage + "%"
                    itemView.tv_battery_percentage.setTextColor(context.resources.getColor(R.color.navy_blue_dot))
                }
                else {
                    itemView.tv_battery_percentage.text = "N.A."
                    itemView.tv_battery_percentage.setTextColor(context.resources.getColor(R.color.login_txt_color))
                }

                if (!TextUtils.isEmpty(teamLocList[adapterPosition].network_status)) {
                    itemView.tv_network_status.text = teamLocList[adapterPosition].network_status

                    if (itemView.tv_network_status.text.toString().trim().equals("Online", ignoreCase = true))
                        itemView.tv_network_status.setTextColor(context.resources.getColor(R.color.navy_blue_dot))
                    else
                        itemView.tv_network_status.setTextColor(context.resources.getColor(R.color.maroon))
                }
                else {
                    itemView.tv_battery_percentage.text = "N.A."
                    itemView.tv_network_status.setTextColor(context.resources.getColor(R.color.login_txt_color))
                }

                itemView.tv_lat_long.text = teamLocList[adapterPosition].latitude + ", " + teamLocList[adapterPosition].longitude

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}