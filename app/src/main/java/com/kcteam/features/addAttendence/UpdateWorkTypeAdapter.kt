package com.kcteam.features.addAttendence

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.domain.WorkTypeEntity
import kotlinx.android.synthetic.main.inflate_vehicle_log_type.view.*

/**
 * Created by Saikat on 31-Jul-20.
 */
class UpdateWorkTypeAdapter(private val context: Context, private val workTypeList: ArrayList<WorkTypeEntity>?,
                            private var isFirstTime: Boolean, private val listener: OnWorkTypeClickListener) :
        RecyclerView.Adapter<UpdateWorkTypeAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private var mWorkTypeList: ArrayList<WorkTypeEntity>

    init {
        mWorkTypeList = ArrayList()
        mWorkTypeList.addAll(workTypeList!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_vehicle_log_type, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, workTypeList, listener)
    }

    override fun getItemCount(): Int {
        return workTypeList?.size!!
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, workTypeList: ArrayList<WorkTypeEntity>?, listener: OnWorkTypeClickListener) {

            itemView.tv_log_type.text = workTypeList?.get(adapterPosition)?.Descrpton
            //itemView.iv_check.isSelected = workTypeList?.get(adapterPosition)?.isSelected!!
            //itemView.iv_check.visibility = View.GONE

            if (!TextUtils.isEmpty(Pref.isFieldWorkVisible) && Pref.isFieldWorkVisible.equals("false", ignoreCase = true)) {
                if (itemView.tv_log_type.text.toString().trim().equals(context.getString(R.string.field_work), ignoreCase = true)) {
                    itemView.ll_work_type_main.visibility = View.GONE
                } else {
                    itemView.ll_work_type_main.visibility = View.VISIBLE
                }
            } else {
                if (itemView.tv_log_type.text.toString().trim().equals(context.getString(R.string.sales_visit), ignoreCase = true)) {
                    itemView.ll_work_type_main.visibility = View.GONE
                } else {
                    itemView.ll_work_type_main.visibility = View.VISIBLE
                }
            }

            try {
                if (isFirstTime)
                    itemView.iv_check.isSelected = workTypeList?.get(adapterPosition)?.isSelected!!
                else {
                    if (!Pref.isMultipleAttendanceSelection)
                        itemView.iv_check.isSelected = workTypeList?.get(adapterPosition)?.isSelected!!
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }


            itemView.setOnClickListener {

                if (Pref.isMultipleAttendanceSelection) {
                    if (itemView.iv_check.isSelected) {
                        itemView.iv_check.isSelected = false
                        //workTypeList?.get(adapterPosition)?.isSelected = false
                    } else {
                        itemView.iv_check.isSelected = true
                        //workTypeList?.get(adapterPosition)?.isSelected = true
                    }
                    listener.onWorkTypeClick(workTypeList?.get(adapterPosition), adapterPosition)
                } else {
                    if (itemView.iv_check.isSelected) {
                        //itemView.iv_check.isSelected = false
                        mWorkTypeList[adapterPosition].isSelected = false
                    } else {
                        //itemView.iv_check.isSelected = true

                        mWorkTypeList.forEach {
                            it.isSelected = false
                        }

                        mWorkTypeList[adapterPosition].isSelected = true
                    }
                    listener.onWorkTypeClick(mWorkTypeList[adapterPosition], adapterPosition)
                    isFirstTime = false
                    notifyDataSetChanged()
                }
            }
        }
    }

    interface OnWorkTypeClickListener {
        fun onWorkTypeClick(workType: WorkTypeEntity?, adapterPosition: Int)
    }
}