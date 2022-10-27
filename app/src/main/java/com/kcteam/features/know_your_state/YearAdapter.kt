package com.kcteam.features.know_your_state

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import kotlinx.android.synthetic.main.inflate_vehicle_log_type.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 27-11-2019.
 */
class YearAdapter(private val context: Context, private val itemList: ArrayList<String>?, private val listener: YearAdapter.OnItemClickListener) :
        RecyclerView.Adapter<YearAdapter.MyViewHolder>(), Filterable {

    private var layoutInflater: LayoutInflater? = null
    private var originalItemList: ArrayList<String>? = null
    private var tempItemList: ArrayList<String>? = null
    private var filterItemList: ArrayList<String>? = null

    init {
        originalItemList = ArrayList<String>()
        tempItemList = ArrayList()
        filterItemList = ArrayList()

        originalItemList?.addAll(itemList!!)
        filterItemList?.addAll(itemList!!)

        layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater?.inflate(R.layout.inflate_year_item, parent, false)
        return MyViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, originalItemList, listener)
    }

    override fun getItemCount(): Int {
        return originalItemList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, categoryList: ArrayList<String>?, listener: OnItemClickListener) {

            itemView.tv_log_type.text = categoryList?.get(adapterPosition)

            itemView.setOnClickListener {
                listener.onItemClick(categoryList?.get(adapterPosition), adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(category: String?, adapterPosition: Int)
    }

    override fun getFilter(): Filter {
       return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterItemList?.clear()

            /*for (i in tempCategoryList?.indices!!){
                if (tempCategoryList?.get(i)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!) {
                    filterCategoryList?.add(tempCategoryList?.get(i)!!)
                }
            }

            tempCategoryList?.indices!!
                    .asSequence()
                    .filter { tempCategoryList?.get(it)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterCategoryList?.add(tempCategoryList?.get(it)!!) }*/

            tempItemList?.indices!!
                    .filter { tempItemList?.get(it)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterItemList?.add(tempItemList?.get(it)!!) }

            results.values = filterItemList
            results.count = filterItemList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterItemList = results?.values as ArrayList<String>?
                originalItemList?.clear()
                val hashSet = HashSet<String>()
                if (filterItemList != null) {

                    /*for (i in filterCategoryList?.indices!!) {
                        if (hashSet.add(filterCategoryList?.get(i)!!))
                            categoryList?.add(filterCategoryList?.get(i)!!)
                    }

                    filterCategoryList?.indices!!
                            .asSequence()
                            .filter { hashSet.add(filterCategoryList?.get(it)!!) }
                            .forEach { categoryList?.add(filterCategoryList?.get(it)!!) }*/

                    filterItemList?.indices!!
                            .filter { hashSet.add(filterItemList?.get(it)!!) }
                            .forEach { originalItemList?.add(filterItemList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}