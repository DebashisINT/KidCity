package com.kcteam.features.performance.api

/**
 * Created by Saikat on 15-11-2018.
 */
object UpdateGpsStatusRepoProvider {
    fun updateGpsStatusRepository(): UpdateGpsStatusRepo {
        return UpdateGpsStatusRepo(UpdateGpsStatusApi.create())
    }
}