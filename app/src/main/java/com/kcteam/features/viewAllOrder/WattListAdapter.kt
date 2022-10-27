package com.kcteam.features.viewAllOrder

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.ProductListEntity
import kotlinx.android.synthetic.main.inflate_vehicle_log_type.view.*
import java.util.HashSet
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 26-11-2018.
 */
class WattListAdapter(private val context: Context, private val workTypeList: ArrayList<ProductListEntity>?, private val listener: OnCategoryClickListener) :
        RecyclerView.Adapter<WattListAdapter.MyViewHolder>(), Filterable {

    private var layoutInflater: LayoutInflater? = null
    private var wattList: ArrayList<ProductListEntity>? = null
    private var tempWattList: ArrayList<ProductListEntity>? = null
    private var filterWattList: ArrayList<ProductListEntity>? = null

    init {
        wattList = ArrayList<ProductListEntity>()
        tempWattList = ArrayList()
        filterWattList = ArrayList()

        wattList?.addAll(workTypeList!!)
        tempWattList?.addAll(workTypeList!!)

        layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater?.inflate(R.layout.inflate_vehicle_log_type, parent, false)
        return MyViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, wattList, listener)
    }

    override fun getItemCount(): Int {
        return wattList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, wattList: ArrayList<ProductListEntity>?, listener: OnCategoryClickListener) {

            itemView.tv_log_type.text = wattList?.get(adapterPosition)?.watt
            //itemView.iv_check.isSelected = workTypeList?.get(adapterPosition)?.isSelected!!
            itemView.iv_check.visibility = View.GONE
            itemView.setOnClickListener {
                listener.onCategoryClick(wattList?.get(adapterPosition), adapterPosition)
            }
        }
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(category: ProductListEntity?, adapterPosition: Int)
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }
    
    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterWattList?.clear()

            /*for (i in tempWattList?.indices!!){
                if (tempWattList?.get(i)?.contains(p0?.toString()!!)!!) {
                    filterWattList?.add(tempWattList?.get(i)!!)
                }
            }*/

            /*tempWattList?.indices!!
                    .asSequence()
                    .filter { tempWattList?.get(it)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterWattList?.add(tempWattList?.get(it)!!) }*/

            tempWattList?.indices!!
                    .filter { tempWattList?.get(it)?.watt?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterWattList?.add(tempWattList?.get(it)!!) }

            results.values = filterWattList
            results.count = filterWattList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterWattList = results?.values as ArrayList<ProductListEntity>?
                wattList?.clear()
                val hashSet = HashSet<String>()
                if (filterWattList != null) {

                    /*for (i in filterWattList?.indices!!) {
                        if (hashSet.add(filterWattList?.get(i)!!))
                            wattList?.add(filterWattList?.get(i)!!)
                    }

                    filterWattList?.indices!!
                            .asSequence()
                            .filter { hashSet.add(filterWattList?.get(it)!!) }
                            .forEach { wattList?.add(filterWattList?.get(it)!!) }*/

                    filterWattList?.indices!!
                            .filter { hashSet.add(filterWattList?.get(it)?.watt!!) }
                            .forEach { wattList?.add(filterWattList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}