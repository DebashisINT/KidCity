package com.kcteam.features.task.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.TaskEntity
import kotlinx.android.synthetic.main.inflate_event_item.view.*

class EventAdapter(private var mContext: Context, private val list: ArrayList<TaskEntity>?) : RecyclerView.Adapter<EventAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_event_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list?.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                tv_event_name.text = list?.get(adapterPosition)?.task_name

                if (!TextUtils.isEmpty(list?.get(adapterPosition)?.details)) {
                    tv_details.text = list?.get(adapterPosition)?.details
                    tv_details.visibility = View.VISIBLE
                }
                else
                    tv_details.visibility = View.GONE

                if(list?.get(adapterPosition)?.isCompleted!!)
                    tv_status.text = "Completed"
                else
                    tv_status.text = "Pending"

            }
        }
    }
}