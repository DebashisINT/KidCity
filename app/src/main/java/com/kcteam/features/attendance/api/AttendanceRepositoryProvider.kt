package com.kcteam.features.attendance.api

/**
 * Created by Pratishruti on 30-11-2017.
 */
object AttendanceRepositoryProvider {
    fun provideAttendanceRepository(): AttendanceListRepository {
        return AttendanceListRepository(AttendanceListApi.create())
    }
}