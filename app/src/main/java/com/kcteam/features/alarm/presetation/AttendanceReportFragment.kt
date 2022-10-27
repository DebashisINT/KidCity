package com.kcteam.features.alarm.presetation

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.FileProvider
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.uiaction.IntentActionable
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.AppUtils.Companion.getFormattedDateForApi
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.alarm.api.attendance_report_list_api.AttendanceReportRepoProvider
import com.kcteam.features.alarm.api.report_confirm_api.ReviewConfirmRepoProvider
import com.kcteam.features.alarm.model.AttendanceReport
import com.kcteam.features.alarm.model.AttendanceReportDataModel
import com.kcteam.features.alarm.model.ReviewConfirmInputModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.model.alarmconfigmodel.AlarmConfigDataModel
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*

/**
 * Created by Saikat on 20-02-2019.
 */
class AttendanceReportFragment : BaseFragment(), View.OnClickListener/*, DatePickerDialog.OnDateSetListener*/ {

    private lateinit var mContext: Context
    private lateinit var rv_attendance_report_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var iv_check_icon: ImageView
    private lateinit var tv_confirm_btn: AppCustomTextView
    private lateinit var tv_reviewed: AppCustomTextView
    private lateinit var include_confirm_footer: RelativeLayout
    private lateinit var view_report_confirm: View
    private lateinit var ll_attendance_report_main: LinearLayout
    private lateinit var footer_view: View
    private lateinit var tv_pick_date_range: AppCustomTextView
    private lateinit var iv_share: AppCompatImageView

    private var attendance_report_list: ArrayList<AttendanceReport>?= null

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    companion object {

        private var alarm: AlarmConfigDataModel? = null

        fun newInstance(objects: Any): AttendanceReportFragment {
            val fragment = AttendanceReportFragment()

            if (objects is AlarmConfigDataModel)
                alarm = objects
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_attendance_report, container, false)
        initView(view)

        if ((mContext as DashboardActivity).isAttendanceFromAlarm)
            getAttendanceReport("")
        else
            getAttendanceReport(AppUtils.getCurrentDateForShopActi())

        return view
    }

    private fun initView(view: View) {
        rv_attendance_report_list = view.findViewById(R.id.rv_attendance_report_list)
        rv_attendance_report_list.layoutManager = LinearLayoutManager(mContext)
        tv_no_data_available = view.findViewById(R.id.tv_no_data_available)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        iv_check_icon = view.findViewById(R.id.iv_check_icon)
        tv_confirm_btn = view.findViewById(R.id.tv_confirm_btn)
        tv_reviewed = view.findViewById(R.id.tv_reviewed)
        include_confirm_footer = view.findViewById(R.id.include_confirm_footer)
        view_report_confirm = view.findViewById(R.id.view)
        ll_attendance_report_main = view.findViewById(R.id.ll_attendance_report_main)
        footer_view = view.findViewById(R.id.view)
        tv_pick_date_range = view.findViewById(R.id.tv_pick_date_range)
        iv_share = view.findViewById(R.id.iv_share)

        if ((mContext as DashboardActivity).isAttendanceFromAlarm) {
            include_confirm_footer.visibility = View.VISIBLE
            footer_view.visibility = View.VISIBLE
            tv_pick_date_range.visibility = View.GONE
        } else {
            (mContext as DashboardActivity).isConfirmed = true
            include_confirm_footer.visibility = View.GONE
            footer_view.visibility = View.GONE
            tv_pick_date_range.apply {
                visibility = View.VISIBLE
                text = AppUtils.getFormattedDate(myCalendar.time)
            }
        }

        ll_attendance_report_main.setOnClickListener(null)
        iv_check_icon.setOnClickListener(this)
        tv_confirm_btn.setOnClickListener(this)
        tv_reviewed.setOnClickListener(this)
        tv_pick_date_range.setOnClickListener(this)
        iv_share.setOnClickListener(this)
    }

