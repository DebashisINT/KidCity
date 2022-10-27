package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant
import com.kcteam.features.logoutsync.presentation.LogoutSyncFragment
import com.kcteam.features.viewAllOrder.model.ProductOrder
import com.kcteam.features.viewAllOrder.orderNew.NewOdrScrListFragment
import com.kcteam.features.viewAllOrder.orderNew.NewOrderScrOrderDetailsFragment

@Dao
interface NewOrderScrOrderDao {
    @Insert
    fun insert(vararg newOrderScrOrderEntity: NewOrderScrOrderEntity)


    @Query("SELECT * FROM " + AppConstant.NEW_ORDER_ENTRY)
    fun getAll(): List<NewOrderScrOrderEntity>

    @Query("Select * from "+ AppConstant.NEW_ORDER_ENTRY +" where shop_id=:shop_id ")
    fun getShopOrderAll(shop_id:String): List<NewOrderScrOrderEntity>

    @Query("Select DISTINCT order_id,shop_id ,order_date from "+ AppConstant.NEW_ORDER_ENTRY +" order by order_date desc")
    fun getDistinctOrderShopAll(): List<NewOdrScrListFragment.ViewDataNewOdrScr>


    @Query("Select DISTINCT order_id,shop_id ,order_date from "+ AppConstant.NEW_ORDER_ENTRY +" where order_date=:order_date order by order_date desc")
    fun getDistinctOrderShopAllDateFiltered(order_date:String): List<NewOdrScrListFragment.ViewDataNewOdrScr>

    @Query("Select * from "+ AppConstant.NEW_ORDER_ENTRY +" where isUploaded=0 ")
    fun getUnSyncOrderAll(): List<NewOrderScrOrderEntity>


    @Query("Select DISTINCT order_id from "+ AppConstant.NEW_ORDER_ENTRY +" where shop_id=:shop_id order by order_date desc ")
    fun getShopOrderDistinct(shop_id:String): List<String>

    @Query("Select  qty from "+ AppConstant.NEW_ORDER_ENTRY +" where  order_id=:order_id ")
    fun getShopOrderQtyOrderIDWise(order_id:String): List<String>

    @Query("Select DISTINCT product_id  from "+ AppConstant.NEW_ORDER_ENTRY +" where order_id=:order_id ")
    fun getProductCodeDistinctByOrderID(order_id:String): List<String>

    @Query("Select DISTINCT color_id  from "+ AppConstant.NEW_ORDER_ENTRY +" where order_id=:order_id  and product_id=:product_id")
    fun getColorIDDistinctByOrderID(order_id:String,product_id:String): List<String>

    @Query("Select size,qty  from "+ AppConstant.NEW_ORDER_ENTRY +" where product_id=:product_id and color_id=:color_id and order_id=:order_id")
    fun getSizeQtyByProductColorID(order_id:String,product_id:String,color_id:String): List<ProductOrder>

    //@Query("Select size,qty  from "+ AppConstant.NEW_ORDER_ENTRY +" where product_id=:product_id and color_id=:color_id and order_id=:order_id and gender='Male' ")
    //@Query("Select size,qty  from "+ AppConstant.NEW_ORDER_ENTRY +" where product_id=:product_id and color_id=:color_id and order_id=:order_id and gender='MALE' ")
    @Query("Select size,qty  from "+ AppConstant.NEW_ORDER_ENTRY +" where product_id=:product_id and color_id=:color_id and order_id=:order_id and gender=:gender")
    fun getSizeQtyByProductColorIDMale(order_id:String,product_id:String,color_id:String,gender:String): List<ProductOrder>


    //@Query("Select size,qty  from "+ AppConstant.NEW_ORDER_ENTRY +" where product_id=:product_id and color_id=:color_id and order_id=:order_id and gender='Female' ")
    //@Query("Select size,qty  from "+ AppConstant.NEW_ORDER_ENTRY +" where product_id=:product_id and color_id=:color_id and order_id=:order_id and gender='FEMALE' ")
    @Query("Select size,qty  from "+ AppConstant.NEW_ORDER_ENTRY +" where product_id=:product_id and color_id=:color_id and order_id=:order_id and gender=:gender ")
    fun getSizeQtyByProductColorIDFemale(order_id:String,product_id:String,color_id:String,gender:String): List<ProductOrder>




    @Query("update "+ AppConstant.NEW_ORDER_ENTRY+ " set isUploaded = 1 where order_id=:order_id " )
    fun syncNewOrder(order_id:String)

    @Query("update "+ AppConstant.NEW_ORDER_ENTRY+ " set isUploaded = 1 where (order_id=:order_id and product_id=:product_id and gender=:gender and " +
            " size=:size and qty=:qty and shop_id=:shop_id and color_id=:color_id)" )
    fun syncNewOrderComplex(order_id:String,product_id: String,gender:String,size:String,qty:String,shop_id: String,color_id: String)



    @Query("Select order_date,isUploaded  from "+ AppConstant.NEW_ORDER_ENTRY+ " where order_id=:order_id " )
    fun getOrderIdDateStatus(order_id:String):NewOrderScrOrderDetailsFragment.OrderIDDateStatus




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<NewOrderScrOrderEntity>)

    @Query("update " + AppConstant.NEW_ORDER_ENTRY+" set product_name = UPPER(product_name) , gender = UPPER(gender), size = UPPER(size) , " +
            " color_name = UPPER(color_name) ")
    fun updateSizeNametoUpperCase()


    @Query("delete  from "+ AppConstant.NEW_ORDER_ENTRY )
    fun deleteAll()

    @Query("Select * from "+ AppConstant.NEW_ORDER_ENTRY +" where isUploaded=0 and order_id=:order_id")
    fun getUnSyncOrderAllByOrdID(order_id:String): List<NewOrderScrOrderEntity>

    @Query("Select DISTINCT order_id,shop_id,order_date from "+ AppConstant.NEW_ORDER_ENTRY +" where isUploaded=0 ")
    fun getUnSyncOrderAllUniqOrderID(): List<LogoutSyncFragment.NewOrderRoomDataLogoutPurpose>

    @Query("SELECT rate FROM " + AppConstant.NEW_ORDER_ENTRY + " where product_id=:product_id and order_id=:order_id")
    fun getNewOrderProductRateByOrdID(product_id:String,order_id:String): String

    @Query("SELECT qty FROM " + AppConstant.NEW_ORDER_ENTRY + " where product_id=:product_id and order_id=:order_id")
    fun getNewOrderProductQtyByOrdID(product_id:String,order_id:String):  String

/*    @Query("Select gender from "+ AppConstant.NEW_ORDER_ENTRY +" where order_id=:order_id")
    fun getUniqGenderForOrderID(order_id:String): List<ProductOrder>*/



    @Query("Select *  from "+ AppConstant.NEW_ORDER_ENTRY+ " where order_date=:order_date " )
    fun getRateListByDate(order_date:String):List<NewOrderScrOrderEntity>

}
