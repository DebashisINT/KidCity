package com.kcteam.features.survey.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.photoReg.api.GetUserListPhotoRegApi
import com.kcteam.features.photoReg.model.DeleteUserPicResponse
import com.kcteam.features.photoReg.model.ImageResponse
import com.kcteam.features.survey.*
import com.kcteam.features.viewAllOrder.api.addorder.AddOrderApi
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SurveyDataApi {

    @FormUrlEncoded
    @POST("SurveyQuestionAnswer/QuestionList")
    fun getQ(@Field("session_token") session_token: String): Observable<QaListResponseModel>

    @POST("SurveyQuestionAnswer/AnswerListSave")
    fun getSubmitQ(@Body adddata: SaveQAModel?): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("SurveyQuestionAnswer/QuestionAnswerDetailsList")
    fun getView(@Field("session_token") session_token: String,@Field("user_id") user_id: String,@Field("shop_id") shop_id: String): Observable<viewsurveyModel>

    @FormUrlEncoded
    @POST("SurveyQAImage/SurveyQAImageDelete")
    fun getDel(@Field("session_token") session_token: String,@Field("user_id") user_id: String,@Field("survey_id") shop_id: String): Observable<BaseResponse>

    @Multipart
    @POST("SurveyQAImage/SurveyQAImageSave")
    fun subAddImage(@Query("data") face: String, @Part attachments: MultipartBody.Part?): Observable<ImageResponse>

    companion object Factory {
        fun create(): SurveyDataApi {
            val retrofit = Retrofit.Builder()
                .client(NetworkConstant.setTimeOutNoRetry())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(NetworkConstant.BASE_URL)
                .build()

            return retrofit.create(SurveyDataApi::class.java)
        }

        fun createImage(): SurveyDataApi {
            val retrofit = Retrofit.Builder()
                .client(NetworkConstant.setTimeOutNoRetry())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                .build()

            return retrofit.create(SurveyDataApi::class.java)
        }
    }
}