package com.kcteam.features.addAttendence

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.RouteShopListEntity
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 22-11-2018.
 */
class RouteShopListDialog : DialogFragment() {

    private lateinit var mContext: Context
    private lateinit var iv_close_icon: ImageView
    private lateinit var rv_shop_list: RecyclerView
    private var routeId = ""
    private var isSelected = false
    private var adapter: RouteShopListAdapter? = null
    private lateinit var tv_ok_btn: AppCustomTextView

    companion object {

        private lateinit var addressUpdateClickListener: RouteShopClickLisneter

        fun getInstance(routeId: String, isSelected: Boolean, listener: RouteShopClickLisneter): RouteShopListDialog {
            val mUpdateShopAddressDialog = RouteShopListDialog()

            val bundle = Bundle()
            bundle.putString("routeId", routeId)
            bundle.putBoolean("isSelected", isSelected)
            mUpdateShopAddressDialog.arguments = bundle

            addressUpdateClickListener = listener
            return mUpdateShopAddressDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        routeId = arguments?.getString("routeId").toString()
        isSelected = arguments?.getBoolean("isSelected")!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_route_shop_list, container, false)
        //addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)
        isCancelable = false
        initView(v)
        return v
    }

    private fun initView(v: View) {
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        tv_ok_btn = v.findViewById(R.id.tv_ok_btn)
        rv_shop_list = v.findViewById(R.id.rv_shop_list)
        rv_shop_list.layoutManager = LinearLayoutManager(mContext)

        val list = AppDatabase.getDBInstance()?.routeShopListDao()?.getDataRouteIdWise(routeId) as ArrayList<RouteShopListEntity>
        adapter = RouteShopListAdapter(mContext, list, isSelected, object : RouteShopListAdapter.OnRouteClickListener {
            override fun onLeaveTypeClick(leaveTypeList: RouteShopListEntity?, adapterPosition: Int) {
                adapter?.notifyDataSetChanged()
                addressUpdateClickListener.onCheckClick(leaveTypeList)
            }
        })
        rv_shop_list.adapter = adapter

        iv_close_icon.setOnClickListener({
            dismiss()
        })

        tv_ok_btn.setOnClickListener({
            dismiss()
        })

    }

    interface RouteShopClickLisneter {
        fun onCheckClick(leaveTypeList: RouteShopListEntity?)
    }
}