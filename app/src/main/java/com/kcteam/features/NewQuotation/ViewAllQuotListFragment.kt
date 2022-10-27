package com.kcteam.features.NewQuotation

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.NewQuotation.adapter.ViewAllQuotViewAdapter
import com.kcteam.features.NewQuotation.api.GetQuotRegProvider
import com.kcteam.features.NewQuotation.model.ViewDetailsQuotResponse
import com.kcteam.features.NewQuotation.model.ViewQuotResponse
import com.kcteam.features.NewQuotation.model.shop_wise_quotation_list
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.model.TeamShopListDataModel
import com.kcteam.widgets.AppCustomTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ViewAllQuotListFragment : BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context
    var viewAllQuotRecyclerViewAdapter: ViewAllQuotViewAdapter?=null
    private lateinit var quot_list_rv: RecyclerView
    lateinit var no_quot_tv: AppCustomTextView
    lateinit var progress_wheel: ProgressWheel
    lateinit var shop_IV: ImageView
    lateinit var myshop_name_TV: AppCustomTextView
    lateinit var myshop_address_TV: AppCustomTextView
    lateinit var tv_contact_number: AppCustomTextView
    lateinit var add_quot_tv: FloatingActionButton
    var i: Int = 0
    var addedQuotList:ArrayList<shop_wise_quotation_list> = ArrayList()

    lateinit var simpleDialog: Dialog
    lateinit var addQuotEditResult: ViewDetailsQuotResponse

    companion object {
        var shop_id: String = ""
        var shop_name: String? = null
        var shop_contact_number: String? = null
        var address: String? = null
        var obj = TeamShopListDataModel()
        fun getInstance(shopObj: Any?): ViewAllQuotListFragment {
            val mQuotListFragment = ViewAllQuotListFragment()
            if (!TextUtils.isEmpty(shopObj.toString())) {
                obj = shopObj as TeamShopListDataModel
                shop_id = obj!!.shop_id.toString()
                shop_name = obj!!.shop_name
                shop_contact_number = obj!!.shop_contact
                address = obj!!.shop_address
            }
            return mQuotListFragment
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_view_all_quot_list, container, false)
        initView(view)

        if(AppUtils.isOnline(mContext)){
            quotListCall(shop_id)
        }
        else{
            Toaster.msgShort(mContext, "No Internet connection")
        }

        return view
    }



    private fun initView(view: View) {
        quot_list_rv = view.findViewById(R.id.quot_list_rv)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        no_quot_tv = view.findViewById(R.id.no_quot_tv)
        shop_IV = view.findViewById(R.id.shop_IV)
        myshop_name_TV = view.findViewById(R.id.myshop_name_TV)
        myshop_address_TV = view.findViewById(R.id.myshop_address_TV)
        tv_contact_number = view.findViewById(R.id.tv_contact_number)
        add_quot_tv = view.findViewById(R.id.add_quot_tv)
        setData()
        add_quot_tv.setOnClickListener(this)

        CustomStatic.IsNewQuotEdit=false

    }


    private fun setData() {
        progress_wheel.stopSpinning()
        myshop_address_TV.text = address
        myshop_name_TV.text = shop_name
        tv_contact_number.text = shop_contact_number
        val drawable = TextDrawable.builder()
                .buildRoundRect(shop_name!!.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)
        shop_IV.setImageDrawable(drawable)
    }

    override fun onClick(p0: View?) {
        i = 0
        when (p0?.id) {
            R.id.add_quot_tv -> {
                (mContext as DashboardActivity).loadFragment(FragType.AddQuotFormFragment, true, obj)
            }
        }
    }


    private fun quotListCall(shopId: String) {
        try{
            progress_wheel.spin()
            val repository = GetQuotRegProvider.provideSaveButton()
            BaseActivity.compositeDisposable.add(
                    repository.viewQuot(shopId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addQuotResult = result as ViewQuotResponse
                                progress_wheel.stopSpinning()
                                if (addQuotResult!!.status == NetworkConstant.SUCCESS) {
                                    if (addQuotResult!!.shop_wise_quotation_list!!.size > 0) {
                                        quot_list_rv.visibility = View.VISIBLE
                                        no_quot_tv.visibility = View.GONE
                                        addedQuotList.clear()
                                        addedQuotList.addAll(addQuotResult!!.shop_wise_quotation_list!!)

                                        addedQuotList.reverse()

                                        setAdapter()
                                    }

                                } else {
                                    quot_list_rv.visibility = View.GONE
                                    no_quot_tv.visibility = View.VISIBLE
//                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                progress_wheel.stopSpinning()
                                BaseActivity.isApiInitiated = false
                                if (error != null) {
                                }
                            })
            )
        }catch (ex: Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
            progress_wheel.stopSpinning()
        }

    }

    private fun setAdapter() {
        viewAllQuotRecyclerViewAdapter = ViewAllQuotViewAdapter(mContext, addedQuotList, object : ViewAllQuotViewAdapter.OnClickListener {
            override fun onView(adapterPosition: Int, QuotId: String,DocId: String) {
                if(QuotId==null || QuotId.equals("")){
                    (mContext as DashboardActivity).loadFragment(FragType.ViewDetailsQuotFragment, true, "x,$DocId")
                }
                else{
                    (mContext as DashboardActivity).loadFragment(FragType.ViewDetailsQuotFragment, true, "$QuotId,x")
                }

            }

            override fun onShare(adapterPosition: Int) {
                getDtlsBeforePDF(addedQuotList.get(adapterPosition))

            }

            override fun onShowMsg(msg: String) {
                Toaster.msgShort(mContext,msg)
            }

            override fun onDelete(adapterPosition: Int, QuotId: String) {
                simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_yes_no)
                val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                val dialogBody = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                val btn_no = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                val btn_yes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView

                dialogHeader.text = AppUtils.hiFirstNameText() + "!"
                dialogBody.text = "Do you want to delete this Quotation?..."

                btn_yes.setOnClickListener({ view ->
                    deleteQuot(QuotId)
                })
                btn_no.setOnClickListener({ view ->
                    simpleDialog.cancel()
                })
                simpleDialog.show()

            }

        })
        quot_list_rv.adapter=viewAllQuotRecyclerViewAdapter
    }

    private fun deleteQuot(quotId: String) {
        try{
            progress_wheel.spin()
            val repository = GetQuotRegProvider.provideSaveButton()
            BaseActivity.compositeDisposable.add(
                    repository.delQuot(quotId)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val delQuotResult = result as BaseResponse
                                progress_wheel.stopSpinning()
                                if (delQuotResult!!.status == NetworkConstant.SUCCESS) {
                                    (mContext as DashboardActivity).showSnackMessage(delQuotResult.message!!)
                                    simpleDialog.cancel()
                                    updateView()

                                } else {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                progress_wheel.stopSpinning()
                                BaseActivity.isApiInitiated = false
                                if (error != null) {
                                }
                            })
            )
        }catch (ex: Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
            progress_wheel.stopSpinning()
        }

    }

    private fun getDtlsBeforePDF(obj: shop_wise_quotation_list){
        try{
            progress_wheel.spin()
            val repository = GetQuotRegProvider.provideSaveButton()
            BaseActivity.compositeDisposable.add(
                    repository.viewDetailsQuot(obj.quotation_number!!)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val addQuotResult = result as ViewDetailsQuotResponse
                                addQuotEditResult = addQuotResult
                                progress_wheel.stopSpinning()
                                if (addQuotResult!!.status == NetworkConstant.SUCCESS) {
                                    saveDataAsPdf(addQuotEditResult)
                                } else {
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                }
                                BaseActivity.isApiInitiated = false
                            }, { error ->
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                                progress_wheel.stopSpinning()
                                BaseActivity.isApiInitiated = false
                                if (error != null) {
                                }
                            })
            )
        }catch (ex: Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
            progress_wheel.stopSpinning()
        }

    }


