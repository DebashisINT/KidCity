package com.kcteam.features.activities.presentation

import android.Manifest
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.activities.api.ActivityRepoProvider
import com.kcteam.features.activities.model.*
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.api.productlistapi.ProductListRepoProvider
import com.kcteam.features.login.model.productlistmodel.ProductListResponseModel
import com.kcteam.features.nearbyshops.api.ShopListRepositoryProvider
import com.kcteam.features.nearbyshops.model.ShopData
import com.kcteam.features.nearbyshops.model.ShopListResponse
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditActivityFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var tv_party_dropdown: AppCustomTextView
    private lateinit var tv_date: AppCustomTextView
    private lateinit var tv_time: AppCustomTextView
    private lateinit var et_name: AppCustomEditText
    private lateinit var tv_activity_dropdown: AppCustomTextView
    private lateinit var tv_type_dropdown: AppCustomTextView
    private lateinit var tv_product_dropdown: AppCustomTextView
    private lateinit var et_subject: AppCustomEditText
    private lateinit var et_details: AppCustomEditText
    private lateinit var et_hrs: AppCustomEditText
    private lateinit var et_mins: AppCustomEditText
    private lateinit var tv_priority_dropdown: AppCustomTextView
    private lateinit var tv_due_date: AppCustomTextView
    private lateinit var tv_due_time: AppCustomTextView
    private lateinit var et_attachment: AppCustomEditText
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var submit_button_TV: AppCustomTextView
    private lateinit var iv_party_dropdown_icon: ImageView
    private lateinit var et_photo: AppCustomEditText
    private lateinit var rl_add_activity_main: RelativeLayout
    private lateinit var iv_activity_dropdown_icon: ImageView
    private lateinit var iv_type_dropdown_icon: ImageView
    private lateinit var iv_product_dropdown_icon: ImageView
    private lateinit var iv_priority_dropdown_icon: ImageView

    private var partyId = ""
    private var activityId = ""
    private var typeId = ""
    private var productId = ""
    private var priorityId = ""
    private var dataPath = ""
    private var imagePath = ""
    private var dateMilis = 0L
    private var isDueDate = false
    private var timeMilis = 0L
    private var dueTimeMilis = 0L
    private var permissionUtils: PermissionUtils? = null
    private var shopList = ArrayList<AddShopDBModelEntity>()
    private var isAttachment = false
    private var selectedDate = ""
    private var selectedDueDate = ""

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    companion object {

        private var activityEntity: ActivityEntity? = null

        fun newInstance(mActivity: Any): EditActivityFragment {
            val fragment = EditActivityFragment()

            if (mActivity is ActivityEntity)
                activityEntity = mActivity

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_add_activity, container, false)

        initView(view)
        initClickListener()
        setData()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            tv_party_dropdown = findViewById(R.id.tv_party_dropdown)
            tv_date = findViewById(R.id.tv_date)
            tv_time = findViewById(R.id.tv_time)
            et_name = findViewById(R.id.et_name)
            tv_activity_dropdown = findViewById(R.id.tv_activity_dropdown)
            tv_type_dropdown = findViewById(R.id.tv_type_dropdown)
            tv_product_dropdown = findViewById(R.id.tv_product_dropdown)
            et_subject = findViewById(R.id.et_subject)
            et_details = findViewById(R.id.et_details)
            et_hrs = findViewById(R.id.et_hrs)
            et_mins = findViewById(R.id.et_mins)
            tv_priority_dropdown = findViewById(R.id.tv_priority_dropdown)
            tv_due_date = findViewById(R.id.tv_due_date)
            tv_due_time = findViewById(R.id.tv_due_time)
            et_attachment = findViewById(R.id.et_attachment)
            progress_wheel = findViewById(R.id.progress_wheel)
            submit_button_TV = findViewById(R.id.submit_button_TV)
            iv_party_dropdown_icon = findViewById(R.id.iv_party_dropdown_icon)
            et_photo = findViewById(R.id.et_photo)
            rl_add_activity_main = findViewById(R.id.rl_add_activity_main)
            iv_activity_dropdown_icon = findViewById(R.id.iv_activity_dropdown_icon)
            iv_type_dropdown_icon = findViewById(R.id.iv_type_dropdown_icon)
            iv_product_dropdown_icon = findViewById(R.id.iv_product_dropdown_icon)
            iv_priority_dropdown_icon = findViewById(R.id.iv_priority_dropdown_icon)
        }

        progress_wheel.stopSpinning()
    }

    private fun initClickListener() {
        tv_party_dropdown.setOnClickListener(this)
        tv_date.setOnClickListener(this)
        tv_time.setOnClickListener(this)
        tv_activity_dropdown.setOnClickListener(this)
        tv_type_dropdown.setOnClickListener(this)
        tv_product_dropdown.setOnClickListener(this)
        tv_priority_dropdown.setOnClickListener(this)
        tv_due_date.setOnClickListener(this)
        tv_due_time.setOnClickListener(this)
        et_attachment.setOnClickListener(this)
        submit_button_TV.setOnClickListener(this)
        et_photo.setOnClickListener(this)
        rl_add_activity_main.setOnClickListener(null)
        iv_party_dropdown_icon.setOnClickListener(this)
        iv_activity_dropdown_icon.setOnClickListener(this)
        iv_type_dropdown_icon.setOnClickListener(this)
        iv_product_dropdown_icon.setOnClickListener(this)
        iv_priority_dropdown_icon.setOnClickListener(this)
    }

    private fun setData() {
        val shop = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(activityEntity?.party_id)
        tv_party_dropdown.isEnabled = false
        tv_party_dropdown.text = shop?.shopName
        partyId = activityEntity?.party_id!!
        iv_party_dropdown_icon.visibility = View.GONE
        et_name.setText(shop?.ownerName)

        selectedDate = activityEntity?.date!!
        tv_date.text = AppUtils.getBillingDateFromCorrectDate(activityEntity?.date!!)
        val calender = Calendar.getInstance(Locale.ENGLISH)
        calender.set(AppUtils.getYearFromReverseFormat(activityEntity?.date!!).toInt(), AppUtils.getMonthValue(AppUtils.getMonthFromReverseFormat(activityEntity?.date!!)).toInt(),
                AppUtils.getDayFromReverseFormat(activityEntity?.date!!).toInt())
        dateMilis = calender.timeInMillis

        tv_time.text = activityEntity?.time
        timeMilis = AppUtils.convertTimeWithMeredianToLong(activityEntity?.time)

        val activityDropDown = AppDatabase.getDBInstance()?.activityDropdownDao()?.getSingleItem(activityEntity?.activity_dropdown_id!!)
        activityDropDown?.let {
            tv_activity_dropdown.text = it.activity_name
            activityId = it.activity_id!!
        }

        val type = AppDatabase.getDBInstance()?.typeDao()?.getSingleType(activityEntity?.type_id!!)
        type?.let {
            tv_type_dropdown.text = it.name
            typeId = it.type_id!!
        }

        if (!TextUtils.isEmpty(activityEntity?.product_id)) {
            val product = AppDatabase.getDBInstance()?.productListDao()?.getSingleProduct(activityEntity?.product_id?.toInt()!!)
            product?.let {
                tv_product_dropdown.text = it.product_name
                productId = it.id.toString()
            }
        }

        val priority = AppDatabase.getDBInstance()?.priorityDao()?.getSingleType(activityEntity?.priority_id!!)
        priority?.let {
            tv_priority_dropdown.text = it.name
            priorityId = it.priority_id!!
        }

        et_subject.setText(activityEntity?.subject)
        et_details.setText(activityEntity?.details)

        val hrs = activityEntity?.duration?.substring(0, activityEntity?.duration?.indexOf(":")!!)
        et_hrs.setText(hrs)

        val mins = activityEntity?.duration?.substring(activityEntity?.duration?.indexOf(":")!! + 1,
                activityEntity?.duration?.length!!)
        et_mins.setText(mins)

        selectedDueDate = activityEntity?.due_date!!
        tv_due_date.text = AppUtils.getBillingDateFromCorrectDate(activityEntity?.due_date!!)
        tv_due_time.text = activityEntity?.due_time
        dueTimeMilis = AppUtils.convertTimeWithMeredianToLong(activityEntity?.due_time)

        et_attachment.setText(activityEntity?.attachments)
        dataPath = activityEntity?.attachments!!

        et_photo.setText(activityEntity?.image)
        imagePath = activityEntity?.image!!
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_party_dropdown, R.id.iv_party_dropdown_icon -> {
                if (shopList.isEmpty())
                    callShopListApi()
                else
                    showPartyDialog()
            }

            R.id.tv_date -> {
                isDueDate = false
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))

                datePicker.show()
            }

            R.id.tv_time -> {
                val cal = Calendar.getInstance(Locale.ENGLISH)

                val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)

                    timeMilis = cal.timeInMillis
                    tv_time.text = SimpleDateFormat("hh:mm a").format(cal.time)
                }

                val timePicker = TimePickerDialog(mContext, R.style.DatePickerTheme, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
                timePicker.show()
            }

            R.id.tv_activity_dropdown, R.id.iv_activity_dropdown_icon -> {
                val list = AppDatabase.getDBInstance()?.activityDropdownDao()?.getAll()
                if (list == null || list.isEmpty())
                    getActivityDropdownList()
                else
                    openActivityList(list)
            }

            R.id.tv_type_dropdown, R.id.iv_type_dropdown_icon -> {
                val list = AppDatabase.getDBInstance()?.typeDao()?.getAll()
                if (list == null || list.isEmpty())
                    getTypeList()
                else
                    openTypeList()
            }

            R.id.tv_product_dropdown, R.id.iv_product_dropdown_icon -> {
                val list = AppDatabase.getDBInstance()?.productListDao()?.getAll()
                if (list == null || list.isEmpty())
                    getProductList()
                else
                    openProductList(list)
            }

            R.id.tv_priority_dropdown, R.id.iv_priority_dropdown_icon -> {
                val list = AppDatabase.getDBInstance()?.priorityDao()?.getAll()
                if (list == null || list.isEmpty())
                    getPriorityList()
                else
                    openPriorityList(list)
            }

            R.id.tv_due_date -> {
                if (TextUtils.isEmpty(tv_date.text.toString().trim()))
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_date_first))
                else {
                    isDueDate = true
                    val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH))

                    datePicker.datePicker.minDate = dateMilis

                    datePicker.show()
                }
            }

            R.id.tv_due_time -> {
                val cal = Calendar.getInstance(Locale.ENGLISH)

                val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)

                    dueTimeMilis = cal.timeInMillis
                    tv_due_time.text = SimpleDateFormat("hh:mm a").format(cal.time)
                }

                val timePicker = TimePickerDialog(mContext, R.style.DatePickerTheme, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false)
                timePicker.show()
            }

            R.id.et_attachment -> {
                isAttachment = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck()
                else {
                    showPictureDialog()
                }
            }

            R.id.submit_button_TV -> {
                checkValidation()
            }

            R.id.et_photo -> {
                isAttachment = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck()
                else {
                    (mContext as DashboardActivity).captureImage()
                }
            }
        }
    }

    private fun callShopListApi() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = ShopListRepositoryProvider.provideShopListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getShopList(Pref.session_token!!, Pref.user_id!!)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val shopList = result as ShopListResponse
                            if (shopList.status == NetworkConstant.SUCCESS) {
                                if (shopList.data!!.shop_list == null || shopList.data!!.shop_list!!.isEmpty()) {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(shopList.message!!)
                                } else {
                                    convertToShopListSetAdapter(shopList.data!!.shop_list!!)
                                }
                            }
                            else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(shopList.message!!)
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun convertToShopListSetAdapter(shop_list: List<ShopData>) {
        val list: MutableList<AddShopDBModelEntity> = ArrayList()
        val shopObj = AddShopDBModelEntity()
        for (i in 0 until shop_list.size) {
            shopObj.shop_id = shop_list[i].shop_id
            shopObj.shopName = shop_list[i].shop_name
            shopObj.shopImageLocalPath = shop_list[i].Shop_Image
            shopObj.shopLat = shop_list[i].shop_lat!!.toDouble()
            shopObj.shopLong = shop_list[i].shop_long!!.toDouble()
            shopObj.duration = "0"
            shopObj.endTimeStamp = "0"
            shopObj.timeStamp = "0"
            shopObj.dateOfBirth = shop_list[i].dob
            shopObj.dateOfAniversary = shop_list[i].date_aniversary
            shopObj.visited = true
            shopObj.visitDate = AppUtils.getCurrentDate()
            shopObj.totalVisitCount = "1"
            shopObj.address = shop_list[i].address
            shopObj.ownerEmailId = shop_list[i].owner_email
            shopObj.ownerContactNumber = shop_list[i].owner_contact_no
            shopObj.pinCode = shop_list[i].pin_code
            shopObj.isUploaded = true
            shopObj.ownerName = shop_list[i].owner_name
            shopObj.user_id = Pref.user_id
            shopObj.orderValue = 0
            shopObj.type = shop_list[i].type
            shopObj.assigned_to_dd_id = shop_list[i].assigned_to_dd_id
            shopObj.assigned_to_pp_id = shop_list[i].assigned_to_pp_id
            shopObj.lastVisitedDate = AppUtils.getCurrentDate()
            shopObj.is_otp_verified = shop_list[i].is_otp_verified
            shopObj.added_date = shop_list[i].added_date

            if (shop_list[i].amount == null || shop_list[i].amount == "0.00")
                shopObj.amount = ""
            else
                shopObj.amount = shop_list[i].amount

            if (shop_list[i].entity_code == null)
                shopObj.entity_code = ""
            else
                shopObj.entity_code = shop_list[i].entity_code

            if (shop_list[i].area_id == null)
                shopObj.area_id = ""
            else
                shopObj.area_id = shop_list[i].area_id

            if (TextUtils.isEmpty(shop_list[i].model_id))
                shopObj.model_id = ""
            else
                shopObj.model_id = shop_list[i].model_id

            if (TextUtils.isEmpty(shop_list[i].primary_app_id))
                shopObj.primary_app_id = ""
            else
                shopObj.primary_app_id = shop_list[i].primary_app_id

            if (TextUtils.isEmpty(shop_list[i].secondary_app_id))
                shopObj.secondary_app_id = ""
            else
                shopObj.secondary_app_id = shop_list[i].secondary_app_id

            if (TextUtils.isEmpty(shop_list[i].lead_id))
                shopObj.lead_id = ""
            else
                shopObj.lead_id = shop_list[i].lead_id

            if (TextUtils.isEmpty(shop_list[i].stage_id))
                shopObj.stage_id = ""
            else
                shopObj.stage_id = shop_list[i].stage_id

            if (TextUtils.isEmpty(shop_list[i].funnel_stage_id))
                shopObj.funnel_stage_id = ""
            else
                shopObj.funnel_stage_id = shop_list[i].funnel_stage_id

            if (TextUtils.isEmpty(shop_list[i].booking_amount))
                shopObj.booking_amount = ""
            else
                shopObj.booking_amount = shop_list[i].booking_amount

            if (TextUtils.isEmpty(shop_list[i].type_id))
                shopObj.type_id = ""
            else
                shopObj.type_id = shop_list[i].type_id

            shopObj.family_member_dob = shop_list[i].family_member_dob
            shopObj.director_name = shop_list[i].director_name
            shopObj.person_name = shop_list[i].key_person_name
            shopObj.person_no = shop_list[i].phone_no
            shopObj.add_dob = shop_list[i].addtional_dob
            shopObj.add_doa = shop_list[i].addtional_doa

            shopObj.doc_degree = shop_list[i].degree
            shopObj.doc_family_dob = shop_list[i].doc_family_member_dob
            shopObj.specialization = shop_list[i].specialization
            shopObj.patient_count = shop_list[i].average_patient_per_day
            shopObj.category = shop_list[i].category
            shopObj.doc_address = shop_list[i].doc_address
            shopObj.doc_pincode = shop_list[i].doc_pincode
            shopObj.chamber_status = shop_list[i].is_chamber_same_headquarter.toInt()
            shopObj.remarks = shop_list[i].is_chamber_same_headquarter_remarks
            shopObj.chemist_name = shop_list[i].chemist_name
            shopObj.chemist_address = shop_list[i].chemist_address
            shopObj.chemist_pincode = shop_list[i].chemist_pincode
            shopObj.assistant_name = shop_list[i].assistant_name
            shopObj.assistant_no = shop_list[i].assistant_contact_no
            shopObj.assistant_dob = shop_list[i].assistant_dob
            shopObj.assistant_doa = shop_list[i].assistant_doa
            shopObj.assistant_family_dob = shop_list[i].assistant_family_dob

            if (TextUtils.isEmpty(shop_list[i].entity_id))
                shopObj.entity_id = ""
            else
                shopObj.entity_id = shop_list[i].entity_id

            if (TextUtils.isEmpty(shop_list[i].party_status_id))
                shopObj.party_status_id = ""
            else
                shopObj.party_status_id = shop_list[i].party_status_id

            if (TextUtils.isEmpty(shop_list[i].retailer_id))
                shopObj.retailer_id = ""
            else
                shopObj.retailer_id = shop_list[i].retailer_id

            if (TextUtils.isEmpty(shop_list[i].dealer_id))
                shopObj.dealer_id = ""
            else
                shopObj.dealer_id = shop_list[i].dealer_id

            if (TextUtils.isEmpty(shop_list[i].beat_id))
                shopObj.beat_id = ""
            else
                shopObj.beat_id = shop_list[i].beat_id

            if (TextUtils.isEmpty(shop_list[i].account_holder))
                shopObj.account_holder = ""
            else
                shopObj.account_holder = shop_list[i].account_holder

            if (TextUtils.isEmpty(shop_list[i].account_no))
                shopObj.account_no = ""
            else
                shopObj.account_no = shop_list[i].account_no

            if (TextUtils.isEmpty(shop_list[i].bank_name))
                shopObj.bank_name = ""
            else
                shopObj.bank_name = shop_list[i].bank_name

            if (TextUtils.isEmpty(shop_list[i].ifsc_code))
                shopObj.ifsc_code = ""
            else
                shopObj.ifsc_code = shop_list[i].ifsc_code

            if (TextUtils.isEmpty(shop_list[i].upi))
                shopObj.upi_id = ""
            else
                shopObj.upi_id = shop_list[i].upi

            if (TextUtils.isEmpty(shop_list[i].assigned_to_shop_id))
                shopObj.assigned_to_shop_id = ""
            else
                shopObj.assigned_to_shop_id = shop_list[i].assigned_to_shop_id

            list.add(shopObj)
            AppDatabase.getDBInstance()!!.addShopEntryDao().insert(shopObj)
        }
        progress_wheel.stopSpinning()

        val list_ = AppDatabase.getDBInstance()?.addShopEntryDao()?.all as ArrayList<AddShopDBModelEntity>?
        list_?.forEach {
            if (it.type != "7" && it.type != "8")
                shopList.add(it)
        }
        showPartyDialog()
    }

    private fun showPartyDialog() {
        PartyListDialog.newInstance(shopList) {
            tv_party_dropdown.text = it.shopName
            partyId = it.shop_id
            et_name.setText(it.ownerName)
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getProductList() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = ProductListRepoProvider.productListProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getProductList(Pref.session_token!!, Pref.user_id!!, "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ProductListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.product_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {


                                        AppDatabase.getDBInstance()?.productListDao()?.insertAll(list!!)


                                   /*     for (i in list.indices) {
                                            val productEntity = ProductListEntity()
                                            productEntity.id = list[i].id?.toInt()!!
                                            productEntity.product_name = list[i].product_name
                                            productEntity.watt = list[i].watt
                                            productEntity.category = list[i].category
                                            productEntity.brand = list[i].brand
                                            productEntity.brand_id = list[i].brand_id
                                            productEntity.watt_id = list[i].watt_id
                                            productEntity.category_id = list[i].category_id
                                            productEntity.date = AppUtils.getCurrentDateForShopActi()
                                            AppDatabase.getDBInstance()?.productListDao()?.insert(productEntity)
                                        }*/

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            openProductList(AppDatabase.getDBInstance()?.productListDao()?.getAll()!!)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                        })
        )
    }

    private fun openProductList(list: List<ProductListEntity>) {
        ProductListDialog.newInstance(list as ArrayList<ProductListEntity>) {
            productId = it.id.toString()
            tv_product_dropdown.text = it.product_name
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    private fun getActivityDropdownList() {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.activityDropdownList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ActivityDropdownListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.activity_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        list.forEach {
                                            val activity = ActivityDropDownEntity()
                                            AppDatabase.getDBInstance()?.activityDropdownDao()?.insertAll(activity.apply {
                                                activity_id = it.id
                                                activity_name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            openActivityList(AppDatabase.getDBInstance()?.activityDropdownDao()?.getAll()!!)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                        })
        )
    }

    private fun openActivityList(list: List<ActivityDropDownEntity>) {
        ActivityListDialog.newInstance(list as ArrayList<ActivityDropDownEntity>) {
            activityId = it.activity_id.toString()
            tv_activity_dropdown.text = it.activity_name
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getTypeList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.typeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as TypeListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.type_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        list.forEach {
                                            val type = TypeEntity()
                                            AppDatabase.getDBInstance()?.typeDao()?.insert(type.apply {
                                                type_id = it.id
                                                name = it.name
                                                activity_id = it.activityId
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            openTypeList()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                        })
        )
    }

    private fun openTypeList() {

        if (TextUtils.isEmpty(activityId)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_activity_first))
            return
        }

        val list = AppDatabase.getDBInstance()?.typeDao()?.getTypeActivityWise(activityId)
        if (list == null || list.isEmpty()) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
            return
        }

        TypeListDialog.newInstance(list as ArrayList<TypeEntity>) {
            typeId = it.type_id.toString()
            tv_type_dropdown.text = it.name
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getPriorityList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = ActivityRepoProvider.activityRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.priorityList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as PriorityListResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.priority_list

                                if (list != null && list.isNotEmpty()) {

                                    doAsync {

                                        list.forEach {
                                            val priority = PriorityListEntity()
                                            AppDatabase.getDBInstance()?.priorityDao()?.insertAll(priority.apply {
                                                priority_id = it.id
                                                name = it.name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            openPriorityList(AppDatabase.getDBInstance()?.priorityDao()?.getAll()!!)
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                        })
        )
    }

    private fun openPriorityList(list: List<PriorityListEntity>) {
        PriorityListDialog.newInstance(list as ArrayList<PriorityListEntity>) {
            priorityId = it.priority_id.toString()
            tv_priority_dropdown.text = it.name
        }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        if (!isDueDate) {
            selectedDate = AppUtils.getFormattedDateForApi(myCalendar.time)
            tv_date.text = AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time))
            dateMilis = myCalendar.timeInMillis
        } else {
            selectedDueDate = AppUtils.getFormattedDateForApi(myCalendar.time)
            tv_due_date.text = AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time))
        }
    }

    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                if (isAttachment)
                    showPictureDialog()
                else
                    (mContext as DashboardActivity).captureImage()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture Image", "Select file from file manager")
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> selectImageInAlbum()
                        1 -> {
                            //(mContext as DashboardActivity).openFileManager()
                            launchCamera()
                        }
                        2 -> {
                            (mContext as DashboardActivity).openFileManager()
                        }
                    }
                })
        pictureDialog.show()
    }

    private fun launchCamera() {
        (mContext as DashboardActivity).captureImage()
    }

    private fun selectImageInAlbum() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        (mContext as DashboardActivity).startActivityForResult(galleryIntent, PermissionHelper.REQUEST_CODE_STORAGE)
    }

    fun setImage(file: File) {
        if (isAttachment) {
            et_attachment.setText(file.name)
            dataPath = file.absolutePath
        }
        else {
            imagePath = file.absolutePath
            et_photo.setText(file.name)
        }
    }

    private fun checkValidation() {
        when {
            TextUtils.isEmpty(partyId) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_party))
            TextUtils.isEmpty(tv_date.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_date))
            TextUtils.isEmpty(tv_time.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_time))
            TextUtils.isEmpty(activityId) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_activity))
            TextUtils.isEmpty(typeId) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_type))
            TextUtils.isEmpty(et_subject.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_subject))
            TextUtils.isEmpty(et_details.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_details))
            TextUtils.isEmpty(et_hrs.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_hrs))
            TextUtils.isEmpty(et_mins.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_mins))
            et_hrs.text.toString().trim().toInt() > 23 -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_hrs))
            et_mins.text.toString().trim().toInt() > 59 -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_enter_valid_mins))
            TextUtils.isEmpty(priorityId) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_priority))
            TextUtils.isEmpty(tv_due_date.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_due_date))
            TextUtils.isEmpty(tv_due_time.text.toString().trim()) -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_due_time))
            dueTimeMilis <= timeMilis -> (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_proper_time))
            else -> {
                AppDatabase.getDBInstance()?.activDao()?.updateActivity(activityEntity?.apply {
                    party_id = partyId
                    date = selectedDate
                    time = tv_time.text.toString().trim()
                    name = et_name.text.toString().trim()
                    activity_dropdown_id = activityId
                    type_id = typeId
                    product_id = productId
                    subject = et_subject.text.toString().trim()
                    details = et_details.text.toString().trim()

                    var hrs = ""
                    var mins = ""

                    hrs = if (et_hrs.text.toString().trim().length == 1)
                        "0" + et_hrs.text.toString().trim()
                    else
                        et_hrs.text.toString().trim()

                    mins = if (et_mins.text.toString().trim().length == 1)
                        "0" + et_mins.text.toString().trim()
                    else
                        et_mins.text.toString().trim()

                    val time = "$hrs:$mins"

                    duration = time
                    priority_id = priorityId
                    due_date = selectedDueDate
                    due_time = tv_due_time.text.toString().trim()
                    attachments = dataPath
                    image = imagePath
                    isUploaded = false
                }!!)

                callAddActivityApi(activityEntity!!)
            }
        }
    }

    private fun callAddActivityApi(activity: ActivityEntity) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage("Activity edited successfully")
            (mContext as DashboardActivity).onBackPressed()
            return
        }

        val activityInput = AddActivityInputModel(Pref.session_token!!, Pref.user_id!!, activity.activity_id!!, activity.party_id!!,
                activity.date!!, activity.time!!, activity.name!!, activity.activity_dropdown_id!!, activity.type_id!!,
                activity.product_id!!, activity.subject!!, activity.details!!, activity.duration!!, activity.priority_id!!,
                activity.due_date!!, activity.due_time!!)


        XLog.d("==============Edit Activity Input Params (Add Activity)====================")
        XLog.d("user_id=======> " + Pref.user_id)
        XLog.d("session_token=======> " + Pref.session_token)
        XLog.d("id=======> " + activity.activity_id)
        XLog.d("party_id=======> " + activity.party_id)
        XLog.d("details=======> " + activity.details)
        XLog.d("date=======> " + activity.date)
        XLog.d("time=======> " + activity.time)
        XLog.d("name=======> " + activity.name)
        XLog.d("time=======> " + activity.time)
        XLog.d("activity_id=======> " + activity.activity_dropdown_id)
        XLog.d("type_id=======> " + activity.type_id)
        XLog.d("product_id=======> " + activity.product_id)
        XLog.d("subject=======> " + activity.subject)
        XLog.d("details=======> " + activity.details)
        XLog.d("duration=======> " + activity.duration)
        XLog.d("priority_id=======> " + activity.priority_id)
        XLog.d("due_date=======> " + activity.due_date)
        XLog.d("due_time=======> " + activity.due_time)
        XLog.d("attachments=======> " + activity.attachments)
        XLog.d("image=======> " + activity.image)
        XLog.d("========================================================================")

        if (TextUtils.isEmpty(activity.attachments) && TextUtils.isEmpty(activity.image)) {
            val repository = ActivityRepoProvider.activityRepoProvider()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.addActivity(activityInput)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.activDao()?.updateIsUploaded(true, activity.activity_id!!)
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage("Activity edited successfully")
                                }

                                (mContext as DashboardActivity).onBackPressed()

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Activity edited successfully")
                                (mContext as DashboardActivity).onBackPressed()
                            })
            )
        }
        else {

            val imgList = ArrayList<ActivityImage>()

            if (!TextUtils.isEmpty(activity.attachments)) {
                imgList.add(ActivityImage(activity.attachments!!, "attachment"))
            }

            if (!TextUtils.isEmpty(activity.image)) {
                imgList.add(ActivityImage(activity.image!!, "image"))
            }

            val repository = ActivityRepoProvider.activityImageRepoProvider()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.addActivityWithAttachment(activityInput, imgList, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                if (response.status == NetworkConstant.SUCCESS) {
                                    AppDatabase.getDBInstance()?.activDao()?.updateIsUploaded(true, activity.activity_id!!)
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                } else {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage("Activity edited successfully")
                                }

                                (mContext as DashboardActivity).onBackPressed()

                            }, { error ->
                                error.printStackTrace()
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage("Activity edited successfully")
                                (mContext as DashboardActivity).onBackPressed()
                            })
            )
        }
    }
}