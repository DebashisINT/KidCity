package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 12-Jun-20.
 */
@Entity(tableName = AppConstant.BSLIST_TABLE)
class BSListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "bs_id")
    var bs_id: String? = null

    @ColumnInfo(name = "bs_name")
    var bs_name: String? = null
}