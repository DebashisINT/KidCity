package com.kcteam.features.dashboard.presentation.api.unreadnotificationapi

import com.kcteam.app.NetworkConstant
import com.kcteam.features.dashboard.presentation.model.UnreadNotificationResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 07-03-2019.
 */
interface UnreadNotificationApi {
    @FormUrlEncoded
    @POST("Notification/GetNotificationUnread")
    fun unreadNotification(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<UnreadNotificationResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): UnreadNotificationApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(UnreadNotificationApi::class.java)
        }
    }
}