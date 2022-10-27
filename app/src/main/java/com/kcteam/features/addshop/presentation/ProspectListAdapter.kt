package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.ProspectEntity
import com.kcteam.app.domain.StageEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet


class ProspectListAdapter(private val context: Context, prosList: ArrayList<ProspectEntity>?,
                          private val onItemClick: (ProspectEntity) -> Unit?) : RecyclerView.Adapter<ProspectListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mProsList: ArrayList<ProspectEntity>? = null
    private var tempProsList: ArrayList<ProspectEntity>? = null
    private var filterProsList: ArrayList<ProspectEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mProsList = ArrayList()
        mProsList?.addAll(prosList!!)

        tempProsList = ArrayList()
        filterProsList = ArrayList()

        tempProsList?.addAll(prosList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mProsList?.get(position)?.pros_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mProsList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mProsList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterProsList?.clear()

            tempProsList?.indices!!
                    .filter { tempProsList?.get(it)?.pros_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterProsList?.add(tempProsList?.get(it)!!) }

            results.values = filterProsList
            results.count = filterProsList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterProsList = results?.values as ArrayList<ProspectEntity>?
                mProsList?.clear()
                val hashSet = HashSet<String>()
                if (filterProsList != null) {

                    filterProsList?.indices!!
                            .filter { hashSet.add(filterProsList?.get(it)?.pros_id!!) }
                            .forEach { mProsList?.add(filterProsList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}