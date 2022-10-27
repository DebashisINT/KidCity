package com.kcteam.features.dashboard.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.types.DashboardType
import com.kcteam.features.myorder.presentation.MyOrderListClickListener
import kotlinx.android.synthetic.main.inflate_route_activity_layout.view.*

/**
 * Created by rp : 31-10-2017:16:57
 */
class YesterdayRouteAdapter(context: Context, mFragment: DashboardType, val listener: MyOrderListClickListener): RecyclerView.Adapter<YesterdayRouteAdapter.RouteActivityViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var  context: Context
    private lateinit var mFragment:DashboardType

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context=context
        this.mFragment=mFragment
    }
    override fun onBindViewHolder(holder: RouteActivityViewHolder, position: Int) {
        holder.bindItems(context,mFragment,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteActivityViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_route_activity_layout, parent, false)
        return RouteActivityViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 12
    }

    class RouteActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context,mFragment:DashboardType,listener:MyOrderListClickListener) {


                itemView.distance_log.setText("You have covered the distance of 5 KM in last hour")
//                itemView.shops_log.setText("You have visited 4 shops")
                itemView.time_log.setText("10.00 AM")


            if(adapterPosition==0) {
                itemView.login_report.visibility = View.VISIBLE
                itemView.view.visibility =View.VISIBLE
            }
            else {
                itemView.login_report.visibility = View.GONE
                itemView.view.visibility =View.GONE
            }
            if (adapterPosition%2==0)
                itemView.route_CV.setCardBackgroundColor(ContextCompat.getColor(context, R.color.translucent_color_primary))
            else
                itemView.route_CV.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
            itemView.setOnClickListener{listener.OnOrderListClick(adapterPosition)}
        }

    }
}