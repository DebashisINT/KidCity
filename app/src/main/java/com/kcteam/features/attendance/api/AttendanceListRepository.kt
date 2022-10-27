package com.kcteam.features.attendance.api

import com.kcteam.features.attendance.model.AttendanceRequest
import com.kcteam.features.attendance.model.AttendanceResponse
import io.reactivex.Observable

/**
 * Created by Pratishruti on 30-11-2017.
 */
class AttendanceListRepository(val apiService: AttendanceListApi) {
    fun getAttendanceList(attendanceRequest: AttendanceRequest?): Observable<AttendanceResponse> {
        return apiService.getAttendanceList(attendanceRequest)
    }
}