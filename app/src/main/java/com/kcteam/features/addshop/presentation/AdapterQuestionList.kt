package com.kcteam.features.addshop.presentation

import android.content.Context
import android.view.*
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.QuestionEntity
import com.kcteam.app.utils.Toaster
import com.kcteam.features.viewAllOrder.interf.QaOnCLick
import kotlinx.android.synthetic.main.row_qa_list.view.*


class AdapterQuestionList(var context: Context, var quesAnsList:ArrayList<AddShopFragment.QuestionAns>, var view_list:ArrayList<QuestionEntity>,
                          var isFromAddShop:Boolean,var listner: QaOnCLick) :
        RecyclerView.Adapter<AdapterQuestionList.AdapterQuestionListHolder>(){

    var adapterAnsList: AnswerAdapter? = null
    var items : ArrayList<String> =ArrayList()

/*
    var updateQuesAnsList:ArrayList<AddShopFragment.QuestionAns>? = null

    init {
        updateQuesAnsList= ArrayList()
        updateQuesAnsList?.addAll(quesAnsList)
    }
*/


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterQuestionListHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_qa_list,parent,false)
        items.add("Yes")
        items.add("No")
        return AdapterQuestionListHolder(view)
    }

    override fun getItemCount(): Int {
        return view_list!!.size
    }

    override fun onBindViewHolder(holder: AdapterQuestionListHolder, position: Int) {

        //holder.bindItems(context, updateQuesAnsList!!)

        holder.questionName.text=view_list.get(position).question.toString()

        if(!quesAnsList.get(holder.adapterPosition).qAns.equals("-1")){
            if(quesAnsList.get(holder.adapterPosition).qAns.equals("1")){
                holder.questionAnswer.text="Yes"
                holder.questionAnswer.setTextColor(context.resources.getColor(R.color.color_custom_green))
                holder.cb_yes.isChecked = true
            }
            else{
                holder.questionAnswer.text="No"
                holder.questionAnswer.setTextColor(context.resources.getColor(R.color.color_custom_red))
                holder.cb_no.isChecked = true
            }
        }else{
            holder.questionAnswer.text="answer"
            holder.questionAnswer.setTextColor(context.resources.getColor(R.color.gray))
            holder.cb_yes.isChecked = false
            holder.cb_no.isChecked = false
        }


        holder.cb_yes.setOnCheckedChangeListener{ buttonView, isChecked ->
                if (isChecked){
                    holder.cb_no.isChecked=false
                    holder.questionAnswer.text = "Yes"
                    holder.questionAnswer.setTextColor(context.resources.getColor(R.color.color_custom_green))
                    listner.getQaID(view_list.get(holder.adapterPosition).question_id!!,"1")
                }else{
                    holder.cb_yes.isChecked=false
                }
            }
        holder.cb_no.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked){
                holder.cb_yes.isChecked=false
                holder.questionAnswer.text = "No"
                holder.questionAnswer.setTextColor(context.resources.getColor(R.color.color_custom_red))
                listner.getQaID(view_list.get(holder.adapterPosition).question_id!!,"0")
            }else{
                holder.cb_no.isChecked=false
            }
        }

    }

    inner class AdapterQuestionListHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val questionName=itemView.question_TV
        val questionAnswer=itemView.answer_TV
        val answer_ll=itemView.answer_ll
        val cb_yes=itemView.cb_row_qa_list_yes
        val cb_no=itemView.cb_row_qa_list_no

    }



}