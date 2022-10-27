package com.kcteam.test.viewPermission

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.permissionList.ViewPermissionFragment
import kotlinx.android.synthetic.main.row_permission_list.view.*

class AdapterViewPermission(val context: Context,val permList:List<ViewPermissionFragment.PermissionDetails>): RecyclerView.Adapter<AdapterViewPermission.ViewPermission>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPermission {
        val view = LayoutInflater.from(context).inflate(R.layout.row_permission_list,parent,false)
        return ViewPermission(view)
    }

    override fun onBindViewHolder(holder: ViewPermission, position: Int) {
        holder.tv_permiName.text=permList[position].permissionName
        if(permList[position].permissionTag == 3){
            holder.tv_permiTag.text="Permission Granted"
            holder.tv_permiTag.setTextColor(context.getResources().getColor(R.color.green))
        }else if(permList[position].permissionTag == 1){
            holder.tv_permiTag.text="Permission Not Granted"
            holder.tv_permiTag.setTextColor(context.getResources().getColor(R.color.red))
        }
    }

    override fun getItemCount(): Int {
        return permList.size
    }

    inner class ViewPermission(itemView: View): RecyclerView.ViewHolder(itemView){
        val tv_permiName = itemView.tv_permi_name
        val tv_permiTag = itemView.tv_permi_tag
    }

}