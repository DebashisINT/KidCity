package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 15-11-2019.
 */
@Dao
interface BillingProductListDao {
    @Query("SELECT * FROM " + AppConstant.BILL_PRODUCT_LIST_TABLE)
    fun getAll(): List<BillingProductListEntity>

    /*@Query("SELECT * FROM " + AppConstant.BILL_PRODUCT_LIST_TABLE + " where order_id=:order_id and shop_id=:shop_id")
    fun getDataAccordingToShopAndOrderId(order_id: String, shop_id: String): List<BillingProductListEntity>*/

    @Query("SELECT * FROM " + AppConstant.BILL_PRODUCT_LIST_TABLE + " where bill_id=:bill_id")
    fun getDataAccordingToBillId(bill_id: String): List<BillingProductListEntity>

    @Query("SELECT * FROM " + AppConstant.BILL_PRODUCT_LIST_TABLE + " where order_id=:order_id")
    fun getDataAccordingToOrderId(order_id: String): List<BillingProductListEntity>

    @Insert
    fun insert(vararg billingProductList: BillingProductListEntity)

    @Query("DELETE FROM " + AppConstant.BILL_PRODUCT_LIST_TABLE)
    fun delete()
}