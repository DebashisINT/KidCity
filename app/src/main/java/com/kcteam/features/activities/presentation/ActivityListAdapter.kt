package com.kcteam.features.activities.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.ActivityDropDownEntity
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.widgets.AppCustomTextView
import kotlinx.android.synthetic.main.inflate_activity_item.view.*
import java.util.HashSet

class ActivityListAdapter(private val context: Context, list: ArrayList<AddShopDBModelEntity>?,
                          private val onCallClick: (AddShopDBModelEntity) -> Unit?, private val onLocClick: (AddShopDBModelEntity) -> Unit?,
                          private val onViewClick: (AddShopDBModelEntity) -> Unit?, private val onListSize: (Int) -> Unit) :
        RecyclerView.Adapter<ActivityListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mList: ArrayList<AddShopDBModelEntity>? = null
    private var tempList: ArrayList<AddShopDBModelEntity>? = null
    private var filterList: ArrayList<AddShopDBModelEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mList = ArrayList()
        mList?.addAll(list!!)

        tempList = ArrayList()
        filterList = ArrayList()

        tempList?.addAll(list!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.inflate_activity_item, parent, false)
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
                tv_name.text = mList?.get(adapterPosition)?.shopName

                tv_team_details.setOnClickListener {
                    onViewClick(mList?.get(adapterPosition)!!)
                }

                iv_map_icon.setOnClickListener {
                    onLocClick(mList?.get(adapterPosition)!!)
                }

                iv_call_icon.setOnClickListener {
                    onCallClick(mList?.get(adapterPosition)!!)
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
                    .filter { tempList?.get(it)?.shopName?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
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
                            .filter { hashSet.add(filterList?.get(it)?.shop_id!!) }
                            .forEach { mList?.add(filterList?.get(it)!!) }

                    onListSize(mList?.size!!)

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