package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 23-Sep-18.
 */
@Dao
interface OrderDetailsListDao {

    @Query("SELECT * FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE + " order by id desc")
    fun getAll(): List<OrderDetailsListEntity>

    @Query("SELECT * FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE + " where shop_id=:shop_id order by id desc")
    fun getListAccordingToShopId(shop_id: String): List<OrderDetailsListEntity>

    @Query("SELECT * FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE + " where SUBSTR(order_details_list.date , 1,10)>=:date_start and SUBSTR(order_details_list.date , 1,10)<=:date_end and shop_id=:shop_id order by id desc")
    fun getListAccordingTodateonOrderDD(date_start:String,date_end:String,shop_id: String): List<OrderDetailsListEntity>

    @Query("SELECT DISTINCT(shop_detail.assigned_to_pp_id) FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE +
            " inner join shop_detail on shop_detail.shop_id = order_details_list.shop_id  where SUBSTR(order_details_list.date , 1,10)>=:date_start and SUBSTR(order_details_list.date , 1,10)<=:date_end ")
    fun getListAccordingTodateonOrderPP(date_start:String,date_end:String): List<String>

    @Query("SELECT * FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE + " where isUploaded=:isUploaded and order_id=:order_id")
    fun getUnsyncListAccordingToOrderId(order_id: String, isUploaded: Boolean): List<OrderDetailsListEntity>

    @Query("SELECT * FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE + " where isUploaded=:isUploaded")
    fun getUnsyncedData(isUploaded: Boolean): List<OrderDetailsListEntity>

    @Query("SELECT MAX(CAST(amount as DOUBLE)) FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE + " where shop_id=:shop_id")
    fun getAmountAccordingToShopId(shop_id: String): String

    @Insert
    fun insert(vararg orderDetails: OrderDetailsListEntity)

    @Query("update " + AppConstant.ORDER_DETAILS_LIST_TABLE + " set isUploaded=:isUploaded where order_id=:order_id")
    fun updateIsUploaded(isUploaded: Boolean, order_id: String)

    @Query("SELECT * FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE + " where only_date=:date order by id desc")
    fun getListAccordingDate(date: String): List<OrderDetailsListEntity>

    @Query("DELETE FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.ORDER_DETAILS_LIST_TABLE + " where order_id=:order_id")
    fun getSingleOrder(order_id: String): OrderDetailsListEntity

    @Query("select SUM(amount) from order_details_list where shop_id=:shop_id ")
    fun getOrderSumAmt(shop_id: String): String

    @Query("select amount from order_details_list where order_id=:order_id ")
    fun getOrderAmt(order_id: String): String

    @Query("select order_id from order_details_list where order_id=:order_id ")
    fun getOrderId(order_id: String): String

    @Query("select only_date from order_details_list where order_id=:order_id ")
    fun getOrderDate(order_id: String): String

    @Query("select distinct shop_id from order_details_list where only_date !=:only_date")
    fun getDistinctShopIDExceptCurrDate(only_date:String): List<String>

    @Query("select * from order_details_list where only_date=:only_date and shop_id=:shop_id ")
    fun getAllByOnlyDate(only_date:String,shop_id:String): List<OrderDetailsListEntity>

}