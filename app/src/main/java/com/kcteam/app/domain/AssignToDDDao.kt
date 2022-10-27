package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 18-09-2018.
 */
@Dao
interface AssignToDDDao {

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_DD_TABLE)
    fun getAll(): List<AssignToDDEntity>

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_DD_TABLE+ " where pp_id=:pp_id")
    fun getAllDDFilterPP(pp_id: String): List<AssignToDDEntity>

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_DD_TABLE + " where dd_id=:dd_id")
    fun getSingleValue(dd_id: String): AssignToDDEntity

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_DD_TABLE + " where pp_id=:pp_id")
    fun getValuePPWise(pp_id: String): List<AssignToDDEntity>

    /*@Query("delete " + AppConstant.ASSIGNED_TO_DD_TABLE + " where dd_id=:dd_id")
    fun deleteDDId(dd_id: String)*/

    @Query("update " + AppConstant.ASSIGNED_TO_DD_TABLE + " set dd_name=:dd_name where dd_id=:dd_id")
    fun updateDDName(dd_id: String, dd_name: String)

    @Query("update " + AppConstant.ASSIGNED_TO_DD_TABLE + " set pp_id=:pp_id where dd_id=:dd_id")
    fun updatePPId(dd_id: String, pp_id: String)

    @Query("update " + AppConstant.ASSIGNED_TO_DD_TABLE + " set dd_phn_no=:dd_phn_no where dd_id=:dd_id")
    fun updateDDNo(dd_id: String, dd_phn_no: String)

    @Insert
    fun insert(vararg assignToPP: AssignToDDEntity)

    @Query("DELETE FROM " + AppConstant.ASSIGNED_TO_DD_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.ASSIGNED_TO_DD_TABLE + " where type_id=:type_id")
    fun getValueTypeWise(type_id: String): List<AssignToDDEntity>

    @Query("SELECT dd_name FROM " + AppConstant.ASSIGNED_TO_DD_TABLE + " where dd_id=:dd_id")
    fun getSingleDDValue(dd_id: String): AssignToDDEntity
}