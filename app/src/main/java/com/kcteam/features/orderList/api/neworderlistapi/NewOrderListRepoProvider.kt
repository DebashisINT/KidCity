package com.kcteam.features.orderList.api.neworderlistapi

/**
 * Created by Saikat on 03-12-2018.
 */
object NewOrderListRepoProvider {
    fun provideOrderListRepository(): NewOrderListRepo {
        return NewOrderListRepo(NewOrderListApi.create())
    }
}