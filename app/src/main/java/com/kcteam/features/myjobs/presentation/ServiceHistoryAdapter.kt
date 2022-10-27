package com.kcteam.features.myjobs.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.myjobs.model.CustomerDataModel
import com.kcteam.features.myjobs.model.HistoryDataModel
import kotlinx.android.synthetic.main.inflate_service_history_item.view.*

class ServiceHistoryAdapter(private val mContext: Context, private val historyList: ArrayList<HistoryDataModel>) : RecyclerView.Adapter<ServiceHistoryAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_service_history_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                rating_bar.isEnabled = false
                tv_area_header.text = "Area (${historyList[adapterPosition].uom_text})"
                tv_service_due_header.text = "Service due for (${historyList[adapterPosition].uom_text})"
                tv_service_completed_header.text = "Service completed for (${historyList[adapterPosition].uom_text})"

                if (TextUtils.isEmpty(historyList[adapterPosition].schedule_date_time))
                    ll_schedule_date_time.visibility = View.GONE
                else {
                    ll_schedule_date_time.visibility = View.VISIBLE
                    tv_schedule_date_time.text = AppUtils.convertToNotificationDateTime(historyList[adapterPosition].schedule_date_time)
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].job_code))
                    ll_job_code.visibility = View.GONE
                else {
                    ll_job_code.visibility = View.VISIBLE
                    tv_job_code.text = historyList[adapterPosition].job_code
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].service_for))
                    ll_service_for.visibility = View.GONE
                else {
                    ll_service_for.visibility = View.VISIBLE
                    tv_service_for.text = historyList[adapterPosition].service_for
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].area))
                    ll_area.visibility = View.GONE
                else {
                    ll_area.visibility = View.VISIBLE
                    tv_area.text = historyList[adapterPosition].area
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].team))
                    ll_team.visibility = View.GONE
                else {
                    ll_team.visibility = View.VISIBLE
                    tv_team.text = historyList[adapterPosition].team
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].status))
                    ll_status.visibility = View.GONE
                else {
                    ll_status.visibility = View.VISIBLE
                    tv_status.text = historyList[adapterPosition].status
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].start_date_time))
                    ll_start_date_time.visibility = View.GONE
                else {
                    ll_start_date_time.visibility = View.VISIBLE
                    tv_start_date_time.text = historyList[adapterPosition].start_date_time
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].service_due_for))
                    ll_service_due_for.visibility = View.GONE
                else {
                    ll_service_due_for.visibility = View.VISIBLE
                    tv_service_due.text = historyList[adapterPosition].service_due_for
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].service_completed_for))
                    ll_service_completed.visibility = View.GONE
                else {
                    ll_service_completed.visibility = View.VISIBLE
                    tv_service_completed.text = historyList[adapterPosition].service_completed_for
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].next_date_time))
                    ll_next_date_time.visibility = View.GONE
                else {
                    ll_next_date_time.visibility = View.VISIBLE
                    tv_next_date_time.text = AppUtils.convertToNotificationDateTime(historyList[adapterPosition].next_date_time)
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].wip_remarks))
                    ll_wip_remarks.visibility = View.GONE
                else {
                    ll_wip_remarks.visibility = View.VISIBLE
                    tv_wip_remarks.text = historyList[adapterPosition].wip_remarks
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].wip_attachment))
                    ll_wip_attachment.visibility = View.GONE
                else {
                    ll_wip_attachment.visibility = View.VISIBLE
                    tv_wip_attachment.text = historyList[adapterPosition].wip_attachment
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].wip_photo))
                    ll_wip_photo.visibility = View.GONE
                else {
                    ll_wip_photo.visibility = View.VISIBLE
                    tv_wip_photo.text = historyList[adapterPosition].wip_photo
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].hold_date_time))
                    ll_hold_date_time.visibility = View.GONE
                else {
                    ll_hold_date_time.visibility = View.VISIBLE
                    tv_hold_date_time.text = AppUtils.convertToNotificationDateTime(historyList[adapterPosition].hold_date_time)
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].hold_reason))
                    ll_reason_hold.visibility = View.GONE
                else {
                    ll_reason_hold.visibility = View.VISIBLE
                    tv_hold_reason.text = historyList[adapterPosition].hold_reason
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].hold_remarks))
                    ll_hold_remarks.visibility = View.GONE
                else {
                    ll_hold_remarks.visibility = View.VISIBLE
                    tv_hold_remarks.text = historyList[adapterPosition].hold_remarks
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].hold_attachment))
                    ll_hold_attachment.visibility = View.GONE
                else {
                    ll_hold_attachment.visibility = View.VISIBLE
                    tv_hold_attachment.text = historyList[adapterPosition].hold_attachment
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].hold_photo))
                    ll_hold_photo.visibility = View.GONE
                else {
                    ll_hold_photo.visibility = View.VISIBLE
                    tv_hold_photo.text = historyList[adapterPosition].hold_photo
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].complete_date_time))
                    ll_completed_date_time.visibility = View.GONE
                else {
                    ll_completed_date_time.visibility = View.VISIBLE
                    tv_completed_date_time.text = AppUtils.convertToNotificationDateTime(historyList[adapterPosition].complete_date_time)
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].complete_remarks))
                    ll_completed_remarks.visibility = View.GONE
                else {
                    ll_completed_remarks.visibility = View.VISIBLE
                    tv_completed_remarks.text = historyList[adapterPosition].complete_remarks
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].complete_attachment))
                    ll_complete_attachment.visibility = View.GONE
                else {
                    ll_complete_attachment.visibility = View.VISIBLE
                    tv_completed_attachment.text = historyList[adapterPosition].complete_attachment
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].complete_photo))
                    ll_complete_photo.visibility = View.GONE
                else {
                    ll_complete_photo.visibility = View.VISIBLE
                    tv_completed_photo.text = historyList[adapterPosition].complete_photo
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].cancelled_date_time))
                    ll_canceled_date_time.visibility = View.GONE
                else {
                    ll_canceled_date_time.visibility = View.VISIBLE
                    tv_canceled_date_time.text = historyList[adapterPosition].cancelled_date_time
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].cancel_reason))
                    ll_cancel_reason.visibility = View.GONE
                else {
                    ll_cancel_reason.visibility = View.VISIBLE
                    tv_cancel_reason.text = historyList[adapterPosition].cancel_reason
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].cancelled_date_time))
                    ll_cancel_remarks.visibility = View.GONE
                else {
                    ll_cancel_remarks.visibility = View.VISIBLE
                    tv_cancel_remarks.text = historyList[adapterPosition].cancelled_date_time
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].cancel_attachment))
                    ll_cancel_attachment.visibility = View.GONE
                else {
                    ll_cancel_attachment.visibility = View.VISIBLE
                    tv_cancel_attachment.text = historyList[adapterPosition].cancel_attachment
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].cancel_photo))
                    ll_cancel_photo.visibility = View.GONE
                else {
                    ll_cancel_photo.visibility = View.VISIBLE
                    tv_cancel_photo.text = historyList[adapterPosition].cancel_photo
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].review_details))
                    ll_review_details.visibility = View.GONE
                else {
                    ll_review_details.visibility = View.VISIBLE
                    tv_review_details.text = historyList[adapterPosition].review_details
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].review_attachment))
                    ll_review_attachment.visibility = View.GONE
                else {
                    ll_review_attachment.visibility = View.VISIBLE
                    tv_review_attachment.text = historyList[adapterPosition].review_attachment
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].review_photo))
                    ll_review_photo.visibility = View.GONE
                else {
                    ll_review_photo.visibility = View.VISIBLE
                    tv_review_photo.text = historyList[adapterPosition].review_photo
                }

                if (TextUtils.isEmpty(historyList[adapterPosition].ratings) || historyList[adapterPosition].ratings.toFloat() == 0.0f)
                    ll_ratings.visibility = View.GONE
                else {
                    ll_ratings.visibility = View.VISIBLE
                    rating_bar.rating = historyList[adapterPosition].ratings.toFloat()
                }
            }
        }
    }
}