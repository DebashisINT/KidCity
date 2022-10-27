package com.kcteam.app.domain

import androidx.room.*
import com.kcteam.app.AppConstant
import com.kcteam.features.login.model.productlistmodel.ProductListDataModel

/**
 * Created by Saikat on 08-11-2018.
 */
@Dao
interface ProductListDao {

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE)
    fun getAll(): List<ProductListEntity>

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE +" where id IN (SELECT id FROM product_list group by brand)")
    fun getUniqueBrandList(): List<ProductListEntity>

    @Query("SELECT Distinct(brand) FROM " + AppConstant.PRODUCT_LIST_TABLE + " order by brand")
    fun getBrandList(): List<String>

    @Query("SELECT Distinct(brand) FROM " + AppConstant.PRODUCT_LIST_TABLE + " where category=:category COLLATE NOCASE")
    fun getBrandListAccordingToCategory(category: String): List<String>

    @Query("SELECT Distinct(category) FROM " + AppConstant.PRODUCT_LIST_TABLE)
    fun getCategoryList(): List<String>

    @Query("SELECT Distinct(category) FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand=:brand COLLATE NOCASE order by category")
    fun getCategoryListAccordingToBrand(brand: String): List<String>

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand_id=:brand_id and id IN (SELECT id FROM product_list group by category) COLLATE NOCASE order by category")
    fun getCategoryListAccordingToBrandId(brand_id: String): List<ProductListEntity>

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand=:brand and category=:category COLLATE NOCASE")
    fun getAllValueAccordingToCategoryBrand(brand: String, category: String): List<ProductListEntity>

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand_id=:brand_id and category_id=:category_id COLLATE NOCASE")
    fun getAllValueAccordingToCategoryBrandId(brand_id: String, category_id: String): List<ProductListEntity>

    /*@Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand=:brand and category=:category order by cast(watt as int)")
    fun getAllValueAccordingToCategoryBrandFilteredByWatt(brand: String, category: String): List<ProductListEntity>*/

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand=:brand and category=:category and watt=:watt COLLATE NOCASE")
    fun getAllValueAccordingToCategoryBrandFilteredByWatt(brand: String, category: String, watt: String): List<ProductListEntity>

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand_id=:brand_id and category_id=:category_id and watt_id=:watt_id COLLATE NOCASE")
    fun getAllValueAccordingToCategoryBrandFilteredByWattId(brand_id: String, category_id: String, watt_id: String): List<ProductListEntity>

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand=:brand COLLATE NOCASE")
    fun getAllValueAccordingToBrand(brand: String): List<ProductListEntity>

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand_id=:brand_id COLLATE NOCASE")
    fun getAllValueAccordingToBrandId(brand_id: String): List<ProductListEntity>

    /*@Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand=:brand order by cast(watt as int)")
    fun getAllValueAccordingToBrandWattWise(brand: String): List<ProductListEntity>*/

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand=:brand and watt=:watt COLLATE NOCASE")
    fun getAllValueAccordingToBrandWattWise(brand: String, watt: String): List<ProductListEntity>

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand_id=:brand_id and watt_id=:watt_id  COLLATE NOCASE")
    fun getAllValueAccordingToBrandWattIdWise(brand_id: String, watt_id: String): List<ProductListEntity>

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where category=:category")
    fun getAllValueAccordingToCategory(category: String): List<ProductListEntity>

    @Query("SELECT Distinct(watt) FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand=:brand COLLATE NOCASE")
    fun getWattListBrandWise(brand: String): List<String>

    /*@Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand_id=:brand_id and id IN (SELECT id FROM product_list group by watt) COLLATE NOCASE")
    fun getWattListBrandIdWise(brand_id: String): List<ProductListEntity>*/

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand_id=:brand_id COLLATE NOCASE group by watt")
    fun getWattListBrandIdWise(brand_id: String): List<ProductListEntity>

    @Query("SELECT Distinct(watt) FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand=:brand and category=:category COLLATE NOCASE")
    fun getWattListBrandCategoryWise(brand: String, category: String): List<String>

    /*@Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand_id=:brand_id  and category_id=:category_id and " +
            "id IN (SELECT id FROM product_list group by watt) COLLATE NOCASE")
    fun getWattListBrandCategoryIdWise(brand_id: String, category_id: String): List<ProductListEntity>*/

    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE + " where brand_id=:brand_id  and category_id=:category_id COLLATE NOCASE group by watt")
    fun getWattListBrandCategoryIdWise(brand_id: String, category_id: String): List<ProductListEntity>

    @Query("DELETE FROM " + AppConstant.PRODUCT_LIST_TABLE)
    fun deleteAllProduct()

    @Insert
    fun insert(vararg leaveType: ProductListEntity)

/*    @Insert
    fun insertAll(vararg product_list: ArrayList<ProductListEntity>)*/

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<ProductListEntity>)


    @Query("SELECT * FROM " + AppConstant.PRODUCT_LIST_TABLE +" where id=:id")
    fun getSingleProduct(id: Int): ProductListEntity
}