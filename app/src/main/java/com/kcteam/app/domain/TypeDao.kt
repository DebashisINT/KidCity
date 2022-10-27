package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 22-Jun-20.
 */
@Dao
interface TypeDao {

    @Insert
    fun insert(vararg type: TypeEntity)

    @Query("DELETE FROM " + AppConstant.TYPE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.TYPE)
    fun getAll(): List<TypeEntity>

    @Query("SELECT * FROM " + AppConstant.TYPE + " where type_id=:type_id")
    fun getSingleType(type_id: String): TypeEntity

    @Query("SELECT * FROM " + AppConstant.TYPE + " where activity_id=:activity_id")
    fun getTypeActivityWise(activity_id: String): List<TypeEntity>
}