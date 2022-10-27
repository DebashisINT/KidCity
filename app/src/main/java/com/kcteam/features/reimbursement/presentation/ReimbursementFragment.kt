package com.kcteam.features.reimbursement.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.AppUtils.Companion.convertFromRightToReverseFormatWithTime
import com.kcteam.app.utils.AppUtils.Companion.getFormattedDateForApi
import com.kcteam.app.utils.AppUtils.Companion.hideSoftKeyboard
import com.kcteam.app.utils.CustomTextWatcher
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.ProcessImageUtils_v1
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.reimbursement.api.ReimbursementConfigRepoProvider
import com.kcteam.features.reimbursement.api.applyapi.ApplyReimbursementRepoProvider
import com.kcteam.features.reimbursement.api.configfetchapi.ReimbursementConfigFetchRepoProvider
import com.kcteam.features.reimbursement.api.reimbursementshopapi.ReimbursementShopRepoProvider
import com.kcteam.features.reimbursement.model.*
import com.kcteam.features.reimbursement.model.reimbursement_shop.ReimbursementShopDataModel
import com.kcteam.features.reimbursement.model.reimbursement_shop.ReimbursementShopResponseModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
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


/**
 * Created by Kinsuk on 17-01-2019.
 */
class ReimbursementFragment : BaseFragment(), DateAdapter.onPetSelectedListener, View.OnClickListener, TabLayout.OnTabSelectedListener, RadioGroup.OnCheckedChangeListener {

    private val visittypeArrayList: ArrayList<ReimbursementConfigVisitTypeDataModel> = ArrayList()
    private val expenseTypesArrayList: ArrayList<ReimbursementConfigExpenseTypeModel> = ArrayList()
    private val modeOfTravelArrayList: ArrayList<ReimbursementConfigModeOfTravelDataModel> = ArrayList()
    private val fuelTypesArrayList: ArrayList<ReimbursementConfigFuelTypeModel> = ArrayList()

    private val imagePathArray: ArrayList<String> = ArrayList()


    private lateinit var mContext: Context
    var rvDateList: RecyclerView? = null
    var dateAdapter: DateAdapter? = null

    val dateList: ArrayList<Date> = arrayListOf()
    private val convenenceType: ArrayList<String> = arrayListOf()
    private val reinbursementInputArrayList: ArrayList<ApplyReimbursementInputModel> = arrayListOf()

    var selectedDate: Date? = null
    var sheetBehavior: BottomSheetBehavior<*>? = null
    var tvPriceRow: TextView? = null
    var tvPriceRowTitle: TextView? = null
    var tvPriceDesc: TextView? = null
    var bottom_sheet: LinearLayout? = null
    var ivBottomSheetExapnd: ImageView? = null
    var llChildLayout: LinearLayout? = null

    var myCalendar = Calendar.getInstance(Locale.ENGLISH)

    private lateinit var maximum_amount_allowance_Per_Km_TV: AppCustomTextView
    private lateinit var maximum_amount_allowance_Km_TV: AppCustomTextView
    private lateinit var km_travelled_TV: AppCustomTextView
    private lateinit var mode_of_travel_TV: AppCustomTextView

    private lateinit var checked_in_date_TV: AppCustomTextView
    private lateinit var checked_in_time_TV: AppCustomTextView
    private lateinit var checked_out_date_TV: AppCustomTextView
    private lateinit var checked_out_time_TV: AppCustomTextView

    private lateinit var expense_type_TV: AppCustomTextView
    private lateinit var mode_of_travel_type_TV: AppCustomTextView

    private lateinit var remark_LL: LinearLayout
    private lateinit var allowance_amount_LL: LinearLayout
    private lateinit var allowance_travelled_LL: LinearLayout
    private lateinit var km_travelled_LL: LinearLayout
    private lateinit var mode_of_travel_RL: RelativeLayout
    private lateinit var tabLayout: TabLayout
    private lateinit var reimbursement_type_RL: RelativeLayout
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var fuel_type_RL: RelativeLayout
    private lateinit var fuel_type_value_TV: AppCustomTextView

    private lateinit var locatioon_to_EDT: AppCustomEditText
    private lateinit var locatioon_from_EDT: AppCustomEditText
    private lateinit var amount_EDT: AppCustomEditText
    private lateinit var radioGroup_food: RadioGroup
    private lateinit var et_remark: AppCustomEditText

    private lateinit var location_from_to_LL: LinearLayout
    private lateinit var hotel_root_LL: LinearLayout
    private lateinit var food_root_LL: LinearLayout
    private lateinit var iv_upload_bills_1: ImageView
    private lateinit var iv_upload_bills_2: ImageView
    private lateinit var iv_upload_bills_3: ImageView

    private lateinit var et_hotel_loc: AppCustomEditText
    private lateinit var et_food_loc: AppCustomEditText
    private lateinit var et_hotel_name: AppCustomEditText

    private lateinit var addNewTA_TV: AppCustomTextView
    private lateinit var travelled_EDT: AppCustomEditText
    private lateinit var clBottomSheet: CoordinatorLayout
    private lateinit var submit_button_TV: AppCustomTextView
    private lateinit var rv_ta_list: RecyclerView
    private lateinit var tv_ta_count: AppCustomTextView
    private lateinit var nsv_apply_reimbursement: NestedScrollView
    private lateinit var tv_total_amount: AppCompatTextView
    private lateinit var food_break_fast: RadioButton
    private lateinit var food_lunch: RadioButton
    private lateinit var food_dinner: RadioButton
    private lateinit var select_date_tv: AppCustomTextView
    private lateinit var til_amount: TextInputLayout
    private lateinit var ll_to_loc: LinearLayout
    private lateinit var ll_from_loc: LinearLayout
    private lateinit var tv_from_loc: AppCustomTextView
    private lateinit var tv_to_loc: AppCustomTextView
    private lateinit var ll_editable_location: LinearLayout
    private lateinit var ll_non_editable_location: LinearLayout
    private lateinit var rl_add_reimbursement_main: RelativeLayout

    var popup: ListPopupWindow? = null
    var conveyancePopupWindow: PopupWindow? = null

    private var visitTypeId = ""
    private var travelId = ""
    private var expenseId = ""
    private var expenseType = ""
    private var permissionUtils: PermissionUtils? = null
    private var mCurrentPhotoPath: String = ""
    private var imagePath_1 = ""
    private var imagePath_2 = ""
    private var imagePath_3 = ""
    private var imageState = -1
    private var foodType = ""
    private var date = ""
    private var fuelId = ""
    private var reimbursmentTaList = ArrayList<ApplyReimbursementInputModel>()
    private var reimbursementBottomAdapter: ReimbursementBottomLayoutAdapter? = null
    private var apiIsRunning = false
    private var rate = ""

    private lateinit var rl_image_2: RelativeLayout
    private lateinit var rl_image_3: RelativeLayout
    private lateinit var rl_image_1: RelativeLayout

    private lateinit var iv_image_cross_icon_1: AppCompatImageView
    private lateinit var iv_image_cross_icon_2: AppCompatImageView
    private lateinit var iv_image_cross_icon_3: AppCompatImageView

