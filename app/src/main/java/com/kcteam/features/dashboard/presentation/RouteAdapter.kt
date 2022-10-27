package com.kcteam.features.dashboard.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import kotlinx.android.synthetic.main.inflate_work_plan_item.view.*

/**
 * Created by Kinsuk on 01-12-2018.
 */
class RouteAdapter(context: Context, list: String?) : RecyclerView.Adapter<RouteAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    private lateinit var mList: String


    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        mList = list!!
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, mList, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_selected_route_shop_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 1
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: String, position: Int) {
            itemView.shop_name_TV.text = "Route " + list
            itemView.shop_address_TV.visibility = View.GONE
            itemView.avg_order_val_TV.visibility = View.GONE
        }
    }
}