package com.kcteam.features.chat.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.chat.model.ChatListDataModel
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter
import kotlinx.android.synthetic.main.inflate_chat_item.view.*
import kotlinx.android.synthetic.main.inflate_chat_msg_header.view.*
import java.lang.Exception

class ChatListAdapter(private val mContext: Context, private val isGroup: Boolean, private val onPagination: () -> Unit) : RecyclerView.Adapter<ChatListAdapter.MyViewHolder>(),
        StickyRecyclerHeadersAdapter<ChatListAdapter.ViewHolder> {

    val chatList: ArrayList<ChatListDataModel> by lazy {
        ArrayList<ChatListDataModel>()
    }

    private var previousGetCount = 0

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.inflate_chat_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun getHeaderId(position: Int): Long {
        return AppUtils.convertDateStringToLong(AppUtils.changeAttendanceDateFormatToCurrent(chatList[position].time))
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup?): ViewHolder {
        val v = LayoutInflater.from(mContext).inflate(R.layout.inflate_chat_msg_header, parent, false)
        return ViewHolder(v)
    }

    override fun onBindHeaderViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bindItems(position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {

            if (adapterPosition == 0) {
                if (itemCount > 1 && itemCount != previousGetCount) {
                    onPagination()
                    previousGetCount = itemCount
                }
            }

            itemView.apply {

                val msgTime = AppUtils.getMeredianTimeFromISODateTime(chatList[adapterPosition].time)

                /*val msgDateToShow = AppUtils.getFormattedDateFromDateTime(chatList[adapterPosition].time)
                tv_date.text = msgDateToShow*/

                if (chatList[adapterPosition].from_id == "0") {
                    fl_user_msg.visibility = View.GONE
                    fl_my_msg.visibility = View.GONE
                    tv_system_msg.visibility = View.VISIBLE
                    tv_system_msg.text = AppUtils.decodeEmojiAndText(chatList[adapterPosition].msg)
                }
                else if (chatList[adapterPosition].from_id.equals(Pref.user_id!!, ignoreCase = true)) {
                    fl_user_msg.visibility = View.GONE
                    fl_my_msg.visibility = View.VISIBLE
                    tv_system_msg.visibility = View.GONE
                    tv_my_msg.text = AppUtils.decodeEmojiAndText(chatList[adapterPosition].msg)
                    tv_my_time.text = msgTime
                    tv_my_status.text = chatList[adapterPosition].status
                }
                else {
                    fl_user_msg.visibility = View.VISIBLE
                    fl_my_msg.visibility = View.GONE
                    tv_system_msg.visibility = View.GONE
                    tv_user_msg.text = AppUtils.decodeEmojiAndText(chatList[adapterPosition].msg)
                    tv_user_time.text = msgTime
                }

                tv_user_name.text = chatList[adapterPosition].from_name
                if (isGroup)
                    tv_user_name.visibility = View.VISIBLE
                else
                    tv_user_name.visibility = View.GONE

            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(position: Int) {
            itemView.apply {
                try {
                    tv_chat_header.text = AppUtils.getFormattedDateFromDateTime(chatList[position].time)
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun refreshList(chat_list: ArrayList<ChatListDataModel>) {
        chatList.clear()
        chatList.addAll(chat_list)
        notifyDataSetChanged()
    }

    fun refreshListWithOneMsg(msg: ChatListDataModel) {
        chatList.add(msg)
        notifyItemInserted(chatList.size - 1)
    }

    fun refreshListForPagination(chat_list: ArrayList<ChatListDataModel>) {
        /*chatList.addAll(0, chat_list)
        notifyItemRangeInserted(0, chat_list.size)
        notifyDataSetChanged()*/

        chatList.addAll(0, chat_list)
        notifyDataSetChanged()
    }
}