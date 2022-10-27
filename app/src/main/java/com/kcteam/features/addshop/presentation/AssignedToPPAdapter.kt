package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.AssignToPPEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 18-Sep-18.
 */

class AssignedToPPAdapter(private val context: Context, private val mAssignedList: ArrayList<AssignToPPEntity>, private val onItemClickListener: OnItemClickListener) 
    : RecyclerView.Adapter<AssignedToPPAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var ppList: ArrayList<AssignToPPEntity>? = null
    private var tempPPList: ArrayList<AssignToPPEntity>? = null
    private var filterPPList: ArrayList<AssignToPPEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        ppList = ArrayList()
        ppList?.addAll(/*AppDatabase.getDBInstance()?.ppListDao()?.getAll() as ArrayList<AssignToPPEntity>*/mAssignedList)

        tempPPList = ArrayList()
        filterPPList = ArrayList()

        tempPPList?.addAll(ppList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = ppList?.get(position)?.pp_name
        holder.tv_phn_no.text = "(" + ppList?.get(position)?.pp_phn_no + ")"
        holder.tv_phn_no.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return ppList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClickListener.onItemClick(ppList?.get(adapterPosition))
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(pp: AssignToPPEntity?)
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterPPList?.clear()

            /*for (i in tempPPList?.indices!!){
                if (tempPPList?.get(i)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!) {
                    filterPPList?.add(tempPPList?.get(i)!!)
                }
            }

            tempPPList?.indices!!
                    .asSequence()
                    .filter { tempPPList?.get(it)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterPPList?.add(tempPPList?.get(it)!!) }*/

            tempPPList?.indices!!
                    .filter { tempPPList?.get(it)?.pp_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterPPList?.add(tempPPList?.get(it)!!) }

            results.values = filterPPList
            results.count = filterPPList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterPPList = results?.values as ArrayList<AssignToPPEntity>?
                ppList?.clear()
                val hashSet = HashSet<String>()
                if (filterPPList != null) {

                    /*for (i in filterPPList?.indices!!) {
                        if (hashSet.add(filterPPList?.get(i)!!))
                            brandList?.add(filterPPList?.get(i)!!)
                    }

                    filterPPList?.indices!!
                            .asSequence()
                            .filter { hashSet.add(filterPPList?.get(it)!!) }
                            .forEach { brandList?.add(filterPPList?.get(it)!!) }*/

                    filterPPList?.indices!!
                            .filter { hashSet.add(filterPPList?.get(it)?.pp_phn_no!!) }
                            .forEach { ppList?.add(filterPPList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
