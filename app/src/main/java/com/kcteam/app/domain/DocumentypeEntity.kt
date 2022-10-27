package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.DOCUMENT_TYPE_TABLE)
class DocumentypeEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "type_id")
    var type_id: String? = null

    @ColumnInfo(name = "type_name")
    var type_name: String? = null

    @ColumnInfo(name = "image")
    var image: String? = null

    @ColumnInfo(name = "IsForOrganization")
    var IsForOrganization: Boolean = false

    @ColumnInfo(name = "IsForOwn")
    var IsForOwn: Boolean = false
}