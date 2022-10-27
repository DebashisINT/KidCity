package com.kcteam.features.viewAllOrder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.ViewAllOrderListEntity
import com.kcteam.features.averageshop.presentation.AverageShopListClickListener
import kotlinx.android.synthetic.main.inflater_order_history_item.view.*
import java.util.*

/**
 * Created by Pratishruti on 15-11-2017.
 */
class ViewAllOrderListAdapter(context: Context, userLocationDataEntity: List<ViewAllOrderListEntity>, val listener: AverageShopListClickListener) : RecyclerView.Adapter<ViewAllOrderListAdapter.MyViewHolder>() {
    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<ViewAllOrderListEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflater_order_history_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<ViewAllOrderListEntity>, listener: AverageShopListClickListener) {

            if (!TextUtils.isEmpty(userLocationDataEntity.get(adapterPosition).date))
                itemView.order_date_tv.text = "Date: " + userLocationDataEntity.get(adapterPosition).date

            if (!TextUtils.isEmpty(userLocationDataEntity.get(adapterPosition).amount))
                itemView.ordered_amount_tv.text = "Amount:  \u20B9" + userLocationDataEntity.get(adapterPosition).amount
//
            itemView.setOnClickListener {
                //listener.OnItemClick(adapterPosition)
            }
        }

    }

    open fun updateList(locationDataEntity: List<ViewAllOrderListEntity>) {
        Collections.reverse(locationDataEntity)
        userLocationDataEntity = locationDataEntity
        notifyDataSetChanged()
    }
}