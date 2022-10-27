package com.kcteam.features.report.api

/**
 * Created by Pratishruti on 27-12-2017.
 */
object GetMISRepositoryProvider {
    fun provideMISRepository(): GetMISRepository {
        return GetMISRepository(GetMISApi.create())
    }
}