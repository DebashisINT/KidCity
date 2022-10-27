package com.kcteam.features.myallowancerequest

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.myorder.presentation.MyOrderListClickListener
import kotlinx.android.synthetic.main.inflate_my_order_item.view.*

/**
 * Created by Kinsuk on 30-10-2017.
 */
class MyallowanceRequestAdapter (context: Context, val listener: MyOrderListClickListener): RecyclerView.Adapter<MyallowanceRequestAdapter.MyOrderViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var  context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context=context
    }
    override fun onBindViewHolder(holder: MyOrderViewHolder, position: Int) {
        holder.bindItems(context,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyOrderViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_my_allowance_request, parent, false)
        return MyOrderViewHolder(v)
    }

    override fun getItemCount(): Int {
        return 5
    }

    class MyOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context,listener:MyOrderListClickListener) {
//            if (adapterPosition%2==0)
                itemView.order_item_CV.setBackgroundColor(ContextCompat.getColor(context, R.color.rec_odd_no_color))
//            else
//                itemView.order_item_CV.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            itemView.setOnClickListener{listener.OnOrderListClick(adapterPosition)}
        }

    }

}