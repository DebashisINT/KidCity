package com.kcteam.features.myprofile.api.citylist

import com.kcteam.app.NetworkConstant
import com.kcteam.features.myprofile.model.citylist.CityListApiResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Created by Pratishruti on 19-02-2018.
 */
interface CityListApi {
    @GET("CityList/Cities")
    fun getAllCities(): Observable<CityListApiResponse>
    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): CityListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(CityListApi::class.java)
        }
    }
}