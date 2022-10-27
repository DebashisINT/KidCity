package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.LeadTypeEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

/**
 * Created by Saikat on 05-Jun-20.
 */
class LeadListAdapter(private val context: Context, leadList: ArrayList<LeadTypeEntity>?,
                      private val onItemClick: (LeadTypeEntity) -> Unit?) : RecyclerView.Adapter<LeadListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mLeadList: ArrayList<LeadTypeEntity>? = null
    private var tempLeadList: ArrayList<LeadTypeEntity>? = null
    private var filterLeadList: ArrayList<LeadTypeEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mLeadList = ArrayList()
        mLeadList?.addAll(leadList!!)

        tempLeadList = ArrayList()
        filterLeadList = ArrayList()

        tempLeadList?.addAll(leadList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mLeadList?.get(position)?.lead_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mLeadList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mLeadList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterLeadList?.clear()

            tempLeadList?.indices!!
                    .filter { tempLeadList?.get(it)?.lead_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterLeadList?.add(tempLeadList?.get(it)!!) }

            results.values = filterLeadList
            results.count = filterLeadList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterLeadList = results?.values as ArrayList<LeadTypeEntity>?
                mLeadList?.clear()
                val hashSet = HashSet<String>()
                if (filterLeadList != null) {

                    filterLeadList?.indices!!
                            .filter { hashSet.add(filterLeadList?.get(it)?.lead_id!!) }
                            .forEach { mLeadList?.add(filterLeadList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}