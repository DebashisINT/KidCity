package com.kcteam.features.dailyPlan.api

/**
 * Created by Saikat on 24-12-2019.
 */
object PlanRepoProvider {
    fun planListRepoProvider(): PlanRepo {
        return PlanRepo(PlanApi.create())
    }
}