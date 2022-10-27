package com.kcteam.features.addAttendence.api.routeapi

/**
 * Created by Saikat on 22-11-2018.
 */
object RouteRepoProvider {
    fun routeListRepoProvider(): RouteRepo {
        return RouteRepo(RouteApi.create())
    }
}