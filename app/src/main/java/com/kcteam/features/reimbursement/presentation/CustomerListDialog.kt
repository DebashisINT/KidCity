package com.kcteam.features.reimbursement.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.features.member.model.CustomerDataModel
import com.kcteam.features.member.presentation.CustomerAdapter
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 07-Apr-20.
 */
class CustomerListDialog : DialogFragment() {

    private lateinit var rv_common_dialog_list: RecyclerView
    private lateinit var mContext: Context
    //private var mAssignedList: ArrayList<String>? = null
    private lateinit var dialog_header_TV: AppCustomTextView
    private lateinit var et_search: AppCustomEditText
    private var adapter: CustomerAdapter? = null
    private var customerList: ArrayList<CustomerDataModel>? = null
    private lateinit var iv_close_icon: ImageView

    companion object {

        private lateinit var onClick: (CustomerDataModel) -> Unit

        fun newInstance(customerList: ArrayList<CustomerDataModel>?, mOnClick: (CustomerDataModel) -> Unit): CustomerListDialog {
            val dialogFragment = CustomerListDialog()

            val bundle = Bundle()
            bundle.putSerializable("list", customerList)
            dialogFragment.arguments = bundle
            onClick = mOnClick

            return dialogFragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customerList = arguments?.getSerializable("list") as ArrayList<CustomerDataModel>?
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(true)

        val v = inflater.inflate(R.layout.dialog_list, container, false)

        isCancelable = false

        initView(v)
        initTextChangeListener()
        return v
    }

    private fun initView(v: View) {
        dialog_header_TV = v.findViewById(R.id.dialog_header_TV)
        rv_common_dialog_list = v.findViewById(R.id.rv_common_dialog_list)
        rv_common_dialog_list.layoutManager = LinearLayoutManager(mContext)
        iv_close_icon = v.findViewById(R.id.iv_close_icon)

        /*adapter = ClientAdapter(mContext, customerList, object : AssignedToPPAdapter.OnItemClickListener {
            override fun onItemClick(pp: AssignToPPEntity?) {
                listener?.onItemSelect(pp)
                dismiss()
            }
        })*/

        adapter = CustomerAdapter(mContext, customerList!!, { customer: CustomerDataModel ->
            onClick(customer)
            dismiss()
        })

        rv_common_dialog_list.adapter = adapter
        dialog_header_TV.text = "Customer List"
        et_search = v.findViewById(R.id.et_search)

        iv_close_icon.apply {
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
                //if (!TextUtils.isEmpty(et_grp_search.text.toString().trim()) /*&& et_grp_search.text.toString().trim().length >= 2*/)
                adapter?.filter?.filter(et_search.text.toString().trim())
            }
        })
    }
}