package com.kcteam.features.reimbursement.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.appcompat.widget.*
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import android.widget.ListPopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.reimbursement.api.ReimbursementConfigRepoProvider
import com.kcteam.features.reimbursement.api.applyapi.ApplyReimbursementRepoProvider
import com.kcteam.features.reimbursement.api.configfetchapi.ReimbursementConfigFetchRepoProvider
import com.kcteam.features.reimbursement.model.*
import com.kcteam.features.reimbursement.model.reimbursementlist.ReimbursementListDataModel
import com.kcteam.features.reimbursement.model.reimbursementlist.ReimbursementListDetailsModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Saikat on 31-01-2019.
 */
class ReimbursementDetailsFragment : BaseFragment(), DateAdapter.onPetSelectedListener, View.OnClickListener, TabLayout.OnTabSelectedListener, RadioGroup.OnCheckedChangeListener {

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
    private lateinit var food_break_fast: AppCompatRadioButton
    private lateinit var food_lunch: AppCompatRadioButton
    private lateinit var food_dinner: AppCompatRadioButton
    private lateinit var select_date_tv: AppCustomTextView
    private lateinit var iv_expense_dropdown_icon: ImageView
    private lateinit var iv_travel_dropdown_icon: ImageView
    private lateinit var iv_fuel_type_dropdown_icon: ImageView
    private lateinit var tv_upload_ticket: AppCustomTextView
    private lateinit var rl_image: RelativeLayout
    private lateinit var ll_to_loc: LinearLayout
    private lateinit var ll_from_loc: LinearLayout
    private lateinit var tv_from_loc: AppCustomTextView
    private lateinit var tv_to_loc: AppCustomTextView
    private lateinit var iv_from_dropdown_icon: ImageView
    private lateinit var iv_to_dropdown_icon: ImageView
    private lateinit var ll_editable_location: LinearLayout
    private lateinit var ll_non_editable_location: LinearLayout
    private lateinit var iv_food_location_dropdown: ImageView
    private lateinit var iv_location_dropdown: ImageView

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
    private var reimbursementDetails: ReimbursementListDetailsModel? = null

    private lateinit var rl_image_2: RelativeLayout
    private lateinit var rl_image_3: RelativeLayout
    private lateinit var rl_image_1: RelativeLayout

    private lateinit var iv_image_cross_icon_1: AppCompatImageView
    private lateinit var iv_image_cross_icon_2: AppCompatImageView
    private lateinit var iv_image_cross_icon_3: AppCompatImageView

    private lateinit var til_hotel_loc: TextInputLayout
    private lateinit var til_food_loc: TextInputLayout

    private var isEditable = false

