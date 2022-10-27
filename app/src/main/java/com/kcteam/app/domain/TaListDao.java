package com.kcteam.app.domain;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static com.kcteam.app.AppConstant.TA_TABLE;

/**
 * Created by sayantan.sarkar on 2/11/17.
 */
@Dao
public interface TaListDao {

    @Query("SELECT * FROM " + TA_TABLE)
    List<TaListDBModelEntity> getAll();

    @Query("SELECT COUNT(*) from " + TA_TABLE)
    int countListItem();

    @Insert
    void insertAll(TaListDBModelEntity... ta);

    @Insert
    void insert(TaListDBModelEntity ta);

    @Delete
    void delete(TaListDBModelEntity user);

    @Query("update ta_list set status=:status where id=:id")
    void updateIsVisited(String status, String id);

    /*@Query("SELECT COUNT(*) from " + TA_TABLE)
    int checkIfDataExist(String status, String id);*/

    /*@Query("update ta_list set isVisited=:isvisited")
    long updateIsVisitedToFalse(Boolean isvisited);*/

    @Query("Select * from ta_list where status=:status and id=:id")
    TaListDBModelEntity getShopById(String status, String id);

    @Query("DELETE FROM " + TA_TABLE)
    void deleteAll();

    /*@Query("Select * from ta_list where shop_id=:shopId")
    TaListDBModelEntity getShopByIdN(String shopId);

    @Query("Select * from ta_list where shop_id=:shopId")
    List<TaListDBModelEntity> getShopByIdList(String shopId);

    @Query("Select * from ta_list where visitDate=:date and isVisited=:isvisited")
    List<TaListDBModelEntity> getShopsVisitedPerDay(String date, Boolean isvisited);

    @Query("Select * from ta_list where isVisited=:isvisited")
    List<TaListDBModelEntity> getTotalShopVisited(Boolean isvisited);

    @Query("Select * from ta_list where visitDate=:visitDate and isVisited=:isvisited")
    List<TaListDBModelEntity> getTotalShopVisitedForADay(String visitDate, Boolean isvisited);

    @Query("Select lastVisitedDate from ta_list where shop_id=:shopId")
    String getLastVisitedDate(String shopId);

    @Query("Select * from ta_list where shop_name=:shopName and isVisited=:isvisited")
    List<TaListDBModelEntity> getVisitedShopListByName(String shopName, Boolean isvisited);

    @Query("Select * from ta_list group by shop_name")
    List<TaListDBModelEntity> getUniqueShoplist();

    @Query("Select * from ta_list where isVisited=:isvisited")
    List<TaListDBModelEntity> getAllVisitedShops(Boolean isvisited);

    @Query("update ta_list set endTimeStamp=:endTimeStamp where shop_id=:shopId")
    long updateEndTimeSpan(String endTimeStamp, String shopId);

    @Query("update ta_list set duration=:timeDuration where shop_id=:shopId")
    long updateTimeDuration(String timeDuration, String shopId);

    @Query("Select duration from ta_list where shop_id=:shopId and visitDate=:date")
    String getTimeDurationForDayOfShop(String shopId, String date);

    @Query("Select * from ta_list where visitDate=:date and isVisited=:isvisited and isUploaded=:isUploaded")
    List<TaListDBModelEntity> getShopsNotUploaded(String date, Boolean isvisited, Boolean isUploaded);

    @Query("Select * from ta_list where isVisited=:isvisited and isUploaded=:isUploaded")
    List<TaListDBModelEntity> getAllShopsNotUploaded(Boolean isvisited, Boolean isUploaded);
//    @Query("Select * from ta_list where shopId=:shopId and visitDate=:visitDate and isVisited=:isvisited")
//    TaListDBModelEntity getShopVisitCouint(Boolean isvisited, String shopId);

    @Query("Select * from ta_list where visitDate=:visitDate and isVisited=:isvisited")
    TaListDBModelEntity getDayWiseShopList(String visitDate, Boolean isvisited);

    @Query("SELECT count(DISTINCT visitDate) FROM ta_list")
    int getTotalDays();

    @Query("update ta_list set isUploaded=:isUploaded where shop_id=:shopId")
    long updateIsUploaded(Boolean isUploaded, String shopId);

    @Query("update ta_list set totalVisitCount=:totalCount where shop_id=:shopId")
    long updateTotalCount(String totalCount, String shopId);

    @Query("update ta_list set lastVisitedDate=:visitDate where shop_id=:shopId")
    long updateLastVisitDate(String visitDate, String shopId);

    @Query("Select * from ta_list where shop_id=:shopId")
    TaListDBModelEntity getShopDetail(String shopId);

    @Query("DELETE FROM ta_list where shop_id=:shopId")
    int deleteShopById(String shopId);

    @Query("Select * from ta_list where isUploaded=:isUploaded")
    List<TaListDBModelEntity> getUnSyncedShops(Boolean isUploaded);

    @Query("Select * from ta_list where owner_contact_number=:contactNum")
    List<TaListDBModelEntity> getDuplicateShopData(String contactNum);

    @Query("Select * from ta_list where shop_name LIKE '%' || :shopNameorNum  || '%' OR owner_contact_number LIKE '%' || :shopNameorNum  || '%' ")
    List<TaListDBModelEntity> getShopBySearchData(String shopNameorNum);


    @Update
    int updateAddShop(TaListDBModelEntity mTaListDBModelEntity);


    @Query("DELETE FROM " + SHOP_TABLE)
    void deleteAll();

    @Update
    public int updateShopDao(TaListDBModelEntity... mTaListDBModelEntity);

    @Query("update ta_list set isAddressUpdated=:isAddressUpdated where shop_id=:shopId")
    public int updateIsAddressUpdated(String shopId, Boolean isAddressUpdated);*/


//    @Query("INSERT OR REPLACE INTO SHOP_TABLE (shopId,shopName,address,pinCode,ownerName,isVisited) VALUES (:id, :title, :url, COALESCE((SELECT isSubscribed FROM articles WHERE id = :id), 0));")
//    void insertOrUpdateShop(long id, String title, String url);
}
