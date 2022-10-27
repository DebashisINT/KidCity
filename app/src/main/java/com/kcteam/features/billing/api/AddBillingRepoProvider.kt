package com.kcteam.features.billing.api

/**
 * Created by Saikat on 20-02-2019.
 */
object AddBillingRepoProvider {
    fun addBillRepository(): AddBillingRepo {
        return AddBillingRepo(AddBillingApi.create())
    }

    fun addBillImageRepository(): AddBillingRepo {
        return AddBillingRepo(AddBillingApi.createImage())
    }
}