package com.kcteam.features.member.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.addshop.model.AreaListResponseModel
import com.kcteam.features.member.model.*
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 29-01-2020.
 */
interface TeamApi {

    @FormUrlEncoded
    @POST("UserHierarchy/HierarchyMemberList")
    fun getTeamList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                    @Field("isFirstScreen") isFirstScreen: Boolean, @Field("isAllTeam") isAllTeam: Boolean): Observable<TeamListResponseModel>

    @FormUrlEncoded
    @POST("UserHierarchy/HierarchyShopList")
    fun getTeamShopList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("area_id") area_id: String): Observable<TeamShopListResponseModel>

    @FormUrlEncoded
    @POST("UserHierarchy/ShopTypewiseUserShopList")
    fun getAllTeamShopList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                           @Field("shop_id") shop_id: String, @Field("area_id") area_id: String): Observable<TeamShopListResponseModel>

    @FormUrlEncoded
    @POST("PJPDetails/TeamLocationList")
    fun getTeamLocList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("creater_user_id") creater_user_id: String, @Field("date") date: String): Observable<TeamLocListResponseModel>

    @FormUrlEncoded
    @POST("PJPDetails/PJPDetailsList")
    fun getTeamPJPList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("creater_user_id") creater_user_id: String, @Field("year") year: String, @Field("month") month: String): Observable<TeamPjpResponseModel>

    @FormUrlEncoded
    @POST("PJPDetails/PJPConfigList")
    fun getTeamPJPConfig(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                         @Field("creater_user_id") creater_user_id: String): Observable<TeamPjpConfigResponseModel>


    @FormUrlEncoded
    @POST("PJPDetails/PJPCustomer")
    fun getCustomerList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                        @Field("creater_user_id") creater_user_id: String): Observable<CustomerResponseModel>

    @POST("PJPDetails/PJPAddList")
    fun addPjp(@Body addPjpInputParams: AddpjpInputParams): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("PJPDetails/PJPDeleteList")
    fun deletePJP(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                  @Field("creater_user_id") creater_user_id: String, @Field("pjp_id") pjp_id: String): Observable<BaseResponse>

    @POST("PJPDetails/PJPEditList")
    fun editPjp(@Body editPjpInputParams: EditPjpInputParams): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("PJPDetails/PJPList")
    fun getUserPJPList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("date") month: String): Observable<UserPjpResponseModel>

    @FormUrlEncoded
    @POST("OfflineTeam/GetMemberList")
    fun getOfflineTeamList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                           @Field("date") date: String): Observable<TeamListResponseModel>

    @FormUrlEncoded
    @POST("OfflineTeam/GetShopList")
    fun getOfflineTeamShopList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                               @Field("date") date: String): Observable<TeamShopListResponseModel>

    @FormUrlEncoded
    @POST("OfflineTeam/GetAreaList")
    fun getOfflineAreaList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("city_id") city_id: String): Observable<TeamAreaListResponseModel>


    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): TeamApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(TeamApi::class.java)
        }
    }
}