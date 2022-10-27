package com.kcteam.features.member.presentation

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.model.TeamListDataModel
import com.kcteam.widgets.AppCustomTextView
import kotlinx.android.synthetic.main.inflate_member_list_item.view.*


/**
 * Created by Saikat on 29-01-2020.
 */
class MemberListAdapter(context: Context, val teamList: ArrayList<TeamListDataModel>, val listener: OnClickListener) :
        RecyclerView.Adapter<MemberListAdapter.MyViewHolder>(), Filterable {

    private val layoutInflater: LayoutInflater
    private var context: Context
    private var mTeamList: ArrayList<TeamListDataModel>? = null
    private var tempTeamList: ArrayList<TeamListDataModel>? = null
    private var filterTeamList: ArrayList<TeamListDataModel>? = null

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        mTeamList = ArrayList()
        tempTeamList = ArrayList()
        filterTeamList = ArrayList()

        mTeamList?.addAll(teamList)
        tempTeamList?.addAll(teamList)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, mTeamList!!, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_member_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mTeamList!!.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, teamList: ArrayList<TeamListDataModel>, listener: OnClickListener) {

            itemView.tv_name.text = teamList[adapterPosition].user_name

            itemView.tv_shop_details.text = "Click for " + Pref.shopText + " Details"

            if (Pref.isActivatePJPFeature) {
                /*if (Pref.isAllowPJPUpdateForTeam)
                    itemView.iv_pjp_icon.visibility = View.VISIBLE
                else {
                    if (teamList[adapterPosition].user_id == Pref.user_id)
                        itemView.iv_pjp_icon.visibility = View.VISIBLE
                    else
                        itemView.iv_pjp_icon.visibility = View.GONE
                }*/
                itemView.iv_pjp_icon.visibility = View.VISIBLE
            } else
                itemView.iv_pjp_icon.visibility = View.GONE

            if((context as DashboardActivity).isAllTeam)
                itemView.tv_team_details.visibility = View.GONE
            else
                itemView.tv_team_details.visibility = View.VISIBLE

            if(teamList[adapterPosition].isLeaveApplied){
                if(teamList[adapterPosition].isLeavePending){
                    DrawableCompat.setTint(
                            DrawableCompat.wrap(itemView.iv_leave.getDrawable()),
                            ContextCompat.getColor(context,R.color.color_custom_red)
                    )
                }
                else{
                    DrawableCompat.setTint(
                            DrawableCompat.wrap(itemView.iv_leave.getDrawable()),
                            ContextCompat.getColor(context,R.color.color_custom_green)
                    )
                }
            }
             else{
                DrawableCompat.setTint(
                        DrawableCompat.wrap(itemView.iv_leave.getDrawable()),
                        ContextCompat.getColor(context,R.color.default_gray)
                )
            }
//                itemView.iv_leave.setImageDrawable(context.getDrawable(R.drawable.ic_applyleave))

            if(Pref.Leaveapprovalfromsupervisorinteam){
                itemView.iv_leave.visibility = View.VISIBLE
            }
            else{
                itemView.iv_leave.visibility = View.GONE
            }

            itemView.iv_leave.setOnClickListener({
                if(teamList[adapterPosition].isLeaveApplied){
                    listener.onLeaveClick(teamList[adapterPosition])
                }else{
                    val simpleDialog = Dialog(context)
                    simpleDialog.setCancelable(false)
                    simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    simpleDialog.setContentView(R.layout.dialog_message)
                    val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                    val dialogBody = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                    val obBtn = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                    dialogHeader.text="Hi "+Pref.user_name+"!"
                    dialogBody.text = "Nothing to show."
                    obBtn.setOnClickListener({ view ->
                        simpleDialog.cancel()

                    })
                    simpleDialog.show()
                }

            })
            itemView.tv_team_details.setOnClickListener({
                listener.onTeamClick(teamList[adapterPosition])
            })

            itemView.tv_shop_details.setOnClickListener({
                listener.onShopClick(teamList[adapterPosition])
            })

            itemView.iv_call_icon.setOnClickListener {
                listener.onCallClick(teamList[adapterPosition])
            }

            itemView.iv_pjp_icon.setOnClickListener {
                listener.onPjpClick(teamList[adapterPosition])
            }

            itemView.iv_map_icon.setOnClickListener {
                listener.onLocClick(teamList[adapterPosition])
            }

            itemView.iv_coll.setOnClickListener{
                listener.onCollClick(teamList[adapterPosition])
            }

            itemView.iv_job.visibility=View.GONE

            if(Pref.IsShowRepeatOrdersNotificationinTeam){
                itemView.iv_zero_order.visibility = View.VISIBLE
            }else{
                itemView.iv_zero_order.visibility = View.GONE
            }

            itemView.iv_zero_order.setOnClickListener{
                listener.onZeroOrderClick(teamList[adapterPosition])
            }

            if(Pref.IsBeatRouteReportAvailableinTeam){
                itemView.iv_beat.visibility = View.VISIBLE
            }else{
                itemView.iv_beat.visibility = View.GONE
            }

            itemView.iv_beat.setOnClickListener{
                listener.onBeatClick(teamList[adapterPosition])
            }

        }
    }

    interface OnClickListener {
        fun onLeaveClick(team: TeamListDataModel)

        fun onTeamClick(team: TeamListDataModel)

        fun onShopClick(team: TeamListDataModel)

        fun onCallClick(team: TeamListDataModel)

        fun onPjpClick(team: TeamListDataModel)

        fun onLocClick(team: TeamListDataModel)

        fun onCollClick(team: TeamListDataModel)

        fun onZeroOrderClick(team: TeamListDataModel)

        fun onBeatClick(team: TeamListDataModel)

        fun getSize(size: Int)
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterTeamList?.clear()

            tempTeamList?.indices!!
                    .filter { tempTeamList?.get(it)?.user_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterTeamList?.add(tempTeamList?.get(it)!!) }

            results.values = filterTeamList
            results.count = filterTeamList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterTeamList = results?.values as ArrayList<TeamListDataModel>?
                mTeamList?.clear()
                val hashSet = HashSet<String>()
                if (filterTeamList != null) {

                    filterTeamList?.indices!!
                            .filter { hashSet.add(filterTeamList?.get(it)?.user_id!!) }
                            .forEach { mTeamList?.add(filterTeamList?.get(it)!!) }

                    listener.getSize(mTeamList?.size!!)

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshList(teamList: ArrayList<TeamListDataModel>) {
        mTeamList?.clear()
        mTeamList?.addAll(teamList)

        tempTeamList?.clear()
        tempTeamList?.addAll(teamList)

        if (filterTeamList == null)
            filterTeamList = ArrayList()
        filterTeamList?.clear()

        notifyDataSetChanged()
    }

}