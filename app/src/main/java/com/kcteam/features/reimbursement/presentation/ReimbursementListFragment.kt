package com.kcteam.features.reimbursement.presentation

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.elvishew.xlog.XLog
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.reimbursement.api.applyapi.ApplyReimbursementRepoProvider
import com.kcteam.features.reimbursement.api.reimbursement_list_api.ReimbursementListRepoProvider
import com.kcteam.features.reimbursement.model.AppliedReimbursementDeleteInputModel
import com.kcteam.features.reimbursement.model.ListExpenseTypeModel
import com.kcteam.features.reimbursement.model.ReimburesementListExpenseModel
import com.kcteam.features.reimbursement.model.reimbursementlist.ReimbursementListDataModel
import com.kcteam.features.reimbursement.model.reimbursementlist.ReimbursementListDetailsModel
import com.kcteam.features.reimbursement.model.reimbursementlist.ReimbursementListResponseModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import com.rackspira.kristiawan.rackmonthpicker.RackMonthPicker
import com.rackspira.kristiawan.rackmonthpicker.listener.DateMonthDialogListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DateFormatSymbols
import java.util.*

/**
 * Created by Saikat on 22-01-2019.
 */
class ReimbursementListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    private lateinit var rv_reimbursment_list: RecyclerView
    private lateinit var fl_travel: FrameLayout
    private lateinit var fl_hotel: FrameLayout
    private lateinit var fl_food: FrameLayout
    private lateinit var cv_travel_price: CardView
    private lateinit var iv_tavel_rupee: ImageView
    private lateinit var tv_travel_amount: AppCustomTextView
    private lateinit var cv_hotel_price: CardView
    private lateinit var iv_hotel_rupee: ImageView
    private lateinit var tv_hotel_amount: AppCustomTextView
    private lateinit var cv_food_price: CardView
    private lateinit var iv_food_rupee: ImageView
    private lateinit var tv_food_amount: AppCustomTextView
    private lateinit var ll_month: LinearLayout
    private lateinit var tv_month: AppCustomTextView
    var mPopupWindow: PopupWindow? = null
    var conveyancePopupWindow: PopupWindow? = null
    private lateinit var fab: FloatingActionButton
    private lateinit var rv_expense_list: RecyclerView
    private lateinit var ll_conveyence: LinearLayout
    private lateinit var tv_conveyence: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var tv_no_ta: AppCustomTextView
    private lateinit var tv_approved_amount: AppCustomTextView
    private lateinit var tv_total_claim_amount: AppCustomTextView
    private lateinit var tv_approve_amount: AppCustomTextView
    private lateinit var tv_claim_amount: AppCustomTextView
    private lateinit var progressBar_timer: ProgressBar
    private lateinit var tv_year: AppCustomTextView

    private var monthPopup: ListPopupWindow? = null
    private var conveyancePopup: ListPopupWindow? = null
    private var visitId = ""

    private var adapter: ReimbursementExpenseAdapter? = null
    private var model: ReimburesementListExpenseModel? = null
    private val expenseList = ArrayList<ListExpenseTypeModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_reimbursement_list, container, false)
        initView(view)
        //initAdapter(expense_list[i].expense_list_details)
        //initExpenseAdapter(reimbursementResponse.expense_list)

        //showConveyenceTypePopUp()

        getReimbursementListApi("")
        return view
    }

    private fun initView(view: View) {
        fl_travel = view.findViewById(R.id.fl_travel)
        fl_hotel = view.findViewById(R.id.fl_hotel)
        fl_food = view.findViewById(R.id.fl_food)
        cv_travel_price = view.findViewById(R.id.cv_travel_price)
        iv_tavel_rupee = view.findViewById(R.id.iv_tavel_rupee)
        tv_travel_amount = view.findViewById(R.id.tv_travel_amount)
        cv_hotel_price = view.findViewById(R.id.cv_hotel_price)
        iv_hotel_rupee = view.findViewById(R.id.iv_hotel_rupee)
        tv_hotel_amount = view.findViewById(R.id.tv_hotel_amount)
        iv_hotel_rupee = view.findViewById(R.id.iv_hotel_rupee)
        cv_food_price = view.findViewById(R.id.cv_food_price)
        iv_food_rupee = view.findViewById(R.id.iv_food_rupee)
        tv_food_amount = view.findViewById(R.id.tv_food_amount)
        rv_reimbursment_list = view.findViewById(R.id.rv_reimbursment_list)
        rv_reimbursment_list.layoutManager = LinearLayoutManager(mContext)
        ll_month = view.findViewById(R.id.ll_month)
        tv_month = view.findViewById(R.id.tv_month)
        tv_month.text = AppUtils.getCurrentMonth()
        tv_year = view.findViewById(R.id.tv_year)
        tv_year.text = AppUtils.getCurrentYear()
        fab = view.findViewById(R.id.fab)
        rv_expense_list = view.findViewById(R.id.rv_expense_list)
        rv_expense_list.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        ll_conveyence = view.findViewById(R.id.ll_conveyence)
        tv_conveyence = view.findViewById(R.id.tv_conveyence)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        tv_no_data = view.findViewById(R.id.tv_no_data)
        tv_no_ta = view.findViewById(R.id.tv_no_ta)
        tv_approved_amount = view.findViewById(R.id.tv_approved_amount)
        tv_total_claim_amount = view.findViewById(R.id.tv_total_claim_amount)
        tv_approve_amount = view.findViewById(R.id.tv_approve_amount)
        tv_claim_amount = view.findViewById(R.id.tv_claim_amount)
        progressBar_timer = view.findViewById(R.id.progressBar_timer)

        cv_travel_price.isSelected = true
        iv_tavel_rupee.isSelected = true
        tv_travel_amount.isSelected = true

        fl_travel.setOnClickListener(this)
        fl_hotel.setOnClickListener(this)
        fl_food.setOnClickListener(this)
        ll_month.setOnClickListener(this)
        fab.setOnClickListener(this)
        ll_conveyence.setOnClickListener(this)

        rv_reimbursment_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                /*if (dy > 0 || dy < 0 && fab.isShown)
                    fab.hide()*/

                if (dy < 0 && !fab.isShown)
                    fab.show()
                else if (dy > 0 && fab.isShown)
                    fab.hide()
            }


            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                /*if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab.show()*/

                super.onScrollStateChanged(recyclerView!!, newState)
            }
        })


        val expenseModelAll = ListExpenseTypeModel()
        expenseModelAll.expenseId = "0"
        expenseModelAll.expenseType = "All"
        expenseList.add(expenseModelAll)

        val expenseModelLocal = ListExpenseTypeModel()
        expenseModelLocal.expenseId = "1"
        expenseModelLocal.expenseType = "Local"
        expenseList.add(expenseModelLocal)

        val expenseModelOutstation = ListExpenseTypeModel()
        expenseModelOutstation.expenseId = "2"
        expenseModelOutstation.expenseType = "Outstation"
        expenseList.add(expenseModelOutstation)

        visitId = "0"

        tv_conveyence.text = expenseList[0].expenseType

    }

    private fun getReimbursementListApi(message: String) {

        if (!AppUtils.isOnline(mContext)) {
            //(mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            showInternetAlert()
            return
        }

        val month = tv_month.text.toString().trim().substring(0, 3)

        XLog.d("====ReimbursementList Input params======")
        XLog.d("user id===> " + Pref.user_id!!)
        XLog.d("session token===> " + Pref.session_token!!)
        XLog.d("month===> " + AppUtils.getMonthValue(month))
        XLog.d("year===> " + tv_year.text.toString().trim())
        XLog.d("visit id===> $visitId")
        XLog.d("========================================")

        val repository = ReimbursementListRepoProvider.getReimbursementListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getReimbursementList(Pref.user_id!!, Pref.session_token!!, AppUtils.getMonthValue(month), tv_year.text.toString().trim(), visitId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            val reimbursementResponse = result as ReimbursementListResponseModel
                            XLog.d("ReimbursementList Api Response : " + "\n" + "Status=====> " + reimbursementResponse.status + ", Message====> " + reimbursementResponse.message)

                            progress_wheel.stopSpinning()
                            if (reimbursementResponse.status == NetworkConstant.SUCCESS) {

                                if (!TextUtils.isEmpty(reimbursementResponse.total_approved_amount))
                                    tv_approved_amount.text = reimbursementResponse.total_approved_amount

                                if (!TextUtils.isEmpty(reimbursementResponse.total_claim_amount))
                                    tv_total_claim_amount.text = reimbursementResponse.total_claim_amount

                                if (!TextUtils.isEmpty(reimbursementResponse.total_approved_amount))
                                    tv_approve_amount.text = reimbursementResponse.total_approved_amount

                                if (!TextUtils.isEmpty(reimbursementResponse.total_claim_amount))
                                    tv_claim_amount.text = reimbursementResponse.total_claim_amount

                                if (reimbursementResponse.expense_list != null && reimbursementResponse.expense_list?.size!! > 0)
                                    initExpenseAdapter(reimbursementResponse.expense_list, message)
                                else {
                                    //tv_no_data.visibility = View.VISIBLE
                                    tv_no_ta.visibility = View.VISIBLE
                                    rv_reimbursment_list.visibility = View.GONE

                                    tv_approved_amount.text = "0"
                                    tv_total_claim_amount.text = "0"
                                    tv_approve_amount.text = "0"
                                    tv_claim_amount.text = "0"

                                    if (!TextUtils.isEmpty(message))
                                        (mContext as DashboardActivity).showSnackMessage(message)
                                }

                                val getAmountPercentage = (tv_total_claim_amount.text.toString().trim().toFloat().toInt() - tv_approved_amount.text.toString().trim().toFloat().toInt()) / 100
                                progressBar_timer.progress = getAmountPercentage

                            } else {
                                (mContext as DashboardActivity).showSnackMessage(reimbursementResponse.message!!)
                                //tv_no_data.visibility = View.VISIBLE
                                rv_expense_list.visibility = View.GONE
                                tv_no_ta.visibility = View.VISIBLE
                                rv_reimbursment_list.visibility = View.GONE

                                tv_approved_amount.text = "0"
                                tv_total_claim_amount.text = "0"
                                tv_approve_amount.text = "0"
                                tv_claim_amount.text = "0"
                            }
                            BaseActivity.isApiInitiated = false

                        }, { error ->
                            BaseActivity.isApiInitiated = false
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("ReimbursementList Api ERROR: " + error.localizedMessage)
                            rv_expense_list.visibility = View.GONE
                            //tv_no_data.visibility = View.VISIBLE
                            tv_no_ta.visibility = View.VISIBLE
                            rv_reimbursment_list.visibility = View.GONE

                            tv_approved_amount.text = "0"
                            tv_total_claim_amount.text = "0"
                            tv_approve_amount.text = "0"
                            tv_claim_amount.text = "0"
                        })
        )
    }


    private fun showInternetAlert() {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(mContext)

        // Set the alert dialog title
        builder.setTitle("Network Alert!")

        // Display a message on alert dialog
        builder.setMessage("You need to enable the network in order to view reimbursement list")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Ok") { dialog, which ->
            // Do something when user press the positive button
            //Toast.makeText(mContext,"Ok, we change the app background.",Toast.LENGTH_SHORT).show()
            (mContext as DashboardActivity).onBackPressed()
        }


        // Display a negative button on alert dialog
        /*builder.setNegativeButton("No"){dialog,which ->
            Toast.makeText(applicationContext,"You are not agree.",Toast.LENGTH_SHORT).show()
        }*/


        // Display a neutral button on alert dialog
        /*builder.setNeutralButton("Cancel"){_,_ ->
            Toast.makeText(applicationContext,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
        }*/

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        // Display the alert dialog on app interface
        dialog.show()

    }

    private fun initExpenseAdapter(expense_list: ArrayList<ReimbursementListDataModel>?, message: String) {

        /*val list = ArrayList<ReimburesementListExpenseModel>()

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Travel"
        model?.isSelected = true
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "300"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "1,900"
        model?.expense_type = "Food"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)

        model = ReimburesementListExpenseModel()
        model?.amount = "200"
        model?.expense_type = "Hotel"
        model?.isSelected = false
        list.add(model!!)*/

        tv_no_data.visibility = View.GONE
        rv_expense_list.visibility = View.VISIBLE

        expense_list!![0].isSelected = true

        if (expense_list[0].expense_list_details != null && expense_list[0].expense_list_details!!.size > 0)
            initAdapter(expense_list[0].expense_list_details, expense_list[0], message)
        else {
            tv_no_ta.visibility = View.VISIBLE
            rv_reimbursment_list.visibility = View.GONE

            if (!TextUtils.isEmpty(message))
                (mContext as DashboardActivity).showSnackMessage(message)
        }

        adapter = ReimbursementExpenseAdapter(mContext, expense_list, object : ReimbursementExpenseAdapter.OnItemClickListener {
            override fun onItemClick(adapterPosition: Int) {

                for (i in expense_list!!.indices) {
                    Log.e("Reimbursement list", "list position====> $i")
                    Log.e("Reimbursement list", "adapter position====> $adapterPosition")
                    if (i == adapterPosition) {
                        expense_list[i].isSelected = true

                        if (expense_list[i].expense_list_details != null && expense_list[i].expense_list_details!!.size > 0)
                            initAdapter(expense_list[i].expense_list_details, expense_list[i], message)
                        else {
                            tv_no_ta.visibility = View.VISIBLE
                            rv_reimbursment_list.visibility = View.GONE
                        }

                    } else {
                        expense_list[i].isSelected = false
                    }

                    //expense_list[i].isSelected = i == adapterPosition
                }

                adapter?.notifyDataSetChanged()
            }
        })
        rv_expense_list.adapter = adapter
    }

    private fun initAdapter(expense_list_details: ArrayList<ReimbursementListDetailsModel>?, reimbursementListDataModel: ReimbursementListDataModel, message: String) {

        tv_no_ta.visibility = View.GONE
        rv_reimbursment_list.visibility = View.VISIBLE

        rv_reimbursment_list.adapter = ReimbursmentListAdapter(mContext, expense_list_details!!, object : ReimbursmentListAdapter.OnItemClickListener {
            override fun onDeleteClick(adapterPosition: Int) {
                showDeleteAlert(adapterPosition, expense_list_details)
            }

            override fun onEditClick(adapterPosition: Int) {
                (mContext as DashboardActivity).reimbursementSelectPosition = adapterPosition
                (mContext as DashboardActivity).loadFragment(FragType.EditReimbursementFragment, true, reimbursementListDataModel)
            }

            override fun onViewClick(adapterPosition: Int) {
                (mContext as DashboardActivity).reimbursementSelectPosition = adapterPosition
                (mContext as DashboardActivity).loadFragment(FragType.ReimbursementDetailsFragment, true, reimbursementListDataModel)
            }
        })

        if (!TextUtils.isEmpty(message))
            (mContext as DashboardActivity).showSnackMessage(message)

        rv_reimbursment_list.smoothScrollToPosition(0)
        fab.show()
    }

    private fun showDeleteAlert(adapterPosition: Int, expense_list_details: ArrayList<ReimbursementListDetailsModel>?) {

        CommonDialog.getInstance("Delete Alert", "Do you really want to delete this TA?", getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {

                val deletReinbursementInput = AppliedReimbursementDeleteInputModel()
                val reimbursmentTaRow = expense_list_details!![adapterPosition]

                deletReinbursementInput.session_token = Pref.session_token!!
                deletReinbursementInput.user_id = Pref.user_id!!
                deletReinbursementInput.Expense_mapId = reimbursmentTaRow.Expense_mapId!!
                deletReinbursementInput.Subexpense_MapId = reimbursmentTaRow.Subexpense_MapId!!
                deletReinbursementInput.visit_type_id = reimbursmentTaRow.visit_type_id!!
                deletReinbursementInput.date = reimbursmentTaRow.applied_date!!

                appliedReimbursementDeleteApi(deletReinbursementInput)
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }


    private fun appliedReimbursementDeleteApi(deletReinbursementInput: AppliedReimbursementDeleteInputModel) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

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
                                getReimbursementListApi(configResponse.message!!)
                            } else
                                (mContext as DashboardActivity).showSnackMessage(configResponse.message!!)

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("Delete Reimbursement Api ERROR: " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onClick(p0: View?) {

        when (p0?.id) {

            R.id.fl_travel -> {
                cv_travel_price.isSelected = true
                iv_tavel_rupee.isSelected = true
                tv_travel_amount.isSelected = true

                cv_hotel_price.isSelected = false
                iv_hotel_rupee.isSelected = false
                tv_hotel_amount.isSelected = false

                cv_food_price.isSelected = false
                iv_food_rupee.isSelected = false
                tv_food_amount.isSelected = false

                //initAdapter(expense_list[i].expense_list_details)
            }

            R.id.fl_hotel -> {
                cv_travel_price.isSelected = false
                iv_tavel_rupee.isSelected = false
                tv_travel_amount.isSelected = false

                cv_hotel_price.isSelected = true
                iv_hotel_rupee.isSelected = true
                tv_hotel_amount.isSelected = true

                cv_food_price.isSelected = false
                iv_food_rupee.isSelected = false
                tv_food_amount.isSelected = false

                //initAdapter(expense_list[i].expense_list_details)
            }

            R.id.fl_food -> {
                cv_travel_price.isSelected = false
                iv_tavel_rupee.isSelected = false
                tv_travel_amount.isSelected = false

                cv_hotel_price.isSelected = false
                iv_hotel_rupee.isSelected = false
                tv_hotel_amount.isSelected = false

                cv_food_price.isSelected = true
                iv_food_rupee.isSelected = true
                tv_food_amount.isSelected = true

                //initAdapter(expense_list[i].expense_list_details)
            }

            R.id.ll_month -> {

                conveyancePopupWindow?.dismiss()

                /*if (mPopupWindow != null && mPopupWindow?.isShowing!!)
                    mPopupWindow?.dismiss()
                else
                    showMonthPopupWindow()*/

                callMonthPicker()
            }

            R.id.fab -> {

                if (mPopupWindow != null)
                    mPopupWindow?.dismiss()

                conveyancePopupWindow?.dismiss()

                if (!Pref.isAddAttendence)
                    (mContext as DashboardActivity).checkToShowAddAttendanceAlert()
                else
                    (mContext as DashboardActivity).loadFragment(FragType.ReimbursementFragment, true, "")
            }

            R.id.ll_conveyence -> {

                mPopupWindow?.dismiss()

                if (conveyancePopupWindow != null && conveyancePopupWindow!!.isShowing)
                    conveyancePopupWindow?.dismiss()
                else
                    showConveyenceTypePopUp()
            }
        }
    }

    private fun callMonthPicker() {

        //val currentMonthVal = AppUtils.getMonthValue(AppUtils.getCurrentMonth())

        RackMonthPicker(mContext)
                .setLocale(Locale.ENGLISH)
                .setColorTheme(R.color.colorPrimary)
                .setSelectedMonth(AppUtils.getCurrentMonthInNum().toInt() - 1)
                .setSelectedYear(AppUtils.getCurrentYear().toInt())
                .setPositiveButton(DateMonthDialogListener { month, startDate, endDate, year, monthLabel ->
                    tv_month.text = getMonth(month)
                    tv_year.text = year.toString()
                    getReimbursementListApi("")
                })
                .setNegativeButton { dialog ->
                    dialog.dismiss()
                }.show()
    }

    private fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month - 1]
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showConveyenceTypePopUp() {
        /*val conveyenceList = ArrayList<String>(3)
        conveyenceList.add("All")
        conveyenceList.add("Local")
        conveyenceList.add("Outstation")

        conveyancePopup = ListPopupWindow(mContext)
        conveyancePopup?.verticalOffset = resources.getDimensionPixelOffset(R.dimen._minus1sdp)
        conveyancePopup?.anchorView = ll_conveyence
        conveyancePopup?.height = resources.getDimensionPixelOffset(R.dimen._120sdp)
        conveyancePopup?.width = resources.getDimensionPixelOffset(R.dimen._100sdp)
        conveyancePopup?.setDropDownGravity(Gravity.CENTER)
        conveyancePopup?.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                tv_conveyence.text = conveyenceList[p2]
                *//*when (p2) {
                    0 -> {
                        setVisibilityForTravel()
                    }
                    1 -> {
                        setVisibilityForHotel()
                    }
                    2 -> {
                        setVisibilityForFood()
                    }
                }*//*

                conveyancePopup?.dismiss()
                isItemClicked = true
            }
        })
        conveyancePopup?.setAdapter(ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, conveyenceList))
        conveyancePopup?.show()

        conveyancePopup?.setOnDismissListener {
            //conveyancePopup?.dismiss()
            //conveyancePopup = null
            if (!isItemClicked)
                isItemClicked = true
        }*/

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Inflate the custom layout/view
        val customView = inflater!!.inflate(R.layout.dialog_months, null)
        conveyancePopupWindow = PopupWindow(customView, resources.getDimensionPixelOffset(R.dimen._110sdp), resources.getDimensionPixelOffset(R.dimen._100sdp))
        val rv_member_no_list = customView.findViewById(R.id.rv_months) as RecyclerView
        rv_member_no_list.layoutManager = LinearLayoutManager(mContext)

        val et_search = customView.findViewById<AppCustomEditText>(R.id.et_search)
        et_search.visibility = View.GONE

        conveyancePopupWindow?.elevation = 200f

        /*val monthList = ArrayList<String>(3)
        monthList.add("All")
        monthList.add("Local")
        monthList.add("Outstation")*/


        rv_member_no_list.adapter = MonthListAdapter(mContext, expenseList, object : MonthListAdapter.OnItemClickListener {
            override fun onItemClick(adapterPosition: Int) {
                conveyancePopupWindow?.dismiss()
                tv_conveyence.text = expenseList[adapterPosition].expenseType
                visitId = expenseList[adapterPosition].expenseId
                getReimbursementListApi("")
            }
        })

        if (conveyancePopupWindow != null && !conveyancePopupWindow?.isShowing!!) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                conveyancePopupWindow?.showAsDropDown(ll_conveyence, resources.getDimensionPixelOffset(R.dimen._10sdp), 0, Gravity.BOTTOM)
            } else {
                conveyancePopupWindow?.showAsDropDown(ll_conveyence, ll_conveyence.width - conveyancePopupWindow?.width!!, 0)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showMonthPopupWindow() {

        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

        // Inflate the custom layout/view
        val customView = inflater!!.inflate(R.layout.dialog_months, null)
        mPopupWindow = PopupWindow(customView, resources.getDimensionPixelOffset(R.dimen._110sdp), resources.getDimensionPixelOffset(R.dimen._200sdp))
        val rv_member_no_list = customView.findViewById(R.id.rv_months) as RecyclerView
        rv_member_no_list.layoutManager = LinearLayoutManager(mContext)

        mPopupWindow?.elevation = 200f

        val monthList = ArrayList<String>(12)
        monthList.add("January")
        monthList.add("February")
        monthList.add("March")
        monthList.add("April")
        monthList.add("May")
        monthList.add("June")
        monthList.add("July")
        monthList.add("August")
        monthList.add("September")
        monthList.add("October")
        monthList.add("November")
        monthList.add("December")

        rv_member_no_list.adapter = MonthListAdapter(mContext, monthList, object : MonthListAdapter.OnItemClickListener {
            override fun onItemClick(adapterPosition: Int) {
                mPopupWindow?.dismiss()
                tv_month.text = monthList[adapterPosition]

                getReimbursementListApi("")
            }
        })

        if (mPopupWindow != null && !mPopupWindow?.isShowing!!) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mPopupWindow?.showAsDropDown(ll_month, 0, 0, Gravity.BOTTOM)
            } else {
                mPopupWindow?.showAsDropDown(ll_month, ll_month.width - mPopupWindow?.width!!, 0)
            }
        }

        /* monthPopup = ListPopupWindow(mContext)
         monthPopup?.verticalOffset = resources.getDimensionPixelOffset(R.dimen._minus1sdp)
         monthPopup?.anchorView = ll_month
         monthPopup?.height = resources.getDimensionPixelOffset(R.dimen._200sdp)
         monthPopup?.setDropDownGravity(Gravity.CENTER)
         monthPopup?.setOnItemClickListener(object : AdapterView.OnItemClickListener {
             override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                 tv_month.text = monthList[p2]
 //                when (p2) {
 //                    0 -> {
 //                        setVisibilityForTravel()
 //                    }
 //                    1 -> {
 //                        setVisibilityForHotel()
 //                    }
 //                    2 -> {
 //                        setVisibilityForFood()
 //                    }
 //                }

                 monthPopup?.dismiss()
             }
         })
         monthPopup?.setAdapter(ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, monthList))
         monthPopup?.show()*/
    }

    override fun onPause() {
        super.onPause()
        mPopupWindow?.dismiss()
        conveyancePopupWindow?.dismiss()
    }

    fun callApi() {
        getReimbursementListApi("")

        if (!fab.isShown)
            fab.show()
    }

    fun updateFloatingButton() {
        if (!fab.isShown)
            fab.show()
    }
}