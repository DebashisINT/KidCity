package com.kcteam.features.dashboard.presentation.api.dayStartEnd

import com.kcteam.app.Pref
import com.kcteam.base.BaseResponse
import com.kcteam.features.dashboard.presentation.model.DaystartDayendRequest
import com.kcteam.features.dashboard.presentation.model.StatusDayStartEnd
import com.kcteam.features.stockCompetetorStock.ShopAddCompetetorStockRequest
import com.kcteam.features.stockCompetetorStock.api.AddCompStockApi
import io.reactivex.Observable

class DayStartEndRepository (val apiService: DayStartEndApi){
    fun dayStart(daystartDayendRequest: DaystartDayendRequest): Observable<BaseResponse> {
        return apiService.submitDayStartEnd(daystartDayendRequest)
    }

    fun dayStartEndStatus(date:String): Observable<StatusDayStartEnd> {
        return apiService.statusDayStartEnd(Pref.session_token!!, Pref.user_id!!,date)
    }


}