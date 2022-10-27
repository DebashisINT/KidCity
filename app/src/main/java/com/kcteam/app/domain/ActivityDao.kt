package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kcteam.app.AppConstant

@Dao
interface ActivityDao {

    @Query("SELECT * FROM " + AppConstant.Activity)
    fun getAll(): List<ActivityEntity>

    @Insert
    fun insertAll(vararg activity: ActivityEntity)

    @Query("DELETE FROM " + AppConstant.Activity)
    fun deleteAll()

    @Query("update " + AppConstant.Activity + " set isUploaded=:isUploaded where activity_id=:activity_id")
    fun updateIsUploaded(isUploaded: Boolean, activity_id: String)

    @Query("SELECT * FROM " + AppConstant.Activity + " where date=:date")
    fun getDatewWise(date: String): List<ActivityEntity>

    @Query("SELECT * FROM " + AppConstant.Activity + " where date=:date and party_id=:party_id")
    fun getDateShopIdWise(date: String, party_id: String): List<ActivityEntity>

    @Query("SELECT * FROM " + AppConstant.Activity + " where party_id=:party_id and date>=:date_from and date<=:date_to")
    fun getDateRangeShopIdWise(date_from: String, date_to: String, party_id: String): List<ActivityEntity>

    @Query("SELECT * FROM " + AppConstant.Activity + " where party_id=:party_id")
    fun getShopIdWise(party_id: String): List<ActivityEntity>

    @Query("SELECT * FROM " + AppConstant.Activity + " where due_date=:due_date")
    fun getDueDateWise(due_date: String): List<ActivityEntity>

    @Query("SELECT * FROM " + AppConstant.Activity + " where due_date>=:due_date_from and due_date<=:due_date_to")
    fun getDueDateRangeWise(due_date_from: String, due_date_to: String): List<ActivityEntity>

    @Query("SELECT * FROM " + AppConstant.Activity + " where due_date=:due_date and party_id=:party_id")
    fun getDueDateShopIdWise(due_date: String, party_id: String): ActivityEntity

    @Query("SELECT * FROM " + AppConstant.Activity + " where isUploaded=:isUploaded")
    fun getDataSyncWise(isUploaded: Boolean): List<ActivityEntity>

    @Update
    fun updateActivity(activity: ActivityEntity)
}