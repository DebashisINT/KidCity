package com.kcteam.features.stockCompetetorStock.api

import com.kcteam.base.BaseResponse
import com.kcteam.features.orderList.model.NewOrderListResponseModel
import com.kcteam.features.stockCompetetorStock.ShopAddCompetetorStockRequest
import com.kcteam.features.stockCompetetorStock.model.CompetetorStockGetData
import io.reactivex.Observable

class AddCompStockRepository(val apiService:AddCompStockApi){

    fun addCompStock(shopAddCompetetorStockRequest: ShopAddCompetetorStockRequest): Observable<BaseResponse> {
        return apiService.submShopCompStock(shopAddCompetetorStockRequest)
    }

    fun getCompStockList(sessiontoken: String, user_id: String, date: String): Observable<CompetetorStockGetData> {
        return apiService.getCompStockList(sessiontoken, user_id, date)
    }
}