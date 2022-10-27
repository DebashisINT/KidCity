package com.kcteam.features.addAttendence.model

import java.io.Serializable

/**
 * Created by Saikat on 16-Apr-20.
 */
class SendLeaveApprovalInputParams : Serializable {
    var session_token = ""
    var user_id = ""
    var leave_type = ""
    var leave_from_date = ""
    var leave_to_date = ""
    var leave_reason = ""
    var leave_lat = ""
    var leave_long = ""
    var leave_add = ""
}