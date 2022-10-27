package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 05-10-2018.
 */
@Dao
interface UpdateStockDao {

    @Query("SELECT * FROM " + AppConstant.UPDATE_STOCK_TABLE)
    fun getAll(): List<UpdateStockEntity>

    @Query("SELECT * FROM " + AppConstant.UPDATE_STOCK_TABLE + " where isUploaded=:isUploaded")
    fun getStockAccordingToSyncState(isUploaded: Boolean): List<UpdateStockEntity>

    @Query("SELECT * FROM " + AppConstant.UPDATE_STOCK_TABLE + " where shop_id=:shop_id")
    fun getStockAccordingToShopId(shop_id: String): List<UpdateStockEntity>

    @Query("SELECT * FROM " + AppConstant.UPDATE_STOCK_TABLE + " where shop_id=:shop_id and closing_stock_month_val=:closing_stock_month_val and " +
            "closing_stock_year_val=:closing_stock_year_val and opening_stock_month_val=:opening_stock_month_val and opening_stock_year_val=:opening_stock_year_val")
    fun getStockAccordingToShopIdStockMonth(shop_id: String, closing_stock_month_val: String, closing_stock_year_val: String,
                                            opening_stock_month_val: String, opening_stock_year_val: String): UpdateStockEntity

    @Query("SELECT * FROM " + AppConstant.UPDATE_STOCK_TABLE + " where isUploaded=:isUploaded and shop_id=:shop_id")
    fun getStockAccordingToSyncStateId(isUploaded: Boolean, shop_id: String): List<UpdateStockEntity>

    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set isUploaded=:isUploaded where shop_id=:shop_id")
    fun updateIsUploaded(isUploaded: Boolean, shop_id: String)

    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set isUploaded=:isUploaded where id=:id")
    fun updateIsUploadedAccordingToId(isUploaded: Boolean, id: Int)

    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set isUploaded=:isUploaded where shop_id=:shop_id and closing_stock_month_val=" +
            ":closing_stock_month_val and closing_stock_year_val=:closing_stock_year_val and opening_stock_month_val=:opening_stock_month_val and " +
            "opening_stock_year_val=:opening_stock_year_val")
    fun updateIsUploadedForOneItem(isUploaded: Boolean, shop_id: String, closing_stock_month_val: String, closing_stock_year_val: String,
                                   opening_stock_month_val: String, opening_stock_year_val: String)

    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set closing_stock_amount=:closing_stock_amount where shop_id=:shop_id and closing_stock_month_val=" +
            ":closing_stock_month_val and closing_stock_year_val=:closing_stock_year_val")
    fun updateClosingAmount(closing_stock_amount: String, shop_id: String, closing_stock_month_val: String, closing_stock_year_val: String)

    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set opening_stock_amount=:opening_stock_amount where shop_id=:shop_id and opening_stock_month_val=" +
            ":opening_stock_month_val and opening_stock_year_val=:opening_stock_year_val")
    fun updateOpeningAmount(opening_stock_amount: String, shop_id: String, opening_stock_month_val: String, opening_stock_year_val: String)

    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set mo=:mo where shop_id=:shop_id and closing_stock_month_val=:closing_stock_month_val and " +
            "closing_stock_year_val=:closing_stock_year_val and opening_stock_month_val=:opening_stock_month_val and opening_stock_year_val=:opening_stock_year_val")
    fun updateMO(mo: String, shop_id: String, closing_stock_month_val: String, closing_stock_year_val: String,
                 opening_stock_month_val: String, opening_stock_year_val: String)

    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set po=:po where shop_id=:shop_id and closing_stock_month_val=:closing_stock_month_val and " +
            "closing_stock_year_val=:closing_stock_year_val and opening_stock_month_val=:opening_stock_month_val and opening_stock_year_val=:opening_stock_year_val")
    fun updatePO(po: String, shop_id: String, closing_stock_month_val: String, closing_stock_year_val: String,
                 opening_stock_month_val: String, opening_stock_year_val: String)

    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set co=:co where shop_id=:shop_id and closing_stock_month_val=:closing_stock_month_val and " +
            "closing_stock_year_val=:closing_stock_year_val and opening_stock_month_val=:opening_stock_month_val and opening_stock_year_val=:opening_stock_year_val")
    fun updateCO(co: String, shop_id: String, closing_stock_month_val: String, closing_stock_year_val: String,
                 opening_stock_month_val: String, opening_stock_year_val: String)

    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set description=:description where shop_id=:shop_id and closing_stock_month_val=" +
            ":closing_stock_month_val and closing_stock_year_val=:closing_stock_year_val and opening_stock_month_val=:opening_stock_month_val and " +
            "opening_stock_year_val=:opening_stock_year_val")
    fun updateDescription(description: String, shop_id: String, closing_stock_month_val: String, closing_stock_year_val: String,
                 opening_stock_month_val: String, opening_stock_year_val: String)


    @Query("update " + AppConstant.UPDATE_STOCK_TABLE + " set current_date=:current_date where shop_id=:shop_id and closing_stock_month_val=" +
            ":closing_stock_month_val and closing_stock_year_val=:closing_stock_year_val and opening_stock_month_val=:opening_stock_month_val and " +
            "opening_stock_year_val=:opening_stock_year_val")
    fun updateCurrentDate(current_date: String, shop_id: String, closing_stock_month_val: String, closing_stock_year_val: String,
                          opening_stock_month_val: String, opening_stock_year_val: String)

    @Insert
    fun insert(vararg updateStock: UpdateStockEntity)

    @Query("DELETE FROM " + AppConstant.UPDATE_STOCK_TABLE)
    fun delete()
}