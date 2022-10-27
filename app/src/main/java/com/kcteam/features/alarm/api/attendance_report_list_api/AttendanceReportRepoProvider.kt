package com.kcteam.features.alarm.api.attendance_report_list_api

/**
 * Created by Saikat on 20-02-2019.
 */
object AttendanceReportRepoProvider {
    fun provideAttendanceReportRepository(): AttendanceReportRepo {
        return AttendanceReportRepo(AttendanceReportApi.create())
    }
}