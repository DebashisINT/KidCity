package com.kcteam.features.dymanicSection.api

import android.content.Context
import android.net.Uri
import com.kcteam.app.FileUtils
import com.kcteam.app.Pref
import com.kcteam.base.BaseResponse
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.dymanicSection.model.*
import com.fasterxml.jackson.databind.ObjectMapper
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by Saikat on 19-Aug-20.
 */
class DynamicRepo(val apiService: DynamicApi) {
    fun getDynamicData(id: String): Observable<DynamicResponseModel> {
        return apiService.getDynamicData(Pref.session_token!!, Pref.user_id!!, id)
    }

    fun saveDynamicData(dynamic: DynamicSubmitInputParams): Observable<BaseResponse> {
        return apiService.saveDynamicData(dynamic)
    }

    fun addDynamicDataMultipart(dynamic: DynamicSubmitInputParams, file_link: String, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null

        var profile_img_file: File? = null

        if (file_link.startsWith("file"))
            profile_img_file = FileUtils.getFile(context, Uri.parse(file_link))
        else {
            profile_img_file = File(file_link)

            if (!profile_img_file?.exists()) {
                profile_img_file?.createNewFile()
            }
        }

        if (profile_img_file != null && profile_img_file.exists()) {
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            profile_img_data = MultipartBody.Part.createFormData("attachments", profile_img_file.name, profileImgBody)
        } else {
            var mFile: File
            mFile = (context as DashboardActivity).getShopDummyImageFile()
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
            profile_img_data = MultipartBody.Part.createFormData("attachments", mFile.name, profileImgBody)
        }

        //var dynamicObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(dynamic)
            //dynamicObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
            //shopObject = RequestBody.create(MediaType.parse("text/plain"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        //return apiService.addBillWithImage(jsonInString, profile_img_data)
        return apiService.saveDynamicData(jsonInString!!, profile_img_data)
    }

    fun getAllDynamicList(): Observable<AllDynamicListResponseModel> {
        return apiService.getDynamicAllList(Pref.session_token!!, Pref.user_id!!, Pref.dynamicFormName)
    }

    fun getDynamicListData(id: String): Observable<DynamicListResponseModel> {
        return apiService.getDynamicList(Pref.session_token!!, Pref.user_id!!, Pref.dynamicFormName, id)
    }

    fun editDynamicData(dynamic: EditDynamicInputParams): Observable<BaseResponse> {
        return apiService.editDynamicData(dynamic)
    }

    fun editDynamicDataMultipart(dynamic: DynamicSubmitInputParams, file_link: String, context: Context): Observable<BaseResponse> {
        var profile_img_data: MultipartBody.Part? = null

        var profile_img_file: File? = null

        if (file_link.startsWith("file"))
            profile_img_file = FileUtils.getFile(context, Uri.parse(file_link))
        else {
            profile_img_file = File(file_link)

            if (!profile_img_file?.exists()) {
                profile_img_file?.createNewFile()
            }
        }

        if (profile_img_file != null && profile_img_file.exists()) {
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
            profile_img_data = MultipartBody.Part.createFormData("attachments", profile_img_file.name, profileImgBody)
        } else {
            var mFile: File
            mFile = (context as DashboardActivity).getShopDummyImageFile()
            val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), mFile)
            profile_img_data = MultipartBody.Part.createFormData("attachments", mFile.name, profileImgBody)
        }

        //var dynamicObject: RequestBody? = null
        var jsonInString = ""
        try {
            jsonInString = ObjectMapper().writeValueAsString(dynamic)
            //dynamicObject = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonInString)
            //shopObject = RequestBody.create(MediaType.parse("text/plain"), jsonInString)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        //return apiService.addBillWithImage(jsonInString, profile_img_data)
        return apiService.editDynamicData(jsonInString!!, profile_img_data)
    }

    fun getEditDynamicData(dynamicListModel: DynamicListDataModel?): Observable<DynamicResponseModel> {
        return apiService.getEditDynamicData(Pref.session_token!!, Pref.user_id!!, dynamicListModel?.super_id!!, dynamicListModel?.id!!)
    }
}