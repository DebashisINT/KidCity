package com.kcteam.features.addshop.api.typeList


import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.addshop.model.*
import com.kcteam.features.addshop.model.assigntopplist.AssignToPPListResponseModel
import com.kcteam.features.beatCustom.BeatTeamResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 22-Jun-20.
 */
interface TypeListApi {

    @FormUrlEncoded
    @POST("PartyType/List")
    fun getTypeList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<TypeListResponseModel>

    @FormUrlEncoded
    @POST("Entity/List")
    fun getEntityList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<EntityResponseModel>

    @FormUrlEncoded
    @POST("PartyStatus/List")
    fun getPartyStatusList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<PartyStatusResponseModel>

    @FormUrlEncoded
    @POST("PartyStatus/Update")
    fun updatePartyStatus(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                          @Field("shop_id") shop_id: String, @Field("party_status_id") party_status_id: String,
                          @Field("reason") reason: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("SubType/RetailerList")
    fun getRetailerList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<RetailerListResponseModel>

    @FormUrlEncoded
    @POST("SubType/DDList")
    fun getDealerList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<DealerListResponseModel>

    @FormUrlEncoded
    @POST("SubType/BeatList")
    fun getBeatList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<BeatListResponseModel>

    @FormUrlEncoded
    @POST("Bankdetails/Update")
    fun updateBankDetails(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                          @Field("shop_id") shop_id: String, @Field("account_holder") account_holder: String,
                          @Field("account_no") account_no: String, @Field("bank_name") bank_name: String,
                          @Field("ifsc") ifsc: String, @Field("upi") upi: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("ShopAssignment/GetAssignedToShopList")
    fun getAssignedToShopList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("state_id") state_id: String): Observable<AssignedToShopListResponseModel>


    /**
     * Companion object to create the ShopActivityApi
     */
    companion object Factory {
        fun create(): TypeListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(TypeListApi::class.java)
        }
    }

}