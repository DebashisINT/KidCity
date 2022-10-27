package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 19-02-2019.
 */
@Dao
interface BillingDao {
    @Query("SELECT * FROM " + AppConstant.BILLING_TABLE)
    fun getAll(): List<BillingEntity>

    @Query("SELECT * FROM " + AppConstant.BILLING_TABLE + " where order_id=:order_id ORDER BY invoice_date DESC")
    fun getDataOrderIdWise(order_id: String): List<BillingEntity>


    @Query("SELECT invoice_no FROM  billing_list where order_id=:order_id ")
    fun getInvoice(order_id: String): String


    @Query("SELECT invoice_date FROM billing_list where order_id=:order_id")
    fun getInvoiceDate(order_id: String): String

    @Query("SELECT * FROM " + AppConstant.BILLING_TABLE + " where isUploaded=:isUploaded")
    fun getDataSyncWise(isUploaded: Boolean): List<BillingEntity>

    @Insert
    fun insertAll(vararg bill: BillingEntity)

    @Query("update " + AppConstant.BILLING_TABLE + " set isUploaded=:isUploaded where id=:id")
    fun updateIsUploaded(isUploaded: Boolean, id: Int)

    @Query("update " + AppConstant.BILLING_TABLE + " set isUploaded=:isUploaded where bill_id=:bill_id")
    fun updateIsUploadedBillingIdWise(isUploaded: Boolean, bill_id: String)

    @Query("update " + AppConstant.BILLING_TABLE + " set isEditUploaded=:isEditUploaded where id=:id")
    fun updateIsEdited(isEditUploaded: Int, id: Int)

    @Query("update " + AppConstant.BILLING_TABLE + " set attachment=:attachment where id=:id")
    fun updateAttachment(attachment: String, id: Int)

    @Query("DELETE FROM " + AppConstant.BILLING_TABLE)
    fun deleteAll()

    @Query("select SUM(invoice_amount) from billing_list where order_id=:order_id ")
    fun getInvoiceSumAmt(order_id: String): String



    @Query("update billing_list set  invoice_amount=:invoice_amount where bill_id=:bill_id ")
    fun updateAmt(invoice_amount:String,bill_id: String)

    @Query("SELECT * FROM " + AppConstant.BILLING_TABLE + " where bill_id=:bill_id")
    fun getSingleBillData(bill_id: String): BillingEntity
}