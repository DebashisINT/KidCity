package com.kcteam.features.myjobs.api

import android.content.Context
import android.net.Uri
import android.util.Log
import com.kcteam.app.FileUtils
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.features.myjobs.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class MyJobRepo(val apiService: MyJobApi) {

    fun getCustomerListDateWise(date: String,user_id:String): Observable<CustomerListResponseModel> {
        return apiService.getCustomerListDateWise(Pref.session_token!!, user_id, date)
    }

    fun getCustomerList(): Observable<CustListResponseModel> {
        return apiService.getCustomerList(Pref.session_token!!, Pref.user_id!!)
    }

    fun getStatus(user_id:String,id: String): Observable<CustomerStatusResponseModel> {
        //return apiService.getStatus(Pref.session_token!!, Pref.user_id!!, id)
        return apiService.getStatus(Pref.session_token!!, user_id, id)
    }

    fun getWipSettings(id: String): Observable<CustomerWIPStatusModel> {
        return apiService.getWipSettings(Pref.session_token!!, Pref.user_id!!, id)
    }

    fun submitWIP(wipRequestData: WIPSubmit): Observable<BaseResponse> {
        return apiService.submitWIP(wipRequestData)
    }

    fun submitWIP(wipRequestData: WIPSubmit, wipImageSubmitList: ArrayList<WIPImageSubmit>, context: Context?): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        for (item in wipImageSubmitList) {
            Thread.sleep(500)

            var attachment: File? = null
            if (item.link.startsWith("file"))
                attachment = FileUtils.getFile(context, Uri.parse(item.link))
            else {
                attachment = File(item.link)

                if (!attachment?.exists()) {
                    attachment?.createNewFile()
                }
            }

            val attachmentBody = RequestBody.create(MediaType.parse("multipart/form-data"), attachment)
            //val fileExt = FileUtils.getFile(context, Uri.parse(item.link)).extension //File(item.link).extension

            var fileExt = ""
            fileExt = if (item.link.startsWith("file"))
                FileUtils.getFile(context, Uri.parse(item.link)).extension
            else
                File(item.link).extension

            val imageName = wipRequestData.user_id + "~" + wipRequestData.start_date + "~" + wipRequestData.job_id + "~"
            val fileName = imageName + item.type + "_" + System.currentTimeMillis() + "." + fileExt

            Log.e("WIP Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("attachments", fileName, attachmentBody)
            multiPartArray.add(profile_img_data)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(wipRequestData)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return apiService.wipSubmitMultipart(jsonInString, multiPartArray)
    }

    fun submitWOH(woh: WorkOnHoldInputParams): Observable<BaseResponse> {
        return apiService.submitWOH(woh)
    }

    fun submitWOH(woh: WorkOnHoldInputParams, wipImageSubmitList: ArrayList<WIPImageSubmit>, context: Context?): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        for (item in wipImageSubmitList) {
            Thread.sleep(500)

            var attachment: File? = null
            if (item.link.startsWith("file"))
                attachment = FileUtils.getFile(context, Uri.parse(item.link))
            else {
                attachment = File(item.link)

                if (!attachment?.exists()) {
                    attachment?.createNewFile()
                }
            }

            val attachmentBody = RequestBody.create(MediaType.parse("multipart/form-data"), attachment)
            //val fileExt = FileUtils.getFile(context, Uri.parse(item.link)).extension //File(item.link).extension

            var fileExt = ""
            fileExt = if (item.link.startsWith("file"))
                FileUtils.getFile(context, Uri.parse(item.link)).extension
            else
                File(item.link).extension

            val imageName = woh.user_id + "~" + woh.hold_date + "~" + woh.job_id + "~"
            val fileName = imageName + item.type + "_" + System.currentTimeMillis() + "." + fileExt

            Log.e("WOH Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("attachments", fileName, attachmentBody)
            multiPartArray.add(profile_img_data)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(woh)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return apiService.wohSubmitMultipart(jsonInString, multiPartArray)
    }

    fun submitWorkCompleted(workCompleted: WorkCompletedInputParams): Observable<BaseResponse> {
        return apiService.submitWorkCompleted(workCompleted)
    }

    fun submitWorkCompleted(workCompleted: WorkCompletedInputParams, wipImageSubmitList: ArrayList<WIPImageSubmit>, context: Context?): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        for (item in wipImageSubmitList) {
            Thread.sleep(500)

            var attachment: File? = null
            if (item.link.startsWith("file"))
                attachment = FileUtils.getFile(context, Uri.parse(item.link))
            else {
                attachment = File(item.link)

                if (!attachment?.exists()) {
                    attachment?.createNewFile()
                }
            }

            val attachmentBody = RequestBody.create(MediaType.parse("multipart/form-data"), attachment)
            //val fileExt = FileUtils.getFile(context, Uri.parse(item.link)).extension //File(item.link).extension

            var fileExt = ""
            fileExt = if (item.link.startsWith("file"))
                FileUtils.getFile(context, Uri.parse(item.link)).extension
            else
                File(item.link).extension

            val imageName = workCompleted.user_id + "~" + workCompleted.finish_date + "~" + workCompleted.job_id + "~"
            val fileName = imageName + item.type + "_" + System.currentTimeMillis() + "." + fileExt

            Log.e("Work Completed Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("attachments", fileName, attachmentBody)
            multiPartArray.add(profile_img_data)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(workCompleted)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return apiService.workCompletedSubmitMultipart(jsonInString, multiPartArray)
    }

    fun submitWorkCancelled(workCancel: WorkCancelledInputParams): Observable<BaseResponse> {
        return apiService.submitWorkCancelled(workCancel)
    }

    fun submitWorkCancelled(workCancel: WorkCancelledInputParams, wipImageSubmitList: ArrayList<WIPImageSubmit>, context: Context?): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        for (item in wipImageSubmitList) {
            Thread.sleep(500)

            var attachment: File? = null
            if (item.link.startsWith("file"))
                attachment = FileUtils.getFile(context, Uri.parse(item.link))
            else {
                attachment = File(item.link)

                if (!attachment?.exists()) {
                    attachment?.createNewFile()
                }
            }

            val attachmentBody = RequestBody.create(MediaType.parse("multipart/form-data"), attachment)
            //val fileExt = FileUtils.getFile(context, Uri.parse(item.link)).extension //File(item.link).extension

            var fileExt = ""
            fileExt = if (item.link.startsWith("file"))
                FileUtils.getFile(context, Uri.parse(item.link)).extension
            else
                File(item.link).extension

            val imageName = workCancel.user_id + "~" + workCancel.date + "~" + workCancel.job_id + "~"
            val fileName = imageName + item.type + "_" + System.currentTimeMillis() + "." + fileExt

            Log.e("Work Cancelled Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("attachments", fileName, attachmentBody)
            multiPartArray.add(profile_img_data)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(workCancel)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return apiService.workCancelledSubmitMultipart(jsonInString, multiPartArray)
    }

    fun updateReview(updateReview: UpdateReviewInputParams): Observable<BaseResponse> {
        return apiService.updateReview(updateReview)
    }

    fun updateReview(updateReview: UpdateReviewInputParams, wipImageSubmitList: ArrayList<WIPImageSubmit>, context: Context?): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        for (item in wipImageSubmitList) {
            Thread.sleep(500)

            var attachment: File? = null
            if (item.link.startsWith("file"))
                attachment = FileUtils.getFile(context, Uri.parse(item.link))
            else {
                attachment = File(item.link)

                if (!attachment?.exists()) {
                    attachment?.createNewFile()
                }
            }

            val attachmentBody = RequestBody.create(MediaType.parse("multipart/form-data"), attachment)
            //val fileExt = FileUtils.getFile(context, Uri.parse(item.link)).extension //File(item.link).extension

            var fileExt = ""
            fileExt = if (item.link.startsWith("file"))
                FileUtils.getFile(context, Uri.parse(item.link)).extension
            else
                File(item.link).extension

            val imageName = updateReview.user_id + "~" + AppUtils.getCurrentDateForShopActi() + "~" + updateReview.job_id + "~"
            val fileName = imageName + item.type + "_" + System.currentTimeMillis() + "." + fileExt

            Log.e("Work Cancelled Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("attachments", fileName, attachmentBody)
            multiPartArray.add(profile_img_data)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(updateReview)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return apiService.updateReviewMultipart(jsonInString, multiPartArray)
    }

    fun getWorkCompletedSettings(id: String): Observable<WorkCompletedSettingsResponseModel> {
        return apiService.getWorkCompletedSettings(Pref.session_token!!, Pref.user_id!!, id)
    }

    fun submitWorkUnhold(woh: WorkUnHoldInputParams): Observable<BaseResponse> {
        return apiService.submitWorkUnhold(woh)
    }

    fun submitWorkUnhold(woh: WorkUnHoldInputParams, wipImageSubmitList: ArrayList<WIPImageSubmit>, context: Context?): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null
        val multiPartArray: java.util.ArrayList<MultipartBody.Part> = arrayListOf()

        for (item in wipImageSubmitList) {
            Thread.sleep(500)

            var attachment: File? = null
            if (item.link.startsWith("file"))
                attachment = FileUtils.getFile(context, Uri.parse(item.link))
            else {
                attachment = File(item.link)

                if (!attachment?.exists()) {
                    attachment?.createNewFile()
                }
            }

            val attachmentBody = RequestBody.create(MediaType.parse("multipart/form-data"), attachment)
            //val fileExt = FileUtils.getFile(context, Uri.parse(item.link)).extension //File(item.link).extension

            var fileExt = ""
            fileExt = if (item.link.startsWith("file"))
                FileUtils.getFile(context, Uri.parse(item.link)).extension
            else
                File(item.link).extension

            val imageName = woh.user_id + "~" + woh.unhold_date + "~" + woh.job_id + "~"
            val fileName = imageName + item.type + "_" + System.currentTimeMillis() + "." + fileExt

            Log.e("WOH Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("attachments", fileName, attachmentBody)
            multiPartArray.add(profile_img_data)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(woh)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return apiService.workUnholdSubmitMultipart(jsonInString, multiPartArray)
    }

    fun getHistoryList(startDate: String, endDate: String): Observable<HistoryResponseModel> {
        return apiService.getHistoryList(Pref.session_token!!, Pref.user_id!!, startDate, endDate)
    }
}