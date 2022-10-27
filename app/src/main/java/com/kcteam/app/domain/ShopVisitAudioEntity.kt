package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 21-Jul-20.
 */
@Entity(tableName = AppConstant.SHOP_VISIT_AUDIO_TABLE)
class ShopVisitAudioEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "audio")
    var audio: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "visit_datetime")
    var visit_datetime: String? = null
}