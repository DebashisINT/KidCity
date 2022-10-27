//package com.fieldtrackingsystem.app.utils
//
//import android.app.Activity
//import android.app.Dialog
//import android.content.Context
//import android.content.DialogInterface
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.os.Bundle
//import android.view.Window
//import com.fieldtrackingsystem.R
//import com.pnikosis.materialishprogress.ProgressWheel
//
///**
// * Created by Pratishruti on 24-11-2017.
// */
//class FTSProgressDialog(mCon) : Dialog() {
//
//    internal var mContext: Activity
//
//    fun EvoCardsProgressDialog(context: Context): ??? {
//        super(context)
//    }
//
//    fun EvoCardsProgressDialog(context: Activity): ??? {
//        super(context)
//        mContext = context
//    }
//
//    protected override fun onCreate(savedInstanceState: Bundle) {
//        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
//        getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        setContentView(R.layout.progress_loader)
//        setCancelable(true)
//        val progressWheel = findViewById(R.id.progress_wheel) as ProgressWheel
//        progressWheel.spin()
//    }
//
//    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
//        super.setOnDismissListener(listener)
//        mContext.onBackPressed()
//    }
//
//    override fun onBackPressed() {
//        //        super.onBackPressed();
//        dismiss()
//        mContext.onBackPressed()
//    }
//
//    fun onShow() {
//        if (!isShowing()) {
//            show()
//        }
//    }
//
//    fun onCancel() {
//        if (isShowing()) {
//            dismiss()
//        }
//    }
//}
