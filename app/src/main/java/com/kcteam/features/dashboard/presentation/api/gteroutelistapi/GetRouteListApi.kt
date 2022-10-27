package com.kcteam.features.dashboard.presentation.api.gteroutelistapi

import com.kcteam.app.NetworkConstant
import com.kcteam.features.dashboard.presentation.model.SelectedRouteListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 03-12-2018.
 */
interface GetRouteListApi {
    @FormUrlEncoded
    @POST("RouteList/ListofRoutes")
    fun getRouteList(@Field("session_token") session_token: String, @Field("UserID") user_id: String): Observable<SelectedRouteListResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): GetRouteListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(GetRouteListApi::class.java)
        }
    }
}