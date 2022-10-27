package com.kcteam.features.member.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.MemberShopEntity
import com.kcteam.app.utils.AppUtils
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.*
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.history_llll
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.history_vvview
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.last_visited_date_TV
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.ll_shop_code
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.myshop_address_TV
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.myshop_name_TV
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.shop_IV
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.total_visited_value_TV
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.tv_shop_code
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.tv_shop_contact_no
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.visit_rl
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.iconWrapper_rl
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.add_quot_ll
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.add_order_ll
import kotlinx.android.synthetic.main.inflate_member_shop_list.view.order_view
import kotlinx.android.synthetic.main.inflate_nearby_shops.view.*

/**
 * Created by Saikat on 03-Jul-20.
 */
class OfflineAllShopAdapter(private val context: Context, private val teamShopList: ArrayList<MemberShopEntity>,
                            private val listener: (MemberShopEntity) -> Unit, private val onUpdateLocClick: (MemberShopEntity) -> Unit,
                            private val getListSize: (Int) -> Unit, private val onSyncClick: (MemberShopEntity) -> Unit) : RecyclerView.Adapter<OfflineAllShopAdapter.MyViewHolder>(),
        Filterable {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private var shopList: ArrayList<MemberShopEntity>? = null
    private var tempshopList: ArrayList<MemberShopEntity>? = null
    private var filtershopList: ArrayList<MemberShopEntity>? = null

    init {
        shopList = ArrayList()
        tempshopList = ArrayList()
        filtershopList = ArrayList()

        shopList?.addAll(teamShopList)
        tempshopList?.addAll(teamShopList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_member_shop_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, shopList!!, listener, onUpdateLocClick, onSyncClick)
    }

    override fun getItemCount(): Int {
        return shopList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, teamShopList: ArrayList<MemberShopEntity>, listener: (MemberShopEntity) -> Unit,
                      onUpdateLocClick: (MemberShopEntity) -> Unit, onSyncClick: (MemberShopEntity) -> Unit) {
            itemView.apply {
                myshop_name_TV.text = teamShopList[adapterPosition].shop_name
                myshop_address_TV.text = teamShopList[adapterPosition].shop_address
                tv_shop_contact_no.text = teamShopList[adapterPosition].shop_contact
                total_visited_value_TV.text = teamShopList[adapterPosition].total_visited
                visit_rl.visibility = View.GONE
                tv_update_address.visibility = View.VISIBLE
                sync_icon.visibility = View.VISIBLE

                val shopType = AppDatabase.getDBInstance()?.shopTypeDao()?.getSingleType(teamShopList[adapterPosition].shop_type!!)

                if (shopType != null && !TextUtils.isEmpty(shopType.shoptype_name)) {
                    tv_shop_type.text = shopType.shoptype_name
                    ll_shop_type.visibility = View.VISIBLE
                } else
                    ll_shop_type.visibility = View.GONE

                if (!TextUtils.isEmpty(teamShopList[adapterPosition].last_visit_date))
                    last_visited_date_TV.text = AppUtils.changeAttendanceDateFormat(teamShopList[adapterPosition].last_visit_date!!)
                else
                    last_visited_date_TV.text = "N.A."

                val drawable = TextDrawable.builder()
                        .buildRoundRect(teamShopList[adapterPosition].shop_name?.trim()?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

                shop_IV.setImageDrawable(drawable)

                setOnClickListener {
                    listener(teamShopList[adapterPosition])
                }

                if (Pref.isEntityCodeVisible) {
                    if (!TextUtils.isEmpty(teamShopList[adapterPosition].entity_code)) {
                        ll_shop_code.visibility = View.VISIBLE
                        tv_shop_code.text = teamShopList[adapterPosition].entity_code
                    } else
                        ll_shop_code.visibility = View.GONE
                } else
                    ll_shop_code.visibility = View.GONE


                tv_update_address.setOnClickListener {
                    onUpdateLocClick(teamShopList[adapterPosition])
                }

                if (teamShopList[adapterPosition].isUploaded)
                    sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                else {
                    sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    sync_icon.setOnClickListener {
                        onSyncClick(teamShopList[adapterPosition])
                    }
                }

                if (Pref.isCustomerFeatureEnable) {
                    ll_dd_name.visibility = View.GONE

                    if (!TextUtils.isEmpty(teamShopList[adapterPosition].stage_id)) {
                        val stage = AppDatabase.getDBInstance()?.stageDao()?.getSingleType(teamShopList[adapterPosition].stage_id!!)

                        if (stage == null) {
                            tv_stage_header.visibility = View.GONE
                            tv_stage.visibility = View.GONE
                        } else {
                            tv_stage_header.visibility = View.VISIBLE
                            tv_stage.visibility = View.VISIBLE

                            tv_stage.text = stage.stage_name
                        }
                    } else {
                        tv_stage_header.visibility = View.GONE
                        tv_stage.visibility = View.GONE
                    }

                    if (!TextUtils.isEmpty(teamShopList[adapterPosition].funnel_stage_id)) {
                        val funnelStage = AppDatabase.getDBInstance()?.funnelStageDao()?.getSingleType(teamShopList[adapterPosition].funnel_stage_id!!)

                        if (funnelStage == null) {
                            tv_funnel_stage_header.visibility = View.GONE
                            itemView.tv_funnel_stage.visibility = View.GONE
                        } else {
                            tv_funnel_stage_header.visibility = View.VISIBLE
                            tv_funnel_stage.visibility = View.VISIBLE

                            tv_funnel_stage.text = funnelStage.funnel_stage_name
                        }
                    } else {
                        tv_funnel_stage_header.visibility = View.GONE
                        tv_funnel_stage.visibility = View.GONE
                    }
                }
                else {
                    tv_funnel_stage_header.visibility = View.GONE
                    tv_funnel_stage.visibility = View.GONE
                    tv_stage_header.visibility = View.GONE
                    tv_stage.visibility = View.GONE

                    when {
                        teamShopList[adapterPosition].shop_type == "1" -> {
                            //tv_shop_type.text = context.getString(R.string.shop_type)
                            ll_dd_name.visibility = View.VISIBLE

                            if (!TextUtils.isEmpty(teamShopList[adapterPosition].dd_name))
                                tv_dd_name.text = teamShopList[adapterPosition].dd_name
                            else
                                tv_dd_name.text = "N.A."

                        }
                        teamShopList[adapterPosition].shop_type == "2" -> {
                            //tv_shop_type.text = context.getString(R.string.pp_type)
                            ll_dd_name.visibility = View.GONE
                        }
                        teamShopList[adapterPosition].shop_type == "3" -> {
                            //tv_shop_type.text = context.getString(R.string.new_party_type)
                            ll_dd_name.visibility = View.GONE
                        }
                        teamShopList[adapterPosition].shop_type == "4" -> {
                            //tv_shop_type.text = context.getString(R.string.distributor_type)
                            ll_dd_name.visibility = View.GONE
                        }
                        teamShopList[adapterPosition].shop_type == "5" -> {
                            //tv_shop_type.text = context.getString(R.string.diamond_type)
                            ll_dd_name.visibility = View.VISIBLE

                            if (!TextUtils.isEmpty(teamShopList[adapterPosition].dd_name))
                                tv_dd_name.text = teamShopList[adapterPosition].dd_name
                            else
                                tv_dd_name.text = "N.A."
                        }
                        else -> {
                            ll_dd_name.visibility = View.GONE
                        }
                    }
                }




            }
        }
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filtershopList?.clear()

            tempshopList?.indices!!
                    .filter { tempshopList?.get(it)?.shop_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filtershopList?.add(tempshopList?.get(it)!!) }

            results.values = filtershopList
            results.count = filtershopList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filtershopList = results?.values as ArrayList<MemberShopEntity>?
                shopList?.clear()
                val hashSet = HashSet<String>()
                if (filtershopList != null) {

                    filtershopList?.indices!!
                            .filter { hashSet.add(filtershopList?.get(it)?.shop_id!!) }
                            .forEach { shopList?.add(filtershopList?.get(it)!!) }

                    getListSize(shopList?.size!!)

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshList(teamShopList: ArrayList<MemberShopEntity>) {
        shopList?.clear()
        shopList?.addAll(teamShopList)

        tempshopList?.clear()
        tempshopList?.addAll(teamShopList)

        if (filtershopList == null)
            filtershopList = ArrayList()
        filtershopList?.clear()

        notifyDataSetChanged()
    }
}