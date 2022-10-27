package com.kcteam.features.reimbursement.api

/**
 * Created by Saikat on 24-01-2019.
 */
object ReimbursementConfigRepoProvider {
    fun provideReimbursementConfigRepository(): ReimbursementConfigRepo {
        return ReimbursementConfigRepo(ReimbursementConfigApi.create())
    }
}