//    @SuppressLint("UseRequireInsteadOfGet")
//    private fun saveDataAsPdf(addQuotEditResult: ViewDetailsQuotResponse) {
//        var document: Document = Document()
//        val time = System.currentTimeMillis()
//        //val fileName = "QUTO_" +  "_" + time
//        var fileName = addQuotEditResult.quotation_number!!.toUpperCase() +  "_" + time
//        fileName=fileName.replace("/", "_")
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/kcteamApp/QUTO/"
//
//        val dir = File(path)
//        if (!dir.exists()) {
//            dir.mkdirs()
//        }
//
//        try{
//            progress_wheel.spin()
//            var pdfWriter :PdfWriter = PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))
//            val event = HeaderFooterPageEvent()
//            pdfWriter.setPageEvent(event)
//
//            //PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))
//            document.open()
//
//            var font: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD)
//            var fontB1: Font = Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD)
//            var font1: Font = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL)
//            var font1Big: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)
//            val grayFront = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.GRAY)
//
//
//            //image add
//            val bm: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.breezelogo)
//            val bitmap = Bitmap.createScaledBitmap(bm, 220, 90, true);
//            val stream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//            var img: Image? = null
//            val byteArray: ByteArray = stream.toByteArray()
//            try {
//                img = Image.getInstance(byteArray)
//                img.scaleToFit(155f,90f)
//                img.scalePercent(70f)
//                img.alignment=Image.ALIGN_RIGHT
//            } catch (e: BadElementException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            document.add(img)
//
//
//            //var quotDate = AppUtils.getFormatedDateNew(addQuotEditResult.quotation_date_selection!!.replace("12:00:00 AM",""),"mm-dd-yyyy","dd-mm-yyyy")
//
//            /*val dateLine = Paragraph("DATE: " + addQuotEditResult.quotation_date_selection!! +
//                    "                                                              " + addQuotEditResult.quotation_number, font)
//            dateLine.alignment = Element.ALIGN_LEFT
//            dateLine.spacingAfter = 5f
//            document.add(dateLine)*/
//
//
//            val para = Paragraph()
//            val glue = Chunk(VerticalPositionMark())
//            val ph1 = Phrase()
//            val main = Paragraph()
//            ph1.add(Chunk("DATE: " + addQuotEditResult.quotation_date_selection!!, font))
//            ph1.add(glue) // Here I add special chunk to the same phrase.
//
//            ph1.add(Chunk(addQuotEditResult.quotation_number + "                         ", font))
//            para.add(ph1)
//            document.add(para)
//
//            val xxx = Paragraph("", font)
//            xxx.spacingAfter = 5f
//            document.add(xxx)
//
//            val toLine = Paragraph("To,", font)
//            toLine.alignment = Element.ALIGN_LEFT
//            toLine.spacingAfter = 2f
//            document.add(toLine)
//
//            val cusName = Paragraph(addQuotEditResult.shop_name, font)
//            cusName.alignment = Element.ALIGN_LEFT
//            cusName.spacingAfter = 2f
//            document.add(cusName)
//
//
//            //// addr test begin
//            var finalStr =""
//            try{
//                var str = addQuotEditResult.shop_addr.toString().toCharArray()
//                //var str = "1602, Marathon Icon,Opp. Peninsula Corporate Park, Off Ganpatrao Kadam Marg,Lower Parel (W),".toCharArray()
//                //var str = "Chhatrapati Shivaji Terminus Main Post Office, Borabazar Precinct, Ballard Estate, Fort, Mumbai, Maharashtra 400001, India".toCharArray()
//                finalStr =""
//                var isNw=false
//                var comCnt=0
//                for(i in 0..str.size-1){
//                    if(str[i].toString().equals(",")){
//                        comCnt++
//                        finalStr=finalStr+","
//                        if(comCnt%2==0){
//                            finalStr=finalStr+"\n"
//                            isNw=true
//                        }
//                    }else {
//                        if(isNw && str[i].toString().equals(" ")){
//                            isNw=false
//                        }else{
//                            finalStr=finalStr+str[i].toString()
//                        }
//                    }
//                }
//            }catch (ex:Exception){
//                finalStr=""
//            }
//
//
//            //// addr test end
//
////            val cusAddress = Paragraph(addQuotEditResult.shop_addr, font)
//            val cusAddress = Paragraph(finalStr, font)
//            cusAddress.alignment = Element.ALIGN_LEFT
//            cusAddress.spacingAfter = 5f
//            document.add(cusAddress)
//
////            val cusemail = Paragraph("Email : " + addQuotEditResult.shop_email, font)
////            cusemail.alignment = Element.ALIGN_LEFT
////            cusemail.spacingAfter = 5f
////            document.add(cusemail)
//
//
//            val projectName = Paragraph("Project Name : "+addQuotEditResult.project_name, font)
//            projectName.alignment = Element.ALIGN_LEFT
//            projectName.spacingAfter = 5f
//            document.add(projectName)
//
//            val cusemail = Chunk("Email : " +  addQuotEditResult.shop_email, font)
//            //cusemail.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
//            document.add(cusemail)
//
//
//            val xx = Paragraph("", font)
//            xx.spacingAfter = 6f
//            document.add(xx)
//
//            //val cusowner = Paragraph("Kind Attn. " + addQuotEditResult.shop_owner_name +"  "+ "(Mob.No.  " + addQuotEditResult.shop_phone_no +  ")", font)
//            val cusowner = Chunk("Kind Attn. " +  "Mr./Mrs. "+addQuotEditResult.shop_owner_name + "  " + "(Mob.No.  " + addQuotEditResult.shop_phone_no + ")", font)
//            cusowner.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
//            //cusowner.alignment = Element.ALIGN_LEFT
//            //cusowner.spacingAfter = 5f
//            document.add(cusowner)
//
//
//
//            val x = Paragraph("", font)
//            //cusemail.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
//            x.spacingAfter = 6f
//            document.add(x)
//
//
//            //val sub = Paragraph("Sub :-Quotation For Eurobond-ALUMINIUM COMPOSITE PANEL", font)
//            //val sub = Chunk("Sub :-Quotation For Eurobond-ALUMINIUM COMPOSITE PANEL", font)
//            val sub = Chunk("Sub :-Quotation For "+getString(R.string.app_name), font)
//            sub.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
//            //sub.alignment = Element.ALIGN_LEFT
//            //sub.spacingAfter = 10f
//            document.add(sub)
//
//            val body = Paragraph("\nSir,\n" +
//                    "In reference to the discusssion held with you regarding the said subject,we are please to quote our most " +
//                    "preferred rates & others terms and condition for the same as follows.\n", grayFront)
//            body.alignment = Element.ALIGN_LEFT
//            body.spacingAfter = 10f
//            document.add(body)
//
//            // table header
//            //val widths = floatArrayOf(0.05f, 0.55f, 0.2f, 0.2f)
//            val widths = floatArrayOf(0.07f, 0.40f,0.13f, 0.2f, 0.2f)
//
//            var tableHeader: PdfPTable = PdfPTable(widths)
//            tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
//            tableHeader.setWidthPercentage(100f)
//
//            val cell1 = PdfPCell(Phrase("Sr.No",font1Big))
//            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//            tableHeader.addCell(cell1);
//
//            val cell2 = PdfPCell(Phrase("Description",font1Big))
//            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
//            tableHeader.addCell(cell2);
//
//            val cell2_1 = PdfPCell(Phrase("Color Code",font1Big))
//            cell2_1.setHorizontalAlignment(Element.ALIGN_CENTER);
//            tableHeader.addCell(cell2_1);
//
//            val cell3 = PdfPCell(Phrase("Rate/Sq.Mtr (INR)",font1Big))
//            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
//            tableHeader.addCell(cell3);
//
//            val cell4 = PdfPCell(Phrase("Rate/Sq.Ft (INR)",font1Big))
//            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
//            tableHeader.addCell(cell4);
//
//            //tableHeader.addCell(PdfPCell(Phrase("Description", font1)))
//            //tableHeader.addCell(PdfPCell(Phrase("Rate/Sq.Mtr (INR)", font1)))
//            //tableHeader.addCell(PdfPCell(Phrase("Rate/Sq.Ft (INR)", font1)))
//
//            //tableHeader.addCell("Sr. No.")
//            //tableHeader.addCell("Description.")
//            //tableHeader.addCell("Rate/Sq.Mtr (INR)")
//            //tableHeader.addCell("Rate/Sq.Ft (INR)")
//            document.add(tableHeader)
//
//            //table body
//            var srNo:String=""
//            var desc:String=""
//            var catagory:String=""
//            var colorCode:String=""
//            var rateSqFt:String=""
//            var rateSqMtr:String=""
//            var obj=addQuotEditResult.quotation_product_details_list
//            for (i in 0..obj!!.size-1) {
//                srNo= (i+1).toString()
//                desc=obj!!.get(i).product_name.toString() //+ "\n\n"+ "Color Code : "+obj.get(i).color_name
//                colorCode = obj.get(i).color_name.toString()
//                rateSqFt="INR - "+obj!!.get(i).rate_sqft.toString()
//                rateSqMtr="INR - "+obj!!.get(i).rate_sqmtr.toString()
//                try{
//                    catagory = AppDatabase.getDBInstance()?.productListDao()?.getSingleProduct(obj!!.get(i).product_id!!.toInt()!!)!!.category.toString()
//                }catch (ex:Exception){
//                    catagory=""
//                }
//                desc=desc+"\n"+catagory
//
//                val tableRows = PdfPTable(widths)
//                tableRows.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
//                tableRows.setWidthPercentage(100f);
//
//                var cellBodySl = PdfPCell(Phrase(srNo,font1Big))
//                cellBodySl.setHorizontalAlignment(Element.ALIGN_CENTER);
//                tableRows.addCell(cellBodySl)
//
//                var cellBodyDesc = PdfPCell(Phrase(desc,font1Big))
//                cellBodyDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
//                tableRows.addCell(cellBodyDesc)
//
//                var cellBodyColor = PdfPCell(Phrase(colorCode,font1Big))
//                cellBodyColor.setHorizontalAlignment(Element.ALIGN_CENTER);
//                tableRows.addCell(cellBodyColor)
//
//                var cellBodySqMtr = PdfPCell(Phrase(rateSqMtr,font1Big))
//                cellBodySqMtr.setHorizontalAlignment(Element.ALIGN_CENTER);
//                tableRows.addCell(cellBodySqMtr)
//
//                var cellBodySqFt = PdfPCell(Phrase(rateSqFt,font1Big))
//                cellBodySqFt.setHorizontalAlignment(Element.ALIGN_CENTER);
//                tableRows.addCell(cellBodySqFt)
//
//
//                document.add(tableRows)
//
//                document.add(Paragraph())
//            }
//
//
//
//            val terms = Chunk("\nTerms & Conditions:-", font)
////            terms.alignment = Element.ALIGN_LEFT
////            terms.spacingAfter = 5f
//            terms.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
//            document.add(terms)
//
//
//
//            val taxs = Paragraph("Taxes                                                  :     " + addQuotEditResult.taxes, font1Big)
//            taxs.alignment = Element.ALIGN_LEFT
//            taxs.spacingAfter = 2f
//            document.add(taxs)
//
//
//            val freight = Paragraph("Freight                                                 :     " + addQuotEditResult.Freight, font1Big)
//            freight.alignment = Element.ALIGN_LEFT
//            freight.spacingAfter = 2f
//            document.add(freight)
//
//
//            val delivery = Paragraph("Delivery Time                                      :     " + addQuotEditResult.delivery_time, font1Big)
//            delivery.alignment = Element.ALIGN_LEFT
//            delivery.spacingAfter = 2f
//            document.add(delivery)
//
//
//            val payment = Paragraph("Payment                                              :     " + addQuotEditResult.payment, font1Big)
//            payment.alignment = Element.ALIGN_LEFT
//            payment.spacingAfter = 2f
//            document.add(payment)
//
//            val validity = Paragraph("Validity                                                 :     " + addQuotEditResult.validity, font1Big)
//            validity.alignment = Element.ALIGN_LEFT
//            validity.spacingAfter = 2f
//            document.add(validity)
//
//            val billing = Paragraph("Billing                                                   :     " + addQuotEditResult.billing, font1Big)
//            billing.alignment = Element.ALIGN_LEFT
//            billing.spacingAfter = 2f
//            document.add(billing)
//
//            val product_tolerance_of_thickness = Paragraph("Product Tolerance of Thickness          :     " + addQuotEditResult.product_tolerance_of_thickness, font1Big)
//            product_tolerance_of_thickness.alignment = Element.ALIGN_LEFT
//            product_tolerance_of_thickness.spacingAfter = 2f
//            document.add(product_tolerance_of_thickness)
//
//            val product_tolerance_of_coating = Paragraph("Tolerance of Coating Thickness          :     " + addQuotEditResult.tolerance_of_coating_thickness, font1Big)
//            product_tolerance_of_coating.alignment = Element.ALIGN_LEFT
//            product_tolerance_of_coating.spacingAfter = 3f
//            document.add(product_tolerance_of_coating)
//
//
//            val end = Paragraph("Anticipating healthy business relation with your esteemed organization.", grayFront)
//            end.alignment = Element.ALIGN_LEFT
//            end.spacingAfter = 4f
//            document.add(end)
//
//            val thanks = Paragraph("\nThanks & Regards,", fontB1)
//            thanks.alignment = Element.ALIGN_LEFT
//            thanks.spacingAfter = 2f
//            document.add(thanks)
//
//            //val companyName = Paragraph("EURO PANEL PRODUCTS LIMITED", fontB1)
//            val companyName = Paragraph(getString(R.string.app_name), fontB1)
//            companyName.alignment = Element.ALIGN_LEFT
//            companyName.spacingAfter = 2f
//            document.add(companyName)
//
//            val salesmanName = Paragraph(addQuotEditResult.salesman_name, fontB1)
//            salesmanName.alignment = Element.ALIGN_LEFT
//            salesmanName.spacingAfter = 2f
//            document.add(salesmanName)
//
//            val salesmanDes = Paragraph(addQuotEditResult.salesman_designation, fontB1)
//            salesmanDes.alignment = Element.ALIGN_LEFT
//            salesmanDes.spacingAfter = 2f
//            document.add(salesmanDes)
//
//            //val salesmanphone = Paragraph(addQuotEditResult.salesman_phone_no, fontB1)
//            val salesmanphone = Paragraph(addQuotEditResult.salesman_login_id, fontB1)
//            salesmanphone.alignment = Element.ALIGN_LEFT
//            salesmanphone.spacingAfter =  2f
//            document.add(salesmanphone)
//
//            val salesmanemail = Paragraph("Email : "+addQuotEditResult.salesman_email, fontB1)
//            salesmanemail.alignment = Element.ALIGN_LEFT
//            salesmanemail.spacingAfter =  2f
//            document.add(salesmanemail)
//
//            val xxxx = Paragraph("", font)
//            xxxx.spacingAfter = 4f
//            document.add(xxxx)
//
//            //val euroHead = Paragraph("\nEURO PANEL PRODUCTS LIMITED", font)
//            val euroHead = Paragraph("\n"+getString(R.string.app_name), font)
//            euroHead.alignment = Element.ALIGN_LEFT
//            //document.add(euroHead)
//
//            //strip_line//bar//ics
//            //Hardcoded for EuroBond
////            val bm1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ics_image)
//            val bm1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bar)
//            val bitmap1 = Bitmap.createScaledBitmap(bm1, 850, 120, true)
//            val stream1 = ByteArrayOutputStream()
//            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1)
//            var img1: Image? = null
//            val byteArray1: ByteArray = stream1.toByteArray()
//            try {
//                img1 = Image.getInstance(byteArray1)
//                img1.alignment=Image.ALIGN_LEFT
//            } catch (e: BadElementException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
////            document.add(img1)
//
//            val bm2: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bar)
//            val bitmap2 = Bitmap.createScaledBitmap(bm2, 50, 50, true)
//            val stream2 = ByteArrayOutputStream()
//            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream2)
//            var img2: Image? = null
//            val byteArray2: ByteArray = stream2.toByteArray()
//            try {
//                img2 = Image.getInstance(byteArray2)
//            } catch (e: BadElementException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
////            document.add(img2)
//
//
//            val companydel = Paragraph("Regd.Off: 702,Aravali Business Centre,Ramadas Sutrale Road,Borivali(West),Mumbai-400 092." +
//                    "Factory: Survey No.124/4,Manekpur,Sanjan,Khattalwada,Taluka- Umbergaon,Dist.Valsad,Gujarat - 396120" +
//                    "T: +91-22-29686500(30 lines) +91-7666625999 - E: sale@eurobondacp.com + W: www.eurobondacp.com + CIN: U28931MH2013PTC251176" +
//                    "", font1)
//            companydel.alignment = Element.ALIGN_RIGHT
//            companydel.spacingAfter = 10f
//            //document.add(img1)
//            //document.add(img2)
//            //img2!!.alignment=Image.ALIGN_CENTER
//            //document.add(companydel)
//
//
//            val tablee = PdfPTable(1)
//            tablee.widthPercentage = 100f
//            var cell = PdfPCell()
//            var p = Paragraph()
//            p.alignment=Element.ALIGN_LEFT
//            img1!!.scalePercent(50f)
//            p.add(Chunk(img1, 0f, 0f, true))
//            //p.add(Chunk(img2, 0f, 0f, true))
//            //p.add(companydel)
//            cell.addElement(p)
//            cell.backgroundColor= BaseColor(0, 0, 0, 0)
//            cell.borderColor=BaseColor(0, 0, 0, 0)
//
//            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
//            tablee.addCell(cell)
//            //document.add(tablee)
//
//
//            val bm3: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.strip_line)
//            val bitmap3 = Bitmap.createScaledBitmap(bm3, 520, 20, true)
//            val stream3 = ByteArrayOutputStream()
//            bitmap3.compress(Bitmap.CompressFormat.PNG, 100, stream3)
//            var img3: Image? = null
//            val byteArray3: ByteArray = stream3.toByteArray()
//            try {
//                img3 = Image.getInstance(byteArray3)
//            } catch (e: BadElementException) {
//                e.printStackTrace()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//            //document.add(img3)
//
//
//            document.close()
//
//
//            var sendingPath=path+fileName+".pdf"
//            /*if (!TextUtils.isEmpty(sendingPath)) {
//               try {
//                   val shareIntent = Intent(Intent.ACTION_SEND)
//                   shareIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
//                   shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("saheli.bhattacharjee@indusnet.co.in","suman.bachar@indusnet.co.in"))
////                    shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("sales1@eurobondacp.com","sales@eurobondacp.com"))
//                   shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quotation for $shop_name created on dated ${addQuotEditResult.save_date_time}.")
//                   shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello Team,  \n Please find attached Quotation No. ${addQuotEditResult.quotation_number} Dated ${addQuotEditResult.save_date_time} " +
//                           " for $shop_name \n\n\n" +
//                           "Regards \n${Pref.user_name}. ")
//                   shareIntent.type = "message/rfc822"
//                   val fileUrl = Uri.parse(sendingPath)
//                   val file = File(fileUrl.path)
//                   val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
////                    shareIntent.type = "image/png"
//                   shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//                   startActivity(Intent.createChooser(shareIntent, "Share pdf using"))
//               } catch (e: Exception) {
//                   e.printStackTrace()
//                   (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
//               }
//           }*/
//
//            /*if (!TextUtils.isEmpty(sendingPath)) {
//                try {
//                    val shareIntent = Intent(Intent.ACTION_SEND)
//                    val fileUrl = Uri.parse(sendingPath)
//                    val file = File(fileUrl.path)
//                    val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
//                    shareIntent.type = "image/png"
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
//                    startActivity(Intent.createChooser(shareIntent, "Share pdf using"))
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
//                }
//            }*/
//
//
//            val m = Mail("eurobondacp02@gmail.com", "nuqfrpmdjyckkukl")
////            val m = Mail("saheli.bhattacharjee@indusnet.co.in", "@Intsaheli22")
//            val toArr = arrayOf("saheli.bhattacharjee@indusnet.co.in","suman.bachar@indusnet.co.in","suman.roy@indusnet.co.in")
////            val toArr = arrayOf("sales1@eurobondacp.com", "sales@eurobondacp.com")
//            m.setTo(toArr)
//            m.setFrom("TEAM");
//            m.setSubject("Quotation for $shop_name created on dated ${addQuotEditResult.save_date_time!!.split(" ").get(0)}.")
//            m.setBody("Hello Team,  \n Please find attached Quotation No. ${addQuotEditResult.quotation_number} Dated ${addQuotEditResult.save_date_time!!.split(" ").get(0)} for $shop_name \n\n\n Regards \n${Pref.user_name}.")
//            doAsync {
//                val fileUrl = Uri.parse(sendingPath)
//                val i = m.send(fileUrl.path)
//                uiThread {
//                    progress_wheel.stopSpinning()
//                    openDialogPopup("Hi ${Pref.user_name} !","Email was sent successfully.")
//                    /*try {
//                        if (i == true) {
//                            Toast.makeText(mContext, "Email was sent successfully ", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(mContext, "Email was not sent successfully ", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    catch (e2: java.lang.Exception) {
//                        e2.printStackTrace()
//                        Toast.makeText(mContext, "Email Error ", Toast.LENGTH_SHORT).show()
//                    }*/
//                }
//            }
//       /*     if (!TextUtils.isEmpty(sendingPath)) {
//                try {
//                    val shareIntent = Intent(Intent.ACTION_SEND)
//                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    shareIntent.setType("vnd.android.cursor.item/email");
//                    shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("saheli.bhattacharjee@indusnet.co.in"))
//                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quotation for $shop_name created on dated ${addQuotEditResult.save_date_time!!.split(" ").get(0)}.")
//                    shareIntent.putExtra(Intent.EXTRA_TEXT,  "Hello Team,  \n Please find attached Quotation No. ${addQuotEditResult.quotation_number} Dated ${addQuotEditResult.save_date_time!!.split(" ").get(0)} for $shop_name \n\n\n Regards \n${Pref.user_name}.")
//
//                    val fileUrl = Uri.parse(sendingPath)
//                    val file = File(fileUrl.path)
//                    val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
//
//                    if (!file.exists() || !file.canRead()) {
//                        Toast.makeText(getContext(), "Attachment Error", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                    shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
//                    startActivity(shareIntent)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
//                }
//            }*/
//
//        }catch (ex: Exception){
//            progress_wheel.stopSpinning()
//            ex.printStackTrace()
//            Toaster.msgShort(mContext, ex.message.toString())
//            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
//        }
//
//
//    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun saveDataAsPdf(addQuotEditResult: ViewDetailsQuotResponse) {
        var document: Document = Document()
        val time = System.currentTimeMillis()
        //val fileName = "QUTO_" +  "_" + time
        var fileName = addQuotEditResult.quotation_number!!.toUpperCase() +  "_" + time
        fileName=fileName.replace("/", "_")
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/eurobondApp/QUTO/"

        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        try{
            progress_wheel.spin()
            var pdfWriter :PdfWriter = PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))
            val event = HeaderFooterPageEvent()
            pdfWriter.setPageEvent(event)

            //PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))
            document.open()

            var font: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD)
            var fontB1: Font = Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD)
            var font1: Font = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL)
            var font1Big: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL)
            var font2Big: Font = Font(Font.FontFamily.HELVETICA, 9f, Font.NORMAL)
            var font1small: Font = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL)
            val grayFront = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.GRAY)


            //image add
            val bm: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.breezelogo)
            val bitmap = Bitmap.createScaledBitmap(bm, 220, 90, true);
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            var img: Image? = null
            val byteArray: ByteArray = stream.toByteArray()
            try {
                img = Image.getInstance(byteArray)
                img.scaleToFit(155f,90f)
                img.scalePercent(70f)
                img.alignment=Image.ALIGN_RIGHT
            } catch (e: BadElementException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            document.add(img)


            //var quotDate = AppUtils.getFormatedDateNew(addQuotEditResult.quotation_date_selection!!.replace("12:00:00 AM",""),"mm-dd-yyyy","dd-mm-yyyy")

            /*val dateLine = Paragraph("DATE: " + addQuotEditResult.quotation_date_selection!! +
                    "                                                              " + addQuotEditResult.quotation_number, font)
            dateLine.alignment = Element.ALIGN_LEFT
            dateLine.spacingAfter = 5f
            document.add(dateLine)*/


            val para = Paragraph()
            val glue = Chunk(VerticalPositionMark())
            val ph1 = Phrase()
            val main = Paragraph()
            ph1.add(Chunk("DATE: " + addQuotEditResult.quotation_date_selection!!, font))
            ph1.add(glue) // Here I add special chunk to the same phrase.

            ph1.add(Chunk(addQuotEditResult.quotation_number + "                         ", font))
            para.add(ph1)
            document.add(para)

            val xxx = Paragraph("", font)
            xxx.spacingAfter = 5f
            document.add(xxx)

            val toLine = Paragraph("To,", font)
            toLine.alignment = Element.ALIGN_LEFT
            toLine.spacingAfter = 2f
            document.add(toLine)

            val cusName = Paragraph(addQuotEditResult.shop_name, font)
            cusName.alignment = Element.ALIGN_LEFT
            cusName.spacingAfter = 2f
            document.add(cusName)


            //// addr test begin
            var finalStr =""
            try{
                var str = addQuotEditResult.shop_addr.toString().toCharArray()
                //var str = "1602, Marathon Icon,Opp. Peninsula Corporate Park, Off Ganpatrao Kadam Marg,Lower Parel (W),".toCharArray()
                //var str = "Chhatrapati Shivaji Terminus Main Post Office, Borabazar Precinct, Ballard Estate, Fort, Mumbai, Maharashtra 400001, India".toCharArray()
                finalStr =""
                var isNw=false
                var comCnt=0
                for(i in 0..str.size-1){
                    if(str[i].toString().equals(",")){
                        comCnt++
                        finalStr=finalStr+","
                        if(comCnt%2==0){
                            finalStr=finalStr+"\n"
                            isNw=true
                        }
                    }else {
                        if(isNw && str[i].toString().equals(" ")){
                            isNw=false
                        }else{
                            finalStr=finalStr+str[i].toString()
                        }
                    }
                }
            }catch (ex:Exception){
                finalStr=""
            }


            //// addr test end

//            val cusAddress = Paragraph(addQuotEditResult.shop_addr, font)
            val cusAddress = Paragraph(finalStr, font)
            cusAddress.alignment = Element.ALIGN_LEFT
            cusAddress.spacingAfter = 5f
            document.add(cusAddress)

//            val cusemail = Paragraph("Email : " + addQuotEditResult.shop_email, font)
//            cusemail.alignment = Element.ALIGN_LEFT
//            cusemail.spacingAfter = 5f
//            document.add(cusemail)

            val shopPincode = Paragraph("Pincode : "+addQuotEditResult.shop_address_pincode, font)
            shopPincode.alignment = Element.ALIGN_LEFT
            shopPincode.spacingAfter = 5f
            document.add(shopPincode)


            val projectName = Paragraph("Project Name : "+addQuotEditResult.project_name, font)
            projectName.alignment = Element.ALIGN_LEFT
            projectName.spacingAfter = 5f
            document.add(projectName)

            val cusemail = Chunk("Email : " +  addQuotEditResult.shop_email, font)
            //cusemail.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
            document.add(cusemail)


            val xx = Paragraph("", font)
            xx.spacingAfter = 6f
            document.add(xx)

            //val cusowner = Paragraph("Kind Attn. " + addQuotEditResult.shop_owner_name +"  "+ "(Mob.No.  " + addQuotEditResult.shop_phone_no +  ")", font)
            val cusowner = Chunk("Kind Attn. " +  "Mr./Mrs. "+addQuotEditResult.shop_owner_name + "  " + "(Mob.No.  " + addQuotEditResult.shop_phone_no + ")", font)
            cusowner.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
            //cusowner.alignment = Element.ALIGN_LEFT
            //cusowner.spacingAfter = 5f
            document.add(cusowner)



            val x = Paragraph("", font)
            //cusemail.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
            x.spacingAfter = 6f
            document.add(x)

            // Hardcoded for EuroBond
//            val sub = Paragraph("Sub :-Quotation For Eurobond-ALUMINIUM COMPOSITE PANEL", font)
//            val sub = Chunk("Sub :-Quotation For Eurobond-ALUMINIUM COMPOSITE PANEL", font)
            val sub = Chunk("Sub :-Quotation For "+getString(R.string.app_name), font)
            sub.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
            //sub.alignment = Element.ALIGN_LEFT
            //sub.spacingAfter = 10f
            document.add(sub)

            val body = Paragraph("\nSir,\n" +
                    "In reference to the discusssion held with you regarding the said subject,we are please to quote our most " +
                    "preferred rates & others terms and condition for the same as follows.\n", grayFront)
            body.alignment = Element.ALIGN_LEFT
            body.spacingAfter = 10f
            document.add(body)

            // table header
//            val widths = floatArrayOf(0.07f, 0.40f,0.13f, 0.2f, 0.2f)
            val widths = floatArrayOf(0.07f, 0.44f,0.13f, 0.18f, 0.18f)

            var tableHeader: PdfPTable = PdfPTable(widths)
            tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
            tableHeader.setWidthPercentage(100f)

            val cell1 = PdfPCell(Phrase("Sr.No",font1Big))
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cell1);

            val cell2 = PdfPCell(Phrase("Description",font1Big))
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cell2);

            val cell2_1 = PdfPCell(Phrase("Color Code/Series",font1Big))
            cell2_1.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cell2_1);

            val cell3 = PdfPCell(Phrase("Rate/Sq.Mtr (INR)",font1Big))
            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cell3);

            val cell4 = PdfPCell(Phrase("Rate/Sq.Ft (INR)",font1Big))
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            tableHeader.addCell(cell4);

            //tableHeader.addCell(PdfPCell(Phrase("Description", font1)))
            //tableHeader.addCell(PdfPCell(Phrase("Rate/Sq.Mtr (INR)", font1)))
            //tableHeader.addCell(PdfPCell(Phrase("Rate/Sq.Ft (INR)", font1)))

            //tableHeader.addCell("Sr. No.")
            //tableHeader.addCell("Description.")
            //tableHeader.addCell("Rate/Sq.Mtr (INR)")
            //tableHeader.addCell("Rate/Sq.Ft (INR)")
            document.add(tableHeader)

            //table body
            var srNo:String=""
            var desc:String=""
            var catagory:String=""
            var colorCode:String=""
            var rateSqFt:String=""
            var rateSqMtr:String=""
            var obj=addQuotEditResult.quotation_product_details_list
            for (i in 0..obj!!.size-1) {
                srNo= (i+1).toString()
                desc=obj!!.get(i).product_name.toString() //+ "\n\n"+ "Color Code : "+obj.get(i).color_name
                colorCode = obj.get(i).color_name.toString()
//                colorCode = "solid and metalic"
                rateSqFt="INR - "+obj!!.get(i).rate_sqft.toString()
                rateSqMtr="INR - "+obj!!.get(i).rate_sqmtr.toString()
                try{
                    catagory = AppDatabase.getDBInstance()?.productListDao()?.getSingleProduct(obj!!.get(i).product_id!!.toInt()!!)!!.category.toString()
                }catch (ex:Exception){
                    catagory=""
                }
                desc=desc+"\n"+catagory

                val tableRows = PdfPTable(widths)
                tableRows.defaultCell.horizontalAlignment = Element.ALIGN_CENTER
                tableRows.setWidthPercentage(100f);

                var cellBodySl = PdfPCell(Phrase(srNo,font1small))
                cellBodySl.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableRows.addCell(cellBodySl)

                var cellBodyDesc = PdfPCell(Phrase(desc,font1small))
                cellBodyDesc.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableRows.addCell(cellBodyDesc)

                var cellBodyColor = PdfPCell(Phrase(colorCode,font1small))
                cellBodyColor.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableRows.addCell(cellBodyColor)

                var cellBodySqMtr = PdfPCell(Phrase(rateSqMtr,font1small))
                cellBodySqMtr.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableRows.addCell(cellBodySqMtr)

                var cellBodySqFt = PdfPCell(Phrase(rateSqFt,font1small))
                cellBodySqFt.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableRows.addCell(cellBodySqFt)


                document.add(tableRows)

                document.add(Paragraph())
            }



            val terms = Chunk("\nTerms & Conditions:-", font)
