package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 09-01-2020.
 */
@Dao
interface AddDoctorProductListDao {

    @Query("SELECT * FROM " + AppConstant.DOCTOR_VISIT_PRODUCT_TABLE)
    fun getAll(): List<AddDoctorProductListEntity>

    @Insert
    fun insertAll(vararg addChemProd: AddDoctorProductListEntity)

    @Query("DELETE FROM " + AppConstant.DOCTOR_VISIT_PRODUCT_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.DOCTOR_VISIT_PRODUCT_TABLE + " where doc_visit_id=:doc_visit_id")
    fun getDataIdWise(doc_visit_id: String): List<AddDoctorProductListEntity>

    @Query("SELECT * FROM " + AppConstant.DOCTOR_VISIT_PRODUCT_TABLE + " where doc_visit_id=:doc_visit_id and product_status=:product_status")
    fun getDataIdPodWise(doc_visit_id: String, product_status: Int): List<AddDoctorProductListEntity>

    @Query("DELETE FROM " + AppConstant.DOCTOR_VISIT_PRODUCT_TABLE + " where doc_visit_id=:doc_visit_id and product_status=:product_status")
    fun deleteStatusDocIdWise(doc_visit_id: String, product_status: Int)
}