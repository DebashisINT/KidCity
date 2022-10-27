package com.kcteam.features.dailyPlan.prsentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dailyPlan.model.AllPlanListDataModel
import kotlinx.android.synthetic.main.inflate_all_plan_item.view.*
import java.util.HashSet
import kotlin.collections.ArrayList


/**
 * Created by Saikat on 03-01-2020.
 */
class AllPlanAdapter(private val context: Context, planDataDetails: ArrayList<AllPlanListDataModel>) : RecyclerView.Adapter<AllPlanAdapter.MyViewHolder>(),
        Filterable {

    private val layoutInflater: LayoutInflater

    private var updatePlanList: ArrayList<AllPlanListDataModel>? = null
    private var tempUpdatePlanList: ArrayList<AllPlanListDataModel>? = null
    private var filetrUpdatePlanList: ArrayList<AllPlanListDataModel>? = null

    init {
        layoutInflater = LayoutInflater.from(context)

        updatePlanList = ArrayList()
        updatePlanList?.addAll(planDataDetails)

        tempUpdatePlanList = ArrayList()
        tempUpdatePlanList?.addAll(planDataDetails)

        filetrUpdatePlanList = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_all_plan_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, updatePlanList!!)
    }

    override fun getItemCount(): Int {
        return updatePlanList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, planDataDetails: ArrayList<AllPlanListDataModel>) {

            itemView.tv_plan_date.text = AppUtils.convertToCommonFormat(planDataDetails[adapterPosition].plan_date)
            if (!TextUtils.isEmpty(planDataDetails[adapterPosition].plan_value))
                itemView.tv_plan_value.text = context.getString(R.string.rupee_symbol_with_space) + planDataDetails[adapterPosition].plan_value
            else
                itemView.tv_plan_value.text = context.getString(R.string.rupee_symbol_with_space) + "0.00"

            if (!TextUtils.isEmpty(planDataDetails[adapterPosition].achievement_value))
                itemView.tv_achv_value.text = context.getString(R.string.rupee_symbol_with_space) + planDataDetails[adapterPosition].achievement_value
            else
                itemView.tv_achv_value.text = context.getString(R.string.rupee_symbol_with_space) + "0.00"

            itemView.tv_percent.text = planDataDetails[adapterPosition].percnt

            if (!TextUtils.isEmpty(planDataDetails[adapterPosition].achievement_remarks))
                itemView.tv_achv_remark.text = planDataDetails[adapterPosition].achievement_remarks
            else
                itemView.tv_achv_remark.text = "N.A."

            if (!TextUtils.isEmpty(planDataDetails[adapterPosition].plan_remarks))
                itemView.tv_plan_remark.text = planDataDetails[adapterPosition].plan_remarks
            else
                itemView.tv_plan_remark.text = "N.A."

            itemView.tv_party.text = planDataDetails[adapterPosition].party_name
        }
    }

    public fun updateList(mUpdatePlanList: ArrayList<AllPlanListDataModel>?) {
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
                filetrUpdatePlanList = results?.values as ArrayList<AllPlanListDataModel>?
                updatePlanList?.clear()
                val hashSet = HashSet<String>()
                if (filetrUpdatePlanList != null) {

                    filetrUpdatePlanList?.indices!!
                            .filter { hashSet.add(filetrUpdatePlanList?.get(it)?.plan_data_id!!) }
                            .forEach { updatePlanList?.add(filetrUpdatePlanList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}