package com.kcteam.features.TA

import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.textfield.TextInputLayout
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.*
import android.widget.DatePicker
import android.widget.ListView
import android.widget.PopupWindow
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.domain.TaListDBModelEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.ImagePickerManager
import com.kcteam.features.addshop.presentation.InflateThemeListPopupWindowAdapter
import com.kcteam.features.addshop.presentation.onPopupMenuClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.nearbyshops.presentation.ShopAddressUpdateListener
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.themechangeapp.pickimage.PermissionHelper
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Pratishruti on 02-02-2018.
 */
class AddTADialog : DialogFragment(), View.OnTouchListener, View.OnClickListener {

    private lateinit var shop_name_TV: AppCustomTextView
    private lateinit var order_edt: AppCustomEditText
    private lateinit var from_date_tl: TextInputLayout
    private lateinit var to_date_tl: TextInputLayout
    private lateinit var from_date_edt: AppCustomEditText
    private lateinit var to_date_edt: AppCustomEditText
    private lateinit var total_amount_edt: AppCustomEditText
    private lateinit var description_edt: AppCustomEditText
    private lateinit var status_edt: AppCustomEditText
    private lateinit var supervisor_email_edt: AppCustomEditText
    private lateinit var add_TV: AppCustomTextView
    private lateinit var upload_doc_btn: AppCustomTextView
    private lateinit var uploaded_doc_name_tv: AppCustomTextView
    private lateinit var mContext: Context
    private var mAutoHighlight: Boolean = false
    private lateinit var themeListPopupWindowAdapter: InflateThemeListPopupWindowAdapter
    var myCalendar = Calendar.getInstance(Locale.ENGLISH)
    private var isFromDateClicked = false
    private var isToDateClicked = false
    private val minimumDate = null
    val minDate = Calendar.getInstance(Locale.ENGLISH)
    val c = Calendar.getInstance(Locale.ENGLISH)
    val FromCalender = Calendar.getInstance(Locale.ENGLISH)
    var fromDate = ""
    var toDate = ""
    private lateinit var tw: TextWatcher
    private lateinit var dialog_header_TV: AppCustomTextView
    private var mfilePath: String = ""

    companion object {
        private lateinit var mShopActivityEntityObj: ShopActivityEntity
        private lateinit var addressUpdateClickListener: ShopAddressUpdateListener
        private var mAction: Boolean = false
        fun getInstance(action: Boolean, listener: ShopAddressUpdateListener): AddTADialog {
            val mUpdateShopAddressDialog = AddTADialog()
            addressUpdateClickListener = listener
            mAction = action
            return mUpdateShopAddressDialog
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window!!.setBackgroundDrawableResource(R.drawable.rounded_corner_white_bg)
        val v = inflater?.inflate(R.layout.dialog_add_ta, container, false)
        //addShopData = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopDetail(shopId)
        //Utils.DIALOG_NAME = "AddTADialog"
        initView(v)
        addressUpdateClickListener.getDialogInstance(dialog)
        return v
    }

    private fun initView(v: View?) {
        from_date_edt = v?.findViewById(R.id.from_date_edt)!!
        to_date_edt = v?.findViewById(R.id.to_date_edt)!!
        upload_doc_btn = v?.findViewById(R.id.upload_doc_btn)!!
        uploaded_doc_name_tv = v?.findViewById(R.id.uploaded_doc_name_tv)
        dialog_header_TV = v?.findViewById(R.id.dialog_header_TV)
        from_date_tl = v?.findViewById(R.id.from_date_tl)!!
        to_date_tl = v?.findViewById(R.id.to_date_tl)!!
        total_amount_edt = v?.findViewById(R.id.total_amount_edt)!!
        description_edt = v?.findViewById(R.id.description_edt)!!
        status_edt = v?.findViewById(R.id.status_edt)!!
        supervisor_email_edt = v?.findViewById(R.id.supervisor_email_edt)!!
        add_TV = v?.findViewById(R.id.add_TV)!!


        shop_name_TV = v!!.findViewById(R.id.shop_name_TV)
        add_TV = v!!.findViewById(R.id.add_TV)

        status_edt.setOnTouchListener(this)
        from_date_edt.setOnTouchListener(this)
        to_date_edt.setOnTouchListener(this)
        upload_doc_btn.setOnClickListener(this)


        if (mAction)
            enabledEditAction()
        else
            disableEditAction()

        add_TV.setOnClickListener(View.OnClickListener {
            /*if (order_edt.text.isNullOrBlank()) {
                (mContext as DashboardActivity).showSnackMessage("Please enter some order")
            } else {
                addressUpdateClickListener.onUpdateClick(null)
                dialog.dismiss()
            }*/
            if (from_date_edt.text.isNullOrEmpty()) {
                (mContext as DashboardActivity).showSnackMessage("Please select from date")
            } else if (to_date_edt.text.isNullOrEmpty()) {
                (mContext as DashboardActivity).showSnackMessage("Please select to date")
            } else if (total_amount_edt.text.isNullOrEmpty()) {
                (mContext as DashboardActivity).showSnackMessage("Please provide total amount")
            } else if (description_edt.text.isNullOrEmpty()) {
                (mContext as DashboardActivity).showSnackMessage("Please provide description")
            }/* else if (status_edt.text.isNullOrEmpty()) {
                (mContext as DashboardActivity).showSnackMessage("Please provide status")
            }*/ /*else if (supervisor_email_edt.text.isNullOrEmpty()) {
                (mContext as DashboardActivity).showSnackMessage("Please provide email")
            }*/ else if (mfilePath == "") {
                (mContext as DashboardActivity).showSnackMessage("Please pick an image")
            } else {
                insertNewTaToDB()
                (mContext as DashboardActivity).showSnackMessage("TA Data added successfully")
                dialog?.dismiss()
                addressUpdateClickListener.onAddedDataSuccess()
            }
        })
        //shop_name_TV.text = mShopActivityEntityObj.shop_name
        //order_edt.setText(mShopActivityEntityObj.shop_address)
        total_amount_edt.setSelection(resources.getString(R.string.rupee_symbol_with_space).length)


        tw = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(s: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                if (editable.length > 0 && editable.toString() == resources.getString(R.string.rupee_symbol_with_space)) {
                    total_amount_edt.removeTextChangedListener(tw)
                    total_amount_edt.setText("")
                    total_amount_edt.setText(resources.getString(R.string.rupee_symbol_with_space))
                    total_amount_edt.setSelection(resources.getString(R.string.rupee_symbol_with_space).length)
                    total_amount_edt.addTextChangedListener(tw)
                }

                if (!editable.toString().startsWith(resources.getString(R.string.rupee_symbol_with_space))) {
                    total_amount_edt.removeTextChangedListener(tw)

                    total_amount_edt.setText(resources.getString(R.string.rupee_symbol_with_space));
                    Selection.setSelection(total_amount_edt.text, total_amount_edt.text!!.length)

                    total_amount_edt.addTextChangedListener(tw)

                }
            }
        }

        total_amount_edt.addTextChangedListener(tw)

    }

