package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant


@Dao
interface OrderDtlsTeamDao {

    @Query("SELECT * FROM " + AppConstant.ORDER_DTLS_TEAM + " order by id desc")
    fun getAll(): List<OrderDtlsTeamEntity>

    @Query("SELECT * FROM " + AppConstant.ORDER_DTLS_TEAM + " where shop_id=:shop_id order by id desc")
    fun getListAccordingToShopId(shop_id: String): List<OrderDtlsTeamEntity>



    @Query("SELECT * FROM " + AppConstant.ORDER_DTLS_TEAM + " where isUploaded=:isUploaded and order_id=:order_id")
    fun getUnsyncListAccordingToOrderId(order_id: String, isUploaded: Boolean): List<OrderDtlsTeamEntity>

    @Query("SELECT * FROM " + AppConstant.ORDER_DTLS_TEAM + " where isUploaded=:isUploaded")
    fun getUnsyncedData(isUploaded: Boolean): List<OrderDtlsTeamEntity>

    @Query("SELECT MAX(CAST(amount as DOUBLE)) FROM " + AppConstant.ORDER_DTLS_TEAM + " where shop_id=:shop_id")
    fun getAmountAccordingToShopId(shop_id: String): String

    @Insert
    fun insert(vararg orderDetails: OrderDtlsTeamEntity)

    @Query("update " + AppConstant.ORDER_DTLS_TEAM + " set isUploaded=:isUploaded where order_id=:order_id")
    fun updateIsUploaded(isUploaded: Boolean, order_id: String)

    @Query("SELECT * FROM " + AppConstant.ORDER_DTLS_TEAM + " where only_date=:date order by id desc")
    fun getListAccordingDate(date: String): List<OrderDtlsTeamEntity>

    @Query("DELETE FROM " + AppConstant.ORDER_DTLS_TEAM)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.ORDER_DTLS_TEAM + " where order_id=:order_id")
    fun getSingleOrder(order_id: String): OrderDtlsTeamEntity

    @Query("select SUM(amount) from "+ AppConstant.ORDER_DTLS_TEAM +" where shop_id=:shop_id ")
    fun getOrderSumAmt(shop_id: String): String

    @Query("select amount from "+ AppConstant.ORDER_DTLS_TEAM +" where order_id=:order_id ")
    fun getOrderAmt(order_id: String): String

    @Query("select distinct shop_id from order_dtls_team")
    fun getDistinctShopIDTeam(): List<String>

    @Query("select * from order_dtls_team where only_date=:only_date and shop_id=:shop_id ")
    fun getAllByOnlyDateTeam(only_date:String,shop_id:String): List<OrderDtlsTeamEntity>

}