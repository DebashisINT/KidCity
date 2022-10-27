package com.kcteam.features.leaveapplynew

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.kcteam.MySingleton
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.types.FragType
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.addAttendence.api.addattendenceapi.AddAttendenceRepoProvider
import com.kcteam.features.addAttendence.api.leavetytpeapi.LeaveTypeRepoProvider
import com.kcteam.features.addAttendence.model.ApprovalLeaveResponseModel
import com.kcteam.features.addAttendence.model.GetReportToFCMResponse
import com.kcteam.features.addAttendence.model.Leave_list_Response
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.leaveapplynew.adapter.AdapterAppliedLeaveList
import com.kcteam.features.leaveapplynew.model.ApprovalRejectReqModel
import com.kcteam.features.leaveapplynew.model.clearAttendanceonRejectReqModelRejectReqModel
import com.kcteam.widgets.AppCustomEditText
import com.kcteam.widgets.AppCustomTextView
import com.elvishew.xlog.XLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.util.HashMap

class ApprovalPendFrag: BaseFragment(), View.OnClickListener {

    private lateinit var mContext: Context

    private lateinit var rvApprovalPendingList: RecyclerView
    private lateinit var noData: AppCustomTextView
    var appliedLeaveList: ArrayList<Leave_list_Response> = ArrayList()
    private var userId = ""
    private var remark = ""

