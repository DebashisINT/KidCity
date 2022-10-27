package com.kcteam.features.dashboard.presentation.getcontentlisapi

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.dashboard.presentation.model.ContentListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 05-03-2019.
 */
interface GetContentListApi {
    @FormUrlEncoded
    @POST("Content/GetList")
    fun getContentList(@Field("session_token") session_token: String): Observable<ContentListResponseModel>

    @FormUrlEncoded
    @POST("UserLogin/ChangePass")
    fun changePassword(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("old_pwd") old_pwd: String,
                       @Field("new_pwd") new_pwd: String): Observable<BaseResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): GetContentListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(GetContentListApi::class.java)
        }
    }
}