package com.kcteam.features.avgtimespent.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_avgtimespent_items.view.*
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by Pratishruti on 30-10-2017.
 */
class AvgTimeSpentListAdapter(context: Context, list: List<ShopActivityEntity>, val listener: AvgTimeSpentListClickListener) : RecyclerView.Adapter<AvgTimeSpentListAdapter.MyViewHolder>() {
    private var compressedImage: File? = null
    private val layoutInflater: LayoutInflater
    private var context: Context
    private lateinit var mList: List<ShopActivityEntity>


    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        mList = list
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, mList, listener)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_avgtimespent_items, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: List<ShopActivityEntity>, listener: AvgTimeSpentListClickListener) {

            try {

                val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(list[adapterPosition].shopid)

                if (Pref.willStockShow) {
                    if (Pref.isStockAvailableForAll) {
                        itemView.ll_stock.visibility = View.VISIBLE
                        itemView.stock_view.visibility = View.VISIBLE

                        if (Pref.isOrderShow) {
                            itemView.add_order_ll.visibility = View.VISIBLE
                            itemView.stock_view.visibility = View.VISIBLE

                            if (Pref.isQuotationShow)
                                itemView.order_view.visibility = View.VISIBLE
                            else
                                itemView.order_view.visibility = View.GONE

                        }
                        else {
                            itemView.add_order_ll.visibility = View.GONE
                            itemView.stock_view.visibility = View.GONE
                            itemView.order_view.visibility = View.GONE
                        }

                    } else {
                        if (!TextUtils.isEmpty(shop?.type) && shop?.type == "4") {
                            itemView.ll_stock.visibility = View.VISIBLE
                            itemView.stock_view.visibility = View.VISIBLE

                            if (Pref.isOrderShow) {
                                itemView.add_order_ll.visibility = View.VISIBLE
                                itemView.stock_view.visibility = View.VISIBLE

                                if (Pref.isQuotationShow)
                                    itemView.order_view.visibility = View.VISIBLE
                                else
                                    itemView.order_view.visibility = View.GONE
                            }
                            else {
                                itemView.add_order_ll.visibility = View.GONE
                                itemView.stock_view.visibility = View.GONE
                                itemView.order_view.visibility = View.GONE
                            }

                        } else {
                            itemView.ll_stock.visibility = View.GONE
                            itemView.stock_view.visibility = View.GONE

                            if (Pref.isOrderShow) {
                                itemView.add_order_ll.visibility = View.VISIBLE

                                if (Pref.isQuotationShow)
                                    itemView.order_view.visibility = View.VISIBLE
                                else
                                    itemView.order_view.visibility = View.GONE
                            }
                            else {
                                itemView.add_order_ll.visibility = View.GONE
                                itemView.order_view.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    itemView.ll_stock.visibility = View.GONE
                    itemView.stock_view.visibility = View.GONE

                    if (Pref.isOrderShow) {
                        itemView.add_order_ll.visibility = View.VISIBLE

                        if (Pref.isQuotationShow)
                            itemView.order_view.visibility = View.VISIBLE
                        else
                            itemView.order_view.visibility = View.GONE
                    }
                    else {
                        itemView.add_order_ll.visibility = View.GONE
                        itemView.order_view.visibility = View.GONE
                    }
                }

                if (shop != null && shop.isUploaded) {

                    if (list[adapterPosition].isUploaded) {
                        itemView.sync_icon.visibility = View.VISIBLE
                        val list_ = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, list[adapterPosition].shopid!!,
                                list[adapterPosition].visited_date!!)

                        if (list_ != null && list_.isNotEmpty()) {
                            itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                            itemView.sync_icon.setOnClickListener(View.OnClickListener {
                                listener.onSyncClick(adapterPosition)
                            })
                        } else {

                            val audioList = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false, list[adapterPosition].shopid!!,
                                    list[adapterPosition].visited_date!!)

                            if (audioList != null && audioList.isNotEmpty()) {
                                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                                itemView.sync_icon.setOnClickListener(View.OnClickListener {
                                    listener.onSyncClick(adapterPosition)
                                })
                            }
                            else
                                itemView.sync_icon.setImageResource(R.drawable.ic_dashboard_green_tick_new)
                        }
                    }

                    if (list[adapterPosition].isDurationCalculated && !list[adapterPosition].isUploaded) {
                        itemView.sync_icon.visibility = View.VISIBLE
                        itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                        itemView.sync_icon.setOnClickListener(View.OnClickListener {
                            listener.onSyncClick(adapterPosition)
                        })
                    }
                } else {
                    if (list[adapterPosition].isDurationCalculated && !list[adapterPosition].isUploaded) {
                        itemView.sync_icon.visibility = View.VISIBLE
                        itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                        itemView.sync_icon.setOnClickListener(View.OnClickListener {
                            listener.onSyncClick(adapterPosition)
                        })
                    } else
                        itemView.sync_icon.visibility = View.GONE
                }

                itemView.myshop_name_TV.text = list[adapterPosition].shop_name
                var address: String = list[adapterPosition].shop_address!!
                itemView.myshop_address_TV.text = address
                itemView.duration_TV.text = list[adapterPosition].duration_spent

                val drawable = TextDrawable.builder()
                        .buildRoundRect(list[adapterPosition].shop_name!!.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)
                itemView.shop_image_IV.setImageDrawable(drawable)

                itemView.menu_IV.findViewById<ImageView>(R.id.menu_IV).setOnClickListener(View.OnClickListener {
                    listener.menuClick(adapterPosition, itemView.menu_IV)
                })
//
                itemView.setOnClickListener {
                    listener.OnTimeSpentListClick(adapterPosition)
                }

                itemView.tv_shop_contact_no.text = shop.ownerName + " (${shop.ownerContactNumber})"
                itemView.tv_shop_contact_no.setOnClickListener {
                    IntentActionable.initiatePhoneCall(context, shop?.ownerContactNumber)
                }

                itemView.ll_stock.setOnClickListener{
                    (context as DashboardActivity).loadFragment(FragType.StockListFragment, true, shop!!)
                }

                itemView.add_order_ll.setOnClickListener{
                    (context as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, shop!!)
                }

                itemView.ll_activity.setOnClickListener {
                    when (shop?.type) {
                        "7" -> {
                            (context as DashboardActivity).isFromShop = true
                            (context as DashboardActivity).loadFragment(FragType.ChemistActivityListFragment, true, shop)
                        }
                        "8" -> {
                            (context as DashboardActivity).isFromShop = true
                            (context as DashboardActivity).loadFragment(FragType.DoctorActivityListFragment, true, shop)
                        }
                        else -> {
                            (context as DashboardActivity).isFromMenu = false
                            (context as DashboardActivity).loadFragment(FragType.AddActivityFragment, true, shop!!)
                        }
                    }
                }

                if (Pref.isQuotationShow) {
                    itemView.add_quot_ll.visibility = View.VISIBLE
                    //itemView.order_view.visibility = View.VISIBLE
                } else {
                    itemView.add_quot_ll.visibility = View.GONE
                    //itemView.order_view.visibility = View.GONE
                }

                itemView.add_quot_ll.setOnClickListener {
                    (context as DashboardActivity).isBack = true
                    (context as DashboardActivity).loadFragment(FragType.QuotationListFragment, true, shop?.shop_id!!)
                }

                if (!TextUtils.isEmpty(list[adapterPosition].device_model))
                    itemView.tv_device_model.text = list[adapterPosition].device_model

                if (!TextUtils.isEmpty(list[adapterPosition].android_version))
                    itemView.tv_android_version.text = list[adapterPosition].android_version

                if (!TextUtils.isEmpty(list[adapterPosition].battery))
                    itemView.tv_battery.text = list[adapterPosition].battery + "%"

                if (!TextUtils.isEmpty(list[adapterPosition].net_status))
                    itemView.tv_net_status.text = list[adapterPosition].net_status

                if (!TextUtils.isEmpty(list[adapterPosition].net_type))
                    itemView.tv_net_type.text = list[adapterPosition].net_type

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun getMinuteFromTimeSpan(startTimeStamp: String, endTimeStamp: String): String {
            if (startTimeStamp.isBlank() || endTimeStamp.isBlank())
                return "0"
            var totalMinutes = (endTimeStamp.toLong() - startTimeStamp.toLong())
            return convertToHrs(TimeUnit.MILLISECONDS.toMinutes(totalMinutes).toString())
        }

        fun convertToHrs(durationInMin: String): String {

            try {
                var totalMinutesInt = Integer.valueOf(durationInMin)
                if (totalMinutesInt < 60)
                    return durationInMin

                var hours = totalMinutesInt / 60
                var hoursToDisplay = hours

//        if (hours > 12) {
//            hoursToDisplay = hoursToDisplay - 12;
//        }

                var minutesToDisplay = totalMinutesInt - (hours * 60)

                var minToDisplay: String
                if (minutesToDisplay == 0)
                    minToDisplay = "00"
                else if (minutesToDisplay < 10) minToDisplay = "0" + minutesToDisplay
                else minToDisplay = "" + minutesToDisplay

                var displayValue = hoursToDisplay.toString() + ":" + minToDisplay

//        if (hours < 12)
//            displayValue = displayValue + " AM";
//        else
//            displayValue = displayValue + " PM";

                return displayValue
            } catch (e: Exception) {
                return "00:00"
            }
        }

        fun shoulIBotherToUpdate(shopId: String): Boolean {
            return AppDatabase.getDBInstance()!!.shopActivityDao().isDurationAvailable(shopId, AppUtils.getCurrentDateForShopActi())
        }

    }

    open fun updateList(locationDataEntity: List<ShopActivityEntity>) {
        Collections.reverse(locationDataEntity)
        mList = locationDataEntity
        notifyDataSetChanged()
    }


}