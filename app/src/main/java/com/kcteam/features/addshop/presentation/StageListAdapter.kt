package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.StageEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

/**
 * Created by Saikat on 05-Jun-20.
 */
class StageListAdapter(private val context: Context, stageList: ArrayList<StageEntity>?,
                       private val onItemClick: (StageEntity) -> Unit?) : RecyclerView.Adapter<StageListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mStageList: ArrayList<StageEntity>? = null
    private var tempStageList: ArrayList<StageEntity>? = null
    private var filterStageList: ArrayList<StageEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mStageList = ArrayList()
        mStageList?.addAll(stageList!!)

        tempStageList = ArrayList()
        filterStageList = ArrayList()

        tempStageList?.addAll(stageList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mStageList?.get(position)?.stage_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mStageList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mStageList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterStageList?.clear()

            tempStageList?.indices!!
                    .filter { tempStageList?.get(it)?.stage_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterStageList?.add(tempStageList?.get(it)!!) }

            results.values = filterStageList
            results.count = filterStageList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterStageList = results?.values as ArrayList<StageEntity>?
                mStageList?.clear()
                val hashSet = HashSet<String>()
                if (filterStageList != null) {

                    filterStageList?.indices!!
                            .filter { hashSet.add(filterStageList?.get(it)?.stage_id!!) }
                            .forEach { mStageList?.add(filterStageList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}