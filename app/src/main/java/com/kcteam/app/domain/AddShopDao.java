package com.kcteam.app.domain;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import static com.kcteam.app.AppConstant.SHOP_TABLE;

/**
 * Created by sayantan.sarkar on 2/11/17.
 */
@Dao
public interface AddShopDao {

//    @Query("SELECT COUNT(*) AS row_count FROM "  + SHOP_TABLE + " WHERE landline_number = :landlineNumber")
//    int getLandNumber(String landlineNumber);



    //@Query("SELECT landline_number FROM "  + SHOP_TABLE + " WHERE  landline_number = :landline_number IS NOT NULL and landline_number = :landline_number > 0")
//    @Query("SELECT landline_number FROM "  + SHOP_TABLE + " WHERE  landline_number LIKE '%' || :landline_number || '%'  limit 1 ")
//    String getLandNumber(String landline_number);


    @Query("SELECT * FROM " + SHOP_TABLE)
    List<AddShopDBModelEntity> getAll();

    @Query("SELECT * FROM " + SHOP_TABLE+" where isOwnshop=:isOwnshop")
    List<AddShopDBModelEntity> getAllOwn(Boolean isOwnshop);

    @Query("select distinct shop_detail.* from shop_detail inner join order_details_list on shop_detail.shop_id = order_details_list.shop_id ")
    List<AddShopDBModelEntity> getShopIdHasOrder();

    @Query("select distinct shop_detail.* from shop_detail inner join order_details_list on shop_detail.shop_id = order_details_list.shop_id and assigned_to_dd_id=:assigned_to_dd_id and lastVisitedDate=:lastVisitedDate")
    List<AddShopDBModelEntity> getShopIdHasOrderDDWise(String assigned_to_dd_id,String lastVisitedDate);


    @Query("select  * from shop_detail where shopid < 10")
    List<AddShopDBModelEntity> getTop10();

    @Query("SELECT COUNT(*) from " + SHOP_TABLE)
    int countUsers();

    @Insert
    void insertAll(AddShopDBModelEntity... shops);

    @Insert
    void insert(AddShopDBModelEntity shops);

    @Delete
    void delete(AddShopDBModelEntity user);

    @Query("update shop_detail set isVisited=:isvisited where shop_id=:shopId")
    void updateIsVisited(Boolean isvisited, String shopId);

    @Query("update shop_detail set isVisited=:isvisited")
    void updateIsVisitedToFalse(Boolean isvisited);

    @Query("Select * from shop_detail where shop_id=:shopId and isVisited=:isvisited")
    AddShopDBModelEntity getShopById(Boolean isvisited, String shopId);

    @Query("Select * from shop_detail where shop_id=:shopId")
    AddShopDBModelEntity getShopByIdN(String shopId);

    @Query("Select * from shop_detail where shop_id=:shopId")
    List<AddShopDBModelEntity> getShopByIdList(String shopId);

    @Query("Select * from shop_detail where visitDate=:date and isVisited=:isvisited")
    List<AddShopDBModelEntity> getShopsVisitedPerDay(String date, Boolean isvisited);

    @Query("Select * from shop_detail where type=:type")
    List<AddShopDBModelEntity> getShopsAccordingToType(String type);

    @Query("Select * from shop_detail where isVisited=:isvisited")
    List<AddShopDBModelEntity> getTotalShopVisited(Boolean isvisited);

    @Query("Select * from shop_detail where visitDate=:visitDate and isVisited=:isvisited")
    List<AddShopDBModelEntity> getTotalShopVisitedForADay(String visitDate, Boolean isvisited);

    @Query("Select lastVisitedDate from shop_detail where shop_id=:shopId")
    String getLastVisitedDate(String shopId);

    @Query("Select * from shop_detail where shop_name=:shopName and isVisited=:isvisited")
    List<AddShopDBModelEntity> getVisitedShopListByName(String shopName, Boolean isvisited);

    @Query("Select * from shop_detail group by shop_name")
    List<AddShopDBModelEntity> getUniqueShoplist();

    @Query("Select * from shop_detail where isVisited=:isvisited")
    List<AddShopDBModelEntity> getAllVisitedShops(Boolean isvisited);

    @Query("update shop_detail set endTimeStamp=:endTimeStamp where shop_id=:shopId")
    void updateEndTimeSpan(String endTimeStamp, String shopId);

    @Query("update shop_detail set duration=:timeDuration where shop_id=:shopId")
    void updateTimeDuration(String timeDuration, String shopId);

