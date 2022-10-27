package com.kcteam.features.dymanicSection.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.features.dymanicSection.model.AllDynamicDataModel
import com.kcteam.features.dymanicSection.model.DynamicListDataModel
import com.kcteam.widgets.AppCustomTextView
import kotlinx.android.synthetic.main.inflate_all_dynamic_list_item.view.*
import kotlinx.android.synthetic.main.inflate_dynamic_list_item.view.*

class DynamicListAdapter(private val context: Context, private val dynamicList: ArrayList<DynamicListDataModel>?,
                         private val onEditClick: (DynamicListDataModel) -> Unit) :
        RecyclerView.Adapter<DynamicListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_dynamic_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dynamicList?.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {

            itemView.apply {

                tv_date.text = dynamicList?.get(adapterPosition)?.date

                ll_item_body.removeAllViews()
                dynamicList?.get(adapterPosition)?.field_list?.forEach {
                    val textView = AppCustomTextView(context)
                    val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    p.topMargin = resources.getDimensionPixelOffset(R.dimen._5sdp)

                    textView.apply {
                        layoutParams = p
                        setTextColor(resources.getColor(R.color.black))
                        textSize = resources.getDimension(R.dimen._5sdp)
                        id = View.generateViewId()
                        text = it.key
                    }.let {
                        ll_item_body.addView(it)
                    }
                }

                iv_edit_icon.setOnClickListener {
                    onEditClick(dynamicList?.get(adapterPosition)!!)
                }
            }
        }
    }
}