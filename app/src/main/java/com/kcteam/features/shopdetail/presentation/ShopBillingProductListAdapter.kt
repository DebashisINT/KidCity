package com.kcteam.features.shopdetail.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.orderList.model.NewProductListDataModel
import kotlinx.android.synthetic.main.inflate_billing_details_item.view.*

class ShopBillingProductListAdapter(private val context: Context, private val list: ArrayList<NewProductListDataModel>?) :
        RecyclerView.Adapter<ShopBillingProductListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_billing_details_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, billingDetailsProduct: ArrayList<NewProductListDataModel>?) {
            itemView.tv_particular_item.text = billingDetailsProduct?.get(adapterPosition)?.product_name
            itemView.tv_qty.text = billingDetailsProduct?.get(adapterPosition)?.qty
            itemView.tv_rate.text = billingDetailsProduct?.get(adapterPosition)?.rate
        }
    }
}