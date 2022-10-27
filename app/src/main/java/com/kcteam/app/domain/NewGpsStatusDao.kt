package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface NewGpsStatusDao {
    @Insert
    fun insert(vararg obj: NewGpsStatusEntity)

    @Query("Select * from new_gps_status where  isUploaded=:isUploaded")
    fun getNotUploaded(isUploaded: Boolean?): List<NewGpsStatusEntity>


    @Query("update new_gps_status set isUploaded=:isUploaded where id=:id")
    fun updateIsUploadedAccordingToId(isUploaded: Boolean, id: Int)

}