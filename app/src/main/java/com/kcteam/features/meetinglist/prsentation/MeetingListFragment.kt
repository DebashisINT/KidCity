package com.kcteam.features.meetinglist.prsentation

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elvishew.xlog.XLog
import com.pnikosis.materialishprogress.ProgressWheel
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.domain.MeetingEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.model.MeetingDurationDataModel
import com.kcteam.features.location.model.MeetingDurationInputParams
import com.kcteam.features.location.shopdurationapi.ShopDurationRepositoryProvider
import com.kcteam.features.meetinglist.api.MeetingRepoProvider
import com.kcteam.features.meetinglist.model.MeetingListResponseModel
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

/**
 * Created by Saikat on 20-01-2020.
 */
class MeetingListFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var rv_meeting_list: RecyclerView
    private lateinit var tv_no_data: AppCustomTextView
    private lateinit var progress_wheel: ProgressWheel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_meeting_list, container, false)

        initView(view)

        val list = AppDatabase.getDBInstance()!!.addMeetingDao().getAll() as ArrayList
        if (list != null && list.isNotEmpty())
            initAdapter(list)
        else
            getMeetingList()

        return view
    }

    private fun initView(view: View) {
        rv_meeting_list = view.findViewById(R.id.rv_meeting_list)
        rv_meeting_list.layoutManager = LinearLayoutManager(mContext)

        tv_no_data = view.findViewById(R.id.tv_no_data)
        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
    }

    private fun getMeetingList() {

        if (!AppUtils.isOnline(mContext)) {
            tv_no_data.visibility = View.VISIBLE
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = MeetingRepoProvider.meetingRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.meetingList()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as MeetingListResponseModel
                            XLog.d("GET MEETING DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {

                                if (response.meeting_list != null && response.meeting_list!!.size > 0) {

                                    doAsync {
                                        for (i in response.meeting_list!!.indices) {
                                            val meetingEntity = MeetingEntity()
                                            meetingEntity.date = response.meeting_list?.get(i)?.date
                                            meetingEntity.duration_spent = response.meeting_list?.get(i)?.duration
                                            meetingEntity.remakrs = response.meeting_list?.get(i)?.remarks
                                            meetingEntity.date_time = response.meeting_list?.get(i)?.date_time
                                            meetingEntity.lattitude = response.meeting_list?.get(i)?.latitude
                                            meetingEntity.longitude = response.meeting_list?.get(i)?.longitude
                                            meetingEntity.meetingTypeId = response.meeting_list?.get(i)?.meeting_type_id
                                            meetingEntity.address = response.meeting_list?.get(i)?.address
                                            meetingEntity.pincode = response.meeting_list?.get(i)?.pincode
                                            meetingEntity.distance_travelled = response.meeting_list?.get(i)?.distance_travelled
                                            meetingEntity.isUploaded = true
                                            meetingEntity.isDurationCalculated = true

                                            AppDatabase.getDBInstance()!!.addMeetingDao().insertAll(meetingEntity)
                                        }

                                        uiThread {
                                            progress_wheel.stopSpinning()

                                            val list = AppDatabase.getDBInstance()!!.addMeetingDao().getAll() as ArrayList
                                            initAdapter(list)
                                        }
                                    }
                                } else {
                                    tv_no_data.visibility = View.VISIBLE
                                    progress_wheel.stopSpinning()
                                    (mContext as DashboardActivity).showSnackMessage(response.message!!)
                                }
                            } else {
                                tv_no_data.visibility = View.VISIBLE
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("GET MEETING DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            tv_no_data.visibility = View.VISIBLE
                        })
        )
    }

    private fun initAdapter(list: java.util.ArrayList<MeetingEntity>) {
        tv_no_data.visibility = View.GONE
        rv_meeting_list.adapter = MeetingListAdapter(mContext, list, object : MeetingListAdapter.OnClickListener {
            override fun onSyncClick(adapterPosition: Int) {
                syncMeetingData(list[adapterPosition])
            }
        })
    }

    private fun syncMeetingData(meetingEntity: MeetingEntity) {

        if (TextUtils.isEmpty(Pref.user_id))
            return

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }

        XLog.e("IS MEETING UPDATING (MEETING LIST FRAGMENT)===========> ${BaseActivity.isMeetingUpdating}")

        if (BaseActivity.isMeetingUpdating)
            return

        BaseActivity.isMeetingUpdating = true

        val meeting = MeetingDurationInputParams()
        meeting.session_token = Pref.session_token!!
        meeting.user_id = Pref.user_id!!

        val meetingDataList = ArrayList<MeetingDurationDataModel>()

        val meetingData = MeetingDurationDataModel()
        meetingData.duration = meetingEntity.duration_spent!!
        meetingData.latitude = meetingEntity.lattitude!!
        meetingData.longitude = meetingEntity.longitude!!
        meetingData.remarks = meetingEntity.remakrs!!
        meetingData.meeting_type_id = meetingEntity.meetingTypeId!!
        meetingData.distance_travelled = meetingEntity.distance_travelled!!
        meetingData.date = meetingEntity.date!!
        meetingData.address = meetingEntity.address!!
        meetingData.pincode = meetingEntity.pincode!!
        meetingData.date_time = meetingEntity.date_time!!

        meetingDataList.add(meetingData)

        meeting.meeting_list = meetingDataList

        XLog.d("========UPLOAD MEETING DATA INPUT PARAMS (MEETING LIST)======")
        XLog.d("USER ID======> " + meeting.user_id)
        XLog.d("SESSION ID======> " + meeting.session_token)
        XLog.d("MEETING LIST SIZE=========> " + meeting.meeting_list.size)
        XLog.d("==============================================================")

        progress_wheel.spin()
        val repository = ShopDurationRepositoryProvider.provideShopDurationRepository()
        BaseActivity.compositeDisposable.add(
                repository.meetingDuration(meeting)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("SYNC MEETING DATA : " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            if (response.status == NetworkConstant.SUCCESS) {
                                AppDatabase.getDBInstance()!!.addMeetingDao().updateIsUploaded(true, meetingEntity.id)
                            }
                            (mContext as DashboardActivity).showSnackMessage(response.message!!)
                            progress_wheel.stopSpinning()
                            BaseActivity.isMeetingUpdating = false

                            val list = AppDatabase.getDBInstance()!!.addMeetingDao().getAll() as ArrayList
                            initAdapter(list)

                        }, { error ->
                            progress_wheel.stopSpinning()
                            XLog.d("SYNC MEETING DATA : " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            BaseActivity.isMeetingUpdating = false
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.unable_to_sync_meeting))
                        })
        )

    }
}