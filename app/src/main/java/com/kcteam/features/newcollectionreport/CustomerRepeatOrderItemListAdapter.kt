package com.kcteam.features.newcollectionreport

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.lead.model.CustomerLeadList
import com.kcteam.features.nearbyshops.model.NewOrderModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.inflater_customer_list_item.view.*


class CustomerRepeatOrderItemListAdapter(context: Context, list: ArrayList<AddShopDBModelEntity>, val listener: RepeatOrderShopsListClickListener,private val getSize: (Int) -> Unit) :
        RecyclerView.Adapter<CustomerRepeatOrderItemListAdapter.MyViewHolder>(), Filterable {
    private val layoutInflater: LayoutInflater
    private var context: Context
    private var mList: ArrayList<AddShopDBModelEntity>
    private var tempList: ArrayList<AddShopDBModelEntity>? = null
    private var filterList: ArrayList<AddShopDBModelEntity>? = null

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        mList= ArrayList()
        tempList=ArrayList()
        filterList = ArrayList()

        mList?.addAll(list)
        tempList?.addAll(list)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, mList, listener)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflater_customer_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newOrderList = ArrayList<NewOrderModel>()
        fun bindItems(context: Context, list: List<AddShopDBModelEntity>, listener: RepeatOrderShopsListClickListener) {
            try {
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
                    listener.orderClick(list.get(adapterPosition))
                    //(context as DashboardActivity).showSnackMessage(context.getString(R.string.functionality_disabled))
                })


                itemView.total_visited_value_TV.text = " " + list[adapterPosition].totalVisitCount
                itemView.last_visited_date_TV.text = " " + list[adapterPosition].lastVisitedDate
                itemView.sync_icon.visibility = View.VISIBLE
                if (list[adapterPosition].isUploaded) {
                    if (list[adapterPosition].isEditUploaded == 0) {
                        itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                        itemView.sync_icon.setOnClickListener(View.OnClickListener {
                            listener.syncClick(adapterPosition)
                        })
                    } else
                        itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                } else {
                    itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    itemView.sync_icon.setOnClickListener(View.OnClickListener {
                        listener.syncClick(adapterPosition)
                    })
                }

                val shopType = AppDatabase.getDBInstance()?.shopTypeDao()?.getSingleType(list[adapterPosition].type)

                if (shopType != null && !TextUtils.isEmpty(shopType.shoptype_name)) {
                    itemView.tv_type.text = shopType.shoptype_name
                    itemView.ll_shop_type.visibility = View.VISIBLE
                } else
                    itemView.ll_shop_type.visibility = View.GONE

                if(Pref.isCollectioninMenuShow) {
                    itemView.ll_collection.visibility = View.VISIBLE
                    itemView.collection_view.visibility = View.VISIBLE
                }
                else {
                    itemView.ll_collection.visibility = View.GONE
                    itemView.collection_view.visibility = View.GONE
                }

                itemView.ll_collection.setOnClickListener {
                    listener.onCollectionClick(adapterPosition)
                }

                itemView.shop_list_LL.setOnClickListener(View.OnClickListener {
                    listener.OnNearByShopsListClick(adapterPosition)
                })

                itemView.tag_iv.setOnClickListener(View.OnClickListener {
                    (context as DashboardActivity).loadFragment(FragType.MarketingPagerFragment, true, list[adapterPosition].shop_id)
                })


                itemView.ll_stock.setOnClickListener {
                    listener.onStockClick(adapterPosition)
                }

                itemView.ll_activity.setOnClickListener {
                    listener.onActivityClick(adapterPosition)
                }

                itemView.share_icon.setOnClickListener {
                    listener.onShareClick(adapterPosition)
                }

                itemView.lead_new_question_ll.setOnClickListener {
                    listener.onQuestionnarieClick(list[adapterPosition].shop_id!!)
                }
                /*17-12-2021 modify*/
                if(Pref.IsReturnEnableforParty) {
                    if(Pref.IsReturnActivatedforPP){
                        if(list[adapterPosition].type!!.equals("2")){
                            itemView.lead_return_ll.visibility = View.VISIBLE
                            itemView.lead_return_view.visibility =  View.VISIBLE
                        }
                        else{
                            itemView.lead_return_ll.visibility = View.GONE
                            itemView.lead_return_view.visibility = View.GONE
                        }
                    }
                    else if(Pref.IsReturnActivatedforDD){
                        if(list[adapterPosition].type!!.equals("4")){
                            itemView.lead_return_ll.visibility = View.VISIBLE
                            itemView.lead_return_view.visibility =  View.VISIBLE
                        }
                        else{
                            itemView.lead_return_ll.visibility = View.GONE
                            itemView.lead_return_view.visibility = View.GONE
                        }
                    }
                    else if(Pref.IsReturnActivatedforSHOP){
                        if(list[adapterPosition].type!!.equals("1")){
                            itemView.lead_return_ll.visibility = View.VISIBLE
                            itemView.lead_return_view.visibility =  View.VISIBLE
                        }
                        else{
                            itemView.lead_return_ll.visibility = View.GONE
                            itemView.lead_return_view.visibility = View.GONE
                        }
                    }
                }
                else{
                    itemView.lead_return_ll.visibility = View.GONE
                    itemView.lead_return_view.visibility = View.GONE
                }
                /*20-12-2021*/
                val OrderavalibleByShopId = AppDatabase.getDBInstance()?.orderDetailsListDao()?.getListAccordingToShopId(list[adapterPosition].shop_id) as ArrayList<OrderDetailsListEntity>
                if(OrderavalibleByShopId.size>0){
                    //itemView.lead_return_ll.isEnabled=true
                    itemView.lead_return_ll.setOnClickListener {
                        listener.onReturnClick(adapterPosition)
                    }
                }
                else{
                    itemView.lead_return_ll.setOnClickListener {
                        Toaster.msgShort(context,"No Minimum Order Avalible to return.")
                    }
                    //itemView.lead_return_ll.isEnabled=false
                }


                if(Pref.IsnewleadtypeforRuby && list[adapterPosition].type.equals("16")){
                    itemView.lead_new_question_view.visibility = View.VISIBLE
                    itemView.lead_new_question_ll.visibility = View.VISIBLE
                }
                else{
                    itemView.lead_new_question_view.visibility = View.GONE
                    itemView.lead_new_question_ll.visibility = View.GONE
                }






                if (Pref.willStockShow) {
                    if (Pref.isStockAvailableForAll) {
                        itemView.ll_stock.visibility = View.VISIBLE
                        itemView.stock_view.visibility = View.VISIBLE
                    } else {
                        if (list[adapterPosition].type == "4") {
                            itemView.ll_stock.visibility = View.VISIBLE
                            itemView.stock_view.visibility = View.VISIBLE
                        } else {
                            itemView.ll_stock.visibility = View.GONE
                            itemView.stock_view.visibility = View.GONE
                        }
                    }
                }
                else {
                    itemView.ll_stock.visibility = View.GONE
                    itemView.stock_view.visibility = View.GONE
                }
                itemView.tv_shop_contact_no.text = list[adapterPosition].ownerName + " (${list[adapterPosition].ownerContactNumber})"




                if (list[adapterPosition].type == "7" || list[adapterPosition].type == "8") {
                    itemView.ll_activity.visibility = View.VISIBLE
                    itemView.activity_view.visibility = View.VISIBLE
                }
                else if (Pref.willActivityShow) {
                    itemView.ll_activity.visibility = View.VISIBLE
                    itemView.activity_view.visibility = View.VISIBLE
                }
                else {
                    itemView.ll_activity.visibility = View.GONE
                    itemView.activity_view.visibility = View.GONE
                }


                Log.e("Shop List", "Shop name=======> " + list[adapterPosition].shopName)
                Log.e("Shop List", "Shop Id=======> " + list[adapterPosition].shop_id)
                Log.e("Shop List", "Stage Id=======> " + list[adapterPosition].stage_id)

                if (Pref.isCustomerFeatureEnable) {

                    itemView.ll_dd_name.visibility = View.GONE





                    when (list[adapterPosition].type) {
                        "1" -> {
                            /*itemView.run {
                                            tv_type.text = context.getString(R.string.shop_type)
                                        }*/

                            if (!TextUtils.isEmpty(list[adapterPosition].assigned_to_dd_id)) {
                                list[adapterPosition].assigned_to_dd_id.let {
                                    AppDatabase.getDBInstance()?.ddListDao()?.getSingleValue(it)
                                }?.run {
                                    itemView.also {
                                        if (!TextUtils.isEmpty(dd_name)) {
                                            it.tv_dd_name.text = dd_name
                                            it.ll_dd_name.visibility = View.VISIBLE
                                        } else
                                            it.ll_dd_name.visibility = View.GONE
                                    }
                                }
                            } else
                                itemView.ll_dd_name.visibility = View.GONE

                        }
                        "2" -> {
                            itemView.run {
                                //tv_type.text = context.getString(R.string.pp_type)
                                ll_dd_name.visibility = View.GONE
                            }
                        }
                        "3" -> {
                            itemView.run {
                                //tv_type.text = context.getString(R.string.new_party_type)
                                ll_dd_name.visibility = View.GONE
                            }
                        }
                        "4" -> {
                            itemView.run {
                                //tv_type.text = context.getString(R.string.distributor_type)
                                ll_dd_name.visibility = View.GONE
                            }
                        }
                        "5" -> {
                            /*itemView.run {
                                            tv_type.text = context.getString(R.string.diamond_type)
                                        }*/

                            if (!TextUtils.isEmpty(list[adapterPosition].assigned_to_dd_id)) {
                                list[adapterPosition].assigned_to_dd_id.let {
                                    AppDatabase.getDBInstance()?.ddListDao()?.getSingleValue(it)
                                }?.run {
                                    itemView.also {
                                        if (!TextUtils.isEmpty(dd_name)) {
                                            it.tv_dd_name.text = dd_name
                                            it.ll_dd_name.visibility = View.VISIBLE
                                        } else
                                            it.ll_dd_name.visibility = View.GONE
                                    }
                                }
                            } else
                                itemView.ll_dd_name.visibility = View.GONE
                        }
                        else -> {
                            itemView.ll_dd_name.visibility = View.GONE
                        }
                    }
                }

                val shopActivityList = AppDatabase.getDBInstance()?.shopActivityDao()?.getShopActivityForIdDescVisitDate(list[adapterPosition].shop_id)
                if (shopActivityList != null && shopActivityList.isNotEmpty()) {

                    var averageTimeSpent = ""
                    var totalTimeSpent = 0L

                    shopActivityList.forEach {
                        if (!TextUtils.isEmpty(it.totalMinute))
                            totalTimeSpent += it.totalMinute.toLong()
                    }

                    averageTimeSpent =
                        if (!TextUtils.isEmpty(list[adapterPosition].totalVisitCount) && totalTimeSpent != 0L)
                            AppUtils.getHourMinuteFromMins(totalTimeSpent / list[adapterPosition].totalVisitCount.toLong())
                        else
                            "00:00"
                }

                    itemView.iv_whatsapp.setOnClickListener {
                        listener.onWhatsAppClick(list[adapterPosition].ownerContactNumber)
                    }

                    if (Pref.isShowSmsForParty)
                        itemView.iv_sms.visibility = View.VISIBLE
                    else
                        itemView.iv_sms.visibility = View.GONE

                    itemView.iv_sms.setOnClickListener {
                        listener.onSmsClick(list[adapterPosition].ownerContactNumber)
                    }

                    val lastVisitAge = AppUtils.getDayFromSubtractDates(AppUtils.getLongTimeStampFromDate2(list[adapterPosition].lastVisitedDate), AppUtils.convertDateStringToLong(AppUtils.getCurrentDateForShopActi()))
                    itemView.tv_last_visit_age.text = "$lastVisitAge Day(s)"

                    if (Pref.isCreateQrCode)
                        itemView.iv_create_qr.visibility = View.VISIBLE
                    else
                        itemView.iv_create_qr.visibility = View.GONE

                    itemView.iv_create_qr.setOnClickListener {
                        listener.onCreateQrClick(adapterPosition)
                    }



                    if (Pref.willShowEntityTypeforShop && list[adapterPosition].type == "1")
                        itemView.rl_entity_type.visibility = View.VISIBLE
                    else
                        itemView.rl_entity_type.visibility = View.GONE

                    if (!TextUtils.isEmpty(list[adapterPosition].entity_id)) {
                        val entity = AppDatabase.getDBInstance()?.entityDao()?.getSingleItem(list[adapterPosition].entity_id)
                        itemView.tv_entity_type.text = entity?.name
                    } else
                        itemView.tv_entity_type.text = "N.A."

                    if (!TextUtils.isEmpty(list[adapterPosition].party_status_id)) {
                        val partyStatus = AppDatabase.getDBInstance()?.partyStatusDao()?.getSingleItem(list[adapterPosition].party_status_id)
                        itemView.tv_party_status.text = partyStatus?.name
                    } else
                        itemView.tv_party_status.text = "N.A."


                    if (Pref.IsFeedbackHistoryActivated) {
                        itemView.shop_history_ll.visibility = View.VISIBLE
                        itemView.shop_history_view.visibility = View.VISIBLE
                    } else {
                        itemView.shop_history_ll.visibility = View.GONE
                        itemView.shop_history_view.visibility = View.GONE
                    }


                    itemView.shop_history_ll.setOnClickListener {
                        listener.onHistoryClick(list[adapterPosition])
                    }

                    //Hardcoded for EuroBond
                    //itemView.ll_last_visit_age.visibility=View.GONE
                    //itemView.ll_average_visit_time.visibility=View.GONE
                    //itemView.ll_distance.visibility=View.GONE
                    //itemView.order_amount_tv.visibility=View.GONE
                    //itemView.highest_order_amount_tv.visibility=View.GONE
                    //itemView.avg_order_amount_tv.visibility=View.GONE
                    //itemView.lowest_order_amount_tv.visibility=View.GONE
                    //itemView.high_value_month_tv.visibility=View.GONE
                    //itemView.low_value_month_tv.visibility=View.GONE


                itemView.lead_return_ll.visibility=View.GONE
                itemView.shop_history_view.visibility=View.GONE
                itemView.lead_new_question_ll.visibility=View.GONE
                itemView.lead_return_view.visibility=View.GONE
                itemView.lead_new_question_view.visibility=View.GONE
                itemView.ll_collection.visibility=View.GONE
                itemView.collection_view.visibility=View.GONE
                itemView.ll_activity.visibility=View.GONE
                itemView.activity_view.visibility=View.GONE
                itemView.ll_stock.visibility=View.GONE
                itemView.stock_view.visibility=View.GONE
                itemView.call_ll.visibility=View.GONE
                itemView.call_view.visibility=View.GONE
                itemView.add_quot_ll.visibility=View.GONE
                itemView.direction_ll.visibility=View.GONE
                itemView.direction_view.visibility=View.GONE
                itemView.order_view.visibility=View.GONE
                itemView.shop_history_ll.visibility=View.GONE



            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    fun updateAdapter(mlist: ArrayList<AddShopDBModelEntity>) {
        this.mList = mlist
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterList?.clear()

            tempList?.indices!!
                .filter { tempList?.get(it)?.shopName?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!}
                .forEach { filterList?.add(tempList?.get(it)!!) }
            results.values = filterList
            results.count = filterList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterList = results?.values as ArrayList<AddShopDBModelEntity>?
                mList?.clear()
                val hashSet = HashSet<String>()
                if (filterList != null) {

                    filterList?.indices!!
                        .filter { hashSet.add(filterList?.get(it)?.shopName!!) }
                        .forEach { mList?.add(filterList?.get(it)!!) }

                    getSize(mList?.size!!)

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshList(list: ArrayList<AddShopDBModelEntity>) {
        mList?.clear()
        mList?.addAll(list)

        tempList?.clear()
        tempList?.addAll(list)

        if (filterList == null)
            filterList = ArrayList()
        filterList?.clear()

        notifyDataSetChanged()
    }

}