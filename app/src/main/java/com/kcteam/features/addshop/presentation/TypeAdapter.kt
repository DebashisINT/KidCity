package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.TypeListEntity
import com.kcteam.widgets.AppCustomTextView

import java.util.HashSet

/**
 * Created by Saikat on 05-Jun-20.
 */
class TypeAdapter(private val context: Context, typeList: ArrayList<TypeListEntity>?,
                  private val onItemClick: (TypeListEntity) -> Unit?) : RecyclerView.Adapter<TypeAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mTypeList: ArrayList<TypeListEntity>? = null
    private var tempTypeList: ArrayList<TypeListEntity>? = null
    private var filterTypeList: ArrayList<TypeListEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mTypeList = ArrayList()
        mTypeList?.addAll(typeList!!)

        tempTypeList = ArrayList()
        filterTypeList = ArrayList()

        tempTypeList?.addAll(typeList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mTypeList?.get(position)?.name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mTypeList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mTypeList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterTypeList?.clear()

            tempTypeList?.indices!!
                    .filter { tempTypeList?.get(it)?.name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterTypeList?.add(tempTypeList?.get(it)!!) }

            results.values = filterTypeList
            results.count = filterTypeList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterTypeList = results?.values as ArrayList<TypeListEntity>?
                mTypeList?.clear()
                val hashSet = HashSet<String>()
                if (filterTypeList != null) {

                    filterTypeList?.indices!!
                            .filter { hashSet.add(filterTypeList?.get(it)?.type_id!!) }
                            .forEach { mTypeList?.add(filterTypeList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}