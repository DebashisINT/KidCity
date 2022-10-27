package com.kcteam.features.addAttendence

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.RouteShopListEntity
import kotlinx.android.synthetic.main.inflate_route_shop_list_item.view.*

/**
 * Created by Saikat on 22-11-2018.
 */
class RouteShopListAdapter(private val context: Context, private val routeList: ArrayList<RouteShopListEntity>, private val isSelected: Boolean,
                           private val listener: OnRouteClickListener) : RecyclerView.Adapter<RouteShopListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var isFirstTime = false

    init {
        isFirstTime = true
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_route_shop_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        /*if (isFirstTime) {
            holder.bindItems(context, routeList, listener, isSelected, isFirstTime)
            isFirstTime = false
        } else
            holder.bindItems(context, routeList, listener, isSelected, isFirstTime)*/

        holder.bindItems(context, routeList, listener, isSelected, isFirstTime)
        isFirstTime = false
    }

    override fun getItemCount(): Int {
        return routeList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, leaveTypeList: ArrayList<RouteShopListEntity>?, listener: OnRouteClickListener, isSelected: Boolean, isFirstTime: Boolean) {

            if (!leaveTypeList?.get(adapterPosition)?.shop_name.equals("other", ignoreCase = true)) {
                itemView.tv_shop_nema.setTextColor(context.resources.getColor(R.color.colorPrimary))
                //itemView.tv_shop_nema.textSize = context.resources.getDimensionPixelOffset(R.dimen._12sdp).toFloat()
            }
            else {
                itemView.tv_shop_nema.setTextColor(context.resources.getColor(R.color.login_txt_color))
                //itemView.tv_shop_nema.textSize = context.resources.getDimensionPixelOffset(R.dimen._11sdp).toFloat()
            }
            itemView.tv_shop_nema.text = leaveTypeList?.get(adapterPosition)?.shop_name
            itemView.tv_shop_address.text = leaveTypeList?.get(adapterPosition)?.shop_address
            itemView.tv_shop_contact.text = "Contact no: " + leaveTypeList?.get(adapterPosition)?.shop_contact_no

            //if (isFirstTime) {
            itemView.iv_route_check.isSelected = leaveTypeList?.get(adapterPosition)?.isSelected!!
            //}

            itemView.setOnClickListener {
                Log.e("route shop adapter", "====================on checked===================")
                if (itemView.iv_route_check.isSelected) {
                    itemView.iv_route_check.isSelected = false

                    leaveTypeList.get(adapterPosition).isSelected = false

                    AppDatabase.getDBInstance()?.routeShopListDao()?.updateIsUploadedAccordingToRouteAndShopId(itemView.iv_route_check.isSelected, leaveTypeList?.get(adapterPosition)?.route_id!!,
                            leaveTypeList.get(adapterPosition).shop_id!!)
                } else {
                    itemView.iv_route_check.isSelected = true

                    leaveTypeList.get(adapterPosition).isSelected = true

                    AppDatabase.getDBInstance()?.routeShopListDao()?.updateIsUploadedAccordingToRouteAndShopId(itemView.iv_route_check.isSelected, leaveTypeList?.get(adapterPosition)?.route_id!!,
                            leaveTypeList.get(adapterPosition).shop_id!!)
                }

                listener.onLeaveTypeClick(leaveTypeList.get(adapterPosition), adapterPosition)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    interface OnRouteClickListener {
        fun onLeaveTypeClick(leaveTypeList: RouteShopListEntity?, adapterPosition: Int)
    }
}