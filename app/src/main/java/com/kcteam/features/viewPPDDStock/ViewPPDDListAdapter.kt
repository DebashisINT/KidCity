package com.kcteam.features.viewPPDDStock

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.domain.StockListEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.inflate_pp_dd_stock.view.*


/**
 * Created by Pratishruti on 30-10-2017.
 */
class ViewPPDDListAdapter(context: Context, list: List<AddShopDBModelEntity>, val stoclist: List<StockListEntity>, private val isOutstanding: Boolean,
                          val listener: ViewPPDDListClickListener) : RecyclerView.Adapter<ViewPPDDListAdapter.MyViewHolder>() {
    private val layoutInflater: LayoutInflater
    private var context: Context
    private var mList: List<AddShopDBModelEntity>


    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        mList = list
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, mList, listener, stoclist, isOutstanding)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_pp_dd_stock, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: List<AddShopDBModelEntity>, listener: ViewPPDDListClickListener, stoclist: List<StockListEntity>,
                      isOutstanding: Boolean) {
            //Picasso.with(context).load(list[adapterPosition].shopImageLocalPath).into(itemView.shop_image_IV)

            try {

                itemView.shop_list_LL.setOnClickListener(View.OnClickListener {
                    listener.OnNearByShopsListClick(adapterPosition)
                })

                itemView.tag_iv.setOnClickListener(View.OnClickListener {
                    (context as DashboardActivity).loadFragment(FragType.MarketingPagerFragment, true, list[adapterPosition].shop_id)
                })

                val stockList = AppDatabase.getDBInstance()!!.updateStockDao().getStockAccordingToShopId(list[adapterPosition].shop_id)

                if (!TextUtils.isEmpty(list[adapterPosition].shopImageLocalPath)) {
                    Picasso.get()
                            .load(list[adapterPosition].shopImageLocalPath)
                            .resize(100, 100)
                            .into(itemView.shop_image_IV)
                }

                if (!TextUtils.isEmpty(list[adapterPosition].shopName))
                    itemView.myshop_name_TV.text = list[adapterPosition].shopName

                if (!TextUtils.isEmpty(list[adapterPosition].address)) {
                    if (!TextUtils.isEmpty(list[adapterPosition].pinCode)) {
                        val address: String = list[adapterPosition].address + ", " + list[adapterPosition].pinCode
                        itemView.myshop_address_TV.text = address
                    } else
                        itemView.myshop_address_TV.text = list[adapterPosition].address
                }

                val day = AppUtils.getDayFromReverseFormat(AppUtils.getCurrentDateForShopActi()).toInt()
                if (day >= 1 && day <= 5)
                    itemView.update_address_TV.visibility = View.VISIBLE
                else
                    itemView.update_address_TV.visibility = View.GONE

                val drawable = TextDrawable.builder()
                        .buildRoundRect(list[adapterPosition].shopName.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

                itemView.shop_IV.setImageDrawable(drawable)

                itemView.shop_image_IV.findViewById<ImageView>(R.id.shop_image_IV).setOnClickListener(View.OnClickListener {
                    listener.OnNearByShopsListClick(adapterPosition)
                })

                itemView.call_ll.findViewById<LinearLayout>(R.id.call_ll).setOnClickListener(View.OnClickListener {
                    listener.callClick(adapterPosition)
                })

                itemView.direction_ll.findViewById<LinearLayout>(R.id.direction_ll).setOnClickListener(View.OnClickListener {
                    listener.mapClick(adapterPosition)
                })

                itemView.add_order_ll.findViewById<LinearLayout>(R.id.add_order_ll).setOnClickListener(View.OnClickListener {
                    listener.orderClick(adapterPosition)
                })
                itemView.update_address_TV.findViewById<AppCustomTextView>(R.id.update_address_TV).setOnClickListener(View.OnClickListener {
                    listener.updateLocClick(adapterPosition)
                })
                itemView.order_amt_p_TV.text = " " + context.getString(R.string.zero_order_in_value)

                if (!TextUtils.isEmpty(list[adapterPosition].totalVisitCount))
                    itemView.total_visited_value_TV.text = " " + list[adapterPosition].totalVisitCount

                if (!TextUtils.isEmpty(list[adapterPosition].lastVisitedDate))
                    itemView.last_visited_date_TV.text = " " + list[adapterPosition].lastVisitedDate

                itemView.sync_icon.visibility = View.VISIBLE

                val outstandingList= AppDatabase.getDBInstance()!!.updateOutstandingDao().getOutstandingAccordingToShopId(list[adapterPosition].shop_id)

                if (list[adapterPosition].isUploaded) {
                    /*if (isOutstanding){
                        for (i in outstandingList.indices) {
                            if (!outstandingList[i].isUploaded) {
                                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                                itemView.sync_icon.setOnClickListener(View.OnClickListener {
                                    listener.syncClick(adapterPosition)
                                })
                                break
                            } else {
                                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                            }
                        }
                    }
                    else {*/
                        for (i in stockList.indices) {
                            if (!stockList[i].isUploaded) {
                                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                                itemView.sync_icon.setOnClickListener(View.OnClickListener {
                                    listener.syncClick(adapterPosition)
                                })
                                break
                            } else {
                                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                            }
                        }
                    //}
                } else {
                    itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    itemView.sync_icon.setOnClickListener({
                        listener.syncClick(adapterPosition)
                    })
                }

                val stock_list = AppDatabase.getDBInstance()!!.stockListDao().getStockAccordingToShopId(list[adapterPosition].shop_id)

                if (stock_list == null || stock_list.isEmpty())
                    itemView.order_RL.visibility = View.GONE
                else {
                    itemView.order_RL.visibility = View.VISIBLE
                    if (!TextUtils.isEmpty(stock_list[0].stock_value))
                        itemView.order_amt_p_TV.text = "\u20B9 " + stock_list[0].stock_value

                    if (!TextUtils.isEmpty(stock_list[0].current_date))
                        itemView.order_amt_TV.text = "Stock Amount on " + AppUtils.convertToCommonFormat(stock_list[0].current_date!!) + ": "
                }

                val orderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(list[adapterPosition].shop_id) as ArrayList<OrderDetailsListEntity>

                if (orderList != null && orderList.isNotEmpty()) {
                    itemView.order_amount_tv.visibility = View.VISIBLE
                    var amount = 0.0
                    for (i in orderList.indices) {
                        if (!TextUtils.isEmpty(orderList[i].amount))
                            amount += orderList[i].amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())
                    itemView.order_amount_tv.text = "Given Order: \u20B9 $finalAmount"
                } else
                    itemView.order_amount_tv.visibility = View.GONE


                itemView.tv_view_stock.setOnClickListener({
                    listener.viewStock(adapterPosition)
                })

                /*if (isOutstanding) {
                    itemView.update_address_TV.text = context.getString(R.string.update_outstanding)
                    itemView.tv_view_stock.text = context.getString(R.string.view_outstanding)
                } else {
                    itemView.update_address_TV.text = context.getString(R.string.update_stock)
                    itemView.tv_view_stock.text = context.getString(R.string.view_stock)
                }*/

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun updateAdapter(mlist: List<AddShopDBModelEntity>) {
        this.mList = mlist
        notifyDataSetChanged()
    }


}