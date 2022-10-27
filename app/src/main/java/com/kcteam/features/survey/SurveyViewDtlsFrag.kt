package com.kcteam.features.survey

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
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.AddShopDBModelEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.app.widgets.MovableFloatingActionButton
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.damageProduct.model.Shop_wise_breakage_list
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.survey.api.SurveyDataProvider
import com.kcteam.widgets.AppCustomTextView
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.pnikosis.materialishprogress.ProgressWheel
import com.squareup.picasso.Cache
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.net.URL
import kotlin.math.sign

class SurveyViewDtlsFrag: BaseFragment() {
    private lateinit var mContext: Context
    lateinit var progress_wheel: ProgressWheel

    lateinit var rvDtls:RecyclerView
    lateinit var adapterDtls:AdapterSurveyDtlsView
    lateinit var headingTV:TextView
    lateinit var shareFB: MovableFloatingActionButton

    var qaList: ArrayList<question_ans_list> = ArrayList()
    var qaMainObj: survey_list = survey_list()
    var imageLinkList:ArrayList<String> = ArrayList()

    var sharePdfPrepCount:Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    companion object {
        var mAddShopDataObj: AddShopDBModelEntity? = null
        var shop_id:String = ""
        var survey_id:String = ""
        fun getInstance(objects: Any): SurveyViewDtlsFrag {
            val fragment = SurveyViewDtlsFrag()
            if (!TextUtils.isEmpty(objects.toString())) {
                shop_id=objects.toString().split("~").get(0)
                survey_id=objects.toString().split("~").get(1)
                mAddShopDataObj = AppDatabase.getDBInstance()!!.addShopEntryDao().getShopByIdN(shop_id)
            }
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_survey_view_dtls, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View?) {
        progress_wheel=view?.findViewById(R.id.progress_wheel) as ProgressWheel
        headingTV=view?.findViewById(R.id.tv_frag_survey_view_dtls_head)
        progress_wheel.stopSpinning()
        rvDtls=view?.findViewById(R.id.rv_frag_survey_view_dtls)
        shareFB=view?.findViewById(R.id.fb_frag_survey_view_share)

        shareFB.setCustomClickListener{
            imageLinkList = qaList.filter { it.image_link!!.startsWith("http") }.map { it.image_link } as ArrayList<String>
            sharePdfPrepCount=-1
            sharePdfPrep()
        }

        headingTV.text="Survey ($survey_id) Q&A for ${mAddShopDataObj!!.shopName}"

        getQuestionData()
    }

    fun getQuestionData(){
        progress_wheel.spin()
        try{
            val repository = SurveyDataProvider.provideSurveyQ()
            BaseActivity.compositeDisposable.add(
                repository.provideSurveyViewApi(Pref.session_token!!, Pref.user_id!!, SurveyViewFrag.shop_id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        progress_wheel.stopSpinning()
                        var response = result as viewsurveyModel
                        if (response.status == NetworkConstant.SUCCESS) {
                            doAsync {
                                if(response.survey_list!=null){
                                    for(i in 0..response.survey_list!!.size-1){
                                        if(response.survey_list!!.get(i).survey_id!!.equals(survey_id)){
                                            qaMainObj = response.survey_list!!.get(i)
                                            qaList=response.survey_list!!.get(i).question_ans_list!!
                                            break
                                        }
                                    }
                                }
                                uiThread {
                                    dataSetup()
                                }
                            }
                        }else {
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_date_found))
                        }

                    }, { error ->
                        progress_wheel.stopSpinning()
                        error.printStackTrace()
                        (mContext as DashboardActivity).showSnackMessage("ERROR")
                    })
            )
        }catch (ex:Exception){
            progress_wheel.stopSpinning()
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage("ERROR")
        }
    }

   fun dataSetup(){
       adapterDtls= AdapterSurveyDtlsView(mContext,qaList,object : AdapterSurveyDtlsView.OnClickListener{
           override fun viewPicOnLick(obj: question_ans_list) {
               progress_wheel.spin()
               val simpleDialogg = Dialog(mContext)
               simpleDialogg.setCancelable(true)
               simpleDialogg.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
               simpleDialogg.setContentView(R.layout.view_img)


               val faceImg = simpleDialogg.findViewById(R.id.iv_face_img) as ImageView
               faceImg.setImageDrawable(null)
               faceImg.setBackgroundDrawable(null)
               faceImg.invalidate();
               faceImg.setImageBitmap(null);
               val faceCanel = simpleDialogg.findViewById(R.id.iv_face_reg_cancel) as ImageView


               val picasso = Picasso.Builder(mContext)
                   .memoryCache(Cache.NONE)
                   .indicatorsEnabled(false)
                   .loggingEnabled(true) //add other settings as needed
                   .build()
               picasso.load(Uri.parse(obj.image_link))
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

       })
       rvDtls.adapter=adapterDtls
   }

    fun sharePdfPrep(){
        sharePdfPrepCount++
        if(sharePdfPrepCount < imageLinkList.size) {
            GetImageFromUrl().execute(imageLinkList.get(sharePdfPrepCount))
        }
        if(sharePdfPrepCount==imageLinkList.size){
            sharePdf()
        }
    }

    var bitmapDamagePic:ArrayList<Bitmap> = ArrayList()
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
            println("reg_face - registerFace called"+ AppUtils.getCurrentDateTime());
            bitmapDamagePic.add(result!!)
            sharePdfPrep()
        }

    }

    fun sharePdf(){
        var document: Document = Document()
        var fileName = "FTS" + "_" + survey_id
        fileName = fileName.replace("/", "_")

        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "/kcteamApp/SURVEYDETALIS/"

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

            val h = Paragraph("Survey Details", fontBoldUHead)
            h.alignment = Element.ALIGN_CENTER

            val pHead = Paragraph()
            pHead.add(Chunk(img, 0f, -30f))
            pHead.add(h)
            document.add(pHead)

            val x = Paragraph("", font)
            x.spacingAfter = 20f
            document.add(x)






            val Parties = Paragraph("Customer Name       :      " + mAddShopDataObj?.shopName, font1)
            Parties.alignment = Element.ALIGN_LEFT
            Parties.spacingAfter = 2f
            document.add(Parties)

            var typeName = AppDatabase.getDBInstance()?.shopTypeDao()?.getSingleType(mAddShopDataObj?.type!!.toString())!!.shoptype_name
            val PartiesType = Paragraph("Customer Type        :      " + typeName, font1)
            PartiesType.alignment = Element.ALIGN_LEFT
            PartiesType.spacingAfter = 2f
            document.add(PartiesType)

            val address = Paragraph("Address                   :      " + mAddShopDataObj?.address, font1)
            address.alignment = Element.ALIGN_LEFT
            address.spacingAfter = 2f
            document.add(address)

            val Contact = Paragraph("Phone No.               :      " + mAddShopDataObj?.ownerContactNumber, font1)
            Contact.alignment = Element.ALIGN_LEFT
            Contact.spacingAfter = 5f
            document.add(Contact)

            val Email = Paragraph("Email                       :      " + mAddShopDataObj?.ownerEmailId, font1)
            Email.alignment = Element.ALIGN_LEFT
            Email.spacingAfter = 5f
            document.add(Email)

            val SaveDate = Paragraph("Date                        :      " + AppUtils.getFormatedDateNew(qaMainObj.saved_date_time!!.split("T").get(0),"yyyy-mm-dd","dd-mm-yyyy"), font1)
            SaveDate.alignment = Element.ALIGN_LEFT
            SaveDate.spacingAfter = 5f
            document.add(SaveDate)

            val SaveTime = Paragraph("Time                        :      " + qaMainObj.saved_date_time!!.split("T").get(1), font1)
            SaveTime.alignment = Element.ALIGN_LEFT
            SaveTime.spacingAfter = 5f
            document.add(SaveTime)

            val SType = Paragraph("Survey Type            :      " + qaMainObj.group_name, font1)
            SType.alignment = Element.ALIGN_LEFT
            SType.spacingAfter = 5f
            document.add(SType)

            val EnterBy = Paragraph("Salesman                :      " + Pref.user_name, font1)
            EnterBy.alignment = Element.ALIGN_LEFT
            EnterBy.spacingAfter = 15f
            document.add(EnterBy)

            for(i in 0..qaList.size-1){
                var question = qaList.get(i).question_desc
                var ans = qaList.get(i).answer
                var imgLink = qaList.get(i).image_link

                val questionPara = Paragraph("${i+1} $question", font1)
                questionPara.alignment = Element.ALIGN_LEFT
                questionPara.spacingAfter = 2f
                document.add(questionPara)

                val answerPara = Paragraph("Ans : $ans", font1)
                answerPara.alignment = Element.ALIGN_LEFT
                answerPara.spacingAfter = 2f
                document.add(answerPara)

                if(!imgLink.equals("")){
                    val bitmap1 = Bitmap.createScaledBitmap(bitmapDamagePic.get(0), 130, 130, true);
                        val stream1 = ByteArrayOutputStream()
                        bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1)
                        var img1: Image? = null
                        val byteArray1: ByteArray = stream1.toByteArray()
                        try {
                            img1 = Image.getInstance(byteArray1)
                            img1.scaleToFit(100f, 100f)
                            img1.scalePercent(100f)
                            img1.alignment = Image.ALIGN_CENTER
                        } catch (e: BadElementException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        document.add(img1)

                    bitmapDamagePic.removeAt(0)
                }
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
        } catch (ex: Exception) {
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
        }
    }
}