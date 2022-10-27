package com.kcteam.features.returnsOrder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.averageshop.presentation.AverageShopListClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_new_order_item.view.*
import kotlinx.android.synthetic.main.inflate_new_order_item.view.myshop_address_TV
import kotlinx.android.synthetic.main.inflate_new_order_item.view.myshop_name_TV
import kotlinx.android.synthetic.main.inflate_new_order_item.view.sync_icon
import kotlinx.android.synthetic.main.inflate_new_order_item.view.total_visited_value_TV


class NewDateWiseReturnListAdapter(context: Context, userLocationDataEntity: ArrayList<ReturnDetailsEntity>,
                                   val listener: AverageShopListClickListener,
                                   private val onDownloadClick: (ReturnDetailsEntity) -> Unit,
                                   private val onLocationClick: (ReturnDetailsEntity) -> Unit,
                                   private val onCreateQRClick: (ReturnDetailsEntity) -> Unit) :
        RecyclerView.Adapter<NewDateWiseReturnListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    var userLocationDataEntity: List<ReturnDetailsEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_new_return_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<ReturnDetailsEntity>, listener: AverageShopListClickListener) {

            try {


                itemView.total_visited_value_TV.text = userLocationDataEntity[adapterPosition].return_id
                if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].date))
                    itemView.tv_order_date.text = AppUtils.convertDateTimeToCommonFormat(userLocationDataEntity[adapterPosition].date!!)
                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(userLocationDataEntity[adapterPosition].shop_id)
                itemView.myshop_name_TV.text = shop?.shopName
                itemView.myshop_address_TV.text = shop?.address
                val list = AppDatabase.getDBInstance()!!.returnProductListDao().getDataAccordingToOrderId(userLocationDataEntity[adapterPosition].return_id!!) as ArrayList<ReturnProductListEntity>
                itemView.tv_total_item.text = list.size.toString()

                if (shop != null) {
                    if (shop.isUploaded) {

                        if (userLocationDataEntity[adapterPosition].isUploaded) {
                            itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                        } else {
                            itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                            itemView.sync_icon.setOnClickListener(View.OnClickListener {
                                listener.onSyncClick(adapterPosition)
                            })
                        }
                    } else {
                        itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                        itemView.sync_icon.setOnClickListener(View.OnClickListener {
                            listener.onSyncClick(adapterPosition)
                        })
                    }
                }

                var totalAmount = 0.0

                for (i in list.indices) {
                    totalAmount += list[i].total_price?.toDouble()!!
                }
                val totalPrice = String.format("%.2f", totalAmount.toFloat())
                itemView.tv_total_amount.text = context.getString(R.string.rupee_symbol) + totalPrice

                if (Pref.isOrderMailVisible) {
                    if (shop.type != "1" && shop.type != "2" && shop.type != "3" && shop.type != "4" && shop.type != "5")
                        itemView.email_icon.visibility = View.GONE
                    else
                        itemView.email_icon.visibility = View.VISIBLE
                } else
                    itemView.email_icon.visibility = View.GONE


                itemView.setOnClickListener {
                    (context as DashboardActivity).loadFragment(FragType.ViewCartReturnFragment, true, userLocationDataEntity.get(adapterPosition))
                }




                itemView.download_icon.setOnClickListener {
                    onDownloadClick(userLocationDataEntity[adapterPosition])
                }


                itemView.location_icon.setOnClickListener {
                    onLocationClick(userLocationDataEntity[adapterPosition])
                }

                itemView.iv_create_qr.setOnClickListener {
                    onCreateQRClick(userLocationDataEntity[adapterPosition])
                }

                itemView.email_icon.visibility=View.GONE
                itemView.collection_icon.visibility=View.GONE

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    open fun updateList(locationDataEntity: List<ShopActivityEntity>) {

    }
}