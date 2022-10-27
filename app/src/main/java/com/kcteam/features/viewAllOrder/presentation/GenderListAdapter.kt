package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.NewOrderGenderEntity
import com.kcteam.features.viewAllOrder.interf.GenderListOnClick
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.android.synthetic.main.row_dialog_new_order_gender.view.*

class GenderListAdapter(private var context:Context,private var gender_list:ArrayList<NewOrderGenderEntity>,private val listner: GenderListOnClick):
  RecyclerView.Adapter<GenderListAdapter.GenderListViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenderListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_dialog_new_order_gender,parent,false)
        return GenderListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return gender_list!!.size!!
    }

    override fun onBindViewHolder(holder: GenderListViewHolder, position: Int) {
        holder.tv_gender.text=gender_list.get(position).gender!!

        holder.cv_gender.setOnClickListener { listner?.genderListOnClick(gender_list.get(holder.adapterPosition!!)) }
    }

    inner class GenderListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val tv_gender = itemView.tv_row_dialog_new_order_gender
        val cv_gender = itemView.cv_gender
    }

}

