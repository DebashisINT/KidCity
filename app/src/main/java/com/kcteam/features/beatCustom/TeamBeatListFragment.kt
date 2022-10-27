package com.kcteam.features.beatCustom

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.BeatEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.app.widgets.MovableFloatingActionButton
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addshop.api.typeList.TypeListRepoProvider
import com.kcteam.features.addshop.model.BeatListResponseModel
import com.kcteam.features.addshop.presentation.BeatListDialog
import com.kcteam.features.alarm.api.visit_report_api.VisitReportRepoProvider
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.login.presentation.LoginActivity
import com.kcteam.features.member.model.TeamListDataModel
import com.kcteam.widgets.AppCustomTextView
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class TeamBeatListFragment : BaseFragment(), DatePickerDialog.OnDateSetListener,
    View.OnClickListener {
    private lateinit var mContext: Context
    private lateinit var rv_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var tv_select_beat_team: AppCustomTextView
    private lateinit var iv_show: ImageView
    private lateinit var date_range: AppCompatRadioButton
    private lateinit var radioList: ArrayList<RadioButton>
    private var isChkChanged: Boolean = false
    private val mAutoHighlight: Boolean = false
    private var fromDate: String = ""
    private var toDate: String = ""
    private var selectedBeat: String = ""
    private var selectedBeatName: String = ""
    private lateinit var date_rangeDisplay: AppCustomTextView

    private lateinit var beatTeamListAdapter :BeatTeamListAdapter
    private lateinit var ftn_share: MovableFloatingActionButton
    private lateinit var finalList: ArrayList<BeatViewModel>
    private lateinit var beatViewModel:BeatViewModel
    private var shareBeatModel :ArrayList<BeatViewModel> = ArrayList()

    companion object {
        var mobj: TeamListDataModel? = null
        fun getInstance(objects: Any): TeamBeatListFragment {
            val TeamBeatListFragment = TeamBeatListFragment()
            if (objects != null) {
                if (objects is TeamListDataModel)
                    this.mobj = objects
            }
            return TeamBeatListFragment
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_team_beat_list, container, false)
        initView(view)
        return view
    }

    @SuppressLint("RestrictedApi")
    private fun initView(view: View) {
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        tv_no_data = view.findViewById(R.id.tv_no_data)
        rv_list = view.findViewById(R.id.rv_frag_team_beat_list)
        radioList = ArrayList()
        date_range = view.findViewById(R.id.frag_team_beat_list_date_range)
        date_rangeDisplay = view.findViewById(R.id.frag_team_beat_date_range_display)
        tv_select_beat_team = view.findViewById(R.id.tv_select_beat_team)
        iv_show = view.findViewById(R.id.frag_lead_pending_show_team)
        date_range.setOnClickListener(this)
        tv_select_beat_team.setOnClickListener(this)
        iv_show.setOnClickListener(this)
        tv_no_data.visibility = View.GONE
        ftn_share=view.findViewById(R.id.add_share)
        ftn_share.setCustomClickListener{
            saveDataAsPdf()
        }
        ftn_share.visibility = View.GONE

    }


    override fun onDateSet(
        view: DatePickerDialog?,
        year: Int,
        monthOfYear: Int,
        dayOfMonth: Int,
        yearEnd: Int,
        monthOfYearEnd: Int,
        dayOfMonthEnd: Int
    ) {
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        val date =
            "From " + dayOfMonth + AppUtils.getDayNumberSuffix(dayOfMonth) + " " + FTStorageUtils.formatMonth(
                (++monthOfYear).toString()
            ) + " " + year + " To " + dayOfMonthEnd + AppUtils.getDayNumberSuffix(dayOfMonthEnd) + " " + FTStorageUtils.formatMonth(
                (++monthOfYearEnd).toString()
            ) + " " + yearEnd
        date_rangeDisplay.text = date
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd
        if (dayOfMonth < 10)
            day = "0$dayOfMonth"
        if (dayOfMonthEnd < 10)
            dayEnd = "0$dayOfMonthEnd"
        var fronString: String =
            day + "-" + FTStorageUtils.formatMonth((monthOfYear /*+ 1*/).toString() + "") + "-" + year
        var endString: String =
            dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd /*+ 1*/).toString() + "") + "-" + yearEnd
        fromDate = AppUtils.changeLocalDateFormatToAtt(fronString).replace("/", "-")
        toDate = AppUtils.changeLocalDateFormatToAtt(endString).replace("/", "-")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.frag_team_beat_list_date_range -> {
                if (!isChkChanged) {
                    date_range.isChecked = true
                    val now = Calendar.getInstance(Locale.ENGLISH)
                    val dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.isAutoHighlight = mAutoHighlight
                    dpd.maxDate = Calendar.getInstance(Locale.ENGLISH)
                    dpd.show((context as Activity).fragmentManager, "Datepickerdialog")
                } else {
                    isChkChanged = false
                }
            }
            R.id.tv_select_beat_team -> {
                /*val list = AppDatabase.getDBInstance()?.beatDao()?.getAll() as ArrayList<BeatEntity>
                if (list != null && list.isNotEmpty())
                    showBeatListDialog(list)
                else
                    getBeatListApi()*/

                getBeatListApi()
            }
            R.id.frag_lead_pending_show_team ->{
                if(!fromDate.equals("") && !toDate.equals("")){
                    if(!selectedBeat.equals("")){
                        getVisitReport(fromDate,toDate)
                    }else{
                        Toaster.msgShort(mContext,"Please select beat")
                    }
                }else{
                    Toaster.msgShort(mContext,"Please select date range")
                }
            }
        }


    }

    private fun saveDataAsPdf() {
        var document: Document = Document()
        val time = System.currentTimeMillis()
        var fileName = "BeatShare" +  "_" + time
        fileName=fileName.replace("/", "_")
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/kcteamApp/BEAT/"

        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        try{
            var pdfWriter :PdfWriter = PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))

            PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))
            document.open()

            var font: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD)
            var fontBlueColor: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD,BaseColor.BLACK)
            var fontB1: Font = Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD)
            var font1: Font = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL)
            var font1Gray: Font = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL,BaseColor.DARK_GRAY)
            var font1Big: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)
            val grayFront = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.GRAY)
            var fontBoldU: Font = Font(Font.FontFamily.HELVETICA, 12f, Font.UNDERLINE or Font.BOLD)

            //image add
            val bm: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.breezelogo)
            val bitmap = Bitmap.createScaledBitmap(bm, 200, 70, true);
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            var img: Image? = null
            val byteArray: ByteArray = stream.toByteArray()
            try {
                img = Image.getInstance(byteArray)
                img.scaleToFit(125f,60f)
                img.scalePercent(70f)
                img.alignment=Image.ALIGN_LEFT
            } catch (e: BadElementException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            document.add(img)


            val pdfHead = Paragraph("Beat wise Visit/Revisit Status" , fontBoldU)
            pdfHead.alignment = Element.ALIGN_CENTER
            pdfHead.spacingAfter = 5f
            document.add(pdfHead)

            val salesman = Paragraph("Name : "+mobj!!.user_name , font)
            salesman.alignment = Element.ALIGN_LEFT
            salesman.spacingAfter = 5f
            document.add(salesman)

            val widths = floatArrayOf(0.20f, 0.20f,0.20f,0.20f,0.20f)

            for(l in 0..shareBeatModel.size-1){
                val dateBeatHead = Paragraph("${Pref.beatText} : ${shareBeatModel.get(l).beatName!!}" + "       Date: " + AppUtils.getFormatedDateNew(shareBeatModel.get(l).date!!,"yyyy-mm-dd","dd-mm-yyyy") , fontBlueColor)
                dateBeatHead.alignment = Element.ALIGN_LEFT
                dateBeatHead.spacingAfter = 5f
                document.add(dateBeatHead)

                /*val subHead = Paragraph("${Pref.shopText}           Status     Visit/Revisit", fontB1)
                subHead.alignment = Element.ALIGN_LEFT
                subHead.spacingAfter = 2f
                document.add(subHead)*/


                var tableHeader: PdfPTable = PdfPTable(widths)
                tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT)
                tableHeader.setWidthPercentage(100f)

                val cell1 = PdfPCell(Phrase("${Pref.shopText}",fontB1))
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell1.setBorder(Rectangle.NO_BORDER)
                tableHeader.addCell(cell1);

                val cell2 = PdfPCell(Phrase("Status",fontB1))
                cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2.setBorder(Rectangle.NO_BORDER)
                tableHeader.addCell(cell2);

                val cell2_1 = PdfPCell(Phrase("Visit/Revisit",fontB1))
                cell2_1.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2_1.setBorder(Rectangle.NO_BORDER)
                tableHeader.addCell(cell2_1);

                val cell2_2 = PdfPCell(Phrase("",fontB1))
                cell2_2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2_2.setBorder(Rectangle.NO_BORDER)
                tableHeader.addCell(cell2_2);

                val cell2_3 = PdfPCell(Phrase("",fontB1))
                cell2_3.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2_3.setBorder(Rectangle.NO_BORDER)
                tableHeader.addCell(cell2_3);

                document.add(tableHeader)

                for(k in 0..shareBeatModel.get(l).beatList.size-1){

                    val tableRows = PdfPTable(widths)
                    tableRows.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableRows.setWidthPercentage(100f);

                    var cellBodyS1 = PdfPCell(Phrase(shareBeatModel.get(l).beatList.get(k).cusName,font1Gray))
                    cellBodyS1.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellBodyS1.setBorder(Rectangle.NO_BORDER)
                    tableRows.addCell(cellBodyS1)

                    var cellBodyS2 = PdfPCell(Phrase(shareBeatModel.get(l).beatList.get(k).status,font1Gray))
                    cellBodyS2.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellBodyS2.setBorder(Rectangle.NO_BORDER)
                    tableRows.addCell(cellBodyS2)

                    var cellBodyS3 = PdfPCell(Phrase(shareBeatModel.get(l).beatList.get(k).vTime,font1Gray))
                    cellBodyS3.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellBodyS3.setBorder(Rectangle.NO_BORDER)
                    tableRows.addCell(cellBodyS3)

                    var cellBodyS4 = PdfPCell(Phrase("",font1Gray))
                    cellBodyS4.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellBodyS4.setBorder(Rectangle.NO_BORDER)
                    tableRows.addCell(cellBodyS4)

                    var cellBodyS5 = PdfPCell(Phrase("",font1Gray))
                    cellBodyS5.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellBodyS5.setBorder(Rectangle.NO_BORDER)
                    tableRows.addCell(cellBodyS5)

                    document.add(tableRows)
                    document.add(Paragraph())

                    /*var shopName = shareBeatModel.get(l).beatList.get(k).cusName
                    var status = shareBeatModel.get(l).beatList.get(k).status
                    var vt = shareBeatModel.get(l).beatList.get(k).vTime

                    val shopNames = Paragraph("$shopName          $status           $vt", font1)
                    shopNames.alignment = Element.ALIGN_LEFT
                    shopNames.spacingAfter = 2f
                    document.add(shopNames)*/
                }
            }

            val xxx = Paragraph("", font)
            xxx.spacingAfter = 5f
            document.add(xxx)

            val generatedDT = Paragraph("Generated Date Time :"+AppUtils.getCurrentDateTimeDDMMYY() , font1)
            generatedDT.alignment = Element.ALIGN_LEFT
            generatedDT.spacingAfter = 5f
            document.add(generatedDT)

            document.close()

            var sendingPath=path+fileName+".pdf"
            if (!TextUtils.isEmpty(sendingPath)) {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    val fileUrl = Uri.parse(sendingPath)
                    val file = File(fileUrl.path)
                    val uri: Uri = FileProvider.getUriForFile(mContext, requireContext().applicationContext.packageName.toString() + ".provider", file)
                    shareIntent.type = "image/png"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(shareIntent, "Share pdf using"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                }
            }

        }catch (ex: Exception){
            ex.printStackTrace()
            Toaster.msgShort(mContext, ex.message.toString())
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }

    }


    private fun showBeatListDialog(list: ArrayList<BeatEntity>) {
        var obj:BeatEntity = BeatEntity()
        obj.apply {
            beat_id = "-1"
            name = "All"
        }
        list.add(0,obj)
        BeatListDialog.newInstance(list) {
            tv_select_beat_team.text = it.name
            selectedBeat = it.beat_id!!
            selectedBeatName = it.name!!
            }.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun getBeatListApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        val repository = TypeListRepoProvider.provideTypeListRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.beatListForTeam(mobj!!.user_id!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val response = result as BeatListResponseModel
                    if (response.status == NetworkConstant.SUCCESS) {
                        val list = response.beat_list

                        if (list != null && list.isNotEmpty()) {
                            //AppDatabase.getDBInstance()?.beatDao()?.delete()
                            doAsync {
                                /*list.forEach {
                                    val beat = BeatEntity()
                                    AppDatabase.getDBInstance()?.beatDao()?.insert(beat.apply {
                                        beat_id = it.id
                                        name = it.name
                                    })
                                }*/
                                var beatList :ArrayList<BeatEntity> = ArrayList()
                                for(i in 0..list.size-1){
                                    var ob = BeatEntity()
                                    ob.beat_id = list.get(i).id
                                    ob.name = list.get(i).name
                                    beatList.add(ob)
                                }
                                uiThread {
                                    progress_wheel.stopSpinning()
                                    showBeatListDialog(
                                        beatList
                                        //AppDatabase.getDBInstance()?.beatDao()?.getAll() as ArrayList<BeatEntity>
                                    )
                                }
                            }
                        } else {
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)
                        }
                    } else if (response.status == NetworkConstant.NO_DATA) {
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                    } else {
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(response.message!!)
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))

                })
        )
    }

    private fun getVisitReport(fromDate: String, toDate: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = VisitReportRepoProvider.provideVisitReportRepository()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
            repository.getVisitReportListFromTeam(fromDate, toDate,mobj!!.user_id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    val visitList = result as BeatTeamResponseModel
                    when {
                        visitList.status == NetworkConstant.SUCCESS -> {
                            progress_wheel.stopSpinning()
                            initAdapter(visitList.visit_report_list)

                        }
                        visitList.status == NetworkConstant.SESSION_MISMATCH -> {
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(visitList.message!!)
                            startActivity(
                                Intent(
                                    mContext as DashboardActivity,
                                    LoginActivity::class.java
                                )
                            )
                            (mContext as DashboardActivity).overridePendingTransition(0, 0)
                            (mContext as DashboardActivity).finish()
                        }
                        visitList.status == NetworkConstant.NO_DATA -> {
                            progress_wheel.stopSpinning()
                            tv_no_data.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(visitList.message!!)

                        }
                        else -> {
                            progress_wheel.stopSpinning()
                            tv_no_data.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(visitList.message!!)
                        }
                    }

                }, { error ->
                    progress_wheel.stopSpinning()
                    error.printStackTrace()
                    tv_no_data.visibility = View.VISIBLE
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                })
        )
    }

    @SuppressLint("RestrictedApi")
    private fun initAdapter(visit_report_list: ArrayList<VisitreportList>?) {

        var rootObj :VisitreportList? = null
        if(visit_report_list!!.size > 0){
            for(i in 0..visit_report_list.size-1){
                if(mobj!!.user_id.equals(visit_report_list.get(i).user_id)){
                    rootObj =  visit_report_list.get(i)
                    break
                }
            }
        }

        if(rootObj!=null && rootObj.visit_details_list.size>0){

            var beatViewModelList:ArrayList<BeatViewModel> = ArrayList()

            do{
                var beatViewModel : BeatViewModel = BeatViewModel()

                var beatName = rootObj.visit_details_list.get(0).beat_name
                var visitDate = rootObj.visit_details_list.get(0).date

                var nameDateWiseList = rootObj.visit_details_list.filter { it.beat_name.equals(beatName) && it.date.equals(visitDate) && !it.beat_name.equals("")}
                beatViewModel.beatName = beatName!!
                beatViewModel.date=visitDate
                for(j in 0..nameDateWiseList.size-1)
                    beatViewModel.beatList.add(BeatViewListModel(nameDateWiseList.get(j).shop_name,nameDateWiseList.get(j).visit_status,nameDateWiseList.get(j).visit_time))


                beatViewModelList.add(beatViewModel)

                var recifiedL:ArrayList<VisitdetailsList> = ArrayList()
                do{
                    var bn = rootObj.visit_details_list.get(0).beat_name
                    var dt = rootObj.visit_details_list.get(0).date
                    if(bn.equals(beatName) && dt.equals(visitDate) ){
                        rootObj.visit_details_list.removeAt(0)
                    }else if(!bn.equals("")){
                        recifiedL.add(rootObj.visit_details_list.get(0))
                        rootObj.visit_details_list.removeAt(0)
                    }else if(bn.equals("")){
                        rootObj.visit_details_list.removeAt(0)
                    }
                }while (rootObj.visit_details_list.size>0)
                //var recifiedL = rootObj.visit_details_list.filter { !it.beat_name.equals(beatName) && !it.date.equals(visitDate) && !it.beat_name.equals("") } as ArrayList<VisitdetailsList>
                rootObj.visit_details_list=recifiedL


            }while(rootObj.visit_details_list.size>0)


            var finalList = if(selectedBeatName.equals("All")) {
                beatViewModelList as ArrayList<BeatViewModel>

            } else{
                beatViewModelList.filter { it.beatName.equals(selectedBeatName) } as ArrayList<BeatViewModel>
            }

            if(finalList.size==0 && finalList.equals(null))
                tv_no_data.visibility = View.VISIBLE
            else
                tv_no_data.visibility = View.GONE

            if(finalList.size>0){
                finalList = finalList.reversed() as ArrayList<BeatViewModel>

                for(i in 0..finalList.size-1){
                    for(j in 0..finalList.get(i).beatList.size-1){
                        finalList.get(i).beatList = finalList.get(i).beatList.reversed() as ArrayList<BeatViewListModel>
                    }
                }

                shareBeatModel=finalList
                beatTeamListAdapter = BeatTeamListAdapter(mContext,finalList)
                rv_list.adapter=beatTeamListAdapter
                ftn_share.visibility = View.VISIBLE
            }else{
                shareBeatModel = ArrayList()
                ftn_share.visibility = View.GONE
            }
        }

    }

}