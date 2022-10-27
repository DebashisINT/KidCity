package com.kcteam.features.dashboard.presentation.api.dayStartEnd

import com.kcteam.features.stockCompetetorStock.api.AddCompStockApi
import com.kcteam.features.stockCompetetorStock.api.AddCompStockRepository

object DayStartEndRepoProvider {
    fun dayStartRepositiry(): DayStartEndRepository {
        return DayStartEndRepository(DayStartEndApi.create())
    }

}