package com.kcteam.features.addAttendence

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.BeatEntity
import com.kcteam.app.domain.SelectedWorkTypeEntity
import com.kcteam.app.domain.WorkTypeEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.features.addAttendence.api.WorkTypeListRepoProvider
import com.kcteam.features.addAttendence.api.addattendenceapi.AddAttendenceRepoProvider
import com.kcteam.features.addAttendence.model.WorkTypeResponseModel
import com.kcteam.features.beatCustom.BeatGetStatusModel
import com.kcteam.features.beatCustom.BeatUpdateModel
import com.kcteam.features.beatCustom.api.GetBeatRegProvider
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Created by Saikat on 31-Jul-20.
 */
class UpdateWorkTypeFragment : Fragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var ll_work_type_list: LinearLayout
    private lateinit var rv_work_type_list: RecyclerView
    private lateinit var iv_work_type_dropdown: ImageView
    private lateinit var rl_work_type_header: RelativeLayout
    private lateinit var et_work_type_text: AppCustomEditText
    private lateinit var tv_submit: AppCustomTextView
    private lateinit var tv_work_type: AppCustomTextView
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private lateinit var cv_dd_field: CardView
    private lateinit var et_dd_name: AppCustomEditText
    private lateinit var et_market_worked: AppCustomEditText
    private lateinit var cv_beat: CardView
    private lateinit var tv_beat_type: AppCustomTextView
    private var mbeatId = ""

    private var workTypeId = ""

    private val workTypeList by lazy {
        ArrayList<WorkTypeEntity>()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_update_work_type, container, false)

        initView(view)
        initClickListener()

        return view
    }

    private fun initView(view: View) {
        tv_submit = view.findViewById(R.id.tv_submit)
        ll_work_type_list = view.findViewById(R.id.ll_work_type_list)
        rv_work_type_list = view.findViewById(R.id.rv_work_type_list)
        iv_work_type_dropdown = view.findViewById(R.id.iv_work_type_dropdown)
        rl_work_type_header = view.findViewById(R.id.rl_work_type_header)
        et_work_type_text = view.findViewById(R.id.et_work_type_text)
        tv_work_type = view.findViewById(R.id.tv_work_type)
        et_market_worked = view.findViewById(R.id.et_market_worked)
        et_dd_name = view.findViewById(R.id.et_dd_name)
        cv_dd_field = view.findViewById(R.id.cv_dd_field)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
        cv_beat = view.findViewById(R.id.cv_beat_type_root)
        tv_beat_type= view.findViewById(R.id.tv_beat_type)

        tv_beat_type.hint = "Select " + "${Pref.beatText}" + " Type"

        try {
            var beatName = AppDatabase.getDBInstance()?.beatDao()?.getSingleItem(Pref.SelectedBeatIDFromAttend)!!.name
            tv_beat_type.setText(beatName)
        }catch (e: Exception) {
            e.printStackTrace()
            tv_beat_type.setText("")
        }

        if(Pref.IsBeatRouteAvailableinAttendance)
        { cv_beat.visibility=View.VISIBLE
        }else{
            cv_beat.visibility=View.GONE
        }

        if (Pref.isDDFieldEnabled) {
            cv_dd_field.visibility = View.VISIBLE

            et_dd_name.setText(Pref.distributorName)
            et_market_worked.setText(Pref.marketWorked)
        }
        else
            cv_dd_field.visibility = View.GONE


        et_work_type_text.setOnTouchListener(View.OnTouchListener { v, event ->
            v.parent.requestDisallowInterceptTouchEvent(true)

            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }

            false
        })

        val list = AppDatabase.getDBInstance()?.workTypeDao()?.getAll() as ArrayList<WorkTypeEntity>
        if (list == null || list.isEmpty())
            getWorkTypeListApi()
        else
            initAdapter(list)
    }

    private fun getWorkTypeListApi() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        val repository = WorkTypeListRepoProvider.workTypeListRepo()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.getWorkTypeList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            progress_wheel.stopSpinning()
                            val response = result as WorkTypeResponseModel
                            if (response.status == NetworkConstant.SUCCESS) {
                                val list = response.worktype_list

                                for (i in 0 until (list?.size ?: 0)) {
                                    val workType = WorkTypeEntity()
                                    workType.ID = list!![i].ID.toInt()
                                    workType.Descrpton = list[i].Descrpton

                                    val work_type_list = AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.getTodaysData(AppUtils.getCurrentDate()) as ArrayList<SelectedWorkTypeEntity>
                                    for (j in work_type_list.indices) {
                                        workType.isSelected = false
                                        if (workType.ID == work_type_list[j].ID) {
                                            workType.isSelected = true
                                            break
                                        }
                                    }

                                    AppDatabase.getDBInstance()?.workTypeDao()?.insertAll(workType)
                                }

                                val list_ = AppDatabase.getDBInstance()?.workTypeDao()?.getAll() as ArrayList<WorkTypeEntity>
                                initAdapter(list_)

                                Log.e("add attendance", "api work type")
                            }
                        }, { error ->
                            error.printStackTrace()
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }

    @SuppressLint("WrongConstant")
    private fun initAdapter(list: ArrayList<WorkTypeEntity>) {
        list.forEach {
            if (it.isSelected) {
                workTypeList.add(it)

                workTypeId = if (TextUtils.isEmpty(workTypeId))
                    it.ID.toString()
                else
                    workTypeId + "," + it.ID.toString()

                if (TextUtils.isEmpty(tv_work_type.text.toString().trim()))
                    tv_work_type.text = it.Descrpton
                else
                    tv_work_type.text = tv_work_type.text.toString().trim() + ", " + it.Descrpton
            }
        }

        rv_work_type_list.layoutManager = LinearLayoutManager(mContext, LinearLayout.VERTICAL, false)
        rv_work_type_list.adapter = UpdateWorkTypeAdapter(mContext, list, true, object : UpdateWorkTypeAdapter.OnWorkTypeClickListener {
            override fun onWorkTypeClick(workType: WorkTypeEntity?, adapterPosition: Int) {

                checkIfWorkTypeSelected(workType, adapterPosition, list)

                if (workTypeList.size > 0) {

                    for (i in workTypeList.indices) {
                        if (Pref.isMultipleAttendanceSelection) {
                            if (i == 0) {
                                workTypeId = if (TextUtils.isEmpty(workTypeId))
                                    workTypeList[i].ID.toString()
                                else
                                    workTypeId + "," + workTypeList[i].ID

                                if (TextUtils.isEmpty(tv_work_type.text.toString().trim()))
                                    tv_work_type.text = workTypeList[i].Descrpton
                                else
                                    tv_work_type.text = tv_work_type.text.toString().trim() + ", " + workTypeList[i].Descrpton

                            } else {
                                workTypeId = workTypeId + "," + workTypeList[i].ID
                                tv_work_type.text = tv_work_type.text.toString().trim() + ", " + workTypeList[i].Descrpton
                            }
                        } else {
                            workTypeId = workTypeList[i].ID.toString()
                            tv_work_type.text = workTypeList[i].Descrpton
                        }
                    }
                } else {
                    workTypeId = ""
                    tv_work_type.text = ""

                }
            }
        })
    }

    private fun checkIfWorkTypeSelected(workType: WorkTypeEntity?, adapterPosition: Int, list: ArrayList<WorkTypeEntity>?) {
        if (workTypeList.size > 0) {

            if (Pref.isMultipleAttendanceSelection) {
                workTypeId = ""
                tv_work_type.text = ""
            }

            for (i in workTypeList.indices) {
                if (workTypeList[i].ID == workType?.ID) {
                    //AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(false, workType.ID)
                    workTypeList.remove(list?.get(adapterPosition))
                    return
                }
            }

            if (!Pref.isMultipleAttendanceSelection) {
                workTypeList.forEach {
                    //AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(false, it.ID)
                    workTypeList.remove(it)
                }
            }

            //AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(true, workType?.ID!!)
            workTypeList.add(list?.get(adapterPosition)!!)

        } else {
            //AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(true, workType?.ID!!)
            workTypeList.add(list!![adapterPosition])
        }
    }

    private fun initClickListener() {
        tv_submit.setOnClickListener(this)
        rl_work_type_header.setOnClickListener(this)
        cv_beat.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_submit -> {
                AppUtils.hideSoftKeyboard(mContext as DashboardActivity)
                visibilityCheck()
            }

            R.id.cv_beat_type_root->{
                val list = AppDatabase.getDBInstance()?.beatDao()?.getAll() as ArrayList<BeatEntity>
                if (list != null && list.isNotEmpty())
                    showBeatListDialog(list)
                else
                    (mContext as DashboardActivity).showSnackMessage("No list found")
            }

            R.id.rl_work_type_header -> {
                if (iv_work_type_dropdown.isSelected) {
                    iv_work_type_dropdown.isSelected = false
                    ll_work_type_list.visibility = View.GONE
                } else {
                    iv_work_type_dropdown.isSelected = true
                    ll_work_type_list.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun visibilityCheck() {
        if (TextUtils.isEmpty(workTypeId))
            (mContext as DashboardActivity).showSnackMessage("Please select work type")
        else if (Pref.isDDFieldEnabled && TextUtils.isEmpty(et_dd_name.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage("Please enter distributor name")
        else if (Pref.isDDFieldEnabled && TextUtils.isEmpty(et_market_worked.text.toString().trim()))
            (mContext as DashboardActivity).showSnackMessage("Please enter market worked")
        else {
            if (!AppUtils.isOnline(mContext)) {
                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
                return
            }

            if (BaseActivity.isApiInitiated)
                return

            BaseActivity.isApiInitiated = true

            XLog.d("=========Update Work Type Input Params==========")
            XLog.d("session_token======> " + Pref.session_token)
            XLog.d("user_id========> " + Pref.user_id)
            XLog.d("workTypeId=======> $workTypeId")
            XLog.d("work_desc=======> " + et_work_type_text.text.toString().trim())
            XLog.d("distributor_name=======> " + et_dd_name.text.toString().trim())
            XLog.d("market_worked=======> " + et_market_worked.text.toString().trim())
            XLog.d("=================================================")

            val repository = AddAttendenceRepoProvider.addAttendenceRepo()
            progress_wheel.spin()
            BaseActivity.compositeDisposable.add(
                    repository.updateWorkType(workTypeId, et_work_type_text.text.toString().trim(), et_dd_name.text.toString().trim(),
                            et_market_worked.text.toString().trim())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                progress_wheel.stopSpinning()
                                val response = result as BaseResponse
                                BaseActivity.isApiInitiated = false

                                XLog.d("Update work type Response Code========> " + response.status)
                                XLog.d("Update work type Response Msg=========> " + response.message)

//                                (mContext as DashboardActivity).showSnackMessage(response.message!!)

                                if (response.status == NetworkConstant.SUCCESS) {

                                    Pref.distributorName = et_dd_name.text.toString().trim()
                                    Pref.marketWorked = et_market_worked.text.toString().trim()

                                    val list = AppDatabase.getDBInstance()?.workTypeDao()?.getAll()
                                    list?.forEach {
                                        AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(false, it.ID)
                                    }

                                    workTypeList.forEach {
                                        AppDatabase.getDBInstance()?.workTypeDao()?.updateIsSelected(true, it.ID)
                                    }

                                    AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.delete()

                                    if (TextUtils.isEmpty(Pref.isFieldWorkVisible) || Pref.isFieldWorkVisible.equals("true", ignoreCase = true)) {

                                        val list_ = AppDatabase.getDBInstance()?.workTypeDao()?.getSelectedWork(true)
                                        if (list_ != null && list_.isNotEmpty()) {

                                            for (i in list_.indices) {
                                                val selectedwortkType = SelectedWorkTypeEntity()
                                                selectedwortkType.ID = list_[i].ID
                                                selectedwortkType.Descrpton = list_[i].Descrpton
                                                selectedwortkType.date = AppUtils.getCurrentDate()
                                                AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.insertAll(selectedwortkType)
                                            }
                                        }
                                    }
                                    /*else
                                        AppDatabase.getDBInstance()?.selectedWorkTypeDao()?.delete()*/

                                    //(mContext as DashboardActivity).onBackPressed()
                                    /*Update beat type Restricted 10-08-2022 */
                                    //var shopCreated = AppDatabase.getDBInstance()?.addShopEntryDao()?.getShopCreatedToday(AppUtils.getCurrentDate())
                                    var shopRevit = AppDatabase.getDBInstance()!!.shopActivityDao().getTotalShopVisitedForADay(AppUtils.getCurrentDateyymmdd())
                                   if(Pref.IsBeatRouteAvailableinAttendance){
                                       //if(shopCreated!!.size>0 || shopRevit!!.size>0){
                                       if(shopRevit!!.size>0){
                                           val simpleDialog = Dialog(mContext)
                                           simpleDialog.setCancelable(false)
                                           simpleDialog.getWindow()!!.setBackgroundDrawable(
                                               ColorDrawable(Color.TRANSPARENT)
                                           )
                                           simpleDialog.setContentView(R.layout.dialog_message)
                                           val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                                           val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                                           dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"
                                           dialogHeader.text = "Update work type unable due to shop visit/revisit."
                                           val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                                           dialogYes.setOnClickListener({ view ->
                                               simpleDialog.cancel()
                                           })
                                           simpleDialog.show()
                                       }else{
                                           updateBeatType()
                                       }
                                   }
                                 else{
                                       (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                   }

                                }

                            }, { error ->
                                XLog.d("Update work type Response Msg=========> " + error.message)
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }
    }

    private fun showBeatListDialog(list: ArrayList<BeatEntity>) {
        BeatListCustomDialog.newInstance(list as ArrayList<BeatEntity>) {
            tv_beat_type.text = it.name
            mbeatId = it.beat_id!!
            //Pref.SelectedBeatIDFromAttend = mbeatId
        }.show((mContext as DashboardActivity).supportFragmentManager, "")

    }

    private fun updateBeatType(){
        progress_wheel.spin()
        try{
            val repository = GetBeatRegProvider.provideSaveButton()
            BaseActivity.compositeDisposable.add(
                repository.getUpdateBeat(Pref.user_id!!,mbeatId,AppUtils.getCurrentDateyymmdd())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        val viewResult = result as BeatUpdateModel
                        if (viewResult!!.status == NetworkConstant.SUCCESS) {
                            progress_wheel.stopSpinning()
                            doAsync {
                                Pref.SelectedBeatIDFromAttend = viewResult.updated_beat_id!!
                                uiThread {
                                    (mContext as DashboardActivity).onBackPressed()
                                }
                            }
                        }
                        else {
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(viewResult.message!!)
                        }
                    }, { error ->
                        progress_wheel.stopSpinning()
                        (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                    })
            )
        }
        catch (ex:Exception){
            ex.printStackTrace()
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong1))
        }

    }

}