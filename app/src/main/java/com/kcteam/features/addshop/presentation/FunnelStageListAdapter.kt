package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.FunnelStageEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

/**
 * Created by Saikat on 05-Jun-20.
 */
class FunnelStageListAdapter(private val context: Context, funnelStageList: ArrayList<FunnelStageEntity>?,
                             private val onItemClick: (FunnelStageEntity) -> Unit?) : RecyclerView.Adapter<FunnelStageListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mFunnelStageList: ArrayList<FunnelStageEntity>? = null
    private var tempFunnelStageList: ArrayList<FunnelStageEntity>? = null
    private var filterFunnelStageList: ArrayList<FunnelStageEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mFunnelStageList = ArrayList()
        mFunnelStageList?.addAll(funnelStageList!!)

        tempFunnelStageList = ArrayList()
        filterFunnelStageList = ArrayList()

        tempFunnelStageList?.addAll(funnelStageList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mFunnelStageList?.get(position)?.funnel_stage_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mFunnelStageList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mFunnelStageList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterFunnelStageList?.clear()

            tempFunnelStageList?.indices!!
                    .filter { tempFunnelStageList?.get(it)?.funnel_stage_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterFunnelStageList?.add(tempFunnelStageList?.get(it)!!) }

            results.values = filterFunnelStageList
            results.count = filterFunnelStageList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterFunnelStageList = results?.values as ArrayList<FunnelStageEntity>?
                mFunnelStageList?.clear()
                val hashSet = HashSet<String>()
                if (filterFunnelStageList != null) {

                    filterFunnelStageList?.indices!!
                            .filter { hashSet.add(filterFunnelStageList?.get(it)?.funnel_stage_id!!) }
                            .forEach { mFunnelStageList?.add(filterFunnelStageList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}