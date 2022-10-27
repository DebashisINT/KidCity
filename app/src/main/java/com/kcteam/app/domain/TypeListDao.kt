package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 22-Jun-20.
 */
@Dao
interface TypeListDao {

    @Insert
    fun insert(vararg type: TypeListEntity)

    @Query("DELETE FROM " + AppConstant.TYPE_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.TYPE_TABLE)
    fun getAll(): List<TypeListEntity>

    @Query("SELECT * FROM " + AppConstant.TYPE_TABLE + " where type_id=:type_id")
    fun getSingleType(type_id: String): TypeListEntity
}