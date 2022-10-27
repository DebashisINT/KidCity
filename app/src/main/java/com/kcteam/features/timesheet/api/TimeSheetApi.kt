package com.kcteam.features.timesheet.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.timesheet.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Saikat on 29-Apr-20.
 */
interface TimeSheetApi {

    @FormUrlEncoded
    @POST("TimeSheet/TimeSheetList")
    fun getTimeSheetList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                    @Field("date") date: String): Observable<TimeSheetListResponseModel>

    @FormUrlEncoded
    @POST("TimeSheet/DeleteTimeSheet")
    fun deleteTimeSheet(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                         @Field("timesheet_id") timesheet_id: String): Observable<EditDeleteTimesheetResposneModel>

    @FormUrlEncoded
    @POST("TimeSheet/GetTimeSheetConfig")
    fun timeSheetConfig(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                        @Field("isAdd") isAdd: Boolean): Observable<TimeSheetConfigResponseModel>

    @FormUrlEncoded
    @POST("TimeSheet/GetDropDown")
    fun getTimeSheetDropdownData(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<TimeSheetDropDownResponseModel>

    @POST("TimeSheet/SaveTimeSheet")
    fun addTimesheet(@Body addTimesheet: AddTimeSheetInputModel): Observable<BaseResponse>

    @POST("TimeSheet/UpdateTimeSheet")
    fun editTimesheet(@Body editTimesheet: EditTimeSheetInputModel): Observable<EditDeleteTimesheetResposneModel>

    @Multipart
    @POST("TimeSheetImage/SaveTimeSheet")
    fun addTimesheetWithImage(@Query("data") timesheet: String, @Part logo_img_data: MultipartBody.Part?): Observable<BaseResponse>

    @Multipart
    @POST("TimeSheetImage/UpdateTimeSheet")
    fun editTimesheetWithImage(@Query("data") timesheet: String, @Part logo_img_data: MultipartBody.Part?): Observable<EditDeleteTimesheetResposneModel>


    companion object Factory {
        fun create(): TimeSheetApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(TimeSheetApi::class.java)
        }

        fun createImage(): TimeSheetApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(TimeSheetApi::class.java)
        }
    }
}