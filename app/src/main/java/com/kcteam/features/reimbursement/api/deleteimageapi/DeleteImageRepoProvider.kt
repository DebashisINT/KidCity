package com.kcteam.features.reimbursement.api.deleteimageapi

/**
 * Created by Saikat on 08-02-2019.
 */
object DeleteImageRepoProvider {
    fun provideReimbursementConfigRepository(): DeleteImageRepo {
        return DeleteImageRepo(DeleteImageApi.create())
    }
}