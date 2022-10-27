package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 09-01-2020.
 */
@Dao
interface AddDoctorDao {
    @Query("SELECT * FROM " + AppConstant.DOCTOR_VISIT_LIST_TABLE)
    fun getAll(): List<AddDoctorEntity>

    @Insert
    fun insertAll(vararg addChem: AddDoctorEntity)

    @Query("DELETE FROM " + AppConstant.DOCTOR_VISIT_LIST_TABLE)
    fun deleteAll()

    @Query("update " + AppConstant.DOCTOR_VISIT_LIST_TABLE + " set isUploaded=:isUploaded where doc_visit_id=:doc_visit_id")
    fun updateIsUploaded(isUploaded: Boolean, doc_visit_id: String)

    @Query("SELECT * FROM " + AppConstant.DOCTOR_VISIT_LIST_TABLE + " where isUploaded=:isUploaded")
    fun getDataSyncWise(isUploaded: Boolean): List<AddDoctorEntity>

    @Query("SELECT * FROM " + AppConstant.DOCTOR_VISIT_LIST_TABLE + " where shop_id=:shop_id order by visit_date desc")
    fun getDataShopIdWise(shop_id: String): List<AddDoctorEntity>

    @Update
    fun updateDoc(vararg addDoc: AddDoctorEntity)
}