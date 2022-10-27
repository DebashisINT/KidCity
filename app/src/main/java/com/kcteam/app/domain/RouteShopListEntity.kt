package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 22-11-2018.
 */
@Entity(tableName = AppConstant.ROUTE_SHOP_LIST_TABLE)
class RouteShopListEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "route_id")
    var route_id: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "shop_address")
    var shop_address: String? = null

    @ColumnInfo(name = "shop_name")
    var shop_name: String? = null

    @ColumnInfo(name = "shop_contact_no")
    var shop_contact_no: String? = null

    @ColumnInfo(name = "isSelected")
    var isSelected: Boolean = false
}