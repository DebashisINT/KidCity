package com.kcteam.features.averageshop.presentation

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_avg_shop_item.view.*
import kotlinx.android.synthetic.main.inflate_avg_shop_item.view.activity_view
import kotlinx.android.synthetic.main.inflate_avg_shop_item.view.myshop_gstin_TV
import kotlinx.android.synthetic.main.inflate_avg_shop_item.view.myshop_pancard_TV
import kotlinx.android.synthetic.main.inflate_avg_shop_item.view.shop_damage_ll
import kotlinx.android.synthetic.main.inflate_avg_shop_item.view.shop_damage_view
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.*
import kotlinx.android.synthetic.main.inflate_registered_shops.view.*
import kotlinx.android.synthetic.main.inflate_registered_shops.view.add_order_ll
import kotlinx.android.synthetic.main.inflate_registered_shops.view.add_quot_ll
import kotlinx.android.synthetic.main.inflate_registered_shops.view.call_ll
import kotlinx.android.synthetic.main.inflate_registered_shops.view.call_view
import kotlinx.android.synthetic.main.inflate_registered_shops.view.direction_ll
import kotlinx.android.synthetic.main.inflate_registered_shops.view.direction_view
import kotlinx.android.synthetic.main.inflate_registered_shops.view.last_visited_RL
import kotlinx.android.synthetic.main.inflate_registered_shops.view.last_visited_date_TV
import kotlinx.android.synthetic.main.inflate_registered_shops.view.ll_activity
import kotlinx.android.synthetic.main.inflate_registered_shops.view.ll_shop_code
import kotlinx.android.synthetic.main.inflate_registered_shops.view.ll_stage
import kotlinx.android.synthetic.main.inflate_registered_shops.view.ll_stock
import kotlinx.android.synthetic.main.inflate_registered_shops.view.menu_IV
import kotlinx.android.synthetic.main.inflate_registered_shops.view.myshop_address_TV
import kotlinx.android.synthetic.main.inflate_registered_shops.view.myshop_name_TV
import kotlinx.android.synthetic.main.inflate_registered_shops.view.order_RL
import kotlinx.android.synthetic.main.inflate_registered_shops.view.order_view
import kotlinx.android.synthetic.main.inflate_registered_shops.view.shop_IV
import kotlinx.android.synthetic.main.inflate_registered_shops.view.stock_view
import kotlinx.android.synthetic.main.inflate_registered_shops.view.sync_icon
import kotlinx.android.synthetic.main.inflate_registered_shops.view.total_visited_RL
import kotlinx.android.synthetic.main.inflate_registered_shops.view.tv_shop_code
import kotlinx.android.synthetic.main.inflate_registered_shops.view.tv_shop_contact_no
import kotlinx.android.synthetic.main.inflate_registered_shops.view.update_address_TV
import kotlinx.android.synthetic.main.inflate_registered_shops.view.update_stage_TV
import kotlinx.android.synthetic.main.inflate_avg_shop_item.view.add_multiple_ll
import kotlinx.android.synthetic.main.inflate_avg_shop_item.view.multiple_tv
import kotlinx.android.synthetic.main.inflate_avg_shop_item.view.new_multi_view
import java.util.*

/**
 * Created by Pratishruti on 15-11-2017.
 */
