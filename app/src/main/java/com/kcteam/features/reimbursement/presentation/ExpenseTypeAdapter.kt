package com.kcteam.features.reimbursement.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.widgets.AppCustomTextView
import java.util.*

/**
 * Created by Saikat on 24-01-2019.
 */
class ExpenseTypeAdapter(private val context: Context, private val memberNoList: ArrayList<*>, private val onItemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<ExpenseTypeAdapter.ViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = inflater.inflate(R.layout.inflate_month_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == memberNoList.size - 1)
            holder.view.visibility = View.GONE
        else
            holder.view.visibility = View.VISIBLE

        holder.tv_month.text = memberNoList[position] as CharSequence?
    }

    override fun getItemCount(): Int {
        return memberNoList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var tv_month: AppCustomTextView
        internal var view: View

        init {
            tv_month = itemView.findViewById<View>(R.id.tv_month) as AppCustomTextView
            view = itemView.findViewById(R.id.view) as View

            tv_month.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            when (v.id) {
                R.id.tv_month -> onItemClickListener.onItemClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(adapterPosition: Int)
    }
}