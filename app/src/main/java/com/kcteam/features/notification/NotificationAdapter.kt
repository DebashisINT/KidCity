package com.kcteam.features.notification

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.notification.model.NotificationListDataModel
import kotlinx.android.synthetic.main.inflate_notification_item.view.*

/**
 * Created by Saikat on 25-02-2019.
 */
class NotificationAdapter(context: Context, val notificationList: ArrayList<NotificationListDataModel>?, val listener: OnClickListener) :
        RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater
    private var context: Context
    //var userLocationDataEntity: List<OrderDetailsListEntity> = userLocationDataEntity

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, notificationList, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_notification_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return notificationList?.size!!
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, notificationList: ArrayList<NotificationListDataModel>?, listener: OnClickListener) {

            if (adapterPosition == notificationList!!.size - 1) {
                itemView.view.visibility = View.GONE
            } else
                itemView.view.visibility = View.VISIBLE


            itemView.tv_notification_msg.text = notificationList[adapterPosition].notificationmessage

            if (!TextUtils.isEmpty(notificationList[adapterPosition].date_time))
                itemView.tv_notification_date.text = AppUtils.convertToNotificationDateTime(notificationList[adapterPosition].date_time!!)


            itemView.setOnClickListener {
                listener.onNotificationClick(adapterPosition)
            }

            if(notificationList[adapterPosition].phoneNo.equals("")){
                itemView.iv_noti_item_whatsapp.visibility=View.GONE
            }else{
                itemView.iv_noti_item_whatsapp.visibility=View.VISIBLE
            }
            itemView.iv_noti_item_whatsapp.setOnClickListener {
                listener.getWhatsappOnLick(notificationList?.get(adapterPosition)?.phoneNo.toString())
            }

        }
    }

    interface OnClickListener {
        fun onNotificationClick(adapterPosition: Int)
        fun getWhatsappOnLick(phone: String)
    }
}