package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant.STATE_TABLE


/**
 * Created by Pratishruti on 07-12-2017.
// */
@Entity(tableName = STATE_TABLE)
class StateListEntity {

//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "Id")
//    var id: Int? = 0

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "state_id")
    var state_id: Int = 0

    @ColumnInfo(name = "state_name")
    var state_name: String? = null

}