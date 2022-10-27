package com.kcteam.features.beatCustom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.NewOrderGenderEntity
import com.kcteam.features.survey.GroupNameOnClick
import com.kcteam.features.survey.SurveyFromListDialog
import com.kcteam.features.survey.SurveyFromTypeListAdapter
import com.kcteam.features.viewAllOrder.interf.GenderListOnClick
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.android.synthetic.main.row_beat_custom_list.view.*
import kotlinx.android.synthetic.main.row_beat_custom_sub_list.view.*
import kotlinx.android.synthetic.main.row_dialog_new_order_gender.view.*

class BeatTeamListsubAdapter(private var context:Context, private var list:ArrayList<BeatViewListModel>):
  RecyclerView.Adapter<BeatTeamListsubAdapter.BeatTeamListViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeatTeamListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_beat_custom_sub_list,parent,false)
        return BeatTeamListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list!!.size!!
    }

    override fun onBindViewHolder(holder: BeatTeamListViewHolder, position: Int) {
        holder.shopNameTV.text=list.get(position).cusName
        holder.statusTV.text=list.get(position).status
        holder.visitTimeTV.text=list.get(position).vTime

        if(position%2 == 0){
            holder.llRoot.setBackgroundResource(R.drawable.shape_cursor_one)
        }else{
            holder.llRoot.setBackgroundResource(R.drawable.shape_cursor_five)
        }

    }


    inner class BeatTeamListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val shopNameTV = itemView.tv_shop_name_row_beat_cus_sublist
        val statusTV = itemView.tv_status_row_beat_cus_sublist
        val visitTimeTV = itemView.tv_visit_time_row_beat_cus_sublist
        val llRoot = itemView.ll_row_beat_custom_bub_root

    }


}

