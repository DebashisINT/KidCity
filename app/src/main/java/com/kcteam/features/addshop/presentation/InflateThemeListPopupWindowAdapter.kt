package com.kcteam.features.addshop.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kcteam.R
import java.util.*

/**
 * Created by sandip on 28-11-2017.
 */
class InflateThemeListPopupWindowAdapter(private val context: Context, private val populate_list_items: ArrayList<String>?, onPopupMenuClickListener: onPopupMenuClickListener) : BaseAdapter() {
    private val mInflater: LayoutInflater
    private lateinit var mPopUpClickListener: onPopupMenuClickListener

    init {

        this.mPopUpClickListener = onPopupMenuClickListener
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return populate_list_items?.size ?: 0
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        var holder: Holder? = null

        if (convertView == null) {
            holder = Holder()
            convertView = mInflater.inflate(R.layout.exp_popup_window_list_item, null)
            holder.list_item_tv = convertView.findViewById(R.id.list_item_tv)

            convertView.tag = holder
        } else {
            holder = convertView.tag as Holder
        }

        holder.list_item_tv!!.setText(populate_list_items!![position])

        convertView?.setOnClickListener { mPopUpClickListener.onPopupMenuClick(populate_list_items!![position], position) }

        return convertView
    }

    inner class Holder {
        internal var list_item_tv: TextView? = null
    }
}