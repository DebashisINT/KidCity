package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant.MARKETING_CATEGORY_TABLE


/**
 * Created by Pratishruti on 07-12-2017.
// */
@Entity(tableName = MARKETING_CATEGORY_TABLE)
class MarketingDetailEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "material_id")
    var material_id:String? = null

    @ColumnInfo(name = "material_name")
    var material_name: String? = null

    @ColumnInfo(name = "date")
    var date: String? =null

    @ColumnInfo(name = "typeid")
    var typeid: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

}