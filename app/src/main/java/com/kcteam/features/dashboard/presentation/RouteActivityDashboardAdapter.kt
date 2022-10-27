package com.kcteam.features.dashboard.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.location.UserLocationDataEntity
import kotlinx.android.synthetic.main.inflate_route_activity_item.view.*

/**
 * Created by Kinsuk on 01-11-2017.
 */
class RouteActivityDashboardAdapter(context: Context, userLocationDataEntity: List<UserLocationDataEntity>) : RecyclerView.Adapter<RouteActivityDashboardAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<UserLocationDataEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
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
                itemView.dot_IV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.yellow_round))
            }

            itemView.shop_tv.setText(userLocationDataEntity.get(adapterPosition).shops)
//            if (userLocationDataEntity.get(adapterPosition).shops == "0" || userLocationDataEntity.get(adapterPosition).shops == "1") {
//                itemView.shop_visited_tv.setText(context.getString(R.string.shop_visited))
//            } else {
//                itemView.shop_visited_tv.setText(context.getString(R.string.no_of_shop_visited))
//            }
            itemView.location_name_tv.setText(userLocationDataEntity.get(adapterPosition).locationName)
            itemView.distance_tv.setText(userLocationDataEntity.get(adapterPosition).distance)
            itemView.time_log.setText(userLocationDataEntity.get(adapterPosition).time)
            itemView.meridiem.setText(userLocationDataEntity.get(adapterPosition).meridiem)
//            itemView.shop_tv.setText(adapterPosition.toString())


//            itemView.myshop_name_TV.setText(context.getString(R.string.name_colon)+" "+context.getString(R.string.capital_electronics))
//            itemView.myshop_address_TV.setText(context.getString(R.string.kcteam_address))

//            itemView.map_IV.findViewById<ImageView>(R.id.map_IV).setOnClickListener(View.OnClickListener {
//                listener.mapClick()
//            })
//            itemView.order_IV.findViewById<ImageView>(R.id.order_IV).setOnClickListener(View.OnClickListener {
//                listener.orderClick()
//            })
//            itemView.location_IV.findViewById<ImageView>(R.id.location_IV).setOnClickListener(View.OnClickListener {
//                listener.callClick()
//            })
//
//            itemView.setOnClickListener {
//                listener.OnNearByShopsListClick(adapterPosition)
//            }
        }

    }

    open fun update(userLocationDataEntity: List<UserLocationDataEntity>) {
        this.userLocationDataEntity = userLocationDataEntity
    }


}