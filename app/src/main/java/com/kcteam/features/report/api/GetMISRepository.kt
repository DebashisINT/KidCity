package com.kcteam.features.report.api

import com.kcteam.features.report.model.MISResponse
import io.reactivex.Observable

/**
 * Created by Pratishruti on 27-12-2017.
 */
class GetMISRepository(val apiService: GetMISApi) {
    fun getMISDetail(user_id: String, session_token: String, month: String, start_date: String, end_date: String, year: String): Observable<MISResponse> {
        return apiService.getMISDetail(user_id, session_token, month, start_date, end_date, year)
    }

}