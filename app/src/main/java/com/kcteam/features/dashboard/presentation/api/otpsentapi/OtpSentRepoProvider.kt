package com.kcteam.features.dashboard.presentation.api.otpsentapi

/**
 * Created by Saikat on 22-11-2018.
 */
object OtpSentRepoProvider {
    fun otpSentRepoProvider(): OtpSentRepo {
        return OtpSentRepo(OtpSentAPi.create())
    }
}