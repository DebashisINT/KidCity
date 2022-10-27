package com.kcteam.features.photoReg.api

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.kcteam.app.FileUtils
import com.kcteam.base.BaseResponse
import com.kcteam.features.addshop.model.AddShopRequestCompetetorImg
import com.kcteam.features.damageProduct.model.AddBreakageReqData
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.myjobs.model.WIPImageSubmit
import com.kcteam.features.photoReg.model.*
import com.kcteam.features.stockAddCurrentStock.api.ShopAddStockApi
import com.kcteam.features.stockAddCurrentStock.model.CurrentStockGetData
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class GetUserListPhotoRegRepository(val apiService : GetUserListPhotoRegApi) {

    fun getAllAadhaar(session_token: String): Observable<GetAllAadhaarResponse> {
        return apiService.getAllAadhaar(session_token)
    }

    fun getUserListApi(user_id: String, session_token: String): Observable<GetUserListResponse> {
        return apiService.getUserListApi(user_id,session_token)
    }

    fun deleteUserPicApi(user_id: String, session_token: String): Observable<DeleteUserPicResponse> {
        return apiService.deleteUserPicApi(user_id,session_token)
    }

    fun getUserFacePicUrlApi(user_id: String, session_token: String): Observable<UserFacePicUrlResponse> {
        return apiService.getUserFacePic(user_id,session_token)
    }

    fun sendUserAadhaarApi(aadhaarSubmitData:AadhaarSubmitData): Observable<BaseResponse> {
        return apiService.submitAadhaarDetailsSingle(aadhaarSubmitData)
    }


    fun addUserFaceRegImg(obj: UserPhotoRegModel, user_image: String?, context: Context,user_contactid:String?): Observable<FaceRegResponse> {
        var profile_img_data: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(user_image)){
            val profile_img_file = FileUtils.getFile(context, Uri.parse(user_image))
            if (profile_img_file != null && profile_img_file.exists()) {
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
                profile_img_data = MultipartBody.Part.createFormData("attachments", profile_img_file.name.replaceAfter("cropped",user_contactid.toString()).replace("cropped","")+".jpg", profileImgBody)
            }
        }


        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(obj)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return  apiService.getAddUserFaceImage(jsonInString, profile_img_data)
    }

    fun addImgwithdata(obj: AddBreakageReqData, user_image: String?, context: Context, user_contactid:String?): Observable<ImageResponse> {
        var profile_img_data: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(user_image)){
            //val profile_img_file = FileUtils.getFile(context, Uri.parse(user_image))
            val profile_img_file = File(user_image)
            if (profile_img_file != null && profile_img_file.exists()) {
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
                profile_img_data = MultipartBody.Part.createFormData("attachments", profile_img_file.name.replaceAfter("cropped",user_contactid.toString()).replace("cropped","")+".jpg", profileImgBody)
            }
        }


        var jsonInString = ""
        try {
            jsonInString = Gson().toJson(obj)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return  apiService.subAddImage(jsonInString, profile_img_data)
    }


    fun submitAadhaarDetails(aadhaarSubmitData: AadhaarSubmitData, wipImageSubmitList: ArrayList<WIPImageSubmit>, context: Context?): Observable<BaseResponse> {
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

            val imageName = aadhaarSubmitData.aadhaar_holder_user_id + aadhaarSubmitData.date
            val fileName = imageName + item.type + "_" + System.currentTimeMillis() + "." + fileExt

            Log.e("Work Reschedule Image", "File Name=========> $fileName")

            profile_img_data = MultipartBody.Part.createFormData("attachments", fileName, attachmentBody)
            multiPartArray.add(profile_img_data)
        }


        //var shopObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(aadhaarSubmitData)
            //  shopObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return apiService.submitAadhaarDetails(jsonInString, multiPartArray)
    }

}