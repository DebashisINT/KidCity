package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant


@Dao
interface ProspectDao {
    @Query("SELECT * FROM " + AppConstant.PROSPECT_TABLE_MASTER)
    fun getAll(): List<ProspectEntity>


    @Query("DELETE FROM " + AppConstant.PROSPECT_TABLE_MASTER)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.PROSPECT_TABLE_MASTER + " where pros_id=:pros_id")
    fun getSingleType(pros_id: String): ProspectEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<ProspectEntity>)

    @Query("SELECT pros_name FROM " + AppConstant.PROSPECT_TABLE_MASTER + " where pros_id=:pros_id")
    fun getProsNameByProsId(pros_id: String): String
}