package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

/**
 * Created by Saikat on 03-Jul-20.
 */
@Dao
interface MemberDao {

    @Query("SELECT * FROM " + AppConstant.MEMBER_TABLE)
    fun getAll(): List<MemberEntity>

    @Query("SELECT * FROM " + AppConstant.MEMBER_TABLE + " where user_id=:user_id")
    fun getSingleUserMember(user_id: String): List<MemberEntity>

    @Query("SELECT * FROM " + AppConstant.MEMBER_TABLE + " where super_id=:super_id")
    fun getUserSuperWise(super_id: String): List<MemberEntity>

    @Insert
    fun insertAll(vararg member: MemberEntity)

    @Query("DELETE FROM " + AppConstant.MEMBER_TABLE)
    fun deleteAll()

}


