package com.kcteam.features.addAttendence.model

import com.kcteam.base.BaseResponse


class ApprovalLeaveResponseModel: BaseResponse() {
    var user_id_leave_applied:String?  = null
    var user_name_leave_applied: String? = null
    var leave_list: ArrayList<Leave_list_Response>? = null
}

data class Leave_list_Response (
    var applied_date:String?  = null,
    var applied_date_time: String? = null,
    var from_date: String? = null,
    var to_date: String? = null,
    var leave_type: String? = null,
    var approve_status: Boolean? = null,
    var reject_status: Boolean? = null,
    var leave_reason: String? = null,
    var approval_date_time: String? = null,
    var approver_remarks: String? = null
)