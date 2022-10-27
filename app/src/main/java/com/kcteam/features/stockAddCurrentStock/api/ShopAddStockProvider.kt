package com.kcteam.features.stockAddCurrentStock.api

import com.kcteam.features.location.shopRevisitStatus.ShopRevisitStatusApi
import com.kcteam.features.location.shopRevisitStatus.ShopRevisitStatusRepository

object ShopAddStockProvider {
    fun provideShopAddStockRepository(): ShopAddStockRepository {
        return ShopAddStockRepository(ShopAddStockApi.create())
    }
}