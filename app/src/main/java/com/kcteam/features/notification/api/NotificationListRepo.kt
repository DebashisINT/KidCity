package com.kcteam.features.notification.api

import com.kcteam.features.notification.model.NotificationListResponseModel
import io.reactivex.Observable

/**
 * Created by Saikat on 06-03-2019.
 */
class NotificationListRepo(val apiService: NotificationListApi) {
    fun notificationList(session_token: String, user_id: String): Observable<NotificationListResponseModel> {
        return apiService.notificationList(session_token, user_id)
    }
}