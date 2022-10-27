package com.kcteam.features.orderList.api

/**
 * Created by Saikat on 01-10-2018.
 */
object OrderListRepoProvider {
    fun provideOrderListRepository(): OrderListRepo {
        return OrderListRepo(OrderListApi.create())
    }
}