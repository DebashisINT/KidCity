package com.kcteam.features.reimbursement.presentation

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import com.kcteam.R
import com.kcteam.app.utils.AppUtils
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.reimbursement.model.reimbursement_shop.ReimbursementShopDataModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 25-10-2019.
 */
class AddLocationDialog : DialogFragment() {

    private lateinit var rv_common_dialog_list: RecyclerView
    private lateinit var mContext: Context
    private lateinit var dialog_header_TV: AppCustomTextView
    private lateinit var et_search: AppCustomEditText
    private var adapter: LocationAdapter? = null
    private lateinit var iv_close_icon: ImageView
    private var locList: ArrayList<ReimbursementShopDataModel>? = null

    companion object {

        private var listener: OnItemSelectedListener? = null

        fun newInstance(locList: ArrayList<ReimbursementShopDataModel>?, param: OnItemSelectedListener): AddLocationDialog {
            val dialogFragment = AddLocationDialog()
            listener = param

            val bundle = Bundle()
            bundle.putSerializable("localist", locList)
            dialogFragment.arguments = bundle

            return dialogFragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            locList = arguments?.getSerializable("localist") as ArrayList<ReimbursementShopDataModel>?
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

        val v = inflater.inflate(R.layout.dialog_list, container, false)

        //isCancelable = true

        initView(v)
        initTextChangeListener()
        return v
    }

    private fun initView(v: View) {

        val stringList = ArrayList<String>()
        stringList.add("Alumnus Software Ltd, Plot A3, GP Block, Sector V, Bidhannagar, Kolkata, West Bengal 700091, India")
        stringList.add("SDF Building, GP Block, Sector V, Bidhannagar, Kolkata, West Bengal 700091, India")
        stringList.add("L2, GP Block, Sector V, Bidhannagar, Kolkata, West Bengal 700091, India")
        stringList.add("81A, Joy Krishna St, Kotrung, Uttarpara, West Bengal 712258, India")
        stringList.add("Unnamed Road, Jamandapara, Natagarh, Kolkata, West Bengal 700113, India")
        stringList.add("Asansol - Chittaranjan Rd, Munshi Bazar, Asansol, West Bengal 713301, India")
        stringList.add("4, Dwarick Jungle Street, Bhadrakali, Uttarpara, West Bengal 712232, India")
        stringList.add("Rabindra Avenue, Foara More, Malda, West Bengal 732101, India")
        stringList.add("Ram Chandra Path, Chunripara, Kanthadhar, Ichapur, Kolkata, West Bengal 743144, India")
        stringList.add("Rabindra Avenue, Malda, West Bengal 732101, India")
        stringList.add("Rabindra Avenue, Foara More, Malda, West Bengal 732101, India")
        stringList.add("Uttarpara College AC Bus Stand, Kotrung, Uttarpara, West Bengal 712258, India")
        stringList.add("Ram Chandra Path, Chunripara, Kanthadhar, Ichapur, Kolkata, West Bengal 743144, India")
        stringList.add("Raha Lane, Munshi Bazar, Asansol, West Bengal 713301, India")
        stringList.add("Unnamed Road, Burnpur, Asansol, West Bengal 713325, India")
        stringList.add("84, Raja Basanta Roy Rd, Lake Market, Kalighat, Kolkata, West Bengal 700029, India")
        stringList.add("260, AH1, Durga Nagar, Birati, Kolkata, West Bengal 700051, India")
        stringList.add("Ram Chandra Path, Chunripara, Kanthadhar, Ichapur, Kolkata, West Bengal 743144, India")
        stringList.add("Raha Lane, Munshi Bazar, Asansol, West Bengal 713301, India")
        stringList.add("Nalagola - Pakuahat - Malda Rd, Nityanandapur, West Bengal 732122, India")

        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        iv_close_icon.visibility = View.VISIBLE
        iv_close_icon.setOnClickListener {
            dismiss()
        }

        dialog_header_TV = v.findViewById(R.id.dialog_header_TV)
        rv_common_dialog_list = v.findViewById(R.id.rv_common_dialog_list)
        rv_common_dialog_list.layoutManager = LinearLayoutManager(mContext)
        rv_common_dialog_list.visibility = View.GONE

        adapter = LocationAdapter(mContext, locList, stringList, object : LocationAdapter.OnItemClickListener {
            override fun showList(isShowList: Boolean) {
                if (isShowList) {

                    if (TextUtils.isEmpty(et_search.text.toString().trim())) {
                        rv_common_dialog_list.visibility = View.GONE
                    } else
                        rv_common_dialog_list.visibility = View.VISIBLE
                } else
                    rv_common_dialog_list.visibility = View.GONE
            }

            override fun onItemClick(item: Int) {
                dismiss()
                AppUtils.hideSoftKeyboardFromDialog((mContext as DashboardActivity))
                listener?.onItemSelect(item)
            }
        })
        rv_common_dialog_list.adapter = adapter

        dialog_header_TV.text = "Add Location"
        et_search = v.findViewById(R.id.et_search)
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

    interface OnItemSelectedListener {
        fun onItemSelect(adapterPosition: Int)
    }
}