    companion object {
        private var reimbursementItem: ReimbursementListDataModel? = null

        fun newInstance(objects: Any): ReimbursementDetailsFragment {
            val fragment = ReimbursementDetailsFragment()
            if (objects != null && objects is ReimbursementListDataModel)
                reimbursementItem = objects
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        reimbursementDetails = reimbursementItem?.expense_list_details?.get((mContext as DashboardActivity).reimbursementSelectPosition)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_reimbursement_new, container, false)

        initView(view)
        initClickListener()
        disableUi()
        callReimbursementSettingsApi()
        setData()

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

        til_food_loc = view.findViewById(R.id.til_food_loc)
        til_hotel_loc = view.findViewById(R.id.til_food_loc)
        til_food_loc.hint = "Location"
        til_hotel_loc.hint = "Location"

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
        iv_expense_dropdown_icon = view.findViewById(R.id.iv_expense_dropdown_icon)
        iv_travel_dropdown_icon = view.findViewById(R.id.iv_travel_dropdown_icon)
        iv_fuel_type_dropdown_icon = view.findViewById(R.id.iv_fuel_type_dropdown_icon)
        tv_upload_ticket = view.findViewById(R.id.tv_upload_ticket)
        rl_image = view.findViewById(R.id.rl_image)
        //tv_upload_ticket = view.findViewById(R.id.tv_upload_ticket)

        iv_food_location_dropdown = view.findViewById(R.id.iv_food_location_dropdown)
        iv_location_dropdown = view.findViewById(R.id.iv_location_dropdown)

        iv_location_dropdown.visibility = View.GONE
        iv_food_location_dropdown.visibility = View.GONE

        ll_editable_location = view.findViewById(R.id.ll_editable_location)
        ll_non_editable_location = view.findViewById(R.id.ll_non_editable_location)

        ll_to_loc = view.findViewById(R.id.ll_to_loc)
        tv_to_loc = view.findViewById(R.id.tv_to_loc)
        ll_from_loc = view.findViewById(R.id.ll_from_loc)
        tv_from_loc = view.findViewById(R.id.tv_from_loc)
        iv_from_dropdown_icon = view.findViewById(R.id.iv_from_dropdown_icon)
        iv_from_dropdown_icon.visibility = View.GONE
        iv_to_dropdown_icon = view.findViewById(R.id.iv_to_dropdown_icon)
        iv_to_dropdown_icon.visibility = View.GONE

        rvDateList = view.findViewById(R.id.rvDateList)
        rvDateList?.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        dateAdapter = DateAdapter(mContext, false, this)
        rvDateList?.adapter = dateAdapter

        llChildLayout = view.findViewById(R.id.llChildLayout)
        rv_ta_list = view.findViewById(R.id.rv_ta_list)
        rv_ta_list.layoutManager = LinearLayoutManager(mContext)
        ivBottomSheetExapnd = view.findViewById(R.id.ivBottomSheetExapnd)
        bottom_sheet = view.findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
        //setBottomSheetbehaviour()
        //attachkcteamData()

        rl_image_1 = view.findViewById(R.id.rl_image_1)
        rl_image_2 = view.findViewById(R.id.rl_image_2)
        rl_image_3 = view.findViewById(R.id.rl_image_3)

        iv_image_cross_icon_1 = view.findViewById(R.id.iv_image_cross_icon_1)
        iv_image_cross_icon_2 = view.findViewById(R.id.iv_image_cross_icon_2)
        iv_image_cross_icon_3 = view.findViewById(R.id.iv_image_cross_icon_3)

        checked_in_date_TV.text = getCurrentDate()
        checked_in_time_TV.text = getCurrentTime()
        checked_out_date_TV.text = getCurrentDate()
        checked_out_time_TV.text = getCurrentTime()

        // travelled_EDT.addTextChangedListener(CustomTextWatcher(travelled_EDT, 6, 2))
    }


