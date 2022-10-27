package com.kcteam.features.nearbyshops.api.updateaddress

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.nearbyshops.model.updateaddress.AddressUpdateRequest
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Pratishruti on 28-11-2017.
 */
interface ShopAddressUpdateApi {

    @POST("UpdateShop/Update")
    fun getShopAddressUpdate(@Body shopAddress:AddressUpdateRequest): Observable<BaseResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ShopAddressUpdateApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ShopAddressUpdateApi::class.java)
        }
    }
}