package com.kcteam.features.addAttendence.api.addattendenceapi

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.addAttendence.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Saikat on 05-09-2018.
 */
interface AddAttendenceApi {

    //@FormUrlEncoded
    //@POST("ShopAttendance/AddAttendance")
    @POST("ShopAttendance/AttendanceSubmit")
    fun addAttendence(@Body/*("session_token")*/ addAttendenceModel: AddAttendenceInpuModel): Observable<BaseResponse>

    @POST("LeaveApproval/Records")
    fun sendLeaveApproval(@Body sendLeaveApprovalInputParams: SendLeaveApprovalInputParams): Observable<BaseResponse>

    @Multipart
    @POST("kcteamAttendance/AddAttendanceImage")
    fun attendenceWithImage(@Query("data") addAttendence: String, @Part logo_img_data: MultipartBody.Part?): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Worktypes/UpdateWorkType")
    fun updateWorkType(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("work_type") work_type: String,
                       @Field("work_desc") work_desc: String, @Field("distributor_name") distributor_name: String,
                       @Field("market_worked") market_worked: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Leave/GetLeaveList")
    fun leaveList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("from_date") from_date: String,
                       @Field("to_date") to_date: String): Observable<LeaveListResponseModel>

    @FormUrlEncoded
    @POST("UserHierarchy/UserReportToInfo")
    fun getReportToUserIDAPI(@Field("user_id") user_id: String,@Field("session_token") session_token: String): Observable<GetReportToResponse>

    @FormUrlEncoded
    @POST("Devicetoken/UserDeviceTokenInfo")
    fun getReportToFCMInfoAPI(@Field("user_id") user_id: String,@Field("session_token") session_token: String): Observable<GetReportToFCMResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): AddAttendenceApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(AddAttendenceApi::class.java)
        }

        fun approveLeave(): AddAttendenceApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(AddAttendenceApi::class.java)
        }

        fun sendAttendanceImg(): AddAttendenceApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(AddAttendenceApi::class.java)
        }
    }
}