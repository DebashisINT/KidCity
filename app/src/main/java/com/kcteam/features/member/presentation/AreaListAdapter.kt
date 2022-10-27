package com.kcteam.features.member.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.domain.TeamAreaEntity
import com.kcteam.features.addshop.model.AreaListDataModel
import kotlinx.android.synthetic.main.inflate_area_list_item.view.*

/**
 * Created by Saikat on 14-May-20.
 */
class AreaListAdapter(private val context: Context, private val areaList: ArrayList<AreaListDataModel>, private val onItemClick: (Int) -> Unit) :
        RecyclerView.Adapter<AreaListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_area_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return areaList.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            try {

                itemView.apply {
                    if (adapterPosition == areaList.size - 1)
                        area_view.visibility = View.GONE
                    else
                        area_view.visibility = View.VISIBLE

                    tv_area_name.text = areaList[adapterPosition].area_name

                    setOnClickListener {
                        onItemClick(adapterPosition)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}