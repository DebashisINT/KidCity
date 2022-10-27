package com.kcteam.features.lead.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.TBL_LEAD_ACTIVITY)
class LeadActivityEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "crm_id")
    var crm_id: String? = null

    @ColumnInfo(name = "customer_name")
    var customer_name: String? = null

    @ColumnInfo(name = "mobile_no")
    var mobile_no: String? = null

    @ColumnInfo(name = "activity_date")
    var activity_date: String? = null

    @ColumnInfo(name = "activity_time")
    var activity_time: String? = null

    @ColumnInfo(name = "activity_type_name")
    var activity_type_name: String? = null

    @ColumnInfo(name = "activity_status")
    var activity_status: String? = null

    @ColumnInfo(name = "activity_details")
    var activity_details: String? = null

    @ColumnInfo(name = "other_remarks")
    var other_remarks: String? = null

    @ColumnInfo(name = "activity_next_date")
    var activity_next_date: String? = null


}