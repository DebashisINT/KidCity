package com.kcteam.features.reimbursement.api.reimbursementshopapi

import com.kcteam.app.Pref
import com.kcteam.features.reimbursement.model.reimbursement_shop.ReimbursementShopResponseModel
import io.reactivex.Observable

/**
 * Created by Saikat on 31-05-2019.
 */
class ReimbursementShopRepo(val apiService: ReimbursementShopApi) {
    fun getReimbursementShop(date: String, isEditable: Boolean, expense_mapId: String, subexpense_MapId: String): Observable<ReimbursementShopResponseModel> {
        return apiService.getReimbursementShop(Pref.user_id!!, Pref.session_token!!, date, isEditable, expense_mapId, subexpense_MapId)
    }

    fun getReimbursementLoc(date: String): Observable<ReimbursementShopResponseModel> {
        return apiService.getReimbursementLoc(Pref.user_id!!, Pref.session_token!!, date)
    }
}