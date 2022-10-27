package com.kcteam.features.alarm.api.attendance_report_list_api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.alarm.model.AttendanceReportDataModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Kinsuk on 20-02-2019.
 */
interface AttendanceReportApi {
    @FormUrlEncoded
    @POST("AlarmConfig/FetchAattendance")
    fun attendanceReportResponse(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                                 @Field("date") date: String): Observable<AttendanceReportDataModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): AttendanceReportApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(AttendanceReportApi::class.java)
        }
    }


}