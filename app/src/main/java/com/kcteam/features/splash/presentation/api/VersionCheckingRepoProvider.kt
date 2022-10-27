package com.kcteam.features.splash.presentation.api

/**
 * Created by Saikat on 02-01-2019.
 */
object VersionCheckingRepoProvider {
    fun versionCheckingRepository(): VersionCheckingRepo {
        return VersionCheckingRepo(VersionCheckingApi.create())
    }
}