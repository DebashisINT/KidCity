package com.kcteam.features.timesheet.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.kcteam.R
import com.kcteam.app.domain.ProjectListEntity
import com.kcteam.features.timesheet.model.TimeSheetProjectDataModel
import com.kcteam.widgets.AppCustomTextView
import java.util.ArrayList
import java.util.HashSet

/**
 * Created by Saikat on 29-Apr-20.
 */
class ProjectAdapter(private val context: Context, private val clientList: ArrayList<ProjectListEntity>,
                     private val onItemClickListener: (ProjectListEntity) -> Unit) : RecyclerView.Adapter<ProjectAdapter.ViewHolder>(), Filterable {

    private val inflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private var tempList: ArrayList<ProjectListEntity>
    private var fileteredList: ArrayList<ProjectListEntity>
    private var projectList: ArrayList<ProjectListEntity>

    init {
        tempList = ArrayList()
        fileteredList = ArrayList()
        projectList = ArrayList()

        tempList.addAll(clientList)
        projectList.addAll(clientList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //val v = inflater.inflate(R.layout.inflate_month_item, parent, false)
        val v = inflater.inflate(R.layout.exp_popup_window_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == projectList.size - 1)
            holder.view.visibility = View.GONE
        else
            holder.view.visibility = View.VISIBLE


        holder.list_item_tv.text = projectList[position].project_name
        holder.tv_phn_no.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)/*, View.OnClickListener*/ {

        /*internal var tv_month: AppCustomTextView
        internal var view: View
        internal var ll_shop_details: LinearLayout
        internal var tv_shop_name: AppCustomTextView
        internal var tv_shop_area: AppCustomTextView*/

        var list_item_tv: AppCustomTextView
        var tv_phn_no: AppCustomTextView
        var view: View

        init {
            /*tv_month = itemView.findViewById<View>(R.id.tv_month) as AppCustomTextView
            view = itemView.findViewById(R.id.view) as View
            ll_shop_details = itemView.find(R.id.ll_shop_details)
            tv_shop_name = itemView.find(R.id.tv_shop_name)
            tv_shop_area = itemView.find(R.id.tv_shop_area)

            tv_month.setOnClickListener(this)
            ll_shop_details.setOnClickListener(this)*/

            list_item_tv = itemView.findViewById(R.id.list_item_tv)
            tv_phn_no = itemView.findViewById(R.id.tv_phn_no)
            view = itemView.findViewById(R.id.view)

            itemView.setOnClickListener {
                onItemClickListener(projectList[adapterPosition])
            }
        }

        /*override fun onClick(v: View) {
            when (v.id) {
                R.id.tv_month -> onItemClickListener.onItemClick(tempList[adapterPosition])

                R.id.ll_shop_details -> onItemClickListener.onItemClick(tempList[adapterPosition])
            }
        }*/
    }

    /*interface OnItemClickListener {
        fun onItemClick(customer: CustomerDataModel)
    }*/

    override fun getFilter(): Filter {
        return SearchFilter()
    }

    inner class SearchFilter : Filter() {
        override fun performFiltering(p0: CharSequence?): FilterResults {
            val results = FilterResults()

            fileteredList.clear()

            tempList.indices
                    .filter { tempList.get(it).project_name?.toLowerCase()?.contains(p0?.toString()?.toLowerCase()!!)!! }
                    .forEach { fileteredList.add(tempList.get(it)) }

            results.values = fileteredList
            results.count = fileteredList.size

            return results
        }

        override fun publishResults(p0: CharSequence?, results: FilterResults?) {

            try {
                fileteredList = results?.values as ArrayList<ProjectListEntity>
                projectList.clear()
                val hashSet = HashSet<String>()
                if (fileteredList != null) {

                    fileteredList.indices
                            .filter { hashSet.add(fileteredList[it].project_id!!) }
                            .forEach { projectList.add(fileteredList[it]) }

                    notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}