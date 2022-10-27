package com.kcteam.features.activities.api

import android.content.Context
import android.net.Uri
import android.util.Log
import com.kcteam.app.FileUtils
import com.kcteam.app.Pref
import com.kcteam.base.BaseResponse
import com.kcteam.features.activities.model.*
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ActivityRepo(val apiService: ActivityApi) {
    fun activityDropdownList(): Observable<ActivityDropdownListResponseModel> {
        return apiService.getActivityDropdownList(Pref.session_token!!, Pref.user_id!!)
    }

    fun typeList(): Observable<TypeListResponseModel> {
        return apiService.getTypeList(Pref.session_token!!, Pref.user_id!!)
    }

    fun priorityList(): Observable<PriorityListResponseModel> {
        return apiService.getPriorityList(Pref.session_token!!, Pref.user_id!!)
    }

    fun addActivity(addActivity: AddActivityInputModel): Observable<BaseResponse> {
        return apiService.addActivity(addActivity)
    }

    fun addActivityWithAttachment(activity: AddActivityInputModel, imageList: ArrayList<ActivityImage>, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        for (item in imageList) {

            Thread.sleep(500)

            var profile_img_file: File? = null
            if (item.link.startsWith("file"))
                profile_img_file = FileUtils.getFile(context, Uri.parse(item.link))
            else {
                profile_img_file = File(item.link)

                if (!profile_img_file?.exists()) {
                    profile_img_file?.createNewFile()
                }
            }

            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            //val fileExt = FileUtils.getFile(context, Uri.parse(item.link)).extension //File(item.link).extension

            var fileExt = ""
            fileExt = if (item.link.startsWith("file"))
                FileUtils.getFile(context, Uri.parse(item.link)).extension
            else
                File(item.link).extension

            val imageName = activity.user_id + "~" + activity.date + "~" + activity.id + "~" + activity.party_id + "~"
            val fileName = imageName + item.type + "_" + System.currentTimeMillis() + "." + fileExt

            Log.e("Activity Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("attachments", fileName, profileImgBody)
            multiPartArray.add(profile_img_data)

        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(activity)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return apiService.addActivityMultipart(jsonInString, multiPartArray)
        // return apiService.getAddShopWithoutImage(jsonInString)
    }

    fun activityList(): Observable<ActivityListResponseModel> {
        return apiService.getActivityList(Pref.session_token!!, Pref.user_id!!)
    }

    fun uploadChemistVisit(addChemistVisit: AddChemistVisitInputModel): Observable<BaseResponse> {
        return apiService.uploadChemistVisit(addChemistVisit)
    }

    fun getChemistVisit(): Observable<ChemistVisitResponseModel> {
        return apiService.getChemistVisit(Pref.session_token!!, Pref.user_id!!)
    }

    fun uploadDoctorVisit(addDocVisit: AddDoctorVisitInputModel): Observable<BaseResponse> {
        return apiService.uploadDoctorVisit(addDocVisit)
    }

    fun getDoctortVisit(): Observable<DoctorListResponseModel> {
        return apiService.getDoctorVisit(Pref.session_token!!, Pref.user_id!!)
    }
}