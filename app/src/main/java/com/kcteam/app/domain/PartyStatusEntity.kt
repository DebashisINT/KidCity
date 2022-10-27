package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.PARTY_STATUS_TABLE)
class PartyStatusEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "party_status_id")
    var party_status_id: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null
}