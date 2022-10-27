package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface CurrentStockEntryProductDao {
    @Insert
    fun insert(vararg currentStockProductEntry: CurrentStockEntryProductModelEntity)

    @Query("Select * from "+ AppConstant.SHOP_CURRENT_STOCK_PRODUCTS_TABLE)
    fun getShopProductsStockAll(): List<CurrentStockEntryProductModelEntity>

    @Query("Select * from "+ AppConstant.SHOP_CURRENT_STOCK_PRODUCTS_TABLE+" where stock_id=:stock_id")
    fun getShopProductsStockAllByStockID(stock_id:String): List<CurrentStockEntryProductModelEntity>

    @Query("update "+ AppConstant.SHOP_CURRENT_STOCK_PRODUCTS_TABLE+" set isUploaded = 1 where stock_id=:stock_id")
    fun syncShopProductsStock(stock_id:String)
}