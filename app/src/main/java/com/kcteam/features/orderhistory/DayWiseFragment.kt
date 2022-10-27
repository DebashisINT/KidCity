package com.kcteam.features.orderhistory

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.AppUtils.Companion.getFormattedDate
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.UserLocationDataEntity
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.orderhistory.activitiesapi.LocationFetchRepositoryProvider
import com.kcteam.features.orderhistory.api.LocationUpdateRepositoryProviders
import com.kcteam.features.orderhistory.model.*
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import com.itextpdf.text.*
import com.itextpdf.text.BaseColor
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.util.*


/**
 * Created by Pratishruti on 01-11-2017.
 */
class DayWiseFragment : BaseFragment(), View.OnClickListener {

    private lateinit var pickDate: AppCustomTextView
    private lateinit var dayWiseHistory: RecyclerView
    private lateinit var dayWiseAdapter: DayWiseAdapter
    private lateinit var mContext: Context
    private lateinit var layoutManager: RecyclerView.LayoutManager
    var myCalendar = Calendar.getInstance(Locale.ENGLISH)
    var list: MutableList<UserLocationDataEntity> = ArrayList()
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    lateinit var actionFeed: ActionFeed
    private lateinit var tv_total_distance: AppCustomTextView
    private lateinit var tv_share_logs: AppCustomTextView
    private lateinit var tv_sync_all: AppCustomTextView
    private lateinit var ll_visit_distance: LinearLayout
    private lateinit var tv_visit_distance: AppCustomTextView
    private lateinit var tv_share_pdf: AppCustomTextView

    val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        updateLabel()
        updateLocationList()
        writeDataToFile()
    }


