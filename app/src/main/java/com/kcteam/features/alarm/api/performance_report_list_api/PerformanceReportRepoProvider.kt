package com.kcteam.features.alarm.api.performance_report_list_api

/**
 * Created by Saikat on 21-02-2019.
 */
object PerformanceReportRepoProvider {
    fun providePerformanceReportRepository(): PerformanceReportRepo {
        return PerformanceReportRepo(PerformanceReportApi.create())
    }
}