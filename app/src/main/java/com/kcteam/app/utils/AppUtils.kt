package com.kcteam.app.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import android.provider.CalendarContract
import android.provider.MediaStore
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.format.DateFormat
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.features.login.model.LoginStateListDataModel
import com.kcteam.features.login.model.productlistmodel.ProductRateDataModel
import com.elvishew.xlog.XLog
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import org.apache.commons.lang3.StringEscapeUtils
import java.io.*
import java.math.BigDecimal
import java.sql.Timestamp
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by Pratishruti on 08-11-2017.
 */
class AppUtils {
    companion object {
        var contx:Context?= null


        var sImagePath: String? = null
        var isRevisit: Boolean? = false
        var isOnReceived = false
        var isGpsOffCalled = false
        var gpsOnTime: Long = 0
        var gpsOffTime: Long = 0
        var gpsEnabledTime = ""
        var gpsDisabledTime = ""
        //var isLoginLoaded: Boolean = false
        var gpsDisabledAction = "android.location.GPS_Disabled"
        var gpsEnabledAction = "android.location.GPS_Enabled"
        var isProfile = false
        var isFromViewPPDD = false
        var isShopAdded = false
        var mLocation: Location? = null
        var minDistance = "100"
        var maxDistance = "600.00"
        var minAccuracy = "200"
        var maxAccuracy = "1500"
        var isVisitSync = "1"
        var isAddressUpdated = "1"
        var idle_time = "30"
//        var isShopVisited = false
        var isLocationActivityUpdating = false
        var isAppInfoUpdating = false
        var notificationChannelId = "fts_1"
        var notificationChannelName = "FTS Channel"
        var isGpsReceiverCalled = false
        //var timer: Timer? = null
        var isFromAttendance = false

        // From Hahnemann
        var isAllSelect = false
        var stockStatus = -1
        var isAutoRevisit = false
        var isBroadCastRecv = false
        //var weatherKey = "a10a3857bd44e67c80282b38858d37fa"

        //var tempDistance = 0.0
        //var totalS2SDistance = 0.0  // Shop to shop distance
        //var mGoogleAPIClient: GoogleApiClient? = null

        private var mLastClickTime: Long = 0

        const val CLICK_MIN_INTERVAL: Long = 700

        fun convertTime(date: Date): String {
            val df = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            return df.format(date).toString()
        }

        val isN: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N


        fun getMonthValue(month: String): String {
            var monthVal = 0

            when {
                month.equals("Jan", ignoreCase = true) -> monthVal = 1
                month.equals("Feb", ignoreCase = true) -> monthVal = 2
                month.equals("Mar", ignoreCase = true) -> monthVal = 3
                month.equals("Apr", ignoreCase = true) -> monthVal = 4
                month.equals("May", ignoreCase = true) -> monthVal = 5
                month.equals("Jun", ignoreCase = true) -> monthVal = 6
                month.equals("Jul", ignoreCase = true) -> monthVal = 7
                month.equals("Aug", ignoreCase = true) -> monthVal = 8
                month.equals("Sep", ignoreCase = true) -> monthVal = 9
                month.equals("Oct", ignoreCase = true) -> monthVal = 10
                month.equals("Nov", ignoreCase = true) -> monthVal = 11
                month.equals("Dec", ignoreCase = true) -> monthVal = 12
            }

            return monthVal.toString()
        }

        fun getFullMonthValue(month: String): String {
            var monthVal = 0

            when {
                month.equals("January", ignoreCase = true) -> monthVal = 1
                month.equals("February", ignoreCase = true) -> monthVal = 2
                month.equals("March", ignoreCase = true) -> monthVal = 3
                month.equals("April", ignoreCase = true) -> monthVal = 4
                month.equals("May", ignoreCase = true) -> monthVal = 5
                month.equals("June", ignoreCase = true) -> monthVal = 6
                month.equals("July", ignoreCase = true) -> monthVal = 7
                month.equals("August", ignoreCase = true) -> monthVal = 8
                month.equals("September", ignoreCase = true) -> monthVal = 9
                month.equals("October", ignoreCase = true) -> monthVal = 10
                month.equals("November", ignoreCase = true) -> monthVal = 11
                month.equals("December", ignoreCase = true) -> monthVal = 12
            }

            return monthVal.toString()
        }

        fun getMonthFromValue(monthValue: String): String {
            var monthVal = ""

            when {
                /*monthValue.equals("1", ignoreCase = true) -> monthVal = "Jan"
                monthValue.equals("2", ignoreCase = true) -> monthVal = "Feb"
                monthValue.equals("3", ignoreCase = true) -> monthVal = "Mar"
                monthValue.equals("4", ignoreCase = true) -> monthVal = "Apr"
                monthValue.equals("5", ignoreCase = true) -> monthVal = "May"
                monthValue.equals("6", ignoreCase = true) -> monthVal = "Jun"
                monthValue.equals("7", ignoreCase = true) -> monthVal = "Jul"
                monthValue.equals("8", ignoreCase = true) -> monthVal = "Aug"
                monthValue.equals("9", ignoreCase = true) -> monthVal = "Sep"
                monthValue.equals("10", ignoreCase = true) -> monthVal = "Oct"
                monthValue.equals("11", ignoreCase = true) -> monthVal = "Nov"
                monthValue.equals("12", ignoreCase = true) -> monthVal = "Dec"*/

                monthValue.toInt() == 1 -> monthVal = "Jan"
                monthValue.toInt() == 2 -> monthVal = "Feb"
                monthValue.toInt() == 3 -> monthVal = "Mar"
                monthValue.toInt() == 4 -> monthVal = "Apr"
                monthValue.toInt() == 5 -> monthVal = "May"
                monthValue.toInt() == 6 -> monthVal = "Jun"
                monthValue.toInt() == 7 -> monthVal = "Jul"
                monthValue.toInt() == 8 -> monthVal = "Aug"
                monthValue.toInt() == 9 -> monthVal = "Sep"
                monthValue.toInt() == 10 -> monthVal = "Oct"
                monthValue.toInt() == 11 -> monthVal = "Nov"
                monthValue.toInt() == 12 -> monthVal = "Dec"
            }

            return monthVal
        }


        @Throws(IOException::class)
        fun createImageFile(): File {
            // Create an image file name
            val imageFileName = "fieldtrackingsystem" +  /*Calendar.getInstance(Locale.ENGLISH).time*/ java.util.UUID.randomUUID()
            //val storageDir = File(Environment.getExternalStorageDirectory().toString()
                    //+ File.separator + "fieldtrackingsystem" + File.separator)
            //27-09-2021
            val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + "kcteamApp/fieldtrackingsystem" + File.separator)
            storageDir.mkdirs()

            // Save a file: path for use with ACTION_VIEW intents
            return File.createTempFile(imageFileName, /* prefix */
                    ".jpg", /* suffix */
                    storageDir /* directory */
            )
        }

        fun getPath(mActivity: Activity, uri: Uri): String? {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = mActivity.managedQuery(uri, projection, null, null, null)
            if (cursor != null) {
                // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
                // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
                val column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                return cursor.getString(column_index)
            } else
                return null
        }