    private fun disableUi() {

        val tabStrip = tabLayout.getChildAt(0) as LinearLayout

        for (i in 0 until tabStrip.childCount) {
            tabStrip.getChildAt(i).setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    return true
                }
            })
        }

        iv_image_cross_icon_1.visibility = View.GONE
        iv_image_cross_icon_2.visibility = View.GONE
        iv_image_cross_icon_3.visibility = View.GONE

        //tabLayout.isEnabled = false
        iv_expense_dropdown_icon.visibility = View.GONE
        iv_travel_dropdown_icon.visibility = View.GONE
        iv_fuel_type_dropdown_icon.visibility = View.GONE
        expense_type_TV.isEnabled = false
        //expense_type_TV.setOnClickListener(null)
        mode_of_travel_type_TV.isEnabled = false
        fuel_type_value_TV.isEnabled = false
        et_hotel_name.isEnabled = false
        checked_in_date_TV.isEnabled = false
        checked_in_time_TV.isEnabled = false
        checked_out_date_TV.isEnabled = false
        checked_out_time_TV.isEnabled = false
        et_hotel_loc.isEnabled = false
        food_break_fast.isEnabled = false
        food_lunch.isEnabled = false
        food_dinner.isEnabled = false
        et_food_loc.isEnabled = false
        locatioon_from_EDT.isEnabled = false
        locatioon_to_EDT.isEnabled = false
        amount_EDT.isEnabled = false
        travelled_EDT.isEnabled = false
        et_remark.isEnabled = false
        tv_upload_ticket.visibility = View.GONE
        submit_button_TV.visibility = View.GONE
        /*iv_upload_bills_1.isEnabled = false
        iv_upload_bills_2.isEnabled = false
        iv_upload_bills_3.isEnabled = false*/
        rl_image_1.visibility = View.GONE
    }

    private fun setData() {
        if (reimbursementDetails?.visit_type.equals("local", ignoreCase = true)) {
            tabLayout.getTabAt(0)?.select()
        } else {
            tabLayout.getTabAt(1)?.select()
        }

        if (!TextUtils.isEmpty(reimbursementDetails?.travel_mode)) {
            mode_of_travel_RL.visibility = View.VISIBLE
            mode_of_travel_type_TV.text = reimbursementDetails?.travel_mode
        }

        if (!TextUtils.isEmpty(reimbursementDetails?.fuel_type)) {
            fuel_type_RL.visibility = View.VISIBLE
            fuel_type_value_TV.text = reimbursementDetails?.fuel_type
        }

        expense_type_TV.text = reimbursementItem?.expense_type

        if (reimbursementItem?.expense_type_id == "1") {
            defaultSelectionOfTravelledMode()
        } else if (reimbursementItem?.expense_type_id == "2") {
            setVisibilityForHotel()
        } else if (reimbursementItem?.expense_type_id == "3") {
            setVisibilityForFood()
        } else
            setVisibilityForRemark()


        if (!TextUtils.isEmpty(reimbursementDetails?.food_type)) {
            if (reimbursementDetails?.food_type.equals(getString(R.string.breakfast), ignoreCase = true))
                food_break_fast.isChecked = true
            else if (reimbursementDetails?.food_type.equals(getString(R.string.lunch), ignoreCase = true))
                food_lunch.isChecked = true
            else if (reimbursementDetails?.food_type.equals(getString(R.string.dinner), ignoreCase = true))
                food_dinner.isChecked = true
        }

        if (!TextUtils.isEmpty(reimbursementDetails?.remarks))
            et_remark.setText(reimbursementDetails?.remarks)
        else
            et_remark.setText("N.A.")

        if (!TextUtils.isEmpty(reimbursementDetails?.travel_mode_id)) {
            km_travelled_LL.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(reimbursementDetails?.distance)) {
                travelled_EDT.setText(reimbursementDetails?.distance)
            } else
                travelled_EDT.setText("N.A.")
        } else
            km_travelled_LL.visibility = View.GONE

        if (!TextUtils.isEmpty(reimbursementDetails?.from_location)) {
            locatioon_from_EDT.setText(reimbursementDetails?.from_location)
            tv_from_loc.text = reimbursementDetails?.from_location
        } else
            locatioon_from_EDT.setText("N.A.")

        if (!TextUtils.isEmpty(reimbursementDetails?.to_location)) {
            locatioon_to_EDT.setText(reimbursementDetails?.to_location)
            tv_to_loc.text = reimbursementDetails?.to_location
        } else
            locatioon_to_EDT.setText("N.A.")

        if (!TextUtils.isEmpty(reimbursementDetails?.hotel_location)) {
            et_food_loc.setText(reimbursementDetails?.hotel_location)
        } else
            et_food_loc.setText("N.A.")

        if (!TextUtils.isEmpty(reimbursementDetails?.hotel_location)) {
            et_hotel_loc.setText(reimbursementDetails?.hotel_location)
        } else
            et_hotel_loc.setText("N.A.")

        if (!TextUtils.isEmpty(reimbursementDetails?.hotel_name)) {
            et_hotel_name.setText(reimbursementDetails?.hotel_name)
        } else
            et_hotel_name.setText("N.A.")


        if (!TextUtils.isEmpty(reimbursementDetails?.maximum_distance) && reimbursementDetails?.maximum_distance != "0.00" &&
                (reimbursementDetails?.travel_mode_id == "1" || reimbursementDetails?.travel_mode_id == "2")) {
            allowance_travelled_LL.visibility = View.VISIBLE
            maximum_amount_allowance_Km_TV.text = "${reimbursementDetails?.maximum_distance} K.M @ \u20B9 ${reimbursementDetails?.maximum_rate}  per K.M"
        }

        if (!TextUtils.isEmpty(reimbursementDetails?.maximum_allowance) && reimbursementDetails?.maximum_allowance != "0.00") {
            allowance_amount_LL.visibility = View.VISIBLE
            maximum_amount_allowance_Per_Km_TV.text = "\u20B9 ${reimbursementDetails?.maximum_allowance}"
        }

        if (!TextUtils.isEmpty(reimbursementDetails?.start_date_time)) {
            checked_in_date_TV.text = AppUtils.getDateFromDateTime(reimbursementDetails?.start_date_time!!)
            checked_in_time_TV.text = AppUtils.getMeredianTimeFromDateTime(reimbursementDetails?.start_date_time!!)
        }

        if (!TextUtils.isEmpty(reimbursementDetails?.end_date_time)) {
            checked_out_date_TV.text = AppUtils.getDateFromDateTime(reimbursementDetails?.end_date_time!!)
            checked_out_time_TV.text = AppUtils.getMeredianTimeFromDateTime(reimbursementDetails?.end_date_time!!)
        }

        if (!TextUtils.isEmpty(reimbursementDetails?.amount)) {
            amount_EDT.setText(reimbursementDetails?.amount)
        } else
            amount_EDT.setText("N.A.")


        if (reimbursementDetails?.image_list != null && reimbursementDetails?.image_list?.size!! > 0) {
            try {

                tv_upload_ticket.visibility = View.VISIBLE
                tv_upload_ticket.text = "Ticket/Bills Image"

                Glide.with(mContext)
                        .load(reimbursementDetails?.image_list?.get(0)?.links)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                        .into(iv_upload_bills_1)
                rl_image_1.visibility = View.VISIBLE

                Glide.with(mContext)
                        .load(reimbursementDetails?.image_list?.get(1)?.links)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                        .into(iv_upload_bills_2)
                rl_image_2.visibility = View.VISIBLE

                Glide.with(mContext)
                        .load(reimbursementDetails?.image_list?.get(2)?.links)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                        .into(iv_upload_bills_3)
                rl_image_3.visibility = View.VISIBLE

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        setDateData("7")
    }

    private fun initClickListener() {
        /*reimbursement_type_RL.setOnClickListener(this)
        mode_of_travel_RL.setOnClickListener(this)
        checked_in_date_TV.setOnClickListener(this)
        checked_in_time_TV.setOnClickListener(this)
        checked_out_date_TV.setOnClickListener(this)
        checked_out_time_TV.setOnClickListener(this)
        fuel_type_value_TV.setOnClickListener(this)*/
        iv_upload_bills_1.setOnClickListener(this)
        iv_upload_bills_2.setOnClickListener(this)
        iv_upload_bills_3.setOnClickListener(this)
        ll_to_loc.setOnClickListener(null)
        ll_from_loc.setOnClickListener(null)
        /*addNewTA_TV.setOnClickListener(this)
        submit_button_TV.setOnClickListener(this)*/
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

                                isEditable = configResponse.isEditable!!

                                if (isEditable) {
                                    ll_editable_location.visibility = View.VISIBLE
                                    ll_non_editable_location.visibility = View.GONE
                                } else {
                                    ll_editable_location.visibility = View.GONE
                                    ll_non_editable_location.visibility = View.VISIBLE
                                }

                            }

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
        if (sheetBehavior?.getState() === BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior?.setState(BottomSheetBehavior.STATE_COLLAPSED)
        } else {
            sheetBehavior?.setState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDateData(reimbursement_past_days: String?) {
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        //calendar.add(Calendar.DAY_OF_YEAR, -1)
        var todayDate: Date? = null /*= calendar.time*/


        if (!TextUtils.isEmpty(reimbursementDetails?.applied_date)) {
            todayDate = AppUtils.getDateFromDateString(reimbursementDetails?.applied_date!!)
        }
        calendar.time = todayDate!!
        dateList.add(todayDate)

        selectedDate = todayDate
        val dateFormat = SimpleDateFormat("dd MMM")
        val formattedDate = dateFormat.format(selectedDate)
        date = AppUtils.getFormattedDateForApi(selectedDate!!)

        val lastPastDay = reimbursement_past_days!!.toInt() - 1

        select_date_tv.text = "Select Date (You can apply for past $reimbursement_past_days days only)"

        for (i in 1..lastPastDay) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)

            val nextDate = calendar.time
            dateList.add(nextDate)
        }

        dateAdapter?.refreshAdapter(dateList)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabSelected(tab: TabLayout.Tab?) {

        expense_type_TV.text = ""
        mode_of_travel_type_TV.text = ""
        fuel_type_value_TV.text = ""
        fuel_type_RL.visibility = View.GONE

        if (1 == tab!!.position) {
            visitTypeId = "2"
        } else {
            defaultSelectionOfTravelledMode()
            visitTypeId = "1"
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
                    callExpenseTypeDropDownPopUp(reimbursement_type_RL, expenseTypesArrayList, expense_type_TV)
            }
            R.id.addNewTA_TV -> {
                AppUtils.hideSoftKeyboard(mContext as Activity)
                checkValidation()
                //applyReimbursementApi()
            }
            R.id.mode_of_travel_RL -> {
                if (conveyancePopupWindow != null && conveyancePopupWindow!!.isShowing)
                    conveyancePopupWindow?.dismiss()
                else
                    callExpenseTypeDropDownPopUp(mode_of_travel_RL, modeOfTravelArrayList, mode_of_travel_type_TV)
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
                    callExpenseTypeDropDownPopUp(fuel_type_RL, fuelTypesArrayList, fuel_type_value_TV)
            }

            R.id.iv_upload_bills_1 -> {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck(1)
                else {
                    imageState = 1
                    (mContext as DashboardActivity).captureImage()
                }*/

                FullImageDialog.getInstance(reimbursementDetails?.image_list?.get(0)?.links!!).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            R.id.iv_upload_bills_2 -> {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck(2)
                else {
                    imageState = 2
                    (mContext as DashboardActivity).captureImage()
                }*/

                FullImageDialog.getInstance(reimbursementDetails?.image_list?.get(1)?.links!!).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            R.id.iv_upload_bills_3 -> {
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck(3)
                else {
                    imageState = 3
                    (mContext as DashboardActivity).captureImage()
                }*/

                FullImageDialog.getInstance(reimbursementDetails?.image_list?.get(2)?.links!!).show((mContext as DashboardActivity).supportFragmentManager, "")
            }

            R.id.submit_button_TV -> {
                //applyReimbursementApi()
                //(mContext as DashboardActivity).onBackPressed()
                checkValidation()
            }
        }
    }

    fun setImage(filePath: String) {

        Log.e("Reimbursement Fragment", "Image link====>$filePath")
        imagePathArray.add(filePath)

        if (iv_upload_bills_2.visibility == View.GONE) {
            iv_upload_bills_2.visibility = View.VISIBLE
        } else {
            iv_upload_bills_3.visibility = View.VISIBLE
        }

        if (imageState == 1) {
            imagePath_1 = filePath
            Glide.with(mContext)
                    .load(filePath)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                    .into(iv_upload_bills_1)
        } else if (imageState == 2) {
            imagePath_2 = filePath
            Glide.with(mContext)
                    .load(filePath)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                    .into(iv_upload_bills_2)
        } else if (imageState == 3) {
            imagePath_3 = filePath
            Glide.with(mContext)
                    .load(filePath)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_upload_icon).error(R.drawable.ic_upload_icon))
                    .into(iv_upload_bills_3)
        }
    }

    private fun initPermissionCheck(state: Int) {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                imageState = state
                (mContext as DashboardActivity).captureImage()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    override fun onDateItemClick(pos: Int) {
        /*selectedDate = dateList.get(pos)
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate)

        date = AppUtils.getFormattedDateForApi(selectedDate!!)*/
    }

    private fun setModeOfTravelSelection(txtView: AppCustomTextView, selectedBackgroundColor: Int, selectedIcon: Int, color: Int) {
        txtView.setBackgroundResource(selectedBackgroundColor)
        txtView.setCompoundDrawablesWithIntrinsicBounds(0, selectedIcon, 0, 0);
        txtView.setTextColor(ContextCompat.getColor(mContext, color))
    }

    private fun defaultSelectionOfTravelledMode() {
        km_travelled_LL.visibility = View.VISIBLE
        //km_travelled_TV.visibility = View.VISIBLE

        hotel_root_LL.visibility = View.GONE
        location_from_to_LL.visibility = View.VISIBLE
        food_root_LL.visibility = View.GONE

        mode_of_travel_TV.visibility = View.VISIBLE
        mode_of_travel_RL.visibility = View.VISIBLE

        // remark_LL.visibility = View.GONE
        //radioGroup_food.clearCheck()

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
        //radioGroup_food.clearCheck()
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

        //radioGroup_food.clearCheck()
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

        //radioGroup_food.clearCheck()
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
    private fun callExpenseTypeDropDownPopUp(anchorView: View, arr_themes: ArrayList<*>, textView: AppCustomTextView) {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Inflate the custom layout/view
        val customView = inflater!!.inflate(R.layout.dialog_months, null)

        conveyancePopupWindow = PopupWindow(customView, resources.getDimensionPixelOffset(R.dimen._270sdp), RelativeLayout.LayoutParams.WRAP_CONTENT)
        val rv_member_no_list = customView.findViewById(R.id.rv_months) as RecyclerView
        rv_member_no_list.layoutManager = LinearLayoutManager(mContext)

        conveyancePopupWindow?.elevation = 200f

        rv_member_no_list.adapter = MonthListAdapter(mContext, arr_themes, object : MonthListAdapter.OnItemClickListener {
            override fun onItemClick(adapterPosition: Int) {
                conveyancePopupWindow?.dismiss()

                val genericObj = arr_themes[adapterPosition]

                when (genericObj) {
                    is String -> textView.text = genericObj
                    is ReimbursementConfigModeOfTravelDataModel -> {
                        textView.text = genericObj.travel_type
                        travelId = genericObj.travel_id!!

                        if (genericObj.travel_type.equals("bike", ignoreCase = true) || genericObj.travel_type.equals("car", ignoreCase = true)) {
                            //km_travelled_TV.visibility = View.VISIBLE
                            km_travelled_LL.visibility = View.VISIBLE
                        } else {
                            km_travelled_TV.visibility = View.GONE
                            km_travelled_LL.visibility = View.GONE
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

                        when (expenseId) {
                            "1" -> defaultSelectionOfTravelledMode()
                            "2" -> setVisibilityForHotel()
                            "3" -> setVisibilityForFood()
                            else -> {
                                setVisibilityForRemark()
                            }
                        }

                        if (genericObj.expanse_id != "1")
                            fetchConfigDetails("")
                    }
                    is ReimbursementConfigFuelTypeModel -> {
                        textView.text = genericObj.fuel_type
                        fuelId = genericObj.fuel_type_id!!
                        fetchConfigDetails(genericObj.fuel_type_id!!)
                    }
                }
            }
        })

        if (conveyancePopupWindow != null && !conveyancePopupWindow?.isShowing!!) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                conveyancePopupWindow?.showAsDropDown(anchorView, resources.getDimensionPixelOffset(R.dimen._10sdp), 0, Gravity.BOTTOM)
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

                                if (!TextUtils.isEmpty(configResponse.distance) && configResponse.distance != "0.00" && inputModel.travel_id == "1" && inputModel.travel_id == "2") {
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

                            }

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("Fetch ReimbursementConfigApiResponse ERROR: " + error.localizedMessage)
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
        else if (TextUtils.isEmpty(amount_EDT.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_select_amount))
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
            reimbursementDetails.start_date_time = AppUtils.convertFromRightToReverseFormatWithTime(checked_in_date_TV.text.toString() + " " + checked_in_time_TV.text.toString())
            reimbursementDetails.end_date_time = AppUtils.convertFromRightToReverseFormatWithTime(checked_out_date_TV.text.toString() + " " + checked_out_time_TV.text.toString())
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
            reimbursementDetails.from_location = locatioon_from_EDT.text.toString()
            reimbursementDetails.to_location = locatioon_to_EDT.text.toString()
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

                            progress_wheel.stopSpinning()
                            if (configResponse.status == NetworkConstant.SUCCESS) {
                                if (imagePathArray.size > 0)
                                    applyReimbursementImageApi(reinbursementInput)
                                else {
                                    apiIsRunning = false
                                    changeUi(reinbursementInput)
                                    (mContext as DashboardActivity).showSnackMessage(configResponse.message!!)
                                }
                            } else {
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

        iv_upload_bills_1.setBackgroundResource(0)
        iv_upload_bills_1.setImageResource(0)
        iv_upload_bills_2.setBackgroundResource(0)
        iv_upload_bills_2.setImageResource(0)
        iv_upload_bills_3.setBackgroundResource(0)
        iv_upload_bills_3.setImageResource(0)

        iv_upload_bills_1.setImageResource(R.drawable.ic_upload_icon)

        iv_upload_bills_3.visibility = View.GONE
        iv_upload_bills_2.visibility = View.GONE
        iv_upload_bills_1.visibility = View.VISIBLE

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
        //travelled_EDT.setText("")
        et_remark.setText("")

        checked_in_date_TV.text = getCurrentDate()
        checked_in_time_TV.text = getCurrentTime()
        checked_out_date_TV.text = getCurrentDate()
        checked_out_time_TV.text = getCurrentTime()

        radioGroup_food.clearCheck()

        //defaultSelectionOfTravelledMode()
    }

    private fun initBottomLayoutAdapter() {

        var totalAmount = 0
        for (i in reimbursmentTaList.indices) {
            totalAmount += reimbursmentTaList[i].expense_details[0].reimbursement_details[0].amount.toInt()
        }

        tv_total_amount.text = getString(R.string.rupee_symbol) + " " + totalAmount.toString()

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