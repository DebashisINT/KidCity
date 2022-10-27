package com.kcteam.features.document.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.CustomStatic
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.NewFileUtils
import com.kcteam.app.Pref
import com.kcteam.app.domain.DocumentListEntity
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.PermissionUtils
import com.kcteam.app.utils.ProcessImageUtils_v1
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.commondialog.presentation.CommonDialog
import com.kcteam.features.commondialog.presentation.CommonDialogClickListener
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.document.api.DocumentRepoProvider
import com.kcteam.features.document.model.AddEditDocumentInputParams
import com.kcteam.features.document.model.DocumentAttachmentModel
import com.kcteam.features.document.model.DocumentListResponseModel
import com.kcteam.features.reimbursement.presentation.FullImageDialog
import com.kcteam.widgets.AppCustomTextView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.elvishew.xlog.XLog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File

class DocumentListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_document_list: RecyclerView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_add_document: FloatingActionButton
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var rl_doc_list_main: RelativeLayout

    private var permissionUtils: PermissionUtils? = null
    private var typeId = ""
    private var filePath = ""
    private var isAdd = false
    private var docId = ""
    private var docList:  ArrayList<DocumentListEntity>?= null

    private var type =""
    private lateinit var file_name: AppCustomTextView
    private var documentName = ""



    companion object {
        fun newInstance(objects: Any): DocumentListFragment {
            val fragment = DocumentListFragment()

            if (!TextUtils.isEmpty(objects.toString())) {
                val bundle = Bundle()
                bundle.putString("typeId", objects as String?)
                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context

        typeId = arguments?.getString("typeId").toString()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_document_list, container, false)

        initView(view)

//        file_name =(AppCustomTextView)findviewById(R.id.file_name)


        if(CustomStatic.IsDocZero){
            var type = "oraganizer"
            tv_add_document.visibility = View.GONE
            file_name.visibility = View.VISIBLE

        }
        else{
            var type = "own"
            tv_add_document.visibility = View.VISIBLE
            file_name.visibility = View.GONE
        }



        val list = AppDatabase.getDBInstance()?.documentListDao()?.getAll()
        if (list != null && list.isNotEmpty()) {
            docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
            if (docList != null && docList!!.isNotEmpty())
                initAdapter()
        }
        else
            getDocumentListApi()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            rv_document_list = findViewById(R.id.rv_document_list)
            progress_wheel = findViewById(R.id.progress_wheel)
            tv_add_document = findViewById(R.id.tv_add_document)
            tv_no_data = findViewById(R.id.tv_no_data)
            rl_doc_list_main = findViewById(R.id.rl_doc_list_main)
            file_name = findViewById(R.id.file_name)


        }



        progress_wheel.stopSpinning()
        rv_document_list.layoutManager = LinearLayoutManager(mContext)

        rl_doc_list_main.setOnClickListener(null)
        tv_add_document.setOnClickListener {

            if (docList != null) {
                if (docList?.size!! < Pref.docAttachmentNo.toInt()) {
                    isAdd = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        initPermissionCheck()
                    else {
                        showPictureDialog()
                    }
                }
                else
                    (mContext as DashboardActivity).showSnackMessage("Can not add documents more than " + Pref.docAttachmentNo)
            }
            else {
                isAdd = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    initPermissionCheck()
                else {
                    showPictureDialog()
                }
            }
        }
    }


    private fun initPermissionCheck() {
        permissionUtils = PermissionUtils(mContext as Activity, object : PermissionUtils.OnPermissionListener {
            override fun onPermissionGranted() {
                showPictureDialog()
            }

            override fun onPermissionNotGranted() {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.accept_permission))
            }

        }, arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    }

    fun onRequestPermission(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(mContext)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture Image", "Select file from file manager")
        pictureDialog.setItems(pictureDialogItems,
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        0 -> selectImageInAlbum()
                        1 -> {
                            //(mContext as DashboardActivity).openFileManager()
                            launchCamera()
                        }
                        2 -> {
                            (mContext as DashboardActivity).openFileManager()
                        }
                    }
                })
        pictureDialog.show()
    }

    private fun launchCamera() {
        (mContext as DashboardActivity).captureImage()
    }

    private fun selectImageInAlbum() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        (mContext as DashboardActivity).startActivityForResult(galleryIntent, PermissionHelper.REQUEST_CODE_STORAGE)
    }

    fun setDocument(file: File){
        filePath = file.absolutePath
        checkToSeeAddOrEdit()
    }

    fun setImage(filePath: String) {
        val file = File(filePath)

        var newFile: File? = null

        progress_wheel.spin()
        doAsync {

            val processImage = ProcessImageUtils_v1(mContext, file, 50, true)
            newFile = processImage.ProcessImage()

            uiThread {
                if (newFile != null) {
                    XLog.e("=========Image from new technique==========")
                    documentPic(newFile!!.length(), newFile?.absolutePath!!)
                } else {
                    // Image compression
                    val fileSize = AppUtils.getCompressImage(filePath)
                    documentPic(fileSize, filePath)
                }
            }
        }
    }

    private fun documentPic(fileSize: Long, file_path: String) {
        val fileSizeInKB = fileSize / 1024
        Log.e("Add Document", "image file size after compression=====> $fileSizeInKB KB")

        progress_wheel.stopSpinning()

        if (!TextUtils.isEmpty(Pref.maxFileSize)) {
            if (fileSizeInKB > Pref.maxFileSize.toInt()) {
                (mContext as DashboardActivity).showSnackMessage("More than " + Pref.maxFileSize + " KB file is not allowed")
                return
            }
        }

        filePath = file_path

        checkToSeeAddOrEdit()
    }

    private fun checkToSeeAddOrEdit() {
        if (isAdd)
            insertNewData()
        else {
            val document = AppDatabase.getDBInstance()?.documentListDao()?.getSingleData(docId)
            if (document?.isUploaded!!) {
                if (AppUtils.isOnline(mContext)) {
                    document.attachment = filePath
                    document.date_time = AppUtils.getCurrentISODateTime()
                    callAddEditDocApi(document)
                }
                else
                    (mContext as DashboardActivity).showSnackMessage("Document already saved in server so edit only possible if you have internet connection")
            }
            else
                editOldData(document)
        }
    }

    private fun editOldData(document: DocumentListEntity) {
        document.attachment = filePath
        document.date_time = AppUtils.getCurrentISODateTime()
        AppDatabase.getDBInstance()?.documentListDao()?.update(document)

        if (AppUtils.isOnline(mContext)) {
            isAdd = true
            callAddEditDocApi(document)
        } else {
            (mContext as DashboardActivity).showSnackMessage("Document edited successfully")
            docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
            initAdapter()
        }
    }

    private fun insertNewData() {
        val docListEntity = DocumentListEntity()
        AppDatabase.getDBInstance()?.documentListDao()?.insert(docListEntity.apply {
            list_id = Pref.user_id + "_Document_" + System.currentTimeMillis()
            type_id = typeId
            date_time = AppUtils.getCurrentISODateTime()
            attachment = filePath
            isUploaded = false
            document_name = documentName
        })

        if (AppUtils.isOnline(mContext))
            callAddEditDocApi(docListEntity)
        else {
            (mContext as DashboardActivity).showSnackMessage("Document added successfully")
            docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
            initAdapter()
        }
    }

    private fun callAddEditDocApi(docListEntity: DocumentListEntity) {
        val docInfo = DocumentAttachmentModel(docListEntity.attachment!!, docListEntity.list_id!!, docListEntity.type_id!!,
                docListEntity.date_time!!)

        val docInfoList = ArrayList<DocumentAttachmentModel>()
        docInfoList.add(docInfo)

        val repository = DocumentRepoProvider.documentRepoProviderMultipart()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.addEditDoc(AddEditDocumentInputParams(Pref.session_token!!, Pref.user_id!!), docInfoList, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse
                            XLog.d("ADD/EDIT DOCUMENT RESPONSE=======> " + response.status)

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.documentListDao()?.updateIsUploaded(true, docListEntity.list_id!!)
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                //AppDatabase.getDBInstance()?.documentListDao()?.updateAttachment("", docListEntity.list_id!!)

                                if (isAdd) {
                                    docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                                    initAdapter()
                                } else {
                                    AppDatabase.getDBInstance()?.documentListDao()?.update(docListEntity)
                                    docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                                    initAdapter()
                                }

                            } else {
                                if (isAdd)
                                    (mContext as DashboardActivity).showSnackMessage("Document added successfully")
                                else
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                                initAdapter()
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("ADD/EDIT DOCUMENT ERROR=======> " + error.localizedMessage)
                            if (isAdd)
                                (mContext as DashboardActivity).showSnackMessage("Document added successfully")
                            else
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))

                            docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                            initAdapter()
                        })
        )
    }

    private fun getDocumentListApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = DocumentRepoProvider.documentRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getDocList(typeId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as DocumentListResponseModel
                            XLog.d("DOCUMENT LIST RESPONSE=======> " + response.status)

                            if (response.status == NetworkConstant.SUCCESS) {
                                if (response.doc_list != null && response.doc_list!!.size > 0) {
                                    doAsync {
                                        AppDatabase.getDBInstance()?.documentListDao()?.deleteAll()
                                        response.doc_list?.forEach {
                                            val docListEntity = DocumentListEntity()
                                            AppDatabase.getDBInstance()?.documentListDao()?.insert(docListEntity.apply {
                                                list_id = it.id
                                                type_id = it.type_id
                                                date_time = it.date_time
                                                attachment = it.attachment
                                                isUploaded = true
                                                document_name = it.document_name
                                            })
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()
                                            docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                                            if (docList != null && docList!!.isNotEmpty())
                                                initAdapter()
                                        }
                                    }
                                } else {
                                    progress_wheel.stopSpinning()
                                    tv_no_data.visibility = View.VISIBLE
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                progress_wheel.stopSpinning()
                                tv_no_data.visibility = View.VISIBLE
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            tv_no_data.visibility = View.VISIBLE
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            XLog.d("DOCUMENT LIST ERROR=======> " + error.localizedMessage)
                        })
        )
    }

    private fun initAdapter() {
        tv_no_data.visibility = View.GONE
        rv_document_list.adapter = DocumentAdapter(mContext, docList!!, {
            isAdd = false
            docId = it.list_id!!
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                initPermissionCheck()
            else {
                showPictureDialog()
            }
        }, {
            showDeleterAlert(it)
        }, { doc, fileName ->

            if (doc.attachment!!.startsWith("http"))
                downloadFile(doc.attachment!!, fileName, doc)
            else
                shareDoc(doc.attachment!!)
        }, {
            syncDocApi(it)
        },
                /*, { document, fileName ->
            val file = File(document.attachment!!)
            if (document.attachment?.startsWith("http")!!) {
                val mimeType = NewFileUtils.getMemeTypeFromFile(file.absolutePath + "." + NewFileUtils.getExtension(file))

                if (mimeType != "image/jpeg" || mimeType != "image/png")
                    downloadFile(document.attachment, fileName, document)
                else
                    openFile(file)
            }
            else
                openFile(file)
        }*/{ document ->
            val fileName: String = document.attachment!!
            if (document.attachment!!.startsWith("http")) {
                var strFileName = File(document.attachment!!).name
                downloadFile(document.attachment!!, strFileName, document)
                //downloadFile(document.attachment!!, fileName, document)
            } else {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.already_download))
                Log.e("Attachment", "Attachment downloading=======> " + "Already Downloading..")
            }
        },
                { doc ->
//                    val file = File(FTStorageUtils.getFolderPath(mContext) + "/" + doc.attachment)
                    var tt="aer"
                    if(doc.attachment!!.contains("Commonfolder") &&  AppUtils.isOnline(mContext)){
                        (mContext as DashboardActivity).loadFragment(FragType.OpenFileWebViewFragment, true,doc.attachment!!)
                    }else{
                        val file = File(doc.attachment)
                        var strFileName = ""
                        if (!doc.attachment!!.startsWith("http")) {
                        strFileName = file.name
                         openFile(file = file)
                        } else {
                        strFileName = doc.attachment!!.substring(doc.attachment!!.lastIndexOf("/")!! + 1)
                        downloadFile(doc.attachment, strFileName)
                        }
                    }




//                    openFile(file)

                },
                { doc, fileName ->
                    //19-10-2021  file open

                    if(doc.attachment!!.contains("Commonfolder") &&  AppUtils.isOnline(mContext)){
                        (mContext as DashboardActivity).loadFragment(FragType.OpenFileWebViewFragment, true,doc.attachment!!)
                    }else {
                        val file = File(doc.attachment!!)
                        var strFileName = ""
                        if (!doc.attachment!!.startsWith("http")) {
                            strFileName = file.name
                            openFile(file = file)
//                        val intent = Intent(mContext, OpenFileWebViewFragment::class.java)
//                        intent.putExtra("file_url", file)
//                        (mContext as DashboardActivity).loadFragment(FragType.OpenFileWebViewFragment, true, FileProvider.getUriForFile(mContext, "", file))

                        } else {
                            strFileName = doc.attachment!!.substring(doc.attachment!!.lastIndexOf("/")!! + 1)
                            downloadFile(doc.attachment, strFileName)
                        }
                    }

                    //val file = File(doc.attachment)
                    //openFile(file = file)
                }
        )
    }

    private fun showDeleterAlert(document: DocumentListEntity) {
        CommonDialog.getInstance("Delete Alert", "Do you really want to delete this Document?", getString(R.string.cancel), getString(R.string.ok), object : CommonDialogClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(editableData: String) {
                if (document.isUploaded)
                    deleteDocument(document.list_id!!)
                else {
                    (mContext as DashboardActivity).showSnackMessage("Document deleted successfully")
                    AppDatabase.getDBInstance()?.documentListDao()?.delete(document.list_id!!)

                    docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                    if (docList != null && docList!!.isNotEmpty())
                        initAdapter()
                    else {
                        tv_no_data.visibility = View.VISIBLE
                        rv_document_list.visibility = View.GONE
                    }
                }
            }

        }).show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun deleteDocument(id: String) {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = DocumentRepoProvider.documentRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.deleteDoc(id)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->

                            progress_wheel.stopSpinning()

                            val response = result as BaseResponse

                            XLog.d("DELETE DOCUMENT RESPONSE=======> " + response.status)

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.documentListDao()?.delete(id)
                            }
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                            docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                            if (docList != null && docList!!.isNotEmpty())
                                initAdapter()
                            else {
                                tv_no_data.visibility = View.VISIBLE
                                rv_document_list.visibility = View.GONE
                            }

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            XLog.d("DELETE DOCUMENT ERROR=======> " + error.localizedMessage)

                            docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                            if (docList != null && docList!!.isNotEmpty())
                                initAdapter()
                            else {
                                tv_no_data.visibility = View.VISIBLE
                                rv_document_list.visibility = View.GONE
                            }
                        })
        )
    }

    private fun syncDocApi(docListEntity: DocumentListEntity) {

        val docInfo = DocumentAttachmentModel(docListEntity.attachment!!, docListEntity.list_id!!, docListEntity.type_id!!,
                docListEntity.date_time!!)
        println("upload_doc" + docInfo.toString());
        val docInfoList = ArrayList<DocumentAttachmentModel>()
        docInfoList.add(docInfo)

        val repository = DocumentRepoProvider.documentRepoProviderMultipart()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.addEditDoc(AddEditDocumentInputParams(Pref.session_token!!, Pref.user_id!!), docInfoList, mContext)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as BaseResponse
                            XLog.d("SYNC DOCUMENT RESPONSE=======> " + response.status)

                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()?.documentListDao()?.updateIsUploaded(true, docListEntity.list_id!!)
                                docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                                initAdapter()
                            }

                            (mContext as DashboardActivity).showSnackMessage(response.message!!)

                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            XLog.d("SYNC DOCUMENT ERROR=======> " + error.localizedMessage)
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun downloadFile(downloadUrl: String?, fileName: String, document: DocumentListEntity) {
        try {

            if (!AppUtils.isOnline(mContext)){
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }

            progress_wheel.spin()



            //PRDownloader.download(downloadUrl, Environment.getExternalStorageDirectory().toString() + File.separator, fileName)
            //27-09-2021
            PRDownloader.download(downloadUrl, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator, fileName)
                    .build()
                    .setOnProgressListener {
                        Log.e("Document List", "Attachment Download Progress======> $it")
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {

                            doAsync {
                                /* AppDatabase.getDBInstance()?.documentListDao()?.updateAttachment(
                                        Environment.getExternalStorageDirectory().toString() + File.separator + fileName, document.list_id!!)*/
                                //27-09-2021
                                AppDatabase.getDBInstance()?.documentListDao()?.updateAttachment(
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + File.separator + fileName, document.list_id!!)
                                uiThread {
                                    progress_wheel.stopSpinning()
                                    /*val file = File(Environment.getExternalStorageDirectory().toString() + File.separator + fileName)
                                    openFile(file)*/

                                    docList = AppDatabase.getDBInstance()?.documentListDao()?.getDataTypeWise(typeId) as ArrayList<DocumentListEntity>?
                                    initAdapter()


                                    (mContext as DashboardActivity).showSnackMessage("File Downloaded")

                                    //val doc_ = AppDatabase.getDBInstance()?.documentListDao()?.getSingleData(document.list_id!!)
                                    //shareDoc(doc_?.attachment!!)
                                }
                            }
                        }

                        override fun onError(error: Error) {
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("Download failed")
                            Log.e("Billing Details", "Attachment download error msg=======> " + error.serverErrorMessage)
                        }
                    })

        } catch (e: Exception) {
            (mContext as DashboardActivity).showSnackMessage("Download failed")
            progress_wheel.stopSpinning()
            e.printStackTrace()
        }

    }


    private fun openFile(file: File) {

        val mimeType = NewFileUtils.getMemeTypeFromFile(file.absolutePath + "." + NewFileUtils.getExtension(file))

        if (mimeType?.equals("application/pdf")!!) {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                 val fileUri = Uri.parse((file).path)
                (mContext as DashboardActivity).loadFragment(FragType.OpenFileWebViewFragment, true,fileUri)
//                val intent = Intent(mContext, OpenFileWebViewFragment::class.java)
//                intent.putExtra("file_url", file.toURI())
//                startActivity(intent)
//                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Pdf")
            }
        } else if (mimeType == "application/msword") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/msword")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }
        } else if (mimeType == "application/vnd.ms-excel") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }

        } else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.template") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.openxmlformats-officedocument.wordprocessingml.template")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }
        } else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Document")
            }

        } else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }
        } else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.template") {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-excel")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Excel")
            }
        } else if (mimeType == "image/jpeg" || mimeType == "image/png") {
            FullImageDialog.getInstance(file.path).show((mContext as DashboardActivity).supportFragmentManager, "")
//            FullImageDialog.getInstance(file.absolutePath).show((mContext as DashboardActivity).supportFragmentManager, "")
        }
    }

    private fun shareDoc(attachment: String) {
        val file = File(attachment)
        val mimeType = NewFileUtils.getMemeTypeFromFile(file.absolutePath + "." + NewFileUtils.getExtension(file))

        if (mimeType?.equals("application/pdf")!!)
            shareFile(attachment, "application/pdf")
        else if (mimeType == "application/msword")
            shareFile(attachment, "application/msword")
        else if (mimeType == "application/vnd.ms-excel")
            shareFile(attachment, "application/vnd.ms-excel")
        else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.template")
            shareFile(attachment, "application/vnd.openxmlformats-officedocument.wordprocessingml.template")
        else if (mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
            shareFile(attachment, "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
        else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            shareFile(attachment, "application/vnd.ms-excel")
        else if (mimeType == "application/vnd.openxmlformats-officedocument.spreadsheetml.template")
            shareFile(attachment, "application/vnd.ms-excel")
        else if (mimeType == "image/jpeg" || mimeType == "image/png" || mimeType == "image/jpg")
            shareFile(attachment, "image/*")
    }

    private fun shareFile(attachment: String, mimeType: String) {
        /*if (attachment.startsWith("http")) {
            downloadFile(attachment, fileName, document)
        }*/

        val intent = Intent(Intent.ACTION_SEND)
        val fileUrl = Uri.parse(File(attachment).path)

        val file = File(fileUrl.path)
        if (!file.exists()) {
            return
        }

//        val uri = Uri.fromFile(file)
        val uri:Uri= FileProvider.getUriForFile(mContext, context!!.applicationContext.packageName.toString() + ".provider", file)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = mimeType
        startActivity(Intent.createChooser(intent, "Share document via..."))

    }


    private fun downloadFile(downloadUrl: String?, fileName: String) {
        try {
            if (!AppUtils.isOnline(mContext)){
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }

            progress_wheel.spin()

            val folder = File(FTStorageUtils.getFolderPath(mContext) + "/", fileName)
            if (folder.exists()) {
                folder.delete()
                if (folder.exists()) {
                    folder.canonicalFile.delete()
                    if (folder.exists()) {
                        mContext.deleteFile(folder.getName())
                    }
                }
            }

            PRDownloader.download(downloadUrl, FTStorageUtils.getFolderPath(mContext) + "/", fileName)
                    .build()
                    .setOnProgressListener {
                        Log.e("Micro Learning Details", "Attachment Download Progress======> $it")
                    }
                    .start(object : OnDownloadListener {
                        override fun onDownloadComplete() {
                            progress_wheel.stopSpinning()
                            val file = File(FTStorageUtils.getFolderPath(mContext) + "/" + fileName)


                            openFile(file)

//                            if (!isVideo) {
//                                tempList.filter {
//                                    it.id == id
//                                }.map {
//                                    it.isDownloaded = true
//                                }
//
//                                list?.filter {
//                                    it.id == id
//                                }?.map {
//                                    it.isDownloaded = true
//                                }
//                                microLearningAdapter?.refreshList(list!!)
//                                openFile(file)
//                            }
//                            else {
//                                /*for (i in tempList.indices) {
//                                    if (tempList[i].id == id) {
//                                        tempList[i].url = file.absolutePath
//                                        break
//                                    }
//                                }*/
//
//                                tempList.filter {
//                                    it.id == id
//                                }.map {
//                                    it.url = file.absolutePath
//                                    it.isDownloaded = true
//                                }
//
//                                list?.filter {
//                                    it.id == id
//                                }?.map {
//                                    it.url = file.absolutePath
//                                    it.isDownloaded = true
//                                }
//
//                                microLearningAdapter?.refreshList(list!!)
//                                //initAdapter()
//                            }
                        }

                        override fun onError(error: Error) {
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("Download failed")
                            Log.e("Micro Learning Details", "Attachment download error msg=======> " + error.serverErrorMessage)
                        }
                    })

        } catch (e: Exception) {
            (mContext as DashboardActivity).showSnackMessage("Download failed")
            progress_wheel.stopSpinning()
            e.printStackTrace()
        }
    }



}