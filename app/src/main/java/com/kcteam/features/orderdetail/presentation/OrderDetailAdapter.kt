package com.kcteam.features.orderdetail.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import kotlinx.android.synthetic.main.inflate_order_detail_item.view.*

/**
 * Created by Pratishruti on 30-10-2017.
 */
class OrderDetailAdapter(context:Context): RecyclerView.Adapter<OrderDetailAdapter.MyOrderViewHolder>() {
    private val layoutInflater: LayoutInflater
    private var  context: Context
    init {
        layoutInflater = LayoutInflater.from(context)
        this.context=context
    }
    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        holder.bindItems(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_order_detail_item, parent, false)
        return MyOrderViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 5
    }

    class MyOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context) {
            if (adapterPosition%2==0)
                itemView.order_detail_RL.setBackgroundColor(ContextCompat.getColor(context, R.color.rec_odd_no_color))
            else
                itemView.order_detail_RL.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

    }
}