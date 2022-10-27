package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.PrimaryAppEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

/**
 * Created by Saikat on 05-Jun-20.
 */
class PrimaryAppListAdapter(private val context: Context, primaryAppList: ArrayList<PrimaryAppEntity>?,
                            private val onItemClick: (PrimaryAppEntity) -> Unit?) : RecyclerView.Adapter<PrimaryAppListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mPrimaryApplList: ArrayList<PrimaryAppEntity>? = null
    private var tempPrimaryAppList: ArrayList<PrimaryAppEntity>? = null
    private var filterPrimaryAppList: ArrayList<PrimaryAppEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mPrimaryApplList = ArrayList()
        mPrimaryApplList?.addAll(primaryAppList!!)

        tempPrimaryAppList = ArrayList()
        filterPrimaryAppList = ArrayList()

        tempPrimaryAppList?.addAll(primaryAppList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mPrimaryApplList?.get(position)?.primary_app_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mPrimaryApplList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mPrimaryApplList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterPrimaryAppList?.clear()

            tempPrimaryAppList?.indices!!
                    .filter { tempPrimaryAppList?.get(it)?.primary_app_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterPrimaryAppList?.add(tempPrimaryAppList?.get(it)!!) }

            results.values = filterPrimaryAppList
            results.count = filterPrimaryAppList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterPrimaryAppList = results?.values as ArrayList<PrimaryAppEntity>?
                mPrimaryApplList?.clear()
                val hashSet = HashSet<String>()
                if (filterPrimaryAppList != null) {

                    filterPrimaryAppList?.indices!!
                            .filter { hashSet.add(filterPrimaryAppList?.get(it)?.primary_app_id!!) }
                            .forEach { mPrimaryApplList?.add(filterPrimaryAppList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}