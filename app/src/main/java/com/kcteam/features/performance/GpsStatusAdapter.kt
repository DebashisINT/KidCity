package com.kcteam.features.performance

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.GpsStatusEntity
import com.kcteam.app.utils.AppUtils
import kotlinx.android.synthetic.main.inflate_gps_status.view.*

/**
 * Created by Saikat on 26-10-2018.
 */
class GpsStatusAdapter(private val context: Context, private val gpsStatusList: List<GpsStatusEntity>) : RecyclerView.Adapter<GpsStatusAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, gpsStatusList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_gps_status, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return  /*10*/ gpsStatusList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, gpsStatusList: List<GpsStatusEntity>) {

            if (adapterPosition % 2 == 0)
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            itemView.tv_date.text = AppUtils.convertToCommonFormat(gpsStatusList[adapterPosition].date!!)
            itemView.tv_gps_off_time.text = gpsStatusList[adapterPosition].gps_off_time
            itemView.tv_gps_on_time.text = gpsStatusList[adapterPosition].gps_on_time
            itemView.tv_duration.text = AppUtils.getTimeInHourMinuteFormat(gpsStatusList[adapterPosition].duration?.toLong()!!)
        }
    }
}