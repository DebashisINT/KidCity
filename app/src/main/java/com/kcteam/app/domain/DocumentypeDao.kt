package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface DocumentypeDao {

    @Query("SELECT * FROM " + AppConstant.DOCUMENT_TYPE_TABLE)
    fun getAll(): List<DocumentypeEntity>

    @Insert
    fun insert(vararg documentType: DocumentypeEntity)

    @Query("DELETE FROM " + AppConstant.DOCUMENT_TYPE_TABLE)
    fun delete()

    @Query("SELECT * FROM " + AppConstant.DOCUMENT_TYPE_TABLE + " where IsForOrganization= 1")
    fun getOrganizationList(): List<DocumentypeEntity>

    @Query("SELECT * FROM " + AppConstant.DOCUMENT_TYPE_TABLE + " where IsForOwn= 1")
    fun getOwnList(): List<DocumentypeEntity>

    @Query("SELECT * FROM " + AppConstant.DOCUMENT_TYPE_TABLE + " where type_id=:type_id ")
    fun isOwn(type_id:String): Boolean
}