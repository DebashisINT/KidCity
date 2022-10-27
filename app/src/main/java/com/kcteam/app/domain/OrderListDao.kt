package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 23-Sep-18.
 */
@Dao
interface OrderListDao {

    @Query("SELECT * FROM " + AppConstant.ORDER_LIST_TABLE)
    fun getAll(): List<OrderListEntity>

    @Query("SELECT * FROM " + AppConstant.ORDER_LIST_TABLE + " where date=:date")
    fun getListAccordingToDate(date: String): List<OrderListEntity>

    @Query("SELECT * FROM " + AppConstant.ORDER_LIST_TABLE + " where shop_id=:shop_id")
    fun getListAccordingToShopID(shop_id: String): List<OrderListEntity>

    //@Query("SELECT sum(order_amount) as total, * FROM " + AppConstant.ORDER_LIST_TABLE + " where date_long between :start_date and :end_date group by shop_id ORDER BY CAST(order_amount as DOUBLE) desc")
    @Query("SELECT * FROM " + AppConstant.ORDER_LIST_TABLE + " where date_long between :start_date and :end_date ORDER BY CAST(order_amount as DOUBLE) desc")
    //@Query("SELECT * FROM " + AppConstant.ORDER_LIST_TABLE + " where date_long between :start_date and :end_date group by shop_id")
    fun getListAccordingToDateRange(start_date: Long, end_date: Long): List<OrderListEntity>

    @Query("update " + AppConstant.ORDER_LIST_TABLE + " set date=:date where shop_id=:shop_id")
    fun updateDate(date: String, shop_id: String)

    @Query("update " + AppConstant.ORDER_LIST_TABLE + " set date_long=:date_long where shop_id=:shop_id")
    fun updateDateLong(date_long: Long, shop_id: String)

    @Insert
    fun insert(vararg order: OrderListEntity)

    @Query("DELETE FROM " + AppConstant.ORDER_LIST_TABLE)
    fun delete()
}