package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant
import com.kcteam.app.utils.AppUtils

@Entity(tableName = AppConstant.TBL_USER_WISE_LEAVE_LIST)
class UserWiseLeaveListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "applied_date")
    var applied_date: String? = ""

    @ColumnInfo(name = "applied_date_time")
    var applied_date_time: String? = ""

    @ColumnInfo(name = "from_date")
    var from_date: String? = ""

    @ColumnInfo(name = "from_date_modified")
    var from_date_modified: String? = ""

    @ColumnInfo(name = "to_date")
    var to_date: String? = ""

    @ColumnInfo(name = "leave_type")
    var leave_type: String? = ""

    @ColumnInfo(name = "approve_status")
    var approve_status: Boolean? = false

    @ColumnInfo(name = "reject_status")
    var reject_status: Boolean? = false

    @ColumnInfo(name = "leave_reason")
    var leave_reason: String? = ""

    @ColumnInfo(name = "approval_date_time")
    var approval_date_time: String? = ""

    @ColumnInfo(name = "approver_remarks")
    var approver_remarks: String? = ""


}