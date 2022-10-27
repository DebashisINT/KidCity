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
import com.kcteam.features.reimbursement.model.reimbursement_shop.ReimbursementShopDataModel
import com.kcteam.widgets.AppCustomTextView
import org.jetbrains.anko.find
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 24-10-2019.
 */
class LocationAdapter(private val context: Context, private var locList: ArrayList<ReimbursementShopDataModel>?, private val memberNoList: ArrayList<String>, private val onItemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<LocationAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var tempList: ArrayList<ReimbursementShopDataModel>? = null
    private var filteredList: ArrayList<ReimbursementShopDataModel>? = null
    private var originalList: ArrayList<ReimbursementShopDataModel>? = null

    init {
        tempList = ArrayList()
        filteredList = ArrayList()
        originalList = ArrayList()

        tempList!!.addAll(locList!!)
        originalList!!.addAll(locList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = inflater.inflate(R.layout.inflate_month_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.tv_month.text = originalList?.get(position)?.loc_name
        holder.tv_month.visibility = View.VISIBLE
        holder.ll_shop_details.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return originalList?.size!!
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
                R.id.tv_month -> {

                    for (i in locList?.indices!!) {
                        if (locList?.get(i)?.loc_id == originalList?.get(adapterPosition)?.loc_id)
                            onItemClickListener.onItemClick(i)
                    }
                }

                R.id.ll_shop_details -> {
                    for (i in locList?.indices!!) {
                        if (locList?.get(i)?.loc_id == originalList?.get(adapterPosition)?.loc_id)
                            onItemClickListener.onItemClick(i)
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

            filteredList?.clear()

            tempList?.indices!!
                    .filter { tempList?.get(it)?.loc_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filteredList?.add(tempList?.get(it)!!) }

            results.values = filteredList
            results.count = filteredList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filteredList = results?.values as ArrayList<ReimbursementShopDataModel>?
                originalList?.clear()
                val hashSet = HashSet<String>()
                if (filteredList != null) {


                    filteredList?.indices!!
                            .filter { hashSet.add(filteredList?.get(it)!!.loc_id!!) }
                            .forEach { originalList?.add(filteredList?.get(it)!!) }

                    if (originalList!!.size > 0)
                        onItemClickListener.showList(true)
                    else
                        onItemClickListener.showList(false)

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    interface OnItemClickListener {
        fun onItemClick(adapterPosition: Int)

        fun showList(isShowList: Boolean)
    }
}