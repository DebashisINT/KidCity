package com.kcteam.features.quotation.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.*
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_quot_item.view.*

/**
 * Created by Saikat on 15-Jun-20.
 */
class QuotationAdapter(private val context: Context, private val quotList: ArrayList<QuotationEntity>,
                       private val onSyncClick: (QuotationEntity) -> Unit, private val onItemClick: (QuotationEntity) -> Unit,
                       private val onCallClick: (String) -> Unit, private val onLocClick: (AddShopDBModelEntity?) -> Unit,
                       private val onSmsClick: (QuotationEntity) -> Unit, private val onMailClick: (QuotationEntity) -> Unit) :
        RecyclerView.Adapter<QuotationAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_quot_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return quotList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {

            try {

                itemView.total_visited_value_TV.text = quotList[adapterPosition].quo_no
                if (!TextUtils.isEmpty(quotList[adapterPosition].date))
                    itemView.tv_order_date.text = AppUtils.convertToCommonFormat(quotList[adapterPosition].date!!)
                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(quotList[adapterPosition].shop_id)
                itemView.myshop_name_TV.text = shop?.shopName
                itemView.myshop_address_TV.text = shop?.address

                if (shop != null) {
                    if (shop.isUploaded) {

                        if (quotList[adapterPosition].isUploaded) {
                            if (quotList[adapterPosition].isEditUpdated == 0) {
                                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                                itemView.sync_icon.setOnClickListener {
                                    onSyncClick(quotList[adapterPosition])
                                }
                            } else
                                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)

                        } else {
                            itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                            itemView.sync_icon.setOnClickListener {
                                onSyncClick(quotList[adapterPosition])
                            }
                        }
                    } else {
                        itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                        itemView.sync_icon.setOnClickListener {
                            onSyncClick(quotList[adapterPosition])
                        }
                    }
                }

                itemView.setOnClickListener {
                    onItemClick(quotList[adapterPosition])
                }


                itemView.call_ll.setOnClickListener {
                    onCallClick(shop?.ownerContactNumber!!)
                }

                itemView.direction_ll.setOnClickListener {
                    onLocClick(shop)
                }

                itemView.add_sms_ll.setOnClickListener {
                    onSmsClick(quotList[adapterPosition])
                }

                itemView.add_mail_ll.setOnClickListener {
                    if (TextUtils.isEmpty(shop.ownerEmailId))
                        (context as DashboardActivity).showSnackMessage("Email Id is not Found")
                    else
                        onMailClick(quotList[adapterPosition])
                }

                val totalPrice = String.format("%.2f", quotList[adapterPosition].net_amount?.toFloat())
                itemView.tv_total_amount.text = context.getString(R.string.rupee_symbol) + totalPrice


            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}