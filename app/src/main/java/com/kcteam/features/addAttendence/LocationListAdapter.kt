package com.kcteam.features.addAttendence

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.features.addAttendence.model.LocationDataModel
import com.kcteam.widgets.AppCustomTextView
import java.util.HashSet

/**
 * Created by Saikat on 14-May-20.
 */
class LocationListAdapter(private val context: Context, areaList: ArrayList<LocationDataModel>?, private val onItemClick: (LocationDataModel) -> Unit?,
                          private val onShowList: (Boolean) -> Unit) : RecyclerView.Adapter<LocationListAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater
    private var mAreaList: ArrayList<LocationDataModel>? = null
    private var tempAreaList: ArrayList<LocationDataModel>? = null
    private var filterAreaList: ArrayList<LocationDataModel>? = null

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        mAreaList = ArrayList()
        mAreaList?.addAll(areaList!!)

        tempAreaList = ArrayList()
        filterAreaList = ArrayList()

        tempAreaList?.addAll(areaList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.list_item_tv.text = mAreaList?.get(position)?.location
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return mAreaList?.size!!
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView

        init {
            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)

            itemView.setOnClickListener {
                onItemClick(mAreaList?.get(adapterPosition)!!)
            }
        }
    }


    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterAreaList?.clear()

            tempAreaList?.indices!!
                    .filter { tempAreaList?.get(it)?.location?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterAreaList?.add(tempAreaList?.get(it)!!) }

            results.values = filterAreaList
            results.count = filterAreaList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterAreaList = results?.values as ArrayList<LocationDataModel>?
                mAreaList?.clear()
                val hashSet = HashSet<String>()
                if (filterAreaList != null) {

                    filterAreaList?.indices!!
                            .filter { hashSet.add(filterAreaList?.get(it)?.id!!) }
                            .forEach { mAreaList?.add(filterAreaList?.get(it)!!) }

                    if (mAreaList?.size!! > 0)
                        onShowList(true)
                    else
                        onShowList(false)

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}