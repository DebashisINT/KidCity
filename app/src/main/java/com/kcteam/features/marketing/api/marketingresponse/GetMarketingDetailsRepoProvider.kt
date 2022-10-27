package com.kcteam.features.marketing.api.marketingresponse

/**
 * Created by Pratishruti on 05-03-2018.
 */
object GetMarketingDetailsRepoProvider {
    fun provideMarketingDetail(): GetMarketingDetailsRepo {
        return GetMarketingDetailsRepo(GetMarketingDetailsApi.create())
    }
}