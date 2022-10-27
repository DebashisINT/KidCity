package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant


/**
 * Created by Saikat on 11-09-2019.
 */
@Dao
interface StockDetailsListDao {

    @Query("SELECT * FROM " + AppConstant.STOCK_DETAILS_LIST + " order by id desc")
    fun getAll(): List<StockDetailsListEntity>

    @Query("SELECT * FROM " + AppConstant.STOCK_DETAILS_LIST + " where shop_id=:shop_id order by id desc")
    fun getListAccordingToShopId(shop_id: String): List<StockDetailsListEntity>

    @Query("SELECT * FROM " + AppConstant.STOCK_DETAILS_LIST + " where isUploaded=:isUploaded and stock_id=:stock_id")
    fun getUnsyncListAccordingToStockId(stock_id: String, isUploaded: Boolean): List<StockDetailsListEntity>

    @Query("SELECT * FROM " + AppConstant.STOCK_DETAILS_LIST + " where isUploaded=:isUploaded")
    fun getUnsyncedData(isUploaded: Boolean): List<StockDetailsListEntity>

    @Query("SELECT MAX(CAST(amount as DOUBLE)) FROM " + AppConstant.STOCK_DETAILS_LIST + " where shop_id=:shop_id")
    fun getAmountAccordingToShopId(shop_id: String): String

    @Insert
    fun insert(vararg stockDetails: StockDetailsListEntity)

    @Query("update " + AppConstant.STOCK_DETAILS_LIST + " set isUploaded=:isUploaded where stock_id=:stock_id")
    fun updateIsUploaded(isUploaded: Boolean, stock_id: String)

    @Query("SELECT * FROM " + AppConstant.STOCK_DETAILS_LIST + " where only_date=:date order by id desc")
    fun getListAccordingDate(date: String): List<StockDetailsListEntity>

    @Query("DELETE FROM " + AppConstant.STOCK_DETAILS_LIST)
    fun delete()
}