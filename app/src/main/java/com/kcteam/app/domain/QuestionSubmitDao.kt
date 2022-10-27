package com.kcteam.app.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kcteam.app.AppConstant
import com.kcteam.features.addshop.model.QuestionSubmit


@Dao
interface QuestionSubmitDao {
    @Query("SELECT * FROM " + AppConstant.QUESTION_TABLE_SUBMIT)
    fun getAll(): List<QuestionSubmitEntity>

    @Insert
    fun insert(vararg stage: QuestionSubmitEntity)

    @Query("DELETE FROM " + AppConstant.QUESTION_TABLE_SUBMIT)
    fun deleteAll()

    @Query("SELECT * FROM " + AppConstant.QUESTION_TABLE_SUBMIT + " where question_id=:question_id")
    fun getSingleType(question_id: String): QuestionSubmitEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAllBulk(kist: List<QuestionSubmitEntity>)

    @Query("Select * from " + AppConstant.QUESTION_TABLE_SUBMIT + " where isUploaded=:isUploaded ")
    fun getUnSync(isUploaded: Boolean): List<QuestionSubmitEntity>

    @Query("update question_list_submit set isUploaded=:isUploaded where shop_id=:shopId")
    fun updateIsUploaded(isUploaded: Boolean?, shopId: String?)


    @Query("Select DISTINCT(shop_id) from question_list_submit where isUploaded=:isUploaded ")
    fun getUnSyncUniqShopID(isUploaded: Boolean): List<String>


    @Query("Select question_id,answer  from " + AppConstant.QUESTION_TABLE_SUBMIT + " where shop_id=:shop_id and isUploaded=:isUploaded ")
    fun getQsAnsByShopID(shop_id: String,isUploaded:Boolean): List<QuestionSubmit>

    @Query("Select question_id,answer  from " + AppConstant.QUESTION_TABLE_SUBMIT + " where shop_id=:shop_id ")
    fun getQsAnsByShopIDToInt(shop_id: String): List<QuestionSubmit>

    @Query("Select question_id,answer  from " + AppConstant.QUESTION_TABLE_SUBMIT + " where shop_id=:shop_id and question_id=:question_id")
    fun getQsAnsByShopID(shop_id: String,question_id:String): QuestionSubmit

    @Query("update " + AppConstant.QUESTION_TABLE_SUBMIT + " set answer=:answer where shop_id=:shop_id and question_id=:question_id and isUploaded=:isUploaded ")
    fun updateAnswerByQueAndShopId(answer:String,question_id: String, shop_id: String,isUploaded:Boolean)


    @Query("update " + AppConstant.QUESTION_TABLE_SUBMIT + " set answer=:answer,isUpdateToUploaded=:isUpdateToUploaded where shop_id=:shop_id and question_id=:question_id ")
    fun updateAnswerByQueAndShopIdNew(answer:String,question_id: String, shop_id: String,isUpdateToUploaded:Boolean)

    @Query("Select DISTINCT(shop_id) from question_list_submit where isUpdateToUploaded=:isUpdateToUploaded ")
    fun getUnSyncUpdatedUniqShopID(isUpdateToUploaded: Boolean): List<String>

    @Query("Select question_id,answer  from " + AppConstant.QUESTION_TABLE_SUBMIT + " where shop_id=:shop_id and isUpdateToUploaded=:isUpdateToUploaded ")
    fun getQsAnsUpdatedByShopID(shop_id: String,isUpdateToUploaded:Boolean): List<QuestionSubmit>

    @Query("update question_list_submit set isUpdateToUploaded=:isUpdateToUploaded where shop_id=:shopId")
    fun updateIsUpdateUploaded(isUpdateToUploaded: Boolean?, shopId: String?)

    @Query("update question_list_submit set isUpdateToUploaded=:isUpdateToUploaded ")
    fun updateisUpdateToUploadedLogin(isUpdateToUploaded: Boolean?)



    @Query("update question_list_submit set answer=:answer1 where  answer=:answer")
    fun updateTrueTo1(answer1:String,answer:String? )
    @Query("update question_list_submit set answer=:answer1 where  answer=:answer")
    fun updateFalseTo0(answer1:String,answer:String? )


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    abstract fun insertAll(kist: List<QuestionSubmitEntity>)

}