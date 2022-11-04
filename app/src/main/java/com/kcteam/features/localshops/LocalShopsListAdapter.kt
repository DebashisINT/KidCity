package com.kcteam.features.localshops

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.features.location.LocationWizard
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.*
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.add_order_ll
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.add_quot_ll
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.call_ll
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.direction_ll
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.direction_view
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.last_visited_date_TV
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.ll_shop_code
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.myshop_address_TV
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.myshop_name_TV
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.order_RL
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.order_amt_p_TV
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.order_view
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.shop_IV
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.shop_damage_ll
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.shop_damage_view
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.shop_image_IV
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.shop_list_LL
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.total_v_TV
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.total_visited_value_TV
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.tv_shop_code
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.tv_shop_contact_no
import kotlinx.android.synthetic.main.inflate_registered_shops.view.*

/**
 * Created by riddhi on 2/1/18.
 */

class LocalShopsListAdapter(context: Context, list: List<AddShopDBModelEntity>, val listener: LocalShopListClickListener,private val getSize: (Int) -> Unit) :
        RecyclerView.Adapter<LocalShopsListAdapter.MyViewHolder>(), Filterable {
    private val layoutInflater: LayoutInflater
    private var context: Context
    private var mList: ArrayList<AddShopDBModelEntity>

    private var tempList: ArrayList<AddShopDBModelEntity>? = null
    private var filterList: ArrayList<AddShopDBModelEntity>? = null



    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context

        //mList = list as ArrayList<AddShopDBModelEntity>
        tempList = ArrayList()
        filterList = ArrayList()
        mList = ArrayList()

        tempList?.addAll(list)
        mList?.addAll(list as ArrayList<AddShopDBModelEntity>)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, mList, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_nearby_shops, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: List<AddShopDBModelEntity>, listener: LocalShopListClickListener) {
            //Picasso.with(context).load(list[adapterPosition].shopImageLocalPath).into(itemView.shop_image_IV)

            try{
                var shopDetailsProjectName=""
                shopDetailsProjectName = AppDatabase.getDBInstance()!!.addShopEntryDao().getProjectName(list[adapterPosition].shop_id)

                var shopDetailLand=""
                shopDetailLand = AppDatabase.getDBInstance()!!.addShopEntryDao().getLand(list[adapterPosition].shop_id)

                if(Pref.IslandlineforCustomer && shopDetailLand!=null){
                    itemView.tv_landline.text = shopDetailLand
                    itemView.tv_landline.visibility = View.VISIBLE
                }
                else{
                    itemView.tv_landline.text = "N.A"
                    itemView.tv_landline.visibility = View.GONE
                }
                if(Pref.IsprojectforCustomer && shopDetailsProjectName!=null){
                    itemView.project_name.text = "Project Name :"+shopDetailsProjectName
                    itemView.project_name.visibility = View.VISIBLE
                }
                else{
                    itemView.project_name.text = "Project Name : N.A"
                    itemView.project_name.visibility = View.GONE
                }

            }
            catch (ex:Exception){
            }

            itemView.total_v_TV.text = context.getString(R.string.total_visits)

            if (!TextUtils.isEmpty(list[adapterPosition].shopImageLocalPath)) {
                Picasso.get()
                        .load(list[adapterPosition].shopImageLocalPath)
                        .resize(100, 100)
                        .into(itemView.shop_image_IV)
            }
            var shopNameByID=""
            if(list[adapterPosition].type!=null || !list[adapterPosition].type.equals("")){
                shopNameByID = AppDatabase.getDBInstance()!!.shopTypeDao().getShopNameById(list[adapterPosition].type)
            }
            else{
                shopNameByID = "N.A"
            }
            itemView.myshop_Type_TV.text = shopNameByID
            itemView.myshop_name_TV.text = list[adapterPosition].shopName
            var address: String = list[adapterPosition].address + ", " + list[adapterPosition].pinCode
            itemView.myshop_address_TV.text = address

            val drawable = TextDrawable.builder()
                    .buildRoundRect(list[adapterPosition].shopName.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

            itemView.shop_IV.setImageDrawable(drawable)

            itemView.shop_image_IV.findViewById<ImageView>(R.id.shop_image_IV).setOnClickListener(View.OnClickListener {
                //                listener.OnNearByShopsListClick(adapterPosition)
            })

            itemView.call_ll.findViewById<LinearLayout>(R.id.call_ll).setOnClickListener(View.OnClickListener {
                //                listener.callClick(adapterPosition)
            })

            itemView.direction_ll.findViewById<LinearLayout>(R.id.direction_ll).setOnClickListener(View.OnClickListener {
                //                listener.mapClick(adapterPosition)
            })

            itemView.add_order_ll.findViewById<LinearLayout>(R.id.add_order_ll).setOnClickListener {
                listener.onOrderClick(list[adapterPosition])
            }

            itemView.order_amt_p_TV.text = " " + context.getString(R.string.zero_order_in_value)
            itemView.total_visited_value_TV.text = " " + list[adapterPosition].totalVisitCount
            itemView.last_visited_date_TV.text = " " + list[adapterPosition].lastVisitedDate

            if (list[adapterPosition].visited) {
                itemView.visit_icon.visibility = View.VISIBLE
                if(Pref.isMultipleVisitEnable){
                    itemView.visit_TV.text = "Revisit Again"
                    itemView.visit_TV.setTextColor(ContextCompat.getColor(context, R.color.color_custom_red))
                    itemView.visit_icon.setColorFilter(ContextCompat.getColor(context,R.color.color_custom_red),android.graphics.PorterDuff.Mode.SRC_IN)
                }else {
                    itemView.visit_TV.text = "Revisited Today"
                    itemView.visit_TV.setTextColor(ContextCompat.getColor(context, R.color.maroon))
                    itemView.visit_icon.setColorFilter(ContextCompat.getColor(context,R.color.maroon),android.graphics.PorterDuff.Mode.SRC_IN)
                }
            } else {
                itemView.visit_icon.visibility = View.GONE

                /*if (Pref.isReplaceShopText)
                    itemView.visit_TV.text = "VISIT THIS CUSTOMER"
                else
                    itemView.visit_TV.text = "VISIT THIS SHOP"*/

                itemView.visit_TV.text = "Revisit Now"
                itemView.visit_TV.setTextColor(ContextCompat.getColor(context, R.color.color_custom_green))
            }

            itemView.shop_list_LL.setOnClickListener(View.OnClickListener {
                //                listener.OnNearByShopsListClick(adapterPosition)
            })

            itemView.visit_rl.setOnClickListener(View.OnClickListener {
                if (Pref.isMultipleVisitEnable) {
                    val list_ = AppDatabase.getDBInstance()!!.shopActivityDao().getShopForDay(list[adapterPosition].shop_id, AppUtils.getCurrentDateForShopActi())
                    /*if (list_ == null || list_.isEmpty())
                        listener.visitShop(list[adapterPosition])
                    else {
                        var isDurationCalculated = false
                        for (i in list_.indices) {
                            isDurationCalculated = list_[i].isDurationCalculated
                            if (!list_[i].isDurationCalculated)
                                break

                        }

                        if (isDurationCalculated)
                            listener.visitShop(list[adapterPosition])
                    }*/
                    listener.visitShop(list[adapterPosition])
                }
                else {
                    if (!list[adapterPosition].visited)
                        listener.visitShop(list[adapterPosition])
                }
            })

            itemView.tv_shop_contact_no.text = list[adapterPosition].ownerContactNumber

            if (!TextUtils.isEmpty(list[adapterPosition].assigned_to_dd_id)) {
                itemView.rl_dd.visibility = View.VISIBLE
                itemView.tv_pp_dd_header.text = context.getString(R.string.dist)

                val dd = AppDatabase.getDBInstance()?.ddListDao()?.getSingleValue(list[adapterPosition].assigned_to_dd_id)

                if (!TextUtils.isEmpty(dd?.dd_name) && !TextUtils.isEmpty(dd?.dd_phn_no))
                    itemView.tv_pp_dd_value.text = dd?.dd_name + " (" + dd?.dd_phn_no + ")"
                else if (!TextUtils.isEmpty(dd?.dd_name))
                    itemView.tv_pp_dd_value.text = dd?.dd_name

            }
            else if (!TextUtils.isEmpty(list[adapterPosition].assigned_to_pp_id)) {
                itemView.rl_dd.visibility = View.VISIBLE
                itemView.tv_pp_dd_header.text = context.getString(R.string.pp_super)

                val pp = AppDatabase.getDBInstance()?.ppListDao()?.getSingleValue(list[adapterPosition].assigned_to_pp_id)

                if (!TextUtils.isEmpty(pp?.pp_name) && !TextUtils.isEmpty(pp?.pp_phn_no))
                    itemView.tv_pp_dd_value.text = pp?.pp_name + " (" + pp?.pp_phn_no + ")"
                else if (!TextUtils.isEmpty(pp?.pp_name))
                    itemView.tv_pp_dd_value.text = pp?.pp_name

            }
            else {
                itemView.rl_dd.visibility = View.GONE
            }

            try {
                val orderList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingToShopId(list[adapterPosition].shop_id) as ArrayList<OrderDetailsListEntity>

                if (orderList != null && orderList.isNotEmpty()) {
                    itemView.order_RL.visibility = View.VISIBLE

                    var amount = 0.0
                    for (i in orderList.indices) {
                        if (!TextUtils.isEmpty(orderList[i].amount))
                            amount += orderList[i].amount?.toDouble()!!
                    }
                    val finalAmount = String.format("%.2f", amount.toFloat())
                    itemView.order_amt_p_TV.text = "\u20B9 $finalAmount"
                } else
                    itemView.order_RL.visibility = View.GONE
            } catch (e: Exception) {
                e.printStackTrace()
                itemView.order_RL.visibility = View.GONE
            }

            itemView.call_ll.setOnClickListener {
                listener.onCallClick(list[adapterPosition])
            }

            itemView.direction_ll.setOnClickListener {
                listener.onLocationClick(list[adapterPosition])
            }

            itemView.add_quot_ll.setOnClickListener {
                listener.onQuationClick(list[adapterPosition])
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
                listener.onDamageClick(list[adapterPosition].shop_id)
            }
            /*21-12-2021*/
            if(Pref.IsReturnEnableforParty) {
                if(Pref.IsReturnActivatedforPP){
                    if(list[adapterPosition].type!!.equals("2")){
                        itemView.lead_return_llll.visibility = View.VISIBLE
                        itemView.lead_return_vvview.visibility =  View.VISIBLE
                    }
                    else{
                        itemView.lead_return_llll.visibility = View.GONE
                        itemView.lead_return_vvview.visibility = View.GONE
                    }
                }
                else if(Pref.IsReturnActivatedforDD){
                    if(list[adapterPosition].type!!.equals("4")){
                        itemView.lead_return_llll.visibility = View.VISIBLE
                        itemView.lead_return_vvview.visibility =  View.VISIBLE
                    }
                    else{
                        itemView.lead_return_llll.visibility = View.GONE
                        itemView.lead_return_vvview.visibility = View.GONE
                    }
                }
                else if(Pref.IsReturnActivatedforSHOP){
                    if(list[adapterPosition].type!!.equals("1")){
                        itemView.lead_return_llll.visibility = View.VISIBLE
                        itemView.lead_return_vvview.visibility =  View.VISIBLE
                    }
                    else{
                        itemView.lead_return_llll.visibility = View.GONE
                        itemView.lead_return_vvview.visibility = View.GONE
                    }
                }
            }
            else{
                itemView.lead_return_llll.visibility = View.GONE
                itemView.lead_return_vvview.visibility = View.GONE
            }
            /*21-12-2021*/
            val OrderavalibleByShopId = AppDatabase.getDBInstance()?.orderDetailsListDao()?.getListAccordingToShopId(list[adapterPosition].shop_id!!) as java.util.ArrayList<OrderDetailsListEntity>
            if(OrderavalibleByShopId.size>0){
                //itemView.lead_return_ll.isEnabled=true
                itemView.lead_return_llll.setOnClickListener {
                    listener.onReturnClick(adapterPosition)
                }
            }
            else{
                itemView.lead_return_llll.setOnClickListener {
                    Toaster.msgShort(context,"No Minimum Order Avalible to return.")
                }
                //itemView.lead_return_ll.isEnabled=false
            }


            if (Pref.isEntityCodeVisible) {
                if (!TextUtils.isEmpty(list[adapterPosition].entity_code)) {
                    itemView.ll_shop_code.visibility = View.VISIBLE
                    itemView.tv_shop_code.text = list[adapterPosition].entity_code
                } else
                    itemView.ll_shop_code.visibility = View.GONE
            }
            else
                itemView.ll_shop_code.visibility = View.GONE


            if (Pref.isQuotationShow) {
                itemView.add_quot_ll.visibility = View.VISIBLE
                itemView.order_view.visibility = View.VISIBLE
            }
            else {
                itemView.add_quot_ll.visibility = View.GONE
                itemView.order_view.visibility = View.GONE
            }

            val distance = LocationWizard.getDistance(list[adapterPosition].shopLat, list[adapterPosition].shopLong,
                    Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble())
            itemView.approx_distance.text = (distance * 1000).toString() + " mtr"

            if (Pref.willShowPartyStatus)
                itemView.rl_party.visibility = View.VISIBLE
            else
                itemView.rl_party.visibility = View.GONE

            if (Pref.willShowEntityTypeforShop && list[adapterPosition].type == "1")
                itemView.rl_entity.visibility = View.VISIBLE
            else
                itemView.rl_entity.visibility = View.GONE

            if (!TextUtils.isEmpty(list[adapterPosition].entity_id)) {
                val entity = AppDatabase.getDBInstance()?.entityDao()?.getSingleItem(list[adapterPosition].entity_id)
                itemView.tv_entity_value.text = entity?.name
            }
            else
                itemView.tv_entity_value.text = "N.A."

            if (!TextUtils.isEmpty(list[adapterPosition].party_status_id)) {
                val partyStatus = AppDatabase.getDBInstance()?.partyStatusDao()?.getSingleItem(list[adapterPosition].party_status_id)
                itemView.tv_party_value.text = partyStatus?.name
            }
            else
                itemView.tv_party_value.text = "N.A."


            if(Pref.IsFeedbackHistoryActivated){
                itemView.history_llll.visibility=View.VISIBLE
                itemView.history_vvview.visibility=View.VISIBLE
            }else{
                itemView.history_llll.visibility=View.GONE
                itemView.history_vvview.visibility=View.GONE
            }
            itemView.history_llll.setOnClickListener {
                listener.onHistoryClick(list[adapterPosition])
            }

            if (Pref.isOrderShow) {
                itemView.add_order_ll.visibility = View.VISIBLE
                itemView.direction_view.visibility = View.VISIBLE
            } else {
                itemView.add_order_ll.visibility = View.GONE
                itemView.direction_view.visibility = View.GONE
            }

            /*if(Pref.IsSurveyRequiredforDealer && list[adapterPosition].type!!.equals("1")) {
                itemView.shop_rv_survey_ll.visibility = View.VISIBLE
                itemView.shop_rv_survey_view.visibility = View.VISIBLE
            }
            else if(Pref.IsSurveyRequiredforNewParty && list[adapterPosition].type!!.equals("3")){
                itemView.shop_rv_survey_ll.visibility = View.VISIBLE
                itemView.shop_rv_survey_view.visibility = View.VISIBLE
            }
            else{
                itemView.shop_rv_survey_ll.visibility = View.GONE
                itemView.shop_rv_survey_view.visibility = View.GONE
            }

            itemView.shop_rv_survey_ll.setOnClickListener{
                listener.onSurveyClick(list[adapterPosition].shop_id)
            }*/

            /*if(Pref.IsAllowNearbyshopWithBeat == false){
                if(Pref.IsBeatRouteAvailableinAttendance){
                    if(list[adapterPosition].beat_id.equals(Pref.SelectedBeatIDFromAttend)){
                        itemView.rl_revisit_nearby_shop.visibility=View.VISIBLE
                        itemView.visit_rl.isEnabled=true
                    }else{
                        itemView.rl_revisit_nearby_shop.visibility=View.GONE
                        itemView.visit_rl.isEnabled=false
                    }
                }
            }*/


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

            //var land=AppDatabase.getDBInstance()!!.addShopEntryDao().getLandNumber(p0?.toString())

            val results = FilterResults()
            filterList?.clear()
            tempList?.indices!!
                    .filter { tempList?.get(it)?.shopName?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!  ||
                            tempList?.get(it)?.pinCode?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!  ||
                            tempList?.get(it)?.ownerName?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!  ||
                            tempList?.get(it)?.ownerContactNumber?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!  ||
                            //AppDatabase.getDBInstance()!!.addShopEntryDao().getLandNumber(tempList?.get(it)?.landline_number!!).toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!  ||
                            //tempList?.get(it)?.landline_number?.toLowerCase()?.contains(land)!! ||
                            //AppDatabase.getDBInstance()!!.shopTypeDao().getShopNameById(tempList?.get(it)?.type!!).toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!  ||
                            tempList?.get(it)?.address?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!
                    }
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