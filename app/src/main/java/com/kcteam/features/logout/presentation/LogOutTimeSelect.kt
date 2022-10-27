//package com.fieldtrackingsystem.features.logout.presentation
//
//import android.app.Activity
//import android.app.AlarmManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.Button
//import android.widget.DatePicker
//import android.widget.TextView
//import android.widget.TimePicker
//import android.widget.Toast
//
//import com.fieldtrackingsystem.R
//import com.fieldtrackingsystem.app.AlarmReceiver
//import com.fieldtrackingsystem.app.utils.AppUtils
//import java.text.ParseException
//import java.text.SimpleDateFormat
//import java.util.*
//
///**
// * Created by Dhiraj on 28-11-2017.
// */
//
//class LogOutTimeSelect : Activity() {
//
//    internal var pickerDate: DatePicker? = null
//    internal var pickerTime: TimePicker? = null
//    internal lateinit var buttonSetAlarm: Button
//    internal var info: TextView? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.logout_time)
//
//        //        info = (TextView)findViewById(R.id.info);
//        //        pickerDate = (DatePicker)findViewById(R.id.pickerdate);
//        //        pickerTime = (TimePicker)findViewById(R.id.pickertime);
//
//        //        Calendar now = Calendar.getInstance();
//
//        //        pickerDate.init(
//        //                now.get(Calendar.YEAR),
//        //                now.get(Calendar.MONTH),
//        //                now.get(Calendar.DAY_OF_MONTH),
//        //                null);
//        //
//        //        pickerTime.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
//        //        pickerTime.setCurrentMinute(now.get(Calendar.MINUTE));
//
//        buttonSetAlarm = findViewById<Button>(R.id.setalarm)
//        buttonSetAlarm.setOnClickListener {
//
//            //                Calendar current = Calendar.getInstance();
//
//            val cal = Calendar.getInstance()
//            //                cal.set(pickerDate.getYear(),
//            //                        pickerDate.getMonth(),
//            //                        pickerDate.getDayOfMonth(),
//            //                        pickerTime.getCurrentHour(),
//            //                        pickerTime.getCurrentMinute(),
//            //                        00);
//
//
//            //                if(cal.compareTo(current) <= 0){
//            //                    //The set Date/Time already passed
//            //                    Toast.makeText(getApplicationContext(),
//            //                            "Invalid Date/Time",
//            //                            Toast.LENGTH_LONG).show();
//            //                }else{
//            setAlarm(cal)
//            //                }
//        }
//    }
//
//    private fun setAlarm(targetCal: Calendar) {
//        var now = Date();
//        now.year
//       var currenttime= now.toString().split(" ")[3].toString();
//        currenttime.replace(currenttime,"16:30:00");
//        val dtStart = now.toString().split(" ")[0].toString()+" "+now.toString().split(" ")[1].toString()+" "+now.toString().split(" ")[2].toString()+" "+"12:00:00"+" "+now.toString().split(" ")[4].toString()+" "+now.toString().split(" ")[5].toString()
//
//
////        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
//        try {
//            val formatter = SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy")
//            try {
//                now = formatter.parse(dtStart)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        } catch (e: ParseException) {
//            e.printStackTrace()
//        }
//
//        //Wed Nov 29 13:01:00 GMT+05:30 2017
//        //Time in millis : 1511940660986
//        val intent = Intent(baseContext, AlarmReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(baseContext, RQS_1, intent, 0)
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        alarmManager.set(AlarmManager.RTC_WAKEUP, now.time, pendingIntent)
//    }
//
//    companion object {
//
//        internal val RQS_1 = 1
//    }
//
//}