//            terms.alignment = Element.ALIGN_LEFT
//            terms.spacingAfter = 5f
            terms.setUnderline(0.1f, -2f) //0.1 thick, -2 y-location
            document.add(terms)



            val taxs = Paragraph("Taxes                                                  :     " + addQuotEditResult.taxes, font2Big)
            taxs.alignment = Element.ALIGN_LEFT
            taxs.spacingAfter = 2f
            document.add(taxs)


            val freight = Paragraph("Freight                                                 :     " + addQuotEditResult.Freight, font2Big)
            freight.alignment = Element.ALIGN_LEFT
            freight.spacingAfter = 2f
            document.add(freight)


            val delivery = Paragraph("Delivery Time                                      :     " + addQuotEditResult.delivery_time, font2Big)
            delivery.alignment = Element.ALIGN_LEFT
            delivery.spacingAfter = 2f
            document.add(delivery)


            val payment = Paragraph("Payment                                              :     " + addQuotEditResult.payment, font2Big)
            payment.alignment = Element.ALIGN_LEFT
            payment.spacingAfter = 2f
            document.add(payment)

            val validity = Paragraph("Validity                                                 :     " + addQuotEditResult.validity, font2Big)
            validity.alignment = Element.ALIGN_LEFT
            validity.spacingAfter = 2f
            document.add(validity)

            val billing = Paragraph("Billing                                                   :     " + addQuotEditResult.billing, font2Big)
            billing.alignment = Element.ALIGN_LEFT
            billing.spacingAfter = 2f
            document.add(billing)

            val product_tolerance_of_thickness = Paragraph("Product Tolerance of Thickness          :     " + addQuotEditResult.product_tolerance_of_thickness, font2Big)
            product_tolerance_of_thickness.alignment = Element.ALIGN_LEFT
            product_tolerance_of_thickness.spacingAfter = 2f
            document.add(product_tolerance_of_thickness)

            val product_tolerance_of_coating = Paragraph("Tolerance of Coating Thickness          :     " + addQuotEditResult.tolerance_of_coating_thickness, font2Big)
            product_tolerance_of_coating.alignment = Element.ALIGN_LEFT
            product_tolerance_of_coating.spacingAfter = 6f
            document.add(product_tolerance_of_coating)


            val end = Paragraph("Anticipating healthy business relation with your esteemed organization.", grayFront)
            end.alignment = Element.ALIGN_LEFT
            end.spacingAfter = 4f
            document.add(end)

            val thanks = Paragraph("\nThanks & Regards,", fontB1)
            thanks.alignment = Element.ALIGN_LEFT
            thanks.spacingAfter = 4f
            document.add(thanks)

            // Hardcoded for EuroBond
