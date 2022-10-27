package com.kcteam.features.addshop.api.areaList

/**
 * Created by Saikat on 14-May-20.
 */
object AreaListRepoProvider {
    fun provideAreaListRepository(): AreaListRepo {
        return AreaListRepo(AreaListApi.create())
    }
}