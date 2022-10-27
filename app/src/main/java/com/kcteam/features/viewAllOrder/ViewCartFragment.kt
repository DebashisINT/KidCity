package com.kcteam.features.viewAllOrder

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
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 21-11-2018.
 */
class ViewCartFragment : BaseFragment() {

    private lateinit var mContext: Context
    private lateinit var tv_total_order_value: AppCustomTextView
    private lateinit var tv_total_order_amount: AppCustomTextView
    private lateinit var ll_schemeRoot: LinearLayout
    private lateinit var tv_total_order_amount_sc: AppCustomTextView
    private lateinit var rv_cart_list: RecyclerView
    private lateinit var tv_cancel: AppCustomTextView
    private lateinit var tv_continue: AppCustomTextView
    private lateinit var fab_add: FloatingActionButton
    private lateinit var rl_cart_main: RelativeLayout
    private var selectedItems = ArrayList<Int>()
    private lateinit var ll_btns: LinearLayout
    private var orderId = ""
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var ll_top: LinearLayout
    private lateinit var cart_view: View
    private lateinit var tv_order_id: AppCustomTextView
    private lateinit var tv_order_date: AppCustomTextView
    private lateinit var tv_shop_name: AppCustomTextView
    private lateinit var iv_call_icon: ImageView
    private lateinit var ll_patient_info: LinearLayout
    private lateinit var tv_patient_name: AppCustomTextView
    private lateinit var tv_patient_no: AppCustomTextView
    private lateinit var tv_patient_address: AppCustomTextView
    private lateinit var tv_patient_lab: AppCustomTextView
    private lateinit var tv_patient_emailaddress: AppCustomTextView

    companion object {

        private var orderDetails: OrderDetailsListEntity? = null

        fun newInstance(objects: Any): ViewCartFragment {
            val fragment = ViewCartFragment()
            /*val bundle = Bundle()
            bundle.putString("order_id", objects.toString())
            fragment.arguments = bundle*/

            if (objects is OrderDetailsListEntity)
                orderDetails = objects
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = orderDetails?.order_id!! //arguments?.getString("order_id").toString()
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

        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(orderDetails?.shop_id)
        tv_shop_name.text = shop.shopName

        tv_order_id = view.findViewById(R.id.tv_order_id)
        tv_order_id.text = orderId

        tv_order_date = view.findViewById(R.id.tv_order_date)
        tv_order_date.text = AppUtils.convertDateTimeToCommonFormat(orderDetails?.date!!)

        ll_btns = view.findViewById(R.id.ll_btns)
        ll_btns.visibility = View.GONE

        tv_total_order_amount = view.findViewById(R.id.tv_total_order_amount)
        rv_cart_list = view.findViewById(R.id.rv_cart_list)
        tv_cancel = view.findViewById(R.id.tv_cancel)
        tv_continue = view.findViewById(R.id.tv_continue)
        fab_add = view.findViewById(R.id.fab_add)
        iv_call_icon = view.findViewById(R.id.iv_call_icon)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        ll_patient_info = view.findViewById(R.id.ll_patient_info)
        tv_patient_name = view.findViewById(R.id.tv_patient_name)
        tv_patient_no = view.findViewById(R.id.tv_patient_no)
        tv_patient_address = view.findViewById(R.id.tv_patient_address)

        /*06-01-2022*/
        tv_patient_lab = view.findViewById(R.id.tv_patient_lab)
        tv_patient_emailaddress = view.findViewById(R.id.tv_patient_emailaddress)

        rl_cart_main = view.findViewById(R.id.rl_cart_main)
        ll_schemeRoot = view.findViewById(R.id.ll_frag_cart_new_scheme_root)
        tv_total_order_amount_sc = view.findViewById(R.id.tv_total_scheme_amount)
        rl_cart_main.setOnClickListener(null)

        rv_cart_list.layoutManager = LinearLayoutManager(mContext)

        if (!Pref.IsnewleadtypeforRuby) {
            ll_schemeRoot.visibility = View.GONE
        } else {
            ll_schemeRoot.visibility = View.VISIBLE

        }

        if (Pref.isPatientDetailsShowInOrder)
            ll_patient_info.visibility = View.VISIBLE
        else
            ll_patient_info.visibility = View.GONE

        if (!TextUtils.isEmpty(orderDetails?.patient_name))
            tv_patient_name.text = orderDetails?.patient_name

        if (!TextUtils.isEmpty(orderDetails?.patient_no))
            tv_patient_no.text = orderDetails?.patient_no

        if (!TextUtils.isEmpty(orderDetails?.patient_address))
            tv_patient_address.text = orderDetails?.patient_address

        if (!TextUtils.isEmpty(orderDetails?.Hospital))
            tv_patient_lab.text = orderDetails?.Hospital

        if (!TextUtils.isEmpty(orderDetails?.Email_Address))
            tv_patient_emailaddress.text = orderDetails?.Email_Address

        val list = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToOrderId(orderId)

        if (list != null)
            tv_total_order_value.text = list.size.toString()

        if (list != null && list.isNotEmpty()) {
            rv_cart_list.adapter = ViewCartAdapter(mContext, list)
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

        Handler().postDelayed(Runnable {
            var totalScAmount = 0.0
            try {
                for (i in list.indices) {
                    totalScAmount += list[i].total_scheme_price?.toDouble()!!
                }
            }catch(ex:java.lang.Exception) {
                ex.printStackTrace()
                totalScAmount = 0.0
            }
            val totalScPrice = String.format("%.2f", totalScAmount.toFloat())
            tv_total_order_amount_sc.text = totalScPrice
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
    }

}