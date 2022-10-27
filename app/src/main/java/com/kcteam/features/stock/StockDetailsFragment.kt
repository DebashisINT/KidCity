package com.kcteam.features.stock

import android.content.Context
import android.os.Bundle
import android.os.Handler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.StockDetailsListEntity
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.widgets.AppCustomTextView


/**
 * Created by Saikat on 11-09-2019.
 */
class StockDetailsFragment: BaseFragment() {

    private lateinit var mContext: Context
    private lateinit var tv_total_order_value: AppCustomTextView
    private lateinit var tv_total_order_amount: AppCustomTextView
    private lateinit var rv_cart_list: RecyclerView
    private lateinit var tv_cancel: AppCustomTextView
    private lateinit var tv_continue: AppCustomTextView
    private lateinit var fab_add: FloatingActionButton
    private lateinit var rl_cart_main: RelativeLayout
    private var selectedItems = ArrayList<Int>()
    private lateinit var ll_btns: LinearLayout
    private var stockId = ""
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var ll_top: LinearLayout
    private lateinit var cart_view: View
    private lateinit var tv_order_id: AppCustomTextView
    private lateinit var tv_order_date: AppCustomTextView
    private lateinit var tv_shop_name: AppCustomTextView
    private lateinit var iv_call_icon: ImageView
    private lateinit var tv_total_value_header: AppCustomTextView
    private lateinit var ll_patient_info: LinearLayout
    private lateinit var ll_scheme_info: LinearLayout

    private lateinit var tv_frag_cart_order_id: AppCustomTextView

    companion object {

        private var stockDetails: StockDetailsListEntity? = null

        fun newInstance(objects: Any): StockDetailsFragment {
            val fragment = StockDetailsFragment()
            /*val bundle = Bundle()
            bundle.putString("order_id", objects.toString())
            fragment.arguments = bundle*/

            if (objects is StockDetailsListEntity)
                stockDetails = objects
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stockId = stockDetails?.stock_id!! //arguments?.getString("order_id").toString()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        cart_view = view.findViewById(R.id.cart_view)
        cart_view.visibility = View.VISIBLE

        ll_top = view.findViewById(R.id.ll_top)
        ll_top.visibility = View.VISIBLE

        tv_total_order_value = view.findViewById(R.id.tv_total_order_value)
        tv_shop_name = view.findViewById(R.id.tv_shop_name)

        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(stockDetails?.shop_id)
        tv_shop_name.text = shop.shopName

        tv_order_id = view.findViewById(R.id.tv_order_id)
        tv_order_id.text = stockId

        tv_order_date = view.findViewById(R.id.tv_order_date)
        tv_order_date.text = AppUtils.convertDateTimeToCommonFormat(stockDetails?.date!!)

        ll_btns = view.findViewById(R.id.ll_btns)
        ll_btns.visibility = View.GONE

        tv_total_order_amount = view.findViewById(R.id.tv_total_order_amount)
        rv_cart_list = view.findViewById(R.id.rv_cart_list)
        tv_cancel = view.findViewById(R.id.tv_cancel)
        tv_continue = view.findViewById(R.id.tv_continue)
        fab_add = view.findViewById(R.id.fab_add)
        iv_call_icon = view.findViewById(R.id.iv_call_icon)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        tv_total_value_header = view.findViewById(R.id.tv_total_value_header)

        tv_frag_cart_order_id = view.findViewById(R.id.tv_frag_cart_order_id)

        rl_cart_main = view.findViewById(R.id.rl_cart_main)
        rv_cart_list.layoutManager = LinearLayoutManager(mContext)

        val list = AppDatabase.getDBInstance()!!.stockProductDao().getDataAccordingToStockId(stockId)

        if (list != null)
            tv_total_order_value.text = list.size.toString()

        if (list != null && list.isNotEmpty()) {
            rv_cart_list.adapter = StockDetailsAdapter(mContext, list)
            tv_no_data_available.visibility = View.GONE
        } else
            tv_no_data_available.visibility = View.VISIBLE

        /*if (selectedProductList != null && selectedProductList?.size!! > 0)
            initAdapter()*/

        Handler().postDelayed(Runnable {
            var totalAmount = 0.0

            for (i in list.indices) {
                totalAmount += list[i].total_price?.toDouble()!!
            }
            //val totalPrice = DecimalFormat("##.##").format(totalAmount)
            val totalPrice = String.format("%.2f", totalAmount.toFloat())
            tv_total_order_amount.text = totalPrice
        }, 200)


        if (!TextUtils.isEmpty(shop.ownerContactNumber) && AppUtils.isValidateMobile(shop.ownerContactNumber)) {
            iv_call_icon.visibility = View.VISIBLE

            if (shop.is_otp_verified.equals("true", ignoreCase = true)){
                iv_call_icon.setImageResource(R.drawable.ic_registered_shop_call_select_green)
            }
            else {
                iv_call_icon.setImageResource(R.drawable.ic_registered_shop_call_deselect)
            }

            iv_call_icon.setOnClickListener {
                IntentActionable.initiatePhoneCall(context, shop.ownerContactNumber)
            }
        }
        else
            iv_call_icon.visibility = View.GONE

        tv_total_value_header.text = getString(R.string.total_stock_value_with_colon)


        ll_patient_info = view.findViewById(R.id.ll_patient_info)
        ll_scheme_info = view.findViewById(R.id.ll_frag_cart_new_scheme_root)
        ll_scheme_info.visibility=View.GONE
        if (Pref.isPatientDetailsShowInOrder)
            ll_patient_info.visibility = View.VISIBLE
        else
            ll_patient_info.visibility = View.GONE

        tv_frag_cart_order_id.text="Stock ID:"

    }

}