package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 12-Jun-20.
 */
@Dao
interface QuotationDao {

    @Insert
    fun insert(vararg quot: QuotationEntity)

    @Query("DELETE FROM " + AppConstant.QUOTATION_TABLE)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.QUOTATION_TABLE + " order by date desc")
    fun getAll(): List<QuotationEntity>

    @Query("SELECT * FROM " + AppConstant.QUOTATION_TABLE + " where quo_id=:quo_id")
    fun getSingleQuotation(quo_id: String): QuotationEntity

    @Query("SELECT * FROM " + AppConstant.QUOTATION_TABLE + " where shop_id=:shop_id order by date desc")
    fun getSingleShopQuotation(shop_id: String): List<QuotationEntity>

    @Query("update " + AppConstant.QUOTATION_TABLE + " set isUploaded=:isUploaded where quo_id=:quo_id")
    fun updateIsUploaded(isUploaded: Boolean, quo_id: String)

    @Query("update " + AppConstant.QUOTATION_TABLE + " set isEditUpdated=:isEditUpdated where quo_id=:quo_id")
    fun updateIsEdit(isEditUpdated: Int, quo_id: String)

    @Query("SELECT * FROM " + AppConstant.QUOTATION_TABLE + " where isUploaded=:isUploaded")
    fun getQuotSyncWise(isUploaded: Boolean): List<QuotationEntity>

    @Query("SELECT * FROM " + AppConstant.QUOTATION_TABLE + " where date=:date order by id desc")
    fun getQuotDateWise(date: String): List<QuotationEntity>

    @Query("SELECT * FROM " + AppConstant.QUOTATION_TABLE + " where date=:date and isUploaded=:isUploaded")
    fun getQuotDateSyncWise(date: String, isUploaded: Boolean): List<QuotationEntity>

    @Query("SELECT * FROM " + AppConstant.QUOTATION_TABLE + " where date=:date and isEditUpdated=:isEditUpdated")
    fun getQuotDateEditSyncWise(date: String, isEditUpdated: Int): List<QuotationEntity>

    @Query("SELECT * FROM " + AppConstant.QUOTATION_TABLE + " where isEditUpdated=:isEditUpdated")
    fun getQuotEditSyncWise(isEditUpdated: Int): List<QuotationEntity>

    @Update
    fun updateQuot(quot: QuotationEntity)
}