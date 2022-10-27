package com.kcteam.features.addAttendence

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.RouteEntity
import kotlinx.android.synthetic.main.inflate_vehicle_log_type.view.*

/**
 * Created by Saikat on 08-11-2018.
 */
class RouteAdapter(private val context: Context, private val routeList: ArrayList<RouteEntity>, private val selectionStatus: Int,
                   private val route_id: String, private val listener: OnRouteClickListener) : RecyclerView.Adapter<RouteAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_vehicle_log_type, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, routeList, listener, selectionStatus, route_id)
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, leaveTypeList: ArrayList<RouteEntity>?, listener: OnRouteClickListener, selectionStatus: Int, route_id: String) {

            itemView.tv_log_type.text = leaveTypeList?.get(adapterPosition)?.route_name
            //itemView.iv_check.isSelected = workTypeList?.get(adapterPosition)?.isSelected!!

            if (selectionStatus == 1 /*&& route_id == leaveTypeList?.get(adapterPosition)?.route_id*/) {
                itemView.iv_check.isSelected = leaveTypeList?.get(adapterPosition)?.isSelected!!

                if (itemView.iv_check.isSelected)
                    listener.onRouteCheckClick(leaveTypeList[adapterPosition], adapterPosition, false)
                /*else
                    listener.unCheckRoute(leaveTypeList[adapterPosition], adapterPosition)*/
            }
            else if (selectionStatus == 0) {
                itemView.iv_check.isSelected = leaveTypeList?.get(adapterPosition)?.isSelected!!

                if (!itemView.iv_check.isSelected) {
                    if (leaveTypeList[adapterPosition].route_id == route_id)
                        listener.unCheckRoute(leaveTypeList[adapterPosition], adapterPosition)
                }
            }

            itemView.iv_check.setOnClickListener {

                if (itemView.iv_check.isSelected) {
                    itemView.iv_check.isSelected = false
                    //workTypeList?.get(adapterPosition)?.isSelected = false
                } else {
                    itemView.iv_check.isSelected = true
                    //workTypeList?.get(adapterPosition)?.isSelected = true
                }

                val list = AppDatabase.getDBInstance()?.routeShopListDao()?.getDataRouteIdWise(leaveTypeList?.get(adapterPosition)?.route_id!!)
                for (i in list?.indices!!) {
                    AppDatabase.getDBInstance()?.routeShopListDao()?.updateIsUploadedAccordingToRouteId(itemView.iv_check.isSelected, leaveTypeList?.get(adapterPosition)?.route_id!!)
                }

                listener.onRouteCheckClick(leaveTypeList?.get(adapterPosition), adapterPosition, true)
            }

            itemView.tv_log_type.setOnClickListener({
                listener.onRouteTextClick(leaveTypeList?.get(adapterPosition), adapterPosition, itemView.iv_check.isSelected)
            })
        }
    }

    interface OnRouteClickListener {
        fun onRouteCheckClick(route: RouteEntity?, adapterPosition: Int, isCheckBoxClicked: Boolean)

        fun onRouteTextClick(route: RouteEntity?, adapterPosition: Int, selected: Boolean)

        fun unCheckRoute(route: RouteEntity?, adapterPosition: Int)
    }
}