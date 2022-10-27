package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 18-09-2018.
 */
@Dao
interface AssignToPPDao {

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_PP_TABLE)
    fun getAll(): List<AssignToPPEntity>

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_PP_TABLE + " where pp_id=:pp_id")
    fun getSingleValue(pp_id: String): AssignToPPEntity

    /*@Query("delete " + AppConstant.ASSIGNED_TO_PP_TABLE + " where pp_id=:pp_id")
    fun deletePPId(pp_id: String)*/

    @Insert
    fun insert(vararg assignToPP: AssignToPPEntity)

    @Query("DELETE FROM " + AppConstant.ASSIGNED_TO_PP_TABLE)
    fun delete()

}