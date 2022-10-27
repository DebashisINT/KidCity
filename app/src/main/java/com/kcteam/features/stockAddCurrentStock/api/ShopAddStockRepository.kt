package com.kcteam.features.stockAddCurrentStock.api

import com.kcteam.base.BaseResponse
import com.kcteam.features.location.model.ShopRevisitStatusRequest
import com.kcteam.features.location.shopRevisitStatus.ShopRevisitStatusApi
import com.kcteam.features.stockAddCurrentStock.ShopAddCurrentStockRequest
import com.kcteam.features.stockAddCurrentStock.model.CurrentStockGetData
import com.kcteam.features.stockCompetetorStock.model.CompetetorStockGetData
import io.reactivex.Observable

class ShopAddStockRepository (val apiService : ShopAddStockApi){
    fun shopAddStock(shopAddCurrentStockRequest: ShopAddCurrentStockRequest?): Observable<BaseResponse> {
        return apiService.submShopAddStock(shopAddCurrentStockRequest)
    }

    fun getCurrStockList(sessiontoken: String, user_id: String, date: String): Observable<CurrentStockGetData> {
        return apiService.getCurrStockListApi(sessiontoken, user_id, date)
    }

}