package com.kcteam.features.returnsOrder

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.ProductListEntity
import com.kcteam.app.domain.ProductRateEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.swipemenulayout.SwipeMenuRecyclerView
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.presentation.AddShopFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.returnsOrder.ReturnTypeListFragment
import com.kcteam.features.shopdetail.presentation.ShopDetailFragment
import com.kcteam.features.viewAllOrder.CartFragment
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView


class CartReturnFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var tv_total_order_value: AppCustomTextView
    private lateinit var tv_total_order_amount: AppCustomTextView
    private lateinit var rv_cart_list: SwipeMenuRecyclerView
    private lateinit var tv_cancel: AppCustomTextView
    private lateinit var tv_continue: AppCustomTextView
    private lateinit var fab_add: FloatingActionButton
    private lateinit var rl_cart_main: RelativeLayout
    private var selectedItems = ArrayList<Int>()
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var tv_stock_value: AppCustomTextView

    private var remarks = ""
    private var imagePath = ""

    companion object {
        private var selectedProductList: ArrayList<ProductListEntity>? = null

        fun newInstance(objects: Any): CartReturnFragment {
            val fragment = CartReturnFragment()
            if (objects != null && objects is ArrayList<*>)
                selectedProductList = objects as ArrayList<ProductListEntity>
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_cart_new_return, container, false)
        initView(view)
        initClickListener()
        return view
    }

    private fun initView(view: View) {
        tv_total_order_value = view.findViewById(R.id.tv_total_order_value)

        if (selectedProductList != null)
            tv_total_order_value.text = selectedProductList?.size.toString()

        tv_total_order_amount = view.findViewById(R.id.tv_total_order_amount)
        rv_cart_list = view.findViewById(R.id.rv_cart_list)
        tv_cancel = view.findViewById(R.id.tv_cancel)
        tv_continue = view.findViewById(R.id.tv_continue)
        fab_add = view.findViewById(R.id.fab_add)
        rl_cart_main = view.findViewById(R.id.rl_cart_main)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        tv_stock_value = view.findViewById(R.id.tv_stock_value)
        rv_cart_list.layoutManager = LinearLayoutManager(mContext)

        if (selectedProductList != null && selectedProductList?.size!! > 0) {
            initAdapter()
            tv_no_data_available.visibility = View.GONE
        } else
            tv_no_data_available.visibility = View.VISIBLE

        Handler().postDelayed(Runnable {
            var totalAmount = 0.0

            for (i in (mContext as DashboardActivity).totalPrice.indices) {
                totalAmount += (mContext as DashboardActivity).totalPrice[i]
            }
            //val totalPrice = DecimalFormat("##.##").format(totalAmount)
            val totalPrice = String.format("%.2f", totalAmount.toFloat())
            tv_total_order_amount.text = totalPrice
        }, 200)

        if (AppUtils.stockStatus == 1)
            tv_stock_value.text = getString(R.string.total_stock_value_with_colon)
    }

    private fun initAdapter() {
        rv_cart_list.setItemViewCacheSize(selectedProductList?.size!!)
        rv_cart_list.adapter = CartReturnAdapter(mContext, selectedProductList, object : CartReturnAdapter.OnProductClickListener {
            override fun onDelete(adapterPosition: Int) {
                showDeleteAlert(adapterPosition)
            }

            override fun onEdit(adapterPosition: Int) {
                try {

                    if (!TextUtils.isEmpty((mContext as DashboardActivity).rateList[adapterPosition]) &&
                            !TextUtils.isEmpty((mContext as DashboardActivity).qtyList[adapterPosition])) {
                        val totalPrice = String.format("%.2f", ((mContext as DashboardActivity).rateList[adapterPosition].toDouble()
                                * (mContext as DashboardActivity).qtyList[adapterPosition].toInt()))
                        (mContext as DashboardActivity).totalPrice[adapterPosition] = totalPrice.toDouble()
                    } else
                        (mContext as DashboardActivity).totalPrice[adapterPosition] = 0.00
                } catch (e: Exception) {
                    e.printStackTrace()
                    (mContext as DashboardActivity).totalPrice[adapterPosition] = 0.00
                }

                if (selectedProductList != null)
                    tv_total_order_value.text = selectedProductList?.size.toString()

                Handler().postDelayed(Runnable {
                    var totalAmount = 0.00

                    for (i in (mContext as DashboardActivity).totalPrice.indices) {
                        totalAmount += (mContext as DashboardActivity).totalPrice[i]
                    }
                    val finalTotalAmount = String.format("%.2f", totalAmount)
                    tv_total_order_amount.text = finalTotalAmount
                }, 200)


            }

            override fun onProductClick(brand: ProductListEntity?, adapterPosition: Int, isSelected: Boolean) {
                if (isSelected)
                    selectedItems.add(adapterPosition)
                else {
                    try {
                        selectedItems.remove(adapterPosition)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    private fun showDeleteAlert(adapterPosition: Int) {

        CommonDialog.getInstance("Delete Alert", "Do you really want to delete this product?", getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                selectedProductList?.removeAt(adapterPosition)
                (mContext as DashboardActivity).tv_cart_count.text = selectedProductList?.size.toString()
                (mContext as DashboardActivity).qtyList.removeAt(adapterPosition)
                (mContext as DashboardActivity).rateList.removeAt(adapterPosition)
                (mContext as DashboardActivity).totalPrice.removeAt(adapterPosition)

                initAdapter()

                if (selectedProductList != null)
                    tv_total_order_value.text = selectedProductList?.size.toString()

                Handler().postDelayed(Runnable {
                    var totalAmount = 0.0

                    for (i in (mContext as DashboardActivity).totalPrice.indices) {
                        totalAmount += (mContext as DashboardActivity).totalPrice[i]
                    }

                    tv_total_order_amount.text = totalAmount.toString()
                }, 200)

                AppUtils.isAllSelect = false

                Handler().postDelayed(Runnable {
                    if (selectedProductList == null || selectedProductList?.size!! == 0) {
                        (mContext as DashboardActivity).onBackPressed()
                        (mContext as DashboardActivity).tv_cart_count.visibility = View.GONE
                    }
                }, 500)
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")

    }


    private fun initClickListener() {
        tv_cancel.setOnClickListener(this)
        tv_continue.setOnClickListener(this)
        fab_add.setOnClickListener(this)
        rl_cart_main.setOnClickListener(null)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_cancel -> {
                showCancelAlert()
            }

            R.id.tv_continue -> {
                if (selectedProductList != null && selectedProductList?.size!! > 0) {
                    val list = (mContext as DashboardActivity).totalPrice
                    (mContext as DashboardActivity).onBackPressed()
                    if ((mContext as DashboardActivity).getFragment() is ReturnTypeListFragment)
                        ((mContext as DashboardActivity).getFragment() as ReturnTypeListFragment).saveOrder(tv_total_order_amount.text.toString().trim(),
                                selectedProductList, list)
                } else
                    (mContext as DashboardActivity).showSnackMessage("Please select a product first")
            }

            R.id.fab_add -> {
                (mContext as DashboardActivity).onBackPressed()
            }
        }
    }


    private fun showCancelAlert() {

        CommonDialog.getInstance("Cancel Alert", "Do you really want to cancel this order?", getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                (mContext as DashboardActivity).isShowAlert = false
                (mContext as DashboardActivity).onBackPressed()
                (mContext as DashboardActivity).onBackPressed()
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")

    }


    fun onConfirmClick() {
        AppUtils.hideSoftKeyboard(mContext as Activity)

        if (!Pref.isShowAllProduct && AppUtils.stockStatus == 2) {
            val qtyList = (mContext as DashboardActivity).qtyList
            val rateList = (mContext as DashboardActivity).rateList

            for (i in rateList.indices) {

                if (TextUtils.isEmpty(rateList[i])) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_rate))
                    return
                }

                if (rateList[i].endsWith(".")) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_rate))
                    return
                }

                if (!Pref.isRateNotEditable) {
                    try {
                        if (rateList[i].toDouble() == 0.00) {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_rate))
                            return
                        }
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()

                        if (rateList[i].toInt() == 0) {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_rate))
                            return
                        }
                    }

                } else {
                    if (Pref.isRateOnline) {
                        val list = AppUtils.loadSharedPreferencesProductRateList(mContext)
                        if (list == null || list.size == 0) {
                            if (!TextUtils.isEmpty(rateList[i])) {
                                try {
                                    if (rateList[i].toDouble() == 0.00) {
                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_rate))
                                        return
                                    }
                                } catch (e: NumberFormatException) {
                                    e.printStackTrace()

                                    if (rateList[i].toInt() == 0) {
                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_rate))
                                        return
                                    }
                                }
                            }
                        }
                    } else {
                        val list = AppDatabase.getDBInstance()?.productRateDao()?.getAll() as ArrayList<ProductRateEntity>?
                        if (list == null || list.size == 0) {
                            if (!TextUtils.isEmpty(rateList[i])) {
                                try {
                                    if (rateList[i].toDouble() == 0.00) {
                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_rate))
                                        return
                                    }
                                } catch (e: NumberFormatException) {
                                    e.printStackTrace()

                                    if (rateList[i].toInt() == 0) {
                                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_rate))
                                        return
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (i in qtyList.indices) {

                if (TextUtils.isEmpty(qtyList[i])) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_qty))
                    return
                }

                if (qtyList[i] < "1") {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_qty))
                    return
                }
            }
        } else {
            val qtyList = ArrayList<String>()
            val rateList = ArrayList<String>()
            val totalPriceList = ArrayList<Double>()

            val tempQtyList = ArrayList<String>()
            val tempRateList = ArrayList<String>()
            val tempPriceList = ArrayList<Double>()

            qtyList.addAll((mContext as DashboardActivity).qtyList)
            rateList.addAll((mContext as DashboardActivity).rateList)
            totalPriceList.addAll((mContext as DashboardActivity).totalPrice)
            tempQtyList.addAll((mContext as DashboardActivity).qtyList)
            tempRateList.addAll((mContext as DashboardActivity).rateList)
            tempPriceList.addAll((mContext as DashboardActivity).totalPrice)

            for (i in rateList.indices) {
                if (rateList[i].endsWith(".")) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_rate))
                    return
                } else if (rateList[i].toDouble() == 0.00) {
                    if (AppUtils.stockStatus == 2)
                        tempRateList.remove(rateList[i])
                }
            }

            for (i in qtyList.indices) {
                if (qtyList[i].length > 1 && qtyList[i].startsWith("0")) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_valid_qty))
                    return
                } else if (qtyList[i].toInt() == 0) {
                    tempQtyList.remove(qtyList[i])
                }
            }

            if (AppUtils.stockStatus == 2) {

                if (tempQtyList.size == 0 || tempQtyList.size < tempRateList.size) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_qty))
                    return
                }

                if (tempRateList.size == 0 || tempQtyList.size > tempRateList.size) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_rate))
                    return
                }

                if (tv_total_order_amount.text.toString().trim() == "0.00" || tv_total_order_amount.text.toString().trim() == "0.0" ||
                        tv_total_order_amount.text.toString().trim() == "0") {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_values))
                    return
                }

                for (i in totalPriceList.indices) {
                    if (totalPriceList[i] == 0.0 || totalPriceList[i] == 0.00) {
                        tempPriceList.remove(totalPriceList[i])
                    }
                }

                if (tempPriceList.size == 0 || tempPriceList.size != tempRateList.size || tempPriceList.size != tempQtyList.size) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_values))
                    return
                }
            } else {
                if (tempQtyList.size == 0) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_qty))
                    return
                }

                try {
                    for (i in tempRateList.indices) {
                        Log.e("cart", "index======> $i")
                        Log.e("cart", "rate========> " + tempRateList[i])
                        Log.e("cart", "qty========> " + (mContext as DashboardActivity).qtyList[i])

                        if (tempRateList[i] != "0.00" && (mContext as DashboardActivity).qtyList[i] == "0") {
                            Log.e("cart", "========" + getString(R.string.enter_qty) + "========")
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_qty))
                            return
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.enter_qty))
                    return
                }
            }
        }
        if (selectedProductList != null && selectedProductList?.size!! > 0) {
            /*val list = (mContext as DashboardActivity).totalPrice
            (mContext as DashboardActivity).onBackPressed()
            if ((mContext as DashboardActivity).getFragment() is OrderTypeListFragment)
                ((mContext as DashboardActivity).getFragment() as OrderTypeListFragment).saveOrder(tv_total_order_amount.text.toString().trim(),
                        selectedProductList, list)*/

            if (AppUtils.stockStatus == 2)
                showCheckAlert("Return Confirmation", "Do you want to recheck the return?")


        } else
            (mContext as DashboardActivity).showSnackMessage("Please select a product first")
    }

    private fun showCheckAlert(header: String, title: String) {
        CommonDialog.getInstance(header, title, getString(R.string.no), getString(R.string.yes), false, object : CommonDialogClickListener {
            override fun onLeftClick() {
                if (AppUtils.stockStatus == 2) {
//                    if (!Pref.isShowOrderRemarks && !Pref.isShowOrderSignature)
                        saveData()
//                    else
//                        showRemarksAlert()
                }
                else
                    saveData()
            }

            override fun onRightClick(editableData: String) {
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    private fun saveData() {
        val list = (mContext as DashboardActivity).totalPrice
        (mContext as DashboardActivity).onBackPressed()
        if ((mContext as DashboardActivity).getFragment() is ReturnTypeListFragment) {
            ShopDetailFragment.isOrderEntryPressed=false
            AddShopFragment.isOrderEntryPressed=false
            if (AppUtils.stockStatus == 2) {
                ((mContext as DashboardActivity).getFragment() as ReturnTypeListFragment).saveOrder(tv_total_order_amount.text.toString().trim(),
                        selectedProductList, list)
            }
        }
    }
}