//    companion object {
//        private val ARG_CAUGHT = "myFragment_caught"
//        fun newInstance(caught: ActionFeed): DayWiseFragment {
//            val args: Bundle = Bundle()
//            args.putSerializable(ARG_CAUGHT, caught)
//            val fragment = DayWiseFragment()
//            fragment.arguments = args
//            return fragment
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        actionFeed = arguments.getSerializable(ARG_CAUGHT) as ActionFeed
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_history_daywise, container, false)
        initView(view)
        updateLabel()
        initList()
        writeDataToFile()
        return view
    }


    private fun initList() {
        if (AppUtils.getCurrentDate() == AppUtils.getFormattedDate(myCalendar)) {
            updateLocationList()
        } else {
            val list_ = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi()).toMutableList()
            if (list_.isEmpty()) {
                updateLocationList()
            } else if (list_.size > 0) {
                list.clear()

                createActivityList(list_)

                initAdapter()
            }
        }
    }


    private fun updateLocationList() {
        if (AppUtils.getCurrentDate() == AppUtils.getFormattedDate(myCalendar)) {
            list.clear()
            list = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getFormattedDateString(myCalendar)).toMutableList()
            if (list.isEmpty() || list.size == 1) {
                val fetchLocReq = FetchLocationRequest()
                fetchLocReq.user_id = Pref.user_id
                fetchLocReq.session_token = Pref.session_token
                fetchLocReq.date_span = ""
                fetchLocReq.from_date = AppUtils.getFormattedDate(myCalendar)
                fetchLocReq.to_date = AppUtils.getFormattedDate(myCalendar)
                callFetchLocationApi(fetchLocReq)
            } else {
                fetchSortedList()
                initAdapter()
            }

        } else {
            list.clear()
            list = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getFormattedDateString(myCalendar)).toMutableList()
            if (list.isEmpty()) {
                val fetchLocReq = FetchLocationRequest()
                fetchLocReq.user_id = Pref.user_id
                fetchLocReq.session_token = Pref.session_token
                fetchLocReq.date_span = ""
                fetchLocReq.from_date = AppUtils.getFormattedDate(myCalendar)
                fetchLocReq.to_date = AppUtils.getFormattedDate(myCalendar)
                callFetchLocationApi(fetchLocReq)
            } else {
                updateList()
            }
        }


    }


    private fun fetchSortedList() {
        list.clear()
        val list_ = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADayNotSyn(AppUtils.getFormattedDateString(myCalendar), true).toMutableList()
        val tempList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADayNotSyn(AppUtils.getFormattedDateString(myCalendar), false).toMutableList()
        list_.addAll(tempList)

        createActivityList(list_)

    }

    private fun createActivityList(list_: MutableList<UserLocationDataEntity>) {
        var distanceCovered: Double = 0.0

        var fiveMinsRowGap = 5

        if (Pref.locationTrackInterval == "30")
            fiveMinsRowGap = 10

        for (i in 0 until list_.size) {
            if (list_[i].latitude == null || list_[i].longitude == null)
                continue

            //list.add(list_[i])

            if (i == 0) {
                list.add(list_[i])
            }

            distanceCovered += list_[i].distance.toDouble()

            if (i != 0 /*&& i % 5 == 0*/) {
                try {

                    val timeStamp = list_[i].timestamp.toLong()

                    if (i % fiveMinsRowGap == 0) {
                        list_[i].distance = distanceCovered.toString()
                        list.add(list_[i])
                        distanceCovered = 0.0
                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                    list_[i].distance = distanceCovered.toString()
                    list.add(list_[i])
                    distanceCovered = 0.0
                }
            }
        }
    }

    fun updateList() {

        try {
            val list_ = ArrayList<UserLocationDataEntity>()
            list_.addAll(list)
            list.clear()

            createActivityList(list_)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initAdapter()
    }

    private fun callFetchLocationApi(fetchLocReq: FetchLocationRequest) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = LocationFetchRepositoryProvider.provideLocationFetchRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.fetchLocationUpdate(fetchLocReq)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val shopList = result as FetchLocationResponse
                            if (shopList.status == "200") {
                                convertToModelAndSave(shopList.location_details, shopList.visit_distance)
                                progress_wheel.stopSpinning()
//                                (mContext as DashboardActivity).showSnackMessage("SUCCESS")
                            } else if (shopList.status == NetworkConstant.SESSION_MISMATCH) {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).clearData()
                                startActivity(Intent(mContext as DashboardActivity, LoginActivity::class.java))
                                (mContext as DashboardActivity).overridePendingTransition(0, 0)
                                (mContext as DashboardActivity).finish()
                            } else if (shopList.status == NetworkConstant.NO_DATA) {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(shopList.message!!)
                                updateList()
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(shopList.message!!)
                                updateList()
                            }
//
                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            updateList()
//                            (mContext as DashboardActivity).showSnackMessage("ERROR")

                        })
        )
    }

    private fun convertToModelAndSave(location_details: List<LocationData>?, visitDistance: String) {
        if (location_details!!.isEmpty())
            return

        list.clear()


        for (i in 0 until location_details.size) {
            val localData = UserLocationDataEntity()
            if (location_details[i].latitude == null)
                continue
            else
                localData.latitude = location_details[i].latitude!!

            if (location_details[i].longitude == null)
                continue
            else
                localData.longitude = location_details[i].longitude!!

            if (location_details[i].date == null)
                continue
            else {
                localData.updateDate = AppUtils.changeAttendanceDateFormatToCurrent(location_details[i].date!!)
                localData.updateDateTime = location_details[i].date!!
            }
            if (location_details[i].last_update_time == null)
                continue
            else {
                val str = location_details[i].last_update_time
                localData.time = str.split(" ")[0]
                localData.meridiem = str.split(" ")[1]
            }
            localData.isUploaded = true
            localData.minutes = "0"
            localData.hour = "0"
            if (location_details[i].distance_covered == null)
                continue
            else
                localData.distance = location_details[i].distance_covered!!

            if (location_details[i].shops_covered == null)
                continue
            else
                localData.shops = location_details[i].shops_covered!!
            if (location_details[i].location_name == null)
                continue
            else
                localData.locationName = location_details[i].location_name!!

            if (location_details[i].date == null)
                continue
            else
                localData.timestamp = AppUtils.getTimeStampFromDate(location_details[i].date!!)

            if (location_details[i].meeting_attended == null)
                continue
            else
                localData.meeting = location_details[i].meeting_attended!!

            if (visitDistance == null)
                continue
            else
                localData.visit_distance = visitDistance

            if (location_details[i].network_status == null)
                continue
            else
                localData.network_status = location_details[i].network_status

            if (location_details[i].battery_percentage == null)
                continue
            else
                localData.battery_percentage = location_details[i].battery_percentage

            XLog.d("=====Current location (Activity)=======")
            XLog.d("distance=====> " + localData.distance)
            XLog.d("lat====> " + localData.latitude)
            XLog.d("long=====> " + localData.longitude)
            XLog.d("location=====> " + localData.locationName)
            XLog.d("date time=====> " + localData.updateDateTime)
            XLog.d("meeting_attended=====> " + localData.meeting)
            XLog.d("visit_distance=====> " + localData.visit_distance)
            XLog.d("network_status=====> " + localData.network_status)
            XLog.d("battery_percentage=====> " + localData.battery_percentage)

            AppDatabase.getDBInstance()!!.userLocationDataDao().insert(localData)

            XLog.d("=======location added to db (Activity)======")
            list.add(localData)
        }

        fetchSortedList()
        initAdapter()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onResume() {
        super.onResume()
        //updateLocationList()
    }

    private fun initView(view: View) {
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        pickDate = view.findViewById(R.id.pick_a_date_TV)
        dayWiseHistory = view.findViewById(R.id.history_daywise_RCV)
        tv_total_distance = view.findViewById(R.id.tv_total_distance)
        tv_share_logs = view.findViewById(R.id.tv_share_logs)
        tv_sync_all = view.findViewById(R.id.tv_sync_all)
        ll_visit_distance = view.findViewById(R.id.ll_visit_distance)
        tv_visit_distance = view.findViewById(R.id.tv_visit_distance)
        tv_share_pdf = view.findViewById(R.id.tv_share_pdf)

        if (Pref.isAttendanceDistanceShow)
            ll_visit_distance.visibility = View.VISIBLE
        else
            ll_visit_distance.visibility = View.GONE

        pickDate.setOnClickListener(this)
        tv_share_logs.setOnClickListener(this)
        tv_sync_all.setOnClickListener(this)
        tv_share_pdf.setOnClickListener(this)
    }

    @SuppressLint("WrongConstant")
    private fun initAdapter() {

        /* Collections.sort(list, object : Comparator<UserLocationDataEntity> {
             override fun compare(item: UserLocationDataEntity, t1: UserLocationDataEntity): Int {
                 val s1 = item.locationId
                 val s2 = t1.locationId
                 return s1.compareTo(s2, ignoreCase = true)
             }
         })*/


        var totalDistance = 0.0
        for (i in list.indices) {
            totalDistance += list[i].distance.toDouble()
        }


        for (i in list.indices) {
            if (!TextUtils.isEmpty(list[i].visit_distance)) {
                tv_visit_distance.text = list[i].visit_distance + " Km(s)"
                break
            }
        }

        val finalDistance = String.format("%.2f", totalDistance)
        tv_total_distance.text = "$finalDistance Km(s)"

        (mContext as DashboardActivity).activityLocationList = list

        dayWiseAdapter = DayWiseAdapter(mContext, list)
        layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        dayWiseHistory.layoutManager = layoutManager
        dayWiseHistory.adapter = dayWiseAdapter
        dayWiseHistory.isNestedScrollingEnabled = false

    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.pick_a_date_TV -> {
                val datePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                datePicker.datePicker.maxDate = Calendar.getInstance(Locale.ENGLISH).timeInMillis
                datePicker.show()
            }

            R.id.tv_share_logs -> {
                openShareIntents()
            }

            R.id.tv_sync_all -> {

                if (AppUtils.isOnline(mContext))
                    syncLocationActivity()
                else
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            }

//            R.id.tv_share_pdf -> {
//                if (list.isEmpty()) {
//                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
//                    return
//                }
//
//                val heading = "TIMELINE DETAILS"
//                var pdfBody = "\n\n\nDate: " + pickDate.text.toString().trim() + "\n\n\n\n" + getString(R.string.visit_distance) +
//                        " " + tv_visit_distance.text.toString().trim() + "\n\n" + getString(R.string.total_distance_travelled) + " " +
//                        tv_total_distance.text.toString().trim() + "\n\n\n\n"
//
//                list.forEach {
//                    pdfBody += it.time + it.meridiem + ":        " + it.locationName +
//                            "\n                           " + it.distance + " " + getString(R.string.distance_covered) +
//                            "\n                           " + it.shops + " " + getString(R.string.no_of_shop_visited) +
//                            "\n                           " + it.meeting + " " + getString(R.string.no_of_meeting_visited) +
//                            "\n\n\n"
//                }
//
//
//                val image = BitmapFactory.decodeResource(mContext.resources, R.mipmap.ic_launcher)
//
//                val path = FTStorageUtils.stringToPdf(pdfBody, mContext, "FTS_Timeline_" +
//                        AppUtils.getFormattedDateForApi(myCalendar.time) + "_" + Pref.user_id + ".pdf", image, heading, 2.7f)
//                if (!TextUtils.isEmpty(path)) {
//                    try {
//                        val shareIntent = Intent(Intent.ACTION_SEND)
//                        val fileUrl = Uri.parse(path)
//
//                        val file = File(fileUrl.path)
//                        //val uri = Uri.fromFile(file)
//                        //27-09-2021
//                        val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
//                        shareIntent.type = "image/png"
//                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//                        startActivity(Intent.createChooser(shareIntent, "Share pdf using"))
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                }
//                else
//                    (mContext as DashboardActivity).showSnackMessage("Pdf can not be sent.")
//            }

            R.id.tv_share_pdf -> {
                saveDataAsPdf(list)
            }
        }
    }

    private fun saveDataAsPdf(list: List<UserLocationDataEntity>) {
        var document: Document = Document()
        var fileName = "FTS_Timeline"+ "_" + AppUtils.getFormattedDateForApi(myCalendar.time) + "_" + Pref.user_id
        fileName = fileName.replace("/", "_")

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() +"/kcteamApp/TIMELINE/"

        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        try {
            PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))
            document.open()


            var font: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD)
            var fontBoldU: Font = Font(Font.FontFamily.HELVETICA, 12f,Font.UNDERLINE or Font.BOLD)
            var font1: Font = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL)
            val grayFront = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, BaseColor.GRAY)

            //image add
            val bm: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            val bitmap = Bitmap.createScaledBitmap(bm, 50, 50, true);
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            var img: Image? = null
            val byteArray: ByteArray = stream.toByteArray()
            try {
                img = Image.getInstance(byteArray)
                img.scaleToFit(90f, 90f)
                img.scalePercent(70f)
                img.alignment = Image.ALIGN_LEFT
            } catch (e: BadElementException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            document.add(img)

            val xx = Paragraph("", font)
            xx.spacingAfter = 2f
            document.add(xx)


//            val content = SpannableString("TIMELINE DETAILS ")
            val Heading = Paragraph("TIMELINE DETAILS " , fontBoldU)
            Heading.alignment = Element.ALIGN_CENTER
            Heading.spacingAfter = 2f
            document.add(Heading)


//            val HeadingLine = Chunk("TIMELINE DETAILS " , fontB)
//            HeadingLine.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
//            document.add(HeadingLine)

            val x = Paragraph("", font)
            x.spacingAfter = 2f
            document.add(x)

            val date = Paragraph("Date: " + pickDate.text.toString().trim(), font)
            date.alignment = Element.ALIGN_CENTER
            date.spacingAfter = 2f
            document.add(date)

//            val visit_distance = Paragraph(getString(R.string.visit_distance) + tv_visit_distance.text.toString().trim(), font)
//            visit_distance.alignment = Element.ALIGN_LEFT
//            visit_distance.spacingAfter = 2f
//            document.add(visit_distance)

            val total_distance = Paragraph(getString(R.string.total_distance_travelled) + tv_total_distance.text.toString().trim(), font)
            total_distance.alignment = Element.ALIGN_CENTER
            total_distance.spacingAfter = 15f
            document.add(total_distance)

            // table header
            val widths = floatArrayOf(0.05f, 0.50f)

            var tableHeader: PdfPTable = PdfPTable(widths)
            tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
            tableHeader.setWidthPercentage(100f)



            val cell1 = PdfPCell(Phrase("Time", font))
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER)
            cell1.borderColor = BaseColor.GRAY
            tableHeader.addCell(cell1);

            val cell2 = PdfPCell(Phrase("Address", font))
            cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell2.borderColor = BaseColor.GRAY
            tableHeader.addCell(cell2);

            document.add(tableHeader)

            //table body
            var time: String = ""
            var addr: String = ""

            var obj=list!!
            for (i in 0..obj.size-1) {
                time = obj!!.get(i).time + obj!!.get(i).meridiem + "       "
                addr = obj!!.get(i).locationName + "\n" + obj!!.get(i).distance + " " + getString(R.string.distance_covered) +
                        "\n" + obj!!.get(i).shops + " " + getString(R.string.no_of_shop_visited) +
                        "\n" + obj!!.get(i).meeting + " " + getString(R.string.no_of_meeting_visited)


                val tableRows = PdfPTable(widths)
                tableRows.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                tableRows.setWidthPercentage(100f);

                var cellBodySl = PdfPCell(Phrase(time, font1))
                cellBodySl.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellBodySl.borderColor = BaseColor.GRAY
                tableRows.addCell(cellBodySl)

                var cellBody2 = PdfPCell(Phrase(addr, font1))
                cellBody2.setHorizontalAlignment(Element.ALIGN_LEFT)
                cellBody2.borderColor = BaseColor.GRAY
                tableRows.addCell(cellBody2)



                document.add(tableRows)

                document.add(Paragraph())
            }


            document.close()

            var sendingPath = path + fileName + ".pdf"
            if (!TextUtils.isEmpty(sendingPath)) {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    val fileUrl = Uri.parse(sendingPath)
                    val file = File(fileUrl.path)
                    val uri: Uri = FileProvider.getUriForFile(mContext, mContext.applicationContext.packageName.toString() + ".provider", file)
                    shareIntent.type = "image/png"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(shareIntent, "Share pdf using"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong1))
                }
            }
        }
        catch (ex: Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }
    }

    private fun syncLocationActivity() {

        XLog.d("syncLocationActivity (Activity Screen) : ENTER")

        if (Pref.user_id.isNullOrEmpty())
            return

        val syncList = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADayNotSyn(AppUtils.getCurrentDateForShopActi(), true)

        val list = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationNotUploaded(false)
        if (list.isEmpty() || AppUtils.isLocationActivityUpdating)
            return

        AppUtils.isLocationActivityUpdating = true

        val locationUpdateReq = LocationUpdateRequest()
        locationUpdateReq.user_id = Pref.user_id
        locationUpdateReq.session_token = Pref.session_token

        val locationList: MutableList<LocationData> = ArrayList()
        val locationListAllId: MutableList<LocationData> = ArrayList()
        var distanceCovered: Double = 0.0
        var timeStamp = 0L

        val allLocationListForToday = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi()).toMutableList()
        val apiLocationList: MutableList<UserLocationDataEntity> = ArrayList()

        var fiveMinsRowGap = 5

        if (Pref.locationTrackInterval == "30")
            fiveMinsRowGap = 10

        for (i in 0 until allLocationListForToday.size) {
            if (allLocationListForToday[i].latitude == null || allLocationListForToday[i].longitude == null)
                continue

            //apiLocationList.add(allLocationListForToday[i])

            if (i == 0) {
                apiLocationList.add(allLocationListForToday[i])
                //selectedTimeStamp = allLocationList[i].timestamp.toLong()
            }

            distanceCovered += allLocationListForToday[i].distance.toDouble()

            if (i != 0 /*&& i % 5 == 0*/) {
                try {

                    val timeStamp_ = allLocationListForToday[i].timestamp.toLong()

                    if (i % fiveMinsRowGap == 0) {
                        allLocationListForToday[i].distance = distanceCovered.toString()

                        if (timeStamp != 0L) {
                            val hh = timeStamp / 3600
                            timeStamp %= 3600
                            val mm = timeStamp / 60
                            timeStamp %= 60
                            val ss = timeStamp
                            allLocationListForToday[i].home_duration = AppUtils.format(hh) + ":" + AppUtils.format(mm) + ":" + AppUtils.format(ss)
                        }
                        apiLocationList.add(allLocationListForToday[i])
                        distanceCovered = 0.0
                        //selectedTimeStamp = allLocationList[i].timestamp.toLong()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                    allLocationListForToday[i].distance = distanceCovered.toString()
                    if (timeStamp != 0L) {
                        val hh = timeStamp / 3600
                        timeStamp %= 3600
                        val mm = timeStamp / 60
                        timeStamp %= 60
                        val ss = timeStamp
                        allLocationListForToday[i].home_duration = AppUtils.format(hh) + ":" + AppUtils.format(mm) + ":" + AppUtils.format(ss)
                    }
                    apiLocationList.add(allLocationListForToday[i])
                    distanceCovered = 0.0
                }
            }

        }

        for (i in apiLocationList.indices) {
            if (!apiLocationList[i].isUploaded) {

                XLog.e("Final Home Duration (Location Fuzed Service)=================> ${apiLocationList[i].home_duration}")
                XLog.e("Time (Location Fuzed Service)=================> ${apiLocationList[i].time} ${apiLocationList[i].meridiem}")


                val locationData = LocationData()

                locationData.locationId = apiLocationList[i].locationId.toString()
                locationData.date = apiLocationList[i].updateDateTime
                locationData.distance_covered = apiLocationList[i].distance
                locationData.latitude = apiLocationList[i].latitude
                locationData.longitude = apiLocationList[i].longitude
                locationData.location_name = apiLocationList[i].locationName
                locationData.shops_covered = apiLocationList[i].shops
                locationData.last_update_time = apiLocationList[i].time + " " + apiLocationList[i].meridiem
                locationData.meeting_attended = apiLocationList[i].meeting
                locationData.home_distance = apiLocationList[i].home_distance
                locationData.network_status = apiLocationList[i].network_status
                locationData.battery_percentage = apiLocationList[i].battery_percentage
                locationData.home_duration = apiLocationList[i].home_duration
                locationList.add(locationData)

                val locationDataAll = LocationData()
                locationDataAll.locationId = apiLocationList[i].locationId.toString()
                locationListAllId.add(locationDataAll)
            }
        }

        if (locationList.size > 0) {
            locationUpdateReq.location_details = locationList
            val repository = LocationUpdateRepositoryProviders.provideLocationUpdareRepository()

            XLog.d("syncLocationActivity (Activity Screen) : REQUEST")

            progress_wheel.spin()

            BaseActivity.compositeDisposable.add(
                    repository.sendLocationUpdate(locationUpdateReq)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val updateShopActivityResponse = result as BaseResponse

                                XLog.d("syncLocationActivity (Activity Screen) : RESPONSE : " + updateShopActivityResponse.status + ":" + updateShopActivityResponse.message)

                                if (updateShopActivityResponse.status == NetworkConstant.SUCCESS) {
                                    doAsync {

                                        for (i in 0 until locationListAllId/*locationList*/.size) {

                                            //AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploaded(true, locationList[i].locationId.toInt())

                                            if (syncList != null && syncList.isNotEmpty()) {

                                                if (i == 0)
                                                    AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploadedFor5Items(true, syncList[syncList.size - 1].locationId.toInt(), locationListAllId[i].locationId.toInt())
                                                else
                                                    AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploadedFor5Items(true, locationListAllId[i - 1].locationId.toInt(), locationListAllId[i].locationId.toInt())

                                            } else {
                                                if (i == 0)
                                                    AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploaded(true, locationListAllId[i].locationId.toInt())
                                                else
                                                    AppDatabase.getDBInstance()!!.userLocationDataDao().updateIsUploadedFor5Items(true, locationListAllId[i - 1].locationId.toInt(), locationListAllId[i].locationId.toInt())
                                            }
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            AppUtils.isLocationActivityUpdating = false

                                            fetchSortedList()
                                            initAdapter()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    AppUtils.isLocationActivityUpdating = false
                                }

                            }, { error ->
                                AppUtils.isLocationActivityUpdating = false
                                progress_wheel.stopSpinning()
                                if (error == null) {
                                    XLog.d("syncLocationActivity (Activity Screen) : ERROR : " + "UNEXPECTED ERROR IN LOCATION ACTIVITY API")
                                } else {
                                    XLog.d("syncLocationActivity (Activity Screen) : ERROR : " + error.localizedMessage)
                                    error.printStackTrace()
                                }

                            })
            )
        } else
            AppUtils.isLocationActivityUpdating = false
    }


    @SuppressLint("UseRequireInsteadOfGet")
    private fun openShareIntents() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
