package com.kcteam.features.login

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant


/**
 * Created by Kinsuk on 16-11-2017.
 */
@Entity(tableName = AppConstant.ATTENDANCE_TABLE)
class UserLoginDataEntity {

    @PrimaryKey(autoGenerate = true)
    var entryId: Int = 0

    @ColumnInfo(name = "logindate")
    var logindate: String = ""

    @ColumnInfo(name = "logintime")
    var logintime: String = ""

    @ColumnInfo(name = "logouttime")
    var logouttime: String = ""

    @ColumnInfo(name = "duration")
    var duration: String = ""

    @ColumnInfo(name = "userId")
    var userId: String = ""

    @ColumnInfo(name = "Isonleave")
    var Isonleave: String? = null

    @ColumnInfo(name = "logindate_number")
    var logindate_number: Long? = null

//    @ColumnInfo(name = "login_date")
//    var login_date: Date? = null


}