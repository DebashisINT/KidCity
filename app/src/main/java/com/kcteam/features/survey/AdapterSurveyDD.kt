package com.kcteam.features.survey

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import kotlinx.android.synthetic.main.row_qa_survey_list.view.*
import java.math.MathContext

class AdapterSurveyDD(var context: Context,var dataList:ArrayList<CheckB>,var isMulti:Boolean,var listner:OnOkClick):
    RecyclerView.Adapter<AdapterSurveyDD.SurveyDDViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurveyDDViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_qa_survey_list,parent,false)
        return SurveyDDViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurveyDDViewHolder, position: Int) {
        holder.quesTV.text=dataList.get(position).value
        if(dataList.get(position).isChk){
            holder.check.isChecked=true
        }else{
            holder.check.isChecked=false
        }
        holder.check.setOnClickListener {
            if(isMulti==false){
            for(i in 0..dataList.size-1){
                if(i!=holder.adapterPosition){
                    dataList.get(i).isChk=false
                }else{
                    dataList.get(i).isChk=true
                }
            }
            }else{
                if(holder.check.isChecked){
                    dataList.get(position).isChk=true
                }else{
                    dataList.get(position).isChk=false
                }
            }

            notifyDataSetChanged()
            listner.onCheckClick(dataList)
        }
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    inner class SurveyDDViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val quesTV = itemView.survey_question_TV
        val check = itemView.cb_row_survey_qa_list_yes
    }

    interface OnOkClick{
        fun onCheckClick(_list:ArrayList<CheckB>)
    }

}