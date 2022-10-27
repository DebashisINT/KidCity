package com.kcteam.features.viewAllOrder.presentation

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
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
import com.kcteam.app.domain.NewOrderColorEntity
import com.kcteam.app.domain.NewOrderGenderEntity
import com.kcteam.features.viewAllOrder.interf.ColorListNewOrderOnClick
import com.kcteam.features.viewAllOrder.interf.GenderListOnClick
import com.kcteam.features.viewAllOrder.presentation.ColorListDialog.Companion.mColorList
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

class ColorListDialog: DialogFragment() {

    private lateinit var header: AppCustomTextView
    private lateinit var close: ImageView
    private lateinit var rv_color: RecyclerView
    private  var adapter:ColorListAdapter? = null
    private lateinit var mContext: Context
    private lateinit  var et_search: AppCustomEditText

    companion object{
        private lateinit var onSelectItem: (NewOrderColorEntity) -> Unit
        private var mColorList: ArrayList<NewOrderColorEntity>? = null

        fun newInstance(gList: ArrayList<NewOrderColorEntity>, function: (NewOrderColorEntity) -> Unit): ColorListDialog {
            val dialogFragment = ColorListDialog()
            ColorListDialog.mColorList = gList
            ColorListDialog.onSelectItem = function
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

        val v = inflater.inflate(R.layout.dialog_new_order_color_list, container, false)

        isCancelable = false

        initView(v)
        initTextChangeListener()
        return v
    }

    private fun initView(v:View){
        header=v.findViewById(R.id.tv_dialog_color_list_header)
        close=v.findViewById(R.id.iv_dialog_color_list_close_icon)
        rv_color=v.findViewById(R.id.rv_dialog_color_list)

        et_search=v!!.findViewById(R.id.et_dialog_color_search)


        rv_color.layoutManager = LinearLayoutManager(mContext)

        header.text="Select Color"

        adapter=ColorListAdapter(mContext,ColorListDialog.mColorList!!,object : ColorListNewOrderOnClick{
            override fun productListOnClick(color: NewOrderColorEntity) {
                dismiss()
                onSelectItem(color)
            }
        })
        rv_color.adapter=adapter

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