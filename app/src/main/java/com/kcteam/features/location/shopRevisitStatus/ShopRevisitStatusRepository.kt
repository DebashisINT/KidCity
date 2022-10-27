package com.kcteam.features.location.shopRevisitStatus

import com.kcteam.base.BaseResponse
import com.kcteam.features.location.model.ShopDurationRequest
import com.kcteam.features.location.model.ShopRevisitStatusRequest
import io.reactivex.Observable

class ShopRevisitStatusRepository(val apiService : ShopRevisitStatusApi) {
    fun shopRevisitStatus(shopRevisitStatus: ShopRevisitStatusRequest?): Observable<BaseResponse> {
        return apiService.submShopRevisitStatus(shopRevisitStatus)
    }
}