package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.domain.NewOrderProductEntity
import com.kcteam.features.viewAllOrder.interf.ProductListNewOrderOnClick
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

class ProductListNewOrderDialog: DialogFragment() {

    private lateinit var header: AppCustomTextView
    private lateinit var close: ImageView
    private lateinit var rv_product: RecyclerView
    private  var adapter:ProductListNewOrderAdapter? = null
    private lateinit  var et_search: AppCustomEditText
    private lateinit var mContext: Context

    companion object{
        private lateinit var onSelectItem: (NewOrderProductEntity) -> Unit
        private var mProductList: ArrayList<NewOrderProductEntity>? = null

        fun newInstance(pList: ArrayList<NewOrderProductEntity>, function: (NewOrderProductEntity) -> Unit): ProductListNewOrderDialog {
            val dialogFragment = ProductListNewOrderDialog()
            ProductListNewOrderDialog.mProductList = pList
            ProductListNewOrderDialog.onSelectItem = function
            return dialogFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val v = inflater.inflate(R.layout.dialog_product_list_new_order, container, false)

        isCancelable = false

        initView(v)
        initTextChangeListener()
        return v
    }

    private fun initView(v: View){
        header=v!!.findViewById(R.id.tv_dialog_product_list_header)
        close=v!!.findViewById(R.id.iv_dialog_product_list_close_icon)
        rv_product=v!!.findViewById(R.id.rv_dialog_product_list)
        et_search=v!!.findViewById(R.id.et_dialog_product_search)

        header.text="Select Product"
        rv_product.layoutManager = LinearLayoutManager(mContext)

        adapter= ProductListNewOrderAdapter(mContext,ProductListNewOrderDialog.mProductList!! as ArrayList<NewOrderProductEntity>,object : ProductListNewOrderOnClick{
            override fun productListOnClick(product: NewOrderProductEntity) {
                dismiss()
                ProductListNewOrderDialog.onSelectItem(product)
            }
        })
        rv_product.adapter=adapter

        close.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                dismiss()
            }
        }

    }


    private fun initTextChangeListener() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter!!.getFilter().filter(et_search.text.toString().trim())
            }
        })
    }

}