    private fun insertNewTaToDB() {
        var shopObj = TaListDBModelEntity()
        shopObj.from_date = AppUtils.getCurrentDateFormatInTa(from_date_edt.text.toString())
        shopObj.to_date = AppUtils.getCurrentDateFormatInTa(to_date_edt.text.toString())
        shopObj.amount = total_amount_edt.text.toString().trim().replace(resources.getString(R.string.rupee_symbol_with_space), "")
        shopObj.status = "Approved"
        shopObj.description = description_edt.text.toString().trim()
        shopObj.email = supervisor_email_edt.text.toString().trim()
        shopObj.image_path = mfilePath
        AppDatabase.getDBInstance()!!.taListDao().insert(shopObj)
    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        if (p1?.getAction() == MotionEvent.ACTION_DOWN) {

            when (p0?.id) {
                R.id.from_date_edt -> {
                    //Utils.DATE_PICKER_SELECTED_STATE = "From"
                    /*val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                            this,
                            Calendar.getInstance().get(Calendar.YEAR),
                            Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    )
                    minDate.set(2015, 0, 1, 0, 0);

                    dpd.minDate = minDate
                    if (!to_date_edt.text.toString().trim().equals("")) {
                        dpd.maxDate = FromCalender
                    } else {
                        dpd.maxDate = Calendar.getInstance()
                    }
                    dpd.show((mContext as Activity).fragmentManager, "Datepickerdialog")
                    isFromDateClicked = true

                    dpd.setOnDismissListener(dpd)
                    dpd.setOnCancelListener(dpd)

                    dpd.setOnCancelListener(DialogInterface.OnCancelListener {
                        if (isFromDateClicked) {
                            isFromDateClicked = false
                        } else if (isToDateClicked) {
                            isToDateClicked = false
                        }
                    })*/

                    val cFromDate = Calendar.getInstance(Locale.ENGLISH)
                    var mYear: Int = cFromDate.get(Calendar.YEAR)
                    var mMonth: Int = cFromDate.get(Calendar.MONTH)
                    var mDay: Int = cFromDate.get(Calendar.DAY_OF_MONTH)

                    var _day: String = ""
                    var month: String = ""
                    var mmonthOfYear: Int = 0


                    //minDate.set(2015, 0, 1, 0, 0);

                    val datePickerDialog = DatePickerDialog(mContext,
                            object : DatePickerDialog.OnDateSetListener {

                                override fun onDateSet(p0: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                                    mmonthOfYear = monthOfYear
                                    if (dayOfMonth < 10) {
                                        _day = "0" + dayOfMonth.toString()
                                    } else {
                                        _day = dayOfMonth.toString()
                                    }
                                    //val date = day + AppUtils.getDayNumberSuffix(day.toInt()) + AppUtils.formatMonth((++monthOfYear).toString() + "") + " " + year
                                    val date = _day + "-" + FTStorageUtils.formatMonth((++mmonthOfYear).toString() + "") + "-" + year
                                    if (monthOfYear < 10) {
                                        month = "0" + monthOfYear.toString()
                                    } else {
                                        month = monthOfYear.toString()
                                    }
                                    c.set(year, monthOfYear, dayOfMonth, 0, 0);
                                    from_date_edt.setText(date)
                                    to_date_edt.setText("")
                                }
                            }, mYear, mMonth, mDay)
                    if (!to_date_edt.text.toString().trim().equals("")) {
                        datePickerDialog.getDatePicker().maxDate = FromCalender.timeInMillis
                    } else {
                        datePickerDialog.getDatePicker().maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                    }
                    datePickerDialog.setTitle("");
                    datePickerDialog.show()
                }
                R.id.to_date_edt -> {
                    if (!from_date_edt.text.toString().equals("")) {
                        /*Utils.DATE_PICKER_SELECTED_STATE = "To"
                        val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                                this,
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                        )
                        dpd.minDate = c
                        dpd.maxDate = Calendar.getInstance()
                        dpd.show((mContext as Activity).fragmentManager, "Datepickerdialog")
                        isToDateClicked = true

                        *//*dpd.setOnDismissListener(dpd)
                        dpd.setOnCancelListener(dpd)*//*

                        dpd.setOnCancelListener(DialogInterface.OnCancelListener {
                            if (isFromDateClicked) {
                                isFromDateClicked = false
                            } else if (isToDateClicked) {
                                isToDateClicked = false
                            }
                        })*/


                        var mYear: Int = c.get(Calendar.YEAR)
                        var mMonth: Int = c.get(Calendar.MONTH)
                        var mDay: Int = c.get(Calendar.DAY_OF_MONTH)

                        var _day: String = ""
                        var month: String = ""
                        var mmonthOfYear: Int = 0
                        val datePickerDialog = DatePickerDialog(mContext,
                                object : DatePickerDialog.OnDateSetListener {

                                    override fun onDateSet(p0: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                                        mmonthOfYear = monthOfYear
                                        if (dayOfMonth < 10) {
                                            _day = "0" + dayOfMonth.toString()
                                        } else {
                                            _day = dayOfMonth.toString()
                                        }
                                        //val date = day + AppUtils.getDayNumberSuffix(day.toInt()) + AppUtils.formatMonth((++monthOfYear).toString() + "") + " " + year
                                        val date = _day + "-" + FTStorageUtils.formatMonth((++mmonthOfYear).toString() + "") + "-" + year
                                        if (monthOfYear < 10) {
                                            month = "0" + monthOfYear.toString()
                                        } else {
                                            month = monthOfYear.toString()
                                        }
                                        to_date_edt.setText(date)
                                    }
                                }, mYear, mMonth, mDay)
                        datePickerDialog.getDatePicker().minDate = c.timeInMillis
                        datePickerDialog.getDatePicker().maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                        datePickerDialog.setTitle("");
                        datePickerDialog.show()

                    } else {
                        (mContext as DashboardActivity).showSnackMessage(resources.getString(R.string.select_from_date))
                    }
                }
                R.id.status_edt -> {
                    var mArrayList = ArrayList<String>()
                    mArrayList.add("Approved")
                    mArrayList.add("Pending")
                    mArrayList.add("Rejected")

                    callThemePopUp(status_edt, mArrayList)
                }

            }
            return true;
        }
        return false;
    }

