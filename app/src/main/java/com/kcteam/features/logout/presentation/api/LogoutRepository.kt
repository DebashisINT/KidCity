package com.kcteam.features.logout.presentation.api

import com.kcteam.base.BaseResponse
import io.reactivex.Observable

/**
 * Created by Pratishruti on 23-11-2017.
 */
class LogoutRepository(val apiService: LogoutApi) {
    fun logout(user_id: String, session_token: String, latitude: String, longitude: String, logout_time: String, distance: String, autoLogout: String,
               address: String): Observable<BaseResponse> {
        return apiService.getLogoutResponse(user_id, session_token, latitude, longitude, logout_time, distance, autoLogout, address)
    }

}