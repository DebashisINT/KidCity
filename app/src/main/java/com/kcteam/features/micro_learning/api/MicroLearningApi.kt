package com.kcteam.features.micro_learning.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.micro_learning.model.MicroLearningResponseModel
import com.kcteam.features.task.api.TaskApi
import com.kcteam.features.task.model.TaskListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MicroLearningApi {

    @FormUrlEncoded
    @POST("Microlearning/List")
    fun microLearningList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<MicroLearningResponseModel>

    @FormUrlEncoded
    @POST("Microlearning/UpdateVideoPosition")
    fun updateVideoPosition(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("id") id: String,
                            @Field("current_window") current_window: String, @Field("play_back_position") play_back_position: String,
                            @Field("play_when_ready") play_when_ready: Boolean, @Field("percentage") percentage: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Microlearning/UpdateNote")
    fun updateNote(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("id") id: String,
                   @Field("note") note: String, @Field("date_time") date_time: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Microlearning/UpdateView")
    fun updateFileOpeningTime(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("id") id: String,
                              @Field("open_date_time") open_date_time: String, @Field("close_date_time") close_date_time: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Microlearning/DownloadHiostory")
    fun updateDownloadStatus(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("id") id: String,
                              @Field("isDownloaded") isDownloaded: Boolean): Observable<BaseResponse>


    companion object Factory {
        fun create(): MicroLearningApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(MicroLearningApi::class.java)
        }
    }
}