package com.kcteam.features.lead.api

import com.kcteam.features.NewQuotation.api.GetQuotListRegRepository
import com.kcteam.features.NewQuotation.api.GetQutoListApi


object GetLeadRegProvider {
    fun provideList(): GetLeadListRegRepository {
        return GetLeadListRegRepository(GetLeadListApi.create())
    }
}