    private var adapterappliedLeaveList: AdapterAppliedLeaveList? = null
    private lateinit var progress_wheel: com.pnikosis.materialishprogress.ProgressWheel


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.frag_aproval_pending, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View){
        rvApprovalPendingList = view.findViewById(R.id.rv_all_approval_pending_list)
        noData = view.findViewById(R.id.tv_no_data)
        rvApprovalPendingList.layoutManager = LinearLayoutManager(mContext)
        userId=LeaveHome.user_uid

        progress_wheel = view.findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()

        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            return
        }
        else{
            getApprovalPendingList()
        }
    }


    private fun getApprovalPendingList(){
        progress_wheel.spin()
        appliedLeaveList.clear()
        val repository = LeaveTypeRepoProvider.leaveTypeListRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getApprovalLeaveList(userId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as ApprovalLeaveResponseModel
                            progress_wheel.stopSpinning()
                            if (response.status == NetworkConstant.SUCCESS) {
                                noData.visibility = View.GONE
                                appliedLeaveList = response.leave_list!!
                                if (appliedLeaveList != null && appliedLeaveList.size > 0) {
                                    var clickedUserId=response.user_id_leave_applied!!
                                    setAdapter(clickedUserId)
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

    private fun setAdapter(clickedUserId:String) {

        var list:ArrayList<Leave_list_Response> = ArrayList()
        for(i in 0..appliedLeaveList.size-1){
            if(appliedLeaveList.get(i).approve_status ==false && appliedLeaveList.get(i).reject_status==false){
                list.add(appliedLeaveList.get(i))
            }
        }
        if(list.size>0){
            rvApprovalPendingList.adapter = AdapterAppliedLeaveList(mContext, list!!, clickedUserId, object : ClickonStatus {
                override fun OnApprovedclick(obj: Leave_list_Response) {
                    dialogOpenRemark("APPROVE", obj)
                }

                override fun OnRejectclick(obj: Leave_list_Response) {
                    dialogOpenRemark("REJECT", obj)
                }
            })
        }else{
            noData.visibility = View.VISIBLE
//            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_data_found))
        }
    }


    private fun dialogOpenRemark(status:String,obj: Leave_list_Response) {
        val simpleDialog = Dialog(mContext)
        simpleDialog.setCancelable(false)
        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        simpleDialog.setContentView(R.layout.dialog_remarks)
        val dialogHeader = simpleDialog.findViewById(R.id.dialog_remark_headerTV) as AppCustomTextView
        val dialog_remark = simpleDialog.findViewById(R.id.dialog_remark) as AppCustomEditText
        dialogHeader.text = AppUtils.hiFirstNameText()+"!"
        val dialogYes = simpleDialog.findViewById(R.id.dialog_remark_ok) as AppCustomTextView

        dialogYes.setOnClickListener({ view ->
            if(dialog_remark.text.toString().trim().length>0){
                simpleDialog.cancel()
                remark =  dialog_remark.text.toString().trim()
                apiCallOnClick(status,obj)
            }else{
                Toaster.msgShort(mContext,"Please enter remarks.")
            }
        })

        simpleDialog.show()
    }

    private fun apiCallOnClick(status:String,obj:Leave_list_Response) {
        var req : ApprovalRejectReqModel = ApprovalRejectReqModel()
        req.user_id=userId
        req.current_status=status
        req.Approve_User=Pref.user_id
        req.approval_date_time=AppUtils.getCurrentDateTime()
        req.applied_date_time=obj.applied_date_time
        req.approver_remarks= remark

        val repository = LeaveTypeRepoProvider.leaveTypeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.postApprovalRejectclick(req)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            if (response.status == NetworkConstant.SUCCESS) {
                                if(status.equals("REJECT")){
                                    apiCallOnClearAttenReject(status,obj)
                                }else{
                                    getDeviceTokerOfAppliedUser(status)
                                }

                       /*         Handler().postDelayed(Runnable {
                                    progress_wheel.stopSpinning()
                                    getApprovalPendingList()
                                }, 3500)*/
                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }




    private fun apiCallOnClearAttenReject(status:String,obj:Leave_list_Response) {
        var req : clearAttendanceonRejectReqModelRejectReqModel = clearAttendanceonRejectReqModelRejectReqModel()
        req.user_id=userId
        req.leave_apply_date=AppUtils.getFormatedDateNew(obj.from_date,"dd-mm-yyyy","yyyy-mm-dd")
        req.isOnLeave=true


        val repository = LeaveTypeRepoProvider.leaveTypeListRepoProvider()
        progress_wheel.spin()
        BaseActivity.compositeDisposable.add(
                repository.clearAttendanceonRejectclick(req)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            if (response.status == NetworkConstant.SUCCESS) {
                                getDeviceTokerOfAppliedUser(status)

                            }
                        }, { error ->
                            progress_wheel.stopSpinning()
                            (mContext as DashboardActivity).showSnackMessage("ERROR")
                        })
        )
    }


    private fun getDeviceTokerOfAppliedUser(leave_status:String){
        try{
            val repository = AddAttendenceRepoProvider.addAttendenceRepo()
            BaseActivity.compositeDisposable.add(
                    repository.getReportToFCMInfo(userId,Pref.session_token.toString())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                            .subscribe({ result ->
                                val response = result as GetReportToFCMResponse

                                if (response.status == NetworkConstant.SUCCESS) {
                                    sendLeaveApprRejctFCMNotiUser(response.device_token!!,leave_status)
                                }

                            }, { error ->
                                XLog.d("Apply Leave Response ERROR=========> " + error.message)
                                BaseActivity.isApiInitiated = false
                                progress_wheel.stopSpinning()
                                (mContext as DashboardActivity).showSnackMessage(getString(R.string.something_went_wrong))
                            })
            )
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }

    private fun sendLeaveApprRejctFCMNotiUser(user_fcmToken:String,leave_status:String){
        if (user_fcmToken != "") {
            try {
                val jsonObject = JSONObject()
                val notificationBody = JSONObject()
                notificationBody.put("body", "Leave Status : "+leave_status)
                notificationBody.put("flag", "flag_status")
                notificationBody.put(    "applied_user_id",userId)
                jsonObject.put("data", notificationBody)
                val jsonArray = JSONArray()
                jsonArray.put(0, user_fcmToken)
                jsonObject.put("registration_ids", jsonArray)
                sendCustomNotification(jsonObject,leave_status)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    fun sendCustomNotification(notification: JSONObject,leave_status:String) {
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                object : Response.Listener<JSONObject?> {
                    override fun onResponse(response: JSONObject?) {

                        val simpleDialog = Dialog(mContext)
                        simpleDialog.setCancelable(false)
                        simpleDialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        simpleDialog.setContentView(R.layout.dialog_message)
                        val dialogHeader = simpleDialog.findViewById(R.id.dialog_message_header_TV) as AppCustomTextView
                        val dialog_yes_no_headerTV = simpleDialog.findViewById(R.id.dialog_message_headerTV) as AppCustomTextView
                        dialog_yes_no_headerTV.text = AppUtils.hiFirstNameText()+"!"

                        var msg=""
                        if(leave_status.equals("APPROVE")){
                            msg="APPROVED"
                        }else{
                            msg="REJECTED"
                        }
                        dialogHeader.text = "Leave status updated as : "+msg

                        val dialogYes = simpleDialog.findViewById(R.id.tv_message_ok) as AppCustomTextView
                        dialogYes.setOnClickListener({ view ->
                            simpleDialog.cancel()
                            Handler(Looper.getMainLooper()).postDelayed({
                                (mContext as DashboardActivity).onBackPressed()
                            }, 500)
                        })
                        simpleDialog.show()

                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError?) {

                    }
                }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = getString(R.string.firebase_key)
                params["Content-Type"] = "application/json"
                return params
            }
        }

        MySingleton.getInstance(mContext)!!.addToRequestQueue(jsonObjectRequest)
    }


    override fun onClick(v: View?) {
        when(v?.id){

        }
    }
}