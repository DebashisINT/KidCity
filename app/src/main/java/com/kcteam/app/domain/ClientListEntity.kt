package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 10-Jul-20.
 */
@Entity(tableName = AppConstant.CLIENT_LIST)
class ClientListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "client_id")
    var client_id: String? = null

    @ColumnInfo(name = "client_name")
    var client_name: String? = null
}