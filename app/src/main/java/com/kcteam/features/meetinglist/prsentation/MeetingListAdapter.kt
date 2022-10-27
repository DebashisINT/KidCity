package com.kcteam.features.meetinglist.prsentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.MeetingEntity
import com.kcteam.app.utils.AppUtils
import kotlinx.android.synthetic.main.inflate_meeting_item.view.*

/**
 * Created by Saikat on 20-01-2020.
 */
class MeetingListAdapter(context: Context, val meetingList: ArrayList<MeetingEntity>?, val listener: OnClickListener) :
        RecyclerView.Adapter<MeetingListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    //var userLocationDataEntity: List<OrderDetailsListEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, meetingList, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_meeting_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return meetingList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, meetingList: ArrayList<MeetingEntity>?, listener: OnClickListener) {

            if (!TextUtils.isEmpty(meetingList?.get(adapterPosition)?.address))
                itemView.tv_meeting_address.text = meetingList?.get(adapterPosition)?.address
            else
                itemView.tv_meeting_address.text = "Unknown"

            if (!TextUtils.isEmpty(meetingList?.get(adapterPosition)?.remakrs))
                itemView.tv_remarks.text = meetingList?.get(adapterPosition)?.remakrs
            else
                itemView.tv_remarks.text = "N.A."

            if (!TextUtils.isEmpty(meetingList?.get(adapterPosition)?.date_time))
                itemView.tv_meeting_date.text = AppUtils.convertToNotificationDateTime(meetingList?.get(adapterPosition)?.date_time!!)
            else
                itemView.tv_meeting_date.text = "N.A."

            val meetingType = AppDatabase.getDBInstance()!!.addMeetingTypeDao().getSingleType(meetingList?.get(adapterPosition)?.meetingTypeId?.toInt()!!)

            if (meetingType != null)
                itemView.tv_market_type.text = meetingType.typeText
            else
                itemView.tv_market_type.text = "N.A."

            if (meetingList[adapterPosition].isDurationCalculated) {
                //itemView.ll_duration.visibility = View.VISIBLE
                itemView.sync_icon.visibility = View.VISIBLE

                itemView.tv_duration.text = meetingList[adapterPosition].duration_spent

                if (meetingList[adapterPosition].isUploaded)
                    itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_sync)
                else {
                    itemView.sync_icon.setImageResource(R.drawable.ic_registered_shop_not_sync)

                    itemView.sync_icon.setOnClickListener({
                        listener.onSyncClick(adapterPosition)
                    })
                }

            } else {
                //itemView.ll_duration.visibility = View.GONE
                itemView.sync_icon.visibility = View.INVISIBLE
            }
        }
    }

    interface OnClickListener {
        fun onSyncClick(adapterPosition: Int)
    }
}