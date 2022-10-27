package com.kcteam.features.leaveapplynew

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kcteam.R
import com.kcteam.app.AppDatabase
import com.kcteam.app.NetworkConstant
import com.kcteam.app.domain.UserWiseLeaveListEntity
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addAttendence.api.leavetytpeapi.LeaveTypeRepoProvider
import com.kcteam.features.addAttendence.model.ApprovalLeaveResponseModel
import com.kcteam.features.addAttendence.model.Leave_list_Response
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.leaveapplynew.adapter.AdapterLeaveStatusList
import com.kcteam.widgets.AppCustomTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class LeaveStatusFrag: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var rvStatusList: RecyclerView
    var appliedStatusList: ArrayList<Leave_list_Response> = ArrayList()
    private lateinit var noData: AppCustomTextView


    private var adapterStatusList: AdapterLeaveStatusList? = null
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel
    private var userId = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.leave_status_frag, container, false)
        initView(view)
        return view
    }

    private fun initView(view:View){
        rvStatusList = view.findViewById(R.id.rv_status_list)
        noData = view.findViewById(R.id.tv_no_data)
        rvStatusList.layoutManager = LinearLayoutManager(mContext)
        userId=LeaveHome.user_uid

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        else{
            Handler().postDelayed(Runnable {
                getStatusList()
            }, 300)

        }
    }
    private fun getStatusList(){
        progress_wheel.spin()
        val repository = LeaveTypeRepoProvider.leaveTypeListRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getApprovalLeaveList(userId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ApprovalLeaveResponseModel
                            progress_wheel.stopSpinning()
                            noData.visibility = View.GONE
                            if (response.status == NetworkConstant.SUCCESS) {
                                appliedStatusList = response.leave_list!!
                                if (appliedStatusList != null && appliedStatusList.size > 0) {
                                    setAdapter()
                                } else{
                                    (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                                }
                            } else{
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                        })
        )
    }

    private fun setAdapter() {

        var list:ArrayList<Leave_list_Response> = ArrayList()
        for(i in 0..appliedStatusList.size-1){
            if(appliedStatusList.get(i).approve_status!! || appliedStatusList.get(i).reject_status!!){
                list.add(appliedStatusList.get(i))
            }
        }
        AppDatabase.getDBInstance()?.userWiseLeaveListDao()?.deleteAll()
        for(j in 0..list.size-1){
            var obj:UserWiseLeaveListEntity = UserWiseLeaveListEntity()
            obj.applied_date=list.get(j).applied_date
            obj.applied_date_time=list.get(j).applied_date_time
            obj.from_date=list.get(j).from_date
            obj.from_date_modified=formatDate(list.get(j).from_date,"dd-mm-yyyy","yyyy-mm-dd")+"T00:00:00.00"
            obj.to_date=list.get(j).to_date
            obj.leave_type=list.get(j).leave_type
            obj.approve_status=list.get(j).approve_status
            obj.reject_status=list.get(j).reject_status
            obj.leave_reason=list.get(j).leave_reason
            obj.approval_date_time=list.get(j).approval_date_time
            obj.approver_remarks=list.get(j).approver_remarks
            AppDatabase.getDBInstance()?.userWiseLeaveListDao()?.insert(obj)
        }

        var finalObjFiltered=AppDatabase.getDBInstance()?.userWiseLeaveListDao()?.getListLeaveStartDateWise()
        list.clear()
        for(k in 0..finalObjFiltered!!.size-1){
            var ob:Leave_list_Response = Leave_list_Response()
            ob.applied_date=finalObjFiltered.get(k).applied_date
            ob.applied_date_time=finalObjFiltered.get(k).applied_date_time
            ob.from_date=finalObjFiltered.get(k).from_date
            ob.to_date=finalObjFiltered.get(k).to_date
            ob.leave_type=finalObjFiltered.get(k).leave_type
            ob.approve_status=finalObjFiltered.get(k).approve_status
            ob.reject_status=finalObjFiltered.get(k).reject_status
            ob.leave_reason=finalObjFiltered.get(k).leave_reason
            ob.approval_date_time=finalObjFiltered.get(k).approval_date_time
            ob.approver_remarks=finalObjFiltered.get(k).approver_remarks
            list.add(ob)
        }

        if(list.size>0){
            Handler(Looper.getMainLooper()).postDelayed({
                rvStatusList.adapter = AdapterLeaveStatusList(mContext, list!!)
            }, 1000)

        }else{
            noData.visibility = View.VISIBLE
//            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
        }

    }

    @Throws(ParseException::class)
    fun formatDate(date: String?, initDateFormat: String?, endDateFormat: String?): String? {
        val initDate: Date = SimpleDateFormat(initDateFormat).parse(date)
        val formatter = SimpleDateFormat(endDateFormat)
        return formatter.format(initDate)
    }

    override fun onClick(v: View?) {
        when(v?.id){


            }
        }
    }




