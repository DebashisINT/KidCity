package com.kcteam.features.viewAllOrder

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.OrderDetailsListEntity
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.CustomTextWatcher
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Pratishruti on 02-02-2018.
 */
class AddOrderDialog : DialogFragment() {
    private lateinit var shop_name_TV: AppCustomTextView
    private lateinit var order_edt: AppCustomEditText
    private lateinit var add_TV: AppCustomTextView
    private lateinit var mContext: Context
    private var isAdd: Boolean = false
    private lateinit var et_order_desc: AppCustomEditText
    private lateinit var textChangeListener: TextWatcher
    private lateinit var collectionTextChangeListener: TextWatcher
    private var shopName = ""
    private lateinit var iv_close_icon: ImageView
    private lateinit var et_collection: AppCustomEditText

    companion object {
        private var mShopActivityEntityObj: ShopActivityEntity? = null
        private var mAddShopDBModelEntity: AddShopDBModelEntity? = null
        private var orderDetailsListEntity: OrderDetailsListEntity? = null

        private lateinit var addressUpdateClickListener: AddOrderClickLisneter

        fun getInstance(mObj: Any?, isAdd: Boolean, shop_name: String, listener: AddOrderClickLisneter): AddOrderDialog {
            val mUpdateShopAddressDialog = AddOrderDialog()

            val bundle = Bundle()
            bundle.putBoolean("isAdd", isAdd)
            bundle.putString("shop_name", shop_name)
            mUpdateShopAddressDialog.arguments = bundle

            if (mObj != null) {
                if (mObj is ShopActivityEntity)
                    this.mShopActivityEntityObj = mObj
                else if (mObj is AddShopDBModelEntity)
                    this.mAddShopDBModelEntity = mObj
                else if (mObj is OrderDetailsListEntity)
                    orderDetailsListEntity = mObj
            }
            addressUpdateClickListener = listener
            return mUpdateShopAddressDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isAdd = arguments?.getBoolean("isAdd")!!
        shopName = arguments?.getString("shop_name").toString()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater?.inflate(R.layout.dialog_add_order, container, false)
        //addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)
        isCancelable = false
        initView(v)
        return v
    }

    private fun initView(v: View?) {
        order_edt = v!!.findViewById(R.id.order_edt)
        shop_name_TV = v.findViewById(R.id.shop_name_TV)
        add_TV = v.findViewById(R.id.add_TV)
        et_order_desc = v.findViewById(R.id.et_order_desc)
        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        et_collection = v.findViewById(R.id.et_collection)

        if (!isAdd) {
            //add_TV.visibility = View.GONE
            order_edt.setText(/*getString(R.string.rupee_symbol_with_space) + " " +*/ orderDetailsListEntity?.amount)
            order_edt.isEnabled = false
            et_order_desc.isEnabled = false
            et_collection.isEnabled = false
            et_order_desc.setText(orderDetailsListEntity?.order_id)
            if (!TextUtils.isEmpty(orderDetailsListEntity?.date))
                et_collection.setText(/*getString(R.string.rupee_symbol_with_space) + " " +*/ AppUtils.convertDateTimeToCommonFormat(orderDetailsListEntity?.date!!))
        }

        add_TV.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(order_edt.text.toString().trim()/*.substring(1)*/)) {
                AppUtils.removeSoftKeyboard(mContext as DashboardActivity, order_edt)
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_order_amount))
            } else if (TextUtils.isEmpty(et_collection.text.toString().trim()/*.substring(1)*/)) {
                AppUtils.removeSoftKeyboard(mContext as DashboardActivity, et_collection)
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_order_collection))
            } else {
                addressUpdateClickListener.onUpdateClick(order_edt.text.toString().trim(), et_order_desc.text.toString().trim(), et_collection.text.toString().trim())
                dialog?.dismiss()
            }
        })

        iv_close_icon.setOnClickListener({
            dismiss()
        })

        textChangeListener = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.isNotEmpty() && p0.toString() == resources.getString(R.string.rupee_symbol_with_space)) {
                    order_edt.removeTextChangedListener(textChangeListener)
                    order_edt.setText("")
                    order_edt.setText(resources.getString(R.string.rupee_symbol_with_space))
                    order_edt.setSelection(resources.getString(R.string.rupee_symbol_with_space).length)
                    order_edt.addTextChangedListener(textChangeListener)
                } else if (!p0.isNotEmpty()) {
                    order_edt.setText(resources.getString(R.string.rupee_symbol_with_space))
                    /*order_edt.setSelection(resources.getString(R.string.rupee_symbol_with_space).length)
                    order_edt.addTextChangedListener(textChangeListener)*/
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }

        order_edt.addTextChangedListener(/*textChangeListener*/CustomTextWatcher(order_edt, 10, 2))


        collectionTextChangeListener = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.isNotEmpty() && p0.toString() == resources.getString(R.string.rupee_symbol_with_space)) {
                    et_collection.removeTextChangedListener(collectionTextChangeListener)
                    et_collection.setText("")
                    order_edt.setText(resources.getString(R.string.rupee_symbol_with_space))
                    et_collection.setSelection(resources.getString(R.string.rupee_symbol_with_space).length)
                    et_collection.addTextChangedListener(collectionTextChangeListener)
                } else if (!p0.isNotEmpty()) {
                    et_collection.setText(resources.getString(R.string.rupee_symbol_with_space))
                    /*order_edt.setSelection(resources.getString(R.string.rupee_symbol_with_space).length)
                    order_edt.addTextChangedListener(textChangeListener)*/
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }

        et_collection.addTextChangedListener(/*collectionTextChangeListener*/CustomTextWatcher(et_collection, 10, 2))



        if (mShopActivityEntityObj != null)
            shop_name_TV.text = mShopActivityEntityObj?.shop_name
        else if (mAddShopDBModelEntity != null)
            shop_name_TV.text = mAddShopDBModelEntity?.shopName
        else
            shop_name_TV.text = shopName


        //order_edt.setText(mShopActivityEntityObj.shop_address)


        /*order_edt.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                order_edt.text = getString(R.string.rupee_symbol_with_space)
                return true
            }
        })*/

    }

    interface AddOrderClickLisneter {
        fun onUpdateClick(amount: String, desc: String, collection: String)
    }
}