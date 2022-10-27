package com.kcteam.features.addAttendence.api.addattendenceapi

import android.content.Context
import android.net.Uri
import com.kcteam.app.FileUtils
import com.kcteam.app.Pref
import com.kcteam.base.BaseResponse
import com.kcteam.features.addAttendence.model.*
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationFuzedService
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by Saikat on 05-09-2018.
 */
class AddAttendenceRepo(val apiService: AddAttendenceApi) {
    fun addAttendence(addAttendenceModel: AddAttendenceInpuModel): Observable<BaseResponse> {
        return apiService.addAttendence(addAttendenceModel)
    }

    fun sendLeaveApproval(sendLeaveApprovalInputParams: SendLeaveApprovalInputParams): Observable<BaseResponse> {
        return apiService.sendLeaveApproval(sendLeaveApprovalInputParams)
    }

    fun attendanceWithImage(image: String, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null

        val profile_img_file = File(image) //FileUtils.getFile(context, Uri.parse(image))
        if (profile_img_file != null && profile_img_file.exists()) {
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            profile_img_data = MultipartBody.Part.createFormData("image", profile_img_file.name, profileImgBody)
        } else {
            var mFile: File? = null
            if (context is DashboardActivity)
                mFile = (context as DashboardActivity).getShopDummyImageFile()
            else
                mFile = (context as LocationFuzedService).getShopDummyImageFile()
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
            profile_img_data = MultipartBody.Part.createFormData("image", mFile.name, profileImgBody)
        }

        val attendanceImg = AddAttendenceImageInput()
        attendanceImg.session_token = Pref.session_token!!
        attendanceImg.user_id = Pref.user_id!!

        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(attendanceImg)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return apiService.attendenceWithImage(jsonInString, profile_img_data)
        // return apiService.getAddShopWithoutImage(jsonInString)
    }

    fun updateWorkType(workType: String, workDesc: String, distributor_name: String, market_worked: String): Observable<BaseResponse> {
        return apiService.updateWorkType(Pref.session_token!!, Pref.user_id!!, workType, workDesc, distributor_name, market_worked)
    }

    fun leaveList(fromDate: String, toDate: String): Observable<LeaveListResponseModel> {
        return apiService.leaveList(Pref.session_token!!, Pref.user_id!!, fromDate, toDate)
    }

    fun getReportToUserID(user_id: String, session_token: String): Observable<GetReportToResponse> {
        return apiService.getReportToUserIDAPI(user_id,session_token)
    }

    fun getReportToFCMInfo(user_id: String, session_token: String): Observable<GetReportToFCMResponse> {
        return apiService.getReportToFCMInfoAPI(user_id,session_token)
    }

}