package com.kcteam.features.chat.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.domain.MemberShopEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.chat.model.ChatUserDataModel
import kotlinx.android.synthetic.main.inflate_new_chat_user_item.view.*


class ChatUserAdapter(private val mContext: Context, chatUserList: ArrayList<ChatUserDataModel>?,
                      private var onItemClick: (ChatUserDataModel) -> Unit) : RecyclerView.Adapter<ChatUserAdapter.MyViewHolder>(),
        Filterable  {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    private var mChatUserList: ArrayList<ChatUserDataModel>? = null
    private var tempUserList: ArrayList<ChatUserDataModel>? = null
    private var filterUserList: ArrayList<ChatUserDataModel>? = null

    init {
        mChatUserList = ArrayList()
        tempUserList = ArrayList()
        filterUserList = ArrayList()

        mChatUserList?.addAll(chatUserList!!)
        tempUserList?.addAll(chatUserList!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_new_chat_user_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mChatUserList?.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {

                if (adapterPosition % 2 == 0)
                    setBackgroundColor(mContext.resources.getColor(R.color.report_screen_bg))
                else
                    setBackgroundColor(mContext.resources.getColor(R.color.white))

                tv_user_name.text = AppUtils.decodeEmojiAndText(mChatUserList?.get(adapterPosition)?.name!!)

                Glide.with(mContext)
                        .load(mChatUserList?.get(adapterPosition)?.image)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_menu_profile_image).error(R.drawable.ic_menu_profile_image))
                        .into(iv_profile_picture)

                if (mChatUserList?.get(adapterPosition)?.last_msg_user_id == "0")
                    tv_last_msg.text = AppUtils.decodeEmojiAndText(mChatUserList?.get(adapterPosition)?.last_msg!!)
                else if (Pref.user_id == mChatUserList?.get(adapterPosition)?.last_msg_user_id)
                    tv_last_msg.text = "You: " + AppUtils.decodeEmojiAndText(mChatUserList?.get(adapterPosition)?.last_msg!!)
                else {
                    if (mChatUserList?.get(adapterPosition)?.isGroup!!)
                        tv_last_msg.text = mChatUserList?.get(adapterPosition)?.last_msg_user_name + ": " + AppUtils.decodeEmojiAndText(mChatUserList?.get(adapterPosition)?.last_msg!!)
                    else
                        tv_last_msg.text = AppUtils.decodeEmojiAndText(mChatUserList?.get(adapterPosition)?.last_msg!!)
                }

                if (AppUtils.changeAttendanceDateFormatToCurrent(mChatUserList?.get(adapterPosition)?.last_msg_time!!) == AppUtils.getCurrentDateForShopActi())
                    tv_time.text = AppUtils.getMeredianTimeFromISODateTime(mChatUserList?.get(adapterPosition)?.last_msg_time!!)
                else
                    tv_time.text = AppUtils.convertDateTimeToCommonFormat(mChatUserList?.get(adapterPosition)?.last_msg_time!!)

                setOnClickListener {
                    onItemClick(mChatUserList?.get(adapterPosition)!!)
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            filterUserList?.clear()

            tempUserList?.indices!!
                    .filter { tempUserList?.get(it)?.name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { filterUserList?.add(tempUserList?.get(it)!!) }

            results.values = filterUserList
            results.count = filterUserList?.size!!

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                filterUserList = results?.values as ArrayList<ChatUserDataModel>?
                mChatUserList?.clear()
                val hashSet = HashSet<String>()
                if (filterUserList != null) {

                    filterUserList?.indices!!
                            .filter { hashSet.add(filterUserList?.get(it)?.id!!) }
                            .forEach { mChatUserList?.add(filterUserList?.get(it)!!) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshList(chatUserList: ArrayList<ChatUserDataModel>) {
        mChatUserList?.clear()
        mChatUserList?.addAll(chatUserList)

        tempUserList?.clear()
        tempUserList?.addAll(chatUserList)

        if (filterUserList == null)
            filterUserList = ArrayList()
        filterUserList?.clear()

        notifyDataSetChanged()
    }
}