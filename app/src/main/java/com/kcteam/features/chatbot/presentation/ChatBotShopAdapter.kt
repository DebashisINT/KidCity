package com.kcteam.features.chatbot.presentation

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.nearbyshops.model.NewOrderModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.inflate_chatbot_shops.view.*


class ChatBotShopAdapter(private val mContext: Context, private val isVisit: Boolean, private val onShareClick: (AddShopDBModelEntity) -> Unit) : RecyclerView.Adapter<ChatBotShopAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    private val list: ArrayList<AddShopDBModelEntity> by lazy {
        ArrayList<AddShopDBModelEntity>()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_chatbot_shops, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newOrderList = ArrayList<NewOrderModel>()

        fun bindItems() {
            //Picasso.with(context).load(list[adapterPosition].shopImageLocalPath).into(itemView.shop_image_IV)
            try {
                if (isVisit) {
                    itemView.ll_order_data.visibility = View.GONE
                    itemView.rl_visit_data.visibility = View.VISIBLE
                }
                else {
                    itemView.ll_order_data.visibility = View.VISIBLE
                    itemView.rl_visit_data.visibility = View.GONE
                }

                if (!TextUtils.isEmpty(list[adapterPosition].shopImageLocalPath)) {
                    Picasso.get()
                            .load(list[adapterPosition].shopImageLocalPath)
                            .resize(100, 100)
                            .into(itemView.shop_image_IV)
                }
                itemView.myshop_name_TV.text = list[adapterPosition].shopName
                var address: String = list[adapterPosition].address + ", " + list[adapterPosition].pinCode
                itemView.myshop_address_TV.text = address

                val drawable = TextDrawable.builder()
                        .buildRoundRect(list[adapterPosition].shopName.trim().toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

                itemView.shop_IV.setImageDrawable(drawable)

                itemView.total_visited_value_TV.text = " " + list[adapterPosition].totalVisitCount
                itemView.last_visited_date_TV.text = " " + list[adapterPosition].lastVisitedDate

                val shopType = AppDatabase.getDBInstance()?.shopTypeDao()?.getSingleType(list[adapterPosition].type)

                if (shopType != null && !TextUtils.isEmpty(shopType.shoptype_name)) {
                    itemView.tv_type.text = shopType.shoptype_name
                    itemView.ll_shop_type.visibility = View.VISIBLE
                } else
                    itemView.ll_shop_type.visibility = View.GONE

                val orderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(list[adapterPosition].shop_id) as ArrayList<OrderDetailsListEntity>
                if (orderList != null && orderList.isNotEmpty()) {
                    val amountList = ArrayList<Double>()
                    var amount = 0.0
                    var month = ""
                    var year = ""
                    var newAmount = 0.0
                    var max = 0.0
                    var min = 0.0
                    var maxIndex = 0
                    var minIndex = 0

                    for (i in orderList.indices) {
                        if (!TextUtils.isEmpty(orderList[i].amount)) {
                            amount += orderList[i].amount?.toDouble()!!
                            amountList.add(orderList[i].amount?.toDouble()!!)

                            if (i == 0) {
                                newAmount = orderList[i].amount?.toDouble()!!
                                month = AppUtils.getMonthNoFromReverseFormat(AppUtils.getCurrentDateFormatInTa(orderList[i].only_date!!))
                                year = AppUtils.getYearFromReverseFormat(AppUtils.getCurrentDateFormatInTa(orderList[i].only_date!!))
                            }
                            else if (i == orderList.size - 1) {
                                val newMonth = AppUtils.getMonthNoFromReverseFormat(AppUtils.getCurrentDateFormatInTa(orderList[i].only_date!!))
                                val newYear = AppUtils.getYearFromReverseFormat(AppUtils.getCurrentDateFormatInTa(orderList[i].only_date!!))

                                if (month == newMonth && year == newYear) {
                                    newAmount += orderList[i].amount?.toDouble()!!
                                    newOrderList.add(NewOrderModel(newAmount, newMonth, newYear))
                                }
                                else {
                                    newOrderList.add(NewOrderModel(newAmount, month, year))
                                    newOrderList.add(NewOrderModel(orderList[i].amount?.toDouble()!!, newMonth, newYear))
                                }
                            }
                            else {
                                val newMonth = AppUtils.getMonthNoFromReverseFormat(AppUtils.getCurrentDateFormatInTa(orderList[i].only_date!!))
                                val newYear = AppUtils.getYearFromReverseFormat(AppUtils.getCurrentDateFormatInTa(orderList[i].only_date!!))

                                if (month == newMonth && year == newYear) {
                                    newAmount += orderList[i].amount?.toDouble()!!
                                    //newOrderList.add(NewOrderModel(newAmount, newMonth, newYear))
                                }
                                else {
                                    newOrderList.add(NewOrderModel(newAmount, month, year))
                                    newAmount = orderList[i].amount?.toDouble()!!
                                }

                                month = newMonth
                                year = newYear
                            }
                        }
                    }

                    val finalAmount = String.format("%.2f", amount.toFloat())

                    val builder = SpannableStringBuilder()

                    val str1 = SpannableString("Total Order Value (till now): ")
                    builder.append(str1)

                    val str2 = SpannableString("₹ $finalAmount")
                    str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                    builder.append(str2)
                    itemView.order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    builder.clear()
                    val str3 = SpannableString("Average Order Value (till now): ")
                    builder.append(str3)

                    var avgOrder = "0.00"
                    if (amount.toInt() != 0)
                        avgOrder = String.format("%.2f", (amount.toFloat() / orderList.size))
                    val str4 = SpannableString("₹ $avgOrder")
                    str4.setSpan(ForegroundColorSpan(Color.BLACK), 0, str4.length, 0)
                    builder.append(str4)
                    itemView.avg_order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    builder.clear()
                    val str5 = SpannableString("Highest Order Value (till now): ")
                    builder.append(str5)

                    var maxOrder = "0.00"
                    if (amountList.isNotEmpty())
                        maxOrder = String.format("%.2f", amountList.maxOrNull()?.toFloat())
                    val str6 = SpannableString("₹ $maxOrder")
                    str6.setSpan(ForegroundColorSpan(Color.BLACK), 0, str6.length, 0)
                    builder.append(str6)
                    itemView.highest_order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    builder.clear()
                    val str7 = SpannableString("Lowest Order Value (till now): ")
                    builder.append(str7)

                    var minOrder = "0.00"
                    if (amountList.isNotEmpty())
                        minOrder = String.format("%.2f", amountList.minOrNull()?.toFloat())
                    val str8 = SpannableString("₹ $minOrder")
                    str8.setSpan(ForegroundColorSpan(Color.BLACK), 0, str8.length, 0)
                    builder.append(str8)
                    itemView.lowest_order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    newOrderList.forEachIndexed { i, it ->
                        if (i == 0) {
                            max = it.amount
                            min = it.amount
                            maxIndex = i
                            minIndex = i
                        }
                        else {
                            if (it.amount > max) {
                                max = it.amount
                                maxIndex = i
                            }

                            if (it.amount < min) {
                                min = it.amount
                                minIndex = i
                            }
                        }
                    }

                    builder.clear()
                    val str9 = SpannableString("Month of High value Business: ")
                    builder.append(str9)

                    val str10 = SpannableString(AppUtils.getMonthFromValue(newOrderList[maxIndex].month) + ", " + newOrderList[maxIndex].year)
                    str10.setSpan(ForegroundColorSpan(Color.BLACK), 0, str10.length, 0)
                    builder.append(str10)
                    itemView.high_value_month_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    builder.clear()
                    val str11 = SpannableString("Month of Low value Business: ")
                    builder.append(str11)

                    val str12 = SpannableString(AppUtils.getMonthFromValue(newOrderList[minIndex].month) + ", " + newOrderList[minIndex].year)
                    str12.setSpan(ForegroundColorSpan(Color.BLACK), 0, str12.length, 0)
                    builder.append(str12)
                    itemView.low_value_month_tv.setText(builder, TextView.BufferType.SPANNABLE)
                } else {
                    val builder = SpannableStringBuilder()

                    val str1 = SpannableString("Total Order Value (till now): ")
                    builder.append(str1)

                    val str2 = SpannableString("₹ 0.00")
                    str2.setSpan(ForegroundColorSpan(Color.BLACK), 0, str2.length, 0)
                    builder.append(str2)
                    itemView.order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    builder.clear()
                    val str3 = SpannableString("Average Order Value (till now): ")
                    builder.append(str3)

                    val str4 = SpannableString("₹ 0.00")
                    str4.setSpan(ForegroundColorSpan(Color.BLACK), 0, str4.length, 0)
                    builder.append(str4)
                    itemView.avg_order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    builder.clear()
                    val str5 = SpannableString("Highest Order Value (till now): ")
                    builder.append(str5)

                    val str6 = SpannableString("₹ 0.00")
                    str6.setSpan(ForegroundColorSpan(Color.BLACK), 0, str6.length, 0)
                    builder.append(str6)
                    itemView.highest_order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    builder.clear()
                    val str7 = SpannableString("Lowest Order Value (till now): ")
                    builder.append(str7)

                    val str8 = SpannableString("₹ 0.00")
                    str8.setSpan(ForegroundColorSpan(Color.BLACK), 0, str8.length, 0)
                    builder.append(str8)
                    itemView.lowest_order_amount_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    builder.clear()
                    val str9 = SpannableString("Month of High value Business: ")
                    builder.append(str9)

                    val str10 = SpannableString("N.A.")
                    str10.setSpan(ForegroundColorSpan(Color.BLACK), 0, str10.length, 0)
                    builder.append(str10)
                    itemView.high_value_month_tv.setText(builder, TextView.BufferType.SPANNABLE)

                    builder.clear()
                    val str11 = SpannableString("Month of Low value Business: ")
                    builder.append(str11)

                    val str12 = SpannableString("N.A.")
                    str12.setSpan(ForegroundColorSpan(Color.BLACK), 0, str12.length, 0)
                    builder.append(str12)
                    itemView.low_value_month_tv.setText(builder, TextView.BufferType.SPANNABLE)
                }

                val shopActivityList = AppDatabase.getDBInstance()?.shopActivityDao()?.getShopActivityForIdDescVisitDate(list[adapterPosition].shop_id)
                if (shopActivityList != null && shopActivityList.isNotEmpty()) {

                    var averageTimeSpent = ""
                    var totalTimeSpent = 0L

                    shopActivityList.forEach {
                        if (!TextUtils.isEmpty(it.totalMinute))
                            totalTimeSpent += it.totalMinute.toLong()
                    }

                    averageTimeSpent = if (!TextUtils.isEmpty(list[adapterPosition].totalVisitCount) && totalTimeSpent != 0L)
                        AppUtils.getHourMinuteFromMins(totalTimeSpent / list[adapterPosition].totalVisitCount.toLong())
                    else
                        "00:00"

                    itemView.tv_avg_visit_time.text = "$averageTimeSpent (hh:mm Approx.)"
                }

                val distance = LocationWizard.getDistance(list[adapterPosition].shopLat, list[adapterPosition].shopLong,
                        Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
                itemView.tv_distance.text = "$distance (Approx. from current location)"

                val lastVisitAge = AppUtils.getDayFromSubtractDates(AppUtils.getLongTimeStampFromDate2(list[adapterPosition].lastVisitedDate),
                        AppUtils.convertDateStringToLong(AppUtils.getCurrentDateForShopActi()))
                itemView.tv_last_visit_age.text = "$lastVisitAge Day(s)"

                itemView.share_icon.setOnClickListener {
                    onShareClick(list[adapterPosition])
                }

                itemView.tv_shop_contact_no.text = list[adapterPosition].ownerName + " (${list[adapterPosition].ownerContactNumber})"
                itemView.tv_shop_contact_no.setOnClickListener {
                    IntentActionable.initiatePhoneCall(mContext, list[adapterPosition].ownerContactNumber)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateAdapter(mlist: List<AddShopDBModelEntity>) {
        list.clear()
        list.addAll(mlist)
        notifyDataSetChanged()
    }
}