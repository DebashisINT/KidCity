package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-Jun-20.
 */
@Dao
interface ModelDao {

    @Query("SELECT * FROM " + AppConstant.MODEL_TABLE)
    fun getAll(): List<ModelEntity>

    @Insert
    fun insertAll(vararg model: ModelEntity)

    @Query("DELETE FROM " + AppConstant.MODEL_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.MODEL_TABLE + " where model_id=:model_id")
    fun getSingleType(model_id: String): ModelEntity


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAllLarge(model: List<ModelEntity>)
}