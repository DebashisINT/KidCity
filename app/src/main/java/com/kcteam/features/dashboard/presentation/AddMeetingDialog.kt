package com.kcteam.features.dashboard.presentation

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.domain.MeetingTypeEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.features.login.api.LoginRepositoryProvider
import com.kcteam.features.login.model.mettingListModel.MeetingListResponseModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 17-01-2020.
 */
class AddMeetingDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var dialogHeader: AppCustomTextView
    private lateinit var dialogContent: AppCustomTextView
    private lateinit var dialogCancel: AppCustomTextView
    private lateinit var dialogOk: AppCustomTextView
    private lateinit var iv_close_icon: ImageView
    private lateinit var til_edt_text: TextInputLayout
    private lateinit var et_text: AppCustomEditText
    private lateinit var tv_meeting_type_dropdown: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var cv_meeting_type_main: CardView

    private var editableData = ""
    private var meetingTypePopupWindow: PopupWindow? = null
    private var meetingTypeList: ArrayList<MeetingTypeEntity>? = null
    private var selectedTypeId = ""

    companion object {

        private lateinit var mHeader: String
        private lateinit var mTitle: String
        private lateinit var mLeftBtn: String
        private lateinit var mRightBtn: String
        private var mIsCancelable: Boolean = true
        private lateinit var mListener: OnButtonClickListener
        private var isShowEditText = false
        private var isShowCross = false

        fun getInstance(header: String, title: String, leftCancel: String, rightOk: String, isCancelable: Boolean, mIsShowEditText: Boolean, mIsShowCross: Boolean, listener: OnButtonClickListener): AddMeetingDialog {
            val cardFragment = AddMeetingDialog()
            mHeader = header
            mTitle = title
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            mIsCancelable = isCancelable
            isShowEditText = mIsShowEditText
            isShowCross = mIsShowCross
            return cardFragment
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater.inflate(R.layout.dialog_fragment_add_meeting, container, false)
        isCancelable = mIsCancelable
        initView(v)

        meetingTypeList = AppDatabase.getDBInstance()!!.addMeetingTypeDao().getAll() as ArrayList<MeetingTypeEntity>

        if (meetingTypeList == null || meetingTypeList!!.size == 0) {
            getMarketTypeListApi()
            /*meetingTypeList = ArrayList()

            val meetingType = MeetingTypeEntity()
            meetingType.typeId = 1
            meetingType.typeText = "GTS- Go to Sub"

            AppDatabase.getDBInstance()!!.addMeetingTypeDao().insertAll(meetingType)

            val meetingType2 = MeetingTypeEntity()
            meetingType2.typeId = 2
            meetingType2.typeText = "GTM- Go to Market"

            AppDatabase.getDBInstance()!!.addMeetingTypeDao().insertAll(meetingType2)

            val meetingType3 = MeetingTypeEntity()
            meetingType3.typeId = 3
            meetingType3.typeText = "Market Leave"

            AppDatabase.getDBInstance()!!.addMeetingTypeDao().insertAll(meetingType3)

            val meetingType4 = MeetingTypeEntity()
            meetingType4.typeId = 4
            meetingType4.typeText = "Other"

            AppDatabase.getDBInstance()!!.addMeetingTypeDao().insertAll(meetingType4)

            meetingTypeList = AppDatabase.getDBInstance()!!.addMeetingTypeDao().getAll() as ArrayList<MeetingTypeEntity>*/
        }

        return v
    }

    private fun initView(v: View) {
        dialogHeader = v.findViewById(R.id.dialog_header_TV)
        dialogContent = v.findViewById(R.id.dialog_content_TV)
        dialogCancel = v.findViewById(R.id.cancel_TV)
        dialogOk = v.findViewById(R.id.ok_TV)
        dialogOk.isSelected = true

        tv_meeting_type_dropdown = v.findViewById(R.id.tv_meeting_type_dropdown)
        progress_wheel = v.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        iv_close_icon = v.findViewById(R.id.iv_close_icon)
        til_edt_text = v.findViewById(R.id.til_edt_text)
        et_text = v.findViewById(R.id.et_text)
        cv_meeting_type_main = v.findViewById(R.id.cv_meeting_type_main)

        til_edt_text.visibility = View.VISIBLE
        dialogContent.visibility = View.GONE
        til_edt_text.hint = getString(R.string.remarks)

        dialogHeader.text = mHeader
        dialogCancel.text = mLeftBtn
        dialogOk.text = mRightBtn

        dialogCancel.setOnClickListener(this)
        dialogOk.setOnClickListener(this)
        iv_close_icon.setOnClickListener(this)
        tv_meeting_type_dropdown.setOnClickListener(this)
    }

    private fun getMarketTypeListApi() {

        if (!AppUtils.isOnline(mContext)) {
            Toaster.msgShort(mContext, getString(R.string.no_internet))
            return
        }

        val repository = LoginRepositoryProvider.provideLoginRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getMeetingList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as MeetingListResponseModel
                            BaseActivity.isApiInitiated = false
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.meeting_type_list != null && response.meeting_type_list!!.size > 0) {

                                    doAsync {

                                        val list = response.meeting_type_list

                                        for (i in list!!.indices) {
                                            val meetingType = MeetingTypeEntity()
                                            meetingType.typeId = list[i].type_id.toInt()
                                            meetingType.typeText = list[i].type_text

                                            AppDatabase.getDBInstance()!!.addMeetingTypeDao().insertAll(meetingType)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            meetingTypeList = AppDatabase.getDBInstance()!!.addMeetingTypeDao().getAll() as ArrayList<MeetingTypeEntity>
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                }
                            } else {
                                progress_wheel.stopSpinning()
                            }

                        }, { error ->
                            error.printStackTrace()
                            BaseActivity.isApiInitiated = false
                            progress_wheel.stopSpinning()
                        })
        )
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.cancel_TV -> {
                if (!mIsCancelable)
                    mListener.onLeftClick()
                dismiss()
            }
            R.id.ok_TV -> {
                if (!TextUtils.isEmpty(tv_meeting_type_dropdown.text.toString().trim())) {
                    dismiss()
                    if (!TextUtils.isEmpty(et_text.text.toString().trim()))
                        editableData = et_text.text.toString().trim()

                    mListener.onRightClick(editableData, selectedTypeId)
                }
                else
                    Toaster.msgShort(mContext, getString(R.string.error_select_meeting_type))
            }
            R.id.iv_close_icon -> {
                dismiss()
            }

            R.id.tv_meeting_type_dropdown -> {
                if (meetingTypePopupWindow != null && meetingTypePopupWindow?.isShowing!!)
                    meetingTypePopupWindow?.dismiss()
                else {

                    if (meetingTypeList == null || meetingTypeList!!.size == 0) {
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                        return
                    }

                    callMeetingTypeDropDownPopUp()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callMeetingTypeDropDownPopUp() {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Inflate the custom layout/view
        val customView = inflater!!.inflate(R.layout.popup_meeting_type, null)

        meetingTypePopupWindow = PopupWindow(customView, resources.getDimensionPixelOffset(R.dimen._220sdp), RelativeLayout.LayoutParams.WRAP_CONTENT)
        val rv_meeting_type_list = customView.findViewById(R.id.rv_meeting_type_list) as RecyclerView
        rv_meeting_type_list.layoutManager = LinearLayoutManager(mContext)

        meetingTypePopupWindow?.elevation = 200f
        meetingTypePopupWindow?.isFocusable = true
        meetingTypePopupWindow?.update()


        rv_meeting_type_list.adapter = MeetingTypeAdapter(mContext, meetingTypeList, object : MeetingTypeAdapter.OnItemClickListener {
            override fun onItemClick(adapterPosition: Int) {
                tv_meeting_type_dropdown.text = meetingTypeList?.get(adapterPosition)?.typeText
                selectedTypeId = meetingTypeList?.get(adapterPosition)?.typeId.toString()
                meetingTypePopupWindow?.dismiss()
            }
        })

        if (meetingTypePopupWindow != null && !meetingTypePopupWindow?.isShowing!!) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                cv_meeting_type_main.post(Runnable {
                    meetingTypePopupWindow?.showAsDropDown(tv_meeting_type_dropdown, resources.getDimensionPixelOffset(R.dimen._1sdp), resources.getDimensionPixelOffset(R.dimen._10sdp), Gravity.BOTTOM)
                })
            } else {
                meetingTypePopupWindow?.showAsDropDown(tv_meeting_type_dropdown, tv_meeting_type_dropdown.width - meetingTypePopupWindow?.width!!, 0)
            }
        }
    }

    interface OnButtonClickListener {
        fun onLeftClick()
        fun onRightClick(editableData: String, selectedTypeId: String)
    }
}