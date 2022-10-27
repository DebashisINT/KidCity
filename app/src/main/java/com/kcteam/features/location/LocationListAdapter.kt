/*
package com.fieldtrackingsystem.features.location

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.fieldtrackingsystem.R
import com.fieldtrackingsystem.app.domain.LocationEntity
import kotlinx.android.synthetic.main.inflate_location_list_item.view.*

*/
/**
 * Created by Saikat on 07-01-2019.
 *//*

class LocationListAdapter(private val context: Context, private val locationList: List<LocationEntity>) : RecyclerView.Adapter<LocationListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, locationList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_location_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  */
/*10*//*
 locationList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, locationList: List<LocationEntity>) {

            try {
                if (adapterPosition % 2 == 0)
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
                else
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

                itemView.tv_date.text = locationList[adapterPosition].date_time
                itemView.tv_lat.text = locationList[adapterPosition].latitude
                itemView.tv_long.text = locationList[adapterPosition].longitude
                itemView.tv_accuracy.text = locationList[adapterPosition].accuracy
                itemView.tv_loc.text = locationList[adapterPosition].locationName
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }
}*/
