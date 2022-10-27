package com.kcteam.features.micro_learning.presentation

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
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.MeetingTypeEntity
import com.kcteam.app.domain.PartyStatusEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.PartyStatusResponseModel
import com.kcteam.features.addshop.presentation.PartyStatusAdapter
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
class UpdateNoteDialog : DialogFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var dialogHeader: AppCustomTextView
    private lateinit var dialogCancel: AppCustomTextView
    private lateinit var dialogOk: AppCustomTextView
    private lateinit var iv_close_icon: ImageView
    private lateinit var et_note: AppCustomEditText
    private lateinit var progress_wheel: ProgressWheel

    companion object {

        private lateinit var mHeader: String
        private lateinit var mLeftBtn: String
        private lateinit var mRightBtn: String
        private var mIsCancelable: Boolean = true
        private lateinit var mListener: OnButtonClickListener
        private var isShowEditText = false
        private var isShowCross = false
        private var note = ""

        fun getInstance(header: String, leftCancel: String, rightOk: String, isCancelable: Boolean, mIsShowEditText: Boolean,
                        mIsShowCross: Boolean, note: String, listener: OnButtonClickListener): UpdateNoteDialog {
            val cardFragment = UpdateNoteDialog()
            mHeader = header
            mLeftBtn = leftCancel
            mRightBtn = rightOk
            mListener = listener
            mIsCancelable = isCancelable
            isShowEditText = mIsShowEditText
            isShowCross = mIsShowCross
            this.note = note
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
        val v = inflater.inflate(R.layout.dialog_update_note, container, false)
        isCancelable = mIsCancelable

        initView(v)

        return v
    }

    private fun initView(v: View) {
        dialogHeader = v.findViewById(R.id.dialog_header_TV)
        dialogCancel = v.findViewById(R.id.cancel_TV)
        dialogOk = v.findViewById(R.id.ok_TV)
        dialogOk.isSelected = true

        et_note = v.findViewById(R.id.et_note)
        progress_wheel = v.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        iv_close_icon = v.findViewById(R.id.iv_close_icon)

        dialogHeader.text = mHeader
        dialogCancel.text = mLeftBtn
        dialogOk.text = mRightBtn

        et_note.setText(note)

        dialogCancel.setOnClickListener(this)
        dialogOk.setOnClickListener(this)
        iv_close_icon.setOnClickListener(this)
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
                if (!TextUtils.isEmpty(et_note.text.toString().trim())) {
                    if (et_note.text.toString().trim().length < 20)
                        Toaster.msgShort(mContext, "You must enter a proper note with a minimum 20 character length.")
                    else {
                        if (AppUtils.isOnline(mContext)) {
                            dismiss()
                            mListener.onRightClick(et_note.text.toString().trim())
                        } else
                            Toaster.msgShort(mContext, "Your network connection is offine. Make it online to proceed with update.")
                    }
                }
                else
                    Toaster.msgShort(mContext, "Please enter note.")
            }
            R.id.iv_close_icon -> {
                dismiss()
            }
        }
    }

    interface OnButtonClickListener {
        fun onLeftClick()
        fun onRightClick(note: String)
    }
}