package com.kcteam.features.addAttendence

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.login.model.LoginStateListDataModel
import kotlinx.android.synthetic.main.inflate_primary_value_item.view.*

/**
 * Created by Saikat on 01-03-2019.
 */
class PrimaryValueAdapter(private val context: Context, private val stateList: java.util.ArrayList<LoginStateListDataModel>) : RecyclerView.Adapter<PrimaryValueAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    companion object {
        val primaryValueList = ArrayList<String>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_primary_value_item, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, stateList, primaryValueList)
    }

    override fun getItemCount(): Int {
        return stateList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(context: Context, stateList: ArrayList<LoginStateListDataModel>?, primaryValueList: ArrayList<String>) {

            itemView.tv_state_name.text = stateList?.get(adapterPosition)?.state_name

            itemView.et_primary_value_plan.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    try {

                        var isEdited = false

                        for (i in primaryValueList.indices) {
                            if (i == adapterPosition) {
                                isEdited = true
                                primaryValueList[i] = itemView.et_primary_value_plan.text.toString().trim()
                                break
                            }
                        }

                        if (!isEdited)
                            primaryValueList.add(itemView.et_primary_value_plan.text.toString().trim())

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

            })

        }
    }
}