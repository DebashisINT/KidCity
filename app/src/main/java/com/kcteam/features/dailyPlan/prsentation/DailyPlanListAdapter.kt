package com.kcteam.features.dailyPlan.prsentation

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
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dailyPlan.model.GetPlanListDataModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_daily_plan_list.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 20-12-2019.
 */
class DailyPlanListAdapter(private val context: Context, private val mUpdatePlanList: ArrayList<GetPlanListDataModel>?, private val listener: OnButtonClickListener)
    : RecyclerView.Adapter<DailyPlanListAdapter.MyViewHolder>(), Filterable {

    private val layoutInflater: LayoutInflater
    private var updatePlanList: ArrayList<GetPlanListDataModel>? = null
    private var tempUpdatePlanList: ArrayList<GetPlanListDataModel>? = null
    private var filetrUpdatePlanList: ArrayList<GetPlanListDataModel>? = null

    init {
        layoutInflater = LayoutInflater.from(context)
        //updatePlanList = mUpdatePlanList

        updatePlanList = ArrayList()
        updatePlanList?.addAll(mUpdatePlanList!!)

        tempUpdatePlanList = ArrayList()
        tempUpdatePlanList?.addAll(mUpdatePlanList!!)

        filetrUpdatePlanList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_daily_plan_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, listener, updatePlanList)
    }

    override fun getItemCount(): Int {
        return updatePlanList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, listener: OnButtonClickListener, updatePlanList: ArrayList<GetPlanListDataModel>?) {

            itemView.tv_shop_name.text = updatePlanList?.get(adapterPosition)?.party_name
            itemView.tv_shop_address.text = updatePlanList?.get(adapterPosition)?.location

            if (!TextUtils.isEmpty(updatePlanList?.get(adapterPosition)?.last_plan_date)) {
                itemView.rl_plan_date.visibility = View.VISIBLE
                itemView.tv_plan_date_value.text = AppUtils.convertToBillingFormat(updatePlanList?.get(adapterPosition)?.last_plan_date!!)
            } else
                itemView.rl_plan_date.visibility = View.GONE

            if (!TextUtils.isEmpty(updatePlanList?.get(adapterPosition)?.last_plan_value)) {
                itemView.ll_plan_value.visibility = View.VISIBLE
                itemView.tv_plan_amt.text = context.getString(R.string.rupee_symbol_with_space) + updatePlanList?.get(adapterPosition)?.last_plan_value
            } else
                itemView.ll_plan_value.visibility = View.GONE

            if (!TextUtils.isEmpty(updatePlanList?.get(adapterPosition)?.last_achv_amount)) {
                itemView.tv_achv_value_header.visibility = View.VISIBLE
                itemView.tv_achv_amount.visibility = View.VISIBLE
                itemView.tv_achv_amount.text = context.getString(R.string.rupee_symbol_with_space) + updatePlanList?.get(adapterPosition)?.last_achv_amount
            } else {
                itemView.tv_achv_value_header.visibility = View.GONE
                itemView.tv_achv_amount.visibility = View.GONE
            }

            if ((context as DashboardActivity).isDailyPlanFromAlarm) {
                itemView.ll_update_plan.visibility = View.GONE
                itemView.view_update_plan.visibility = View.GONE
            }
            else {
                itemView.ll_update_plan.visibility = View.VISIBLE
                itemView.view_update_plan.visibility = View.VISIBLE
            }


            val drawable = TextDrawable.builder()
                    .buildRoundRect(updatePlanList?.get(adapterPosition)?.party_name?.trim()?.toUpperCase()?.take(1), ColorGenerator.MATERIAL.randomColor, 120)

            itemView.iv_shop_initial.setImageDrawable(drawable)

            itemView.ll_update_plan.setOnClickListener {
                listener.onUpdatePlanClick(adapterPosition, updatePlanList?.get(adapterPosition))
            }

            itemView.ll_update_achv.setOnClickListener {
                listener.onUpadteAchvClick(adapterPosition, updatePlanList?.get(adapterPosition))
            }

            itemView.ll_view_all.setOnClickListener {
                listener.onViewAllClick(adapterPosition, updatePlanList?.get(adapterPosition))
            }
        }
    }

    public interface OnButtonClickListener {
        fun onUpdatePlanClick(adapterPosition: Int, updatePlan: GetPlanListDataModel?)
        fun onUpadteAchvClick(adapterPosition: Int, updatePlan: GetPlanListDataModel?)
        fun onViewAllClick(adapterPosition: Int, updatePlan: GetPlanListDataModel?)
    }

    public fun updateList(mUpdatePlanList: ArrayList<GetPlanListDataModel>?) {
        updatePlanList?.clear()
        updatePlanList?.addAll(mUpdatePlanList!!)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filetrUpdatePlanList?.clear()

            tempUpdatePlanList?.indices!!
                    .filter { tempUpdatePlanList?.get(it)?.party_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filetrUpdatePlanList?.add(tempUpdatePlanList?.get(it)!!) }

            results.values = filetrUpdatePlanList
            results.count = filetrUpdatePlanList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filetrUpdatePlanList = results?.values as ArrayList<GetPlanListDataModel>?
                updatePlanList?.clear()
                val hashSet = HashSet<String>()
                if (filetrUpdatePlanList != null) {

                    filetrUpdatePlanList?.indices!!
                            .filter { hashSet.add(filetrUpdatePlanList?.get(it)?.plan_id!!) }
                            .forEach { updatePlanList?.add(filetrUpdatePlanList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}