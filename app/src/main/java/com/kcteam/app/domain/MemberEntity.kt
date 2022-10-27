package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 03-Jul-20.
 */
@Entity(tableName = AppConstant.MEMBER_TABLE)
class MemberEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "user_id")
    var user_id: String? = null

    @ColumnInfo(name = "user_name")
    var user_name: String? = null

    @ColumnInfo(name = "contact_no")
    var contact_no: String? = null

    @ColumnInfo(name = "super_id")
    var super_id: String? = null

    @ColumnInfo(name = "super_name")
    var super_name: String? = null

    @ColumnInfo(name = "date_time")
    var date_time: String? = null
}