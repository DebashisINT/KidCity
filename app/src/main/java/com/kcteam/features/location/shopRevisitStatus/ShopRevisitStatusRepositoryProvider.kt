package com.kcteam.features.location.shopRevisitStatus

import com.kcteam.features.location.shopdurationapi.ShopDurationApi
import com.kcteam.features.location.shopdurationapi.ShopDurationRepository

object ShopRevisitStatusRepositoryProvider {
    fun provideShopRevisitStatusRepository(): ShopRevisitStatusRepository {
        return ShopRevisitStatusRepository(ShopRevisitStatusApi.create())
    }
}