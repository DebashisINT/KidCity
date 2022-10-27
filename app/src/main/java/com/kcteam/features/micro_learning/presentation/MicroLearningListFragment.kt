package com.kcteam.features.micro_learning.presentation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.NewFileUtils
import com.kcteam.app.Pref
import com.kcteam.app.SearchListener
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.FTStorageUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.micro_learning.api.MicroLearningRepoProvider
import com.kcteam.features.micro_learning.model.MicroLearningDataModel
import com.kcteam.features.micro_learning.model.MicroLearningResponseModel
import com.kcteam.widgets.AppCustomTextView
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.themechangeapp.pickimage.PermissionHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MicroLearningListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_micro_learning_list: RecyclerView
    private lateinit var rv_category_list: RecyclerView
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var fab: FloatingActionButton

    private var list: ArrayList<MicroLearningDataModel>?= null
    private var microLearningAdapter : MicroLearningAdapter?= null
    private var categoryAdapter : CategoryAdapter?= null
    private var dialog : UpdateNoteDialog? = null
    var openingDateTime = ""
    var selectedFile: MicroLearningDataModel?= null
    var isFilterSelected = false

    private val tempList: ArrayList<MicroLearningDataModel> by lazy {
        ArrayList<MicroLearningDataModel>()
    }

    private val categoryList : ArrayList<String> by lazy {
        ArrayList<String>()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_micro_learning, container, false)

        initView(view)
        getLearningList()

        (mContext as DashboardActivity).setSearchListener(object : SearchListener {
            override fun onSearchQueryListener(query: String) {
                if (query.isBlank()) {
                    if (rv_micro_learning_list.visibility == View.VISIBLE)
                        microLearningAdapter?.refreshList(list!!)
                    else if (rv_category_list.visibility == View.VISIBLE)
                        categoryAdapter?.refreshList(categoryList)
                } else {
                    if (rv_micro_learning_list.visibility == View.VISIBLE)
                        microLearningAdapter?.filter?.filter(query)
                    else if (rv_category_list.visibility == View.VISIBLE)
                        categoryAdapter?.filter?.filter(query)
                }
            }
        })

        /*Log.e("Learning", "==================Fetch Video duration from onCreate========================")
        doAsync {
            val duration = AppUtils.getDurationFromOnlineVideoLink("http://3.7.30.86:81/Commonfolder/DocumentSharing/Email Reporting.mp4")

            uiThread {
                Log.e("Learning", "Duration==================> $duration")
            }
        }*/

        return view
    }

    private fun initView(view: View) {
        view.apply {
            rv_micro_learning_list = findViewById(R.id.rv_micro_learning_list)
            rv_category_list = findViewById(R.id.rv_category_list)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
            progress_wheel = findViewById(R.id.progress_wheel)
            fab = findViewById(R.id.fab)
        }

        progress_wheel.stopSpinning()
        rv_micro_learning_list.layoutManager = LinearLayoutManager(mContext)
        rv_category_list.layoutManager = LinearLayoutManager(mContext)

        rv_micro_learning_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                /*if (dy > 0 || dy < 0 && fab.isShown)
                    fab.hide()*/

                if (dy < 0 && !fab.isShown)
                    fab.show()
                else if (dy > 0 && fab.isShown)
                    fab.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                /*if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    fab.show()*/

                super.onScrollStateChanged(recyclerView!!, newState)
            }
        })

        fab.setOnClickListener {
            isFilterSelected = true
            val treeSet = TreeSet<String>()
            treeSet.add("All")
            tempList.forEach {
                treeSet.add(it.category_name)
            }
            categoryList.clear()
            categoryList.addAll(treeSet)
            if (categoryList.isNotEmpty()) {
                rv_micro_learning_list.visibility = View.GONE
                tv_no_data_available.visibility = View.GONE
                rv_category_list.visibility = View.VISIBLE

                categoryAdapter = CategoryAdapter(mContext, categoryList) { category ->
                    list = if (category == "All")
                        tempList
                    else {
                        val list_ = tempList.filter {
                            it.category_name == category
                        }
                        list_ as ArrayList<MicroLearningDataModel>?
                    }
                    initAdapter()
                }

                rv_category_list.adapter = categoryAdapter
            }
            else {
                rv_micro_learning_list.visibility = View.GONE
                tv_no_data_available.visibility = View.VISIBLE
                rv_category_list.visibility = View.GONE
            }
            //it.visibility = View.GONE
        }
    }

    fun getLearningList() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = MicroLearningRepoProvider.microLearningRepoProvider()

        BaseActivity.compositeDisposable.add(
                repository.getMicroLearningList()
                        /*.flatMap(Function<MicroLearningResponseModel, Observable<MicroLearningResponseModel?>?> {
                            (micro_learning_list) -> Observable.fromIterable(micro_learning_list)
                                .forEach {
                                    if (it.isVideo) {
                                         val duration = AppUtils.getDurationFromOnlineVideoLink(it.url)
                                         it.video_duration = duration
                                    }
                                } as Observable<MicroLearningResponseModel?>
                        })*/
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as MicroLearningResponseModel
                            XLog.d("MICRO LEARNING LIST: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)

                            if (response.status == NetworkConstant.SUCCESS) {
                                /*Log.e("Learning", "==================Fetch Video duration for api call========================")
                                doAsync {
                                    response.micro_learning_list?.forEach {
                                        if (it.isVideo) {
                                            val duration = AppUtils.getDurationFromOnlineVideoLink(it.url)
                                            it.video_duration = duration
                                        }
                                    }

                                    uiThread {
                                        Log.e("Learning", "==================Video duration fetched for api call========================")*/
                                        progress_wheel.stopSpinning()
                                        list = response.micro_learning_list
                                        tempList.clear()
                                        tempList.addAll(list!!)
                                        initAdapter()
                                    /*}
                                }*/
                            } else {
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("MICRO LEARNING LIST: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun initAdapter() {
        tv_no_data_available.visibility = View.GONE
        rv_category_list.visibility = View.GONE
        rv_micro_learning_list.visibility = View.VISIBLE

        if (microLearningAdapter != null) {
            microLearningAdapter?.refreshList(list!!)
            return
        }

        microLearningAdapter = MicroLearningAdapter(mContext, list, {
            if (AppUtils.isOnline(mContext)) {
                selectedFile = it
                openingDateTime = AppUtils.getCurrentISODateTime()
                if (!it.isVideo) {
                    //downloadFile(it.url, it.file_name)
                    (mContext as DashboardActivity).loadFragment(FragType.MicroLearningWebViewFragment, true, it)
                }
                else {
                    val intent = Intent(mContext, ExoPlayerActivity::class.java)
                    intent.putExtra("learning", it)
                    (mContext as DashboardActivity).startActivityForResult(intent, PermissionHelper.REQUEST_CODE_EXO_PLAYER)
                }
            }
            else
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
        }, {
            showUpdateNoteDialog(it)
        }, {
            callUpdateDownloadStatusApi(it)
        })

        rv_micro_learning_list.adapter = microLearningAdapter
    }

    private fun downloadFile(downloadUrl: String?, fileName: String, isVideo: Boolean, id: String) {
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

                            tempList.filter {
                                it.id == id
                            }.map {
                                it.isDownloaded = true
                                if (isVideo)
                                    it.url = file.absolutePath
                            }

                            list?.filter {
                                it.id == id
                            }?.map {
                                it.isDownloaded = true
                                if (isVideo)
                                    it.url = file.absolutePath
                            }
                            microLearningAdapter?.refreshList(list!!)

                            if (!isVideo)
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

    private fun callUpdateDownloadStatusApi(it: MicroLearningDataModel) {
        if (!AppUtils.isOnline(mContext)){
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = MicroLearningRepoProvider.microLearningRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.updateDownloadStatus(it.id, true)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("UPDATE DOWNLOAD STATUS: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {
                                //(mContext as DashboardActivity).showSnackMessage(response.message!!)
                                downloadFile(it.url, it.file_name, it.isVideo, it.id)
                            }
                            else
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("UPDATE DOWNLOAD STATUS: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
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
                startActivity(intent)
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
        }
        else /*if (mimeType == "application/vnd.ms-powerpoint")*/ {
            val path1 = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path1, "application/vnd.ms-powerpoint")
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                (mContext as DashboardActivity).showSnackMessage("No Application Available to View Powerpoint")
            }
        }
    }

    private fun showUpdateNoteDialog(it: MicroLearningDataModel) {
        dialog = UpdateNoteDialog.getInstance(AppUtils.hiFirstNameText(), getString(R.string.cancel), getString(R.string.ok), false,
                false, false, it.note, object : UpdateNoteDialog.OnButtonClickListener {
            override fun onLeftClick() {
            }

            override fun onRightClick(note: String) {
                callUpdateNoteApi(note, it)
            }

        })
        dialog?.show((mContext as DashboardActivity).supportFragmentManager, "")
    }

    private fun callUpdateNoteApi(note: String, it: MicroLearningDataModel) {
        progress_wheel.spin()
        val repository = MicroLearningRepoProvider.microLearningRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.updateNote(it.id, note)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("UPDATE NOTE: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()

                            if (response.status == NetworkConstant.SUCCESS) {
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                getLearningList()
                            } else {
                                it.note = note
                                showUpdateNoteDialog(it)
                                Toaster.msgShort(mContext, response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("UPDATE NOTE: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            it.note = note
                            showUpdateNoteDialog(it)
                            Toaster.msgShort(mContext, getString(R.string.something_went_wrong))
                        })
        )
    }

    fun showAllList() {
        isFilterSelected = false
        list = tempList
        initAdapter()
    }
}