//            val companyName = Paragraph("EURO PANEL PRODUCTS LIMITED", fontB1)
            val companyName = Paragraph(getString(R.string.app_name), fontB1)
            companyName.alignment = Element.ALIGN_LEFT
            companyName.spacingAfter = 2f
            document.add(companyName)

            val salesmanName = Paragraph(addQuotEditResult.salesman_name, fontB1)
            salesmanName.alignment = Element.ALIGN_LEFT
            salesmanName.spacingAfter = 2f
            document.add(salesmanName)

            val salesmanDes = Paragraph(addQuotEditResult.salesman_designation, fontB1)
            salesmanDes.alignment = Element.ALIGN_LEFT
            salesmanDes.spacingAfter = 2f
            document.add(salesmanDes)

            //val salesmanphone = Paragraph(addQuotEditResult.salesman_phone_no, fontB1)
            val salesmanphone = Paragraph(addQuotEditResult.salesman_login_id, fontB1)
            salesmanphone.alignment = Element.ALIGN_LEFT
            salesmanphone.spacingAfter =  2f
            document.add(salesmanphone)

            val salesmanemail = Paragraph("Email : "+addQuotEditResult.salesman_email, fontB1)
            salesmanemail.alignment = Element.ALIGN_LEFT
            salesmanemail.spacingAfter =  2f
            document.add(salesmanemail)

            val xxxx = Paragraph("", font)
            xxxx.spacingAfter = 4f
            document.add(xxxx)

            // Hardcoded for EuroBond