class AverageShopListAdapter(context: Context, userLocationDataEntity: List<ShopActivityEntity>, val listener: AverageShopListClickListener) : RecyclerView.Adapter<AverageShopListAdapter.MyViewHolder>() {
    private val layoutInflater: LayoutInflater
    private var context: Context
    private var shopType = ""
    var userLocationDataEntity: List<ShopActivityEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, userLocationDataEntity, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_avg_shop_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return userLocationDataEntity.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, userLocationDataEntity: List<ShopActivityEntity>, listener: AverageShopListClickListener) {

            try {

                //itemView.iconWrapper_rl.visibility = View.GONE
                itemView.call_ll.visibility = View.GONE
                itemView.call_view.visibility = View.GONE
                itemView.direction_ll.visibility = View.GONE
                itemView.direction_view.visibility = View.GONE
                itemView.order_RL.visibility = View.GONE
                itemView.total_visited_RL.visibility = View.GONE
                itemView.last_visited_RL.visibility = View.GONE
                itemView.ll_stage.visibility = View.GONE
                itemView.update_stage_TV.visibility = View.GONE

                val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(userLocationDataEntity[adapterPosition].shopid)
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

                        } else {
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

                            } else {
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
                            } else {
                                itemView.add_order_ll.visibility = View.GONE
                                itemView.order_view.visibility = View.GONE
                            }
                        }
                    }
                }
                else {
                    itemView.ll_stock.visibility = View.GONE
                    itemView.stock_view.visibility = View.GONE

                    if (Pref.isOrderShow) {
                        itemView.add_order_ll.visibility = View.VISIBLE

                        if (Pref.isQuotationShow)
                            itemView.order_view.visibility = View.VISIBLE
                        else
                            itemView.order_view.visibility = View.GONE
                    } else {
                        itemView.add_order_ll.visibility = View.GONE
                        itemView.order_view.visibility = View.GONE
                    }

                }

//            Picasso.with(context).load(userLocationDataEntity[adapterPosition].shopImageLocalPath).into(itemView.shop_image_IV);
                itemView.myshop_name_TV.text = userLocationDataEntity[adapterPosition].shop_name
                itemView.myshop_address_TV.text = userLocationDataEntity[adapterPosition].shop_address

                if (shop != null && shop.isUploaded) {

                    if (userLocationDataEntity[adapterPosition].isUploaded && userLocationDataEntity[adapterPosition].isDurationCalculated) {
                        itemView.sync_icon.visibility = View.VISIBLE

                        val list = AppDatabase.getDBInstance()!!.shopVisitImageDao().getTodaysUnSyncedListAccordingToShopId(false, userLocationDataEntity[adapterPosition].shopid!!,
                                userLocationDataEntity[adapterPosition].visited_date!!)

                        if (list != null && list.isNotEmpty()) {
                            itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                            itemView.sync_icon.setOnClickListener(View.OnClickListener {
                                listener.onSyncClick(adapterPosition)
                            })
                        } else {
                            val list_ = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getTodaysUnSyncedListAccordingToShopId(false, userLocationDataEntity[adapterPosition].shopid!!,
                                    userLocationDataEntity[adapterPosition].visited_date!!)

                            if (list_ != null && list_.isNotEmpty()) {
                                itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                                itemView.sync_icon.setOnClickListener(View.OnClickListener {
                                    listener.onSyncClick(adapterPosition)
                                })
                            }
                            else
                                itemView.sync_icon.setImageResource(R.drawable.ic_dashboard_green_tick_new)
                        }

                    } else {
                        if (userLocationDataEntity[adapterPosition].isDurationCalculated && !userLocationDataEntity[adapterPosition].isUploaded) {
                            itemView.sync_icon.visibility = View.VISIBLE
                            itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                            itemView.sync_icon.setOnClickListener(View.OnClickListener {
                                listener.onSyncClick(adapterPosition)
                            })
                        } else
                            itemView.sync_icon.visibility = View.GONE
                    }
                }
                else {
                    if (userLocationDataEntity[adapterPosition].isDurationCalculated && !userLocationDataEntity[adapterPosition].isUploaded) {
                        itemView.sync_icon.visibility = View.VISIBLE
                        itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                        itemView.sync_icon.setOnClickListener(View.OnClickListener {
                            listener.onSyncClick(adapterPosition)
                        })
                    } else
                        itemView.sync_icon.visibility = View.GONE
                }

                itemView.update_address_TV.visibility = View.GONE

//            if (userLocationDataEntity[adapterPosition].lastVisitedDate == "") {
//                var listnew = AppDatabase.getDBInstance()!!.addShopEntryDao().getVisitedShopListByName(userLocationDataEntity[adapterPosition].shop_name, true)
//                userLocationDataEntity[adapterPosition].totalVisitCount = listnew.size.toString()
//                userLocationDataEntity[adapterPosition].lastVisitedDate = listnew[listnew.size - 1].visitDate
//            }

