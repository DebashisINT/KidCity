package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface EntityTypeDao {

    @Query("SELECT * FROM " + AppConstant.ENTITY_LIST_TABLE)
    fun getAll(): List<EntityTypeEntity>

    @Insert
    fun insert(vararg entityType: EntityTypeEntity)

    @Query("DELETE FROM " + AppConstant.ENTITY_LIST_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.ENTITY_LIST_TABLE + " where entity_id=:entity_id")
    fun getSingleItem(entity_id: String): EntityTypeEntity
}