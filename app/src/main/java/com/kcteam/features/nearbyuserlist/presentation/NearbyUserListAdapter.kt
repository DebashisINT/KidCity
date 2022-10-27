package com.kcteam.features.nearbyuserlist.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.nearbyuserlist.model.NearbyUserDataModel
import kotlinx.android.synthetic.main.inflate_user_list_item.view.*

class NearbyUserListAdapter(private val mContext: Context, private val newUserList: ArrayList<NearbyUserDataModel>,
                            private val onCallClick: (Int) -> Unit, private val onWhatsAppClick: (Int) -> Unit,
                            private val onSmsClick: (Int) -> Unit) : RecyclerView.Adapter<NearbyUserListAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(mContext)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_user_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return newUserList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems() {
            itemView.apply {
                tv_name.text = newUserList[adapterPosition].name
                tv_phn_no.text = newUserList[adapterPosition].phone_no

                iv_call.setOnClickListener {
                    onCallClick(adapterPosition)
                }

                iv_sms.setOnClickListener {
                    onWhatsAppClick(adapterPosition)
                }

                iv_whatsapp.setOnClickListener {
                    onSmsClick(adapterPosition)
                }
            }
        }
    }
}