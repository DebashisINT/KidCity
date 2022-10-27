package com.kcteam.features.viewPPDDStock.api.stocklist

import com.kcteam.features.viewPPDDStock.model.stocklist.StockListResponseModel
import io.reactivex.Observable

/**
 * Created by Saikat on 13-11-2018.
 */
class StockListRepo(val apiService: StockListApi) {
    fun stockList(session_token: String, user_id: String): Observable<StockListResponseModel> {
        return apiService.stockList(session_token, user_id)
    }
}