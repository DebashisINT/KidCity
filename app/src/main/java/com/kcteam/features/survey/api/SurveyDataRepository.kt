package com.kcteam.features.survey.api


import android.content.Context
import android.text.TextUtils
import com.kcteam.base.BaseResponse
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.damageProduct.model.AddBreakageReqData
import com.kcteam.features.photoReg.model.ImageResponse
import com.kcteam.features.survey.*
import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class SurveyDataRepository(val apiService : SurveyDataApi) {

    fun provideSurveyQApi(session_token: String): Observable<QaListResponseModel> {
        return apiService.getQ(session_token)
    }

    fun provideSurveySubmitApi(data: SaveQAModel): Observable<BaseResponse> {
        return apiService.getSubmitQ(data)
    }

    fun provideSurveyViewApi(session_token: String,user_id: String,shop_id:String): Observable<viewsurveyModel> {
        return apiService.getView(session_token,user_id,shop_id)
    }

    fun provideSurveyDelApi(session_token: String,user_id: String,survey_id: String): Observable<BaseResponse> {
        return apiService.getDel(session_token,user_id,survey_id)
    }

    fun addImgwithdata(obj: SurveyQAIMGModel, user_image: String?, user_contactid:String?): Observable<ImageResponse> {
        var profile_img_data: MultipartBody.Part? = null
        if (!TextUtils.isEmpty(user_image)){
            //val profile_img_file = FileUtils.getFile(context, Uri.parse(user_image))
            val profile_img_file = File(user_image)
            if (profile_img_file != null && profile_img_file.exists()) {
                val profileImgBody = RequestBody.create(MediaType.parse("multipart/form-data"), profile_img_file)
                profile_img_data = MultipartBody.Part.createFormData("attachments", profile_img_file.name.replaceAfter("cropped",user_contactid.toString()).replace("cropped",""), profileImgBody)
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


}