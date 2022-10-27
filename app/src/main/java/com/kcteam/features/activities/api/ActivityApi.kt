package com.kcteam.features.activities.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.activities.model.*
import com.kcteam.features.member.api.TeamApi
import com.kcteam.features.member.model.TeamListResponseModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ActivityApi {

    @FormUrlEncoded
    @POST("Activity/ActivityDropdownList")
    fun getActivityDropdownList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<ActivityDropdownListResponseModel>

    @FormUrlEncoded
    @POST("Activity/ActivityTypeList")
    fun getTypeList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<TypeListResponseModel>

    @FormUrlEncoded
    @POST("Activity/ActivityPriorityList")
    fun getPriorityList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<PriorityListResponseModel>

    @POST("Activity/ActivityAdd")
    fun addActivity(@Body addActivity: AddActivityInputModel): Observable<BaseResponse>

    @Multipart
    @POST("ActivityMultipart/AddActivity")
    fun addActivityMultipart(@Query("data") activity: String, @Part attachment: List<MultipartBody.Part?>): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Activity/ActivityList")
    fun getActivityList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<ActivityListResponseModel>

    @POST("ChemistActivity/PutChemistActivity")
    fun uploadChemistVisit(@Body addChemistVisit: AddChemistVisitInputModel): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("ChemistActivity/GetChemistActivityList")
    fun getChemistVisit(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<ChemistVisitResponseModel>

    @POST("DoctorActivity/PutDoctorActivity")
    fun uploadDoctorVisit(@Body addDoctorVisit: AddDoctorVisitInputModel): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("DoctorActivity/GetDoctorActivityList")
    fun getDoctorVisit(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<DoctorListResponseModel>


    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): ActivityApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ActivityApi::class.java)
        }

        fun createImage(): ActivityApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(ActivityApi::class.java)
        }
    }
}