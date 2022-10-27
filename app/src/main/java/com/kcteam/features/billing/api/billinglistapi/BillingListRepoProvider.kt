package com.kcteam.features.billing.api.billinglistapi

/**
 * Created by Saikat on 20-02-2019.
 */
object BillingListRepoProvider {
    fun provideBillListRepository(): BillingListRepo {
        return BillingListRepo(BillingListApi.create())
    }
}