//            itemView.total_visited_value_TV.setText(userLocationDataEntity[adapterPosition])
                itemView.last_visited_date_TV.text = userLocationDataEntity[adapterPosition].visited_date

                val drawable = TextDrawable.builder()
                        .buildRoundRect(userLocationDataEntity[adapterPosition].shop_name!!.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

                itemView.shop_IV.setImageDrawable(drawable)

                itemView.menu_IV.findViewById<ImageView>(R.id.menu_IV).setOnClickListener(View.OnClickListener {
                    listener.OnMenuClick(adapterPosition, itemView.menu_IV)
                })
//
                itemView.setOnClickListener {
                    listener.OnItemClick(adapterPosition)
                }

                itemView.tv_shop_contact_no.text = shop?.ownerName + " (${shop?.ownerContactNumber})"

                itemView.tv_shop_contact_no.setOnClickListener {
                    IntentActionable.initiatePhoneCall(context, shop?.ownerContactNumber)
                }

                itemView.ll_stock.setOnClickListener {
                    (context as DashboardActivity).loadFragment(FragType.StockListFragment, true, shop!!)
                }

                itemView.add_order_ll.setOnClickListener {
                    if(Pref.IsActivateNewOrderScreenwithSize){
                        (context as DashboardActivity).loadFragment(FragType.NewOrderScrOrderDetailsFragment, true, shop!!.shop_id)
                    }else{
                        (context as DashboardActivity).loadFragment(FragType.ViewAllOrderListFragment, true, shop!!)
                    }

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

                if (Pref.isEntityCodeVisible) {
                    if (!TextUtils.isEmpty(shop?.entity_code)) {
                        itemView.ll_shop_code.visibility = View.VISIBLE
                        itemView.tv_shop_code.text = shop?.entity_code
                    } else
                        itemView.ll_shop_code.visibility = View.GONE
                } else
                    itemView.ll_shop_code.visibility = View.GONE

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

                /* 12-12-2021*/
                itemView.lead_new_question_lll.setOnClickListener {
                    listener.onQuestionnarieClick(userLocationDataEntity[adapterPosition].shopid!!)
                }

                if(Pref.IsnewleadtypeforRuby && shop?.type!!.equals("16")){
                    itemView.lead_new_questions_view.visibility = View.VISIBLE
                    itemView.lead_new_question_lll.visibility = View.VISIBLE
                }
                else{
                    itemView.lead_new_questions_view.visibility = View.GONE
                    itemView.lead_new_question_lll.visibility = View.GONE
                }

                /*21-12-2021*/
                if(Pref.IsReturnEnableforParty) {
                    if(Pref.IsReturnActivatedforPP){
                        if(shop?.type!!.equals("2")){
                            itemView.lead_return_lll.visibility = View.VISIBLE
                            itemView.lead_return_vview.visibility =  View.VISIBLE
                        }
                        else{
                            itemView.lead_return_lll.visibility = View.GONE
                            itemView.lead_return_vview.visibility = View.GONE
                        }
                    }
                    else if(Pref.IsReturnActivatedforDD){
                        if(shop?.type!!.equals("4")){
                            itemView.lead_return_lll.visibility = View.VISIBLE
                            itemView.lead_return_vview.visibility =  View.VISIBLE
                        }
                        else{
                            itemView.lead_return_lll.visibility = View.GONE
                            itemView.lead_return_vview.visibility = View.GONE
                        }
                    }
                    else if(Pref.IsReturnActivatedforSHOP){
                        if(shop?.type!!.equals("1")){
                            itemView.lead_return_lll.visibility = View.VISIBLE
                            itemView.lead_return_vview.visibility =  View.VISIBLE
                        }
                        else{
                            itemView.lead_return_lll.visibility = View.GONE
                            itemView.lead_return_vview.visibility = View.GONE
                        }
                    }
                }
                else{
                    itemView.lead_return_lll.visibility = View.GONE
                    itemView.lead_return_vview.visibility = View.GONE
                }
                /*21-12-2021*/
    /*            val OrderavalibleByShopId = AppDatabase.getDBInstance()?.orderDetailsListDao()?.getListAccordingToShopId(userLocationDataEntity[adapterPosition].shopid!!) as ArrayList<OrderDetailsListEntity>
                if(OrderavalibleByShopId.size>0){
                    itemView.lead_return_lll.setOnClickListener {
                        listener.onReturnClick(adapterPosition)
                    }
                }
                else{
                    itemView.lead_return_lll.setOnClickListener {
                        Toaster.msgShort(context,"No Minimum Order Avalible to return.")
                    }
                }*/
                itemView.lead_return_lll.setOnClickListener {
                    val OrderavalibleByShopId = AppDatabase.getDBInstance()?.orderDetailsListDao()?.getListAccordingToShopId(userLocationDataEntity[adapterPosition].shopid!!) as ArrayList<OrderDetailsListEntity>
                    if(OrderavalibleByShopId.size>0){
                        listener.onReturnClick(adapterPosition)
                    }else{
                        Toaster.msgShort(context,"No Minimum Order Avalible to return.")
                    }

                }

                if (Pref.IsAllowBreakageTracking) {
                    itemView.shop_damage_ll.visibility = View.VISIBLE
                    itemView.shop_damage_view.visibility = View.VISIBLE
                }
                else {
                    itemView.shop_damage_ll.visibility = View.GONE
                    itemView.shop_damage_view.visibility = View.GONE
                }
                itemView.shop_damage_ll.setOnClickListener{
                    listener.onDamageClick(userLocationDataEntity[adapterPosition].shopid!!)
                }



                if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].device_model))
                    itemView.tv_device_model.text = userLocationDataEntity[adapterPosition].device_model

                if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].android_version))
                    itemView.tv_android_version.text = userLocationDataEntity[adapterPosition].android_version

                if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].battery))
                    itemView.tv_battery.text = userLocationDataEntity[adapterPosition].battery + "%"

                if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].net_status))
                    itemView.tv_net_status.text = userLocationDataEntity[adapterPosition].net_status

                if (!TextUtils.isEmpty(userLocationDataEntity[adapterPosition].net_type))
                    itemView.tv_net_type.text = userLocationDataEntity[adapterPosition].net_type



                //////////////////

                var currentViewSt=AppDatabase.getDBInstance()?.shopTypeStockViewStatusDao()?.getShopCurrentStockViewStatus(shop?.type!!)
                var competitorViewSt=AppDatabase.getDBInstance()?.shopTypeStockViewStatusDao()?.getShopCompetitorStockViewStatus(shop?.type!!)


                if(AppUtils.getSharedPreferencesCurrentStock(context)){
                    if(AppUtils.getSharedPreferencesCurrentStockApplicableForAll(context)){
                        itemView.ll_current_stock.visibility=View.VISIBLE
                        itemView.current_stock_view.visibility=View.VISIBLE
                    }else{
                        //if(shop?.type?.toInt() == 1 || shop?.type?.toInt() == 3){
                        if(currentViewSt==1){
                            itemView.ll_current_stock.visibility=View.VISIBLE
                            itemView.current_stock_view.visibility=View.VISIBLE
                        }
                    }
                }
                if(AppUtils.getSharedPreferencesIscompetitorStockRequired(context)){
                    if(!AppUtils.getSharedPreferencesIsCompetitorStockforParty(context)){
                        itemView.ll_competetor_stock.visibility=View.VISIBLE
                        itemView.competetor_stock_view.visibility=View.VISIBLE
                    }else{
                        //if(shop?.type?.toInt() == 1 || shop?.type?.toInt() == 3){
                        if(competitorViewSt==1){
                            itemView.ll_competetor_stock.visibility=View.VISIBLE
                            itemView.competetor_stock_view.visibility=View.VISIBLE
                        }
                    }
                }



                itemView.ll_current_stock.setOnClickListener{
                    (context as DashboardActivity).loadFragment(FragType.UpdateShopStockFragment, true, userLocationDataEntity[adapterPosition].shopid!!)
                }
                itemView.ll_competetor_stock.setOnClickListener{
                    (context as DashboardActivity).loadFragment(FragType.CompetetorStockFragment, true, userLocationDataEntity[adapterPosition].shopid!!)
                }


                var shopNameByID=""
                var type_id=AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(userLocationDataEntity[adapterPosition].shopid).type
                var type_name=AppDatabase.getDBInstance()!!.shopTypeDao().getShopNameById(type_id)

                itemView.myyshop_Type_TV.text = type_name

                if (Pref.willActivityShow) {
                    itemView.ll_activity.visibility = View.VISIBLE
                    itemView.activity_view.visibility = View.VISIBLE
                }else{
                    itemView.ll_activity.visibility = View.GONE
                    itemView.activity_view.visibility = View.GONE
                }


                if(Pref.IsSurveyRequiredforDealer && shop!!.type!!.equals("1")) {
                    itemView.shop_totalv_survey_ll.visibility = View.VISIBLE
                    itemView.shop_totalv_survey_view.visibility = View.VISIBLE
                }
                else if(Pref.IsSurveyRequiredforNewParty && shop!!.type!!.equals("3")){
                    itemView.shop_totalv_survey_ll.visibility = View.VISIBLE
                    itemView.shop_totalv_survey_view.visibility = View.VISIBLE
                }
                else{
                    itemView.shop_totalv_survey_ll.visibility = View.GONE
                    itemView.shop_totalv_survey_view.visibility = View.GONE
                }

                itemView.shop_totalv_survey_ll.setOnClickListener{
                    listener.onSurveyClick(shop!!.shop_id!!)
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val shopGSTINNumber = AppDatabase.getDBInstance()?.addShopEntryDao()
                    ?.getGSTINNumber(userLocationDataEntity[adapterPosition].shopid)
                val shopPANNumber = AppDatabase.getDBInstance()?.addShopEntryDao()
                    ?.getPancardNumber(userLocationDataEntity[adapterPosition].shopid)
                if (Pref.IsGSTINPANEnableInShop) {
                    if (shopGSTINNumber!!.isNotEmpty()) {
                        itemView.myshop_gstin_TV.text = "GSTIN : " + shopGSTINNumber
                        itemView.myshop_gstin_TV.visibility = View.VISIBLE
                    } else {
                        itemView.myshop_Gstin_TV.text = "GSTIN : " + "N.A"
                        itemView.myshop_gstin_TV.visibility = View.VISIBLE
                    }
                }
                else {
                    itemView.myshop_gstin_TV.visibility = View.GONE
                }
                if (Pref.IsGSTINPANEnableInShop) {
                    if (shopPANNumber!!.isNotEmpty()) {
                        itemView.myshop_pancard_TV.text = "PAN     : " + shopPANNumber
                        itemView.myshop_pancard_TV.visibility = View.VISIBLE
                    } else {
                        itemView.myshop_pancard_TV.text = "PAN     : " + "N.A"
                        itemView.myshop_pancard_TV.visibility = View.VISIBLE
                    }
                } else {
                    itemView.myshop_pancard_TV.visibility = View.GONE
                }
            }
            catch (ex:Exception){
                itemView.myshop_gstin_TV.text = "GSTIN : "+"N.A"
                itemView.myshop_pancard_TV.text = "PAN     : "+"N.A"
            }

            if(Pref.IsMultipleImagesRequired){
                itemView.add_multiple_ll.visibility = View.VISIBLE
                itemView.new_multi_view.visibility = View.GONE
                itemView.add_multiple_ll.setOnClickListener {
                    listener.onMultipleImageClick(userLocationDataEntity[adapterPosition],adapterPosition)
                }
            }
            else{
                itemView.add_multiple_ll.visibility = View.GONE
                itemView.new_multi_view.visibility = View.GONE
            }
        }
    }

    open fun updateList(locationDataEntity: List<ShopActivityEntity>) {
        Collections.reverse(locationDataEntity)
        userLocationDataEntity = locationDataEntity
        notifyDataSetChanged()
    }
}