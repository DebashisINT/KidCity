package com.kcteam.features.task.presentation

import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.TaskEntity
import kotlinx.android.synthetic.main.inflate_task_item.view.*

/**
 * Created by Saikat on 13-Aug-20.
 */
class TaskAdapter(private val context: Context, private val taskList: ArrayList<TaskEntity>?,
                  private val onEditClick: (TaskEntity) -> Unit, private val onDeleteClick: (TaskEntity) -> Unit,
                  private val onSyncClick: (TaskEntity) -> Unit, private val onStatusClick: (TaskEntity) -> Unit) :
        RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, taskList, onEditClick, onDeleteClick, onSyncClick, onStatusClick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_task_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return taskList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, taskList: ArrayList<TaskEntity>?, onEditClick: (TaskEntity) -> Unit,
                      onDeleteClick: (TaskEntity) -> Unit, onSyncClick: (TaskEntity) -> Unit, onStatusClick: (TaskEntity) -> Unit) {

            itemView.apply {

                tv_task.text = taskList?.get(adapterPosition)?.task_name

                if (TextUtils.isEmpty(taskList?.get(adapterPosition)?.details))
                    tv_details.text = "N.A."
                else
                    tv_details.text = taskList?.get(adapterPosition)?.details

                if (taskList?.get(adapterPosition)?.isUploaded!!) {
                    if (taskList[adapterPosition].isStatusUpdated == 0) {
                        sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                        sync_icon.setOnClickListener {
                            onSyncClick(taskList[adapterPosition])
                        }
                    }
                    else
                        sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                } else {
                    sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)
                    sync_icon.setOnClickListener {
                        onSyncClick(taskList[adapterPosition])
                    }
                }

                if (taskList[adapterPosition].isCompleted) {
                    iv_edit_icon.visibility = View.GONE
                    iv_del_view_icon.visibility = View.GONE
                    iv_status_change_icon.isSelected = true
                    tv_task.paintFlags = tv_task.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    tv_task_header.paintFlags = tv_task_header.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    iv_edit_icon.visibility = View.VISIBLE
                    iv_del_view_icon.visibility = View.VISIBLE
                    iv_status_change_icon.isSelected = false
                    tv_task.paintFlags = tv_task.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    tv_task_header.paintFlags = tv_task_header.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                iv_edit_icon.setOnClickListener {
                    onEditClick(taskList[adapterPosition])
                }

                iv_del_view_icon.setOnClickListener {
                    onDeleteClick(taskList[adapterPosition])
                }

                iv_status_change_icon.setOnClickListener {
                    onStatusClick(taskList[adapterPosition])
                }
            }
        }
    }
}