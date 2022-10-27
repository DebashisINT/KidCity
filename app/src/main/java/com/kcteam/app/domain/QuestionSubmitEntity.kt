package com.kcteam.app.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kcteam.app.AppConstant


@Entity(tableName = AppConstant.QUESTION_TABLE_SUBMIT)
class QuestionSubmitEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "shop_id")
    var shop_id: String? = null

    @ColumnInfo(name = "question_id")
    var question_id: String? = null

    @ColumnInfo(name = "answer")
    var answer: String? = null

    @ColumnInfo(name = "isUploaded")
    var isUploaded: Boolean = false

    @ColumnInfo(name = "isUpdateToUploaded")
    var isUpdateToUploaded: Boolean = false
}