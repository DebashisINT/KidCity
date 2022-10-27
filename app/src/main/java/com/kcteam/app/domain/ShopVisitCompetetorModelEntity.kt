package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.SHOP_VISIT_COMPETETOR_IMAGE_TABLE)
class ShopVisitCompetetorModelEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "session_token")
    var session_token: String? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "user_id")
    var user_id: String? = null

    @ColumnInfo(name = "shop_image")
    var shop_image: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "visited_date")
    var visited_date: String? = null
}