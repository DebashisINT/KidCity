package com.kcteam.features.reimbursement.api.editapi

/**
 * Created by Saikat on 08-02-2019.
 */
object ReimbursementEditRepoProvider {
    fun editReimbursementConfigRepository(): ReimbursementEditRepo {
        return ReimbursementEditRepo(ReimbursementEditApi.create())
    }
}