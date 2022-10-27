package com.kcteam.features.chatbot.presentation

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.ShopActivityEntity
import com.kcteam.app.domain.ShopVisitAudioEntity
import com.kcteam.app.domain.ShopVisitImageModelEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.AppUtils.Companion.changeLanguage
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.attendance.api.AttendanceRepositoryProvider
import com.kcteam.features.attendance.model.AttendanceRequest
import com.kcteam.features.attendance.model.AttendanceResponse
import com.kcteam.features.chatbot.model.ChatBotDataModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dashboard.presentation.ReasonDialog
import com.kcteam.features.dashboard.presentation.api.ShopVisitImageUploadRepoProvider
import com.kcteam.features.dashboard.presentation.model.ShopVisitImageUploadInputModel
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopDurationRequestData
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*
import kotlin.collections.ArrayList

class ChatBotFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var rv_chat_bot_list: RecyclerView
    private lateinit var ll_chat_btn: LinearLayout
    private lateinit var tv_attendance: AppCustomTextView
    private lateinit var rl_date: RelativeLayout
    private lateinit var tv_send_btn: AppCustomTextView
    private lateinit var et_date: AppCustomEditText
    private lateinit var tv_visit: AppCustomTextView
    private lateinit var ll_yes_no_btn: LinearLayout
    private lateinit var tv_yes: AppCustomTextView
    private lateinit var tv_no: AppCustomTextView
    private lateinit var ll_visit_btn: LinearLayout
    private lateinit var tv_total_visit: AppCustomTextView
    private lateinit var tv_timelines: AppCustomTextView
    private lateinit var tv_revisit: AppCustomTextView
    private lateinit var tv_visit_sync: AppCustomTextView
    private lateinit var tv_help: AppCustomTextView
    private lateinit var tv_tips: AppCustomTextView
    private lateinit var ll_help_btn: LinearLayout
    private lateinit var tv_share_log: AppCustomTextView
    private lateinit var tv_data_clear: AppCustomTextView
    private lateinit var tv_sync_data: AppCustomTextView
    private lateinit var tv_contact_us: AppCustomTextView
    private lateinit var ll_tips_btn: LinearLayout
    private lateinit var tv_login_tips: AppCustomTextView
    private lateinit var tv_attendance_tips: AppCustomTextView
    private lateinit var tv_visit_tips: AppCustomTextView
    private lateinit var tv_sales_tips: AppCustomTextView
    private lateinit var rl_chat_bot_main: RelativeLayout
    private lateinit var tv_analytics: AppCustomTextView
    private lateinit var tv_reports: AppCustomTextView
    private lateinit var tv_weather: AppCustomTextView
    private lateinit var tv_logout: AppCustomTextView
    private lateinit var ll_attendance: LinearLayout
    private lateinit var ll_visit: LinearLayout
    private lateinit var ll_help: LinearLayout
    private lateinit var ll_tips: LinearLayout
    private lateinit var ll_analytics: LinearLayout
    private lateinit var ll_reports: LinearLayout
    private lateinit var ll_weather: LinearLayout
    private lateinit var ll_logout: LinearLayout
    private lateinit var ll_yes: LinearLayout
    private lateinit var ll_no: LinearLayout
    private lateinit var ll_total_visit: LinearLayout
    private lateinit var ll_timelines: LinearLayout
    private lateinit var ll_revisit: LinearLayout
    private lateinit var ll_visit_sync: LinearLayout
    private lateinit var ll_share_log: LinearLayout
    private lateinit var ll_data_clear: LinearLayout
    private lateinit var ll_sync_data: LinearLayout
    private lateinit var ll_contact_us: LinearLayout
    private lateinit var ll_login_tips: LinearLayout
    private lateinit var ll_attendance_tips: LinearLayout
    private lateinit var ll_visit_tips: LinearLayout
    private lateinit var ll_sales_tips: LinearLayout
    private lateinit var attendance_view: View
    private lateinit var visit_view: View
    private lateinit var visit_tips_view: View
    private lateinit var tts: TextToSpeech
    private lateinit var ll_analytics_details: LinearLayout
    private lateinit var ll_analytics_visit: LinearLayout
    private lateinit var ll_analytics_order: LinearLayout

    private var selectedDate = ""
    private var chatAdapter: ChatBotAdapter?= null
    private var yesStatus = -1
    private var isAttendance = false
    private var i = 0
    var language = ""
    private var reasonDialog: ReasonDialog? = null

    private val chatList: ArrayList<ChatBotDataModel> by lazy {
        ArrayList<ChatBotDataModel>()
    }

    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    companion object {

        fun newInstance(language: Any): ChatBotFragment {
            val fragment = ChatBotFragment()
            val bundle = Bundle()
            bundle.putString("lang", language.toString())
            fragment.arguments = bundle
            return fragment
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        language = arguments?.getString("lang").toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        changeLanguage(mContext, language)
        val view = inflater.inflate(R.layout.fragment_chat_bot, container, false)

        initView(view)
        initClickListener()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            rv_chat_bot_list = findViewById(R.id.rv_chat_bot_list)
            ll_chat_btn = findViewById(R.id.ll_chat_btn)
            tv_attendance = findViewById(R.id.tv_attendance)
            rl_date = findViewById(R.id.rl_date)
            tv_send_btn = findViewById(R.id.tv_send_btn)
            et_date = findViewById(R.id.et_date)
            tv_visit = findViewById(R.id.tv_visit)
            ll_yes_no_btn = findViewById(R.id.ll_yes_no_btn)
            tv_yes = findViewById(R.id.tv_yes)
            tv_no = findViewById(R.id.tv_no)
            ll_visit_btn = findViewById(R.id.ll_visit_btn)
            tv_total_visit = findViewById(R.id.tv_total_visit)
            tv_timelines = findViewById(R.id.tv_timelines)
            tv_revisit = findViewById(R.id.tv_revisit)
            tv_visit_sync = findViewById(R.id.tv_visit_sync)
            tv_help = findViewById(R.id.tv_help)
            tv_tips = findViewById(R.id.tv_tips)
            ll_help_btn = findViewById(R.id.ll_help_btn)
            tv_share_log = findViewById(R.id.tv_share_log)
            tv_data_clear = findViewById(R.id.tv_data_clear)
            tv_sync_data = findViewById(R.id.tv_sync_data)
            tv_contact_us = findViewById(R.id.tv_contact_us)
            ll_tips_btn = findViewById(R.id.ll_tips_btn)
            tv_login_tips = findViewById(R.id.tv_login_tips)
            tv_attendance_tips = findViewById(R.id.tv_attendance_tips)
            tv_visit_tips = findViewById(R.id.tv_visit_tips)
            tv_sales_tips = findViewById(R.id.tv_sales_tips)
            rl_chat_bot_main = findViewById(R.id.rl_chat_bot_main)
            tv_analytics = findViewById(R.id.tv_analytics)
            tv_reports = findViewById(R.id.tv_reports)
            tv_weather = findViewById(R.id.tv_weather)
            tv_logout = findViewById(R.id.tv_logout)
            ll_attendance = findViewById(R.id.ll_attendance)
            ll_visit = findViewById(R.id.ll_visit)
            ll_help = findViewById(R.id.ll_help)
            ll_tips = findViewById(R.id.ll_tips)
            ll_analytics = findViewById(R.id.ll_analytics)
            ll_reports = findViewById(R.id.ll_reports)
            ll_weather = findViewById(R.id.ll_weather)
            ll_logout = findViewById(R.id.ll_logout)
            ll_no = findViewById(R.id.ll_no)
            ll_yes = findViewById(R.id.ll_yes)
            ll_total_visit = findViewById(R.id.ll_total_visit)
            ll_timelines = findViewById(R.id.ll_timelines)
            ll_revisit = findViewById(R.id.ll_revisit)
            ll_visit_sync = findViewById(R.id.ll_visit_sync)
            ll_share_log = findViewById(R.id.ll_share_log)
            ll_data_clear = findViewById(R.id.ll_data_clear)
            ll_sync_data = findViewById(R.id.ll_sync_data)
            ll_contact_us = findViewById(R.id.ll_contact_us)
            ll_login_tips = findViewById(R.id.ll_login_tips)
            ll_attendance_tips = findViewById(R.id.ll_attendance_tips)
            ll_visit_tips = findViewById(R.id.ll_visit_tips)
            ll_sales_tips = findViewById(R.id.ll_sales_tips)
            attendance_view = findViewById(R.id.attendance_view)
            visit_view = findViewById(R.id.visit_view)
            visit_tips_view = findViewById(R.id.visit_tips_view)
            ll_analytics_details = findViewById(R.id.ll_analytics_details)
            ll_analytics_visit = findViewById(R.id.ll_analytics_visit)
            ll_analytics_order = findViewById(R.id.ll_analytics_order)
        }

        et_date.setText(AppUtils.getCurrentDate())

        if (Pref.isAttendanceBotShow) {
            ll_attendance.visibility = View.VISIBLE
            attendance_view.visibility = View.VISIBLE
        }
        else {
            ll_attendance.visibility = View.GONE
            attendance_view.visibility = View.GONE
        }

        if (Pref.isVisitBotShow) {
            ll_visit.visibility = View.VISIBLE
            visit_view.visibility = View.VISIBLE
        }
        else {
            ll_visit.visibility = View.GONE
            visit_view.visibility = View.GONE
        }


        if (Pref.isAttendanceFeatureOnly) {
            ll_visit_tips.visibility = View.GONE
            ll_sales_tips.visibility = View.GONE
            visit_tips_view.visibility = View.GONE
        }
        else {
            ll_visit_tips.visibility = View.VISIBLE
            ll_sales_tips.visibility = View.VISIBLE
            visit_tips_view.visibility = View.VISIBLE
        }

        tv_visit_tips.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_sales_tips.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_total_visit.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_timelines.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_revisit.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_visit_sync.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_share_log.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_data_clear.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_sync_data.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_contact_us.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_login_tips.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")
        tv_attendance_tips.setCustomFont(mContext, "fonts/Roboto-Medium.ttf")

        val linearLayoutManager = LinearLayoutManager(mContext)
        //linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        rv_chat_bot_list.layoutManager = linearLayoutManager
        chatAdapter = ChatBotAdapter(mContext) { it, isAudioPlaying ->
            chatAdapter?.notifyDataSetChanged()
            if (isAudioPlaying) {
                val speechStatus = tts.speak(it, TextToSpeech.QUEUE_FLUSH, null)
                if (speechStatus == TextToSpeech.ERROR)
                    Log.e("ChatBot", "TTS error in converting Text to Speech!")
            }
            else
                tts.stop()
        }
        rv_chat_bot_list.adapter = chatAdapter

        tts = TextToSpeech(mContext, TextToSpeech.OnInitListener { status ->
            if (status == TextToSpeech.SUCCESS) {
                val ttsLang = tts.setLanguage(Locale.getDefault())

                if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED)
                    Log.e("Chatbot", "TTS Language is not supported!")
                else
                    Log.e("Chatbot", "TTS Language Supported.")

                Log.e("Chatbot", "TTS Initialization success.")
            } else
                Log.e("Chatbot", "TTS Initialization failed!")
        })


        updateList(Pref.chatBotMsg, "bot", true, false)

        Handler().postDelayed(Runnable {
            updateList(getString(R.string.please_select_to_started), "bot", true, false)
        }, 500)
    }

    private fun initClickListener() {
        ll_attendance.setOnClickListener(this)
        tv_send_btn.setOnClickListener(this)
        et_date.setOnClickListener(this)
        ll_yes.setOnClickListener(this)
        ll_no.setOnClickListener(this)
        ll_total_visit.setOnClickListener(this)
        ll_timelines.setOnClickListener(this)
        ll_revisit.setOnClickListener(this)
        ll_visit_sync.setOnClickListener(this)
        ll_visit.setOnClickListener(this)
        ll_help.setOnClickListener(this)
        ll_tips.setOnClickListener(this)
        ll_share_log.setOnClickListener(this)
        ll_data_clear.setOnClickListener(this)
        ll_sync_data.setOnClickListener(this)
        ll_contact_us.setOnClickListener(this)
        ll_login_tips.setOnClickListener(this)
        ll_attendance_tips.setOnClickListener(this)
        ll_visit_tips.setOnClickListener(this)
        ll_sales_tips.setOnClickListener(this)
        rl_chat_bot_main.setOnClickListener(null)
        ll_analytics.setOnClickListener(this)
        ll_reports.setOnClickListener(this)
        ll_weather.setOnClickListener(this)
        ll_logout.setOnClickListener(this)
        ll_analytics_visit.setOnClickListener(this)
        ll_analytics_order.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_send_btn -> {
                updateList(et_date.text.toString().trim(), "user", true, false)

                changeLanguage(mContext, language)
                if (AppUtils.getCurrentDateFormatInTa(et_date.text.toString().trim()) >= AppUtils.getCurrentDateForShopActi()) {
                    Handler().postDelayed(Runnable {
                        updateList(getString(R.string.wrong_date), "bot", true, false)
                    }, 500)

                    Handler().postDelayed(Runnable {
                        updateList(getString(R.string.can_i_help), "bot", true, false)
                        yesStatus = 1
                        ll_yes_no_btn.visibility = View.VISIBLE
                        ll_chat_btn.visibility = View.GONE
                        rl_date.visibility = View.GONE
                        ll_visit_btn.visibility = View.GONE
                        ll_help_btn.visibility = View.GONE
                        ll_tips_btn.visibility = View.GONE
                    }, 1000)
                }
                else {
                    if (isAttendance)
                        getAttendance()
                    else {
                        val list = AppDatabase.getDBInstance()?.shopActivityDao()?.getDurationCalculatedVisitedShopForADay(
                                AppUtils.getCurrentDateFormatInTa(et_date.text.toString().trim()), true)

                        var count = 0
                        list?.let {
                            count = it.size
                        }

                        updateList(getString(R.string.your_total_visit) + et_date.text.toString().trim() + getString(R.string.is_with_space) + count + ".", "bot", true, false)

                        Handler().postDelayed(Runnable {
                            updateList(getString(R.string.want_to_see_visit_report), "bot", true, false)
                            yesStatus = 2
                            ll_yes_no_btn.visibility = View.VISIBLE
                            ll_chat_btn.visibility = View.GONE
                            rl_date.visibility = View.GONE
                            ll_visit_btn.visibility = View.GONE
                            ll_help_btn.visibility = View.GONE
                            ll_tips_btn.visibility = View.GONE
                        }, 500)
                    }
                }

            }

            R.id.ll_attendance -> {
                isAttendance = true
                ll_chat_btn.visibility = View.GONE
                rl_date.visibility = View.VISIBLE
                updateList(getString(R.string.check_attendance), "user", true, false)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.enter_date_for_attendance), "bot", true, false)
                }, 500)
            }

            R.id.et_date -> {
                changeLanguage(mContext, "en")
                val datePicker = android.app.DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))

                //datePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                val cal = Calendar.getInstance(Locale.ENGLISH)
                cal.add(Calendar.DATE, -1)
                datePicker.datePicker.maxDate = cal.timeInMillis



                datePicker.show()
            }

            R.id.ll_yes -> {
                if (yesStatus == 0) {
                    changeLanguage(mContext,"en")
                    (mContext as DashboardActivity).isChatBotAttendance = true
                    (mContext as DashboardActivity).loadFragment(FragType.AttendanceFragment, true, "")
                }
                else if (yesStatus == 1) {
                    updateList(getString(R.string.yes), "user", true, false)

                    Handler().postDelayed(Runnable {
                        updateList(getString(R.string.please_select_to_started), "bot", true, false)
                        ll_yes_no_btn.visibility = View.GONE
                        ll_chat_btn.visibility = View.VISIBLE
                        rl_date.visibility = View.GONE
                        ll_visit_btn.visibility = View.GONE
                        ll_help_btn.visibility = View.GONE
                        ll_tips_btn.visibility = View.GONE
                    }, 300)
                }
                else if (yesStatus == 2) {
                    changeLanguage(mContext,"en")
                    (mContext as DashboardActivity).loadFragment(FragType.AverageShopFragment, true, "")
                }
                else if (yesStatus == 3) {
                    updateList(getString(R.string.yes), "user", true, false)

                    Handler().postDelayed(Runnable {
                        updateList(getString(R.string.please_select_to_started), "bot", true, false)
                        ll_yes_no_btn.visibility = View.GONE
                        ll_chat_btn.visibility = View.GONE
                        rl_date.visibility = View.GONE
                        ll_visit_btn.visibility = View.VISIBLE
                        ll_help_btn.visibility = View.GONE
                        ll_tips_btn.visibility = View.GONE
                    }, 300)
                }
                else if (yesStatus == 4) {
                    (mContext as DashboardActivity).apply {
                        if (AppUtils.isOnline(mContext)) {
                            isClearData = true
                            changeLanguage(mContext,"en")
                            loadFragment(FragType.LogoutSyncFragment, false, "")
                        } else
                            updateList(getString(R.string.no_internet), "user", true, false)
                    }
                }
                else if (yesStatus == 5) {
                    (mContext as DashboardActivity).apply {
                        if (AppUtils.isOnline(mContext)) {
                            changeLanguage(mContext,"en")
                            loadFragment(FragType.LogoutSyncFragment, true, "")
                        }
                        else
                            updateList(getString(R.string.no_internet), "user", true, false)
                    }
                }
            }

            R.id.ll_no -> {
                updateList(getString(R.string.no), "user", true, false)

                Handler().postDelayed(Runnable {
                    if (yesStatus == 2) {
                        updateList(getString(R.string.thanks_want_all_visit), "bot", true, false)
                        ll_yes_no_btn.visibility = View.VISIBLE
                        ll_chat_btn.visibility = View.GONE
                        rl_date.visibility = View.GONE
                        ll_visit_btn.visibility = View.GONE
                        ll_help_btn.visibility = View.GONE
                        ll_tips_btn.visibility = View.GONE
                        yesStatus = 3
                    }
                    else if (yesStatus == 4) {
                        updateList(getString(R.string.can_i_help), "bot", true, false)
                        yesStatus = 1
                        ll_yes_no_btn.visibility = View.VISIBLE
                        ll_chat_btn.visibility = View.GONE
                        rl_date.visibility = View.GONE
                        ll_visit_btn.visibility = View.GONE
                        ll_help_btn.visibility = View.GONE
                        ll_tips_btn.visibility = View.GONE
                    }
                    else if (yesStatus == 5) {
                        updateList(getString(R.string.thanks_want_all_options), "bot", true, false)
                        yesStatus = 1
                        ll_yes_no_btn.visibility = View.VISIBLE
                        ll_chat_btn.visibility = View.GONE
                        rl_date.visibility = View.GONE
                        ll_visit_btn.visibility = View.GONE
                        ll_help_btn.visibility = View.GONE
                        ll_tips_btn.visibility = View.GONE
                    }
                    else {
                        updateList(getString(R.string.have_grt_dat), "bot", true, false)
                        ll_yes_no_btn.visibility = View.GONE
                        ll_chat_btn.visibility = View.GONE
                        rl_date.visibility = View.GONE
                        ll_visit_btn.visibility = View.GONE
                        ll_help_btn.visibility = View.GONE
                        ll_tips_btn.visibility = View.GONE

                        Handler().postDelayed(Runnable {
                            (mContext as DashboardActivity).onBackPressed()
                        }, 500)
                    }
                }, 300)
            }

            R.id.ll_total_visit -> {
                updateList(getString(R.string.check_total_visit), "user", true, false)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.enter_date_check_total_visit), "bot", true, false)
                    ll_yes_no_btn.visibility = View.GONE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.VISIBLE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.GONE
                }, 300)
            }

            R.id.ll_timelines -> {
                changeLanguage(mContext,"en")
                (mContext as DashboardActivity).isMemberMap = true
                if (!Pref.willTimelineWithFixedLocationShow)
                    (mContext as DashboardActivity).loadFragment(FragType.OrderhistoryFragment, true, "")
                else
                    (mContext as DashboardActivity).loadFragment(FragType.TimeLineFragment, true, "")
            }

            R.id.ll_revisit -> {
                changeLanguage(mContext,"en")
                (mContext as DashboardActivity).isChatBotLocalShop = true
                (mContext as DashboardActivity).loadFragment(FragType.LocalShopListFragment, true, "")
            }

            R.id.ll_visit_sync -> {
                changeLanguage(mContext,"en")
                try {
                    val shopActivityList = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
                    for (i in shopActivityList.indices) {
                        if (!shopActivityList[i].isDurationCalculated && shopActivityList[i].startTimeStamp != "0") {
                            Pref.durationCompletedShopId = shopActivityList[i].shopid!!
                            val endTimeStamp = System.currentTimeMillis().toString()
                            val totalMinute = AppUtils.getMinuteFromTimeStamp(shopActivityList[i].startTimeStamp, endTimeStamp)
                            val duration = AppUtils.getTimeFromTimeSpan(shopActivityList[i].startTimeStamp, endTimeStamp)

                            if (!Pref.isMultipleVisitEnable) {
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivityList[i].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi())
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivityList[i].shopid!!, duration, AppUtils.getCurrentDateForShopActi())
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                            }
                            else {
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateTotalMinuteForDayOfShop(shopActivityList[i].shopid!!, totalMinute, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateEndTimeOfShop(endTimeStamp, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateTimeDurationForDayOfShop(shopActivityList[i].shopid!!, duration, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateDurationAvailable(true, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateIsUploaded(false, shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                            }
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateOutTime(AppUtils.getCurrentTimeWithMeredian(), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                            AppDatabase.getDBInstance()!!.shopActivityDao().updateOutLocation(LocationWizard.getNewLocationName(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)

                            val netStatus = if (AppUtils.isOnline(mContext))
                                "Online"
                            else
                                "Offline"

                            val netType = if (AppUtils.getNetworkType(mContext).equals("wifi", ignoreCase = true))
                                AppUtils.getNetworkType(mContext)
                            else
                                "Mobile ${AppUtils.mobNetType(mContext)}"

                            if (!Pref.isMultipleVisitEnable) {
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                                        AppUtils.getBatteryPercentage(mContext).toString(), netStatus, netType.toString(), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi())
                            }
                            else {
                                AppDatabase.getDBInstance()!!.shopActivityDao().updateDeviceStatusReason(AppUtils.getDeviceName(), AppUtils.getAndroidVersion(),
                                        AppUtils.getBatteryPercentage(mContext).toString(), netStatus, netType.toString(), shopActivityList[i].shopid!!, AppUtils.getCurrentDateForShopActi(), shopActivityList[i].startTimeStamp)
                            }

                            if (Pref.willShowShopVisitReason && totalMinute.toInt() <= Pref.minVisitDurationSpentTime.toInt()) {
                                Pref.isShowShopVisitReason = true
                                showRevisitReasonDialog(shopActivityList[i].startTimeStamp)
                            }

//                            AppUtils.isShopVisited = false
                            Pref.isShopVisited=false
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                if (!Pref.isShowShopVisitReason) {
                    changeLanguage(mContext, language)
                    callShopDurationApi()
                }
            }

            R.id.ll_visit -> {
                isAttendance = false
                updateList(getString(R.string.only_visit), "user", true, false)

                Handler().postDelayed({
                    updateList(getString(R.string.please_select_to_started), "bot", true, false)
                    ll_yes_no_btn.visibility = View.GONE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.VISIBLE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.GONE
                }, 300)
            }

            R.id.ll_help -> {
                updateList(getString(R.string.help), "user", true, false)

                Handler().postDelayed({
                    updateList(getString(R.string.please_select_to_started), "bot", true, false)
                    ll_yes_no_btn.visibility = View.GONE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.VISIBLE
                    ll_tips_btn.visibility = View.GONE
                }, 300)
            }

            R.id.ll_tips -> {
                updateList(getString(R.string.tips), "user", true, false)

                Handler().postDelayed({
                    updateList(getString(R.string.please_select_to_started), "bot", true, false)
                    ll_yes_no_btn.visibility = View.GONE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.VISIBLE
                }, 300)
            }

            R.id.ll_share_log -> {
                (mContext as DashboardActivity).openShareIntents()
            }

            R.id.ll_data_clear -> {
                updateList(getString(R.string.data_clr_alert), "bot", true, false)

                yesStatus = 4
                ll_yes_no_btn.visibility = View.VISIBLE
                ll_chat_btn.visibility = View.GONE
                rl_date.visibility = View.GONE
                ll_visit_btn.visibility = View.GONE
                ll_help_btn.visibility = View.GONE
                ll_tips_btn.visibility = View.GONE
            }

            R.id.ll_sync_data -> {
                (mContext as DashboardActivity).apply {
                    if (AppUtils.isOnline(mContext)) {
                        changeLanguage(mContext,"en")
                        loadFragment(FragType.LogoutSyncFragment, true, "")
                    } else
                        updateList(getString(R.string.no_internet), "user", true, false)
                }
            }

            R.id.ll_contact_us -> {
                updateList(getString(R.string.reach_us) + ".", "user", true, false)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.write_us) + Pref.contactMail + ".", "bot", true, false)
                }, 300)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.can_i_help), "bot", true, false)
                    yesStatus = 1
                    ll_yes_no_btn.visibility = View.VISIBLE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.GONE
                }, 500)
            }

            R.id.ll_login_tips -> {
                updateList(getString(R.string.login), "user", true, false)
                /*val loginMsg = "1. Must have good internet while login otherwise below message appears:\n'Internet is disconnected," +
                        "you can not login, please switch on the internet.'\n\n2. While login you make sure that the Login ID & Password " +
                        "you have entered is proper otherwise below message appears:\n'No Data Found.'\n\n3. While Login with proper " +
                        "Login ID & Password, if the following message appears:\n'You IMEI is not authorised. Please connect with administrator.'\n" +
                        "Then please call to your Organization Head to inform this & once he authorized, you will be able to login successfully." +
                        "\n\n4. Handset Changed? Please talk to Organization Head about this, they will authorize this & you will be able to " +
                        "login successfully in your new handset."*/

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.login_msg)/*loginMsg*/, "bot", false, true)
                }, 500)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.can_i_help), "bot", false, false)
                    yesStatus = 1
                    ll_yes_no_btn.visibility = View.VISIBLE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.GONE
                }, 1000)
            }

            R.id.ll_attendance_tips -> {
                updateList(getString(R.string.attendance), "user", true, false)
                /*val attendanceMsg = "1. Slow Login?\n\na.If last day logout not done in APP, then it will first logout & " +
                        "then login. So, please logout everyday once work in app completed.\n\nb. If you have clear data in APP, then it" +
                        "will sync all data into the handset so that offline work in the APP to be possible. Hence, it will take some time.\n\n" +
                        "c. Important is stable internet connectivity to login faster. Check the internet please.\n\n2. After login what to do?" +
                        "\n\na. At Work: It will mark you present for the day with your In Time. You need to select the type of work also.\n" +
                        "***Please look into Applicable In Time which is set as per your Organization policy.\n\nb. On Leave: It will mark you " +
                        "Leave for the day with the type of leave you selected. You have to select Leave From & To date & can put Remarks.\n\n" +
                        "3. Wish to see Attendance & Leave Reports?\nFrom top left select option to open Menu, then select 'View attendance' or" +
                        " otherwise from the Dashboard, select 'Attendance' displayed at top.\n\n4. Wish to change Password?\nAs per " +
                        "your Organisation policy, you may have to request Organization head to do this for you. If they allow you " +
                        "to do this by yourself, you may open menu options & select 'Change Password' to do the same.\n\n5. After " +
                        "Login & Attendance, wish to apply leave for future date? From menu option you will have option 'Apply Leave' " +
                        "to do the same."*/

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.attendance_msg)/*attendanceMsg*/, "bot", false, true)
                }, 500)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.can_i_help), "bot", false, false)
                    yesStatus = 1
                    ll_yes_no_btn.visibility = View.VISIBLE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.GONE
                }, 1000)
            }

            R.id.ll_visit_tips -> {
                updateList(getString(R.string.visit), "user", true, false)
                /*val visitMsg = "1. Wish to do a New Visit?\nJust Click on (+) floating icon after successful attendance. " +
                        "Your current location to be traced. Visit Data need to enter & save.\n\n2. Wish to do Revisit?\n\n" +
                        "a. If you are at nearby location of the party you wish to visit, automatically push notification at top to" +
                        " be displayed & ask from you Revisit. Just select 'Yes' & put the neccessary details to complete Revisit.\n\n" +
                        "b. If you are not getting notification due to GPS issue, you may open left side menu option & check from " +
                        "'Nearby' feature to get the list & from there you can Revisit.\n\nc. If you are not getting the list of Party " +
                        "for Revisit, talk to your Supervisor &/or Organisation head to help you out by increasing GPS value from portal." +
                        "\n\n3. Wish to see visit reports?\nFrom the dashboard select 'Total Visits.'\n\n4. Wish to see the time you spent" +
                        " in each visits?\nFrom the dashboard select 'Visit Duration'.\n\n5. If you have Order Booking Facility, wish to see" +
                        " the orders you booked?\nFrom the Dashboard select 'Total Order(s).'\n\n6. Wish to see the places you move " +
                        "today with Timeline?\n\nFrom the Dashboard select 'Timeline'. You can view past days data also by selecting the " +
                        "different date."*/

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.visit_msg)/*visitMsg*/, "bot", false, true)
                }, 500)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.can_i_help), "bot", false, false)
                    yesStatus = 1
                    ll_yes_no_btn.visibility = View.VISIBLE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.GONE
                }, 1000)
            }

            R.id.ll_sales_tips -> {
                updateList(getString(R.string.sales), "user", true, false)
                /*val salesMsg = "1. How to take Orders?\n\na. If you are doing Visit or Revisit to any party, once visit/revisit " +
                        "confirms by you, app will itself give you option to Book Order. You need to Select Items, Qty, Rate & can submit the " +
                        "order.\n\nb. Also from Dashboard, click on 'Party(s)' to get the list, from their you may select 'Order' to" +
                        " book this. It will show you existing order list of the party & one floating icon with (+) sign clicking " +
                        "on which you can book orders.\n\n2. Wish to see order history?\nFrom the Dashboard click on 'Orders', it " +
                        "will give you all the details & you can see upto Items, Qty, Rate level details.\n\n3. Orders not showing " +
                        "Rate or unable to enter Rate?\n\nTalk to your reporting head/Organization head to get this."*/

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.sale_msg)/*salesMsg*/, "bot", false, true)
                }, 500)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.can_i_help), "bot", false, false)
                    yesStatus = 1
                    ll_yes_no_btn.visibility = View.VISIBLE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.GONE
                }, 1000)
            }

            R.id.ll_analytics -> {
                updateList(getString(R.string.analytics), "user", false, false)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.please_select_to_started), "bot", false, false)
                    ll_analytics_details.visibility = View.VISIBLE
                    ll_chat_btn.visibility = View.GONE
                }, 500)
            }

            R.id.ll_reports -> {
                changeLanguage(mContext,"en")
                (mContext as DashboardActivity).loadFragment(FragType.ReportsFragment, true, "")
            }

            R.id.ll_weather -> {
                changeLanguage(mContext,"en")
                (mContext as DashboardActivity).isWeatherFromDrawer = false
                (mContext as DashboardActivity).loadFragment(FragType.WeatherFragment, true, "")
            }

            R.id.ll_logout -> {
                updateList(getString(R.string.logout), "user", true, false)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.r_u_sure), "bot", false, false)
                    yesStatus = 5
                    ll_yes_no_btn.visibility = View.VISIBLE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.GONE
                }, 500)
            }

            R.id.ll_analytics_visit -> {
                changeLanguage(mContext,"en")
                (mContext as DashboardActivity).loadFragment(FragType.ChatBotShopListFragment, true, true)
            }

            R.id.ll_analytics_order -> {
                changeLanguage(mContext,"en")
                (mContext as DashboardActivity).loadFragment(FragType.ChatBotShopListFragment, true, false)
            }
        }
    }

    private fun showRevisitReasonDialog(startTimeStamp: String) {
        val shop = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(Pref.durationCompletedShopId)
        reasonDialog = ReasonDialog.getInstance(shop?.shopName!!, "You are revisiting ${Pref.shopText} but the " +
                "duration spent is less than ${Pref.minVisitDurationSpentTime} minutes. Please write the reason below.", "") {
            reasonDialog?.dismiss()
            Pref.isShowShopVisitReason = false

            if (!Pref.isMultipleVisitEnable)
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEarlyRevisitReason(it, Pref.durationCompletedShopId, AppUtils.getCurrentDateForShopActi())
            else
                AppDatabase.getDBInstance()!!.shopActivityDao().updateEarlyRevisitReason(it, Pref.durationCompletedShopId, AppUtils.getCurrentDateForShopActi(), startTimeStamp)

            changeLanguage(mContext, language)
            callShopDurationApi()
        }
        reasonDialog?.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun updateList(msg: String, userType: String, isListScroll: Boolean, isMicShow: Boolean) {
        if (Pref.isVoiceEnabledForHelpAndTipsInBot)
            chatList.add(ChatBotDataModel(msg, userType, isMicShow, false))
        else
            chatList.add(ChatBotDataModel(msg, userType, false, false))
        chatAdapter?.refreshList(chatList)

        //if(isListScroll)
            rv_chat_bot_list.scrollToPosition(chatList.size - 1)
    }

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        et_date.setText(AppUtils.changeToCurrentDateFormat(myCalendar.time))
        selectedDate = AppUtils.getAttendanceFormattedDateForApi(myCalendar.time)
    }

    private fun getAttendance() {
        if (!AppUtils.isOnline(mContext)) {
            updateList(getString(R.string.no_internet), "bot", true, false)
            return
        }

        updateList(getString(R.string.typing), "bot", true, false)

        changeLanguage(mContext,"en")
        val attendanceReq = AttendanceRequest()
        attendanceReq.apply {
            user_id = Pref.user_id
            session_token = Pref.session_token
            start_date = selectedDate
            end_date = selectedDate
        }

        val repository = AttendanceRepositoryProvider.provideAttendanceRepository()
        BaseActivity.compositeDisposable.add(
                repository.getAttendanceList(attendanceReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val attendanceList = result as AttendanceResponse
                            changeLanguage(mContext,language)
                            when (attendanceList.status) {
                                NetworkConstant.SUCCESS -> {
                                    Handler().postDelayed(Runnable {
                                        chatList.removeAt(chatList.size - 1)
                                        var msg = ""
                                        msg = if (attendanceList.shop_list?.get(0)?.Isonleave?.equals("true", ignoreCase = true)!!)
                                            getString(R.string.leave_status)
                                        else
                                            getString(R.string.present_status)
                                        updateList(msg, "bot", true, false)
                                    }, 500)

                                    Handler().postDelayed(Runnable {
                                        updateList(getString(R.string.want_to_see_attendance), "bot", true, false)
                                        yesStatus = 0
                                        ll_yes_no_btn.visibility = View.VISIBLE
                                        ll_chat_btn.visibility = View.GONE
                                        rl_date.visibility = View.GONE
                                        ll_visit_btn.visibility = View.GONE
                                        ll_help_btn.visibility = View.GONE
                                        ll_tips_btn.visibility = View.GONE
                                    }, 1000)

                                }
                                NetworkConstant.NO_DATA -> {
                                    Handler().postDelayed(Runnable {
                                        chatList.removeAt(chatList.size - 1)
                                        updateList(getString(R.string.absent_status), "bot", true, false)
                                    }, 500)

                                    Handler().postDelayed(Runnable {
                                        updateList(getString(R.string.want_to_see_attendance), "bot", true, false)
                                        yesStatus = 0
                                        ll_yes_no_btn.visibility = View.VISIBLE
                                        ll_chat_btn.visibility = View.GONE
                                        rl_date.visibility = View.GONE
                                        ll_visit_btn.visibility = View.GONE
                                        ll_help_btn.visibility = View.GONE
                                        ll_tips_btn.visibility = View.GONE
                                    }, 1000)
                                }
                                else -> {
                                    Handler().postDelayed(Runnable {
                                        chatList.removeAt(chatList.size - 1)
                                        updateList(getString(R.string.can_not_get_attendance_status), "bot", true, false)
                                    }, 500)
                                }
                            }

                        }, { error ->
                            error.printStackTrace()
                            changeLanguage(mContext,language)
                            Handler().postDelayed(Runnable {
                                chatList.removeAt(chatList.size - 1)
                                updateList(getString(R.string.can_not_get_attendance_status), "bot", true, false)
                            }, 500)
                        })
        )
    }

    private fun callShopDurationApi() {

        if (!AppUtils.isOnline(mContext)) {
            updateList(getString(R.string.no_internet), "bot", true, false)
            return
        }

        XLog.d("callShopDurationApi : ENTER")

        Log.e("ChatBot", "isShopActivityUpdating=============> ${BaseActivity.isShopActivityUpdating}")

        if (BaseActivity.isShopActivityUpdating) {
            updateList(getString(R.string.visit_already_syncing), "bot", true, false)

            Handler().postDelayed(Runnable {
                updateList(getString(R.string.can_i_help), "bot", true, false)
                yesStatus = 1
                ll_yes_no_btn.visibility = View.VISIBLE
                ll_chat_btn.visibility = View.GONE
                rl_date.visibility = View.GONE
                ll_visit_btn.visibility = View.GONE
                ll_help_btn.visibility = View.GONE
                ll_tips_btn.visibility = View.GONE
            }, 500)

            return
        }

        /* Get all the shop list that has been synched successfully*/
        val syncedShopList = AppDatabase.getDBInstance()!!.addShopEntryDao().getUnSyncedShops(true)
        if (syncedShopList.isEmpty()) {
            updateList(getString(R.string.sync_done), "bot", true, false)

            Handler().postDelayed(Runnable {
                updateList(getString(R.string.can_i_help), "bot", true, false)
                yesStatus = 1
                ll_yes_no_btn.visibility = View.VISIBLE
                ll_chat_btn.visibility = View.GONE
                rl_date.visibility = View.GONE
                ll_visit_btn.visibility = View.GONE
                ll_help_btn.visibility = View.GONE
                ll_tips_btn.visibility = View.GONE
            }, 500)

            return
        }

        BaseActivity.isShopActivityUpdating = true

        updateList(getString(R.string.typing), "bot", true, false)

        val shopDataList: MutableList<ShopDurationRequestData> = ArrayList()
        val syncedShop = ArrayList<ShopActivityEntity>()

        for (k in 0 until syncedShopList.size) {

            if (!Pref.isMultipleVisitEnable) {
                /* Get shop activity that has completed time duration calculation*/
                val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(syncedShopList[k].shop_id, true,
                        false)
                if (shopActivity == null) {

                    val shop_activity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShop(syncedShopList[k].shop_id,
                            true, true)
                    if (shop_activity != null)
                        syncedShop.add(shop_activity)

                } else {
                    val shopDurationData = ShopDurationRequestData()
                    shopDurationData.shop_id = shopActivity.shopid
                    shopDurationData.spent_duration = shopActivity.duration_spent
                    shopDurationData.visited_date = shopActivity.visited_date
                    shopDurationData.visited_time = shopActivity.visited_date
                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid) != null)
                        shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shopActivity.shopid).totalVisitCount
                    else
                        shopDurationData.total_visit_count = "1"

                    if (TextUtils.isEmpty(shopActivity.distance_travelled))
                        shopActivity.distance_travelled = "0.0"
                    shopDurationData.distance_travelled = shopActivity.distance_travelled

                    if (!TextUtils.isEmpty(shopActivity.feedback))
                        shopDurationData.feedback = shopActivity.feedback
                    else
                        shopDurationData.feedback = ""

                    shopDurationData.isFirstShopVisited = shopActivity.isFirstShopVisited
                    shopDurationData.distanceFromHomeLoc = shopActivity.distance_from_home_loc

                    shopDurationData.next_visit_date = shopActivity.next_visit_date

                    if (!TextUtils.isEmpty(shopActivity.early_revisit_reason))
                        shopDurationData.early_revisit_reason = shopActivity.early_revisit_reason
                    else
                        shopDurationData.early_revisit_reason = ""

                    shopDurationData.device_model = shopActivity.device_model
                    shopDurationData.android_version = shopActivity.android_version
                    shopDurationData.battery = shopActivity.battery
                    shopDurationData.net_status = shopActivity.net_status
                    shopDurationData.net_type = shopActivity.net_type
                    shopDurationData.in_time = shopActivity.in_time
                    shopDurationData.out_time = shopActivity.out_time
                    shopDurationData.start_timestamp = shopActivity.startTimeStamp
                    shopDurationData.in_location = shopActivity.in_loc
                    shopDurationData.out_location = shopActivity.out_loc

                    shopDurationData.shop_revisit_uniqKey = shopActivity.shop_revisit_uniqKey!!
                    /*10-12-2021*/
                    shopDurationData.updated_by = Pref.user_id
                    try {
                        shopDurationData.updated_on = shopActivity.updated_on!!
                    }
                    catch (ex:Exception){
                        shopDurationData.updated_on = ""
                    }

                    if (!TextUtils.isEmpty(shopActivity.pros_id!!))
                        shopDurationData.pros_id = shopActivity.pros_id!!
                    else
                        shopDurationData.pros_id = ""

                    if (!TextUtils.isEmpty(shopActivity.agency_name!!))
                        shopDurationData.agency_name =shopActivity.agency_name!!
                    else
                        shopDurationData.agency_name = ""

                    if (!TextUtils.isEmpty(shopActivity.approximate_1st_billing_value))
                        shopDurationData.approximate_1st_billing_value = shopActivity.approximate_1st_billing_value!!
                    else
                        shopDurationData.approximate_1st_billing_value = ""
                    //duration garbage fix
                    try{
                        if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                        {
                            shopDurationData.spent_duration="00:00:10"
                        }
                    }catch (ex:Exception){
                        shopDurationData.spent_duration="00:00:10"
                    }
                    shopDataList.add(shopDurationData)

                    XLog.d("====SYNC VISITED SHOP DATA (ChatBot)====")
                    XLog.d("SHOP ID======> " + shopDurationData.shop_id)
                    XLog.d("SPENT DURATION======> " + shopDurationData.spent_duration)
                    XLog.d("VISIT DATE=========> " + shopDurationData.visited_date)
                    XLog.d("VISIT DATE TIME==========> " + shopDurationData.visited_date)
                    XLog.d("TOTAL VISIT COUNT========> " + shopDurationData.total_visit_count)
                    XLog.d("DISTANCE TRAVELLED========> " + shopDurationData.distance_travelled)
                    XLog.d("FEEDBACK========> " + shopDurationData.feedback)
                    XLog.d("isFirstShopVisited========> " + shopDurationData.isFirstShopVisited)
                    XLog.d("distanceFromHomeLoc========> " + shopDurationData.distanceFromHomeLoc)
                    XLog.d("next_visit_date========> " + shopDurationData.next_visit_date)
                    XLog.d("early_revisit_reason========> " + shopDurationData.early_revisit_reason)
                    XLog.d("device_model========> " + shopDurationData.device_model)
                    XLog.d("android_version========> " + shopDurationData.android_version)
                    XLog.d("battery========> " + shopDurationData.battery)
                    XLog.d("net_status========> " + shopDurationData.net_status)
                    XLog.d("net_type========> " + shopDurationData.net_type)
                    XLog.d("in_time========> " + shopDurationData.in_time)
                    XLog.d("out_time========> " + shopDurationData.out_time)
                    XLog.d("start_timestamp========> " + shopDurationData.start_timestamp)
                    XLog.d("in_location========> " + shopDurationData.in_location)
                    XLog.d("out_location========> " + shopDurationData.out_location)
                    XLog.d("========================================================")
                }
            }
            else {
                val shopActivity = AppDatabase.getDBInstance()!!.shopActivityDao().durationAvailableForShopList(syncedShopList[k].shop_id, true,
                        false)

                shopActivity?.forEach {
                    val shopDurationData = ShopDurationRequestData()
                    shopDurationData.shop_id = it.shopid
                    shopDurationData.spent_duration = it.duration_spent
                    shopDurationData.visited_date = it.visited_date
                    shopDurationData.visited_time = it.visited_date
                    if (AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid) != null)
                        shopDurationData.total_visit_count = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(it.shopid).totalVisitCount
                    else
                        shopDurationData.total_visit_count = "1"

                    if (TextUtils.isEmpty(it.distance_travelled))
                        it.distance_travelled = "0.0"
                    shopDurationData.distance_travelled = it.distance_travelled

                    if (!TextUtils.isEmpty(it.feedback))
                        shopDurationData.feedback = it.feedback
                    else
                        shopDurationData.feedback = ""

                    shopDurationData.isFirstShopVisited = it.isFirstShopVisited
                    shopDurationData.distanceFromHomeLoc = it.distance_from_home_loc

                    shopDurationData.next_visit_date = it.next_visit_date

                    if (!TextUtils.isEmpty(it.early_revisit_reason))
                        shopDurationData.early_revisit_reason = it.early_revisit_reason
                    else
                        shopDurationData.early_revisit_reason = ""

                    shopDurationData.device_model = it.device_model
                    shopDurationData.android_version = it.android_version
                    shopDurationData.battery = it.battery
                    shopDurationData.net_status = it.net_status
                    shopDurationData.net_type = it.net_type
                    shopDurationData.in_time = it.in_time
                    shopDurationData.out_time = it.out_time
                    shopDurationData.start_timestamp = it.startTimeStamp
                    shopDurationData.in_location = it.in_loc
                    shopDurationData.out_location = it.out_loc


                    shopDurationData.shop_revisit_uniqKey = it.shop_revisit_uniqKey!!
                    /*10-12-2021*/
                    shopDurationData.updated_by = Pref.user_id
                    try {
                        shopDurationData.updated_on = it.updated_on!!
                    }catch (Ex:Exception){
                        shopDurationData.updated_on = ""
                    }

                    if (!TextUtils.isEmpty(it.pros_id!!))
                        shopDurationData.pros_id = it.pros_id!!
                    else
                        shopDurationData.pros_id = ""

                    if (!TextUtils.isEmpty(it.agency_name!!))
                        shopDurationData.agency_name =it.agency_name!!
                    else
                        shopDurationData.agency_name = ""

                    if (!TextUtils.isEmpty(it.approximate_1st_billing_value))
                        shopDurationData.approximate_1st_billing_value = it.approximate_1st_billing_value!!
                    else
                        shopDurationData.approximate_1st_billing_value = ""
                    //duration garbage fix
                    try{
                        if(shopDurationData.spent_duration!!.contains("-") || shopDurationData.spent_duration!!.length != 8)
                        {
                            shopDurationData.spent_duration="00:00:10"
                        }
                    }catch (ex:Exception){
                        shopDurationData.spent_duration="00:00:10"
                    }
                    shopDataList.add(shopDurationData)


                    XLog.d("====SYNC VISITED SHOP DATA (LOCATION FUZED SERVICE)====")
                    XLog.d("SHOP ID======> " + shopDurationData.shop_id)
                    XLog.d("SPENT DURATION======> " + shopDurationData.spent_duration)
                    XLog.d("VISIT DATE=========> " + shopDurationData.visited_date)
                    XLog.d("VISIT DATE TIME==========> " + shopDurationData.visited_date)
                    XLog.d("TOTAL VISIT COUNT========> " + shopDurationData.total_visit_count)
                    XLog.d("DISTANCE TRAVELLED========> " + shopDurationData.distance_travelled)
                    XLog.d("FEEDBACK========> " + shopDurationData.feedback)
                    XLog.d("isFirstShopVisited========> " + shopDurationData.isFirstShopVisited)
                    XLog.d("distanceFromHomeLoc========> " + shopDurationData.distanceFromHomeLoc)
                    XLog.d("next_visit_date========> " + shopDurationData.next_visit_date)
                    XLog.d("device_model========> " + shopDurationData.device_model)
                    XLog.d("android_version========> " + shopDurationData.android_version)
                    XLog.d("battery========> " + shopDurationData.battery)
                    XLog.d("net_status========> " + shopDurationData.net_status)
                    XLog.d("net_type========> " + shopDurationData.net_type)
                    XLog.d("in_time========> " + shopDurationData.in_time)
                    XLog.d("out_time========> " + shopDurationData.out_time)
                    XLog.d("start_timestamp========> " + shopDurationData.start_timestamp)
                    XLog.d("in_location========> " + shopDurationData.in_location)
                    XLog.d("out_location========> " + shopDurationData.out_location)
                    XLog.d("========================================================")
                }
            }
        }

        if (shopDataList.isEmpty()) {
            //isShopActivityUpdating = false

            val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
            if (syncedShop != null && syncedShop.isNotEmpty()) {
                for (j in syncedShop.indices) {
                    val unSyncImage = AppDatabase.getDBInstance()!!.shopVisitImageDao().getUnSyncedData(false, syncedShop[j].shopid!!)
                    if (unSyncImage != null)
                        unSyncedList.add(unSyncImage)
                }
                if (unSyncedList != null && unSyncedList.isNotEmpty()) {
                    i = 0
                    callShopVisitImageUploadApi(unSyncedList)
                } else {

                    val unSyncedAudioList = ArrayList<ShopVisitAudioEntity>()
                    syncedShop.forEach {
                        val unSyncAudio = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getUnSyncedData(false, it.shopid!!)
                        if (unSyncAudio != null)
                            unSyncedAudioList.add(unSyncAudio)
                    }

                    if (unSyncedAudioList.isNotEmpty()) {
                        i = 0
                        callShopVisitAudioUploadApi(unSyncedAudioList)
                    } else {
                        BaseActivity.isShopActivityUpdating = false

                        chatList.removeAt(chatList.size - 1)
                        updateList(getString(R.string.sync_done), "bot", true, false)

                        Handler().postDelayed(Runnable {
                            updateList(getString(R.string.can_i_help), "bot", true, false)
                            yesStatus = 1
                            ll_yes_no_btn.visibility = View.VISIBLE
                            ll_chat_btn.visibility = View.GONE
                            rl_date.visibility = View.GONE
                            ll_visit_btn.visibility = View.GONE
                            ll_help_btn.visibility = View.GONE
                            ll_tips_btn.visibility = View.GONE
                        }, 500)
                    }
                }
            } else {
                BaseActivity.isShopActivityUpdating = false

                chatList.removeAt(chatList.size - 1)
                updateList(getString(R.string.sync_done), "bot", true, false)

                Handler().postDelayed(Runnable {
                    updateList(getString(R.string.can_i_help), "bot", true, false)
                    yesStatus = 1
                    ll_yes_no_btn.visibility = View.VISIBLE
                    ll_chat_btn.visibility = View.GONE
                    rl_date.visibility = View.GONE
                    ll_visit_btn.visibility = View.GONE
                    ll_help_btn.visibility = View.GONE
                    ll_tips_btn.visibility = View.GONE
                }, 500)
            }
        } else {

            XLog.e("====SYNC VISITED SHOP (ChatBot)====")
            XLog.e("ShopData List size===> " + shopDataList.size)

            //val newShopList = FTStorageUtils.removeDuplicateData(shopDataList)

            val hashSet = HashSet<ShopDurationRequestData>()
            val newShopList = ArrayList<ShopDurationRequestData>()

            if (!Pref.isMultipleVisitEnable) {
                for (i in shopDataList.indices) {
                    if (hashSet.add(shopDataList[i]))
                        newShopList.add(shopDataList[i])
                }
            }

            val shopDurationApiReq = ShopDurationRequest()
            shopDurationApiReq.user_id = Pref.user_id
            shopDurationApiReq.session_token = Pref.session_token
            if (newShopList.size > 0) {
                XLog.e("Unique ShopData List size===> " + newShopList.size)
                shopDurationApiReq.shop_list = newShopList
            } else
                shopDurationApiReq.shop_list = shopDataList

            val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()

            XLog.d("callShopDurationApi : REQUEST")

            BaseActivity.compositeDisposable.add(
                    repository.shopDuration(shopDurationApiReq)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
//                        .timeout(60 * 1, TimeUnit.SECONDS)
                            .subscribe({ result ->
                                XLog.d("callShopDurationApi : RESPONSE " + result.status)
                                if (result.status == NetworkConstant.SUCCESS) {
                                    if (newShopList.size > 0) {
                                        for (i in 0 until newShopList.size) {
                                            AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, newShopList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(newShopList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                        }
                                        syncShopVisitImage(newShopList)
                                    } else {
                                        if (!Pref.isMultipleVisitEnable) {
                                            for (i in 0 until shopDataList.size) {
                                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!) /*AppUtils.getCurrentDateForShopActi()*/)
                                            }

                                            syncShopVisitImage(shopDataList)
                                        }
                                        else {
                                            for (i in 0 until shopDataList.size) {
                                                AppDatabase.getDBInstance()!!.shopActivityDao().updateisUploaded(true, shopDataList[i].shop_id!!, AppUtils.changeAttendanceDateFormatToCurrent(shopDataList[i].visited_date!!), shopDataList[i].start_timestamp!!)
                                            }
                                            // multivisit test
                                            syncShopVisitImage(shopDataList)
                                        }
                                    }
                                }
                                BaseActivity.isShopActivityUpdating = false
                            }, { error ->
                                BaseActivity.isShopActivityUpdating = false
                                if (error == null) {
                                    XLog.d("callShopDurationApi : ERROR " + "UNEXPECTED ERROR IN SHOP ACTIVITY API")
                                } else {
                                    XLog.d("callShopDurationApi : ERROR " + error.localizedMessage)
                                    error.printStackTrace()
                                }

                                chatList.removeAt(chatList.size - 1)
                                updateList(getString(R.string.req_done), "bot", true, false)

                                Handler().postDelayed(Runnable {
                                    updateList(getString(R.string.can_i_help), "bot", true, false)
                                    yesStatus = 1
                                    ll_yes_no_btn.visibility = View.VISIBLE
                                    ll_chat_btn.visibility = View.GONE
                                    rl_date.visibility = View.GONE
                                    ll_visit_btn.visibility = View.GONE
                                    ll_help_btn.visibility = View.GONE
                                    ll_tips_btn.visibility = View.GONE
                                }, 500)

//                                (mContext as DashboardActivity).showSnackMessage("ERROR")
                            })
            )
        }
    }

    private var mShopDataList: MutableList<ShopDurationRequestData>? = null
    private fun syncShopVisitImage(shopDataList: MutableList<ShopDurationRequestData>) {
        /*var unSyncedList: List<ShopVisitImageModelEntity>? = null
        for (i in shopDataList.indices) {
            unSyncedList = AppDatabase.getDBInstance()!!.shopVisitImageDao().getUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!)
        }*/
        mShopDataList = shopDataList
        val unSyncedList = ArrayList<ShopVisitImageModelEntity>()
        for (i in shopDataList.indices) {
            val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitImageDao().getUnSyncedListAccordingToShopId(false, shopDataList[i].shop_id!!)

            if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                unSyncedList.add(unSyncedData[0])
            }
        }

        if (unSyncedList.size > 0) {
            i = 0
            callShopVisitImageUploadApi(unSyncedList)
        } else {
            i = 0
            checkToCallAudioApi()
        }
    }

    private fun callShopVisitImageUploadApi(unSyncedList: List<ShopVisitImageModelEntity>) {

        try {
            val visitImageShop = ShopVisitImageUploadInputModel()
            visitImageShop.session_token = Pref.session_token
            visitImageShop.user_id = Pref.user_id
            visitImageShop.shop_id = unSyncedList[i].shop_id
            visitImageShop.visit_datetime = unSyncedList[i].visit_datetime


            XLog.d("========UPLOAD REVISIT ALL IMAGE INPUT PARAMS (ChatBot)======")
            XLog.d("USER ID======> " + visitImageShop.user_id)
            XLog.d("SESSION ID======> " + visitImageShop.session_token)
            XLog.d("SHOP ID=========> " + visitImageShop.shop_id)
            XLog.d("VISIT DATE TIME==========> " + visitImageShop.visit_datetime)
            XLog.d("IMAGE========> " + unSyncedList[i].shop_image)
            XLog.d("==============================================================")

            val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()

            BaseActivity.compositeDisposable.add(
                    repository.visitShopWithImage(visitImageShop, unSyncedList[i].shop_image!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val logoutResponse = result as BaseResponse
                                XLog.d("UPLOAD REVISIT ALL IMAGE : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                                if (logoutResponse.status == NetworkConstant.SUCCESS)
                                    AppDatabase.getDBInstance()!!.shopVisitImageDao().updateisUploaded(true, unSyncedList.get(i).shop_id!!)

                                i++
                                if (i < unSyncedList.size)
                                    callShopVisitImageUploadApi(unSyncedList)
                                else {
                                    i = 0
                                    checkToCallAudioApi()
                                }

                            }, { error ->
                                XLog.d("UPLOAD REVISIT ALL IMAGE : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()
                                i++
                                if (i < unSyncedList.size)
                                    callShopVisitImageUploadApi(unSyncedList)
                                else {
                                    i = 0
                                    checkToCallAudioApi()
                                }
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            BaseActivity.isShopActivityUpdating = false
            chatList.removeAt(chatList.size - 1)
            updateList(getString(R.string.unsuccessful_sync), "bot", true, false)

            Handler().postDelayed(Runnable {
                updateList(getString(R.string.can_i_help), "bot", true, false)
                yesStatus = 1
                ll_yes_no_btn.visibility = View.VISIBLE
                ll_chat_btn.visibility = View.GONE
                rl_date.visibility = View.GONE
                ll_visit_btn.visibility = View.GONE
                ll_help_btn.visibility = View.GONE
                ll_tips_btn.visibility = View.GONE
            }, 500)
        }
    }

    private fun checkToCallAudioApi() {
        val unSyncAudioList = ArrayList<ShopVisitAudioEntity>()
        mShopDataList?.forEach {
            val unSyncedData = AppDatabase.getDBInstance()!!.shopVisitAudioDao().getUnSyncedListAccordingToShopId(false, it.shop_id!!)

            if (unSyncedData != null && unSyncedData.isNotEmpty()) {
                unSyncAudioList.add(unSyncedData[0])
            }
        }

        if (unSyncAudioList.isNotEmpty()) {
            i = 0
            callShopVisitAudioUploadApi(unSyncAudioList)
        } else {
            BaseActivity.isShopActivityUpdating = false

            chatList.removeAt(chatList.size - 1)
            updateList(getString(R.string.req_done), "bot", true, false)

            Handler().postDelayed(Runnable {
                updateList(getString(R.string.can_i_help), "bot", true, false)
                yesStatus = 1
                ll_yes_no_btn.visibility = View.VISIBLE
                ll_chat_btn.visibility = View.GONE
                rl_date.visibility = View.GONE
                ll_visit_btn.visibility = View.GONE
                ll_help_btn.visibility = View.GONE
                ll_tips_btn.visibility = View.GONE
            }, 500)
        }
    }


    private fun callShopVisitAudioUploadApi(unSyncedList: List<ShopVisitAudioEntity>) {

        try {
            val visitImageShop = ShopVisitImageUploadInputModel()
            visitImageShop.session_token = Pref.session_token
            visitImageShop.user_id = Pref.user_id
            visitImageShop.shop_id = unSyncedList[i].shop_id
            visitImageShop.visit_datetime = unSyncedList[i].visit_datetime

            XLog.d("====UPLOAD REVISIT ALL AUDIO INPUT PARAMS (LOCATION FUZED SERVICE)======")
            XLog.d("USER ID====> " + visitImageShop.user_id)
            XLog.d("SESSION ID====> " + visitImageShop.session_token)
            XLog.d("SHOP ID====> " + visitImageShop.shop_id)
            XLog.d("VISIT DATE TIME=====> " + visitImageShop.visit_datetime)
            XLog.d("AUDIO=====> " + unSyncedList[i].audio)
            XLog.d("===============================================================")

            val repository = ShopVisitImageUploadRepoProvider.provideAddShopRepository()

            BaseActivity.compositeDisposable.add(
                    repository.visitShopWithAudio(visitImageShop, unSyncedList[i].audio!!, mContext)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val logoutResponse = result as BaseResponse
                                XLog.d("UPLOAD REVISIT ALL AUDIO : " + "RESPONSE : " + logoutResponse.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + logoutResponse.message)
                                if (logoutResponse.status == NetworkConstant.SUCCESS)
                                    AppDatabase.getDBInstance()!!.shopVisitAudioDao().updateisUploaded(true, unSyncedList.get(i).shop_id!!)

                                i++
                                if (i < unSyncedList.size)
                                    callShopVisitAudioUploadApi(unSyncedList)
                                else {
                                    i = 0
                                    BaseActivity.isShopActivityUpdating = false

                                    chatList.removeAt(chatList.size - 1)
                                    updateList(getString(R.string.req_done), "bot", true, false)

                                    Handler().postDelayed(Runnable {
                                        updateList(getString(R.string.can_i_help), "bot", true, false)
                                        yesStatus = 1
                                        ll_yes_no_btn.visibility = View.VISIBLE
                                        ll_chat_btn.visibility = View.GONE
                                        rl_date.visibility = View.GONE
                                        ll_visit_btn.visibility = View.GONE
                                        ll_help_btn.visibility = View.GONE
                                        ll_tips_btn.visibility = View.GONE
                                    }, 500)
                                }

                            }, { error ->
                                XLog.d("UPLOAD REVISIT ALL AUDIO : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                                error.printStackTrace()

                                i++
                                if (i < unSyncedList.size)
                                    callShopVisitAudioUploadApi(unSyncedList)
                                else {
                                    i = 0
                                    BaseActivity.isShopActivityUpdating = false

                                    chatList.removeAt(chatList.size - 1)
                                    updateList(getString(R.string.req_done), "bot", true, false)

                                    Handler().postDelayed(Runnable {
                                        updateList(getString(R.string.can_i_help), "bot", true, false)
                                        yesStatus = 1
                                        ll_yes_no_btn.visibility = View.VISIBLE
                                        ll_chat_btn.visibility = View.GONE
                                        rl_date.visibility = View.GONE
                                        ll_visit_btn.visibility = View.GONE
                                        ll_help_btn.visibility = View.GONE
                                        ll_tips_btn.visibility = View.GONE
                                    }, 500)
                                }
                            })
            )
        } catch (e: Exception) {
            e.printStackTrace()
            BaseActivity.isShopActivityUpdating = false

            chatList.removeAt(chatList.size - 1)
            updateList(getString(R.string.unsuccessful_sync), "bot", true, false)

            Handler().postDelayed(Runnable {
                updateList(getString(R.string.can_i_help), "bot", true, false)
                yesStatus = 1
                ll_yes_no_btn.visibility = View.VISIBLE
                ll_chat_btn.visibility = View.GONE
                rl_date.visibility = View.GONE
                ll_visit_btn.visibility = View.GONE
                ll_help_btn.visibility = View.GONE
                ll_tips_btn.visibility = View.GONE
            }, 500)
        }
    }

    override fun onResume() {
        super.onResume()
        changeLanguage(mContext,language)
    }

    fun update() {
        changeLanguage(mContext,language)
    }

    override fun onDestroy() {
        changeLanguage(mContext,"en")

        tts?.let {
            it.stop()
            it.shutdown()
        }

        super.onDestroy()
    }
}
