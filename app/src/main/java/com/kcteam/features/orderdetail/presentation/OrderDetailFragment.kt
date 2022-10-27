package com.kcteam.features.orderdetail.presentation

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.base.presentation.BaseFragment

/**
 * Created by Pratishruti on 30-10-2017.
 */
class OrderDetailFragment :BaseFragment(){
    private lateinit var mOrderDetailAdapter:OrderDetailAdapter
    private lateinit var orderDetailRecyclerView: RecyclerView
    private lateinit var mContext: Context
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater!!.inflate(R.layout.fragment_order_detail, container, false)
        initView(view)
        return view
    }
    private fun initView(view:View) {
        orderDetailRecyclerView=view.findViewById(R.id.order_detail_RCV)
        initAdapter()
    }

    private fun initAdapter() {
        mOrderDetailAdapter= OrderDetailAdapter(mContext)
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        orderDetailRecyclerView.layoutManager=layoutManager
        orderDetailRecyclerView.adapter=mOrderDetailAdapter

    }


}