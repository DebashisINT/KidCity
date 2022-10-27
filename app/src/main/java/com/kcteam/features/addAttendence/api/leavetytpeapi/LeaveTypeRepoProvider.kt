package com.kcteam.features.addAttendence.api.leavetytpeapi

/**
 * Created by Saikat on 22-11-2018.
 */
object LeaveTypeRepoProvider {
    fun leaveTypeListRepoProvider(): LeaveTypeRepo {
        return LeaveTypeRepo(LeaveTypeApi.create())
    }
}