    @Query("Select duration from shop_detail where shop_id=:shopId and visitDate=:date")
    String getTimeDurationForDayOfShop(String shopId, String date);

    @Query("Select * from shop_detail where visitDate=:date and isVisited=:isvisited and isUploaded=:isUploaded")
    List<AddShopDBModelEntity> getShopsNotUploaded(String date, Boolean isvisited, Boolean isUploaded);

    @Query("Select * from shop_detail where isVisited=:isvisited and isUploaded=:isUploaded")
    List<AddShopDBModelEntity> getAllShopsNotUploaded(Boolean isvisited, Boolean isUploaded);
//    @Query("Select * from shop_detail where shopId=:shopId and visitDate=:visitDate and isVisited=:isvisited")
//    AddShopDBModelEntity getShopVisitCouint(Boolean isvisited, String shopId);

    @Query("Select * from shop_detail where visitDate=:visitDate and isVisited=:isvisited")
    AddShopDBModelEntity getDayWiseShopList(String visitDate, Boolean isvisited);

    @Query("SELECT count(DISTINCT visitDate) FROM shop_detail")
    int getTotalDays();

    @Query("update shop_detail set isUploaded=:isUploaded where shop_id=:shopId")
    void updateIsUploaded(Boolean isUploaded, String shopId);

    @Query("update shop_detail set isEditUploaded=:isEditUploaded where shop_id=:shopId")
    void updateIsEditUploaded(int isEditUploaded, String shopId);

    @Query("update shop_detail set totalVisitCount=:totalCount where shop_id=:shopId")
    void updateTotalCount(String totalCount, String shopId);

    @Query("update shop_detail set lastVisitedDate=:visitDate where shop_id=:shopId")
    void updateLastVisitDate(String visitDate, String shopId);

    @Query("Select * from shop_detail where shop_id=:shopId")
    AddShopDBModelEntity getShopDetail(String shopId);

    @Query("DELETE FROM shop_detail where shop_id=:shopId")
    int deleteShopById(String shopId);

    @Query("Select * from shop_detail where isUploaded=:isUploaded")
    List<AddShopDBModelEntity> getUnSyncedShops(Boolean isUploaded);

    @Query("Select * from shop_detail where owner_contact_number=:contactNum")
    List<AddShopDBModelEntity> getDuplicateShopData(String contactNum);

    @Query("Select * from shop_detail where shop_name LIKE '%' || :shopNameorNum  || '%' OR owner_contact_number LIKE '%' || :shopNameorNum  || '%' ")
    List<AddShopDBModelEntity> getShopBySearchData(String shopNameorNum);

    @Query("Select * from shop_detail where shop_name LIKE '%' || :shopNameorNum  || '%' OR owner_contact_number LIKE '%' || :shopNameorNum  ||  '%' OR owner_name LIKE '%' || :shopNameorNum  || '%' ")
    List<AddShopDBModelEntity> getShopBySearchDataNew(String shopNameorNum);


    @Update
    int updateAddShop(AddShopDBModelEntity mAddShopDBModelEntity);


    @Query("DELETE FROM " + SHOP_TABLE)
    void deleteAll();

    @Update
    public int updateShopDao(AddShopDBModelEntity... mAddShopDBModelEntity);

    @Query("update shop_detail set isAddressUpdated=:isAddressUpdated where shop_id=:shopId")
    public int updateIsAddressUpdated(String shopId, Boolean isAddressUpdated);

    @Query("update shop_detail set owner_contact_number=:owner_contact_number where shop_id=:shopId")
    public int updateContactNo(String shopId, String owner_contact_number);

    @Query("update shop_detail set shop_image_local_path=:shop_image_local_path where shop_id=:shopId")
    void updateShopImage(String shop_image_local_path, String shopId);

    @Query("update shop_detail set shop_name=:shop_name where shop_id=:shopId")
    void updateShopName(String shop_name, String shopId);

    @Query("update shop_detail set owner_contact_number=:owner_contact_number where shop_id=:shopId")
    void updateOwnerContactNumber(String owner_contact_number, String shopId);

    @Query("update shop_detail set owner_email=:owner_email where shop_id=:shopId")
    void updateOwnerEmail(String owner_email, String shopId);

    @Query("update shop_detail set owner_name=:owner_name where shop_id=:shopId")
    void updateOwnerName(String owner_name, String shopId);

    @Query("update shop_detail set dateOfBirth=:dateOfBirth where shop_id=:shopId")
    void updateDOB(String dateOfBirth, String shopId);

