package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import com.kcteam.app.AppConstant.TA_TABLE

/**
 * Created by sayantan.sarkar on 2/11/17.
 */
@Entity(tableName = TA_TABLE)
class TaListDBModelEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "from_date")
    var from_date: String? = null

    @ColumnInfo(name = "to_date")
    var to_date: String? = null

    @ColumnInfo(name = "amount")
    var amount: String? = null

    @ColumnInfo(name = "description")
    var description: String? = null

    @ColumnInfo(name = "status")
    var status: String? = null

    @ColumnInfo(name = "email")
    var email: String? = null

    @ColumnInfo(name = "image_path")
    var image_path: String? = null

    @ColumnInfo(name = "from_date_long")
    var from_date_long: Long = 0

    @ColumnInfo(name = "to_date_long")
    var to_date_long: Long = 0


}
