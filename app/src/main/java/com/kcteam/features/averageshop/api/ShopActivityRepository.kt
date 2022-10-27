package com.kcteam.features.averageshop.api

import com.kcteam.features.averageshop.model.ShopActivityRequest
import com.kcteam.features.averageshop.model.ShopActivityResponse
import io.reactivex.Observable

/**
 * Created by Pratishruti on 07-12-2017.
 */
class ShopActivityRepository (val apiService: ShopActivityApi) {
    fun fetchShopActivity(shopActivityReq: ShopActivityRequest?): Observable<ShopActivityResponse> {
        return apiService.fetchShopActivity(shopActivityReq)
    }


    fun fetchShopActivitynew(sessiontoken: String, user_id: String, date_span: String,from_date : String , to_date :String): Observable<ShopActivityResponse> {
        return apiService.fetchShopActivitynew(sessiontoken,user_id,date_span,from_date,to_date)
    }

}