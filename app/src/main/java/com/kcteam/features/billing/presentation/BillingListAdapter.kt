package com.kcteam.features.billing.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.BillingEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_billing_item.view.*

/**
 * Created by Saikat on 19-02-2019.
 */
class BillingListAdapter(context: Context, val userLocationDataEntity: ArrayList<BillingEntity>?, val listener: OnClickListener) :
        RecyclerView.Adapter<BillingListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_billing_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<BillingEntity>?, listener: OnClickListener) {
            itemView.tv_invoice_no_value.text = userLocationDataEntity?.get(adapterPosition)?.invoice_no
            itemView.tv_amount.text = userLocationDataEntity?.get(adapterPosition)?.invoice_amount
            itemView.tv_invoice_date_value.text = AppUtils.convertToSelectedDateReimbursement(userLocationDataEntity?.get(adapterPosition)?.invoice_date!!)

            if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].remarks)) {
                itemView.tv_remarks.visibility = View.VISIBLE
                itemView.tv_remarks.text = userLocationDataEntity[adapterPosition].remarks
            } else {
                itemView.tv_remarks.visibility = View.GONE
            }

            if (userLocationDataEntity[adapterPosition].isUploaded) {
                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
            } else {
                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                itemView.sync_icon.setOnClickListener {
                    listener.onSyncClick(adapterPosition)
                }
            }

            itemView.setOnClickListener {
                val list = AppDatabase.getDBInstance()!!.billProductDao().getDataAccordingToBillId(userLocationDataEntity[adapterPosition].bill_id!!)

                if (list != null && list.isNotEmpty())
                    (context as DashboardActivity).loadFragment(FragType.BillingDetailsFragment, true, userLocationDataEntity.get(adapterPosition))
            }

            itemView.download_icon.setOnClickListener {
                listener.onDownloadClick(userLocationDataEntity[adapterPosition])
            }

            itemView.iv_create_qr.setOnClickListener {
                listener.onCreateQrClick(userLocationDataEntity[adapterPosition])
            }
        }
    }

    interface OnClickListener {
        fun onSyncClick(adapterPosition: Int)

        fun onDownloadClick(bill: BillingEntity)

        fun onCreateQrClick(bill: BillingEntity)
    }
}