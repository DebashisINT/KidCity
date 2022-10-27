package com.kcteam.features.stock.api

/**
 * Created by Saikat on 17-09-2019.
 */
object StockRepositoryProvider {

    fun provideStockRepository(): StockRepository {
        return StockRepository(StockApi.create())
    }

}