package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant.SHOP_ACTIVITY

/**
 * Created by Pratishruti on 07-12-2017.
 */
@Entity(tableName = SHOP_ACTIVITY)
class ViewAllOrderListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "itemId")
    var itemId: Int = 0

    @ColumnInfo(name = "date")
    var date: String? = null

    @ColumnInfo(name = "amount")
    var amount: String? = null

}