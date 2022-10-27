package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant


@Dao
interface CollDtlsTeamDao {

    @Query("SELECT * FROM " + AppConstant.COLL_DTLS_TEAM + " order by id desc")
    fun getAll(): List<CollDtlsTeamEntity>

    @Query("SELECT * FROM " + AppConstant.COLL_DTLS_TEAM + " where shop_id=:shop_id order by id desc")
    fun getListAccordingToShopId(shop_id: String): List<CollDtlsTeamEntity>

    @Query("SELECT * FROM " + AppConstant.COLL_DTLS_TEAM + " where isUploaded=:isUploaded and collection_id=:collection_id")
    fun getUnsyncListAccordingToOrderId(collection_id: String, isUploaded: Boolean): List<CollDtlsTeamEntity>

    @Insert
    fun insert(vararg collectionDetails: CollDtlsTeamEntity)

    @Query("update " + AppConstant.COLL_DTLS_TEAM + " set isUploaded=:isUploaded where collection_id=:collection_id")
    fun updateIsUploaded(isUploaded: Boolean, collection_id: String)

    @Query("SELECT * FROM " + AppConstant.COLL_DTLS_TEAM + " where isUploaded=:isUploaded")
    fun getUnsyncCollection(isUploaded: Boolean): List<CollectionDetailsEntity>

    @Query("SELECT * FROM " + AppConstant.COLL_DTLS_TEAM + " where date=:date order by id desc")
    fun getDateWiseCollection(date: String): List<CollectionDetailsEntity>

    @Query("DELETE FROM " + AppConstant.COLL_DTLS_TEAM)
    fun delete()

    @Query("update " + AppConstant.COLL_DTLS_TEAM + " set file_path=:file_path where collection_id=:collection_id")
    fun updateAttachment(file_path: String, collection_id: String)

    @Query("SELECT * FROM " + AppConstant.COLL_DTLS_TEAM + " where order_id=:order_id")
    fun getListOrderWise(order_id: String): List<CollDtlsTeamEntity>





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<CollDtlsTeamEntity>)


    @Query("select SUM(collection) from coll_dtls_team where shop_id=:shop_id ")
    fun getCollectSumAmt(shop_id: String): String

    @Query("select SUM(collection) from coll_dtls_team where order_id=:order_id ")
    fun getCollectSumAmtByOrdID(order_id: String): String

}