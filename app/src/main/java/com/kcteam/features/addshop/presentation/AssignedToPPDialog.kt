package com.kcteam.features.addshop.presentation


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
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.domain.AssignToPPEntity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView

/**
 * Created by Saikat on 18-Sep-18.
 */

class AssignedToPPDialog : DialogFragment() {

    private lateinit var rv_common_dialog_list: RecyclerView
    private lateinit var mContext: Context
    //private var mAssignedList: ArrayList<String>? = null
    private lateinit var dialog_header_TV: AppCustomTextView
    private lateinit var et_search: AppCustomEditText
    private var adapter: AssignedToPPAdapter? = null
    private var type = ""

    companion object {

        private var listener: OnItemSelectedListener? = null
        private lateinit var mAssignedList: ArrayList<AssignToPPEntity>

        fun newInstance(assignedList: List<AssignToPPEntity>?, type: String, param: OnItemSelectedListener): AssignedToPPDialog {
            val dialogFragment = AssignedToPPDialog()
            /*val bundle = Bundle()
            bundle.putStringArrayList("list", mAssignedList)
            dialogFragment.arguments = bundle*/
            mAssignedList = assignedList as ArrayList<AssignToPPEntity>
            listener = param

            val bundle = Bundle()
            bundle.putString("type", type)
            dialogFragment.arguments = bundle

            return dialogFragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //mAssignedList = arguments?.getStringArrayList("list")
        type = arguments?.getString("type").toString()
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
        dialog_header_TV = v.findViewById(R.id.dialog_header_TV)
        rv_common_dialog_list = v.findViewById(R.id.rv_common_dialog_list)
        rv_common_dialog_list.layoutManager = LinearLayoutManager(mContext)

        if (type != "7")
            dialog_header_TV.text = "Assigned to " + Pref.ppText + " List"
        else
            dialog_header_TV.text = "Assigned to List"

        et_search = v.findViewById(R.id.et_search)

        initAdapter()
    }


    private fun initAdapter() {
        adapter = AssignedToPPAdapter(mContext, mAssignedList, object : AssignedToPPAdapter.OnItemClickListener {
            override fun onItemClick(pp: AssignToPPEntity?) {
                listener?.onItemSelect(pp)
                dismiss()
            }
        })
        rv_common_dialog_list.adapter = adapter
    }

    private fun initTextChangeListener() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!TextUtils.isEmpty(et_search.text.toString().trim()))
                    adapter?.filter?.filter(et_search.text.toString().trim())
                else
                    initAdapter()
            }
        })
    }

    interface OnItemSelectedListener {
        fun onItemSelect(pp: AssignToPPEntity?)
    }
}
