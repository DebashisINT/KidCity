package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 08-01-2020.
 */
@Dao
interface AddChemistProductListDao {

    @Query("SELECT * FROM " + AppConstant.CHEMIST_VISIT_PRODUCT_TABLE)
    fun getAll(): List<AddChemistProductListEntity>

    @Insert
    fun insertAll(vararg addChemProd: AddChemistProductListEntity)

    @Query("DELETE FROM " + AppConstant.CHEMIST_VISIT_PRODUCT_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.CHEMIST_VISIT_PRODUCT_TABLE + " where chemist_visit_id=:chemist_visit_id")
    fun getDataIdWise(chemist_visit_id: String): List<AddChemistProductListEntity>

    @Query("SELECT * FROM " + AppConstant.CHEMIST_VISIT_PRODUCT_TABLE + " where chemist_visit_id=:chemist_visit_id and isPob=:isPob")
    fun getDataIdPodWise(chemist_visit_id: String, isPob: Boolean): List<AddChemistProductListEntity>

    @Query("DELETE FROM " + AppConstant.CHEMIST_VISIT_PRODUCT_TABLE + " where chemist_visit_id=:chemist_visit_id and isPob=:isPob")
    fun deleteIdPodWise(chemist_visit_id: String, isPob: Boolean)
}