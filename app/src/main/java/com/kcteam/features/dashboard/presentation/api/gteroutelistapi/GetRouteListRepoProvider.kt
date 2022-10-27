package com.kcteam.features.dashboard.presentation.api.gteroutelistapi

/**
 * Created by Saikat on 03-12-2018.
 */
object GetRouteListRepoProvider {
    fun routeListRepoProvider(): GetRouteListRepo {
        return GetRouteListRepo(GetRouteListApi.create())
    }
}