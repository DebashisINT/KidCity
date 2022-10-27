package com.kcteam.features.dymanicSection.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.billing.api.AddBillingApi
import com.kcteam.features.dailyPlan.api.PlanApi
import com.kcteam.features.dailyPlan.model.AllPlanListResponseModel
import com.kcteam.features.dymanicSection.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Saikat on 19-Aug-20.
 */
interface DynamicApi {

    @FormUrlEncoded
    @POST("DynamicForm/GetForm")
    fun getDynamicData(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("dynamicFormName") dynamicFormName: String): Observable<DynamicResponseModel>

    @POST("DynamicForm/SaveForm")
    fun saveDynamicData(@Body dynamic: DynamicSubmitInputParams): Observable<BaseResponse>

    @Multipart
    @POST("DynamicFormAtt/SaveFormWithAttachment")
    fun saveDynamicData(@Query("data") dynamicData: /*RequestBody*/ String, @Part file: MultipartBody.Part?): Observable<BaseResponse>


    @FormUrlEncoded
    @POST("DynamicForm/GetFormList")
    fun getDynamicAllList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("dynamicFormName") dynamicFormName: String): Observable<AllDynamicListResponseModel>

    @FormUrlEncoded
    @POST("DynamicForm/GetFormWiseList")
    fun getDynamicList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("dynamicFormName") dynamicFormName: String, @Field("id") id: String): Observable<DynamicListResponseModel>

    @POST("DynamicForm/SaveFormEdit")
    fun editDynamicData(@Body dynamic: EditDynamicInputParams): Observable<BaseResponse>

    @Multipart
    @POST("DynamicFormAtt/SaveFormWithAttachmentEdit")
    fun editDynamicData(@Query("data") dynamicData: /*RequestBody*/ String, @Part file: MultipartBody.Part?): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("DynamicForm/GetFormEdit")
    fun getEditDynamicData(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("dynamicFormName") dynamicFormName: String, @Field("id") id: String): Observable<DynamicResponseModel>


    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): DynamicApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(DynamicApi::class.java)
        }

        fun createImage(): DynamicApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(DynamicApi::class.java)
        }
    }
}