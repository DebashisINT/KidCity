package com.kcteam.features.averageshop.business

import android.text.TextUtils
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.elvishew.xlog.XLog

/**
 * Created by riddhi on 19/12/17.
 */
class InfoWizard {

    companion object {

        fun getAvergareShopVisitCount(): String {
            /*if (Pref.totalAttendance == "1")
                return AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()).size.toString()
            if (Pref.totalAttendance == "0")
                return Math.round((AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()).size.toFloat() + Pref.totalShopVisited.toFloat())).toString()
            if ((AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()).size.toFloat() + Pref.totalShopVisited.toFloat()).toString() == "0.0")
                return "0"
            var shopsVisitedPerDay: Float = (AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()).size.toFloat() + Pref.totalShopVisited.toFloat()) /
                    (Pref.totalAttendance.toFloat())*/

            val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())

            if (list == null)
                return "0"
            else
                return list.size.toString()
            //return Math.round(shopsVisitedPerDay).toString()
        }

        fun getTotalShopVisitCount(): String {
            val totalShops: Int = if (Pref.totalAttendance == "1")
                AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()).size
            else
                AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi()).size + Pref.totalShopVisited.toInt()
            return totalShops.toString()
        }

        fun getTotalShopVisitTime(selectedDate: String): String {
            /*var totalMinutes = 0
            var list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
            if (list.isNotEmpty()) {
                for (i in 0..list.size - 1) {
                    if (!list[i].totalMinute.isEmpty()) {
                        totalMinutes = totalMinutes + list[i].totalMinute.toInt()
                    }
                }
            }
            try {
                var totalMinutesInt: Int = if (Pref.totalAttendance == "1")
                    totalMinutes
                else
                    totalMinutes + Pref.totalTimeSpenAtShop.toInt()

                var hours = totalMinutesInt / 60
                var hoursToDisplay = hours

                var minutesToDisplay = totalMinutesInt - (hours * 60)

                var minToDisplay: String
                if (minutesToDisplay == 0)
                    minToDisplay = "00"
                else if (minutesToDisplay < 10) minToDisplay = "0" + minutesToDisplay
                else minToDisplay = "" + minutesToDisplay

                var displayValue = hoursToDisplay.toString() + ":" + minToDisplay

                return displayValue
            } catch (e: Exception) {

            }*/

//            var totalMinutes = 0
//
//            val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)
//            list?.forEach {
//                if (it.totalMinute.isNotEmpty())
//                    totalMinutes += it.totalMinute.toInt()
//            }
//
//            val startTime = "00:00"
//            val h = totalMinutes / 60 + Integer.parseInt(startTime.substring(0, 1))
//            val m = totalMinutes % 60 + Integer.parseInt(startTime.substring(3, 4))
//
//            var newHour = ""
//            var newMins = ""
//
//            newHour = if (h.toString().length == 1)
//                "0" + h.toString()
//            else
//                h.toString()
//
//            newMins = if (m.toString().length == 1)
//                "0" + m.toString()
//            else
//                m.toString()
//
//            return newHour + ":" + newMins /*totalMinutes.toString()*/



            var totalSeconds = 0
            val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
            if (list.isNotEmpty()) {
                for (i in 0 until list.size) {
                    if (!list[i].duration_spent.isEmpty()) {
                        val splitTime = list[i].duration_spent.split(":")
                        val hour = Integer.parseInt(splitTime[0])
                        val minute = Integer.parseInt(splitTime[1])
                        val second = Integer.parseInt(splitTime[2])

                        totalSeconds += hour * 3600 + minute * 60 + second
                    }
                }
            }

            try {
                var seconds = 0L
                var minutes: Long = 0
                var hours: Long = 0
                if (totalSeconds >= 60) {
                    minutes = totalSeconds.toLong() / 60
                    seconds = totalSeconds.toLong() % 60

                    if (minutes >= 60) {
                        hours = minutes / 60
                        minutes %= 60
                    }

                } else {
                    seconds = totalSeconds.toLong()
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

                val totalDuration = "$sHours:$sMinute:$sSecond"
                XLog.e("Total duration Spent====> $totalDuration")

                return totalDuration
                
            } catch (e: Exception) {
                e.printStackTrace()
                return "00:00:00"
            }
        }

        fun getAverageShopVisitTimeDuration(selectedDate: String): String {
            /*var totalMinutes = 0
            try {
                val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(selectedDate)
                list?.forEach {
                    if (it.totalMinute.isNotEmpty())
                        totalMinutes += it.totalMinute.toInt()
                }

                val avgMinutes = totalMinutes / list?.size

                val startTime = "00:00:00"
                val h = avgMinutes / 60 + Integer.parseInt(startTime.substring(0, 1))
                val m = avgMinutes % 60 + Integer.parseInt(startTime.substring(3, 4))
                val s = avgMinutes % 60 + Integer.parseInt(startTime.substring(3, 4))

                var newHour = ""
                var newMins = ""

                newHour = if (h.toString().length == 1)
                    "0" + h.toString()
                else
                    h.toString()

                newMins = if (m.toString().length == 1)
                    "0" + m.toString()
                else
                    m.toString()

                return newHour + ":" + newMins
            } catch (e: Exception) {
                e.printStackTrace()
                return "00:00"
            }*/


            var totalSeconds = 0
            val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
            if (list.isNotEmpty()) {
                for (i in 0 until list.size) {
                    if (!list[i].duration_spent.isEmpty()) {
                        val splitTime = list[i].duration_spent.split(":")
                        val hour = Integer.parseInt(splitTime[0])
                        val minute = Integer.parseInt(splitTime[1])
                        val second = Integer.parseInt(splitTime[2])

                        totalSeconds += hour * 3600 + minute * 60 + second
                    }
                }
            }

            try {
                if (list.isNotEmpty()) {
                    val averageSeconds = totalSeconds / list.size

                    var seconds = 0L
                    var minutes: Long = 0
                    var hours: Long = 0
                    if (averageSeconds >= 60) {
                        minutes = averageSeconds.toLong() / 60
                        seconds = averageSeconds.toLong() % 60

                        if (minutes >= 60) {
                            hours = minutes / 60
                            minutes %= 60
                        }

                    } else {
                        seconds = averageSeconds.toLong()
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

                    val totalDuration = "$sHours:$sMinute:$sSecond"
                    XLog.e("Total duration Spent====> $totalDuration")

                    return totalDuration
                }
                else 
                    return "00:00:00"
            } catch (e: Exception) {
                e.printStackTrace()
                return "00:00:00"
            }
        }


        fun getTotalShopVisitTimeForActi(): Int {
            var totalMinutes = 0
            var list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
            if (list.isNotEmpty()) {
                for (i in 0..list.size - 1) {
                    if (!list[i].totalMinute.isEmpty()) {
                        totalMinutes = totalMinutes + list[i].totalMinute.toInt()
                    }
                }
            }

            return totalMinutes

        }

        fun getAverageShopVisitTimeDuration(): String {
            var totalMinutes = 0
            var totalSeconds = 0
            val list = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateForShopActi())
            if (list.isNotEmpty()) {
                for (i in 0 until list.size) {
                    if (!list[i].duration_spent.isEmpty()) {
                        val splitTime = list[i].duration_spent.split(":")
                        val hour = Integer.parseInt(splitTime[0])
                        val minute = Integer.parseInt(splitTime[1])
                        val second = Integer.parseInt(splitTime[2])

                        totalSeconds += hour * 3600 + minute * 60 + second
                    }
                }
            }

            try {
//                 var totalMinutesInt: Int = if (Pref.totalAttendance == "1")
//                     totalMinutes
//                 else
//                     (totalMinutes + Pref.totalTimeSpenAtShop.toInt()) / Pref.totalShopVisited.toInt()
//
//                 var hours = totalMinutesInt / 60
//                 var hoursToDisplay = hours
//
//                 var minutesToDisplay = totalMinutesInt - (hours * 60)

                /*val hours = totalMinutes / 60
                val hoursToDisplay = hours

                val minutesToDisplay = totalMinutes - (hours * 60)

                val minToDisplay: String
                minToDisplay = when {
                    minutesToDisplay == 0 -> "00"
                    minutesToDisplay < 10 -> "0$minutesToDisplay"
                    else -> "" + minutesToDisplay
                }

                val displayValue = hoursToDisplay.toString() + ":" + minToDisplay
                return displayValue*/

                var seconds = 0L
                var minutes: Long = 0
                var hours: Long = 0
                if (totalSeconds >= 60) {
                    minutes = totalSeconds.toLong() / 60
                    seconds = totalSeconds.toLong() % 60

                    if (minutes >= 60) {
                        hours = minutes / 60
                        minutes %= 60
                    }

                } else {
                    seconds = totalSeconds.toLong()
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

                val totalDuration = "$sHours:$sMinute:$sSecond"
                XLog.e("Total duration Spent====> $totalDuration")

                return totalDuration
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return totalMinutes.toString()
        }

        fun getTotalOrderAmountForToday(): String {
            try {
                val list = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingDate(AppUtils.getCurrentDate())
                return if (list != null && list.isNotEmpty()) {
                    var amount = 0.0
                    for (i in list.indices) {
                        if (!TextUtils.isEmpty(list?.get(i)?.amount))
                            amount += list?.get(i)?.amount?.toDouble()!!
                    }

                    val finalAmount = String.format("%.2f", amount.toFloat())
                    finalAmount
                } else
                    "0.00"
            } catch (e: Exception) {
                e.printStackTrace()
                return "0.00"
            }
        }

        fun getTotalQuotAmountForToday(): String {
            try {
                val list = AppDatabase.getDBInstance()?.quotDao()?.getQuotDateWise(AppUtils.getCurrentDateForShopActi())
                return if (list != null && list.isNotEmpty()) {
                    var amount = 0.0
                    for (i in list.indices) {
                        if (!TextUtils.isEmpty(list[i].net_amount))
                            amount += list[i].net_amount?.toDouble()!!
                    }

                    val finalAmount = String.format("%.2f", amount.toFloat())
                    finalAmount
                } else
                    "0.00"
            } catch (e: Exception) {
                e.printStackTrace()
                return "0.00"
            }
        }

        fun getActivityForToday(): String {
            return try {
                val list = AppDatabase.getDBInstance()?.activDao()?.getDueDateWise(AppUtils.getCurrentDateForShopActi())
                if (list != null)
                    list.size.toString()
                else
                    "0"

            } catch (e: Exception) {
                e.printStackTrace()
                "0"
            }
        }

        fun getAvgCountOfShopInMIS(totalShopCount: String, totalAttendance: String): String {
            if (totalAttendance == "1")
                return totalShopCount

            var shopsVisitedPerDay: Float = totalShopCount.toFloat() / totalAttendance.toFloat()
            return Math.round(shopsVisitedPerDay).toString()
        }

        fun getAvgTimeOfShopInMIS(totaltimeCount: String, totalAttendance: String): String {
            var totalTimeInMin = ""
            if (totalAttendance == "1") {
                totalTimeInMin = totaltimeCount
            } else {
                val timeSpentPerDay: Float = totaltimeCount.toFloat() / totalAttendance.toFloat()
                totalTimeInMin = Math.round(timeSpentPerDay).toString()
            }
            var hours = 0
            var min = 0
            var minToDisplay = "00"
            if (totalTimeInMin.toInt() > 60)
                hours = (totalTimeInMin.toInt() / 60)
            min = (totalTimeInMin.toInt() % 60)
            if (min < 10)
                minToDisplay = "0" + min
            else
                minToDisplay = min.toString()
            return hours.toString() + ":" + minToDisplay
        }

        fun getTotalShopVisitTimeInHH_MM(totalMin: String): String {
            val totalMinutesInt = totalMin.toInt()
            var hours = totalMinutesInt / 60
            var hoursToDisplay = hours

            var minutesToDisplay = totalMinutesInt - (hours * 60)

            var minToDisplay: String
            if (minutesToDisplay == 0)
                minToDisplay = "00"
            else if (minutesToDisplay < 10) minToDisplay = "0" + minutesToDisplay
            else minToDisplay = "" + minutesToDisplay

            return hoursToDisplay.toString() + ":" + minToDisplay

        }
    }
}