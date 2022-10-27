package com.kcteam.features.meetinglist.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.meetinglist.model.MeetingListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 21-01-2020.
 */
interface MeetingApi {

    @FormUrlEncoded
    @POST("Meeting/GetMeetingList")
    fun getMeetingList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<MeetingListResponseModel>

    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): MeetingApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(MeetingApi::class.java)
        }
    }
}