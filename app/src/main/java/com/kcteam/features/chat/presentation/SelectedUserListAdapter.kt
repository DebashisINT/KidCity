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
import com.kcteam.features.chat.model.GroupUserDataModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import kotlinx.android.synthetic.main.inflate_chat_user_item.view.*
import kotlinx.android.synthetic.main.inflate_selected_user_item.view.*

class SelectedUserListAdapter(private val mContext: Context, private var onItemClick: (GroupUserDataModel, Int) -> Unit) : RecyclerView.Adapter<SelectedUserListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    val selectedUserList: ArrayList<GroupUserDataModel> by lazy {
        ArrayList<GroupUserDataModel>()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_selected_user_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return selectedUserList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {
            itemView.apply {

                if (selectedUserList[adapterPosition].name.contains("(")) {
                    val name = selectedUserList[adapterPosition].name.substring(0, selectedUserList[adapterPosition].name.indexOf("("))
                    tv_name.text = name
                }
                else
                    tv_name.text = selectedUserList[adapterPosition].name

                setOnClickListener {
                    onItemClick(selectedUserList[adapterPosition], adapterPosition)
                    //removeItem(adapterPosition)
                }
            }
        }
    }

    fun refreshList(grpUserList: ArrayList<GroupUserDataModel>) {
        selectedUserList.clear()
        selectedUserList.addAll(grpUserList)
        notifyDataSetChanged()
    }

    fun addItem(user: GroupUserDataModel) {
        selectedUserList.add(user)
        notifyItemInserted(selectedUserList.size - 1)
    }

    fun removeItem(position: Int) {
        selectedUserList.removeAt(position)
        notifyItemRemoved(position)
    }
}