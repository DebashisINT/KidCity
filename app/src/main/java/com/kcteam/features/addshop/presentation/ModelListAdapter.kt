package com.kcteam.features.addshop.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.ModelEntity
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

/**
 * Created by Saikat on 05-Jun-20.
 */
class ModelListAdapter(private val context: Context, modelList: ArrayList<ModelEntity>?, private val onItemClick: (ModelEntity) -> Unit?) : RecyclerView.Adapter<ModelListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mModelList: ArrayList<ModelEntity>? = null
    private var tempModelList: ArrayList<ModelEntity>? = null
    private var filterModelList: ArrayList<ModelEntity>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mModelList = ArrayList()
        mModelList?.addAll(modelList!!)

        tempModelList = ArrayList()
        filterModelList = ArrayList()

        tempModelList?.addAll(modelList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mModelList?.get(position)?.model_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mModelList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mModelList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterModelList?.clear()

            tempModelList?.indices!!
                    .filter { tempModelList?.get(it)?.model_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterModelList?.add(tempModelList?.get(it)!!) }

            results.values = filterModelList
            results.count = filterModelList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterModelList = results?.values as ArrayList<ModelEntity>?
                mModelList?.clear()
                val hashSet = HashSet<String>()
                if (filterModelList != null) {

                    filterModelList?.indices!!
                            .filter { hashSet.add(filterModelList?.get(it)?.model_id!!) }
                            .forEach { mModelList?.add(filterModelList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}