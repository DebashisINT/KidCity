package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 30-11-2018.
 */
@Entity(tableName = AppConstant.SELECTED_ROUTE_TYPE_SHOP_LIST_TABLE)
class SelectedRouteShopListEntity {
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

    @ColumnInfo(name = "date")
    var date: String? = null
}