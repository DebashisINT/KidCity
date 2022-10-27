package com.kcteam.features.report.api.report_api


/**
 * Created by Saikat on 22-Jul-20.
 */
object ReportRepoProvider {
    fun getAchvReport(): ReportRepo {
        return ReportRepo(ReportApi.create())
    }
}