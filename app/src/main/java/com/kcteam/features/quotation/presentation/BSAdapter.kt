package com.kcteam.features.quotation.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.BSListEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

/**
 * Created by Saikat on 12-Jun-20.
 */
class BSAdapter(private val context: Context, bsList: ArrayList<BSListEntity>?,
                private val onItemClick: (BSListEntity) -> Unit?) : RecyclerView.Adapter<BSAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mBSList: ArrayList<BSListEntity>? = null
    private var tempBSList: ArrayList<BSListEntity>? = null
    private var filterBSList: ArrayList<BSListEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mBSList = ArrayList()
        mBSList?.addAll(bsList!!)

        tempBSList = ArrayList()
        filterBSList = ArrayList()

        tempBSList?.addAll(bsList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mBSList?.get(position)?.bs_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mBSList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mBSList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterBSList?.clear()

            tempBSList?.indices!!
                    .filter { tempBSList?.get(it)?.bs_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterBSList?.add(tempBSList?.get(it)!!) }

            results.values = filterBSList
            results.count = filterBSList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterBSList = results?.values as ArrayList<BSListEntity>?
                mBSList?.clear()
                val hashSet = HashSet<String>()
                if (filterBSList != null) {

                    filterBSList?.indices!!
                            .filter { hashSet.add(filterBSList?.get(it)?.bs_id!!) }
                            .forEach { mBSList?.add(filterBSList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}