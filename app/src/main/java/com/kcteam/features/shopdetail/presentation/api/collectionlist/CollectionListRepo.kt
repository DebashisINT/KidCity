package com.kcteam.features.shopdetail.presentation.api.collectionlist

import com.kcteam.app.Pref
import com.kcteam.features.billing.model.BillingListResponseModel
import com.kcteam.features.shopdetail.presentation.model.collectionlist.CollectionListResponseModel
import io.reactivex.Observable

/**
 * Created by Saikat on 13-11-2018.
 */
class CollectionListRepo(val apiService: CollectionListApi) {
    fun collectionList(session_token: String, user_id: String, shop_id: String): Observable<CollectionListResponseModel> {
        return apiService.collectionList(session_token, user_id, shop_id)
    }

    fun billingList(shop_id: String): Observable<BillingListResponseModel> {
        return apiService.billingList(Pref.session_token!!, Pref.user_id!!, shop_id)
    }
}