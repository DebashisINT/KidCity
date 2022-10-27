package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 26-10-2018.
 */
@Dao
interface CollectionDetailsDao {

    @Query("SELECT * FROM " + AppConstant.COLLECTION_LIST_TABLE + " order by id desc")
    fun getAll(): List<CollectionDetailsEntity>

    @Query("SELECT * FROM " + AppConstant.COLLECTION_LIST_TABLE + " where shop_id=:shop_id order by id desc")
    fun getListAccordingToShopId(shop_id: String): List<CollectionDetailsEntity>

    @Query("SELECT * FROM " + AppConstant.COLLECTION_LIST_TABLE + " where isUploaded=:isUploaded and collection_id=:collection_id")
    fun getUnsyncListAccordingToOrderId(collection_id: String, isUploaded: Boolean): List<CollectionDetailsEntity>

    @Insert
    fun insert(vararg collectionDetails: CollectionDetailsEntity)

    @Query("update " + AppConstant.COLLECTION_LIST_TABLE + " set isUploaded=:isUploaded where collection_id=:collection_id")
    fun updateIsUploaded(isUploaded: Boolean, collection_id: String)

    @Query("SELECT * FROM " + AppConstant.COLLECTION_LIST_TABLE + " where isUploaded=:isUploaded")
    fun getUnsyncCollection(isUploaded: Boolean): List<CollectionDetailsEntity>

    @Query("SELECT * FROM " + AppConstant.COLLECTION_LIST_TABLE + " where date=:date order by id desc")
    fun getDateWiseCollection(date: String): List<CollectionDetailsEntity>

    @Query("DELETE FROM " + AppConstant.COLLECTION_LIST_TABLE)
    fun delete()

    @Query("update " + AppConstant.COLLECTION_LIST_TABLE + " set file_path=:file_path where collection_id=:collection_id")
    fun updateAttachment(file_path: String, collection_id: String)

    @Query("SELECT * FROM " + AppConstant.COLLECTION_LIST_TABLE + " where order_id=:order_id")
    fun getListOrderWise(order_id: String): List<CollectionDetailsEntity>





    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<CollectionDetailsEntity>)


    @Query("select SUM(collection) from collection_list where shop_id=:shop_id ")
    fun getCollectSumAmt(shop_id: String): String

    @Query("select SUM(collection) from collection_list where order_id=:order_id ")
    fun getCollectSumAmtByOrdID(order_id: String): String

}