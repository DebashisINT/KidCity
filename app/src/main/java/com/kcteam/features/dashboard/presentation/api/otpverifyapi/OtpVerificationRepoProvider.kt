package com.kcteam.features.dashboard.presentation.api.otpverifyapi

/**
 * Created by Saikat on 22-11-2018.
 */
object OtpVerificationRepoProvider {
    fun otpVerifyRepoProvider(): OtpVerificationRepo {
        return OtpVerificationRepo(OtpVerificationApi.create())
    }
}