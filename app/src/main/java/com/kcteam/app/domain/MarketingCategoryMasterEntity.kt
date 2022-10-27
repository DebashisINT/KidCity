package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant.*


/**
 * Created by Pratishruti on 07-12-2017.
// */
@Entity(tableName = MARKETING_CATEGORY_MASTER_TABLE)
class MarketingCategoryMasterEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    var id: Int? = null

    @ColumnInfo(name = "material_id")
    var material_id:String? = null

    @ColumnInfo(name = "material_name")
    var material_name: String? = null

    @ColumnInfo(name = "type_id")
    var type_id: String? = null

}