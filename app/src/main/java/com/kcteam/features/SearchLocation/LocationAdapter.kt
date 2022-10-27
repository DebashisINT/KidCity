package com.kcteam.features.SearchLocation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 06-08-2018.
 */
class LocationAdapter(val mContext: Context, private var locationList: ArrayList<EditTextAddressModel>, private val onLocationClickListener: OnLocationItemClickListener) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    private lateinit var inflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        inflater = LayoutInflater.from(mContext)
        return ViewHolder(inflater.inflate(R.layout.inflate_location_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_address.text = locationList[position].description

        if (position == locationList.size - 1)
            holder.address_view.visibility = View.GONE
        else
            holder.address_view.visibility = View.VISIBLE

        holder.tv_address.setOnClickListener {
            onLocationClickListener.onLocationItemClick(locationList[position].description, locationList[position].place_id)
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }

    fun refreshList(mListPlace: ArrayList<EditTextAddressModel>?) {
        locationList = mListPlace!!
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_address: AppCustomTextView
        var address_view: View

        init {
            tv_address = itemView.findViewById(R.id.tv_address)
            address_view = itemView.findViewById(R.id.address_view)
        }
    }

    interface OnLocationItemClickListener {
        fun onLocationItemClick(description: String, place_id: String)
    }
}