package com.kcteam.features.member.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.member.model.TeamPjpDataModel
import kotlinx.android.synthetic.main.inflate_member_pjp.view.*


/**
 * Created by Saikat on 30-Mar-20.
 */
class MemberPJPAdapter(private val context: Context, private val pjpList: ArrayList<TeamPjpDataModel>, private val onEditClick: (Int) -> Unit,
                       private val onDelClick: (Int) -> Unit) : RecyclerView.Adapter<MemberPJPAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_member_pjp, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, pjpList, onEditClick, onDelClick)
    }

    override fun getItemCount(): Int {
        return pjpList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, pjpList: ArrayList<TeamPjpDataModel>, onEditClick: (Int) -> Unit, onDelClick: (Int) -> Unit) {
            itemView.apply {
                if (!TextUtils.isEmpty(pjpList[adapterPosition].date))
                    tv_date.text = AppUtils.convertToSelectedDateReimbursement(pjpList[adapterPosition].date)
                else
                    tv_date.text = "N.A."

                tv_from_to_time.text = pjpList[adapterPosition].from_time + " - " + pjpList[adapterPosition].to_time

                if (!TextUtils.isEmpty(pjpList[adapterPosition].location)) {
                    tv_location.visibility = View.VISIBLE

                    val text = "<font color=" + context.resources.getColor(R.color.dark_gray) + ">Location: </font> <font color="+
                            context.resources.getColor(R.color.black) + ">" + pjpList[adapterPosition].location + "</font>"
                    tv_location.text = Html.fromHtml(text)
                } else
                    tv_location.visibility = View.GONE

                if (!TextUtils.isEmpty(pjpList[adapterPosition].customer_name)) {
                    tv_name.visibility = View.VISIBLE
                    val text = "<font color=" + context.resources.getColor(R.color.dark_gray) + ">Customer: </font> <font color="+
                            context.resources.getColor(R.color.black) + ">" + pjpList[adapterPosition].customer_name + "</font>"
                    tv_name.text = Html.fromHtml(text)

                } else
                    tv_name.visibility = View.GONE

                /*if (pjpList[adapterPosition].isUpdateable) {
                    iv_edit_icon.visibility = View.VISIBLE
                    iv_del_view_icon.visibility = View.VISIBLE
                } else {
                    iv_edit_icon.visibility = View.GONE
                    iv_del_view_icon.visibility = View.GONE
                }*/

                if (Pref.isAllowPJPUpdateForTeam) {
                    iv_edit_icon.visibility = View.VISIBLE
                    iv_del_view_icon.visibility = View.VISIBLE
                } else {
                    if (pjpList[adapterPosition].user_id == Pref.user_id) {
                        iv_edit_icon.visibility = View.VISIBLE
                        iv_del_view_icon.visibility = View.VISIBLE
                    } else {
                        iv_edit_icon.visibility = View.GONE
                        iv_del_view_icon.visibility = View.GONE
                    }
                }

                if (!TextUtils.isEmpty(pjpList[adapterPosition].remarks)) {
                    ll_remarks.visibility = View.VISIBLE
                    tv_remarks.text = pjpList[adapterPosition].remarks
                } else
                    ll_remarks.visibility = View.GONE

                iv_edit_icon.setOnClickListener {
                    onEditClick(adapterPosition)
                }

                iv_del_view_icon.setOnClickListener {
                    onDelClick(adapterPosition)
                }
            }
        }
    }
}