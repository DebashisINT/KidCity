package com.kcteam.features.viewPPDDStock

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.Editable
import android.text.Selection
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.CustomTextWatcher
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Saikat on 14-12-2018.
 */
class UpdateOutstandingDialog : DialogFragment(), View.OnClickListener {

    private lateinit var shop_name_TV: AppCustomTextView
    private lateinit var closing_stock_edt: AppCustomEditText
    private lateinit var update_TV: AppCustomTextView
    private lateinit var til_address: LinearLayout
    private lateinit var tv_amount: AppCustomTextView
    private lateinit var opening_stock_edt: AppCustomTextView
    var addShopData = AddShopDBModelEntity()
    private lateinit var stockChangeListener: TextWatcher
    private lateinit var moChangeListener: TextWatcher
    private lateinit var poChangeListener: TextWatcher
    private lateinit var coChangeListener: TextWatcher
    private lateinit var mContext: Context
    private var mMonth: Int = 0;
    private lateinit var til_co: LinearLayout
    private lateinit var til_po: LinearLayout
    private var type = ""
    private lateinit var et_desc: AppCustomEditText
    private lateinit var et_mo: AppCustomEditText
    private lateinit var et_co: AppCustomEditText
    private lateinit var et_po: AppCustomEditText
    private lateinit var iv_close_icon: ImageView
    private lateinit var dialog_header_TV: AppCustomTextView
    private lateinit var til_mo: LinearLayout

