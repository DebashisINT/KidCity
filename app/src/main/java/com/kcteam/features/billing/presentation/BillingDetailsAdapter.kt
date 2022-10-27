package com.kcteam.features.billing.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.BillingProductListEntity
import kotlinx.android.synthetic.main.inflate_billing_details_item.view.*

/**
 * Created by Saikat on 19-11-2019.
 */
class BillingDetailsAdapter(private val context: Context, private val list: List<BillingProductListEntity>) : RecyclerView.Adapter<BillingDetailsAdapter.MyViewHolder>() {

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
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, billingDetailsProduct: List<BillingProductListEntity>?) {
            itemView.tv_particular_item.text = billingDetailsProduct?.get(adapterPosition)?.product_name
            itemView.tv_qty.text = billingDetailsProduct?.get(adapterPosition)?.qty
            itemView.tv_rate.text = billingDetailsProduct?.get(adapterPosition)?.rate
        }
    }

}