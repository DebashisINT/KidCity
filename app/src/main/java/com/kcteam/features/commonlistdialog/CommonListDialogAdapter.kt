package com.kcteam.features.commonlistdialog
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import kotlinx.android.synthetic.main.inflate_comon_list_item.view.*


/**
 * Created by Pratishruti on 20-02-2018.
 */
class CommonListDialogAdapter(context: Context, list: List<String>, val listener: CommonListDialogClickListener): RecyclerView.Adapter<CommonListDialogAdapter.MyViewHolder>()  {
    private val layoutInflater: LayoutInflater
    private var context: Context
    private var mList: List<String>

    init {
        layoutInflater = LayoutInflater.from(context)
        this.context = context
        mList = list
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(context, mList, listener)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_comon_list_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(context: Context, list: List<String>, listener: CommonListDialogClickListener) {

            itemView.item_name_TV.text = list[adapterPosition]

            itemView.setOnClickListener(View.OnClickListener {
                listener.onItemClick(adapterPosition)
            })
        }

    }


}