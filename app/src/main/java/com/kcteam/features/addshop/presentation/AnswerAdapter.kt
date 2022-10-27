package com.kcteam.features.addshop.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.StageEntity
import com.kcteam.features.addshop.model.DCyesno
import com.kcteam.features.viewAllOrder.interf.QaOnCLick
import kotlinx.android.synthetic.main.inflate_answer.view.*
import kotlinx.android.synthetic.main.inflate_shop_type.view.*
import kotlinx.android.synthetic.main.inflate_shop_type.view.view


class AnswerAdapter(private val context: Context, val items:ArrayList<String>,private val onItemClick:(Int) -> Unit?) : RecyclerView.Adapter<AnswerAdapter.ViewHolder>() {
    //var items = arrayOf("Yes", "No")
//    private val inflater: LayoutInflater by lazy {
//        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.inflate_answer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(position:Int) {
            itemView.apply {
                tv_ans_yes.text = items[position]

                if (adapterPosition == items.size - 1)
                    view.visibility = View.GONE
                else
                    view.visibility = View.VISIBLE

                setOnClickListener {
                    //onItemClick(items[adapterPosition])
                    onItemClick(adapterPosition)
                }
            }
        }
    }
}