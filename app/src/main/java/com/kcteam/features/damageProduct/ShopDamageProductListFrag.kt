package com.kcteam.features.damageProduct

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.kcteam.CustomStatic
import com.kcteam.NumberToWords
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.domain.BillingEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.damageProduct.api.GetDamageProductRegProvider
import com.kcteam.features.damageProduct.model.DamageProductResponseModel
import com.kcteam.features.damageProduct.model.Shop_wise_breakage_list
import com.kcteam.features.damageProduct.model.delBreakageReq
import com.kcteam.features.damageProduct.model.viewAllBreakageReq
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.member.model.TeamShopListDataModel
import com.kcteam.features.reimbursement.presentation.FullImageDialog
import com.kcteam.features.viewAllOrder.orderNew.NewOrderScrOrderDetailsFragment
import com.kcteam.widgets.AppCustomTextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.pnikosis.materialishprogress.ProgressWheel
import com.squareup.picasso.Cache
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_quot.*
import java.io.*
import java.net.URL
import java.util.*

class ShopDamageProductListFrag: BaseFragment(), DatePickerDialog.OnDateSetListener, View.OnClickListener {
    private lateinit var mContext: Context

    private lateinit var myshop_name_TV: AppCustomTextView
    private lateinit var myshop_addr_TV: AppCustomTextView
    private lateinit var myshop_contact_TV: AppCustomTextView

    private lateinit var add_new_order_tv: FloatingActionButton
    private lateinit var radioList: ArrayList<RadioButton>
    private lateinit var date_range: AppCompatRadioButton

    private lateinit var tv_startDate: AppCustomTextView
    private lateinit var tv_endDate: AppCustomTextView
    private lateinit var iv_show:ImageView

