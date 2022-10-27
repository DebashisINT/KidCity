package com.kcteam.features.addshop.api.areaList

import com.kcteam.app.NetworkConstant
import com.kcteam.features.addshop.model.AreaListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 14-May-20.
 */
interface AreaListApi {

    @FormUrlEncoded
    @POST("Shoplist/AreaList")
    fun getAreaList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("city_id") city_id: String,
                    @Field("creater_user_id") creater_user_id: String):
            Observable<AreaListResponseModel>

    /**
     * Companion object to create the ShopActivityApi
     */
    companion object Factory {
        fun create(): AreaListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(AreaListApi::class.java)
        }
    }
}