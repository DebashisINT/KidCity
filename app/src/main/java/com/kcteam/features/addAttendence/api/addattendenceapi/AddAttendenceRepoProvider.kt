package com.kcteam.features.addAttendence.api.addattendenceapi

/**
 * Created by Saikat on 05-09-2018.
 */
object AddAttendenceRepoProvider {
    fun addAttendenceRepo(): AddAttendenceRepo {
        return AddAttendenceRepo(AddAttendenceApi.create())
    }

    fun leaveApprovalRepo(): AddAttendenceRepo {
        return AddAttendenceRepo(AddAttendenceApi.approveLeave())
    }

    fun sendAttendanceImgRepo(): AddAttendenceRepo {
        return AddAttendenceRepo(AddAttendenceApi.sendAttendanceImg())
    }

}