package com.kcteam.features.reimbursement.api.reimbursementshopapi

/**
 * Created by Saikat on 31-05-2019.
 */
object ReimbursementShopRepoProvider {
    fun provideReimbursementConfigRepository(): ReimbursementShopRepo {
        return ReimbursementShopRepo(ReimbursementShopApi.create())
    }
}