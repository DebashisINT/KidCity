package com.kcteam.features.viewPPDDStock.api.stocklist

/**
 * Created by Saikat on 13-11-2018.
 */
object StockListRepoProvider {
    fun stockListRepository(): StockListRepo {
        return StockListRepo(StockListApi.create())
    }
}