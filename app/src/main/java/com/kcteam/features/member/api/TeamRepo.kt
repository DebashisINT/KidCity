package com.kcteam.features.member.api

import com.kcteam.app.Pref
import com.kcteam.base.BaseResponse
import com.kcteam.features.member.model.*
import io.reactivex.Observable

/**
 * Created by Saikat on 29-01-2020.
 */
class TeamRepo(val apiService: TeamApi) {
    fun teamList(userId: String, isFirstScreen: Boolean, isAllTeam: Boolean): Observable<TeamListResponseModel> {
        return apiService.getTeamList(Pref.session_token!!, userId, isFirstScreen, isAllTeam)
    }

    fun teamShopList(userId: String, areaId: String): Observable<TeamShopListResponseModel> {
        return apiService.getTeamShopList(Pref.session_token!!, userId, areaId)
    }

    fun teamAllShopList(userId: String, shopId: String, areaId: String): Observable<TeamShopListResponseModel> {
        return apiService.getAllTeamShopList(Pref.session_token!!, userId, shopId, areaId)
    }

    fun teamLocList(userId: String, date: String): Observable<TeamLocListResponseModel> {
        return apiService.getTeamLocList(Pref.session_token!!, userId, Pref.user_id!!, date)
    }

    fun teamPjpList(userId: String, year: String, month: String): Observable<TeamPjpResponseModel> {
        return apiService.getTeamPJPList(Pref.session_token!!, userId, Pref.user_id!!, year, month)
    }

    fun teamPjpConfig(userId: String): Observable<TeamPjpConfigResponseModel> {
        return apiService.getTeamPJPConfig(Pref.session_token!!, userId, Pref.user_id!!)
    }

    fun teamCustomerList(userId: String): Observable<CustomerResponseModel> {
        return apiService.getCustomerList(Pref.session_token!!, userId, Pref.user_id!!)
    }

    fun addPjp(addPjpInputParams: AddpjpInputParams): Observable<BaseResponse> {
        return apiService.addPjp(addPjpInputParams)
    }

    fun deletePjp(userId: String, pjpId: String): Observable<BaseResponse> {
        return apiService.deletePJP(Pref.session_token!!, userId, Pref.user_id!!, pjpId)
    }

    fun editPjp(editPjpInputParams: EditPjpInputParams): Observable<BaseResponse> {
        return apiService.editPjp(editPjpInputParams)
    }

    fun getUserPJPList(date: String): Observable<UserPjpResponseModel> {
        return apiService.getUserPJPList(Pref.session_token!!, Pref.user_id!!, date)
    }

    fun offlineTeamList(date: String): Observable<TeamListResponseModel> {
        return apiService.getOfflineTeamList(Pref.session_token!!, Pref.user_id!!, date)
    }

    fun offlineTeamShopList(date: String): Observable<TeamShopListResponseModel> {
        return apiService.getOfflineTeamShopList(Pref.session_token!!, Pref.user_id!!, date)
    }

    fun teamAreaList(): Observable<TeamAreaListResponseModel> {
        return apiService.getOfflineAreaList(Pref.session_token!!, Pref.user_id!!, Pref.profile_city)
    }
}