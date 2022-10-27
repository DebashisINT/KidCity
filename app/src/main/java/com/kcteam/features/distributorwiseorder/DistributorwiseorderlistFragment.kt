package com.kcteam.features.distributorwiseorder

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.NumberToWords
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.Pref
import com.kcteam.app.domain.*
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomTextView
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.pnikosis.materialishprogress.ProgressWheel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class DistributorwiseorderlistFragment : BaseFragment(), View.OnClickListener {
    private lateinit var mContext: Context
    private lateinit var assign_to_tv: AppCustomTextView
    private lateinit var tv_assign_to_dd: AppCustomTextView
    private lateinit var fromDate: AppCompatRadioButton
    private lateinit var toDate: AppCompatRadioButton
    private lateinit var genereatePdfTv: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var rv_vw_dist_order_list:RecyclerView
    private lateinit var ll_view_of_dist_list:LinearLayout
    private var assignedToPPId = ""
    private var assignedToDDId = ""
    private var fromDateSel: String = ""
    private var toDateSel: String = ""
    private var bill: BillingEntity? = null
    private lateinit var rl_assign_to_dd: LinearLayout
    private lateinit var assign_to_rl_pp: LinearLayout
    private lateinit var tv_head_dd: TextView
    private lateinit var tv_head_pp: TextView
    var viewDistOrderProductAdapter: ViewDistOrderProductAdapter?=null
    var viewList: ArrayList<DistWiseOrderTblEntity> = ArrayList()
    val FromCalender = Calendar.getInstance(Locale.ENGLISH)
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
        val view = inflater.inflate(R.layout.frag_distributor_wise_order_list, container, false)
        initView(view)
        return view
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initView(view: View) {
        assign_to_tv = view.findViewById(R.id.assign_to_tv)
        tv_assign_to_dd = view.findViewById(R.id.tv_assign_to_dd)
        fromDate = view.findViewById(R.id.frag_distribute_wise_order_list_from_date_range)
        toDate = view.findViewById(R.id.frag_distribute_wise_order_list_to_date_range)
        genereatePdfTv = view.findViewById(R.id.frag_distributor_wise_order_generated_pdf_TV)
        rl_assign_to_dd = view.findViewById(R.id.rl_assign_to_dd)
        tv_head_dd = view.findViewById(R.id.tv_head_dd)
        tv_head_pp = view.findViewById(R.id.tv_head_pp)
        assign_to_rl_pp = view.findViewById(R.id.assign_to_rl)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        rv_vw_dist_order_list= view.findViewById(R.id.rv_vw_dist_order_list)
        ll_view_of_dist_list=view.findViewById(R.id.vv_ll)
        assign_to_rl_pp.setOnClickListener(this)
        rl_assign_to_dd.setOnClickListener(this)
        fromDate.setOnClickListener(this)
        toDate.setOnClickListener(this)
        genereatePdfTv.setOnClickListener(this)
        progress_wheel.stopSpinning()

        tv_head_pp.setText("Select " +Pref.ppText)
        assign_to_tv.setHint("Select " +Pref.ppText)
        tv_head_dd.setText("Select " +Pref.ddText)
        tv_assign_to_dd.setHint("Select " +Pref.ddText)

        var listView =  AppDatabase.getDBInstance()!!.distWiseOrderTblDao().getAllByToday(AppUtils.getCurrentDateyymmdd())
        if(listView.size>0){
            ll_view_of_dist_list.visibility = View.VISIBLE
            viewList.addAll(listView)
            adapterSetUp()
        }
        else{
            ll_view_of_dist_list.visibility = View.GONE
        }

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_enabled)
            ), intArrayOf(
                Color.BLACK,  //disabled
                ContextCompat.getColor(mContext, R.color.colorAccent) //enabled
            )
        )

        fromDate.setButtonTintList(colorStateList)
        toDate.setButtonTintList(colorStateList)

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.assign_to_rl -> {
                if(fromDateSel.equals("") || toDateSel.equals("")){
                    Toaster.msgShort(mContext,"Please Select Date Range.")
                    return
                }
                tv_assign_to_dd.text = ""

                var unuiqPPList = AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingTodateonOrderPP(fromDateSel,toDateSel)
                if(unuiqPPList.size==0){
                    return
                }
                var unuiqPPListObj :ArrayList<AssignToPPEntity> = ArrayList()
                for(i in 0..unuiqPPList.size-1){
                    unuiqPPListObj.add(AppDatabase.getDBInstance()?.ppListDao()?.getSingleValue(unuiqPPList.get(i).toString())!!)
                }

                //var assignPPList = AppDatabase.getDBInstance()?.ppListDao()?.getAll()
                var assignPPList = unuiqPPListObj
                if (assignPPList == null || assignPPList.isEmpty()) {
                    if (!TextUtils.isEmpty(Pref.profile_state)) {
                        if (AppUtils.isOnline(mContext))
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    }
                } else {
                    showAssignedToPPDialog(assignPPList, "")
                }
            }
            R.id.rl_assign_to_dd -> {
                val assignDDList = AppDatabase.getDBInstance()?.ddListDao()?.getAllDDFilterPP(assignedToPPId)
                if (assignDDList == null || assignDDList.isEmpty()) {
                    if (!TextUtils.isEmpty(Pref.profile_state)) {
                        if (AppUtils.isOnline(mContext))
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_available))
                        else
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                    }
                } else
                    showAssignedToDDDialog(assignDDList)
            }

            R.id.frag_distribute_wise_order_list_from_date_range -> {

                fromDate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_custom_green_light))
                fromDate.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                fromDate.setButtonTintList(ColorStateList.valueOf(resources.getColor(R.color.white)))
