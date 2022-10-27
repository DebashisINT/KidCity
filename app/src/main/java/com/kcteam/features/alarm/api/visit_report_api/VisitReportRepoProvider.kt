package com.kcteam.features.alarm.api.visit_report_api

/**
 * Created by Saikat on 21-02-2019.
 */
object VisitReportRepoProvider {
    fun provideVisitReportRepository(): VisitReportRepo {
        return VisitReportRepo(VisitReportApi.create())
    }
}