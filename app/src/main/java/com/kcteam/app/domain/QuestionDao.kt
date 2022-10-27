package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant


@Dao
interface QuestionDao {
    @Query("SELECT * FROM " + AppConstant.QUESTION_TABLE_MASTER)
    fun getAll(): List<QuestionEntity>

    @Insert
    fun insert(vararg stage: QuestionEntity)

    @Query("DELETE FROM " + AppConstant.QUESTION_TABLE_MASTER)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.QUESTION_TABLE_MASTER + " where question_id=:question_id")
    fun getSingleType(question_id: String): QuestionEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAllBulk(kist: List<QuestionEntity>)
}