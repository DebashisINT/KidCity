package com.kcteam.features.alarm.presetation

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RadioGroup
import com.kcteam.R
import com.kcteam.app.AlarmReceiver
import com.kcteam.app.AlarmReceiver.Companion.loadSharedPreferencesLogList
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils.Companion.getCurrentTimeWithMeredian
import com.kcteam.app.utils.FTStorageUtils.isMyActivityRunning
import com.kcteam.app.utils.NotificationUtils
import com.kcteam.app.utils.SharedWakeLock
import com.kcteam.features.alarm.model.AlarmData
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import java.util.*


/**
 * Created by anupamchugh on 01/08/17.
 */

class FloatingWidgetService : Service(), RadioGroup.OnCheckedChangeListener, View.OnClickListener {


    private var mWindowManager: WindowManager? = null
    private var mOverlayView: View? = null
    private var activity_background: Boolean = false
    internal val params = null
    private var ll_radio: LinearLayout? = null
    private lateinit var tv_time: AppCustomTextView
    private lateinit var tv_report_title: AppCustomTextView
    private var alarmDataArr = ArrayList<AlarmData>()
    private var alarmDataIndex: Int = -1
    private var alarmData: AlarmData? = null
    private lateinit var ll_view_report: LinearLayout

    private var isSnoozeButtonPressed = false

    override fun onBind(intent: Intent): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val requestCode = intent?.getIntExtra("requestCode", -1)
        if (intent != null) {
            activity_background = intent.getBooleanExtra("activity_background", false)

        }

        if (mOverlayView == null) {

            mOverlayView = LayoutInflater.from(this).inflate(R.layout.activity_alarm, null)

            val params = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT)
            } else {
                WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT)
            }

            params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

            /*  @Suppress("DEPRECATION")
              val flag = (WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
              params.flags = flag*/

            mWindowManager!!.addView(mOverlayView, params)
        }


        val ll_snooze = mOverlayView!!.findViewById(R.id.ll_snooze) as LinearLayout
        ll_radio = mOverlayView!!.findViewById(R.id.ll_radio) as LinearLayout
        ll_snooze.setOnClickListener(this)

        ll_view_report = mOverlayView!!.findViewById(R.id.ll_view_report) as LinearLayout
        ll_view_report.setOnClickListener(this)

        val alarm_radio_button = mOverlayView!!.findViewById(R.id.alarm_radio_button) as RadioGroup
        alarm_radio_button.setOnCheckedChangeListener(this)

        val tv_view_report = mOverlayView!!.findViewById(R.id.tv_view_report) as AppCustomTextView

        tv_time = mOverlayView!!.findViewById(R.id.tv_time)
        tv_report_title = mOverlayView!!.findViewById(R.id.tv_report_title)

        alarmDataArr.forEach { event ->
            if (event.requestCode == (requestCode)) {
                alarmDataIndex = alarmDataArr.indexOf(event)
                alarmData = event

            }
        }
        if (alarmDataIndex == -1) {
            stopSelf()
        } else {
            tv_time.text = getCurrentTimeWithMeredian(alarmData?.alarm_time_hours + ":" + alarmData?.alarm_time_mins)
            tv_report_title.text = alarmData!!.report_title
        }

        when {
            alarmData?.report_id == "5" -> tv_view_report.text = getString(R.string.update_achievement)
            alarmData?.report_id == "6" -> tv_view_report.text = "Take Selfie"
            else -> tv_view_report.text = getString(R.string.view_report)
        }


        if (alarmData?.report_id == "6") {
            ll_snooze.visibility = View.GONE
            //Pref.isSefieAlarmed = true
        } else {
            ll_snooze.visibility = View.VISIBLE
            //Pref.isSefieAlarmed = false
        }

        return START_NOT_STICKY
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.ll_clock -> {

            }
            R.id.ll_snooze -> {
                ll_radio?.visibility = View.VISIBLE
            }
            R.id.ll_view_report -> {
                isSnoozeButtonPressed = true

                AlarmReceiver.stopAlarmManager(applicationContext, alarmData!!.requestCode)

                if (isMyActivityRunning(applicationContext)) {
                    val intent = Intent("ALARM_RECIEVER_BROADCAST")
                    intent.putExtra("ALARM_DATA", alarmData)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                } else {
                    if (Build.VERSION.SDK_INT >= 29) {
                        val notification = NotificationUtils(getString(R.string.app_name), "", "", "")

                        var body = ""
                        if (alarmData?.report_id == "1")
                            body = "Please click to show attendance report"
                        else if (alarmData?.report_id == "2")
                            body = "Please click to show visit report"
                        else if (alarmData?.report_id == "3" || alarmData?.report_id == "4")
                            body = "Please click to show performance report"
                        else if (alarmData?.report_id == "5")
                            body = "Please click to show plan list"
                        else if (alarmData?.report_id == "6")
                            body = "Please click to capture selfie"

                        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.cancelAll()

                        notification.sendAlarmNotification(this, body, alarmData)
                    }
                    else {
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("ALARM_DATA", alarmData)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
                stopSelf()
            }
        }
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        isSnoozeButtonPressed = true
        when (checkedId) {
            R.id.time_5 -> {
                stopAlarmAndSnooze(5)
                stopSelf()
            }
            R.id.time_10 -> {
                stopAlarmAndSnooze(10)
                stopSelf()
            }
            R.id.time_15 -> {
                stopAlarmAndSnooze(15)
                stopSelf()
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        setTheme(R.style.AppTheme)
        alarmDataArr = loadSharedPreferencesLogList(applicationContext)

        val i = Intent(this, AlarmRingingService::class.java)
        startService(i)
    }

    override fun onDestroy() {
        destroyView()
        super.onDestroy()
    }

    private fun destroyView() {
        if (mOverlayView != null) {
            mWindowManager!!.removeView(mOverlayView)
        }
        if (!isSnoozeButtonPressed) {
            stopAlarmAndSnooze(1)
        }
        val i = Intent(this, AlarmRingingService::class.java)
        stopService(i)
        SharedWakeLock.get(applicationContext).releaseFullWakeLock()

    }

    fun stopAlarmAndSnooze(alarmTigerTimeSec: Int) {
        if (alarmData != null) {
            AlarmReceiver.stopAlarmManager(applicationContext, alarmData!!.requestCode)
            AlarmReceiver.triggerAlarmManagerWithSpecifiedTime(applicationContext, alarmTigerTimeSec, alarmData!!.requestCode)
        }
    }


}
