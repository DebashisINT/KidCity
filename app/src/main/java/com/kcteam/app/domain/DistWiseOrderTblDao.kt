package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kcteam.app.AppConstant

@Dao
interface DistWiseOrderTblDao {

    @Query("SELECT * FROM " + AppConstant.TBL_DIST_WISE_ORD_REPORT)
    fun getAll(): List<DistWiseOrderTblEntity>


    @Query("SELECT * FROM " + AppConstant.TBL_DIST_WISE_ORD_REPORT+" where only_date=:only_date")
    fun getAllByToday(only_date:String): List<DistWiseOrderTblEntity>

    @Insert
    fun insertAll(vararg activity: DistWiseOrderTblEntity)

    @Query("DELETE FROM " + AppConstant.TBL_DIST_WISE_ORD_REPORT)
    fun deleteAll()

}