        fun getCurrentTimeWithMeredian(): String {
            val df = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            return df.format(Date()).toString()
        }
        fun getCurrentTimes(): String {
            val df = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getCurrentTimeWithMeredian(date: String): String {

            val sourceDf = SimpleDateFormat("HH:mm", Locale.ENGLISH)
            val sourceDate = sourceDf.parse(date);
            val df = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
            return df.format(sourceDate).toString()
        }

        fun convertTimeWithMeredianToLong(dateString: String?): Long {
            return try {
                val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
                val date = sdf.parse(dateString)

                val startDate = date.time;
                startDate
            } catch (e: ParseException) {
                e.printStackTrace()
                0
            }
        }

        fun convertDayTimeWithMeredianToLong(dateString: String?): Long {
            return try {
                val sdf = SimpleDateFormat("EEEE h:mm a", Locale.ENGLISH)
                val date = sdf.parse(dateString)

                val startDate = date.time;
                startDate
            } catch (e: ParseException) {
                e.printStackTrace()
                0
            }
        }


        /*fun getCurrentDayTime(){
            return try {
                val sdf = SimpleDateFormat("EEEE h:mm a", Locale.ENGLISH)
                val date = sdf.parse(Date())

                val startDate = date.time;
                startDate
            } catch (e: ParseException) {
                e.printStackTrace()
                0
            }
        }*/

        /*fun getDayTimeWithMeredian(): Long {
            return try {
                val sdf = SimpleDateFormat("EEEE h:mm a", Locale.ENGLISH)
                val d = Date()
                val day = sdf.format(d)

                val startDate = day.time
                startDate
            } catch (e: ParseException) {
                e.printStackTrace()
                0
            }
        }*/

        fun getCurrentStockDateMonth(): String {
            val df = SimpleDateFormat("ddMMyyyy", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getTimeDuration(loginTime: String, logoutTime: String): String {
            val simpleDateFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val startDate = simpleDateFormat.parse(loginTime)
            val endDate = simpleDateFormat.parse(logoutTime)
            val difference = endDate.time - startDate.time
            return getHourMinuteSeconds(difference)
//            return difference.toString()
        }

        fun getHourMinuteSeconds(restDatesinMillis: Long): String {
//            val restDatesinMillis = date1.time - date2.time

            var seconds: Long = 0
            var minutes: Long = 0
            var hours: Long = 0

            seconds = restDatesinMillis / 1000
            if (seconds >= 60) {
                minutes = seconds / 60
                seconds %= 60
            }

            if (minutes >= 60) {
                hours = minutes / 60
                minutes %= 60
            }

            // val days = hours / 24
            var sSecond: String = "0"
            var sMinute: String = "0"
            var sHours: String = "0"

            if (seconds < 10)
                sSecond = "0$seconds"
            else
                sSecond = "" + seconds

            if (minutes < 10)
                sMinute = "0$minutes"
            else
                sMinute = "" + minutes

            if (hours < 10)
                sHours = "0$hours"
            else
                sHours = "" + hours

            return "$sHours:$sMinute:$sSecond"
        }

        fun getHourMinuteFromMins(totalMins: Long): String {
//            val restDatesinMillis = date1.time - date2.time

            var minutes: Long = 0
            var hours: Long = 0

            minutes = totalMins

            if (minutes >= 60) {
                hours = minutes / 60
                minutes %= 60
            }

            var sMinute: String = "0"
            var sHours: String = "0"


            sMinute = if (minutes < 10)
                "0$minutes"
            else
                "" + minutes

            sHours = if (hours < 10)
                "0$hours"
            else
                "" + hours

            return "$sHours:$sMinute"
        }

        fun getTimeInHourMinuteFormat(different: Long): String {
            /*val seconds = different / 1000;
            val minutes = seconds / 60;
            val hours = minutes / 60;
            val days = hours / 24;*/

            /*val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24

            var diff = different

            val elapsedDays = diff / daysInMilli
            diff %= daysInMilli

            val elapsedHours = diff / hoursInMilli
            diff %= hoursInMilli

            val elapsedMinutes = diff / minutesInMilli
            diff %= minutesInMilli

            val elapsedSeconds = diff / secondsInMilli*/

            /*val diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(different);
            val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(different);
            val diffInHours = TimeUnit.MILLISECONDS.toHours(different);
            val diffInDays = TimeUnit.MILLISECONDS.toDays(different);*/


            val elapsedSeconds = different / 1000 % 60;
            val elapsedMinutes = different / (60 * 1000) % 60;
            val elapsedHours = different / (60 * 60 * 1000) % 24;
            val elapsedDays = different / (24 * 60 * 60 * 1000);

            return /*elapsedDays.toString() + ":" +*/ "$elapsedHours:$elapsedMinutes:$elapsedSeconds"
        }

        fun convertMinuteToHoursMinFormat(minute: Int): String {
            val hours = minute / 60 //since both are ints, you get an int
            val minutes = minute % 60

            return "$hours:$minutes:0"
        }

        fun getCurrentDateTime(): String {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getCurrentDateTimeDDMMYY(): String {
            val df = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getCurrentTime(): String {
            val df = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getCurrentDateTime12(loginDate: String): String {
//           loginDate: "dd-MMM-yy"
            val dateFormat = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(loginDate) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("yyyy-MM-dd 23:59:00", Locale.ENGLISH)
            return f.format(convertedDate)
        }


        fun getCurrentDateFormatInTa(loginDate: String): String {
//           loginDate: "dd-MMM-yy"
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(loginDate) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            return f.format(convertedDate)
        }

        @Throws(ParseException::class)
        fun getFormatedDateNew(date: String?, initDateFormat: String?, endDateFormat: String?): String? {
            val initDate: Date = SimpleDateFormat(initDateFormat).parse(date)
            val formatter = SimpleDateFormat(endDateFormat)
            return formatter.format(initDate)
        }

        fun convertLoginTimeToAutoLogoutTimeFormat(loginDate: String): String {
            val dateFormat = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(loginDate) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
            return f.format(convertedDate)
        }

        fun convertLoginTimeToAutoLogoutTimeFormatyymmdd(loginDate: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(loginDate) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            return f.format(convertedDate)
        }

        fun getDateFormat(loginDate: String): Date {
//           loginDate: "dd-MMM-yy"
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(loginDate) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            return convertedDate;
        }


        /*Current date for DB Add Shop*/

        //TODO
        fun getCurrentDate(): String {
            val c = Calendar.getInstance(Locale.ENGLISH)
            System.out.println("Current time => " + c.time)

            val df = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            val formattedDate = df.format(c.time)
            return formattedDate.toString()
        }

        fun getCurrentDate_DD_MM_YYYY(): String {
            val c = Calendar.getInstance(Locale.ENGLISH)
            System.out.println("Current time => " + c.time)

            val df = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
            val formattedDate = df.format(c.time)
            return formattedDate.toString()
        }

        fun getCurrentDateyymmdd(): String {
            val c = Calendar.getInstance(Locale.ENGLISH)
            System.out.println("Current time => " + c.time)

            val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val formattedDate = df.format(c.time)
            return formattedDate.toString()
        }

        fun getCurrentDateChanged(): String {
            val c = Calendar.getInstance(Locale.ENGLISH)
//            System.out.println("Current time => " + c.time)

            val df = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
            val formattedDate = df.format(c.time)
            return formattedDate
        }

        //TODO
        fun getFormattedDate(c: Calendar): String {
//            val c = Calendar.getInstance(Locale.ENGLISH)
//            System.out.println("Current time => " + c.time)

            val df = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            val formattedDate = df.format(c.time)
            return formattedDate.toString()
        }

        fun getFormattedDateForActivity(c: Calendar): String {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val formattedDate = df.format(c.time)
            return formattedDate.toString()
        }


        fun changeToCurrentDateFormat(date: Date): String {
            var spf = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            return spf.format(date)
        }


        fun substractDates(date1: Date, date2: Date): String {
            val restDatesinMillis = date1.time - date2.time

            var seconds: Long = 0
            var minutes: Long = 0
            var hours: Long = 0

            seconds = restDatesinMillis / 1000
            if (seconds >= 60) {
                minutes = seconds / 60
                seconds %= 60
            }

            if (minutes >= 60) {
                hours = minutes / 60
                minutes %= 60
            }

            // val days = hours / 24
            var sSecond: String = "0"
            var sMinute: String = "0"
            var sHours: String = "0"

            if (seconds < 10)
                sSecond = "0$seconds"
            else
                sSecond = "" + seconds

            if (minutes < 10)
                sMinute = "0$minutes"
            else
                sMinute = "" + minutes

            if (hours < 10)
                sHours = "0$hours"
            else
                sHours = "" + hours

            return "$sHours:$sMinute:$sSecond"
        }

        fun getDayFromSubtractDates(date1: Long, date2: Long): String {
            val restDatesinMillis = date2 - date1
            return (restDatesinMillis / (1000 * 60 * 60 * 24)).toString()
        }

        fun getTimeFromTimeSpan(startTimeStamp: String, endTimeStamp: String): String {
            /*if (startTimeStamp.isBlank() || endTimeStamp.isBlank())
                return "0"
            if (endTimeStamp == "0")
                return "0"
            var totalTimeInMili = (endTimeStamp.toLong() - startTimeStamp.toLong())
            var minutesInTotal = TimeUnit.MILLISECONDS.toMinutes(totalTimeInMili)
            var minutes: Long = 0
            var hours: Long = 0
            if (minutesInTotal >= 60) {
                hours = minutesInTotal / 60
                minutes = minutesInTotal % 60
            } else {
                minutes = minutesInTotal
            }

            // val days = hours / 24
            var sSecond: String = "00"
            var sMinute: String = "0"
            var sHours: String = "0"

            if (minutes < 10)
                sMinute = "0" + minutes
            else
                sMinute = "" + minutes

            if (hours < 10)
                sHours = "0" + hours
            else
                sHours = "" + hours


            if (sHours > "5") {
                sHours = "5"
            }

            XLog.e("====CALCULATE DURATION (AppUtils)=====")
            XLog.e("Hours Spent====> $sHours")

            val duration = "$sHours:$sMinute:$sSecond"
            XLog.e("Duration Spent====> $duration")

            return duration*/

            if (startTimeStamp.isBlank() || endTimeStamp.isBlank())
                return "0"
            if (endTimeStamp == "0")
                return "0"
            val totalTimeInMili = (endTimeStamp.toLong() - startTimeStamp.toLong())
            /*var minutesInTotal = TimeUnit.MILLISECONDS.toMinutes(totalTimeInMili)
            var minutes: Long = 0
            var hours: Long = 0
            if (minutesInTotal >= 60) {
                hours = minutesInTotal / 60
                minutes = minutesInTotal % 60
            } else {
                minutes = minutesInTotal
            }*/

            val secondsInTotal = TimeUnit.MILLISECONDS.toSeconds(totalTimeInMili)
            var seconds = 0L
            var minutes: Long = 0
            var hours: Long = 0
            if (secondsInTotal >= 60) {
                minutes = secondsInTotal / 60
                seconds = secondsInTotal % 60

                if (minutes >= 60) {
                    hours = minutes / 60
                    minutes %= 60
                }

            } else {
                seconds = secondsInTotal
            }


            // val days = hours / 24
            var sSecond: String = "00"
            var sMinute: String = "0"
            var sHours: String = "0"

            sSecond = if (seconds < 10)
                "0$seconds"
            else
                "" + seconds

            sMinute = if (minutes < 10)
                "0$minutes"
            else
                "" + minutes

            sHours = if (hours < 10)
                "0$hours"
            else
                "" + hours

            /*if (sHours.length > 2) {
                sHours = "23"
            }*/

            if (sHours > "5") {
                sHours = "5"
            }

            XLog.e("====CALCULATE DURATION (AppUtils)=====")
            XLog.e("Hours Spent====> $sHours")

            /*try {
                XLog.e("Minutes Spent====> $sMinute")
                sMinute.toInt()
            } catch (e: Exception) {
                e.printStackTrace()
                sMinute = "5"
            }*/

            val duration = "$sHours:$sMinute:$sSecond"
            XLog.e("Duration Spent====> $duration")

            return duration
        }

        fun format(s: Long): String? {
            return if (s < 10) "0$s" else "" + s
        }

        fun getMinuteFromTimeStamp(startTimeStamp: String, endTimeStamp: String): String {
            if (startTimeStamp.isBlank() || endTimeStamp.isBlank())
                return "0"

            val totalTimeInMili = (endTimeStamp.toLong() - startTimeStamp.toLong())
            return TimeUnit.MILLISECONDS.toMinutes(totalTimeInMili).toString()

        }


        fun compareWithCurrentDate(savedDate: String): Boolean {
            return savedDate == (getCurrentDate())
        }

        fun getMinuteFromString(totalMinString: String): String {
            if (totalMinString.isEmpty() || totalMinString == "0")
                return "0"
            if (totalMinString[0].toString() == "0") {
                return totalMinString[1].toString()
            } else {
                totalMinString
            }
            return "0"
        }

        fun getCurrentTimeInMintes(): Int {
            val c = Calendar.getInstance(Locale.ENGLISH)
            val Hr24 = c.get(Calendar.HOUR_OF_DAY)
            val Min = c.get(Calendar.MINUTE)
            return ((Hr24 * 60) + Min)
        }

        fun convertMinuteFromHHMMSS(timeInHHMMSS: String): String {
            val tokens = timeInHHMMSS.split(":")
            val hours = Integer.parseInt(tokens[0])
            val minutes = Integer.parseInt(tokens[1])
            val seconds = Integer.parseInt(tokens[2])
            val totalMin = (hours * 60) + minutes
            return totalMin.toString()
        }

        fun hideSoftKeyboard(activity: Activity) {
            try {
                val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        fun hideSoftKeyboardFromDialog(activity: Activity) {
            try {
                val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }


        fun removeSoftKeyboard(mActivity: Activity, view: View) {
            try {
                if (!view.isFocused)
                    view.isFocusable = true

                if (view != null) {
                    val imm = mActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        fun getFormattedDate(date: Date): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.time = date
            //2nd of march 2015
            val day = cal.get(Calendar.DATE)

            if (day !in 11..18)
                return when (day % 10) {
                    1 -> SimpleDateFormat("d'st' MMM yyyy", Locale.ENGLISH).format(date)
                    2 -> SimpleDateFormat("d'nd' MMM yyyy", Locale.ENGLISH).format(date)
                    3 -> SimpleDateFormat("d'rd' MMM yyyy", Locale.ENGLISH).format(date)
                    else -> SimpleDateFormat("d'th' MMM yyyy", Locale.ENGLISH).format(date)
                }
            return SimpleDateFormat("d'th' MMM yyyy", Locale.ENGLISH).format(date)
        }

        fun getDobFormattedDate(date: Date): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.time = date
            //2nd of march 2015
            val day = cal.get(Calendar.DATE)
            var dateString = ""
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            try {
                dateString = dateFormat.format(date) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return dateString
        }

        fun getBillFormattedDate(date: Date): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.time = date
            //2nd of march 2015
            val day = cal.get(Calendar.DATE)
            var dateString = ""
            val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
            try {
                dateString = dateFormat.format(date) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return dateString
        }


        fun getFormattedDateFromDateTime(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            var date = Date()
            try {
                date = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.time = date
            //2nd of march 2015
            val day = cal.get(Calendar.DATE)

            if (day !in 11..18)
                return when (day % 10) {
                    1 -> SimpleDateFormat("d'st' MMM yyyy", Locale.ENGLISH).format(date)
                    2 -> SimpleDateFormat("d'nd' MMM yyyy", Locale.ENGLISH).format(date)
                    3 -> SimpleDateFormat("d'rd' MMM yyyy", Locale.ENGLISH).format(date)
                    else -> SimpleDateFormat("d'th' MMM yyyy", Locale.ENGLISH).format(date)
                }
            return SimpleDateFormat("d'th' MMM yyyy", Locale.ENGLISH).format(date)
        }

        fun getFormattedDateFromDate(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var date = Date()
            try {
                date = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.time = date
            //2nd of march 2015
            val day = cal.get(Calendar.DATE)

            if (day !in 11..18)
                return when (day % 10) {
                    1 -> SimpleDateFormat("d'st' MMM yyyy", Locale.ENGLISH).format(date)
                    2 -> SimpleDateFormat("d'nd' MMM yyyy", Locale.ENGLISH).format(date)
                    3 -> SimpleDateFormat("d'rd' MMM yyyy", Locale.ENGLISH).format(date)
                    else -> SimpleDateFormat("d'th' MMM yyyy", Locale.ENGLISH).format(date)
                }
            return SimpleDateFormat("d'th' MMM yyyy", Locale.ENGLISH).format(date)
        }

        fun getCurrentDateTime12FormatToAttr(date: String): String {
//           loginDate: "dd-MMM-yy"
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
            var convertedDate = Date()

            try {
                convertedDate = dateFormat.parse(date) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH)
            return f.format(convertedDate)
        }

        fun getMilisFromMeredian(time: String): Long {
            val formatter = SimpleDateFormat("hh:mm a", Locale.ENGLISH) // I assume d-M, you may refer to M-d for month-day instead.
            val date = formatter.parse(time) // You will need try/catch around this
            val millis = date.time

            return millis
        }


        fun getCurrentDateTime12Format(): String {
//           loginDate: "dd-MMM-yy"
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
            val convertedDate = Date()
            /*try {
                convertedDate = dateFormat.parse(loginDate) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH)*/
            return dateFormat.format(convertedDate)
        }

        fun getFormattedDateAtt(date: Date): String {
            return SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH).format(date)
        }

        fun getFormattedDateForApi(date: Date): String {
            return SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date)
        }

        fun getAttendanceFormattedDateForApi(date: Date): String {
            return SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH).format(date)
        }

        fun fetDateSuffix(day: Int): String {

            if (day < 10) {
                return if (day == 1)
                    "st"
                else if (day == 2)
                    "nd"
                else if (day == 3)
                    "rd"
                else "th"
            } else {
                return if (day % 10 == 1)
                    "st"
                else if (day % 10 == 2)
                    "nd"
                else if (day % 10 == 3)
                    "rd"
                else
                    "th"
            }
        }

        fun isValidateMobile(pHoneNo: String): Boolean {

            return pHoneNo.length == 10
        }


        fun isValidEmail(emailId: String): Boolean {
            return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(emailId).matches()
        }

        fun isValidPanCardNo(panCardNo:String) : Boolean
        {
            var regex :String = "[A-Z]{5}[0-9]{4}[A-Z]{1}";
            var p : Pattern = Pattern.compile(regex);
            if (panCardNo == null)
            {
                return false;
            }
            var m : Matcher = p.matcher(panCardNo);
            return m.matches();
        }

        fun isValidGSTINCardNo(panCardNo:String) : Boolean
        {
            var regex :String = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[A-Z,0-9]{3}";
            var p : Pattern = Pattern.compile(regex);
            if (panCardNo == null)
            {
                return false;
            }
            var m : Matcher = p.matcher(panCardNo);
            return m.matches();
        }

        /**
         * New Implementation
         */
        fun getDayNumberSuffix(day: Int): String {
            if (day in 11..13) {
                return "th "
            }
            return when (day % 10) {
                1 -> "st "
                2 -> "nd "
                3 -> "rd "
                else -> "th "
            }
        }

        fun convertToCommonFormat(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val d = f.parse(date)
                val date = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                return date.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }

        }

        fun convertToBillingFormat(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val d = f.parse(date)
                val date = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
                return date.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }

        }

        fun convertToDateLikeOrderFormat(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val d = f.parse(date)
                val date = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                return date.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }

        }

        fun convertDateTimeToCommonFormat(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                val d = f.parse(date)
                val date = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
                return date.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }

        }

        fun convertCorrectDateTimeToOrderDate(date: String): String {
            return try {
                val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                val d = f.parse(date)
                val date = SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH)
                date.format(d)
    //            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                getCurrentDate()
            }
        }

        fun convertDateTimeToTime(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                val d = f.parse(date)
                val date = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
                return date.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }

        }

        fun convertFromRightToReverseFormat(date: String): String {
            return try {
                val f = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
                val d = f.parse(date)
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                date.format(d)
                //            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                getCurrentDate()
            }

        }

        fun convertFromRightToReverseFormatWithTime(date: String): String {
            try {
                val f = SimpleDateFormat("dd-MM-yyyy HH:mm a", Locale.ENGLISH)
                val d = f.parse(date)
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                return date.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }
        }

        fun getDateFromDateTime(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                val d = f.parse(date)
                val date_ = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                return date_.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }
        }

        fun getBillingDateFromCorrectDate(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val d = f.parse(date)
                val date_ = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
                return date_.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }
        }

        fun getMeredianTimeFromDateTime(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                val d = f.parse(date)
                val date_ = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                return date_.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }
        }

        fun getMeredianTimeFromISODateTime(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                val d = f.parse(date)
                val date_ = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                return date_.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }
        }

