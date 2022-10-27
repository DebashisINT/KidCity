package com.kcteam.features.nearbyshops.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.login.model.GetQtsAnsSubmitDtlsResponseModel
import com.kcteam.features.login.model.GetSecImageUploadResponseModel
import com.kcteam.features.login.model.productlistmodel.ModelListResponse
import com.kcteam.features.nearbyshops.model.*
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Pratishruti on 28-11-2017.
 */
interface ShopListApi {
    @FormUrlEncoded
    @POST("Shoplist/List")
    fun getShopList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<ShopListResponse>

    @FormUrlEncoded
    @POST("Shoplist/SupervisorTeamList")
    fun getExtraTeamShopList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<ShopListResponse>

    @FormUrlEncoded
    @POST("Shoplist/ShopType")
    fun getShopTypeList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<ShopTypeResponseModel>

    @FormUrlEncoded
    @POST("ProductList/ModelList")
    fun getModelList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<ModelListResponseModel>

    @FormUrlEncoded
    @POST("ProductList/ModelList")
    fun getModelListNew(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<ModelListResponse>

    @FormUrlEncoded
    @POST("ProductList/PrimaryApplicationList")
    fun getPrimaryAppList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<PrimaryAppListResponseModel>

    @FormUrlEncoded
    @POST("ProductList/SecondaryApplicationList")
    fun getSecondaryAppList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<SecondaryAppListResponseModel>

    @FormUrlEncoded
    @POST("LeadType/List")
    fun getLeadList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<LeadListResponseModel>

    @FormUrlEncoded
    @POST("Stage/List")
    fun getStageList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<StageListResponseModel>

    @FormUrlEncoded
    @POST("Stage/FunnelStageList")
    fun getFunnelStageList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<FunnelStageListResponseModel>

    @FormUrlEncoded
    @POST("Shoplist/AllShopTypeWithSettings")
    fun getShopTypeListStockView(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<ShopTypeStockViewResponseModel>


    @FormUrlEncoded
    @POST("RubyFoodLead/ProspectList")
    fun getProsList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<ProsListResponseModel>

    @FormUrlEncoded
    @POST("RubyFoodLead/QuestionList")
    fun getQuesList(@Field("session_token") session_token:String,@Field("user_id") user_id:String): Observable<QuesListResponseModel>

    @FormUrlEncoded
    @POST("RubyFoodLead/QuestionAnswerList")
    fun getQuestionAnsSubmitDetails(@Field("session_token") session_token:String,@Field("user_id") user_id:String)
            : Observable<GetQtsAnsSubmitDtlsResponseModel>


    @FormUrlEncoded
    @POST("RubyFoodLead/LeadTypeImageLink")
    fun getSecImageUpload(@Field("session_token") session_token:String,@Field("user_id") user_id:String)
            : Observable<GetSecImageUploadResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ShopListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ShopListApi::class.java)
        }
    }
}