    private fun dispatchGalleryPictureIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        galleryIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        activity!!.startActivityForResult(galleryIntent, ImagePickerManager.REQUEST_GET_GALLERY_PHOTO)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.upload_doc_btn -> {
                showPictureDialog()
            }
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> dispatchGalleryPictureIntent()
                        1 -> launchCamera()
                    }
                })
        pictureDialog.show()
    }

    private fun launchCamera() {
        if (PermissionHelper.checkCameraPermission(mContext as DashboardActivity) && PermissionHelper.checkStoragePermission(mContext as DashboardActivity)) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, (mContext as DashboardActivity).getPhotoFileUri(System.currentTimeMillis().toString() + ".png"))
            (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_CAMERA)
        }
    }

    /*fun initiateDateRangePicker() {
        val now = Calendar.getInstance()
        val dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.isAutoHighlight = mAutoHighlight
        dpd.maxDate = Calendar.getInstance()
        dpd.show((mContext as Activity).fragmentManager, "Datepickerdialog")
    }*/

//    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
//
//        /* view?.maxDate = Calendar.getInstance()
//         var monthOfYear = monthOfYear
//         var monthOfYearEnd = monthOfYearEnd
//         var day = "" + dayOfMonth
//         var dayEnd = "" + dayOfMonthEnd
//         if (dayOfMonth < 10)
//             day = "0" + dayOfMonth
//         if (dayOfMonthEnd < 10)
//             dayEnd = "0" + dayOfMonthEnd
//         var fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear + 1).toString() + "") + "-" + year
//         var endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd + 1).toString() + "") + "-" + yearEnd
//         if (AppUtils.getStrinTODate(endString).before(AppUtils.getStrinTODate(fronString))) {
//             (mContext as DashboardActivity).showSnackMessage("Your end date is before start date.")
//             return
//         }
//         val date = day + AppUtils.getDayNumberSuffix(day.toInt()) + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + " " + year + " To " + dayEnd + AppUtils.getDayNumberSuffix(dayEnd.toInt()) + FTStorageUtils.formatMonth((++monthOfYearEnd).toString() + "") + " " + yearEnd
//         from_date_edt.setText(date)*/
//
//
//        var monthOfYear = monthOfYear
//        var monthOfYearEnd = monthOfYearEnd
//        var day = "" + dayOfMonth
//
//        var month: String = ""
//        var _day: String = ""
//
//        if (dayOfMonth < 10) {
//            _day = "0" + dayOfMonth.toString()
//        } else {
//            _day = dayOfMonth.toString()
//        }
//        //val date = day + AppUtils.getDayNumberSuffix(day.toInt()) + AppUtils.formatMonth((++monthOfYear).toString() + "") + " " + year
//        val date = _day + "-" + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + "-" + year
//        if (monthOfYear < 10) {
//            month = "0" + monthOfYear.toString()
//        } else {
//            month = monthOfYear.toString()
//        }
//
//        if (isFromDateClicked) {
//            isFromDateClicked = false
//            from_date_edt.setText(date)
//            //EnabledFunctionality()
//            fromDate = year.toString() + "-" + month + "-" + _day + " 00:00:00"
//            c.set(year, monthOfYear - 1, dayOfMonth, 0, 0);
//
//        } else if (isToDateClicked) {
//            isToDateClicked = false
//            to_date_edt.setText(date)
//            toDate = year.toString() + "-" + month + "-" + _day + " 23:59:59"
//            FromCalender.set(year, monthOfYear - 1, dayOfMonth, 0, 0)
//        }
//    }


    private var popupWindow: PopupWindow? = null

    private fun callThemePopUp(anchorView: View, arr_themes: ArrayList<String>) {

        popupWindow = PopupWindow(ThemedropDownMenu(R.layout.inflate_items_popup_window, arr_themes, anchorView), anchorView.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.showAsDropDown(anchorView)
        popupWindow!!.update()

    }

    private fun ThemedropDownMenu(layout: Int, arr_roomType: ArrayList<String>, textview: View): View {
        var view: View? = null
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(layout, null, false)
        themeListPopupWindowAdapter = InflateThemeListPopupWindowAdapter(mContext, arr_roomType, object : onPopupMenuClickListener {
            override fun onPopupMenuClick(name: String, position: Int) {
                status_edt.setText(name)
                popupWindow?.dismiss()
            }
        })

        val listView = view.findViewById<ListView>(R.id.lv_roomType)
        listView.adapter = themeListPopupWindowAdapter


        return view
    }

    fun showPickedFile(strFileName: String, filePath: String) {
        mfilePath = filePath
        uploaded_doc_name_tv.text = strFileName
    }

    private fun disableEditAction() {
        dialog_header_TV.setText(resources.getString(R.string.view_ta_text))
        from_date_edt.isEnabled = false
        to_date_edt.isEnabled = false
        upload_doc_btn.visibility = View.GONE
        uploaded_doc_name_tv.visibility = View.GONE

        from_date_tl.isEnabled = false
        to_date_tl.isEnabled = false
        total_amount_edt.isEnabled = false
        description_edt.isEnabled = false
        supervisor_email_edt.isEnabled = false
        add_TV.visibility = View.GONE
    }

    private fun enabledEditAction() {
        dialog_header_TV.setText(resources.getString(R.string.add_ta_text))
        from_date_edt.isEnabled = true
        to_date_edt.isEnabled = true
        upload_doc_btn.visibility = View.VISIBLE
        uploaded_doc_name_tv.visibility = View.VISIBLE

        from_date_tl.isEnabled = true
        to_date_tl.isEnabled = true
        total_amount_edt.isEnabled = true
        description_edt.isEnabled = true
        supervisor_email_edt.isEnabled = true
        add_TV.visibility = View.VISIBLE
    }
}