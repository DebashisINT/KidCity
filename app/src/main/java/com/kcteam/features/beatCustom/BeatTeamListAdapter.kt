package com.kcteam.features.beatCustom

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.NewOrderGenderEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.survey.GroupNameOnClick
import com.kcteam.features.survey.SurveyFromListDialog
import com.kcteam.features.survey.SurveyFromTypeListAdapter
import com.kcteam.features.viewAllOrder.interf.GenderListOnClick
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import kotlinx.android.synthetic.main.row_beat_custom_list.view.*
import kotlinx.android.synthetic.main.row_dialog_new_order_gender.view.*

class BeatTeamListAdapter(private var context:Context, private var list:ArrayList<BeatViewModel>):
  RecyclerView.Adapter<BeatTeamListAdapter.BeatTeamListViewHolder>(){
    private  var adapter:BeatTeamListsubAdapter? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeatTeamListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_beat_custom_list,parent,false)
        return BeatTeamListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list!!.size!!
    }

    override fun onBindViewHolder(holder: BeatTeamListViewHolder, position: Int) {
        holder.dateTV.text = "Date: "+AppUtils.getFormatedDateNew(list.get(position).date,"yyyy-mm-dd","dd-mm-yyyy")
        holder.beatTV.text = "Beat Name: "+list.get(position).beatName

        adapter= BeatTeamListsubAdapter(context,list.get(position).beatList)
        holder.rv_sub_list.adapter=adapter

        }


    inner class BeatTeamListViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val rv_sub_list = itemView.rv_frag_team_sub_beat_list
        val dateTV = itemView.tv_dt_row_beat_cus_list
        val beatTV = itemView.tv_beat_name_row_beat_cus_list

    }


}

