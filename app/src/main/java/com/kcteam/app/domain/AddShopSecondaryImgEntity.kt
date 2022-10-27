package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

@Entity(tableName = AppConstant.ADDSHOP_SECONDARY_IMG_TABLE)
class AddShopSecondaryImgEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "lead_shop_id")
    var lead_shop_id: String? = null

//    rubylead_image1,rubylead_image2(competitor_img1,competitor_img2 replace to 1st one)

    @ColumnInfo(name = "rubylead_image1")
    var rubylead_image1: String? = null

    @ColumnInfo(name = "rubylead_image2")
    var rubylead_image2: String? = null

    @ColumnInfo(name = "isUploaded_image1")
    var isUploaded_image1: Boolean = false

    @ColumnInfo(name = "isUploaded_image2")
    var isUploaded_image2: Boolean = false


}