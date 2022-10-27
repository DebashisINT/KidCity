package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.PAYMENT_MODE_TABLE)
class PaymentModeEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "payment_id")
    var payment_id: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null
}