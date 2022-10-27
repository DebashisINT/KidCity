package com.kcteam.features.shopdetail.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.PaymentModeEntity
import kotlinx.android.synthetic.main.inflate_month_item.view.*

class PaymentTypeAdapter(context: Context, private val paymentTypeList: List<PaymentModeEntity>, private val onItemClick: (PaymentModeEntity) -> Unit) :
        RecyclerView.Adapter<PaymentTypeAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_month_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return paymentTypeList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.setOnClickListener{
                onItemClick(paymentTypeList[adapterPosition])
            }

            itemView.tv_shop_area.visibility = View.GONE
            itemView.tv_shop_name.text = paymentTypeList[adapterPosition].name

            if (adapterPosition == paymentTypeList.size - 1)
                itemView.view.visibility = View.GONE
            else
                itemView.view.visibility = View.VISIBLE
        }
    }
}