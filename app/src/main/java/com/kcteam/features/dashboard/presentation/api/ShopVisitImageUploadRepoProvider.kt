package com.kcteam.features.dashboard.presentation.api

/**
 * Created by Saikat on 28-09-2018.
 */
object ShopVisitImageUploadRepoProvider {
    fun provideAddShopRepository(): ShopVisitImageUploadRepo {
        return ShopVisitImageUploadRepo(ShopVisitImageUploadApi.create())
    }


}