package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 11-09-2019.
 */
@Dao
interface StockProductListDao {

    @Query("SELECT * FROM " + AppConstant.STOCK_PRODUCT_LIST)
    fun getAll(): List<StockProductListEntity>

    @Query("SELECT * FROM " + AppConstant.STOCK_PRODUCT_LIST + " where stock_id=:stock_id and shop_id=:shop_id")
    fun getDataAccordingToShopAndStockId(stock_id: String, shop_id: String): List<StockProductListEntity>

    @Query("SELECT * FROM " + AppConstant.STOCK_PRODUCT_LIST + " where stock_id=:stock_id")
    fun getDataAccordingToStockId(stock_id: String): List<StockProductListEntity>

    @Insert
    fun insert(vararg stockProductList: StockProductListEntity)

    @Query("DELETE FROM " + AppConstant.STOCK_PRODUCT_LIST)
    fun delete()
}