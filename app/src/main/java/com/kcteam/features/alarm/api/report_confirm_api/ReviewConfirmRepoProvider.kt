package com.kcteam.features.alarm.api.report_confirm_api

/**
 * Created by Saikat on 21-02-2019.
 */
object ReviewConfirmRepoProvider {
    fun provideReviewConfirmRepository(): ReviewConfirmRepo {
        return ReviewConfirmRepo(ReviewConfirmApi.create())
    }
}