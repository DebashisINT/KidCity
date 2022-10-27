package com.kcteam.features.addshop.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.AssignToDDEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet
import kotlin.collections.ArrayList

/**
 * Created by Saikat on 18-Sep-18.
 */
class AssignedToDDAdapter(private val context: Context, private val mAssignedList: ArrayList<AssignToDDEntity>?, private val onItemClickListener: OnItemClickListener) 
    : RecyclerView.Adapter<AssignedToDDAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var ddList: ArrayList<AssignToDDEntity>? = null
    private var tempDDList: ArrayList<AssignToDDEntity>? = null
    private var filterDDList: ArrayList<AssignToDDEntity>? = null
    
    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //ddList = mAssignedList //AppDatabase.getDBInstance()?.ddListDao()?.getAll() as ArrayList<AssignToDDEntity>?
        ddList= ArrayList()
        ddList?.addAll(mAssignedList!!)
        
        tempDDList = ArrayList()
        filterDDList = ArrayList()

        tempDDList?.addAll(ddList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = ddList?.get(position)?.dd_name
        holder.tv_phn_no.text = "(" + ddList?.get(position)?.dd_phn_no + ")"
        holder.tv_phn_no.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return ddList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClickListener.onItemClick(ddList?.get(adapterPosition))
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(dd: AssignToDDEntity?)
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterDDList?.clear()

            /*for (i in tempDDList?.indices!!){
                if (tempDDList?.get(i)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!!) {
                    filterDDList?.add(tempDDList?.get(i)!!)
                }
            }

            tempDDList?.indices!!
                    .asSequence()
                    .filter { tempDDList?.get(it)?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterDDList?.add(tempDDList?.get(it)!!) }*/

            tempDDList?.indices!!
                    .filter { tempDDList?.get(it)?.dd_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterDDList?.add(tempDDList?.get(it)!!) }

            results.values = filterDDList
            results.count = filterDDList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterDDList = results?.values as ArrayList<AssignToDDEntity>?
                ddList?.clear()
                val hashSet = HashSet<String>()
                if (filterDDList != null) {

                    /*for (i in filterDDList?.indices!!) {
                        if (hashSet.add(filterDDList?.get(i)!!))
                            brandList?.add(filterDDList?.get(i)!!)
                    }

                    filterDDList?.indices!!
                            .asSequence()
                            .filter { hashSet.add(filterDDList?.get(it)!!) }
                            .forEach { brandList?.add(filterDDList?.get(it)!!) }*/

                    filterDDList?.indices!!
                            .filter { hashSet.add(filterDDList?.get(it)?.dd_phn_no!!) }
                            .forEach { ddList?.add(filterDDList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}