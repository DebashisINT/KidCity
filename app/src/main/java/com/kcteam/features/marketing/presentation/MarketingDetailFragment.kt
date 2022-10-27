package com.kcteam.features.marketing.presentation

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
import com.kcteam.features.marketing.model.MarketingDetailData


/**
 * Created by Pratishruti on 23-02-2018.
 */
class MarketingDetailFragment:BaseFragment() {
    lateinit var marketing_item_list: ArrayList<MarketingDetailData>
    private lateinit var marketing_detail_RCV: RecyclerView
    private lateinit var adapter:MarketingDetailAdapter
    private lateinit var mContext:Context
    private lateinit var layoutManager: RecyclerView.LayoutManager

        companion object {
        private val ARG_LIST = "marketing_list"

        fun newInstance(list: ArrayList<MarketingDetailData>): MarketingDetailFragment {
            val args: Bundle = Bundle()
            args.putParcelableArrayList(ARG_LIST, list)
            val fragment = MarketingDetailFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        marketing_item_list=arguments?.getSerializable(ARG_LIST) as ArrayList<MarketingDetailData>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_retail_branding_material, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        marketing_detail_RCV=view.findViewById(R.id.marketing_detail_RCV)
        adapter= MarketingDetailAdapter(mContext,marketing_item_list, object : RecyclerViewClickListener {
            override fun getDeleteItemPosition(position: Int) {
            }

            override fun getPosition(position: Int) {
//                setMarketingDetailDate
            }
        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        marketing_detail_RCV.layoutManager = layoutManager
        marketing_detail_RCV.adapter=adapter
    }
}