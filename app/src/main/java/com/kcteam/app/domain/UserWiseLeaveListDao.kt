package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserWiseLeaveListDao {

    @Insert
    fun insert(vararg obj: UserWiseLeaveListEntity)

    @Query("Delete from tbl_user_wise_leave_list")
    fun deleteAll()

    @Query("select * from tbl_user_wise_leave_list order by from_date_modified DESC")
    fun getListLeaveStartDateWise():List<UserWiseLeaveListEntity>

}