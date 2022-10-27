package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.DOCUMENT_LIST_TABLE)
class DocumentListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "list_id")
    var list_id: String? = null

    @ColumnInfo(name = "type_id")
    var type_id: String? = null

    @ColumnInfo(name = "date_time")
    var date_time: String? = null

    @ColumnInfo(name = "attachment")
    var attachment: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "document_name")
    var document_name: String? = null


}