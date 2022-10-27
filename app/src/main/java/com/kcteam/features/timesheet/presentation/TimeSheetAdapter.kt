package com.kcteam.features.timesheet.presentation

import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.TimesheetListEntity

import kotlinx.android.synthetic.main.inflate_timesheet_item.view.*

/**
 * Created by Saikat on 29-Apr-20.
 */
class TimeSheetAdapter(private val context: Context, private val timeSheetList: ArrayList<TimesheetListEntity>?,
                       private val onEditClick: (TimesheetListEntity) -> Unit, private val onDeleteClick: (TimesheetListEntity) -> Unit,
                       private val onSyncClick: (TimesheetListEntity) -> Unit, private val onImageClick: (TimesheetListEntity) -> Unit) :
        RecyclerView.Adapter<TimeSheetAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, timeSheetList, onEditClick, onDeleteClick, onSyncClick, onImageClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_timesheet_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return timeSheetList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, timeSheetList: ArrayList<TimesheetListEntity>?, onEditClick: (TimesheetListEntity) -> Unit,
                      onDeleteClick: (TimesheetListEntity) -> Unit, onSyncClick: (TimesheetListEntity) -> Unit, onImageClick: (TimesheetListEntity) -> Unit) {

            itemView.apply {

                /*if (!TextUtils.isEmpty(timeSheetList?.get(adapterPosition)?.date)) {
                    tv_date.visibility = View.VISIBLE
                    tv_date.text = AppUtils.convertToSelectedDateReimbursement(timeSheetList?.get(adapterPosition)?.date!!)
                } else
                    tv_date.visibility = View.GONE*/

                if (!TextUtils.isEmpty(timeSheetList?.get(adapterPosition)?.time)) {
                    tv_time.visibility = View.VISIBLE
                    tv_time.text = timeSheetList?.get(adapterPosition)?.time
                } else
                    tv_time.visibility = View.GONE

                if (!TextUtils.isEmpty(timeSheetList?.get(adapterPosition)?.project_name))
                    tv_project.text = timeSheetList?.get(adapterPosition)?.project_name
                else
                    tv_project.text = "N.A."

                if (!TextUtils.isEmpty(timeSheetList?.get(adapterPosition)?.activity_name))
                    tv_activity.text = timeSheetList?.get(adapterPosition)?.activity_name
                else
                    tv_activity.text = "N.A."

                if (!TextUtils.isEmpty(timeSheetList?.get(adapterPosition)?.product_name))
                    tv_product.text = timeSheetList?.get(adapterPosition)?.product_name
                else
                    tv_product.text = "N.A."

                if (!TextUtils.isEmpty(timeSheetList?.get(adapterPosition)?.comments))
                    tv_comments.text = timeSheetList?.get(adapterPosition)?.comments
                else
                    tv_comments.text = "N.A."

                if (!TextUtils.isEmpty(timeSheetList?.get(adapterPosition)?.client_name))
                    tv_client_name.text = timeSheetList?.get(adapterPosition)?.client_name
                else
                    tv_client_name.text = "N.A."

                if (!TextUtils.isEmpty(timeSheetList?.get(adapterPosition)?.image)) {
                    tv_image.text = timeSheetList?.get(adapterPosition)?.image
                    tv_image.paintFlags = tv_image.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                    tv_image.setTextColor(context.resources.getColor(R.color.link_blue))

                    tv_image.setOnClickListener {
                        onImageClick(timeSheetList?.get(adapterPosition)!!)
                    }
                }
                else {
                    tv_image.text = "N.A."
                    tv_image.paintFlags = tv_image.paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()
                    tv_image.setTextColor(context.resources.getColor(R.color.default_gray))
                }

                /*if (timeSheetList?.get(adapterPosition)?.isUpdateable!!) {
                    iv_edit_icon.visibility = View.VISIBLE
                    iv_del_view_icon.visibility = View.VISIBLE
                    tv_status.text = context.getString(R.string.pending)
                    tv_status.setTextColor(context.resources.getColor(R.color.red))
                } else {
                    iv_edit_icon.visibility = View.GONE
                    iv_del_view_icon.visibility = View.GONE
                    tv_status.text = context.getString(R.string.approved)
                    tv_status.setTextColor(context.resources.getColor(R.color.green))
                }*/

                if (!TextUtils.isEmpty(timeSheetList?.get(adapterPosition)?.status)) {
                    if (timeSheetList?.get(adapterPosition)?.status == context.getString(R.string.pending)) {
                        iv_edit_icon.visibility = View.VISIBLE
                        iv_del_view_icon.visibility = View.VISIBLE
                        tv_status.text = context.getString(R.string.pending)
                        tv_status.setTextColor(context.resources.getColor(R.color.red))
                    } else if (timeSheetList?.get(adapterPosition)?.status == context.getString(R.string.approved)) {
                        iv_edit_icon.visibility = View.GONE
                        iv_del_view_icon.visibility = View.GONE
                        tv_status.text = context.getString(R.string.approved)
                        tv_status.setTextColor(context.resources.getColor(R.color.green))
                    } else if (timeSheetList?.get(adapterPosition)?.status == context.getString(R.string.reject)) {
                        iv_edit_icon.visibility = View.GONE
                        iv_del_view_icon.visibility = View.GONE
                        tv_status.text = context.getString(R.string.reject)
                        tv_status.setTextColor(context.resources.getColor(android.R.color.holo_red_dark))
                    }
                }
                else {
                    iv_edit_icon.visibility = View.VISIBLE
                    iv_del_view_icon.visibility = View.VISIBLE
                    tv_status.text = context.getString(R.string.pending)
                    tv_status.setTextColor(context.resources.getColor(R.color.red))
                }

                if (timeSheetList?.get(adapterPosition)?.isUploaded!!)
                    sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                else {
                    sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    sync_icon.setOnClickListener {
                        onSyncClick(timeSheetList[adapterPosition])
                    }
                }

                iv_edit_icon.setOnClickListener {
                    onEditClick(timeSheetList[adapterPosition])
                }

                iv_del_view_icon.setOnClickListener {
                    onDeleteClick(timeSheetList[adapterPosition])
                }
            }
        }
    }

}