        fun convertToSelectedDateReimbursement(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val d = f.parse(date)
                val date_ = SimpleDateFormat("dd MMM, yyyy ", Locale.ENGLISH)
                return date_.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }
        }

        fun convertToNotificationDateTime(date: String): String {
            try {
                val f = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
                val d = f.parse(date)
                val date_ = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.ENGLISH)
                return date_.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }
        }

        fun convertBilingDateToIdealFormat(date: String): String {
            try {
                val f = SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH)
                val d = f.parse(date)
                val date_ = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                return date_.format(d)
//            System.out.println("Time: " + time.format(d))
            } catch (e: ParseException) {
                e.printStackTrace()
                return getCurrentDate()
            }
        }

        /**
         * Purpose: internet checking
         */
        fun isOnline(mContext: Context): Boolean {
            val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            var info: NetworkInfo? = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            // test for connection for WIFI
            if (info != null && info.isAvailable && info.isConnected) {
                return true
            }
            info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
            // test for connection for Mobile
            return info != null && info.isAvailable && info.isConnected
        }

        fun getNetworkType(context: Context): String? {
            var networkType: String? = null
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetworkInfo
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                    networkType = "WiFi"
                } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                    networkType = "Mobile"
                }
            } else {
                networkType = ""
            }
            return networkType
        }

        @SuppressLint("MissingPermission")
        fun mobNetType(context: Context): String {
            val netType = getNetworkType(context)
            if (TextUtils.isEmpty(netType) || netType.equals("WiFi", ignoreCase = true))
                return ""

            val mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val networkType = mTelephonyManager.networkType
            when (networkType) {
                TelephonyManager.NETWORK_TYPE_GPRS,
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_CDMA,
                TelephonyManager.NETWORK_TYPE_1xRTT,
                TelephonyManager.NETWORK_TYPE_IDEN,
                TelephonyManager.NETWORK_TYPE_GSM
                -> return "2G"
                TelephonyManager.NETWORK_TYPE_UMTS,
                TelephonyManager.NETWORK_TYPE_EVDO_0,
                TelephonyManager.NETWORK_TYPE_EVDO_A,
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_EVDO_B,
                TelephonyManager.NETWORK_TYPE_EHRPD,
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_TD_SCDMA
                -> return "3G"
                TelephonyManager.NETWORK_TYPE_LTE
                -> return "4G"
            /*TelephonyManager.NETWORK_TYPE_NR
            -> return "5G"*/
                else -> return "Unknown"
            }
        }

        fun getBatteryPercentage(context: Context): Int {
            val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, iFilter)

            var level = -1
            var scale = -1

            if (batteryStatus != null) {
                level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            }

            val batteryPct = level / scale.toDouble()

            return (batteryPct * 100).toInt()
        }

        fun getBatteryStatus(context: Context): String {
            val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, iFilter)

            var status = -1

            if (batteryStatus != null)
                status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)

            return when (status) {
                BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
                BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
                BatteryManager.BATTERY_STATUS_FULL -> "Full"
                BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
                BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
                else -> ""
            }
        }

        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }


        private fun capitalize(s: String?): String {
            if (s == null || s.isEmpty()) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                Character.toUpperCase(first) + s.substring(1)
            }
        }

        fun changeAttendanceDateFormatToCurrent(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            return f.format(convertedDate)

        }

        fun convertDateStringToLong(dateString: String?): Long {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                val date = sdf.parse(dateString)

                val startDate = date.time
                startDate
            } catch (e: ParseException) {
                e.printStackTrace()
                0
            }
        }

        fun getThreeMonthsPreviousDate(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.time = dateFormat.parse(dateString)
            cal.add(Calendar.DATE, -89)
            return dateFormat.format(cal.time) //your formatted date here
        }


        fun getOneDayPreviousDate(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.time = dateFormat.parse(dateString)
            cal.add(Calendar.DATE, -1)
            return dateFormat.format(cal.time) //your formatted date here
        }

        fun changeAttendanceDateFormat(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
            return f.format(convertedDate)

        }

        fun getMonthYearFromReverseFormat(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("MMM, yyyy", Locale.ENGLISH)
            return f.format(convertedDate)

        }

        fun getMonthFromReverseFormat(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("MMM", Locale.ENGLISH)
            return f.format(convertedDate)
        }

        fun getMonthNoFromReverseFormat(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("MM", Locale.ENGLISH)
            return f.format(convertedDate)
        }

        fun getYearFromReverseFormat(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("yyyy", Locale.ENGLISH)
            return f.format(convertedDate)

        }

        fun getDayFromReverseFormat(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("dd", Locale.ENGLISH)
            return f.format(convertedDate)

        }

        @SuppressLint("NewApi")
        fun getNextDateForShopActi(): String {
            val tomorrow = LocalDate.now().plus(1, ChronoUnit.DAYS)
            val formattedTomorrow = tomorrow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            return formattedTomorrow.toString()
        }

        fun changeLocalDateFormatToAtt(dateString: String): String {
            val dateFormat = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
            return f.format(convertedDate)

        }

        fun changeLocalDateFormatToAtte(dateString: String): String {
            val dateFormat = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
            return f.format(convertedDate)

        }

        fun getTimeStampFromDate(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:MM:SS", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            return Timestamp(convertedDate.time).toString()
        }

        fun getDateFromDateString(dateString: String): Date? {
            /* val format = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
             try {
                 val date = format.parse(dateString)
                 return date
             } catch (e: ParseException) {
                 e.printStackTrace()
             }*/

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var convertedDate = Date()

            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            val newDateString = f.format(convertedDate)

            try {
                val date = f.parse(newDateString)
                return date
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return null
        }

        fun getTimeStampFromDateOnly(dateString: String): Long {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            return convertedDate.time
        }

        fun getLongTimeStampFromDate(dateString: String): Long {
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            return convertedDate.time
        }

        fun getLongTimeStampFromDate2(dateString: String): Long {
            val dateFormat = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            return convertedDate.time
        }

        fun getStrinTODate(dateString: String): Date {
            val dateFormat = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString)
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }

            return convertedDate
        }

        fun getCurrentISODateTime(): String {
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getDate(time: Long): String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = time
            return DateFormat.format("yyyy-MM-dd HH:mm:ss", cal).toString()
        }

        fun getTimeStamp(dateTime: String): Long {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val date = formatter.parse(dateTime) as Date
            return date.time
        }

        fun getTimeStampFromValidDetTime(dateTime: String): Long {
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val date = formatter.parse(dateTime) as Date
            return date.time
        }

        fun getCurrentISODateAtt(): String {
            val df = SimpleDateFormat("yyyy-MM-dd'T'00:00:00", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getCurrentDateForShopActi(): String {
            val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getCurrentMonthDayForShopActi(): String {
            val df = SimpleDateFormat("MM-dd", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun changeAttendanceDateFormatToMonthDay(dateString: String): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("MM-dd", Locale.ENGLISH)
            return f.format(convertedDate)

        }

        fun getCurrentDateMonth(): String {
            val df = SimpleDateFormat("ddMMyy", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getCurrentMonth(): String {
            val df = SimpleDateFormat("MMMM", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getCurrentYear(): String {
            val df = SimpleDateFormat("yyyy", Locale.ENGLISH)
            return df.format(Date()).toString()
        }

        fun getFormattedDateString(c: Calendar): String {
//            val c = Calendar.getInstance(Locale.ENGLISH)
//            System.out.println("Current time => " + c.time)

            val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val formattedDate = df.format(c.time)
            return formattedDate.toString()
        }

        fun getDateTimeFromTimeStamp(timeStamp: Long): String? {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            val dateString = formatter.format(Date(timeStamp))
            return dateString
        }

        fun getDayFromEmptyDateTimeStamp(dateString: String): String? {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
            var convertedDate = Date()
            try {
                convertedDate = dateFormat.parse(dateString) //"20130526160000"
            } catch (e: ParseException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            }
            val f = SimpleDateFormat("EEE", Locale.ENGLISH)
            return f.format(convertedDate)
        }

        fun getVersionName(context: Context): String {
            var versionName = ""
            var versionCode = -1
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0) as PackageInfo
                versionName = packageInfo.versionName
//                versionCode = packageInfo.versionCode
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
            return "Version " + versionName

        }

        fun getTimeStampFromShopId(shopId: String): String {
            var notificationId = shopId.split("_")[1]
            return notificationId
        }

        fun getCurrentMonthInNum(): String {
            val now = Calendar.getInstance(Locale.ENGLISH)
            var month = (now.get(Calendar.MONTH) + 1)
            return month.toString()
        }

        fun getLastMonthInNum(): String {
            val now = Calendar.getInstance(Locale.ENGLISH)
            var month = (now.get(Calendar.MONTH))
            return month.toString()
        }

        fun getStartDateOfCurrentWeek(): String {
            val c1 = Calendar.getInstance(Locale.ENGLISH)
            //first day of week
            c1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            return getFormattedDateFromCalender(c1)
        }

        fun getEndDateOfCurrentWeek(): String {
            val c1 = Calendar.getInstance(Locale.ENGLISH)
            //first day of week
            c1.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            c1.add(Calendar.DATE, +1)
            return getFormattedDateFromCalender(c1)
        }

        fun getStartDateOflastWeek(): String {
            val c1 = Calendar.getInstance(Locale.ENGLISH)
            //first day of week
            c1.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            c1.add(Calendar.DATE, -7)
            return getFormattedDateFromCalender(c1)
        }


        fun getEndDateOfLastWeek(): String {
            val c1 = Calendar.getInstance(Locale.ENGLISH)
            //first day of week
            c1.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
            c1.add(Calendar.DATE, -6)
            return getFormattedDateFromCalender(c1)
        }


        /* fetch formatted date yyyy/MM/dd */
        fun getFormattedDateFromCalender(c1: Calendar): String {
            val year1 = c1.get(Calendar.YEAR)
            val month1 = c1.get(Calendar.MONTH) + 1
            val day1 = c1.get(Calendar.DAY_OF_MONTH)
            var month = month1.toString()
            if (month1 < 10)
                month = "0" + month1
            return (year1.toString() + "/" + month + "/" + day1.toString())
        }

        fun getCurrentDateForCons(): String {
            val c = Calendar.getInstance(Locale.ENGLISH)
            val df = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
            val formattedDate = df.format(c.time)
            return formattedDate.toString()
        }

        fun getFirstDateOfLastMonth(): String {
            val aCalendar = Calendar.getInstance(Locale.ENGLISH)
// add -1 month to current month
            aCalendar.add(Calendar.MONTH, -1)
// set DATE to 1, so first date of previous month
            aCalendar.set(Calendar.DATE, 1)
            val df = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
            val formattedDate = df.format(aCalendar.time)
            return formattedDate.toString()
        }

        fun getEndDateOflastMonth(): String {
            val aCalendar = Calendar.getInstance(Locale.ENGLISH)
// add -1 month to current month
            aCalendar.add(Calendar.MONTH, -1)
// set DATE to 1, so first date of previous month
            aCalendar.set(Calendar.DATE, aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val df = SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
            val formattedDate = df.format(aCalendar.time)
            return formattedDate.toString()


        }

        fun isLocationEnabled(context: Context): Boolean {
            var locationMode = 0
            val locationProviders: String

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    locationMode = Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)

                } catch (e: SettingNotFoundException) {
                    e.printStackTrace()
                    return false
                }

                return locationMode != Settings.Secure.LOCATION_MODE_OFF

            } else {
                locationProviders = Settings.Secure.getString(context.contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED)
                return !TextUtils.isEmpty(locationProviders)
            }

        }

        fun getCategoryNameFromId(type: String, mContext: Context): String {
            when (type.toInt()) {
                1 -> {
                    return mContext.getString(R.string.shop_type)
                }
                2 -> {
                    return mContext.getString(R.string.pp_type)
                }
                3 -> {
                    return mContext.getString(R.string.new_party_type)
                }
                4 -> {
                    return mContext.getString(R.string.distributor_type)
                }
                5 -> {
                    return mContext.getString(R.string.diamond_type)
                }
            }
            return mContext.getString(R.string.shop_type)
        }

        fun getIMEI() {

        }

        fun getPhotoFilePath(context: Context, fileName: String): String {
            val filePath = getHiddenAppFolder(context) + File.separator + fileName + ".jpg"
            val destinationFolderFile = File(filePath)
//            if (!destinationFolderFile.exists())
//                destinationFolderFile.createNewFile()

            return filePath;
        }

        fun getHiddenAppFolder(context: Context): String {
            //val destinationFolder = Environment.getExternalStorageDirectory().path + File.separator + context.resources.getString(R.string.app_name)
            //27-09-2021
            val destinationFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path + File.separator + context.resources.getString(R.string.app_name)
            val destinationFolderFile = File(destinationFolder)
            if (!destinationFolderFile.exists())
                destinationFolderFile.mkdir()
            else if (!destinationFolderFile.isDirectory())
                destinationFolderFile.mkdir()
            return destinationFolder;
        }

        fun getFileExt(fileName: String): String {
            return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length)
        }

        fun getNewCompressImage(c: Context, filePath: String): Long {
            val file = File(filePath)

            Log.e("Dashboard", "image file size before compression-----------------> " + file.length())

            try {
                val uri = Uri.parse(filePath)
                val bitmap = getScaledBitmap(c, uri, 1080)
                //val bitmap = BitmapFactory.decodeFile(filePath)
                //bitmap.compress(Bitmap.CompressFormat.JPEG, 2, FileOutputStream(file))

                //Convert bitmap to byte array
                val bos = ByteArrayOutputStream()
                //bitmap.compress(Bitmap.CompressFormat.PNG, 2, bos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos)
                val bitmapdata = bos.toByteArray()

                //write the bytes in file
                val fos = FileOutputStream(file)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()

                Log.e("Dashboard", "image file path-----------------> $filePath")
                Log.e("Dashboard", "image file size after compression-----------------> " + file.length())
                return file.length()
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }

        @Throws(FileNotFoundException::class)
        private fun getScaledBitmap(c: Context, uri: Uri, requiredSize: Int): Bitmap {

            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o)

            /*if (BuildConfig.LOG_ENABLED)
            Log.d("BITMAP - original size: " + o.outWidth + "x" + o.outHeight);*/

            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1

            while (true) {
                if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                    break
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }

            /*if (BuildConfig.LOG_ENABLED)
            Log.d("BITMAP - output size: " + (int)(o.outWidth/(float)scale) + "x" + (int)(o.outHeight/(float)scale));*/

            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale

            return BitmapFactory.decodeStream(c.contentResolver.openInputStream(uri), null, o2)!!
        }


        fun getCompressImage(filePath: String): Long {
            val file = File(filePath)

            //XLog.e("Dashboard", "image file size before compression=======> " + file.length())

            XLog.e("Dashboard: image file size before compression=======> " + file.length())

            try {
                val bitmapImage = BitmapFactory.decodeFile(filePath)
                //bitmap.compress(Bitmap.CompressFormat.JPEG, 2, FileOutputStream(file))

                val bitmap = Bitmap.createScaledBitmap(bitmapImage, 500, 500, true);

                //Convert bitmap to byte array
                val bos = ByteArrayOutputStream()
                //bitmap.compress(Bitmap.CompressFormat.PNG, 2, bos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos)
                val bitmapdata = bos.toByteArray()

                //write the bytes in file
                val fos = FileOutputStream(file)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()

                XLog.e("Dashboard: image file path======> $filePath")
                XLog.e("Dashboard: image file size after compression=======> " + file.length())
                return file.length()
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                XLog.e("Dashboard: " + e.localizedMessage)
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
                XLog.e("Dashboard: " + e.localizedMessage)
            } catch (e: Exception) {
                e.printStackTrace()
                XLog.e("Dashboard: " + e.localizedMessage)
            }
            return 0
        }

        fun getCompressOldImage(filePath: String, context: Context): Long {
            var updatedFilePath = ""
            if (filePath.contains("file://")) {
                updatedFilePath = filePath.substring(6, filePath.length)
            }
            val file = File(updatedFilePath)
            val file_path = Uri.fromFile(file)

            Log.e("Dashboard", "file uri-----------------> $file_path")
            Log.e("Dashboard", "image file size before compression-----------------> " + file.length())

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(filePath))
                //bitmap.compress(Bitmap.CompressFormat.JPEG, 2, FileOutputStream(file))

                //Convert bitmap to byte array
                val bos = ByteArrayOutputStream()
                //bitmap.compress(Bitmap.CompressFormat.PNG, 2, bos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos)
                val bitmapdata = bos.toByteArray()

                //write the bytes in file
                val fos = FileOutputStream(file)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()

                Log.e("Dashboard", "image file path-----------------> $filePath")
                Log.e("Dashboard", "image file size after compression-----------------> " + file.length())
                return file.length()
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }

        fun getCompressOldImagev1(filePath: String, context: Context,qlty:Int): Long {
            var updatedFilePath = ""
            if (filePath.contains("file://")) {
                updatedFilePath = filePath.substring(6, filePath.length)
            }
            val file = File(updatedFilePath)
            val file_path = Uri.fromFile(file)

            Log.e("Dashboard", "file uri-----------------> $file_path")
            Log.e("Dashboard", "image file size before compression-----------------> " + file.length())

            try {

                var bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(filePath))
                //bitmap.compress(Bitmap.CompressFormat.JPEG, 2, FileOutputStream(file))

                //Convert bitmap to byte array

                var resized = Bitmap.createScaledBitmap(bitmap,(bitmap.getWidth()*0.2).toInt(), (bitmap.getHeight()*0.2).toInt(), true);
                bitmap=resized
                val bos = ByteArrayOutputStream()
                //bitmap.compress(Bitmap.CompressFormat.PNG, 2, bos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
                val bitmapdata = bos.toByteArray()

                //write the bytes in file
                val fos = FileOutputStream(file)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()

                Log.e("Dashboard", "image file path-----------------> $filePath")
                Log.e("Dashboard", "image file size after compression-----------------> " + file.length())
                return file.length()
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }

        fun getCompressBillingImage(filePath: String, context: Context): Long {
            var updatedFilePath = ""
            if (filePath.contains("file://")) {
                updatedFilePath = filePath.substring(6, filePath.length)
            }
            val file = File(updatedFilePath)
            val file_path = Uri.fromFile(file)

            Log.e("Dashboard", "file uri=========> $file_path")
            Log.e("Dashboard", "image file size before compression=========> " + getFileSizeInKB(file.length()) + " KB")

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(filePath))
                //bitmap.compress(Bitmap.CompressFormat.JPEG, 2, FileOutputStream(file))

                //Convert bitmap to byte array
                val bos = ByteArrayOutputStream()
                //bitmap.compress(Bitmap.CompressFormat.PNG, 2, bos);

                if (file.toString().contains(".jpg") || file.toString().contains(".jpeg"))
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, bos)
                else
                    bitmap.compress(Bitmap.CompressFormat.PNG, 5, bos);

                val bitmapdata = bos.toByteArray()

                //write the bytes in file
                val fos = FileOutputStream(file)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()

                Log.e("Dashboard", "image file path============> $filePath")
                Log.e("Dashboard", "image file size after compression=========> " + getFileSizeInKB(file.length()) + " KB")
                return file.length()
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }

        private fun getFileSizeInKB(fileSize: Long): Long {
            return fileSize / 1024
        }

        fun getCompressContentImage(uri: Uri, context: Activity): Long {
            /*var updatedFilePath = ""
            if (filePath.contains("content://")) {
                updatedFilePath = filePath.substring(9, filePath.length)
            }*/
            val file = File(getRealPathFromUri(context, uri))

            Log.e("Dashboard", "image file size before compression-----------------> " + file.length())

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                //bitmap.compress(Bitmap.CompressFormat.JPEG, 2, FileOutputStream(file))

                //Convert bitmap to byte array
                val bos = ByteArrayOutputStream()
                //bitmap.compress(Bitmap.CompressFormat.PNG, 2, bos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bos)
                val bitmapdata = bos.toByteArray()

                //write the bytes in file
                val fos = FileOutputStream(file)
                fos.write(bitmapdata)
                fos.flush()
                fos.close()

                Log.e("Dashboard", "image file path-----------------> " + uri.toString())
                Log.e("Dashboard", "image file size after compression-----------------> " + file.length())
                return file.length()
            } catch (e: FileNotFoundException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: IOException) {
                // TODO Auto-generated catch block
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }

        fun getRealPathFromUri(activity: Activity, contentUri: Uri): String {
            val proj = arrayOf(MediaStore.Audio.Media.DATA)
            val cursor = activity.managedQuery(contentUri, proj, null, null, null)
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        }

        fun saveSharedPreferenceslogShareinLogin(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("LOG_SHARE_IN_LOGIN", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("isLogShareinLogin", value)
            prefsEditor.apply()
        }

        fun getSharedPreferenceslogShareinLogin(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("LOG_SHARE_IN_LOGIN", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("isLogShareinLogin",false)
        }

        fun saveSharedPreferencesCompetitorImgEnable(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("CompetitorImg_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("isCompetitorImgEnable", value)
            prefsEditor.apply()
        }

        fun getSharedPreferenceslogCompetitorImgEnable(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("CompetitorImg_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("isCompetitorImgEnable",false)
        }

        fun saveSharedPreferencesOrderStatusRequired(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("OrderStatus_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("isOrderStatusRequired", value)
            prefsEditor.apply()
        }

        fun getSharedPreferenceslogOrderStatusRequired(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("OrderStatus_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("isOrderStatusRequired",false)
        }

        fun saveSharedPreferencesCurrentStock(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("CURRENT_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("IsCurrentStockEnable", value)
            prefsEditor.apply()
        }

        fun getSharedPreferencesCurrentStock(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("CURRENT_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("IsCurrentStockEnable",false)
        }

        fun saveSharedPreferencesCurrentStockApplicableForAll(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("CURRENT_APPLICABLE_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("IsCurrentStockApplicableforAll", value)
            prefsEditor.apply()
        }

        fun getSharedPreferencesCurrentStockApplicableForAll(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("CURRENT_APPLICABLE_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("IsCurrentStockApplicableforAll",false)
        }

        fun saveSharedPreferencesIscompetitorStockRequired(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("IsCompetitorStock_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("IscompetitorStockRequired", value)
            prefsEditor.apply()
        }

        fun getSharedPreferencesIscompetitorStockRequired(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("IsCompetitorStock_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("IscompetitorStockRequired",false)
        }
        fun saveSharedPreferencesIsCompetitorStockforParty(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("CompetitorStock_Party_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("IsCompetitorStockforParty", value)
            prefsEditor.apply()
        }

        fun getSharedPreferencesIsCompetitorStockforParty(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("CompetitorStock_Party_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("IsCompetitorStockforParty",false)
        }


        fun saveSharedPreferencesIsFaceDetectionOn(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("IsFaceDetectionOn_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("IsFaceDetectionOn", value)
            prefsEditor.apply()
        }

        fun getSharedPreferencesIsFaceDetectionOn(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("IsFaceDetectionOn_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("IsFaceDetectionOn",false)
        }

        fun saveSharedPreferencesIsFaceDetection(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("IsFaceDetection_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("IsFaceDetection", value)
            prefsEditor.apply()
        }

        fun getSharedPreferencesIsFaceDetection(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("IsFaceDetection_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("IsFaceDetection",false)
        }


        fun saveSharedPreferencesIsFaceDetectionWithCaptcha(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("IsFaceDetectionWithCaptcha_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("IsFaceDetectionWithCaptcha", value)
            prefsEditor.apply()
        }

        fun getSharedPreferencesIsFaceDetectionWithCaptcha(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("IsFaceDetectionWithCaptcha_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("IsFaceDetectionWithCaptcha",false)
        }



        fun saveSharedPreferencesIsScreenRecorderEnable(context: Context,value:Boolean){
            val mPrefs = context.getSharedPreferences("IsScreenRecorderEnable_STATUS", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            prefsEditor.putBoolean("IsScreenRecorderEnable", value)
            prefsEditor.apply()
        }

        fun getSharedPreferencesIsScreenRecorderEnable(context: Context):Boolean{
            val mPrefs = context.getSharedPreferences("IsScreenRecorderEnable_STATUS", Context.MODE_PRIVATE)
            return mPrefs.getBoolean("IsScreenRecorderEnable",false)
        }










        fun saveSharedPreferencesStateList(context: Context, alarmData: ArrayList<LoginStateListDataModel>) {
            val mPrefs = context.getSharedPreferences("STATE_LIST", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            val gson = Gson()
            val json = gson.toJson(alarmData)
            prefsEditor.putString("stateJson", json)
            prefsEditor.commit()
        }

        fun loadSharedPreferencesStateList(context: Context): ArrayList<LoginStateListDataModel> {
            var alarmData: ArrayList<LoginStateListDataModel> = ArrayList<LoginStateListDataModel>()
            val mPrefs = context.getSharedPreferences("STATE_LIST", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = mPrefs.getString("stateJson", "")
            if (json?.isEmpty()!!) {
                alarmData = ArrayList<LoginStateListDataModel>()
            } else {
                val type = object : TypeToken<List<LoginStateListDataModel>>() {

                }.type
                alarmData = gson.fromJson(json, type)
            }
            return alarmData
        }

        fun saveSharedPreferencesProductRateList(context: Context, productRate: ArrayList<ProductRateDataModel>) {
            val mPrefs = context.getSharedPreferences("PRODUCT_RATE_LIST", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            val gson = Gson()
            val json = gson.toJson(productRate)
            prefsEditor.putString("productRateJson", json)
            prefsEditor.apply()
        }

        fun loadSharedPreferencesProductRateList(context: Context): ArrayList<ProductRateDataModel> {
            val productRate: ArrayList<ProductRateDataModel>
            val mPrefs = context.getSharedPreferences("PRODUCT_RATE_LIST", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = mPrefs.getString("productRateJson", "")
            productRate = if (json?.isEmpty()!!) {
                ArrayList<ProductRateDataModel>()
            } else {
                val type = object : TypeToken<List<ProductRateDataModel>>() {

                }.type
                gson.fromJson(json, type)
            }
            return productRate
        }

        fun saveSharedPreferencesImageText(context: Context, textList: ArrayList<String>) {
            val mPrefs = context.getSharedPreferences("TEXT_LIST", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            val gson = Gson()
            val json = gson.toJson(textList)
            prefsEditor.putString("textListJson", json)
            prefsEditor.apply()
        }

        fun loadSharedPreferencesTextList(context: Context): ArrayList<String> {
            val textList: ArrayList<String>
            val mPrefs = context.getSharedPreferences("TEXT_LIST", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = mPrefs.getString("textListJson", "")
            textList = if (json?.isEmpty()!!) {
                ArrayList<String>()
            } else {
                val type = object : TypeToken<List<String>>() {

                }.type
                gson.fromJson(json, type)
            }
            return textList
        }

        fun saveSharedPreferencesLocation(context: Context, loc: Location) {
            val mPrefs = context.getSharedPreferences("Location", Context.MODE_PRIVATE)
            val prefsEditor = mPrefs.edit()
            val gson = Gson()
            val json = gson.toJson(loc)
            prefsEditor.putString("locJson", json)
            prefsEditor.apply()
        }

        fun loadSharedPreferencesLocation(context: Context): Location? {
            var loc: Location? = null
            val mPrefs = context.getSharedPreferences("Location", Context.MODE_PRIVATE)
            val gson = Gson()
            val json = mPrefs.getString("locJson", "")
            loc = if (json?.isEmpty()!!) {
                null
            } else {
                val type = object : TypeToken<Location>() {

                }.type
                gson.fromJson(json, type)
            }
            return loc
        }

        fun clearPreferenceKey(context: Context, preferenceKey: String) {
            val sp = context.getSharedPreferences(preferenceKey, Context.MODE_PRIVATE)
            if (sp.contains("stateJson")) {
                val editor = sp.edit()
                editor.remove("stateJson")
                editor.apply()
            } else if (sp.contains("productRateJson")) {
                val editor = sp.edit()
                editor.remove("productRateJson")
                editor.apply()
            }
            else if (sp.contains("textListJson")) {
                val editor = sp.edit()
                editor.remove("textListJson")
                editor.apply()
            }
        }

        fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
            return ContextCompat.getDrawable(context, vectorResId)?.run {
                setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
                draw(Canvas(bitmap))
                BitmapDescriptorFactory.fromBitmap(bitmap)
            }
        }

        fun round(value: Double, numberOfDigitsAfterDecimalPoint: Int): Double {
            var bigDecimal = BigDecimal(value)
            bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint, BigDecimal.ROUND_HALF_UP)
            return bigDecimal.toDouble()
        }

        fun areThereMockPermissionApps(context: Context): Boolean {
            var count = 0

            val pm = context.packageManager
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

            for (applicationInfo in packages) {
                try {
                    val packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS)

                    // Get Permissions
                    val requestedPermissions = packageInfo.requestedPermissions

                    if (requestedPermissions != null) {
                        for (i in requestedPermissions.indices) {
                            if (requestedPermissions[i] == "android.permission.ACCESS_MOCK_LOCATION" && applicationInfo.packageName != context.packageName) {
                                count++
                            }
                        }
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e("Got exception ", e.message!!)
                }

            }

            return count > 0
        }

        fun spannedString(hintText: String): SpannableStringBuilder {
            val sb = SpannableStringBuilder(hintText)
            val p = Pattern.compile("\\*", Pattern.CASE_INSENSITIVE)
            val m = p.matcher(hintText)
            while (m.find()) {
                sb.setSpan(ForegroundColorSpan(Color.rgb(255, 0, 0)), m.start(), m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }

            return sb
        }

        fun getCalendarId(context: Context): Long? {
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return null
                }
                val projection = arrayOf(CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)
                var calCursor = context.contentResolver.query(
                        CalendarContract.Calendars.CONTENT_URI,
                        projection,
                        CalendarContract.Calendars.VISIBLE + " = 1 AND " + CalendarContract.Calendars.IS_PRIMARY + "=1",
                        null,
                        CalendarContract.Calendars._ID + " ASC"
                )
                if (calCursor != null && calCursor.count <= 0) {
                    calCursor = context.contentResolver.query(
                            CalendarContract.Calendars.CONTENT_URI,
                            projection,
                            CalendarContract.Calendars.VISIBLE + " = 1",
                            null,
                            CalendarContract.Calendars._ID + " ASC"
                    )
                }
                if (calCursor != null) {
                    if (calCursor.moveToFirst()) {
                        val calName: String
                        val calID: String
                        val nameCol = calCursor.getColumnIndex(projection[1])
                        val idCol = calCursor.getColumnIndex(projection[0])
                        calName = calCursor.getString(nameCol)
                        calID = calCursor.getString(idCol)
                        Log.d("Local calendar details", "Calendar name $calName Calendar ID =$idCol")
                        calCursor.close()
                        return java.lang.Long.valueOf(calID)
                    }
                }
            }
            catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return null
        }

        fun changeLanguage(context: Context, language: String) {
            val config = context.resources.configuration
            val locale = Locale(language)
            Locale.setDefault(locale)
            config.locale = locale
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
        }

        fun encodeEmojiAndText(message: String): String {
            return try {
                StringEscapeUtils.escapeJava(message)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                message
            }

        }

        fun decodeEmojiAndText(message: String): String {
            return try {
                StringEscapeUtils.unescapeJava(message)
            } catch (e: Exception) {
                e.printStackTrace()
                message
            }
        }

        fun createQrCode(content: String) : Bitmap? {
            val writer = QRCodeWriter()
            return try {
                val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 812, 812)
                val width = bitMatrix.width
                val height = bitMatrix.height
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                bmp
            } catch (e: WriterException) {
                e.printStackTrace()
                null
            }
        }

        fun hiFirstNameText() : String {
            try{
                val firstName = Pref.user_name?.substring(0, Pref.user_name?.indexOf(" ")!!)
                return "Hi $firstName"
            }catch (ex:Exception){
                return "Hi $Pref.user_name"
            }

        }

        fun getAndroidVersion(): String {
            when(Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.BASE, Build.VERSION_CODES.BASE_1_1 -> {
                    return "Base"
                }
                Build.VERSION_CODES.CUPCAKE -> {
                    return "Cupcake"
                }
                Build.VERSION_CODES.DONUT -> {
                    return "Donut"
                }
                Build.VERSION_CODES.ECLAIR, Build.VERSION_CODES.ECLAIR_0_1, Build.VERSION_CODES.ECLAIR_MR1 -> {
                    return "Eclair"
                }
                Build.VERSION_CODES.FROYO -> {
                    return "Froyo"
                }
                Build.VERSION_CODES.GINGERBREAD, Build.VERSION_CODES.GINGERBREAD_MR1 -> {
                    return "Gingerbread"
                }
                Build.VERSION_CODES.HONEYCOMB, Build.VERSION_CODES.HONEYCOMB_MR1, Build.VERSION_CODES.HONEYCOMB_MR2 -> {
                    return "Honeycomb"
                }
                Build.VERSION_CODES.ICE_CREAM_SANDWICH, Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 -> {
                    return "Ice Cream Sandwich"
                }
                Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.JELLY_BEAN_MR1, Build.VERSION_CODES.JELLY_BEAN_MR2 -> {
                    return "Jelly Bean"
                }
                Build.VERSION_CODES.KITKAT, Build.VERSION_CODES.KITKAT_WATCH -> {
                    return "Kitkat"
                }
                Build.VERSION_CODES.LOLLIPOP, Build.VERSION_CODES.LOLLIPOP_MR1 -> {
                    return "Lollipop"
                }
                Build.VERSION_CODES.M -> {
                    return "Marshmallow"
                }
                Build.VERSION_CODES.N, Build.VERSION_CODES.N_MR1 -> {
                    return "Nougat"
                }
                Build.VERSION_CODES.O, Build.VERSION_CODES.O_MR1 -> {
                    return "Oreo"
                }
                28 -> {
                    return "Pie"
                }
                29 -> {
                    return "Q"
                }
                30 -> {
                    return "11"
                }
                31 -> {
                    return "12"
                }
                else -> {
                    return Build.VERSION.SDK_INT.toString()
                }
            }
        }

        /*06-09-2021 Click on Single handle*/
        fun isSingleClick(): Boolean {
            val currentClickTime = SystemClock.uptimeMillis()
            val elapsedTime = currentClickTime - mLastClickTime
            mLastClickTime = currentClickTime
            return elapsedTime > CLICK_MIN_INTERVAL
        }

        fun getRandomNumber(digit:Int):String{
            val rnd = Random()
            var number =  0
            if(digit==1){
                number = rnd.nextInt(9)
                return String.format("%01d", number)
            }
            if(digit==2){
                number = rnd.nextInt(99)
                return String.format("%02d", number)
            }
            if(digit==3){
                number = rnd.nextInt(999)
                return String.format("%03d", number)
            }
            if(digit==4){
                number = rnd.nextInt(9999)
                return String.format("%04d", number)
            }
            if(digit==5){
                number = rnd.nextInt(99999)
                return String.format("%05d", number)
            }
            if(digit==6){
                number = rnd.nextInt(999999)
                return String.format("%06d", number)
            }
            return ""
        }

        /*fun getDurationFromOnlineVideoLink(link: String) : String {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(link, HashMap<String, String>())
            val time: String = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val timeInMillisec = time.toLong()
            retriever.release()
            val duration: String = convertMillieToHMmSs(timeInMillisec)!!
            return duration
        }*/

        /*private fun convertMillieToHMmSs(millie: Long): String? {
            val seconds = millie / 1000
            val second = seconds % 60
            val minute = seconds / 60 % 60
            val hour = seconds / (60 * 60) % 24
            val result = ""
            return if (hour > 0) {
                String.format("%02d:%02d:%02d", hour, minute, second)
            } else {
                String.format("%02d:%02d", minute, second)
            }
        }*/
             var isFromOrderToshowSchema = false
    }

}