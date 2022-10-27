package com.kcteam.features.micro_learning.api

import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.BaseResponse
import com.kcteam.features.micro_learning.model.MicroLearningResponseModel
import io.reactivex.Observable

class MicroLearningRepo(val apiService: MicroLearningApi) {
    fun getMicroLearningList(): Observable<MicroLearningResponseModel> {
        return apiService.microLearningList(Pref.session_token!!, Pref.user_id!!)
    }

    fun updateVideoPosition(id: String, current_window: String, play_back_position: String, play_when_ready: Boolean, percentage: Float): Observable<BaseResponse> {
        return apiService.updateVideoPosition(Pref.session_token!!, Pref.user_id!!, id, current_window, play_back_position,
                play_when_ready, String.format("%.2f", percentage))
    }

    fun updateNote(id: String, note: String): Observable<BaseResponse> {
        return apiService.updateNote(Pref.session_token!!, Pref.user_id!!, id, note, AppUtils.getCurrentISODateTime())
    }

    fun updateFileOpeningTime(id: String, openingTime: String): Observable<BaseResponse> {
        return apiService.updateFileOpeningTime(Pref.session_token!!, Pref.user_id!!, id, openingTime, AppUtils.getCurrentISODateTime())
    }

    fun updateDownloadStatus(id: String, isDownloaded: Boolean): Observable<BaseResponse> {
        return apiService.updateDownloadStatus(Pref.session_token!!, Pref.user_id!!, id, isDownloaded)
    }
}