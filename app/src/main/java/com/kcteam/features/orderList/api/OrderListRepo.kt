package com.kcteam.features.orderList.api

import com.kcteam.features.orderList.model.OrderListResponseModel
import io.reactivex.Observable

/**
 * Created by Saikat on 01-10-2018.
 */
class OrderListRepo(val apiService: OrderListApi) {
    fun getOrderList(sessiontoken: String, user_id: String, date: String): Observable<OrderListResponseModel> {
        return apiService.getOrderList(sessiontoken, user_id, date)
    }
}