//            val euroHead = Paragraph("\nEURO PANEL PRODUCTS LIMITED", font)
            val euroHead = Paragraph("\n"+getString(R.string.app_name), font)
            euroHead.alignment = Element.ALIGN_LEFT
            //document.add(euroHead)

            //strip_line//bar//ics
            //Hardcoded for EuroBond
            val bm1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ics_image)
//            val bm1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.footer_icon_euro)
//            val bm1: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bar)
            val bitmap1 = Bitmap.createScaledBitmap(bm1, 850, 120, true)
            val stream1 = ByteArrayOutputStream()
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1)
            var img1: Image? = null
            val byteArray1: ByteArray = stream1.toByteArray()
            try {
                img1 = Image.getInstance(byteArray1)
                img1.alignment=Image.ALIGN_LEFT
            } catch (e: BadElementException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
//            document.add(img1)

            val bm2: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.bar)
            val bitmap2 = Bitmap.createScaledBitmap(bm2, 50, 50, true)
            val stream2 = ByteArrayOutputStream()
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream2)
            var img2: Image? = null
            val byteArray2: ByteArray = stream2.toByteArray()
            try {
                img2 = Image.getInstance(byteArray2)
            } catch (e: BadElementException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
//            document.add(img2)


            val companydel = Paragraph("Regd.Off: 702,Aravali Business Centre,Ramadas Sutrale Road,Borivali(West),Mumbai-400 092." +
                    "Factory: Survey No.124/4,Manekpur,Sanjan,Khattalwada,Taluka- Umbergaon,Dist.Valsad,Gujarat - 396120" +
                    "T: +91-22-29686500(30 lines) +91-7666625999 - E: sale@eurobondacp.com + W: www.eurobondacp.com + CIN: U28931MH2013PTC251176" +
                    "", font1)
            companydel.alignment = Element.ALIGN_RIGHT
            companydel.spacingAfter = 10f
            //document.add(img1)
            //document.add(img2)
            //img2!!.alignment=Image.ALIGN_CENTER
            //document.add(companydel)


            val tablee = PdfPTable(1)
            tablee.widthPercentage = 100f
            var cell = PdfPCell()
            var p = Paragraph()
            p.alignment=Element.ALIGN_LEFT
            img1!!.scalePercent(50f)
            p.add(Chunk(img1, 0f, 0f, true))
            //p.add(Chunk(img2, 0f, 0f, true))
            //p.add(companydel)
            cell.addElement(p)
            cell.backgroundColor= BaseColor(0, 0, 0, 0)
            cell.borderColor=BaseColor(0, 0, 0, 0)

            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT)
            tablee.addCell(cell)
            //document.add(tablee)


            val bm3: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.strip_line)
            val bitmap3 = Bitmap.createScaledBitmap(bm3, 520, 20, true)
            val stream3 = ByteArrayOutputStream()
            bitmap3.compress(Bitmap.CompressFormat.PNG, 100, stream3)
            var img3: Image? = null
            val byteArray3: ByteArray = stream3.toByteArray()
            try {
                img3 = Image.getInstance(byteArray3)
            } catch (e: BadElementException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //document.add(img3)


            document.close()


            var sendingPath=path+fileName+".pdf"
            /*if (!TextUtils.isEmpty(sendingPath)) {
               try {
                   val shareIntent = Intent(Intent.ACTION_SEND)
                   shareIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
                   shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("saheli.bhattacharjee@indusnet.co.in","suman.bachar@indusnet.co.in"))
//                    shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("sales1@eurobondacp.com","sales@eurobondacp.com"))
                   shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quotation for $shop_name created on dated ${addQuotEditResult.save_date_time}.")
                   shareIntent.putExtra(Intent.EXTRA_TEXT, "Hello Team,  \n Please find attached Quotation No. ${addQuotEditResult.quotation_number} Dated ${addQuotEditResult.save_date_time} " +
                           " for $shop_name \n\n\n" +
                           "Regards \n${Pref.user_name}. ")
                   shareIntent.type = "message/rfc822"
                   val fileUrl = Uri.parse(sendingPath)
                   val file = File(fileUrl.path)
                   val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
//                    shareIntent.type = "image/png"
                   shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                   startActivity(Intent.createChooser(shareIntent, "Share pdf using"))
               } catch (e: Exception) {
                   e.printStackTrace()
                   (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
               }
           }*/

            /*if (!TextUtils.isEmpty(sendingPath)) {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    val fileUrl = Uri.parse(sendingPath)
                    val file = File(fileUrl.path)
                    val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
                    shareIntent.type = "image/png"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                    startActivity(Intent.createChooser(shareIntent, "Share pdf using"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                }
            }*/

            // Hardcoded for EuroBond
//            val m = Mail("eurobondacp02@gmail.com", "nuqfrpmdjyckkukl")
            val m = Mail("saheli.bhattacharjee@indusnet.co.in", "@Intsaheli22")
            val toArr = arrayOf("saheli.bhattacharjee@indusnet.co.in","suman.bachar@indusnet.co.in","suman.roy@indusnet.co.in")
//            val toArr = arrayOf("sales1@eurobondacp.com", "sales@eurobondacp.com")
            m.setTo(toArr)
            m.setFrom("TEAM");
            m.setSubject("Quotation for $shop_name created on dated ${addQuotEditResult.save_date_time!!.split(" ").get(0)}.")
            m.setBody("Hello Team,  \n Please find attached Quotation No. ${addQuotEditResult.quotation_number} Dated ${addQuotEditResult.save_date_time!!.split(" ").get(0)} for $shop_name \n\n\n Regards \n${Pref.user_name}.")
            doAsync {
                val fileUrl = Uri.parse(sendingPath)
                val i = m.send(fileUrl.path)
                uiThread {
                    progress_wheel.stopSpinning()
                    openDialogPopup("Hi ${Pref.user_name} !","Email was sent successfully.")
                    /*try {
                        if (i == true) {
                            Toast.makeText(mContext, "Email was sent successfully ", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(mContext, "Email was not sent successfully ", Toast.LENGTH_SHORT).show()
                        }
                    }
                    catch (e2: java.lang.Exception) {
                        e2.printStackTrace()
                        Toast.makeText(mContext, "Email Error ", Toast.LENGTH_SHORT).show()
                    }*/
                }
            }
            /*     if (!TextUtils.isEmpty(sendingPath)) {
                     try {
                         val shareIntent = Intent(Intent.ACTION_SEND)
                         shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                         shareIntent.setType("vnd.android.cursor.item/email");
                         shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("saheli.bhattacharjee@indusnet.co.in"))
                         shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Quotation for $shop_name created on dated ${addQuotEditResult.save_date_time!!.split(" ").get(0)}.")
                         shareIntent.putExtra(Intent.EXTRA_TEXT,  "Hello Team,  \n Please find attached Quotation No. ${addQuotEditResult.quotation_number} Dated ${addQuotEditResult.save_date_time!!.split(" ").get(0)} for $shop_name \n\n\n Regards \n${Pref.user_name}.")

                         val fileUrl = Uri.parse(sendingPath)
                         val file = File(fileUrl.path)
                         val uri: Uri = FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)

                         if (!file.exists() || !file.canRead()) {
                             Toast.makeText(getContext(), "Attachment Error", Toast.LENGTH_SHORT).show();
                             return;
                         }
                         shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                         shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                         shareIntent.putExtra(Intent.EXTRA_STREAM,uri)
                         startActivity(shareIntent)
                     } catch (e: Exception) {
                         e.printStackTrace()
                         (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                     }
                 }*/

        }catch (ex: Exception){
            progress_wheel.stopSpinning()
            ex.printStackTrace()
            Toaster.msgShort(mContext, ex.message.toString())
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }


    }

    fun openDialogPopup(header:String,text:String){
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_ok_imei)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_header) as AppCustomTextView
        val dialogBody = simpleDialog.findViewById(R.id.dialog_yes_body) as AppCustomTextView
        dialogHeader.text = header
        dialogBody.text = text
        val dialogYes = simpleDialog.findViewById(R.id.tv_dialog_yes) as AppCustomTextView
        dialogYes.setOnClickListener({ view ->
            simpleDialog.cancel()
        })
        simpleDialog.show()
    }

    fun updateView(){
        quotListCall(shop_id)
    }
}