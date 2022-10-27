package com.kcteam.features.dymanicSection.presentation

import android.content.Context
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.TaskEntity
import com.kcteam.features.dymanicSection.model.AllDynamicDataModel
import com.kcteam.features.task.presentation.TaskAdapter
import kotlinx.android.synthetic.main.inflate_all_dynamic_list_item.view.*

class AllDynamicListAdapter(private val context: Context, private val dynamicList: ArrayList<AllDynamicDataModel>?,
                            private val onItemClick: (AllDynamicDataModel) -> Unit) :
        RecyclerView.Adapter<AllDynamicListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_all_dynamic_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dynamicList?.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {

            itemView.apply {

                tv_name.text = dynamicList?.get(adapterPosition)?.name

                setOnClickListener {
                    onItemClick(dynamicList?.get(adapterPosition)!!)
                }
            }
        }
    }
}