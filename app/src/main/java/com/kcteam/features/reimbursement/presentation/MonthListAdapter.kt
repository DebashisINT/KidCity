package com.kcteam.features.reimbursement.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.features.reimbursement.model.ListExpenseTypeModel
import com.kcteam.features.reimbursement.model.ReimbursementConfigExpenseTypeModel
import com.kcteam.features.reimbursement.model.ReimbursementConfigFuelTypeModel
import com.kcteam.features.reimbursement.model.ReimbursementConfigModeOfTravelDataModel
import com.kcteam.features.reimbursement.model.reimbursement_shop.ReimbursementShopDataModel
import com.kcteam.widgets.AppCustomTextView
import org.jetbrains.anko.find
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 06-03-2018.
 */

class MonthListAdapter(private val context: Context, private val memberNoList: ArrayList<*>, private val onItemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<MonthListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var tempList: ArrayList<String>? = null
    private var filteredList: ArrayList<String>? = null
    private var originalList: ArrayList<String>? = null

    init {
        if (memberNoList[0] is String) {

            tempList = ArrayList()
            filteredList = ArrayList()
            originalList = ArrayList()

            for (i in memberNoList.indices) {
                tempList?.add(memberNoList[i] as String)
                originalList?.add(memberNoList[i] as String)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = inflater.inflate(R.layout.inflate_month_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val genericObj = memberNoList[position]

        if (genericObj is String) {
            if (position == originalList?.size!! - 1)
                holder.view.visibility = View.GONE
            else
                holder.view.visibility = View.VISIBLE
        } else {
            if (position == memberNoList.size - 1)
                holder.view.visibility = View.GONE
            else
                holder.view.visibility = View.VISIBLE
        }

        when (genericObj) {
            is String -> {
                holder.tv_month.text = originalList?.get(position)
                holder.tv_month.visibility = View.VISIBLE
                holder.ll_shop_details.visibility = View.GONE
            }
            is ReimbursementConfigModeOfTravelDataModel -> {
                holder.tv_month.text = genericObj.travel_type
                holder.tv_month.visibility = View.VISIBLE
                holder.ll_shop_details.visibility = View.GONE
            }
            is ReimbursementConfigExpenseTypeModel -> {
                holder.tv_month.text = genericObj.expanse_type
                holder.tv_month.visibility = View.VISIBLE
                holder.ll_shop_details.visibility = View.GONE
            }
            is ReimbursementConfigFuelTypeModel -> {
                holder.tv_month.text = genericObj.fuel_type
                holder.tv_month.visibility = View.VISIBLE
                holder.ll_shop_details.visibility = View.GONE
            }
            is ListExpenseTypeModel -> {
                holder.tv_month.text = genericObj.expenseType
                holder.tv_month.visibility = View.VISIBLE
                holder.ll_shop_details.visibility = View.GONE
            }
            is ReimbursementShopDataModel -> {
                //holder.tv_month.text = genericObj.shop_name
                holder.tv_month.visibility = View.GONE
                holder.ll_shop_details.visibility = View.VISIBLE
                holder.tv_shop_name.text = genericObj.loc_name

                /*val shopDetail = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(genericObj.loc_id)

                if (shopDetail != null && !TextUtils.isEmpty(shopDetail.address)) {
                    holder.tv_shop_area.text = shopDetail.address
                    holder.tv_shop_area.visibility = View.VISIBLE
                } else
                    holder.tv_shop_area.visibility = View.GONE*/
            }
        }
    }

    override fun getItemCount(): Int {
        return if (memberNoList[0] is String)
            originalList?.size!!
        else
            memberNoList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var tv_month: AppCustomTextView
        internal var view: View
        internal var ll_shop_details: LinearLayout
        internal var tv_shop_name: AppCustomTextView
        internal var tv_shop_area: AppCustomTextView

        init {
            tv_month = itemView.findViewById<View>(R.id.tv_month) as AppCustomTextView
            view = itemView.findViewById(R.id.view) as View
            ll_shop_details = itemView.find(R.id.ll_shop_details)
            tv_shop_name = itemView.find(R.id.tv_shop_name)
            tv_shop_area = itemView.find(R.id.tv_shop_area)

            tv_month.setOnClickListener(this)
            ll_shop_details.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.tv_month -> onItemClickListener.onItemClick(adapterPosition)

                R.id.ll_shop_details -> onItemClickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filteredList?.clear()

            tempList?.indices!!
                    .filter { tempList?.get(it)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filteredList?.add(tempList?.get(it)!!) }

            results.values = filteredList
            results.count = filteredList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filteredList = results?.values as ArrayList<String>?
                originalList?.clear()
                val hashSet = HashSet<String>()
                if (filteredList != null) {


                    filteredList?.indices!!
                            .filter { hashSet.add(filteredList?.get(it)!!) }
                            .forEach { originalList?.add(filteredList?.get(it)!!) }



                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(adapterPosition: Int)
    }
}