    private var locList: ArrayList<ReimbursementShopDataModel>? = null
    private var fromShopId = ""
    private var toShopId = ""
    private var isEditable = false
    private var isAttachmentMandatoryForLocal = false
    private var isAttachmentMandatoryForOutstation = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_reimbursement_new, container, false)

        initView(view)
        initClickListener()
        callReimbursementSettingsApi()

        /*convenenceType.add("loc 1")
        convenenceType.add("loc 2")
        convenenceType.add("loc 3")
        convenenceType.add("loc 4")
        convenenceType.add("loc 5")
        convenenceType.add("loc 6")
        convenenceType.add("loc 7")
        convenenceType.add("loc 8")*/

        return view
    }

    private fun initView(view: View) {
        maximum_amount_allowance_Per_Km_TV = view.findViewById(R.id.maximum_amount_allowance_Per_Km_TV)
        maximum_amount_allowance_Km_TV = view.findViewById(R.id.maximum_amount_allowance_Km_TV)
        km_travelled_TV = view.findViewById(R.id.km_travelled_TV)
        mode_of_travel_TV = view.findViewById(R.id.mode_of_travel_TV)

        checked_in_date_TV = view.findViewById(R.id.checked_in_date_TV)
        checked_in_time_TV = view.findViewById(R.id.checked_in_time_TV)
        checked_out_date_TV = view.findViewById(R.id.checked_out_date_TV)
        checked_out_time_TV = view.findViewById(R.id.checked_out_time_TV)

        allowance_amount_LL = view.findViewById(R.id.allowance_amount_LL)
        allowance_travelled_LL = view.findViewById(R.id.allowance_travelled_LL)
        km_travelled_LL = view.findViewById(R.id.km_travelled_LL)
        reimbursement_type_RL = view.findViewById(R.id.reimbursement_type_RL)
        mode_of_travel_RL = view.findViewById(R.id.mode_of_travel_RL)
        location_from_to_LL = view.findViewById(R.id.location_from_to_LL)
        hotel_root_LL = view.findViewById(R.id.hotel_root_LL)
        food_root_LL = view.findViewById(R.id.food_root_LL)

        remark_LL = view.findViewById(R.id.remark_LL)

        expense_type_TV = view.findViewById(R.id.expense_type_TV)
        mode_of_travel_type_TV = view.findViewById(R.id.mode_of_travel_type_TV)
        fuel_type_RL = view.findViewById(R.id.fuel_type_RL)
        fuel_type_value_TV = view.findViewById(R.id.fuel_type_value_TV)
        iv_upload_bills_1 = view.findViewById(R.id.iv_upload_bills_1)
        iv_upload_bills_2 = view.findViewById(R.id.iv_upload_bills_2)
        iv_upload_bills_3 = view.findViewById(R.id.iv_upload_bills_3)

        locatioon_to_EDT = view.findViewById(R.id.locatioon_to_EDT)
        locatioon_from_EDT = view.findViewById(R.id.locatioon_from_EDT)
        amount_EDT = view.findViewById(R.id.amount_EDT)

        radioGroup_food = view.findViewById(R.id.food_type_radio_button)
        radioGroup_food.setOnCheckedChangeListener(this)

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        tabLayout = view.findViewById(R.id.tabs)
        tabLayout.addOnTabSelectedListener(this)

        et_hotel_loc = view.findViewById(R.id.et_hotel_loc)
        et_food_loc = view.findViewById(R.id.et_food_loc)
        et_remark = view.findViewById(R.id.et_remark)
        et_hotel_name = view.findViewById(R.id.et_hotel_name)

        addNewTA_TV = view.findViewById(R.id.addNewTA_TV)
        travelled_EDT = view.findViewById(R.id.travelled_EDT)
        clBottomSheet = view.findViewById(R.id.clBottomSheet)
        submit_button_TV = view.findViewById(R.id.submit_button_TV)
        tv_ta_count = view.findViewById(R.id.tv_ta_count)
        nsv_apply_reimbursement = view.findViewById(R.id.nsv_apply_reimbursement)
        tv_total_amount = view.findViewById(R.id.tv_total_amount)
        food_break_fast = view.findViewById(R.id.food_break_fast)
        food_lunch = view.findViewById(R.id.food_lunch)
        food_dinner = view.findViewById(R.id.food_dinner)
        select_date_tv = view.findViewById(R.id.select_date_tv)

        rl_image_1 = view.findViewById(R.id.rl_image_1)
        rl_image_2 = view.findViewById(R.id.rl_image_2)
        rl_image_3 = view.findViewById(R.id.rl_image_3)

        iv_image_cross_icon_1 = view.findViewById(R.id.iv_image_cross_icon_1)
        iv_image_cross_icon_2 = view.findViewById(R.id.iv_image_cross_icon_2)
        iv_image_cross_icon_3 = view.findViewById(R.id.iv_image_cross_icon_3)

        til_amount = view.findViewById(R.id.til_amount)
        rvDateList = view.findViewById(R.id.rvDateList)
        rvDateList?.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        dateAdapter = DateAdapter(mContext, true, this)
        rvDateList?.adapter = dateAdapter

        ll_to_loc = view.findViewById(R.id.ll_to_loc)
        tv_to_loc = view.findViewById(R.id.tv_to_loc)
        ll_from_loc = view.findViewById(R.id.ll_from_loc)
        tv_from_loc = view.findViewById(R.id.tv_from_loc)
        ll_editable_location = view.findViewById(R.id.ll_editable_location)
        ll_non_editable_location = view.findViewById(R.id.ll_non_editable_location)

        rl_add_reimbursement_main = view.findViewById(R.id.rl_add_reimbursement_main)

        llChildLayout = view.findViewById(R.id.llChildLayout)
        rv_ta_list = view.findViewById(R.id.rv_ta_list)
        rv_ta_list.layoutManager = LinearLayoutManager(mContext)
        ivBottomSheetExapnd = view.findViewById(R.id.ivBottomSheetExapnd)
        bottom_sheet = view.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        setBottomSheetbehaviour()
        //attachkcteamData()

        checked_in_date_TV.text = getCurrentDate()
        checked_in_time_TV.text = getCurrentTime()
        checked_out_date_TV.text = getCurrentDate()
        checked_out_time_TV.text = getCurrentTime()

        //travelled_EDT.addTextChangedListener(CustomTextWatcher(travelled_EDT, 6, 2))
        amount_EDT.addTextChangedListener(CustomTextWatcher(amount_EDT, 8, 2))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initClickListener() {
        reimbursement_type_RL.setOnClickListener(this)
        mode_of_travel_RL.setOnClickListener(this)
        checked_in_date_TV.setOnClickListener(this)
        checked_in_time_TV.setOnClickListener(this)
        checked_out_date_TV.setOnClickListener(this)
        checked_out_time_TV.setOnClickListener(this)
        fuel_type_value_TV.setOnClickListener(this)
        iv_upload_bills_1.setOnClickListener(this)
        iv_upload_bills_2.setOnClickListener(this)
        iv_upload_bills_3.setOnClickListener(this)
        addNewTA_TV.setOnClickListener(this)
        submit_button_TV.setOnClickListener(this)
        iv_image_cross_icon_1.setOnClickListener(this)
        iv_image_cross_icon_2.setOnClickListener(this)
        iv_image_cross_icon_3.setOnClickListener(this)
        ll_to_loc.setOnClickListener(this)
        ll_from_loc.setOnClickListener(this)

        et_hotel_loc.setOnTouchListener { p0, p1 ->
            if (MotionEvent.ACTION_UP == p1?.action)
                selectHotelFoodLoc()
            true
        }

        et_food_loc.setOnTouchListener { p0, p1 ->
            if (MotionEvent.ACTION_UP == p1?.action)
                selectHotelFoodLoc()
            true
        }
    }

    private fun callReimbursementSettingsApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        var state_id = ""
        if (!TextUtils.isEmpty(Pref.profile_state))
            state_id = Pref.profile_state

        XLog.d("ReimbursementConfigApi Request: \n State id====> " + state_id + ", user id====> " + Pref.user_id!!)

        val repository = ReimbursementConfigRepoProvider.provideReimbursementConfigRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getReimbursementConfig(Pref.user_id!!, state_id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val configResponse = result as ReimbursementConfigResponseModel
                            XLog.d("ReimbursementConfigApiResponse : " + "\n" + "Status=====> " + configResponse.status + ", Message====> " + configResponse.message)

                            progress_wheel.stopSpinning()
                            if (configResponse.status == NetworkConstant.SUCCESS) {

                                if (configResponse.expense_types != null && configResponse.expense_types!!.size > 0) {
                                    expenseTypesArrayList.addAll(configResponse.expense_types!!)
                                    //expense_type_TV.text = expenseTypesArrayList[0].expanse_type
                                }

                                if (configResponse.mode_of_travel != null && configResponse.mode_of_travel!!.size > 0) {
                                    modeOfTravelArrayList.addAll(configResponse.mode_of_travel!!)
                                    //mode_of_travel_type_TV.text = modeOfTravelArrayList[0].travel_type
                                }

                                if (configResponse.fuel_types != null && configResponse.fuel_types!!.size > 0) {
                                    fuelTypesArrayList.addAll(configResponse.fuel_types!!)
                                    //fuel_type_value_TV.text = fuelTypesArrayList[0].fuel_type

                                    if (modeOfTravelArrayList[0].fuel_config!!)
                                        fuel_type_RL.visibility = View.VISIBLE
                                    else
                                        fuel_type_RL.visibility = View.GONE
                                }

                                visitTypeId = "1"

                                isEditable = configResponse.isEditable!!
                                //isEditable = false

                                if (isEditable) {
                                    ll_editable_location.visibility = View.VISIBLE
                                    ll_non_editable_location.visibility = View.GONE
                                    travelled_EDT.isEnabled = true
                                } else {
                                    ll_editable_location.visibility = View.GONE
                                    ll_non_editable_location.visibility = View.VISIBLE
                                    travelled_EDT.isEnabled = false
                                }

                                if (!TextUtils.isEmpty(configResponse.reimbursement_past_days))
                                    setDateData(configResponse.reimbursement_past_days)
                                else
                                    setDateData("7")

                                isAttachmentMandatoryForLocal = configResponse.isAttachmentMandatoryForLocal!!
                                isAttachmentMandatoryForOutstation = configResponse.isAttachmentMandatoryForOutstation!!

                            } else
                                (mContext as DashboardActivity).showSnackMessage(configResponse.message!!)

                            BaseActivity.isApiInitiated = false

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("ReimbursementConfigApiResponse ERROR: " + error.localizedMessage)
                        })
        )
    }

    private fun setBottomSheetbehaviour() {
        sheetBehavior?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
                        ivBottomSheetExapnd?.setImageResource(R.drawable.ic_expand_less_black_24dp)
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> ivBottomSheetExapnd?.setImageResource(R.drawable.ic_expand_more_black_24dp)

                    BottomSheetBehavior.STATE_COLLAPSED -> ivBottomSheetExapnd?.setImageResource(R.drawable.ic_expand_less_black_24dp)

                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }

                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
            }
        })

        ivBottomSheetExapnd?.setOnClickListener { toogleBottomsheet() }
    }

    private fun toogleBottomsheet() {
        if (sheetBehavior?.state === BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
        } else {
            sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun attachkcteamData() {
        val layoutInflater = layoutInflater
        for (i in 0..2) {
            val view = layoutInflater.inflate(R.layout.row_price_layout, llChildLayout, false)

            tvPriceRow = view?.findViewById(R.id.tvPriceRow)
            tvPriceRowTitle = view?.findViewById(R.id.tvPriceRowTitle)
            tvPriceDesc = view?.findViewById(R.id.tvPriceDesc)

            when (i) {
                0 -> {
                    tvPriceRow?.setText(R.string.Rs180)
                    tvPriceRowTitle?.text = "Travel Mode"
                    tvPriceDesc?.text = "Bike"
                }
                1 -> {
                    tvPriceRow?.setText(R.string.Rs120)
                    tvPriceRowTitle?.text = "Food"
                    tvPriceDesc?.text = ""
                    tvPriceDesc?.visibility = View.GONE
                }
                2 -> {
                    tvPriceRow?.setText(R.string.Rs40)
                    tvPriceRowTitle?.text = "Travel Mode"
                    tvPriceDesc?.text = "Train"
                }
            }

            llChildLayout?.addView(view)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDateData(reimbursement_past_days: String?) {

        val calendarToday = Calendar.getInstance(Locale.ENGLISH)
        calendarToday.add(Calendar.DATE, 0)
        val currentToday = calendarToday.time
        dateList.add(currentToday)

        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.add(Calendar.DATE, -1)
        val todayDate = calendar.time
        dateList.add(todayDate)

        //selectedDate = todayDate
        selectedDate = currentToday
        val dateFormat = SimpleDateFormat("dd MMM")
        val formattedDate = dateFormat.format(selectedDate)
        date = getFormattedDateForApi(selectedDate!!)

        val lastPastDay = reimbursement_past_days!!.toInt() - 1

        select_date_tv.text = "Select Date (You can apply for past $reimbursement_past_days days only)"

        for (i in 1..lastPastDay) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)

            val nextDate = calendar.time
            dateList.add(nextDate)
        }

        dateAdapter?.refreshAdapter(dateList)

        if (!isEditable)
            callReimbursementShopApi()


    }


    private fun callReimbursementLocationApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("ReimbursementLocApi Request: \n  date====> $date")

        val repository = ReimbursementShopRepoProvider.provideReimbursementConfigRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getReimbursementLoc(date)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val result = result as ReimbursementShopResponseModel
                            XLog.d("ReimbursementLocApi Response : " + "\n" + "Status=====> " + result.status + ", Message====> " + result.message)

                            progress_wheel.stopSpinning()
                            if (result.status == NetworkConstant.SUCCESS) {

                                if (result.loc_list != null && result.loc_list!!.size > 0)
                                    locList = result.loc_list

                            } else {
                                //(mContext as DashboardActivity).showSnackMessage(result.message!!)
                                if (locList != null)
                                    locList?.clear()
                            }

                            BaseActivity.isApiInitiated = false

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            XLog.d("ReimbursementLocApi Response ERROR: " + error.localizedMessage)
                            if (locList != null)
                                locList?.clear()
                        })
        )
    }


    private fun callReimbursementShopApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.d("ReimbursementShopApi Request: \n  date====> $date")

        val repository = ReimbursementShopRepoProvider.provideReimbursementConfigRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getReimbursementShop(date, false, "", "")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val result = result as ReimbursementShopResponseModel
                            XLog.d("ReimbursementShopApiResponse : " + "\n" + "Status=====> " + result.status + ", Message====> " + result.message)

                            progress_wheel.stopSpinning()
                            if (result.status == NetworkConstant.SUCCESS) {

                                if (result.loc_list != null && result.loc_list!!.size > 0)
                                    locList = result.loc_list

                            } else {
                                //(mContext as DashboardActivity).showSnackMessage(result.message!!)
                                if (locList != null)
                                    locList?.clear()
                            }

                            BaseActivity.isApiInitiated = false

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            XLog.d("ReimbursementShopApiResponse ERROR: " + error.localizedMessage)
                            if (locList != null)
                                locList?.clear()
                        })
        )
    }


    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabSelected(tab: TabLayout.Tab?) {

        expense_type_TV.text = ""
        mode_of_travel_type_TV.text = ""
        fuel_type_value_TV.text = ""
        fuel_type_RL.visibility = View.GONE

        if (!isEditable) {
            tv_from_loc.text = ""
            tv_to_loc.text = ""
            amount_EDT.setText("")
            travelled_EDT.setText("")
            travelled_EDT.clearFocus()
            amount_EDT.clearFocus()
        }

        visitTypeId = if (1 == tab!!.position) {
            "2"
        } else {
            defaultSelectionOfTravelledMode()
            "1"
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        val rb = group?.findViewById<RadioButton>(checkedId)
        if (null != rb && checkedId > -1) {
            foodType = rb.text.toString()
        }

    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.reimbursement_type_RL -> {

                if (conveyancePopupWindow != null && conveyancePopupWindow!!.isShowing)
                    conveyancePopupWindow?.dismiss()
                else
                    callExpenseTypeDropDownPopUp(reimbursement_type_RL, expenseTypesArrayList, expense_type_TV, resources.getDimensionPixelOffset(R.dimen._270sdp))
            }
            R.id.addNewTA_TV -> {
                hideSoftKeyboard(mContext as Activity)
                checkValidation()
                //applyReimbursementApi()
            }
            /* R.id.travel_car_TV -> {

                 allowance_amount_LL.visibility = View.VISIBLE
                 allowance_travelled_LL.visibility = View.VISIBLE
                 km_travelled_LL.visibility = View.VISIBLE
                 km_travelled_TV.visibility = View.VISIBLE

                 maximum_amount_allowance_Per_Km_TV.text = "\u20B9 7 per K.M"
                 maximum_amount_allowance_Km_TV.text = "150 K.M @ \u20B9 7 per K.M"


             }
             R.id.travel_bus_TV -> {

                 allowance_amount_LL.visibility = View.GONE
                 allowance_travelled_LL.visibility = View.GONE
                 km_travelled_LL.visibility = View.GONE
                 km_travelled_TV.visibility = View.GONE
             }
             R.id.travel_taxi_TV -> {

                 allowance_amount_LL.visibility = View.GONE
                 allowance_travelled_LL.visibility = View.GONE
                 km_travelled_LL.visibility = View.GONE
                 km_travelled_TV.visibility = View.GONE
             }
             R.id.travel_flight_TV -> {

                 allowance_amount_LL.visibility = View.GONE
                 allowance_travelled_LL.visibility = View.GONE
                 km_travelled_LL.visibility = View.GONE
                 km_travelled_TV.visibility = View.GONE
             }
             R.id.travel_train_TV -> {

                 allowance_amount_LL.visibility = View.GONE
                 allowance_travelled_LL.visibility = View.GONE
                 km_travelled_LL.visibility = View.GONE
                 km_travelled_TV.visibility = View.GONE
             }
    */
            R.id.mode_of_travel_RL -> {
                if (conveyancePopupWindow != null && conveyancePopupWindow!!.isShowing)
                    conveyancePopupWindow?.dismiss()
                else
                    callExpenseTypeDropDownPopUp(mode_of_travel_RL, modeOfTravelArrayList, mode_of_travel_type_TV, resources.getDimensionPixelOffset(R.dimen._270sdp))
            }
            R.id.checked_in_date_TV -> {
                datePicker(checked_in_date_TV)
            }

            R.id.checked_in_time_TV -> {
                getTime(checked_in_time_TV)
            }

            R.id.checked_out_date_TV -> {
                datePicker(checked_out_date_TV)
            }

            R.id.checked_out_time_TV -> {
                getTime(checked_out_time_TV)
            }

            R.id.fuel_type_value_TV -> {
                if (conveyancePopupWindow != null && conveyancePopupWindow!!.isShowing)
                    conveyancePopupWindow?.dismiss()
                else
                    callExpenseTypeDropDownPopUp(fuel_type_RL, fuelTypesArrayList, fuel_type_value_TV, resources.getDimensionPixelOffset(R.dimen._270sdp))
            }

            R.id.iv_upload_bills_1 -> {
                if (TextUtils.isEmpty(imagePath_1)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheck(1)
                    else {
                        imageState = 1
                        //(mContext as DashboardActivity).captureImage()
                        showPictureDialog()
                    }
                } else
                    FullImageDialog.getInstance(imagePath_1).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            R.id.iv_upload_bills_2 -> {
                if (TextUtils.isEmpty(imagePath_2)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheck(2)
                    else {
                        imageState = 2
                        //(mContext as DashboardActivity).captureImage()
                        showPictureDialog()
                    }
                } else
                    FullImageDialog.getInstance(imagePath_2).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            R.id.iv_upload_bills_3 -> {
                if (TextUtils.isEmpty(imagePath_3)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheck(3)
                    else {
                        imageState = 3
                        //(mContext as DashboardActivity).captureImage()
                        showPictureDialog()
                    }
                } else
                    FullImageDialog.getInstance(imagePath_3).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            R.id.submit_button_TV -> {
                //applyReimbursementApi()
                //(mContext as DashboardActivity).onBackPressed()
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                checkValidation()
            }

            R.id.iv_image_cross_icon_1 -> {
                imagePath_1 = ""
                iv_image_cross_icon_1.visibility = View.GONE
                iv_upload_bills_1.setImageResource(R.drawable.ic_upload_icon)
            }

            R.id.iv_image_cross_icon_2 -> {
                imagePath_2 = ""
                iv_image_cross_icon_2.visibility = View.GONE
                iv_upload_bills_2.setImageResource(R.drawable.ic_upload_icon)
            }

            R.id.iv_image_cross_icon_3 -> {
                imagePath_3 = ""
                iv_image_cross_icon_3.visibility = View.GONE
                iv_upload_bills_3.setImageResource(R.drawable.ic_upload_icon)

            }
            R.id.ll_from_loc -> {
                selectLocation(ll_from_loc, tv_from_loc)
            }

            R.id.ll_to_loc -> {
                selectLocation(ll_to_loc, tv_to_loc)
            }

            R.id.et_hotel_loc -> {
                selectHotelFoodLoc()
            }

            R.id.et_food_loc -> {
                selectHotelFoodLoc()
            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun selectHotelFoodLoc() {
        if (locList == null || locList?.size == 0) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            return
        }

        AddLocationDialog.newInstance(locList, object : AddLocationDialog.OnItemSelectedListener {
            override fun onItemSelect(adapterPosition: Int) {
                //tv_loc.text = loc.loc_name

                val genericObj = locList?.get(adapterPosition)

                if (hotel_root_LL.visibility == View.VISIBLE)
                    et_hotel_loc.setText(genericObj?.loc_name)
                else if (food_root_LL.visibility == View.VISIBLE)
                    et_food_loc.setText(genericObj?.loc_name)
            }

        }).show(fragmentManager!!, "")
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun selectLocation(ll_loc: LinearLayout, tv_loc: AppCustomTextView) {
        if (TextUtils.isEmpty(expense_type_TV.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_expense_type))
        else if (mode_of_travel_RL.visibility == View.VISIBLE && TextUtils.isEmpty(mode_of_travel_type_TV.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_travel_type))
        else if (fuel_type_RL.visibility == View.VISIBLE && TextUtils.isEmpty(fuel_type_value_TV.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_fuel_type))
        else {
            if (conveyancePopupWindow != null && conveyancePopupWindow!!.isShowing)
                conveyancePopupWindow?.dismiss()
            else {
                /*if (locList != null && locList!!.size > 0)
                    callExpenseTypeDropDownPopUp(ll_loc, locList!!, tv_loc, resources.getDimensionPixelOffset(R.dimen._270sdp))
                else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))*/

                /*val stringList = ArrayList<String>()
                stringList.add("Alumnus Software Ltd, Plot A3, GP Block, Sector V, Bidhannagar, Kolkata, West Bengal 700091, India")
                stringList.add("SDF Building, GP Block, Sector V, Bidhannagar, Kolkata, West Bengal 700091, India")
                stringList.add("L2, GP Block, Sector V, Bidhannagar, Kolkata, West Bengal 700091, India")

                callLocationDropDownPopUp(ll_loc, stringList, tv_loc, resources.getDimensionPixelOffset(R.dimen._270sdp))*/

                if (locList == null || locList?.size == 0) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                    return
                }

                AddLocationDialog.newInstance(locList, object : AddLocationDialog.OnItemSelectedListener {
                    override fun onItemSelect(adapterPosition: Int) {
                        //tv_loc.text = loc.loc_name

                        val genericObj = locList?.get(adapterPosition)

                        if (tv_loc.id == R.id.tv_to_loc) {
                            if (!TextUtils.isEmpty(tv_from_loc.text.toString().trim())) {

                                var fromIndex = -1

                                for (i in locList!!.indices) {
                                    if (tv_from_loc.text.toString().trim().equals(locList!![i].loc_name, ignoreCase = true)) {
                                        fromIndex = i
                                        break
                                    }
                                }

                                if (fromIndex > adapterPosition)
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.from_loc_smaller_than_to_loc))
                                else if (fromIndex == adapterPosition)
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_location_validation))
                                else if (!tv_from_loc.text.toString().trim().equals(genericObj?.loc_name, ignoreCase = true)) {
                                    tv_loc.text = genericObj?.loc_name
                                    toShopId = genericObj?.loc_id!!
                                }
                            } else {
                                tv_loc.text = genericObj?.loc_name
                                toShopId = genericObj?.loc_id!!
                            }
                        } else if (tv_loc.id == R.id.tv_from_loc) {

                            if (!TextUtils.isEmpty(tv_to_loc.text.toString().trim())) {

                                var toIndex = -1

                                for (i in locList!!.indices) {
                                    if (tv_to_loc.text.toString().trim().equals(locList!![i].loc_name, ignoreCase = true)) {
                                        toIndex = i
                                        break
                                    }
                                }

                                if (toIndex < adapterPosition)
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.from_loc_smaller_than_to_loc))
                                else if (toIndex == adapterPosition)
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_location_validation))
                                else if (!tv_to_loc.text.toString().trim().equals(genericObj?.loc_name, ignoreCase = true)) {
                                    tv_loc.text = genericObj?.loc_name
                                    fromShopId = genericObj?.loc_id!!
                                }

                            } else {
                                tv_loc.text = genericObj?.loc_name
                                fromShopId = genericObj?.loc_id!!
                            }
                        }

                        if (!TextUtils.isEmpty(tv_to_loc.text.toString().trim()) && !TextUtils.isEmpty(tv_from_loc.text.toString().trim())) {

                            var fromIndex = -1
                            var toIndex = -1

                            for (i in locList!!.indices) {
                                if (tv_to_loc.text.toString().trim().equals(locList!![i].loc_name, ignoreCase = true)) {
                                    toIndex = i
                                    break
                                }
                            }

                            for (i in locList!!.indices) {
                                if (tv_from_loc.text.toString().trim().equals(locList!![i].loc_name, ignoreCase = true)) {
                                    fromIndex = i
                                    break
                                }
                            }

                            var distance = 0f

                            for (i in (fromIndex + 1)..toIndex) {
                                distance += locList?.get(i)?.distance!!.toFloat()
                            }

                            travelled_EDT.setText(String.format("%.2f", distance))

                            if (!amount_EDT.isEnabled && !TextUtils.isEmpty(rate))
                                amount_EDT.setText(String.format("%.2f", (travelled_EDT.text.toString().trim().toFloat() * rate.toFloat())))
                        }
                    }

                }).show(fragmentManager!!, "")
            }
        }
    }

    fun setImage(filePath: String) {

        /*if (iv_upload_bills_2.visibility == View.GONE) {
            iv_upload_bills_2.visibility = View.VISIBLE
        } else {
            iv_upload_bills_3.visibility = View.VISIBLE
        }*/

        val file = File(filePath)
        var newFile: File? = null

        progress_wheel.spin()
        doAsync {

            val processImage = ProcessImageUtils_v1(mContext, file, 50)
            newFile = processImage.ProcessImage()

            uiThread {
                if (newFile != null) {
                    XLog.e("=========Image from new technique==========")
                    reimbursementPic(newFile!!.length(), newFile?.absolutePath!!)
                } else {
                    // Image compression
                    val fileSize = AppUtils.getCompressImage(filePath)
                    reimbursementPic(fileSize, filePath)
                }
            }
        }
    }

    private fun reimbursementPic(fileSize: Long, filePath: String) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Add reimbursement", "image file size after compression=====> $fileSizeInKB KB")

        progress_wheel.stopSpinning()

        when (imageState) {
            1 -> {
                rl_image_2.visibility = View.VISIBLE
                imagePath_1 = filePath
                Glide.with(mContext)
                        .load(filePath)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                        .into(iv_upload_bills_1)
                iv_image_cross_icon_1.visibility = View.VISIBLE
            }
            2 -> {
                rl_image_3.visibility = View.VISIBLE
                imagePath_2 = filePath
                Glide.with(mContext)
                        .load(filePath)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                        .into(iv_upload_bills_2)
                iv_image_cross_icon_2.visibility = View.VISIBLE
            }
            3 -> {
                imagePath_3 = filePath
                Glide.with(mContext)
                        .load(filePath)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                        .into(iv_upload_bills_3)
                iv_image_cross_icon_3.visibility = View.VISIBLE
            }
        }
    }

    private fun initPermissionCheck(state: Int) {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                imageState = state
                //(mContext as DashboardActivity).captureImage()
                showPictureDialog()
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
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> selectImageInAlbum()
                        1 -> launchCamera()
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

    override fun onDateItemClick(pos: Int) {
        selectedDate = dateList[pos]
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)

        date = getFormattedDateForApi(selectedDate!!)


        if (!isEditable) {
            tv_from_loc.text = ""
            tv_to_loc.text = ""
            amount_EDT.setText("")
            travelled_EDT.setText("")

            callReimbursementShopApi()
        }

        if (expenseId == "2" || expenseId == "3")
            callReimbursementLocationApi()
    }

    private fun setModeOfTravelSelection(txtView: AppCustomTextView, selectedBackgroundColor: Int, selectedIcon: Int, color: Int) {
        txtView.setBackgroundResource(selectedBackgroundColor)
        txtView.setCompoundDrawablesWithIntrinsicBounds(0, selectedIcon, 0, 0);
        txtView.setTextColor(ContextCompat.getColor(mContext, color))
    }

    private fun defaultSelectionOfTravelledMode() {

        km_travelled_LL.visibility = View.VISIBLE
        //km_travelled_TV.visibility = View.VISIBLE

        travelled_EDT.clearFocus()

        hotel_root_LL.visibility = View.GONE
        location_from_to_LL.visibility = View.VISIBLE
        food_root_LL.visibility = View.GONE

        mode_of_travel_TV.visibility = View.VISIBLE
        mode_of_travel_RL.visibility = View.VISIBLE

        // remark_LL.visibility = View.GONE

        radioGroup_food.clearCheck()

    }

    private fun setVisibilityForHotel() {

        km_travelled_LL.visibility = View.GONE
        km_travelled_TV.visibility = View.GONE
        mode_of_travel_TV.visibility = View.GONE
        mode_of_travel_RL.visibility = View.GONE

        hotel_root_LL.visibility = View.VISIBLE
        location_from_to_LL.visibility = View.GONE
        food_root_LL.visibility = View.GONE

        // remark_LL.visibility = View.GONE
        fuel_type_RL.visibility = View.GONE
        fuel_type_value_TV.text = ""
        radioGroup_food.clearCheck()
    }

    private fun setVisibilityForFood() {

        km_travelled_LL.visibility = View.GONE
        km_travelled_TV.visibility = View.GONE
        mode_of_travel_TV.visibility = View.GONE
        mode_of_travel_RL.visibility = View.GONE
        hotel_root_LL.visibility = View.GONE

        location_from_to_LL.visibility = View.GONE
        food_root_LL.visibility = View.VISIBLE
        fuel_type_RL.visibility = View.GONE
        fuel_type_value_TV.text = ""
        // remark_LL.visibility = View.GONE

        radioGroup_food.clearCheck()
    }

    private fun setVisibilityForRemark() {
        allowance_amount_LL.visibility = View.GONE
        allowance_travelled_LL.visibility = View.GONE
        km_travelled_LL.visibility = View.GONE
        km_travelled_TV.visibility = View.GONE
        mode_of_travel_TV.visibility = View.GONE
        mode_of_travel_RL.visibility = View.GONE
        hotel_root_LL.visibility = View.GONE
        fuel_type_RL.visibility = View.GONE
        fuel_type_value_TV.text = ""
        location_from_to_LL.visibility = View.GONE
        food_root_LL.visibility = View.GONE

        remark_LL.visibility = View.VISIBLE

        radioGroup_food.clearCheck()
    }


    private fun setVisibilityForTravel() {
        allowance_amount_LL.visibility = View.VISIBLE
        allowance_travelled_LL.visibility = View.VISIBLE
        km_travelled_LL.visibility = View.VISIBLE
        //km_travelled_TV.visibility = View.VISIBLE
        mode_of_travel_TV.visibility = View.VISIBLE
        mode_of_travel_RL.visibility = View.VISIBLE
        hotel_root_LL.visibility = View.GONE

        location_from_to_LL.visibility = View.VISIBLE
        food_root_LL.visibility = View.GONE
        defaultSelectionOfTravelledMode()
    }


    private fun datePicker(textView: AppCustomTextView) {

        val date = android.app.DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            textView.text = SimpleDateFormat("dd-MM-yyyy").format(myCalendar.time)
        }

        var datepickerDialog = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH))
        datepickerDialog.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
        datepickerDialog.show()

    }

    private fun getTime(textView: AppCustomTextView) {
        val cal = Calendar.getInstance(Locale.ENGLISH)

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)

            textView.text = SimpleDateFormat("HH:mm a").format(cal.time)
        }

        TimePickerDialog(mContext, R.style.DatePickerTheme, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()

    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(Date())

    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("hh:mm a")
        return sdf.format(Date())

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callLocationDropDownPopUp(anchorView: View, arr_themes: ArrayList<String>, textView: AppCustomTextView, width: Int) {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Inflate the custom layout/view
        val customView = inflater!!.inflate(R.layout.dialog_months, null)

        conveyancePopupWindow = PopupWindow(customView, width, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val rv_member_no_list = customView.findViewById(R.id.rv_months) as RecyclerView
        rv_member_no_list.layoutManager = LinearLayoutManager(mContext)

        conveyancePopupWindow?.elevation = 200f
        conveyancePopupWindow?.isFocusable = true
        conveyancePopupWindow?.update()

        val et_search = customView.findViewById(R.id.et_search) as AppCustomEditText
        et_search.visibility = View.VISIBLE
        rv_member_no_list.visibility = View.GONE

        /*val adapter = LocationAdapter(mContext, arr_themes, object : LocationAdapter.OnItemClickListener {
            override fun showList(isShowList: Boolean) {
                if (isShowList) {

                    if (TextUtils.isEmpty(et_search.text.toString().trim())) {
                        rv_member_no_list.visibility = View.GONE
                    } else
                        rv_member_no_list.visibility = View.VISIBLE
                } else
                    rv_member_no_list.visibility = View.GONE
            }

            override fun onItemClick(adapterPosition: ReimbursementShopDataModel) {
                conveyancePopupWindow?.dismiss()

                textView.text = adapterPosition
            }
        })*/

        //rv_member_no_list.adapter = adapter

        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //adapter.filter.filter(et_search.text.toString().trim())
            }
        })

        if (conveyancePopupWindow != null && !conveyancePopupWindow?.isShowing!!) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                conveyancePopupWindow?.showAtLocation(anchorView, Gravity.TOP, 0, resources.getDimensionPixelOffset(R.dimen._10sdp))
            } else {
                conveyancePopupWindow?.showAsDropDown(anchorView, anchorView.width - conveyancePopupWindow?.width!!, 0)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callExpenseTypeDropDownPopUp(anchorView: View, arr_themes: ArrayList<*>, textView: AppCustomTextView, width: Int) {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Inflate the custom layout/view
        val customView = inflater!!.inflate(R.layout.dialog_months, null)

        conveyancePopupWindow = PopupWindow(customView, width, RelativeLayout.LayoutParams.WRAP_CONTENT)
        val rv_member_no_list = customView.findViewById(R.id.rv_months) as RecyclerView
        rv_member_no_list.layoutManager = LinearLayoutManager(mContext)

        conveyancePopupWindow?.elevation = 200f
        conveyancePopupWindow?.isFocusable = true
        conveyancePopupWindow?.update()

        val et_search = customView.findViewById(R.id.et_search) as AppCustomEditText
        et_search.visibility = View.GONE
        rv_member_no_list.visibility = View.VISIBLE

        if (arr_themes == null || arr_themes.size == 0) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
            return
        }

        val adapter = MonthListAdapter(mContext, arr_themes, object : MonthListAdapter.OnItemClickListener {

            override fun onItemClick(adapterPosition: Int) {
                conveyancePopupWindow?.dismiss()

                val genericObj = arr_themes[adapterPosition]

                when (genericObj) {
                    is String -> {

                        textView.text = genericObj

                        /*if (textView.id == R.id.tv_to_loc) {
                            if (!TextUtils.isEmpty(tv_from_loc.text.toString().trim())) {
                                if (!tv_from_loc.text.toString().trim().equals(genericObj, ignoreCase = true))
                                    textView.text = genericObj
                                else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_location_validation))
                            } else
                                textView.text = genericObj


                        } else if (textView.id == R.id.tv_from_loc) {
                            if (!TextUtils.isEmpty(tv_to_loc.text.toString().trim())) {
                                if (!tv_to_loc.text.toString().trim().equals(genericObj, ignoreCase = true))
                                    textView.text = genericObj
                                else
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_location_validation))
                            } else
                                textView.text = genericObj

                        }

                        if (!TextUtils.isEmpty(tv_to_loc.text.toString().trim()) && !TextUtils.isEmpty(tv_from_loc.text.toString().trim())) {
                            travelled_EDT.setText("5")

                            if (!amount_EDT.isEnabled && !TextUtils.isEmpty(rate))
                                amount_EDT.setText((travelled_EDT.text.toString().trim().toFloat() * rate.toFloat()).toString())
                        }*/
                    }
                    is ReimbursementConfigModeOfTravelDataModel -> {
                        textView.text = genericObj.travel_type
                        travelId = genericObj.travel_id!!

                        if (isEditable) {
                            if (genericObj.travel_type.equals("bike", ignoreCase = true) || genericObj.travel_type.equals("car", ignoreCase = true)) {
                                //km_travelled_TV.visibility = View.VISIBLE
                                km_travelled_LL.visibility = View.VISIBLE
                            } else {
                                km_travelled_TV.visibility = View.GONE
                                km_travelled_LL.visibility = View.GONE
                            }
                        } else {
                            travelled_EDT.setText("")
                            tv_from_loc.text = ""
                            tv_to_loc.text = ""

                            if (genericObj.travel_type.equals("bike", ignoreCase = true) || genericObj.travel_type.equals("car", ignoreCase = true)) {
                                amount_EDT.isEnabled = false
                                til_amount.hint = getString(R.string.amount)
                                amount_EDT.setText("")
                            } else {
                                amount_EDT.isEnabled = true
                                til_amount.hint = getString(R.string.enter_amount)
                                amount_EDT.setText("")
                            }
                        }

                        if (genericObj.fuel_config!!)
                            fuel_type_RL.visibility = View.VISIBLE
                        else {
                            fuel_type_RL.visibility = View.GONE
                            fuel_type_value_TV.text = ""
                            fetchConfigDetails("")
                        }
                    }
                    is ReimbursementConfigExpenseTypeModel -> {
                        textView.text = genericObj.expanse_type
                        expenseId = genericObj.expanse_id!!
                        expenseType = genericObj.expanse_type!!

                        if (!isEditable) {
                            travelled_EDT.setText("")
                            travelled_EDT.clearFocus()
                            amount_EDT.setText("")
                            amount_EDT.clearFocus()
                            tv_from_loc.text = ""
                            tv_to_loc.text = ""
                            mode_of_travel_type_TV.text = ""
                        }

                        when (expenseId) {
                            "1" -> {
                                defaultSelectionOfTravelledMode()
                                /*if (!isEditable) {
                                    amount_EDT.isEnabled = false
                                    til_amount.hint = getString(R.string.amount)
                                    amount_EDT.setText("")
                                } else {
                                    amount_EDT.isEnabled = true
                                    til_amount.hint = getString(R.string.enter_amount)
                                    amount_EDT.setText("")
                                }*/

                                //amount_EDT.addTextChangedListener(CustomTextWatcher(amount_EDT, 10, 10))
                            }
                            "2" -> {
                                callReimbursementLocationApi()
                                setVisibilityForHotel()
                                amount_EDT.isEnabled = true
                                til_amount.hint = getString(R.string.enter_amount)
                                amount_EDT.setText("")

                                //amount_EDT.addTextChangedListener(CustomTextWatcher(amount_EDT, 8, 2))
                            }
                            "3" -> {
                                callReimbursementLocationApi()
                                setVisibilityForFood()
                                amount_EDT.isEnabled = true
                                til_amount.hint = getString(R.string.enter_amount)
                                amount_EDT.setText("")

                                //amount_EDT.addTextChangedListener(CustomTextWatcher(amount_EDT, 8, 2))
                            }
                            else -> {
                                setVisibilityForRemark()
                                amount_EDT.isEnabled = true
                                til_amount.hint = getString(R.string.enter_amount)
                                amount_EDT.setText("")

                                //amount_EDT.addTextChangedListener(CustomTextWatcher(amount_EDT, 8, 2))
                            }
                        }

                        if (genericObj.expanse_id != "1")
                            fetchConfigDetails("")
                    }
                    is ReimbursementConfigFuelTypeModel -> {
                        textView.text = genericObj.fuel_type
                        fuelId = genericObj.fuel_type_id!!

                        if (!isEditable) {
                            travelled_EDT.setText("")
                            amount_EDT.setText("")
                            tv_from_loc.text = ""
                            tv_to_loc.text = ""
                        }

                        fetchConfigDetails(genericObj.fuel_type_id!!)
                    }

                    is ReimbursementShopDataModel -> {
                        if (textView.id == R.id.tv_to_loc) {
                            if (!TextUtils.isEmpty(tv_from_loc.text.toString().trim())) {

                                var fromIndex = -1

                                for (i in locList!!.indices) {
                                    if (tv_from_loc.text.toString().trim().equals(locList!![i].loc_name, ignoreCase = true)) {
                                        fromIndex = i
                                        break
                                    }
                                }

                                if (fromIndex > adapterPosition)
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.from_loc_smaller_than_to_loc))
                                else if (fromIndex == adapterPosition)
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_location_validation))
                                else if (!tv_from_loc.text.toString().trim().equals(genericObj.loc_name, ignoreCase = true)) {
                                    textView.text = genericObj.loc_name
                                    toShopId = genericObj.loc_id!!
                                }
                            } else {
                                textView.text = genericObj.loc_name
                                toShopId = genericObj.loc_id!!
                            }
                        } else if (textView.id == R.id.tv_from_loc) {

                            if (!TextUtils.isEmpty(tv_to_loc.text.toString().trim())) {

                                var toIndex = -1

                                for (i in locList!!.indices) {
                                    if (tv_to_loc.text.toString().trim().equals(locList!![i].loc_name, ignoreCase = true)) {
                                        toIndex = i
                                        break
                                    }
                                }

                                if (toIndex < adapterPosition)
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.from_loc_smaller_than_to_loc))
                                else if (toIndex == adapterPosition)
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_location_validation))
                                else if (!tv_to_loc.text.toString().trim().equals(genericObj.loc_name, ignoreCase = true)) {
                                    textView.text = genericObj.loc_name
                                    fromShopId = genericObj.loc_id!!
                                }

                            } else {
                                textView.text = genericObj.loc_name
                                fromShopId = genericObj.loc_id!!
                            }
                        }

                        if (!TextUtils.isEmpty(tv_to_loc.text.toString().trim()) && !TextUtils.isEmpty(tv_from_loc.text.toString().trim())) {

                            var fromIndex = -1
                            var toIndex = -1

                            for (i in locList!!.indices) {
                                if (tv_to_loc.text.toString().trim().equals(locList!![i].loc_name, ignoreCase = true)) {
                                    toIndex = i
                                    break
                                }
                            }

                            for (i in locList!!.indices) {
                                if (tv_from_loc.text.toString().trim().equals(locList!![i].loc_name, ignoreCase = true)) {
                                    fromIndex = i
                                    break
                                }
                            }

                            var distance = 0f

                            for (i in (fromIndex + 1)..toIndex) {
                                distance += locList?.get(i)?.distance!!.toFloat()
                            }

                            travelled_EDT.setText(String.format("%.2f", distance))

                            if (!amount_EDT.isEnabled && !TextUtils.isEmpty(rate))
                                amount_EDT.setText(String.format("%.2f", (travelled_EDT.text.toString().trim().toFloat() * rate.toFloat())))
                        }
                    }
                }
            }
        })

        rv_member_no_list.adapter = adapter

        if (conveyancePopupWindow != null && !conveyancePopupWindow?.isShowing!!) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                rl_add_reimbursement_main.post(Runnable {
                    conveyancePopupWindow?.showAsDropDown(anchorView, resources.getDimensionPixelOffset(R.dimen._10sdp), 0, Gravity.BOTTOM)
                })
            } else {
                conveyancePopupWindow?.showAsDropDown(anchorView, anchorView.width - conveyancePopupWindow?.width!!, 0)
            }
        }
    }

    private fun fetchConfigDetails(fuel_type_id: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        var state_id = ""
        if (!TextUtils.isEmpty(Pref.profile_state))
            state_id = Pref.profile_state


        val inputModel = ReimbursementConfigFetchInputModel()
        inputModel.user_id = Pref.user_id!!
        inputModel.state_id = state_id
        inputModel.expense_id = expenseId
        inputModel.fuel_id = fuel_type_id
        inputModel.travel_id = travelId
        inputModel.visittype_id = visitTypeId

        XLog.d("=====Fetch ReimbursementConfigApi Request=====")
        XLog.d("user_id===> " + inputModel.user_id)
        XLog.d("state_id===> " + inputModel.state_id)
        XLog.d("expense_id===> " + inputModel.expense_id)
        XLog.d("fuel_id===> " + inputModel.fuel_id)
        XLog.d("travel_id===> " + inputModel.travel_id)
        XLog.d("visittype_id===> " + inputModel.visittype_id)
        XLog.d("===============================================")

        val repository = ReimbursementConfigFetchRepoProvider.provideFetchReimbursementConfigRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.fetchReimbursementConfig(inputModel)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val configResponse = result as ReimbursementConfigFetchResponseModel
                            XLog.d("Fetch ReimbursementConfigApiResponse : " + "\n" + "Status===> " + configResponse.status + ", Message===> " + configResponse.message)

                            progress_wheel.stopSpinning()
                            if (configResponse.status == NetworkConstant.SUCCESS) {

                                if (!TextUtils.isEmpty(configResponse.maximum_allowance)) {
                                    allowance_amount_LL.visibility = View.VISIBLE
                                    maximum_amount_allowance_Per_Km_TV.text = "\u20B9 ${configResponse.maximum_allowance}"
                                } else {
                                    allowance_amount_LL.visibility = View.GONE
                                }

                                if (!TextUtils.isEmpty(configResponse.rate))
                                    rate = configResponse.rate!!

                                if (!TextUtils.isEmpty(configResponse.distance) && configResponse.distance != "0.00" && (inputModel.travel_id == "1" ||
                                                inputModel.travel_id == "2")) {
                                    allowance_travelled_LL.visibility = View.VISIBLE
                                    maximum_amount_allowance_Km_TV.text = "${configResponse.distance} K.M @ \u20B9 ${configResponse.rate}  per K.M"
                                } else {
                                    allowance_travelled_LL.visibility = View.GONE
                                }


                            } else {
                                allowance_amount_LL.visibility = View.GONE
                                allowance_travelled_LL.visibility = View.GONE
                                //  (mContext as DashboardActivity).showSnackMessage(configResponse.message!!)
                                BaseActivity.isApiInitiated = false
                                rate = ""
                            }

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("Fetch ReimbursementConfigApiResponse ERROR: " + error.localizedMessage)
                            rate = ""
                        })
        )
    }


    private fun checkValidation() {
        if (TextUtils.isEmpty(expense_type_TV.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_expense_type))
        else if (mode_of_travel_RL.visibility == View.VISIBLE && TextUtils.isEmpty(mode_of_travel_type_TV.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_travel_type))
        else if (fuel_type_RL.visibility == View.VISIBLE && TextUtils.isEmpty(fuel_type_value_TV.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_fuel_type))
        /*else if (location_from_to_LL.visibility == View.VISIBLE) {
            if (ll_editable_location.visibility == View.VISIBLE) {
                if (TextUtils.isEmpty(locatioon_from_EDT.text.toString().trim()))
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_from_location))
                else if (TextUtils.isEmpty(locatioon_to_EDT.text.toString().trim()))
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_to_location))
            }
            else if (ll_non_editable_location.visibility == View.VISIBLE) {
                if (TextUtils.isEmpty(tv_from_loc.text.toString().trim()))
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_from_location))
                else if (TextUtils.isEmpty(tv_to_loc.text.toString().trim()))
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_to_location))
            }
        }*/
        else if (location_from_to_LL.visibility == View.VISIBLE && ll_editable_location.visibility == View.VISIBLE && TextUtils.isEmpty(locatioon_from_EDT.text.toString().trim())) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_from_location))
        } else if (location_from_to_LL.visibility == View.VISIBLE && ll_editable_location.visibility == View.VISIBLE && TextUtils.isEmpty(locatioon_to_EDT.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_to_location))
        else if (location_from_to_LL.visibility == View.VISIBLE && ll_non_editable_location.visibility == View.VISIBLE && TextUtils.isEmpty(tv_from_loc.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_from_location))
        else if (location_from_to_LL.visibility == View.VISIBLE && ll_non_editable_location.visibility == View.VISIBLE && TextUtils.isEmpty(tv_to_loc.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_to_location))
        else if (TextUtils.isEmpty(amount_EDT.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_amount))
        /*else if (km_travelled_LL.visibility == View.VISIBLE && TextUtils.isEmpty(travelled_EDT.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_km))
        else if (hotel_root_LL.visibility == View.VISIBLE && TextUtils.isEmpty(et_hotel_name.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_hotel))
        else if (hotel_root_LL.visibility == View.VISIBLE && TextUtils.isEmpty(et_hotel_loc.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_loc))
        else if (food_root_LL.visibility == View.VISIBLE && !food_break_fast.isChecked && !food_lunch.isChecked && !food_dinner.isChecked)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_food_type))
        else if (food_root_LL.visibility == View.VISIBLE && TextUtils.isEmpty(et_food_loc.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_loc))*/
        /*  else if (remark_LL.visibility == View.VISIBLE && TextUtils.isEmpty(et_remark.text.toString().trim()))
              (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_remark))*/
        else if (visitTypeId == "1" && (expenseId == "1" || expenseId == "2") && isAttachmentMandatoryForLocal && TextUtils.isEmpty(imagePath_1) && TextUtils.isEmpty(imagePath_2) && TextUtils.isEmpty(imagePath_3))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_attachment))
        else if (visitTypeId == "2" && (expenseId == "1" || expenseId == "2") && isAttachmentMandatoryForOutstation && TextUtils.isEmpty(imagePath_1) && TextUtils.isEmpty(imagePath_2) && TextUtils.isEmpty(imagePath_3))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_attachment))
        else
            applyReimbursementApi()
    }


    private fun createUniqueId(): Int {
        val now = Date()
        val id = Integer.parseInt(SimpleDateFormat("ddHHmmss", Locale.US).format(now))
        return id
    }

    private fun applyReimbursementApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        if (apiIsRunning)
            return

        apiIsRunning = true

        var state_id = ""
        if (!TextUtils.isEmpty(Pref.profile_state))
            state_id = Pref.profile_state

        val reinbursementInput = ApplyReimbursementInputModel()
        reinbursementInput.session_token = Pref.session_token!!
        reinbursementInput.user_id = Pref.user_id!!
        reinbursementInput.Expense_mapId = createUniqueId().toString() + "1234"
        reinbursementInput.state_id = state_id
        //if (!TextUtils.isEmpty(date))
        reinbursementInput.date = date //AppUtils.getCurrentDateFormatInTa(date)
        reinbursementInput.visit_type_id = visitTypeId
        //reinbursementInput.expense_details = state_id

        val reimbursementData = ApplyReimbursementDataInputModel()
        reimbursementData.expence_type = expenseType
        reimbursementData.expence_type_id = expenseId

        val reimbursementDetails = ApplyReimbursementDetailsInputModel()

        reimbursementDetails.Subexpense_MapId = createUniqueId().toString()
        reimbursementDetails.amount = amount_EDT.text.toString()

        if (food_root_LL.visibility == View.VISIBLE)
            reimbursementDetails.food_type = foodType
        else
            reimbursementDetails.food_type = ""

        if (fuel_type_RL.visibility == View.VISIBLE)
            reimbursementDetails.fuel_id = fuelId
        else
            reimbursementDetails.fuel_id = ""

        when {
            food_root_LL.visibility == View.VISIBLE -> reimbursementDetails.location = et_food_loc.text.toString().trim()
            hotel_root_LL.visibility == View.VISIBLE -> reimbursementDetails.location = et_hotel_loc.text.toString().trim()
            else -> reimbursementDetails.location = ""
        }

        if (hotel_root_LL.visibility == View.VISIBLE) {
            reimbursementDetails.hotel_name = et_hotel_name.text.toString().trim()
            reimbursementDetails.start_date_time = convertFromRightToReverseFormatWithTime(checked_in_date_TV.text.toString() + " " + checked_in_time_TV.text.toString())
            reimbursementDetails.end_date_time = convertFromRightToReverseFormatWithTime(checked_out_date_TV.text.toString() + " " + checked_out_time_TV.text.toString())
        } else {
            reimbursementDetails.hotel_name = ""
            reimbursementDetails.start_date_time = ""
            reimbursementDetails.end_date_time = ""
        }

        if (remark_LL.visibility == View.VISIBLE)
            reimbursementDetails.remark = et_remark.text.toString().trim()
        else
            reimbursementDetails.remark = ""

        if (location_from_to_LL.visibility == View.VISIBLE) {
            if (!isEditable) {
                reimbursementDetails.from_location = tv_from_loc.text.toString()
                reimbursementDetails.to_location = tv_to_loc.text.toString()
                reimbursementDetails.from_loc_id = fromShopId
                reimbursementDetails.to_loc_id = toShopId
            } else {
                reimbursementDetails.from_location = locatioon_from_EDT.text.toString()
                reimbursementDetails.to_location = locatioon_to_EDT.text.toString()
            }
        } else {
            reimbursementDetails.from_location = ""
            reimbursementDetails.to_location = ""
        }

        if (mode_of_travel_RL.visibility == View.VISIBLE)
            reimbursementDetails.mode_of_travel = travelId
        else
            reimbursementDetails.mode_of_travel = ""

        if (km_travelled_LL.visibility == View.VISIBLE)
            reimbursementDetails.total_distance = travelled_EDT.text.toString().trim()
        else
            reimbursementDetails.total_distance = ""

        val reimbursementDetailsList = ArrayList<ApplyReimbursementDetailsInputModel>()
        reimbursementDetailsList.add(reimbursementDetails)
        reimbursementData.reimbursement_details = reimbursementDetailsList

        val expenseList = ArrayList<ApplyReimbursementDataInputModel>()
        expenseList.add(reimbursementData)
        reinbursementInput.expense_details = expenseList

        val repository = ApplyReimbursementRepoProvider.applyReimbursementConfigRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.applyReimbursement(reinbursementInput)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val configResponse = result as BaseResponse
                            XLog.d("Apply Reimbursement Api Response : " + "\n" + "Status====> " + configResponse.status + ", Message===> " + configResponse.message)

                            if (configResponse.status == NetworkConstant.SUCCESS) {
                                //if (imagePathArray.size > 0)
                                if (!TextUtils.isEmpty(imagePath_1) || !TextUtils.isEmpty(imagePath_2) || !TextUtils.isEmpty(imagePath_3))
                                    applyReimbursementImageApi(reinbursementInput)
                                else {
                                    progress_wheel.stopSpinning()
                                    apiIsRunning = false
                                    changeUi(reinbursementInput)
                                    (mContext as DashboardActivity).showSnackMessage(configResponse.message!!)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(configResponse.message!!)
                                apiIsRunning = false
                            }

                        }, { error ->
                            apiIsRunning = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("Apply Reimbursement Api ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun applyReimbursementImageApi(reinbursementInput: ApplyReimbursementInputModel) {

        if (!TextUtils.isEmpty(imagePath_1)) {
            imagePathArray.add(imagePath_1)
            XLog.e("Reimbursement Fragment: Image link 1===> $imagePath_1")
        }

        if (!TextUtils.isEmpty(imagePath_2)) {
            imagePathArray.add(imagePath_2)
            XLog.e("Reimbursement Fragment: Image link 2===> $imagePath_2")
        }

        if (!TextUtils.isEmpty(imagePath_3)) {
            imagePathArray.add(imagePath_3)
            XLog.e("Reimbursement Fragment: Image link 3===> $imagePath_3")
        }

        val repository = ApplyReimbursementRepoProvider.applyReimbursementConfigRepository()
        BaseActivity.compositeDisposable.add(
                repository.applyReimbursementImageUpload(reinbursementInput, imagePathArray)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val configResponse = result as BaseResponse
                            XLog.d("Apply Reimbursement Api Response : " + "\n" + "Status====> " + configResponse.status + ", Message===> " + configResponse.message)

                            progress_wheel.stopSpinning()
                            if (configResponse.status == NetworkConstant.SUCCESS) {
                                //(mContext as DashboardActivity).onBackPressed()
                                changeUi(reinbursementInput)
                                imagePath_1 = ""
                                imagePath_2 = ""
                                imagePath_3 = ""
                            }

                            (mContext as DashboardActivity).showSnackMessage(configResponse.message!!)

                            apiIsRunning = false

                        }, { error ->
                            apiIsRunning = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("Apply Reimbursement Api ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun appliedReimbursementDeleteApi(deletReinbursementInput: AppliedReimbursementDeleteInputModel, adapterPosition: Int) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        if (apiIsRunning)
            return

        apiIsRunning = true

        val repository = ApplyReimbursementRepoProvider.applyReimbursementConfigRepository()
        BaseActivity.compositeDisposable.add(
                repository.deleteAppliedReimbursement(deletReinbursementInput)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val configResponse = result as BaseResponse
                            XLog.d("Delete Reimbursement Api Response : " + "\n" + "Status====> " + configResponse.status + ", Message===> " + configResponse.message)

                            progress_wheel.stopSpinning()
                            if (configResponse.status == NetworkConstant.SUCCESS) {

                                reimbursmentTaList.removeAt(adapterPosition)
                                reimbursementBottomAdapter?.notifyItemRemoved(adapterPosition)

                                var totalAmount = 0
                                for (i in reimbursmentTaList.indices) {
                                    totalAmount += reimbursmentTaList[i].expense_details[0].reimbursement_details[0].amount.toInt()
                                }

                                tv_total_amount.text = getString(R.string.rupee_symbol) + " " + totalAmount.toString()

                                if (reimbursmentTaList.size == 0) {
                                    clBottomSheet.visibility = View.GONE

                                    val layoutParams = nsv_apply_reimbursement.layoutParams as RelativeLayout.LayoutParams
                                    layoutParams.setMargins(0, 0, 0, mContext.resources.getDimensionPixelOffset(R.dimen._5sdp));
                                    nsv_apply_reimbursement.layoutParams = layoutParams
                                }
                            }

                            (mContext as DashboardActivity).showSnackMessage(configResponse.message!!)

                            apiIsRunning = false

                        }, { error ->
                            apiIsRunning = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("Delete Reimbursement Api ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun changeUi(reinbursementInput: ApplyReimbursementInputModel) {
        reimbursmentTaList.add(reinbursementInput)
        initBottomLayoutAdapter()

        fromShopId = ""
        toShopId = ""

        /*iv_upload_bills_1.setBackgroundResource(0)
        iv_upload_bills_1.setImageResource(0)
        iv_upload_bills_2.setBackgroundResource(0)
        iv_upload_bills_2.setImageResource(0)
        iv_upload_bills_3.setBackgroundResource(0)
        iv_upload_bills_3.setImageResource(0)*/

        iv_upload_bills_1.setImageResource(R.drawable.ic_upload_icon)
        iv_upload_bills_2.setImageResource(R.drawable.ic_upload_icon)
        iv_upload_bills_3.setImageResource(R.drawable.ic_upload_icon)

        rl_image_3.visibility = View.GONE
        rl_image_2.visibility = View.GONE

        if (rl_image_2.visibility == View.GONE)
            Log.e("Apply Reimbursement", "rl_image_2 is gone")
        else
            Log.e("Apply Reimbursement", "rl_image_2 is visible")

        //iv_upload_bills_1.visibility = View.VISIBLE

        imagePathArray.clear()

        //if (clBottomSheet.visibility == View.GONE) {
        val layoutParams = nsv_apply_reimbursement.layoutParams as RelativeLayout.LayoutParams
        layoutParams.setMargins(0, 0, 0, mContext.resources.getDimensionPixelOffset(R.dimen._50sdp));
        nsv_apply_reimbursement.layoutParams = layoutParams
        //}

        clBottomSheet.visibility = View.VISIBLE

        if (reimbursmentTaList.size == 1)
            tv_ta_count.text = reimbursmentTaList.size.toString() + " TA Entry"
        else
            tv_ta_count.text = reimbursmentTaList.size.toString() + " TA Entries"


        iv_image_cross_icon_1.visibility = View.GONE
        iv_image_cross_icon_2.visibility = View.GONE
        iv_image_cross_icon_3.visibility = View.GONE

        resetData()
    }

    private fun resetData() {
        expense_type_TV.text = ""
        mode_of_travel_type_TV.text = ""
        fuel_type_value_TV.text = ""
        et_hotel_name.setText("")
        et_hotel_loc.setText("")
        et_food_loc.setText("")
        locatioon_from_EDT.setText("")
        locatioon_to_EDT.setText("")
        amount_EDT.setText("")
        travelled_EDT.setText("")
        et_remark.setText("")
        tv_from_loc.text = ""
        tv_to_loc.text = ""

        checked_in_date_TV.text = getCurrentDate()
        checked_in_time_TV.text = getCurrentTime()
        checked_out_date_TV.text = getCurrentDate()
        checked_out_time_TV.text = getCurrentTime()

        radioGroup_food.clearCheck()

        //defaultSelectionOfTravelledMode()

        if (!isEditable)
            callReimbursementShopApi()
    }

    private fun initBottomLayoutAdapter() {

        var totalAmount = 0f
        for (i in reimbursmentTaList.indices) {
            totalAmount += reimbursmentTaList[i].expense_details[0].reimbursement_details[0].amount.toFloat()
        }

        tv_total_amount.text = getString(R.string.rupee_symbol) + " " + String.format("%.2f", totalAmount)

        reimbursementBottomAdapter = ReimbursementBottomLayoutAdapter(mContext, reimbursmentTaList, modeOfTravelArrayList, object : ReimbursementBottomLayoutAdapter.OnItemClickListener {
            override fun onDeleteClick(adapterPosition: Int) {
                showDeleteAlert(adapterPosition)
            }
        })

        rv_ta_list.adapter = reimbursementBottomAdapter
    }

    private fun showDeleteAlert(adapterPosition: Int) {

        CommonDialog.getInstance("Delete Alert", "Do you really want to delete this TA?", getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {

                val deletReinbursementInput = AppliedReimbursementDeleteInputModel()
                val reimbursmentTaRow = reimbursmentTaList[adapterPosition]

                deletReinbursementInput.session_token = Pref.session_token!!
                deletReinbursementInput.user_id = reimbursmentTaRow.user_id
                deletReinbursementInput.Expense_mapId = reimbursmentTaRow.Expense_mapId
                deletReinbursementInput.Subexpense_MapId = reimbursmentTaRow.expense_details[0].reimbursement_details[0].Subexpense_MapId
                deletReinbursementInput.visit_type_id = reimbursmentTaRow.visit_type_id
                deletReinbursementInput.date = reimbursmentTaRow.date

                appliedReimbursementDeleteApi(deletReinbursementInput, adapterPosition)

                /*   reimbursmentTaList.removeAt(adapterPosition)
                   reimbursementBottomAdapter?.notifyItemRemoved(adapterPosition)

                   var totalAmount = 0
                   for (i in reimbursmentTaList.indices) {
                       totalAmount += reimbursmentTaList[i].expense_details[0].reimbursement_details[0].amount.toInt()
                   }

                   tv_total_amount.text = getString(R.string.rupee_symbol) + " " + totalAmount.toString()

                   if (reimbursmentTaList.size == 0) {
                       clBottomSheet.visibility = View.GONE

                       val layoutParams = nsv_apply_reimbursement.layoutParams as RelativeLayout.LayoutParams
                       layoutParams.setMargins(0, 0, 0, mContext.resources.getDimensionPixelOffset(R.dimen._5sdp));
                       nsv_apply_reimbursement.layoutParams = layoutParams
                   }*/
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    override fun onPause() {
        super.onPause()
        conveyancePopupWindow?.dismiss()
    }
}