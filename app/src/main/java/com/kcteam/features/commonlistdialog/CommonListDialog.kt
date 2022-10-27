package com.kcteam.features.commonlistdialog

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import com.kcteam.R


/**
 * Created by Pratishruti on 19-02-2018.
 */
class CommonListDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var adapter:CommonListDialogAdapter
    private lateinit var data_list_RCV: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager

    companion object {
        private lateinit var mListener: CommonListDialogClickListener
        private lateinit var mList:List<String>

        fun getInstance(list:List<String>,listener: CommonListDialogClickListener): CommonListDialog {
            val dialog = CommonListDialog()
            mListener = listener
            mList=list
            return dialog
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCanceledOnTouchOutside(true)
        val v = inflater?.inflate(R.layout.dialog_common_list, container, false)
        initView(v)
        return v
    }

    private fun initView(v: View?) {
        data_list_RCV=v!!.findViewById(R.id.data_list_RCV)
        adapter= CommonListDialogAdapter(mContext,mList, object : CommonListDialogClickListener
        {
            override fun onItemClick(position: Int) {
                dialog?.dismiss()
                mListener.onItemClick(position)
            }

        })
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false) as RecyclerView.LayoutManager
        data_list_RCV.layoutManager = layoutManager
        data_list_RCV.adapter=adapter
    }

    override fun onClick(p0: View?) {

    }

}