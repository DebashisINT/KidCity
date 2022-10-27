package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant.*


/**
 * Created by Pratishruti on 07-12-2017.
// */
@Entity(tableName = MARKETING_IMAGE)
class MarketingDetailImageEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "marketing_img")
    var marketing_img: String? = null

    @ColumnInfo(name = "image_id")
    var image_id: String? = null
}