package com.kcteam.features.shopdetail.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.billing.model.BillingListDataModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_shop_billing_item.view.*


class ShopBillingListAdapter(context: Context, private val billingList: ArrayList<BillingListDataModel>?,
                             private val onCollectionClick: (BillingListDataModel) -> Unit) : RecyclerView.Adapter<ShopBillingListAdapter.MyViewHolder>() {

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
        val v = layoutInflater.inflate(R.layout.inflate_shop_billing_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return billingList?.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.tv_invoice_no_value.text = billingList?.get(adapterPosition)?.invoice_no
            itemView.tv_invoice_date_value.text = AppUtils.convertToSelectedDateReimbursement(billingList?.get(adapterPosition)?.invoice_date!!)

            itemView.tv_total_amount.text = context.getString(R.string.rupee_symbol_with_space) + billingList?.get(adapterPosition)?.total_amount
            itemView.tv_paid_amount.text = context.getString(R.string.rupee_symbol_with_space) + billingList?.get(adapterPosition)?.paid_amount
            itemView.tv_bl_amount.text = context.getString(R.string.rupee_symbol_with_space) + billingList?.get(adapterPosition)?.bal_amount

            itemView.setOnClickListener {
                (context as DashboardActivity).loadFragment(FragType.ShopBillingDetailsFragment, true, billingList[adapterPosition])
            }

            itemView.ll_collection.setOnClickListener {
                onCollectionClick(billingList[adapterPosition])
            }
        }
    }
}