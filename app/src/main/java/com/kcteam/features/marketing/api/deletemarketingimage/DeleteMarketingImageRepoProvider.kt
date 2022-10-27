package com.kcteam.features.marketing.api.deletemarketingimage

/**
 * Created by Pratishruti on 06-03-2018.
 */
object DeleteMarketingImageRepoProvider {
    fun provideDeleteMarketingImage(): DeleteMarketingImageRepo {
        return DeleteMarketingImageRepo(DeleteMarketingImageApi.create())
    }
}