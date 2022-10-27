package com.kcteam.features.chatbot.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.chatbot.model.ChatBotDataModel
import kotlinx.android.synthetic.main.inflate_chat_bot_item.view.*

class ChatBotAdapter(private val mContext: Context, private var onBotActionClick: (String, Boolean) -> Unit) :
        RecyclerView.Adapter<ChatBotAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    private val chatList: ArrayList<ChatBotDataModel> by lazy {
        ArrayList<ChatBotDataModel>()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_chat_bot_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {
                if (chatList[adapterPosition].user.equals("bot", ignoreCase = true)) {
                    ll_bot_msg.visibility = View.VISIBLE
                    ll_user_msg.visibility = View.GONE
                    tv_bot_msg.text = chatList[adapterPosition].msg
                }
                else {
                    ll_bot_msg.visibility = View.GONE
                    ll_user_msg.visibility = View.VISIBLE
                    tv_user_msg.text = chatList[adapterPosition].msg
                }

                if (chatList[adapterPosition].isBotActionShow) {
                    iv_mic.visibility = View.VISIBLE

                    if (chatList[adapterPosition].isAudioPlaying)
                        iv_mic.setImageResource(R.drawable.ic_stop)
                    else
                        iv_mic.setImageResource(R.drawable.ic_mic_white)
                }
                else
                    iv_mic.visibility = View.GONE

                iv_mic.setOnClickListener {
                    if (chatList[adapterPosition].isAudioPlaying)
                        chatList[adapterPosition].isAudioPlaying = false
                    else
                        chatList[adapterPosition].isAudioPlaying = true
                    onBotActionClick(chatList[adapterPosition].msg, chatList[adapterPosition].isAudioPlaying)
                }
            }
        }
    }

    fun refreshList(chatBotList: ArrayList<ChatBotDataModel>) {
        chatList.clear()
        chatList.addAll(chatBotList)
        notifyDataSetChanged()
    }
}