    companion object {
        private lateinit var shopId: String
        private lateinit var addressUpdateClickListener: UpdateOutstandingListener
        fun getInstance(shopId: String, type: String, listener: UpdateOutstandingListener): UpdateOutstandingDialog {
            val mUpdateShopAddressDialog = UpdateOutstandingDialog()
            this.shopId = shopId
            val bundle = Bundle()
            bundle.putString("type", type)
            mUpdateShopAddressDialog.arguments = bundle
            addressUpdateClickListener = listener
            return mUpdateShopAddressDialog
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_update_stock, container, false)

        isCancelable = false

        type = arguments?.getString("type")!!
        addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)
        initView(v)
        return v
    }

    private fun initView(v: View?) {
        shop_name_TV = v!!.findViewById(R.id.shop_name_TV)
        til_address = v.findViewById(R.id.til_address)
        til_address.visibility = View.GONE
        tv_amount = v.findViewById(R.id.tv_amount)
        closing_stock_edt = v.findViewById(R.id.closing_stock_edt)
        update_TV = v.findViewById(R.id.update_TV)
        opening_stock_edt = v.findViewById(R.id.opening_stock_edt)
        opening_stock_edt.visibility = View.GONE
        til_co = v.findViewById(R.id.til_co)
        til_po = v.findViewById(R.id.til_po)
        et_desc = v.findViewById(R.id.et_desc)
        et_mo = v.findViewById(R.id.et_mo)
        et_co = v.findViewById(R.id.et_co)
        et_po = v.findViewById(R.id.et_po)
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        iv_close_icon.setOnClickListener(this)
        dialog_header_TV = v.findViewById(R.id.dialog_header_TV)
        dialog_header_TV.text = getString(R.string.update_outstanding)
        til_mo = v.findViewById(R.id.til_mo)
        til_mo.visibility = View.VISIBLE

        shop_name_TV.text = addShopData.shopName

        if (type == "2") {  //PP List
            til_co.visibility = View.VISIBLE
            til_po.visibility = View.GONE
        } else if (type == "4") {  //DD LIST
            til_co.visibility = View.GONE
            til_po.visibility = View.VISIBLE
        }


        //closing_stock_edt.setHint(resources.getString(R.string.closing_stock_text) + " for " + getMonthFromDate(addShopData.lastVisitedDate, false) + "," + getYearFromDate(addShopData.lastVisitedDate, false))
        update_TV.setOnClickListener(View.OnClickListener {

            /*addShopData.address = opening_stock_edt.text.toString()
            addressUpdateClickListener.onUpdateClick(addShopData)*/

            if (TextUtils.isEmpty(et_mo.text.toString().trim()/*.substring(1)*/)) {
                AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_mo)
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_mo))
                //et_mo.error = "Please enter m/o"
                //Toaster.msgShort(mContext,"Please enter m/o")
            } else if (til_co.visibility == View.VISIBLE && TextUtils.isEmpty(et_co.text.toString().trim()/*.substring(1)*/)) {
                AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_co)
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_co))
                //et_co.error = "Please enter c/o"
                //Toaster.msgShort(mContext,"Please enter c/o")
            } else if (til_po.visibility == View.VISIBLE && TextUtils.isEmpty(et_po.text.toString().trim()/*.substring(1)*/)) {
                AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_po)
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_po))
                //et_po.error = "Please enter p/o"
                //Toaster.msgShort(mContext,"Please enter p/o")
            } else if (et_mo.text.toString().trim() < "1") {
                AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_mo)
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_mo))
            } else if (til_co.visibility == View.VISIBLE && et_co.text.toString().trim() < "1") {
                AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_co)
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_co))
            } else if (til_po.visibility == View.VISIBLE && et_po.text.toString().trim() < "1") {
                AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_po)
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_po))
            } else {
//                addressUpdateClickListener.onUpdateClick(getMonthFromDate(addShopData.lastVisitedDate, true),
//                        getYearFromDate(addShopData.lastVisitedDate, true), closing_stock_edt.text.toString().trim()/*.substring(1)*/, getMonthFromDate(addShopData.lastVisitedDate, false),
//                        getYearFromDate(addShopData.lastVisitedDate, false), et_desc.text.toString().trim(), et_mo.text.toString().trim()/*.substring(1)*/,
//                        et_co.text.toString().trim()/*.substring(1)*/, et_po.text.toString().trim()/*.substring(1)*/)

                addressUpdateClickListener.updateOutstanding(et_mo.text.toString().trim(), et_co.text.toString().trim(), et_po.text.toString().trim())

                dialog?.dismiss()
            }
        })
        shop_name_TV.text = addShopData.shopName


        moChangeListener = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.isNotEmpty() && p0.toString() == resources.getString(R.string.rupee_symbol_with_space)) {
                    et_mo.removeTextChangedListener(moChangeListener)
                    et_mo.setText("")
                    et_mo.setText(resources.getString(R.string.rupee_symbol_with_space))
                    et_mo.setSelection(resources.getString(R.string.rupee_symbol_with_space).length)
                    et_mo.addTextChangedListener(moChangeListener)
                }

                if (!p0.toString().startsWith(resources.getString(R.string.rupee_symbol_with_space))) {
                    et_mo.removeTextChangedListener(moChangeListener)

                    et_mo.setText(resources.getString(R.string.rupee_symbol_with_space));
                    Selection.setSelection(et_mo.text, et_mo.text!!.length)

                    et_mo.addTextChangedListener(moChangeListener)

                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        }
        et_mo.addTextChangedListener(/*moChangeListener*/CustomTextWatcher(et_mo, 10, 2))


        poChangeListener = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.isNotEmpty() && p0.toString() == resources.getString(R.string.rupee_symbol_with_space)) {
                    et_po.removeTextChangedListener(poChangeListener)
                    et_po.setText("")
                    et_po.setText(resources.getString(R.string.rupee_symbol_with_space))
                    et_po.setSelection(resources.getString(R.string.rupee_symbol_with_space).length)
                    et_po.addTextChangedListener(poChangeListener)
                }

                if (!p0.toString().startsWith(resources.getString(R.string.rupee_symbol_with_space))) {
                    et_po.removeTextChangedListener(poChangeListener)

                    et_po.setText(resources.getString(R.string.rupee_symbol_with_space));
                    Selection.setSelection(et_po.text, et_po.text!!.length)

                    et_po.addTextChangedListener(poChangeListener)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        }
        et_po.addTextChangedListener(/*poChangeListener*/CustomTextWatcher(et_po, 10, 2))

        coChangeListener = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.isNotEmpty() && p0.toString() == resources.getString(R.string.rupee_symbol_with_space)) {
                    et_co.removeTextChangedListener(coChangeListener)
                    et_co.setText("")
                    et_co.setText(resources.getString(R.string.rupee_symbol_with_space))
                    et_co.setSelection(resources.getString(R.string.rupee_symbol_with_space).length)
                    et_co.addTextChangedListener(coChangeListener)
                }

                if (!p0.toString().startsWith(resources.getString(R.string.rupee_symbol_with_space))) {
                    et_co.removeTextChangedListener(coChangeListener)

                    et_co.setText(resources.getString(R.string.rupee_symbol_with_space));
                    Selection.setSelection(et_co.text, et_co.text!!.length)

                    et_co.addTextChangedListener(coChangeListener)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        }
        et_co.addTextChangedListener(/*coChangeListener*/CustomTextWatcher(et_co, 10, 2))
    }


    fun getMonthFromDate(date: String, action: Boolean): String {
        val input = date
        val format = SimpleDateFormat("dd-MMM-yy")
        val date = format.parse(input)
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.time = date
        if (action) {
            if (mMonth != null && mMonth != 0) {
                calendar.add(Calendar.MONTH, 0);
                return (SimpleDateFormat("MMM").format(calendar.time))
            }
        } else {
            mMonth = SimpleDateFormat("MM").format(calendar.time).toInt()
            return (SimpleDateFormat("MMM").format(calendar.time))
        }
        return ""
    }

    fun getYearFromDate(date: String, action: Boolean): String {
        val input = date
        val format = SimpleDateFormat("dd-MMM-yy")
        val date = format.parse(input)
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        calendar.setTime(date)
        //return calendar.get(Calendar.YEAR).toString()
        if (action) {
            if (mMonth != null && mMonth != 0) {
                calendar.add(Calendar.MONTH, 1);
                return calendar.get(Calendar.YEAR).toString()
            }
        } else {
            return calendar.get(Calendar.YEAR).toString()
        }

        return ""
    }

    override fun onClick(p0: View?) {

        when (p0?.id) {
            R.id.iv_close_icon -> {
                dismiss()
            }
        }
    }

    interface UpdateOutstandingListener {
        fun updateOutstanding(mo: String, co: String, po: String)
    }
}