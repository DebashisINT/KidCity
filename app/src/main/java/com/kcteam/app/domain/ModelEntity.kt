package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-Jun-20.
 */
@Entity(tableName = AppConstant.MODEL_TABLE)
class ModelEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "model_id")
    var model_id: String? = null

    @ColumnInfo(name = "model_name")
    var model_name: String? = null


}