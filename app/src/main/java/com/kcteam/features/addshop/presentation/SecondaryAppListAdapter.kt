package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.SecondaryAppEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

/**
 * Created by Saikat on 05-Jun-20.
 */
class SecondaryAppListAdapter(private val context: Context, secondaryAppList: ArrayList<SecondaryAppEntity>?,
                              private val onItemClick: (SecondaryAppEntity) -> Unit?) : RecyclerView.Adapter<SecondaryAppListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mSecondaryApplList: ArrayList<SecondaryAppEntity>? = null
    private var tempSecondaryAppList: ArrayList<SecondaryAppEntity>? = null
    private var filterSecondaryyAppList: ArrayList<SecondaryAppEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mSecondaryApplList = ArrayList()
        mSecondaryApplList?.addAll(secondaryAppList!!)

        tempSecondaryAppList = ArrayList()
        filterSecondaryyAppList = ArrayList()

        tempSecondaryAppList?.addAll(secondaryAppList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mSecondaryApplList?.get(position)?.secondary_app_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mSecondaryApplList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mSecondaryApplList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterSecondaryyAppList?.clear()

            tempSecondaryAppList?.indices!!
                    .filter { tempSecondaryAppList?.get(it)?.secondary_app_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterSecondaryyAppList?.add(tempSecondaryAppList?.get(it)!!) }

            results.values = filterSecondaryyAppList
            results.count = filterSecondaryyAppList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterSecondaryyAppList = results?.values as ArrayList<SecondaryAppEntity>?
                mSecondaryApplList?.clear()
                val hashSet = HashSet<String>()
                if (filterSecondaryyAppList != null) {

                    filterSecondaryyAppList?.indices!!
                            .filter { hashSet.add(filterSecondaryyAppList?.get(it)?.secondary_app_id!!) }
                            .forEach { mSecondaryApplList?.add(filterSecondaryyAppList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}