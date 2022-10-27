package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kcteam.app.AppConstant

@Dao
interface DocumentListDao {

    @Query("SELECT * FROM " + AppConstant.DOCUMENT_LIST_TABLE)
    fun getAll(): List<DocumentListEntity>

    @Insert
    fun insert(vararg documentType: DocumentListEntity)

    @Query("DELETE FROM " + AppConstant.DOCUMENT_LIST_TABLE)
    fun deleteAll()

    @Query("DELETE FROM " + AppConstant.DOCUMENT_LIST_TABLE+ " where attachment like '%http%' ")
    fun deleteAllHttp()

    @Query("update " + AppConstant.DOCUMENT_LIST_TABLE + " set isUploaded=:isUploaded where list_id=:list_id")
    fun updateIsUploaded(isUploaded: Boolean, list_id: String)

    @Query("SELECT * FROM " + AppConstant.DOCUMENT_LIST_TABLE + " where isUploaded=:isUploaded")
    fun getDocSyncWise(isUploaded: Boolean): List<DocumentListEntity>

    @Query("SELECT * FROM " + AppConstant.DOCUMENT_LIST_TABLE + " where type_id=:type_id")
    fun getDataTypeWise(type_id: String): List<DocumentListEntity>

    @Query("SELECT * FROM " + AppConstant.DOCUMENT_LIST_TABLE + " where list_id=:list_id")
    fun getSingleData(list_id: String): DocumentListEntity

    @Update
    fun update(vararg documentType: DocumentListEntity)

    @Query("DELETE FROM " + AppConstant.DOCUMENT_LIST_TABLE + " where list_id=:list_id")
    fun delete(list_id: String)

    @Query("update " + AppConstant.DOCUMENT_LIST_TABLE + " set attachment=:attachment where list_id=:list_id")
    fun updateAttachment(attachment: String, list_id: String)

}