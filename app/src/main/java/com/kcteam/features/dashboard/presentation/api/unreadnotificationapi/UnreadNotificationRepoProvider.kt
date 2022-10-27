package com.kcteam.features.dashboard.presentation.api.unreadnotificationapi

/**
 * Created by Saikat on 07-03-2019.
 */
object UnreadNotificationRepoProvider {
    fun unreadNotificationRepoProvider(): UnreadNotificationRepo {
        return UnreadNotificationRepo(UnreadNotificationApi.create())
    }
}