    @Query("update shop_detail set dateOfAniversary=:dateOfAniversary where shop_id=:shopId")
    void updateDOA(String dateOfAniversary, String shopId);

    @Query("update shop_detail set type=:type where shop_id=:shopId")
    void updateType(String type, String shopId);

    @Query("update shop_detail set assigned_to_dd_id=:assigned_to_dd_id where shop_id=:shopId")
    void updateDDid(String assigned_to_dd_id, String shopId);

    @Query("update shop_detail set assigned_to_pp_id=:assigned_to_pp_id where shop_id=:shopId")
    void updatePPid(String assigned_to_pp_id, String shopId);

    @Query("update shop_detail set is_otp_verified=:is_otp_verified where shop_id=:shopId")
    void updateIsOtpVerified(String is_otp_verified, String shopId);

    @Query("Select * from shop_detail where isEditUploaded=:isEditUploaded and isUploaded=:isUploaded")
    List<AddShopDBModelEntity> getUnsyncEditShop(int isEditUploaded, Boolean isUploaded);

    @Query("Select owner_contact_number from shop_detail where shop_id=:shopId")
    String getContactNumber(String shopId);

    @Query("Select type from shop_detail where shop_id=:shop_id")
    String getShopType(String shop_id);

    @Query("update shop_detail set party_status_id=:party_status_id where shop_id=:shopId")
    void updatePartyStatus(String party_status_id, String shopId);

    @Query("Select * from shop_detail where beat_id=:beat_id")
    List<AddShopDBModelEntity> getShopBeatWise(String beat_id);

    @Query("Select * from shop_detail where beat_id=:beat_id and assigned_to_dd_id =:assigned_to_dd_id")
    List<AddShopDBModelEntity> getShopBeatWiseDD(String beat_id,String assigned_to_dd_id);


    @Query("Select * from shop_detail where shop_name LIKE '%' || :shopNameorNum  || '%' OR owner_contact_number LIKE '%' || :shopNameorNum  || '%' and beat_id=:beat_id")
    List<AddShopDBModelEntity> getSearchedShopBeatWise(String beat_id, String shopNameorNum);

    @Query("update shop_detail set account_holder=:account_holder, account_no=:account_no, bank_name=:bank_name, ifsc_code=:ifsc_code, upi_id=:upi_id where shop_id=:shopId")
    void updateBankDetails(String account_holder, String account_no, String bank_name, String ifsc_code, String upi_id, String shopId);

    @Query("Select project_name from shop_detail where shop_id=:shopId")
    String getProjectName(String shopId);

    @Query("Select landline_number from shop_detail where shop_id=:shopId")
    String getLand(String shopId);


    @Query("Select address from shop_detail where shop_id=:shopId")
    String getshopDetailsaddress(String shopId);

    @Query("Select shop_name from shop_detail where shop_id=:shopId")
    String getshopDetailsShopName(String shopId);

    @Query("select * FROM " + SHOP_TABLE +" where shop_id=:shop_id")
    List<AddShopDBModelEntity> getShopIdFromDtls(String shop_id);


    @Query("SELECT * FROM " + SHOP_TABLE+" where assigned_to_dd_id=:assigned_to_dd_id")
    List<AddShopDBModelEntity> getShopByDD(String assigned_to_dd_id);

    @Query("SELECT * FROM " + SHOP_TABLE+" where visitDate=:visitDate")
    List<AddShopDBModelEntity> getShopCreatedToday(String visitDate);

    @Query("Select *  FROM " + SHOP_TABLE+" where type=:type")
    List<AddShopDBModelEntity> getShopNameByDD(String type);

    @Query("SELECT beat_id FROM " + SHOP_TABLE+" where assigned_to_dd_id=:assigned_to_dd_id")
    List<String> getDistinctBeatID(String assigned_to_dd_id);

    @Query("Select Shopowner_PAN from shop_detail where shop_id=:shopId")
    String getPancardNumber(String shopId);

    @Query("Select GSTN_Number from shop_detail where shop_id=:shopId")
    String getGSTINNumber(String shopId);


//    @Query("INSERT OR REPLACE INTO SHOP_TABLE (shopId,shopName,address,pinCode,ownerName,isVisited) VALUES (:id, :title, :url, COALESCE((SELECT isSubscribed FROM articles WHERE id = :id), 0));")
//    void insertOrUpdateShop(long id, String title, String url);
}