//                fromDate.buttonTintList= ColorStateList.valueOf(resources.getColor(R.color.white))

                fromDate.error = null
                fromDateSel=""
                toDateSel = ""
                toDate.text = "To Date"
                val datePicker = android.app.DatePickerDialog(
                    mContext, R.style.DatePickerTheme, date, myCalendar.get(
                        Calendar.YEAR
                    ),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.getDatePicker().maxDate = FromCalender.timeInMillis

                datePicker.show()

                datePicker.setOnDismissListener {
                    fromDate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
                    fromDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                    fromDate.buttonTintList= ColorStateList.valueOf(resources.getColor(R.color.colorAccent))
                }
            }

            R.id.frag_distribute_wise_order_list_to_date_range -> {
                if (fromDateSel.equals("") && fromDateSel.length == 0) {
                    Toaster.msgShort(mContext, "Please select From Date")
                    return

                }

                toDate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_custom_green_light))
                toDate.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                toDate.buttonTintList= ColorStateList.valueOf(resources.getColor(R.color.white))
                toDate.error = null
                val datePicker = DatePickerDialog(mContext, R.style.DatePickerTheme, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH))
                //datePicker.getDatePicker().maxDate = FromCalender.timeInMillis //+ (60*60*1000*24*90)

                var maxT= if((selectFromDate.timeInMillis+ (60*60*1000*24*23)) > FromCalender.timeInMillis)  FromCalender.timeInMillis else selectFromDate.timeInMillis+ (60*60*1000*24*23)


                var   maxCalendar:Calendar = Calendar.getInstance()
                maxCalendar.timeInMillis = selectFromDate.timeInMillis
                maxCalendar.add(Calendar.MONTH, 1);

                datePicker.getDatePicker().maxDate = maxT
                datePicker.getDatePicker().minDate = selectFromDate.timeInMillis //+ (60*60*1000*24*80)

                var maxTT= if(maxCalendar.timeInMillis > FromCalender.timeInMillis)  FromCalender.timeInMillis else maxCalendar.timeInMillis

                datePicker.getDatePicker().maxDate=maxTT

                datePicker.show()

                datePicker.setOnDismissListener {
                    toDate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
                    toDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent))
                    toDate.buttonTintList= ColorStateList.valueOf(resources.getColor(R.color.colorAccent))

                }
            }
            R.id.frag_distributor_wise_order_generated_pdf_TV -> {
                getDataForPrint()
            }
        }
    }

    fun getDataForPrint(){
        var pdfDataRoot = PdfDataRoot()

        pdfDataRoot.distName = AppDatabase.getDBInstance()?.ddListDao()?.getSingleDDValue(assignedToDDId)?.dd_name!!
        var shopL=AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByDD(assignedToDDId) as List<AddShopDBModelEntity>
        if(shopL.size>0){
            for(i in 0..shopL.size-1){
               var ordL= AppDatabase.getDBInstance()!!.orderDetailsListDao().getListAccordingTodateonOrderDD(fromDateSel,toDateSel,shopL.get(i).shop_id.toString()) as List<OrderDetailsListEntity>
                var objOrderList = OrderList()
                for(j in 0..ordL.size-1){
                   objOrderList.ordNo = ordL.get(j).order_id.toString()
                   objOrderList.ordDate=AppDatabase.getDBInstance()!!.orderDetailsListDao().getSingleOrder(ordL.get(j).order_id.toString()).only_date.toString()
                    if(AppDatabase.getDBInstance()!!.billingDao().getInvoice(ordL.get(j).order_id.toString()) != null){
                        objOrderList.invNo = AppDatabase.getDBInstance()!!.billingDao().getInvoice(ordL.get(j).order_id.toString())
                        objOrderList.invDate = AppDatabase.getDBInstance()!!.billingDao().getInvoiceDate(ordL.get(j).order_id.toString())
                    }

                   objOrderList.shop_id=AppDatabase.getDBInstance()!!.orderDetailsListDao().getSingleOrder(ordL.get(j).order_id.toString()).shop_id.toString()
                   var objShopDtls = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopByIdN(objOrderList.shop_id)!!
                   objOrderList.shop_name = objShopDtls.shopName
                   objOrderList.shop_addr = objShopDtls.address
                   objOrderList.shop_ph = objShopDtls.ownerContactNumber

                   var objProductsL = AppDatabase.getDBInstance()!!.orderProductListDao().getDataAccordingToOrderId(objOrderList.ordNo)
                   for(k in 0..objProductsL.size-1){
                       var productList = ProductList()
                       productList.item_desc=objProductsL.get(k).product_name.toString()
                       productList.qty=objProductsL.get(k).qty.toString()
                       productList.unit=objProductsL.get(k).watt.toString()
                       productList.rate=objProductsL.get(k).rate.toString()
                       productList.amt=objProductsL.get(k).total_price.toString()
                       objOrderList.productList.add(productList)
                   }
               }
                if(!objOrderList.ordNo.equals("")){
                    pdfDataRoot.ordList.add(objOrderList)
                }
            }
        }
        if(pdfDataRoot.ordList.size!=0){
            saveDataAsPdf(pdfDataRoot)
        }else{
            Toaster.msgShort(mContext,"No Data Found.")
        }
    }


    data class PdfDataRoot(var distName:String="",var  ordList:ArrayList<OrderList> = ArrayList())
    data class OrderList(var ordNo:String="",var ordDate:String="",var invNo:String="",var invDate:String="",var shop_id:String="",var shop_name:String="",
                         var shop_addr:String="",var shop_ph:String="",var productList:ArrayList<ProductList> = ArrayList())
    data class ProductList(var item_desc:String="",var qty:String="",var unit:String="",var rate:String="",var amt:String="")

    private fun saveDataAsPdf(objData: PdfDataRoot){
        var document: Document = Document()
        val random = Random()
        var fileName = "FTS" + "_" + objData.distName.uppercase()+random.nextInt(99 - 10) + 10
        fileName = fileName.replace("/", "_")

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/kcteamApp/DISTRIBUTORWISEORDERDETALIS/"

        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        try {
            PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))
            document.open()

            var font: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD)
            var fontBoldU: Font = Font(Font.FontFamily.HELVETICA, 12f, Font.UNDERLINE or Font.BOLD)
            var font1: Font = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL)
            val redFront = Font(Font.FontFamily.HELVETICA, 12f, Font.UNDERLINE or Font.BOLD, BaseColor.BLUE)
            var fontBlueBoldU: Font = Font(Font.FontFamily.HELVETICA, 12f, Font.UNDERLINE or Font.BOLD, BaseColor.ORANGE)
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

            val sp = Paragraph("", font)
            sp.spacingAfter = 50f
            document.add(sp)

            val h = Paragraph("SALES ORDER OF < " + objData.distName + " >", redFront)
            h.alignment = Element.ALIGN_CENTER

            val pHead = Paragraph()
            pHead.add(Chunk(img, 0f, -30f))
            pHead.add(h)
            document.add(pHead)

            val x = Paragraph("", font)
            x.spacingAfter = 20f
            document.add(x)


            for(i in 0..objData.ordList.size-1){
                val widthsOrder = floatArrayOf(0.50f, 0.50f)
                var tableHeaderOrder: PdfPTable = PdfPTable(widthsOrder)
                tableHeaderOrder.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
                tableHeaderOrder.setWidthPercentage(100f)

                val cell11 = PdfPCell(Phrase("Order No     :     " + objData.ordList.get(i).ordNo + "\n\n" + "Order Date  :     " + objData.ordList.get(i).ordDate, font))
                cell11.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell11.borderColor = BaseColor.GRAY
                tableHeaderOrder.addCell(cell11)

                var invDate=""
                if(!objData.ordList.get(i).invDate.equals("")){
                    invDate=AppUtils.changeAttendanceDateFormat(objData.ordList.get(i).invDate)
                }

                val cell222 = PdfPCell(Phrase("Invoice No       :     " + objData.ordList.get(i).invNo + "\n\n" + "Invoice Date    :     " + invDate,font))
                cell222.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell222.borderColor = BaseColor.GRAY
                cell222.paddingBottom = 10f
                tableHeaderOrder.addCell(cell222)
                document.add(tableHeaderOrder)
                document.add(Paragraph())
                val xz = Paragraph("", font)
                xz.spacingAfter = 10f
                document.add(xz)


                val HeadingPartyDetls = Paragraph("Details of Party ", fontBoldU)
                HeadingPartyDetls.indentationLeft = 82f
                HeadingPartyDetls.spacingAfter = 2f
                document.add(HeadingPartyDetls)

                val Parties = Paragraph("Name                    :      " +  objData.ordList.get(i).shop_name, font1)
                Parties.alignment = Element.ALIGN_LEFT
                Parties.spacingAfter = 2f
                document.add(Parties)

                val address = Paragraph("Address                :      " + objData.ordList.get(i).shop_addr, font1)
                address.alignment = Element.ALIGN_LEFT
                address.spacingAfter = 2f
                document.add(address)

                val Contact = Paragraph("Contact No.          :      " + objData.ordList.get(i).shop_ph, font1)
                Contact.alignment = Element.ALIGN_LEFT
                Contact.spacingAfter = 2f
                document.add(Contact)



                val xze = Paragraph("", font)
                xze.spacingAfter = 10f
                document.add(xze)

                // table header
                val widths = floatArrayOf(0.06f, 0.58f, 0.07f, 0.07f, 0.07f, 0.15f)

                var tableHeader: PdfPTable = PdfPTable(widths)
                tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT)
                tableHeader.setWidthPercentage(100f)

                val cell111 = PdfPCell(Phrase("SL. ", font))
                cell111.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell111.borderColor = BaseColor.GRAY
                tableHeader.addCell(cell111);

                val cell1 = PdfPCell(Phrase("Item Description ", font))
                cell1.setHorizontalAlignment(Element.ALIGN_LEFT)
                cell1.borderColor = BaseColor.GRAY
                tableHeader.addCell(cell1);

                val cell2 = PdfPCell(Phrase("Qty ", font))
                cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell2.borderColor = BaseColor.GRAY
                tableHeader.addCell(cell2);

                val cell21 = PdfPCell(Phrase("Unit ", font))
                cell21.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell21.borderColor = BaseColor.GRAY
                tableHeader.addCell(cell21);

                val cell3 = PdfPCell(Phrase("Rate ", font))
                cell3.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell3.borderColor = BaseColor.GRAY
                tableHeader.addCell(cell3);

                val cell4 = PdfPCell(Phrase("Amount ", font))
                cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell4.borderColor = BaseColor.GRAY
                tableHeader.addCell(cell4);

                document.add(tableHeader)


                //table body
                var srNo: String = ""
                var item: String = ""
                var qty: String = ""
                var unit: String = ""
                var rate: String = ""
                var amount: String = ""
                var tAmt="0"
                var tempProductObj = objData.ordList.get(i).productList!!
                for (j in 0..tempProductObj.size - 1) {
                    srNo = (j + 1).toString() + " "
                    item = tempProductObj.get(j).item_desc + "       "
                    qty = tempProductObj.get(j).qty + " "
                    unit = "KG" + " "
                    rate =
                        getString(R.string.rupee_symbol_with_space) + " " + tempProductObj.get(j).rate + " "
                    amount =
                        getString(R.string.rupee_symbol_with_space) + " " + tempProductObj.get(j).amt + " "


                    tAmt = (tAmt.toDouble()+tempProductObj.get(j).amt.toDouble()).toString()

                    val tableRows = PdfPTable(widths)
                    tableRows.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                    tableRows.setWidthPercentage(100f);


                    var cellBodySr = PdfPCell(Phrase(srNo, font1))
                    cellBodySr.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellBodySr.borderColor = BaseColor.GRAY
                    tableRows.addCell(cellBodySr)

                    var cellBodySl = PdfPCell(Phrase(item, font1))
                    cellBodySl.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cellBodySl.borderColor = BaseColor.GRAY
                    tableRows.addCell(cellBodySl)

                    var cellBody2 = PdfPCell(Phrase(qty, font1))
                    cellBody2.setHorizontalAlignment(Element.ALIGN_LEFT)
                    cellBody2.borderColor = BaseColor.GRAY
                    tableRows.addCell(cellBody2)


                    var cellBody21 = PdfPCell(Phrase(unit, font1))
                    cellBody21.setHorizontalAlignment(Element.ALIGN_LEFT)
                    cellBody21.borderColor = BaseColor.GRAY
                    tableRows.addCell(cellBody21)

                    var cellBody3 = PdfPCell(Phrase(rate, font1))
                    cellBody3.setHorizontalAlignment(Element.ALIGN_LEFT)
                    cellBody3.borderColor = BaseColor.GRAY
                    tableRows.addCell(cellBody3)

                    var cellBody4 = PdfPCell(Phrase(amount, font1))
                    cellBody4.setHorizontalAlignment(Element.ALIGN_LEFT)
                    cellBody4.borderColor = BaseColor.GRAY
                    tableRows.addCell(cellBody4)

                    document.add(tableRows)

                    document.add(Paragraph())
                }
                val xffx = Paragraph("", font)
                xffx.spacingAfter = 12f
                document.add(xffx)

                val para1 = Paragraph()
                val glue1 = Chunk(VerticalPositionMark())
                val ph11 = Phrase()
                val main1 = Paragraph()
                ph11.add(Chunk("Rupees " + NumberToWords.numberToWord(tAmt.toDouble().toInt()!!)!!.toUpperCase() + " Only  ", font))
                ph11.add(glue1) // Here I add special chunk to the same phrase.

                ph11.add(Chunk("Total  Amount: " + "\u20B9" + tAmt.toString(), font))
                para1.add(ph11)
                document.add(para1)


                val xfx = Paragraph("", font)
                xfx.spacingAfter = 12f
                document.add(xfx)


                val widthsSalesPerson = floatArrayOf(1f)

                var tablewidthsSalesPersonHeader: PdfPTable = PdfPTable(widthsSalesPerson)
                tablewidthsSalesPersonHeader.getDefaultCell()
                    .setHorizontalAlignment(Element.ALIGN_LEFT)
                tablewidthsSalesPersonHeader.setWidthPercentage(100f)

                val cellsales = PdfPCell(Phrase("Entered by: " + Pref.user_name, font1))
                cellsales.setHorizontalAlignment(Element.ALIGN_LEFT)
                cellsales.borderColor = BaseColor.GRAY
                tablewidthsSalesPersonHeader.addCell(cellsales)


                document.add(tablewidthsSalesPersonHeader)

                val canvas = Paragraph("                                                                                                                                                                                                                                                                                                                         ", fontBlueBoldU)
                canvas.spacingAfter = 2f
                document.add(canvas)

                val xfffx = Paragraph("", font)
                xfffx.spacingAfter = 12f
                document.add(xfffx)

            }
            document.close()
            addDataroom()
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

        }catch (ex: Exception) {
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage("No Data Avalibale On Selected Date....")
        }

    }

    private fun addDataroom() {
        var obj: DistWiseOrderTblEntity = DistWiseOrderTblEntity()
        obj.from_date = fromDateSel
        obj.to_date = toDateSel
        obj.selected_pp = assign_to_tv.text.toString()
        obj.selected_dd = tv_assign_to_dd.text.toString()
        obj.genereated_date_time = AppUtils.getCurrentDateTime()
        obj.only_date = AppUtils.getCurrentDateyymmdd()
        AppDatabase.getDBInstance()!!.distWiseOrderTblDao().insertAll(obj)

        viewList.add(obj)

        if(obj!=null){
            ll_view_of_dist_list.visibility = View.VISIBLE
            adapterSetUp()
        }


    }


    private val myCalendar: Calendar by lazy {
        Calendar.getInstance(Locale.ENGLISH)
    }

    var selectFromDate = Calendar.getInstance()

    val date = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        selectFromDate.set(year, monthOfYear, dayOfMonth)
        fromDate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
        //tv_date_dialog.text=  AppUtils.getFormatedDateNew(AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time)),"dd-mm-yyyy","yyyy-mm-dd")
        fromDate.text =
            AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time))
        fromDateSel = AppUtils.getFormattedDateForApi(myCalendar.time)
    }
    val date1 = android.app.DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year)
        myCalendar.set(Calendar.MONTH, monthOfYear)
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        //tv_date_dialog.text=  AppUtils.getFormatedDateNew(AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time)),"dd-mm-yyyy","yyyy-mm-dd")
        toDate.text = AppUtils.getBillingDateFromCorrectDate(AppUtils.getFormattedDateForApi(myCalendar.time))
        toDate.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
        toDateSel = AppUtils.getFormattedDateForApi(myCalendar.time)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun showAssignedToPPDialog(mAssignedList: List<AssignToPPEntity>?, type: String?) {
        AssignedToPPCustomDialog.newInstance(
            mAssignedList,"Select ${Pref.ppText}", type!!,
            object : AssignedToPPCustomDialog.OnItemSelectedListener {
                override fun onItemSelect(pp: AssignToPPEntity?) {
                    assign_to_tv.text = pp?.pp_name + " (" + pp?.pp_phn_no + ")"
                    assignedToPPId = pp?.pp_id.toString()
                }
            }).show(fragmentManager!!, "")
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun showAssignedToDDDialog(mAssignedList: List<AssignToDDEntity>?) {
        AssignedToDDCustomDialog.newInstance(
            mAssignedList,"Select ${Pref.ddText}",
            object : AssignedToDDCustomDialog.OnItemSelectedListener {
                override fun onItemSelect(dd: AssignToDDEntity?) {
                    tv_assign_to_dd.text = dd?.dd_name + " (" + dd?.dd_phn_no + ")"
                    assignedToDDId = dd?.dd_id.toString()
                }
            }).show(fragmentManager!!, "")
    }

    private fun adapterSetUp() {
        viewDistOrderProductAdapter = ViewDistOrderProductAdapter(mContext,viewList)
        rv_vw_dist_order_list.adapter=viewDistOrderProductAdapter
    }

}