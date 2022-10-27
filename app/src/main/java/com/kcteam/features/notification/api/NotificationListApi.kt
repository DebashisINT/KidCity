package com.kcteam.features.notification.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.notification.model.NotificationListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 06-03-2019.
 */
interface NotificationListApi {
    @FormUrlEncoded
    @POST("Notification/GetNotification")
    fun notificationList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<NotificationListResponseModel>

    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): NotificationListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(NotificationListApi::class.java)
        }
    }
}