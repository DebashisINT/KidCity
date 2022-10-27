package com.kcteam.features.know_your_state

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.know_your_state.model.KnowYourStateListDataModel
import kotlinx.android.synthetic.main.inflate_know_state_list_item.view.*

/**
 * Created by Saikat on 27-11-2019.
 */
class KnowStateListAdapter(private val context: Context, private val knowStateList: ArrayList<KnowYourStateListDataModel>) :
        RecyclerView.Adapter<KnowStateListAdapter.MyViewHolder>() {

    private var layoutInflater: LayoutInflater? = null

    init {
        layoutInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater?.inflate(R.layout.inflate_know_state_list_item, parent, false)
        return MyViewHolder(v!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, knowStateList)
    }

    override fun getItemCount(): Int {
        return knowStateList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, knowStateList: ArrayList<KnowYourStateListDataModel>) {

            if (adapterPosition % 2 == 0)
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.report_screen_bg))
            else
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

            itemView.tv_header.text = knowStateList[adapterPosition].Key + ":"

            if (!TextUtils.isEmpty(knowStateList[adapterPosition].Value))
                itemView.tv_value.text = knowStateList[adapterPosition].Value
            else
                itemView.tv_value.text = "N.A."
        }
    }

}