    private fun initAdapter(attendance_report_list: ArrayList<AttendanceReport>) {

        tv_no_data_available.visibility = View.GONE

        rv_attendance_report_list.adapter = AttendanceReportAdapter(mContext, attendance_report_list, object : AttendanceReportAdapter.OnClickListener {
            override fun onCallClick(adapterPosition: Int) {
                if (TextUtils.isEmpty(attendance_report_list[adapterPosition].contact_no) || attendance_report_list[adapterPosition].contact_no.equals("null", ignoreCase = true)
                        || !AppUtils.isValidateMobile(attendance_report_list[adapterPosition].contact_no!!)) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_phn_no_unavailable))
                } else {
                    IntentActionable.initiatePhoneCall(mContext, attendance_report_list[adapterPosition].contact_no)
                }
            }
        })
    }


    private fun getAttendanceReport(date: String) {

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = AttendanceReportRepoProvider.provideAttendanceReportRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getAttendanceReportList(date)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val attendanceList = result as AttendanceReportDataModel
                            when {
                                attendanceList.status == NetworkConstant.SUCCESS -> {
                                    progress_wheel.stopSpinning()
                                    attendance_report_list = attendanceList.attendance_report_list!!
                                    initAdapter(attendanceList.attendance_report_list!!)

                                }
                                attendanceList.status == NetworkConstant.SESSION_MISMATCH -> {
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
                                    startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                    (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                    (mContext as DashboardActivity).finish()
                                }
                                attendanceList.status == NetworkConstant.NO_DATA -> {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)

                                }
                                else -> {
                                    progress_wheel.stopSpinning()
                                    tv_no_data_available.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(attendanceList.message!!)
                                }
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            tv_no_data_available.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.iv_check_icon -> {
                iv_check_icon.isSelected = !iv_check_icon.isSelected
            }

            R.id.tv_confirm_btn -> {
                checkValidation()
            }

            R.id.tv_reviewed -> {
                iv_check_icon.isSelected = !iv_check_icon.isSelected
            }

            R.id.tv_pick_date_range -> {
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.show()
            }

            R.id.iv_share -> {
                if (attendance_report_list == null || attendance_report_list!!.isEmpty()) {
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                    return
                }

                val heading = "ATTENDANCE REPORT DETAILS"
                var pdfBody = "\n\n\nDate: " + tv_pick_date_range.text.toString().trim() + "\n\n\n\n" + getString(R.string.name) +
                        "                                                                        "  +
                        getString(R.string.in_time) + "        " + getString(R.string.status_text) +
                        "\n--------------------------------------------------------------------------------------------------------------------------------\n\n"

                attendance_report_list?.forEach {
                    val name = it.member_name + "\nReport To: " + it.report_to
                    pdfBody += name + "                                      " + it.login_time + "       " + it.status +
                            "\n\n=========================================================\n\n"
                }

                val image = BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher)

                val path = FTStorageUtils.stringToPdf(pdfBody, mContext, "Attendance_report_" +
                        AppUtils.getFormattedDateForApi(myCalendar.time) + "_" + Pref.user_id + ".pdf", image, heading, 3.7f)
                if (!TextUtils.isEmpty(path)) {
                    try {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        val fileUrl = Uri.parse(path)

                        val file = File(fileUrl.path)
                        //val uri = Uri.fromFile(file)
                        //27-09-2021
                        val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
                        shareIntent.type = "image/png"
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                        startActivity(Intent.createChooser(shareIntent, "Share pdf using"));
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else
                    (mContext as DashboardActivity).showSnackMessage("Pdf can not be sent.")
            }
        }
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        tv_pick_date_range.text = AppUtils.getFormattedDate(myCalendar.time)

        getAttendanceReport(getFormattedDateForApi(myCalendar.time))
    }

    /*override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd

        if (dayOfMonth < 10)
            day = "0$dayOfMonth"

        if (dayOfMonthEnd < 10)
            dayEnd = "0$dayOfMonthEnd"

        val fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear + 1).toString() + "") + "-" + year
        val endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd + 1).toString() + "") + "-" + yearEnd

        if (AppUtils.getStrinTODate(endString).before(AppUtils.getStrinTODate(fronString))) {
            (mContext as DashboardActivity).showSnackMessage("Your end date is before start date.")
            return
        }

        val diffInMillis = AppUtils.getStrinTODate(endString).time - AppUtils.getStrinTODate(fronString).time
        if (TimeUnit.MILLISECONDS.toDays(diffInMillis) > 7) {
            (mContext as DashboardActivity).showSnackMessage("Report must be generated for 7 Days")
            return
        }

        val date = day + AppUtils.getDayNumberSuffix(day.toInt()) + FTStorageUtils.formatMonth((++monthOfYear).toString() + "") + " " + year + " To " + dayEnd + AppUtils.getDayNumberSuffix(dayEnd.toInt()) + FTStorageUtils.formatMonth((++monthOfYearEnd).toString() + "") + " " + yearEnd
        tv_pick_date_range.text = date

        getAttendanceReport(AppUtils.convertFromRightToReverseFormat(fronString), AppUtils.convertFromRightToReverseFormat(endString))
    }*/

    private fun checkValidation() {
        if (!iv_check_icon.isSelected)
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.error_check_review))
        else
            callConfirmReviewApi()

    }

    private fun callConfirmReviewApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        try {

            val reviewConfirm = ReviewConfirmInputModel()
            reviewConfirm.session_token = Pref.session_token!!
            reviewConfirm.user_id = Pref.user_id!!
            reviewConfirm.alarm_id = alarm?.id!!
            reviewConfirm.report_id = alarm?.report_id!!

            if (alarm?.alarm_time_mins == "0")
                alarm?.alarm_time_mins = "00"

            reviewConfirm.report_time = alarm?.alarm_time_hours + ":" + alarm?.alarm_time_mins + ":00"
            reviewConfirm.view_time = AppUtils.getCurrentTime()

            val repository = ReviewConfirmRepoProvider.provideReviewConfirmRepository()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.reviewConfirm(reviewConfirm)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as BaseResponse
                                when {
                                    response.status == NetworkConstant.SUCCESS -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                        Handler().postDelayed(Runnable {
                                            (mContext as DashboardActivity).isConfirmed = true
                                            (mContext as DashboardActivity).onBackPressed()
                                        }, 100)

                                    }
                                    response.status == NetworkConstant.SESSION_MISMATCH -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                        startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                        (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                        (mContext as DashboardActivity).finish()
                                    }
                                    else -> {
                                        progress_wheel.stopSpinning()
                                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                    }
                                }

                            }, { error ->
                                progress_wheel.stopSpinning()
                                error.printStackTrace()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }
    }
}