    private lateinit var shop_IV: ImageView
    var viewAllBreakageAdapter: ShopDamageProductAdapter?=null
    private lateinit var rv_damage_product_list: RecyclerView
    private lateinit var no_damage_tv: AppCustomTextView
    private var isChkChanged: Boolean = false
    private val mAutoHighlight: Boolean = false
    private var fromDate:String = ""
    private var toDate:String = ""
    private lateinit var progress_wheel: ProgressWheel
    var viewList:ArrayList<Shop_wise_breakage_list> = ArrayList()
    lateinit var simpleDialog: Dialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var mAddShopDataObj: AddShopDBModelEntity? = AddShopDBModelEntity()
        var shop_id:String = ""
        var userID:String = ""
        var isFromTeam = false
        fun getInstance(objects: Any): ShopDamageProductListFrag {
            val fragment = ShopDamageProductListFrag()
            if (!TextUtils.isEmpty(objects.toString())) {
                if(objects is TeamShopListDataModel){
                    shop_id = objects.shop_id
                    userID = objects.user_id
                    mAddShopDataObj!!.shop_id =objects.shop_id
                    mAddShopDataObj!!.shopName =objects.shop_name
                    mAddShopDataObj!!.address =objects.shop_address
                    mAddShopDataObj!!.ownerContactNumber =objects.shop_contact
                    isFromTeam = true
                }else{
                    shop_id=objects.toString().split("~").get(0)
                    userID=objects.toString().split("~").get(1)
                    mAddShopDataObj = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)
                    isFromTeam = false
                }

            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_shop_damage_product, container, false)
        initView(view)
        return view
    }

    @SuppressLint("UseRequireInsteadOfGet", "RestrictedApi")
    private fun initView(view: View?) {
        shop_IV =  view!!.findViewById(R.id.shop_IV)
        myshop_name_TV = view!!.findViewById(R.id.myshop_name_TV)
        myshop_addr_TV = view!!.findViewById(R.id.myshop_address_TV)
        myshop_contact_TV = view!!.findViewById(R.id.tv_contact_number)
        rv_damage_product_list= view!!.findViewById(R.id.rv_damage_product_list)
        no_damage_tv= view!!.findViewById(R.id.no_damage_tv)
        add_new_order_tv= view!!.findViewById(R.id.add_new_order_tv)
        radioList = ArrayList()
        date_range=view.findViewById(R.id.frag_date_range)
        tv_startDate = view.findViewById(R.id.frag_shop_start_date)
        tv_endDate = view.findViewById(R.id.frag_shop_end_date)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        iv_show = view.findViewById(R.id.frag_lead_breakage_show)
        progress_wheel.stopSpinning()
        add_new_order_tv.setOnClickListener(this)
        date_range.setOnClickListener(this)
        iv_show.setOnClickListener(this)
        radioList.add(date_range)

       //if(CustomStatic.IsBreakageViewFromTeam) {
       if(isFromTeam) {
           add_new_order_tv.visibility = View.GONE
       }
        else{
           add_new_order_tv.visibility = View.VISIBLE
       }

        if( mAddShopDataObj!=null){
            myshop_name_TV.text= mAddShopDataObj?.shopName
            myshop_addr_TV.text= mAddShopDataObj?.address
            myshop_contact_TV.text="Owner Contact Number: " + mAddShopDataObj?.ownerContactNumber.toString()

            val drawable = TextDrawable.builder()
                .buildRoundRect(mAddShopDataObj?.shopName!!.toUpperCase().take(1), ColorGenerator.MATERIAL.randomColor, 120)

            shop_IV.setImageDrawable(drawable)
        }


        tv_startDate.setText("From :  "+AppUtils.changeLocalDateFormatToAtte(AppUtils.getBillFormattedDate(myCalendar.time)))
        tv_endDate.setText("To      :  "+AppUtils.changeLocalDateFormatToAtte(AppUtils.getBillFormattedDate(myCalendar.time)))

        fromDate =AppUtils.getCurrentDateyymmdd()
        toDate =AppUtils.getCurrentDateyymmdd()

        if(AppUtils.isOnline(mContext)){
            breakageListCall()
        }
        else{
            Toaster.msgShort(mContext, "No Internet connection")
        }
    }

    private fun breakageListCall() {
        var obj= viewAllBreakageReq()
        obj.user_id= userID
        obj.from_date = fromDate
        obj.to_date = toDate
        obj.shop_id = shop_id

        try{
            progress_wheel.spin()
            val repository = GetDamageProductRegProvider.provideSaveButton()
            BaseActivity.compositeDisposable.add(
                repository.viewBreakage(obj)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val viewResult = result as DamageProductResponseModel
                        progress_wheel.stopSpinning()
                        if (viewResult!!.status == NetworkConstant.SUCCESS) {
                            if (viewResult!!.breakage_list!!.size > 0) {
                                rv_damage_product_list.visibility = View.VISIBLE
                                no_damage_tv.visibility = View.GONE
                                viewList.clear()
                                viewList.addAll(viewResult!!.breakage_list!!)
                                adapterSetUp()
                            }

                        } else {
                            rv_damage_product_list.visibility = View.GONE
                            no_damage_tv.visibility = View.VISIBLE
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
        }
               catch (ex: Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
            progress_wheel.stopSpinning()
        }

    }

    private fun adapterSetUp() {
        viewAllBreakageAdapter = ShopDamageProductAdapter(mContext, viewList, object : ShopDamageProductAdapter.OnClickListener {
            override fun onView(adapterPosition: Int, QuotId: String) {
               //FullImageDialog.getInstance(viewList.get(adapterPosition).image_link!!).show((mContext as DashboardActivity).supportFragmentManager, "")


                progress_wheel.spin()
                val simpleDialogg = Dialog(mContext)
                simpleDialogg.setCancelable(true)
                simpleDialogg.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialogg.setContentView(R.layout.view_face_img)


                val faceImg = simpleDialogg.findViewById(R.id.iv_face_img) as ImageView
                faceImg.setImageDrawable(null)
                faceImg.setBackgroundDrawable(null)
                faceImg.invalidate();
                faceImg.setImageBitmap(null);
                val faceName = simpleDialogg.findViewById(R.id.face_name) as AppCustomTextView
                val faceCanel = simpleDialogg.findViewById(R.id.iv_face_reg_cancel) as ImageView
                //faceName.text = name
                faceName.visibility=View.GONE

                val picasso = Picasso.Builder(mContext)
                    .memoryCache(Cache.NONE)
                    .indicatorsEnabled(false)
                    .loggingEnabled(true)
                    .build()

                picasso.load(Uri.parse(viewList.get(adapterPosition).image_link!!))
                    .centerCrop()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(500, 500)
                    .into(faceImg)

                progress_wheel.stopSpinning()

                simpleDialogg.show()

                faceCanel.setOnClickListener({ view ->
                    simpleDialogg.dismiss()
                })

                simpleDialogg.setOnCancelListener({ view ->
                    simpleDialogg.dismiss()

                })
                simpleDialogg.setOnDismissListener({ view ->
                    simpleDialogg.dismiss()

                })

            }

            override fun onShare(obj: Shop_wise_breakage_list) {
                //saveDataAsPdf(obj)
                //GetImageFromUrl().execute("http://3.7.30.86:82/CommonFolder/FaceImageDetection/EMS0000070.jpg")
                objForPDF = obj
                GetImageFromUrl().execute(obj.image_link)
            }


            override fun onDelete(adapterPosition: Int, BreakageId: String) {
                simpleDialog = Dialog(mContext)
                simpleDialog.setCancelable(false)
                simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                simpleDialog.setContentView(R.layout.dialog_yes_no)
                val dialogHeader = simpleDialog.findViewById(R.id.dialog_yes_no_headerTV) as AppCustomTextView
                val dialogBody = simpleDialog.findViewById(R.id.dialog_cancel_order_header_TV) as AppCustomTextView
                val btn_no = simpleDialog.findViewById(R.id.tv_dialog_yes_no_no) as AppCustomTextView
                val btn_yes = simpleDialog.findViewById(R.id.tv_dialog_yes_no_yes) as AppCustomTextView

                dialogHeader.text = AppUtils.hiFirstNameText() + "!"
                dialogBody.text = "Do you want to delete this Breakage Number "+ BreakageId+"?"

                btn_yes.setOnClickListener({ view ->
                    deleteBreakage(BreakageId)
                })
                btn_no.setOnClickListener({ view ->
                    simpleDialog.cancel()
                })
                simpleDialog.show()

            }

        })
        rv_damage_product_list.adapter=viewAllBreakageAdapter
    }


   lateinit var bitmapDamagePic:Bitmap
   lateinit var objForPDF: Shop_wise_breakage_list
    inner class GetImageFromUrl : AsyncTask<String?, Void?, Bitmap?>() {
        fun GetImageFromUrl() {
            //this.imageView = img;
        }
        override fun doInBackground(vararg url: String?): Bitmap {
            var bitmappppx: Bitmap? = null
            val stringUrl = url[0]
            bitmappppx = null
            val inputStream: InputStream
            try {
                inputStream = URL(stringUrl).openStream()
                bitmappppx = BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmappppx!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)
            println("reg_face - registerFace called"+AppUtils.getCurrentDateTime());
            bitmapDamagePic = result!!
            saveDataAsPdf(objForPDF)
        }

    }

    private fun saveDataAsPdf(obj: Shop_wise_breakage_list) {
        var document: Document = Document()
        var fileName = "FTS" + "_" + obj.breakage_number
        fileName = fileName.replace("/", "_")

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/kcteamApp/BREAKAGEDETALIS/"

        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        try {
            PdfWriter.getInstance(document, FileOutputStream(path + fileName + ".pdf"))
            document.open()
            var font: Font = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD)
            var fontBoldU: Font = Font(Font.FontFamily.HELVETICA, 12f, Font.UNDERLINE or Font.BOLD)
            var fontBoldUHead: Font = Font(Font.FontFamily.HELVETICA, 16f, Font.UNDERLINE or Font.BOLD)
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
            val sp = Paragraph("", font)
            sp.spacingAfter = 50f
            document.add(sp)

            val h = Paragraph("Breakage Tracking ", fontBoldUHead)
            h.alignment = Element.ALIGN_CENTER

            val pHead = Paragraph()
            pHead.add(Chunk(img, 0f, -30f))
            pHead.add(h)
            document.add(pHead)

            val x = Paragraph("", font)
            x.spacingAfter = 20f
            document.add(x)

//                val order = AppDatabase.getDBInstance()!!.orderDetailsListDao().getSingleOrder(obj.order_id)

            val widthsOrder = floatArrayOf(0.50f, 0.50f)

            var tableHeaderOrder: PdfPTable = PdfPTable(widthsOrder)
            tableHeaderOrder.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER)
            tableHeaderOrder.setWidthPercentage(100f)

            val cell11 = PdfPCell(Phrase("Doc No       :     " + obj.breakage_number + "\n\n" + "Doc Date    :     " + AppUtils.convertToCommonFormat(obj.date_time!!.split("T").get(0)), font))
            cell11.setHorizontalAlignment(Element.ALIGN_LEFT)
            cell11.borderColor = BaseColor.GRAY
            tableHeaderOrder.addCell(cell11)


            var userN = if(isFromTeam) { CustomStatic.BreakageViewFromTeam_Name} else{Pref.user_name }

            val cell222 = PdfPCell(Phrase("Entered By   :     " + userN + "\n\n" + "Entered On  :     " + obj.date_time!!.split("T").get(1), font))
            cell222.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell222.borderColor = BaseColor.GRAY
            cell222.paddingBottom = 10f
            tableHeaderOrder.addCell(cell222)
            document.add(tableHeaderOrder)

//
//            document.add(tableRows)
            document.add(Paragraph())
            val xz = Paragraph("", font)
            xz.spacingAfter = 10f
            document.add(xz)

            val HeadingPartyDetls = Paragraph("Details of Party ", fontBoldU)
            //HeadingPartyDetls.indentationLeft = 82f
            HeadingPartyDetls.alignment = Element.ALIGN_LEFT
            HeadingPartyDetls.spacingAfter = 4f
            document.add(HeadingPartyDetls)


            val Parties = Paragraph("Name                    :      " + mAddShopDataObj?.shopName, font1)
            Parties.alignment = Element.ALIGN_LEFT
            Parties.spacingAfter = 2f
            document.add(Parties)

            val address = Paragraph("Address                :      " + mAddShopDataObj?.address, font1)
            address.alignment = Element.ALIGN_LEFT
            address.spacingAfter = 2f
            document.add(address)


            val Contact = Paragraph("Contact No.          :      " + mAddShopDataObj?.ownerContactNumber, font1)
            Contact.alignment = Element.ALIGN_LEFT
            Contact.spacingAfter = 5f
            document.add(Contact)

            val HeadingDetls = Paragraph("Details of Breakage ", fontBoldU)
            //HeadingDetls.indentationLeft = 82f
            HeadingPartyDetls.alignment = Element.ALIGN_LEFT
            HeadingDetls.spacingAfter = 2f
            document.add(HeadingDetls)

            val productName = Paragraph("Product Name                :      " + obj?.product_name, font1)
            productName.alignment = Element.ALIGN_LEFT
            productName.spacingAfter = 2f
            document.add(productName)

            val breakageDes = Paragraph("Breakage Descrioption  :      " + obj?.description_of_breakage, font1)
            breakageDes.alignment = Element.ALIGN_LEFT
            breakageDes.spacingAfter = 2f
            document.add(breakageDes)


            val customerFeedback = Paragraph("Customer Feedback      :      " + obj?.customer_feedback, font1)
            customerFeedback.alignment = Element.ALIGN_LEFT
            customerFeedback.spacingAfter = 2f
            document.add(customerFeedback)


            val customerRemarks = Paragraph("Remarks                        :      " + obj?.remarks, font1)
            customerRemarks.alignment = Element.ALIGN_LEFT
            customerFeedback.spacingAfter = 4f
            document.add(customerRemarks)




//            val imageFileHead = Paragraph("Image File",font1)
//            imageFileHead.alignment = Element.ALIGN_LEFT
//            imageFileHead.spacingAfter = 2f
//            document.add(imageFileHead)

            //image add
            //val bm1: Bitmap = BitmapFactory.decodeResource(resources, obj?.image_link!!.toInt())
            val bitmap1 = Bitmap.createScaledBitmap(bitmapDamagePic, 250, 250, true);
            val stream1 = ByteArrayOutputStream()
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1)
            var img1: Image? = null
            val byteArray1: ByteArray = stream1.toByteArray()
            try {
                img1 = Image.getInstance(byteArray1)
                img1.scaleToFit(110f, 110f)
                img1.scalePercent(140f)
                img1.alignment = Image.ALIGN_CENTER
            } catch (e: BadElementException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            document.add(img1)


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
        } catch (ex: Exception) {
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }
    }


    private fun deleteBreakage(BreakageId: String) {
        var obj= delBreakageReq()
        obj.user_id= Pref.user_id
        obj.session_token=Pref.session_token
        obj.breakage_number= BreakageId
        try{
            progress_wheel.spin()
            val repository = GetDamageProductRegProvider.provideDel()
            BaseActivity.compositeDisposable.add(
                repository.delBreakage(obj)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val delResult = result as BaseResponse
                        progress_wheel.stopSpinning()
                        if (delResult!!.status == NetworkConstant.SUCCESS) {
                            (mContext as DashboardActivity).showSnackMessage("Deleted Successfully.")
                            simpleDialog.cancel()
                            Handler().postDelayed(Runnable {
                                updatePage()
                            }, 1000)
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

    private var myCalendar = Calendar.getInstance(Locale.ENGLISH)

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.add_new_order_tv ->{
                (mContext as DashboardActivity).loadFragment(FragType.ShopDamageProductSubmitFrag, true, shop_id)
            }

            R.id.frag_date_range -> {
                if (!isChkChanged) {
                    date_range.isChecked = true
                    var dpd = DatePickerDialog.newInstance(this,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)
                    )
                    dpd.isAutoHighlight = mAutoHighlight
                    dpd.maxDate = Calendar.getInstance(Locale.ENGLISH)
                    dpd.show((context as Activity).fragmentManager, "Datepickerdialog")

                } else {
                    isChkChanged = false
                }
            }

            R.id.frag_lead_breakage_show->{
                breakageListCall()
            }
        }
    }

    fun updatePage(){
         breakageListCall()
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        var monthOfYear = monthOfYear
        var monthOfYearEnd = monthOfYearEnd
        val date = "From : " +  String.format("%02d",dayOfMonth) + "-" + FTStorageUtils.formatMm((++monthOfYear).toString()) + "-" + year
        val eDate =  "To      : "+String.format("%02d",dayOfMonthEnd) + "-" + FTStorageUtils.formatMm((++monthOfYearEnd).toString()) + "-" + yearEnd
        tv_startDate.text = date
        tv_endDate.text = eDate
        var day = "" + dayOfMonth
        var dayEnd = "" + dayOfMonthEnd
        if (dayOfMonth < 10)
            day = "0$dayOfMonth"
        if (dayOfMonthEnd < 10)
            dayEnd = "0$dayOfMonthEnd"
        var fronString: String = day + "-" + FTStorageUtils.formatMonth((monthOfYear /*+ 1*/).toString() + "") + "-" + year
        var endString: String = dayEnd + "-" + FTStorageUtils.formatMonth((monthOfYearEnd /*+ 1*/).toString() + "") + "-" + yearEnd
        fromDate = AppUtils.changeLocalDateFormatToAtt(fronString).replace("/","-")
        toDate = AppUtils.changeLocalDateFormatToAtt(endString).replace("/","-")

    }


}