//        val phototUri = Uri.parse(localAbsoluteFilePath)
            val folderPath = FTStorageUtils.getFolderPath(mContext)
            val fileUrl = Uri.parse(File("$folderPath/FTS_Accurate_Location.txt").path)

            val file = File(fileUrl.path)
            if (!file.exists()) {
                return
            }

//            val uri = Uri.fromFile(file)
            val uri : Uri=FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
//        shareIntent.data = fileUrl
            shareIntent.type = "image/png"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(shareIntent, "Share log using"));
        } catch (e: Exception) {
            e.printStackTrace()
        }


//        Uri uri = Uri.fromFile(file);
//        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        startActivity(Intent.createChooser(emailIntent,""))
    }

    fun updateLabel() {
        pickDate.text = getFormattedDate(myCalendar.time)
    }

    private fun writeDataToFile() {

        val list = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getFormattedDateString(myCalendar))
        //val list = AppDatabase.getDBInstance()!!.userLocationDataDao().getLocationUpdateForADay(AppUtils.getCurrentDateForShopActi())

        val company = JSONArray()

        for (i in 0 until list.size) {
            if (list[i].latitude == null || list[i].longitude == null)
                continue
            val jsonObject = JSONObject()
            jsonObject.put("date time", list[i].updateDateTime)
            jsonObject.put("distance_covered", list[i].distance)
            jsonObject.put("latitude", list[i].latitude)
            jsonObject.put("longitude", list[i].longitude)
            //jsonObject.put("accuracy", list[i].accuracy)
            jsonObject.put("location_name", list[i].locationName)
            company.put(jsonObject)
        }

        val accurateObject = JSONObject()
        accurateObject.put("accurate_location_details", company)
        accurateObject.put("total_distance", tv_total_distance.text.toString().trim())

        val inaccurateData = JSONArray()

        val inaccurateList = AppDatabase.getDBInstance()!!.inaccurateLocDao().getLocationUpdateForADay(AppUtils.getFormattedDateString(myCalendar))
        if (inaccurateList != null) {
            for (i in inaccurateList.indices) {
                if (inaccurateList[i].latitude == null || inaccurateList[i].longitude == null)
                    continue
                val jsonObject = JSONObject()
                jsonObject.put("date time", inaccurateList[i].updateDateTime)
                jsonObject.put("latitude", inaccurateList[i].latitude)
                jsonObject.put("longitude", inaccurateList[i].longitude)
                jsonObject.put("accuracy", inaccurateList[i].accuracy)
                jsonObject.put("location_name", inaccurateList[i].locationName)
                inaccurateData.put(jsonObject)
            }
        }

        val configValues = JSONObject()
        configValues.put("min_accuracy", AppUtils.minAccuracy)
        configValues.put("max_accuracy", AppUtils.maxAccuracy)
        configValues.put("min_distance", AppUtils.minDistance)
        configValues.put("max_distance", AppUtils.maxDistance)
        configValues.put("idle_time", AppUtils.idle_time)


        val parentObject = JSONObject()
        parentObject.put("accurate_loc", accurateObject)
        parentObject.put("inaccurate_loc", inaccurateData)
        parentObject.put("config_values", configValues)

        try {
            var output: Writer? = null
            val folderPath = FTStorageUtils.getFolderPath(mContext)
            val file = File("$folderPath/FTS_Accurate_Location.txt")
            if (file.exists()) {
                Log.e("Location List", "File deleted")
                file.delete()
            }
            output = BufferedWriter(FileWriter(file))
            output.write(parentObject.toString())
            output.close()
            Log.e("Location list", "Value saved")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}





