package com.kcteam.features.micro_learning.presentation

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import kotlinx.android.synthetic.main.inflate_category_item.view.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.collections.indices

class CategoryAdapter(private val mContext: Context, list: ArrayList<String>?, private val onItemClick: (String) -> Unit?) :
        RecyclerView.Adapter<CategoryAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mList: ArrayList<String>? = null
    private var tempList: ArrayList<String>? = null
    private var filterList: ArrayList<String>? = null

    init {
        inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mList = ArrayList()
        mList?.addAll(list!!)

        tempList = ArrayList()
        filterList = ArrayList()

        tempList?.addAll(list!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.inflate_category_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun getItemCount(): Int {
        return mList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                if (adapterPosition % 2 == 0)
                    setBackgroundColor(ContextCompat.getColor(mContext, R.color.report_screen_bg))
                else
                    setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))

                tv_category.text = mList?.get(adapterPosition)

                setOnClickListener {
                    onItemClick(mList?.get(adapterPosition)!!)
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

            filterList?.clear()

            tempList?.indices!!
                    .filter { tempList?.get(it)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterList?.add(tempList?.get(it)!!) }

            results.values = filterList
            results.count = filterList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterList = results?.values as ArrayList<String>?
                mList?.clear()
                val hashSet = HashSet<String>()
                if (filterList != null) {

                    filterList?.indices!!
                            .filter { hashSet.add(filterList?.get(it)!!) }
                            .forEach { mList?.add(filterList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshList(list: ArrayList<String>) {
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