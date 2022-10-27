package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.kcteam.app.AppConstant

@Dao
interface ShopDtlsTeamDao {

    @Insert
    fun insert(vararg obj: ShopDtlsTeamEntity)

    @Query("DELETE FROM " + AppConstant.SHOP_DTLS_TEAM)
    fun deleteAll()


    @Query("select * FROM " + AppConstant.SHOP_DTLS_TEAM )
    fun getAll():List<ShopDtlsTeamEntity>


    @Query("select distinct shop_dtls_team.* from shop_dtls_team inner join order_dtls_team on shop_dtls_team.shop_id = order_dtls_team.shop_id ")
    fun getShopIdHasOrder(): List<ShopDtlsTeamEntity?>

    @Query("Select * from shop_dtls_team where shop_id=:shopId")
    fun getShopByIdN(shopId: String?): ShopDtlsTeamEntity?
}