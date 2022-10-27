package com.kcteam.features.myjobs.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.activities.model.AddActivityInputModel
import com.kcteam.features.myjobs.model.*
import com.kcteam.features.myprofile.api.MyProfileApi
import com.kcteam.features.nearbyshops.model.StateCityResponseModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface MyJobApi {

    @FormUrlEncoded
    @POST("JobCustomer/List")
    fun getCustomerListDateWise(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                                @Field("date") date: String): Observable<CustomerListResponseModel>

    @FormUrlEncoded
    @POST("JobCustomer/Customerlist")
    fun getCustomerList(@Field("session_token") session_token: String, @Field("user_id") user_id: String):
            Observable<CustListResponseModel>

    @FormUrlEncoded
    @POST("JobCustomer/GetStatus")
    fun getStatus(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                  @Field("id") id: String): Observable<CustomerStatusResponseModel>

    @FormUrlEncoded
    @POST("JobCustomer/GetWipSettings")
    fun getWipSettings(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("id") id: String): Observable<CustomerWIPStatusModel>

    @Multipart
    @POST("CustomerJobStatus/WorkInProgressSubmit")
    fun wipSubmitMultipart(@Query("data") data: String, @Part attachment: List<MultipartBody.Part?>): Observable<BaseResponse>

    @POST("JobCustomer/WorkInProgressSubmit")
    fun submitWIP(@Body wipSubmit: WIPSubmit): Observable<BaseResponse>

    @Multipart
    @POST("CustomerJobStatus/WorkOnHoldSubmit")
    fun wohSubmitMultipart(@Query("data") data: String, @Part attachment: List<MultipartBody.Part?>): Observable<BaseResponse>

    @POST("JobCustomer/WorkOnHoldSubmit")
    fun submitWOH(@Body wohSubmit: WorkOnHoldInputParams): Observable<BaseResponse>

    @Multipart
    @POST("CustomerJobStatus/WorkOnCompletedSubmit")
    fun workCompletedSubmitMultipart(@Query("data") data: String, @Part attachment: List<MultipartBody.Part?>): Observable<BaseResponse>

    @POST("JobCustomer/WorkOnCompletedSubmit")
    fun submitWorkCompleted(@Body workComplete: WorkCompletedInputParams): Observable<BaseResponse>

    @Multipart
    @POST("CustomerJobStatus/WorkCancelledSubmit")
    fun workCancelledSubmitMultipart(@Query("data") data: String, @Part attachment: List<MultipartBody.Part?>): Observable<BaseResponse>

    @POST("JobCustomer/WorkCancelledSubmit")
    fun submitWorkCancelled(@Body workCancel: WorkCancelledInputParams): Observable<BaseResponse>

    @Multipart
    @POST("CustomerJobStatus/UpdateReview")
    fun updateReviewMultipart(@Query("data") data: String, @Part attachment: List<MultipartBody.Part?>): Observable<BaseResponse>

    @POST("JobCustomer/UpdateReview")
    fun updateReview(@Body updateReview: UpdateReviewInputParams): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("JobCustomer/WorkCompletedSetiings")
    fun getWorkCompletedSettings(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                                 @Field("id") id: String): Observable<WorkCompletedSettingsResponseModel>

    @Multipart
    @POST("CustomerJobStatus/SubmitWorkUnhold")
    fun workUnholdSubmitMultipart(@Query("data") data: String, @Part attachment: List<MultipartBody.Part?>): Observable<BaseResponse>

    @POST("JobCustomer/SubmitWorkUnhold")
    fun submitWorkUnhold(@Body workUnhold: WorkUnHoldInputParams): Observable<BaseResponse>


    @FormUrlEncoded
    @POST("JobCustomer/JobhistoryList")
    fun getHistoryList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("start_date") start_date: String, @Field("end_date") end_date: String): Observable<HistoryResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun createMultiPart(): MyJobApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(MyJobApi::class.java)
        }

        fun create(): MyJobApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(MyJobApi::class.java)
        }
    }
}