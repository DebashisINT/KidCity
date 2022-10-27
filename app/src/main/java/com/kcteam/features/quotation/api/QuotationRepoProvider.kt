package com.kcteam.features.quotation.api

/**
 * Created by Saikat on 12-Jun-20.
 */
object QuotationRepoProvider {
    fun provideBSListRepository(): QuotationRepo {
        return QuotationRepo(QuotationApi.create())
    }
}