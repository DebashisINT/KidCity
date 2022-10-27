package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant


@Dao
interface BillDtlsTeamDao {
    @Query("SELECT * FROM " + AppConstant.BILL_DTLS_TEAM)
    fun getAll(): List<BillDtlsTeamEntity>

    @Query("SELECT * FROM " + AppConstant.BILL_DTLS_TEAM + " where order_id=:order_id ORDER BY invoice_date DESC")
    fun getDataOrderIdWise(order_id: String): List<BillDtlsTeamEntity>


    @Query("SELECT invoice_no FROM  bill_dtls_team where order_id=:order_id ")
    fun getInvoice(order_id: String): String


    @Query("SELECT invoice_date FROM bill_dtls_team where order_id=:order_id")
    fun getInvoiceDate(order_id: String): String

    @Query("SELECT * FROM " + AppConstant.BILL_DTLS_TEAM + " where isUploaded=:isUploaded")
    fun getDataSyncWise(isUploaded: Boolean): List<BillDtlsTeamEntity>

    @Insert
    fun insertAll(vararg bill: BillDtlsTeamEntity)

    @Query("update " + AppConstant.BILL_DTLS_TEAM + " set isUploaded=:isUploaded where id=:id")
    fun updateIsUploaded(isUploaded: Boolean, id: Int)

    @Query("update " + AppConstant.BILL_DTLS_TEAM + " set isUploaded=:isUploaded where bill_id=:bill_id")
    fun updateIsUploadedBillingIdWise(isUploaded: Boolean, bill_id: String)

    @Query("update " + AppConstant.BILL_DTLS_TEAM + " set isEditUploaded=:isEditUploaded where id=:id")
    fun updateIsEdited(isEditUploaded: Int, id: Int)

    @Query("update " + AppConstant.BILL_DTLS_TEAM + " set attachment=:attachment where id=:id")
    fun updateAttachment(attachment: String, id: Int)

    @Query("DELETE FROM " + AppConstant.BILL_DTLS_TEAM)
    fun deleteAll()

    @Query("select SUM(invoice_amount) from bill_dtls_team where order_id=:order_id ")
    fun getInvoiceSumAmt(order_id: String): String


}