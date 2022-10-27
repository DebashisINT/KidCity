package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.RETURN_DETAILS_TABLE)
class ReturnDetailsEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "only_date")
    var only_date: String? = null

    @ColumnInfo(name = "amount")
    var amount: String? = null

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "return_id")
    var return_id: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "return_lat")
    var return_lat: String? = null

    @ColumnInfo(name = "return_long")
    var return_long: String? = null

}
