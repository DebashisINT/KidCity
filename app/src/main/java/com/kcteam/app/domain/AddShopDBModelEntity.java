package com.kcteam.app.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.kcteam.app.AppConstant.SHOP_TABLE;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sayantan.sarkar on 2/11/17.
 */
@Entity(tableName = SHOP_TABLE)
public class AddShopDBModelEntity {

    @PrimaryKey(autoGenerate = true)
    private int shopId;

    @ColumnInfo(name = "shop_name")
    private String shopName = "";

    @ColumnInfo(name = "shop_id")
    private String shop_id = "";

    @ColumnInfo(name = "address")
    private String address = "";

    @ColumnInfo(name = "pin_code")
    private String pinCode = "";

    @ColumnInfo(name = "owner_name")
    private String ownerName = "";

    @ColumnInfo(name = "owner_contact_number")
    private String ownerContactNumber = "";

    @ColumnInfo(name = "owner_email")
    private String ownerEmailId = "";

    @ColumnInfo(name = "shop_image_local_path")
    private String shopImageLocalPath = "";

    @ColumnInfo(name = "shop_image_local_path_competitor")
    private String shopImageLocalPathCompetitor = "";

    @ColumnInfo(name = "shop_image_url")
    private String shopImageUrl = "";

    @ColumnInfo(name = "shopLat")
    private Double shopLat = 0.0;

    @ColumnInfo(name = "shopLong")
    private Double shopLong = 0.0;

    @ColumnInfo(name = "isVisited")
    private Boolean isVisited = false;

    @ColumnInfo(name = "odervalue")
    private int orderValue = 0;

    @ColumnInfo(name = "visitDate")
    private String visitDate = "";

    @ColumnInfo(name = "lastVisitedDate")
    private String lastVisitedDate = "";

    @ColumnInfo(name = "totalVisitCount")
    private String totalVisitCount = "0";

    @ColumnInfo(name = "dateOfBirth")
    private String dateOfBirth = "";

    @ColumnInfo(name = "dateOfAniversary")
    private String dateOfAniversary = "";

    @ColumnInfo(name = "Duration")
    private String duration = "2";

    @ColumnInfo(name = "timeStamp")
    private String timeStamp = "";

    @ColumnInfo(name = "endTimeStamp")
    private String endTimeStamp = "0";

    @ColumnInfo(name = "user_id")
    private String user_id = "";

    @ColumnInfo(name = "type")
    private String type = "1";

    @ColumnInfo(name = "isUploaded")
    private boolean isUploaded = false;

    @ColumnInfo(name = "isAddressUpdated")
    private boolean isAddressUpdated = false;

    @ColumnInfo(name = "assigned_to_dd_id")
    private String assigned_to_dd_id = null;

    @ColumnInfo(name = "assigned_to_pp_id")
    private String assigned_to_pp_id = null;

    @ColumnInfo(name = "isEditUploaded")
    private int isEditUploaded = -1;

    @ColumnInfo(name = "is_otp_verified")
    private String is_otp_verified = null;

    @ColumnInfo(name = "added_date")
    private String added_date = null;

    @ColumnInfo(name = "amount")
    private String amount = null;

    @ColumnInfo(name = "entity_code")
    private String entity_code = null;

    @ColumnInfo(name = "area_id")
    private String area_id = null;

    @ColumnInfo(name = "model_id")
    private String model_id = null;

    @ColumnInfo(name = "primary_app_id")
    private String primary_app_id = null;

    @ColumnInfo(name = "secondary_app_id")
    private String secondary_app_id = null;

    @ColumnInfo(name = "lead_id")
    private String lead_id = null;

    @ColumnInfo(name = "funnel_stage_id")
    private String funnel_stage_id = null;

    @ColumnInfo(name = "stage_id")
    private String stage_id = null;

    @ColumnInfo(name = "booking_amount")
    private String booking_amount = null;

    @ColumnInfo(name = "type_id")
    private String type_id = null;


    @ColumnInfo(name = "director_name")
    private String director_name = null;

    @ColumnInfo(name = "family_member_dob")
    private String family_member_dob = null;

    @ColumnInfo(name = "person_name")
    private String person_name = null;

    @ColumnInfo(name = "person_no")
    private String person_no = null;

    @ColumnInfo(name = "add_dob")
    private String add_dob = null;

    @ColumnInfo(name = "add_doa")
    private String add_doa = null;

    @ColumnInfo(name = "doc_degree")
    private String doc_degree = null;

    @ColumnInfo(name = "specialization")
    private String specialization = null;

    @ColumnInfo(name = "patient_count")
    private String patient_count = null;

    @ColumnInfo(name = "category")
    private String category = null;

    @ColumnInfo(name = "doc_family_dob")
    private String doc_family_dob = null;

    @ColumnInfo(name = "doc_address")
    private String doc_address = null;

    @ColumnInfo(name = "doc_pincode")
    private String doc_pincode = null;

    @ColumnInfo(name = "chamber_status")
    private int chamber_status = 0;

    @ColumnInfo(name = "remarks")
    private String remarks = null;

    @ColumnInfo(name = "chemist_name")
    private String chemist_name = null;

    @ColumnInfo(name = "chemist_address")
    private String chemist_address = null;

    @ColumnInfo(name = "chemist_pincode")
    private String chemist_pincode = null;

    @ColumnInfo(name = "assistant_name")
    private String assistant_name = null;

    @ColumnInfo(name = "assistant_no")
    private String assistant_no = null;

    @ColumnInfo(name = "assistant_dob")
    private String assistant_dob = null;

    @ColumnInfo(name = "assistant_doa")
    private String assistant_doa = null;

    @ColumnInfo(name = "assistant_family_dob")
    private String assistant_family_dob = null;

    @ColumnInfo(name = "entity_id")
    private String entity_id = null;

    @ColumnInfo(name = "party_status_id")
    private String party_status_id = null;

    @ColumnInfo(name = "retailer_id")
    private String retailer_id = null;

    @ColumnInfo(name = "dealer_id")
    private String dealer_id = null;

    @ColumnInfo(name = "beat_id")
    private String beat_id = null;

    @ColumnInfo(name = "account_holder")
    private String account_holder = null;

    @ColumnInfo(name = "account_no")
    private String account_no = null;

    @ColumnInfo(name = "bank_name")
    private String bank_name = null;

    @ColumnInfo(name = "ifsc_code")
    private String ifsc_code = null;

    @ColumnInfo(name = "upi_id")
    private String upi_id = null;

    @ColumnInfo(name = "assigned_to_shop_id")
    private String assigned_to_shop_id = null;

    @ColumnInfo(name = "actual_address")
    private String actual_address = null;

    @ColumnInfo(name = "agency_name")
    private String agency_name = null;

    @ColumnInfo(name = "lead_contact_number")
    private String lead_contact_number = null;


    @ColumnInfo(name = "rubylead_image1")
    private String rubylead_image1 = null;

    @ColumnInfo(name = "rubylead_image2")
    private String rubylead_image2 = null;

    @ColumnInfo(name = "project_name")
    private String project_name = null;

    @ColumnInfo(name = "landline_number")
    private String landline_number = null;

    @ColumnInfo(name = "alternateNoForCustomer")
    private String alternateNoForCustomer = null;

    @ColumnInfo(name = "whatsappNoForCustomer")
    private String whatsappNoForCustomer = null;

    @ColumnInfo(name = "isShopDuplicate")
    private boolean isShopDuplicate = false;

    @ColumnInfo(name = "isOwnshop")
    private boolean isOwnshop = true;

    public boolean isOwnshop() {
        return isOwnshop;
    }
    public void setOwnshop(boolean ownshop) {
        isOwnshop = ownshop;
    }

    public boolean getIsShopDuplicate() {
        return isShopDuplicate;
    }
    public void setIsShopDuplicate(boolean isShopDuplicate) {
        this.isShopDuplicate = isShopDuplicate;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @ColumnInfo(name = "purpose")
    private String purpose = null;

    public String getAlternateNoForCustomer() {
        return alternateNoForCustomer;
    }

    public void setAlternateNoForCustomer(String alternateNoForCustomer) {
        this.alternateNoForCustomer = alternateNoForCustomer;
    }

    public String getWhatsappNoForCustomer() {
        return whatsappNoForCustomer;
    }

    public void setWhatsappNoForCustomer(String whatsappNoForCustomer) {
        this.whatsappNoForCustomer = whatsappNoForCustomer;
    }




    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getLandline_number() {
        return landline_number;
    }

    public void setLandline_number(String landline_number) {
        this.landline_number = landline_number;
    }



    public String getRubylead_image1() {
        return rubylead_image1;
    }

    public void setRubylead_image1(String rubylead_image1) {
        this.rubylead_image1 = rubylead_image1;
    }

    public String getRubylead_image2() {
        return rubylead_image2;
    }

    public void setRubylead_image2(String rubylead_image2) {
        this.rubylead_image2 = rubylead_image2;
    }



    public String getAgency_name() {
        return agency_name;
    }

    public void setAgency_name(String agency_name) {
        this.agency_name = agency_name;
    }

    public String getLead_contact_number() {
        return lead_contact_number;
    }

    public void setLead_contact_number(String lead_contact_number) {
        this.lead_contact_number = lead_contact_number;
    }



    public void setCompetitor_img2(String rubylead_image2) {
        this.rubylead_image2 = rubylead_image2;
    }






    public String getIs_otp_verified() {
        return is_otp_verified;
    }

    public void setIs_otp_verified(String is_otp_verified) {
        this.is_otp_verified = is_otp_verified;
    }

    public int getIsEditUploaded() {
        return isEditUploaded;
    }

    public void setIsEditUploaded(int isEditUploaded) {
        this.isEditUploaded = isEditUploaded;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public boolean isAddressUpdated() {
        return isAddressUpdated;
    }

    public void setAddressUpdated(boolean addressUpdated) {
        isAddressUpdated = addressUpdated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfAniversary() {
        return dateOfAniversary;
    }

    public void setDateOfAniversary(String dateOfAniversary) {
        this.dateOfAniversary = dateOfAniversary;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLastVisitedDate() {
        return lastVisitedDate;
    }

    public void setLastVisitedDate(String lastVisitedDate) {
        this.lastVisitedDate = lastVisitedDate;
    }

    public String getTotalVisitCount() {
        return totalVisitCount;
    }

    public void setTotalVisitCount(String totalVisitCount) {
        this.totalVisitCount = totalVisitCount;
    }

    public String getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(String endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerContactNumber() {
        return ownerContactNumber;
    }

    public void setOwnerContactNumber(String ownerContactNumber) {
        this.ownerContactNumber = ownerContactNumber;
    }

    public String getOwnerEmailId() {
        return ownerEmailId;
    }

    public void setOwnerEmailId(String ownerEmailId) {
        this.ownerEmailId = ownerEmailId;
    }

    public String getShopImageLocalPath() {
        return shopImageLocalPath;
    }

    public void setShopImageLocalPath(String shopImageLocalPath) {
        this.shopImageLocalPath = shopImageLocalPath;
    }

    public String getShopImageLocalPathCompetitor() {
        return shopImageLocalPathCompetitor;
    }

    public void setShopImageLocalPathCompetitor(String shopImageLocalPathCompetitor) {
        this.shopImageLocalPathCompetitor = shopImageLocalPathCompetitor;
    }

    public String getShopImageUrl() {
        return shopImageUrl;
    }

    public void setShopImageUrl(String shopImageUrl) {
        this.shopImageUrl = shopImageUrl;
    }

    public Double getShopLat() {
        return shopLat;
    }

    public void setShopLat(Double shopLat) {
        this.shopLat = shopLat;
    }

    public Double getShopLong() {
        return shopLong;
    }

    public void setShopLong(Double shopLong) {
        this.shopLong = shopLong;
    }

    public Boolean getVisited() {
        return isVisited;
    }

    public void setVisited(Boolean visited) {
        isVisited = visited;
    }

    public int getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(int orderValue) {
        this.orderValue = orderValue;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getAssigned_to_dd_id() {
        return assigned_to_dd_id;
    }

    public void setAssigned_to_dd_id(String assigned_to_dd_id) {
        this.assigned_to_dd_id = assigned_to_dd_id;
    }

    public String getAssigned_to_pp_id() {
        return assigned_to_pp_id;
    }

    public void setAssigned_to_pp_id(String assigned_to_pp_id) {
        this.assigned_to_pp_id = assigned_to_pp_id;
    }

    public String getAdded_date() {
        return added_date;
    }

    public void setAdded_date(String added_date) {
        this.added_date = added_date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEntity_code() {
        return entity_code;
    }

    public void setEntity_code(String entity_code) {
        this.entity_code = entity_code;
    }

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getPrimary_app_id() {
        return primary_app_id;
    }

    public void setPrimary_app_id(String primary_app_id) {
        this.primary_app_id = primary_app_id;
    }

    public String getSecondary_app_id() {
        return secondary_app_id;
    }

    public void setSecondary_app_id(String secondary_app_id) {
        this.secondary_app_id = secondary_app_id;
    }

    public String getLead_id() {
        return lead_id;
    }

    public void setLead_id(String lead_id) {
        this.lead_id = lead_id;
    }

    public String getFunnel_stage_id() {
        return funnel_stage_id;
    }

    public void setFunnel_stage_id(String funnel_stage_id) {
        this.funnel_stage_id = funnel_stage_id;
    }

    public String getStage_id() {
        return stage_id;
    }

    public void setStage_id(String stage_id) {
        this.stage_id = stage_id;
    }

    public String getBooking_amount() {
        return booking_amount;
    }

    public void setBooking_amount(String booking_amount) {
        this.booking_amount = booking_amount;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getDirector_name() {
        return director_name;
    }

    public void setDirector_name(String director_name) {
        this.director_name = director_name;
    }

    public String getFamily_member_dob() {
        return family_member_dob;
    }

    public void setFamily_member_dob(String family_member_dob) {
        this.family_member_dob = family_member_dob;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getPerson_no() {
        return person_no;
    }

    public void setPerson_no(String person_no) {
        this.person_no = person_no;
    }

    public String getAdd_dob() {
        return add_dob;
    }

    public void setAdd_dob(String add_dob) {
        this.add_dob = add_dob;
    }

    public String getAdd_doa() {
        return add_doa;
    }

    public void setAdd_doa(String add_doa) {
        this.add_doa = add_doa;
    }

    public String getDoc_degree() {
        return doc_degree;
    }

    public void setDoc_degree(String doc_degree) {
        this.doc_degree = doc_degree;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPatient_count() {
        return patient_count;
    }

    public void setPatient_count(String patient_count) {
        this.patient_count = patient_count;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDoc_family_dob() {
        return doc_family_dob;
    }

    public void setDoc_family_dob(String doc_family_dob) {
        this.doc_family_dob = doc_family_dob;
    }

    public String getDoc_address() {
        return doc_address;
    }

    public void setDoc_address(String doc_address) {
        this.doc_address = doc_address;
    }

    public String getDoc_pincode() {
        return doc_pincode;
    }

    public void setDoc_pincode(String doc_pincode) {
        this.doc_pincode = doc_pincode;
    }

    public int getChamber_status() {
        return chamber_status;
    }

    public void setChamber_status(int chamber_status) {
        this.chamber_status = chamber_status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getChemist_name() {
        return chemist_name;
    }

    public void setChemist_name(String chemist_name) {
        this.chemist_name = chemist_name;
    }

    public String getChemist_address() {
        return chemist_address;
    }

    public void setChemist_address(String chemist_address) {
        this.chemist_address = chemist_address;
    }

    public String getChemist_pincode() {
        return chemist_pincode;
    }

    public void setChemist_pincode(String chemist_pincode) {
        this.chemist_pincode = chemist_pincode;
    }

    public String getAssistant_name() {
        return assistant_name;
    }

    public void setAssistant_name(String assistant_name) {
        this.assistant_name = assistant_name;
    }

    public String getAssistant_no() {
        return assistant_no;
    }

    public void setAssistant_no(String assistant_no) {
        this.assistant_no = assistant_no;
    }

    public String getAssistant_dob() {
        return assistant_dob;
    }

    public void setAssistant_dob(String assistant_dob) {
        this.assistant_dob = assistant_dob;
    }

    public String getAssistant_doa() {
        return assistant_doa;
    }

    public void setAssistant_doa(String assistant_doa) {
        this.assistant_doa = assistant_doa;
    }

    public String getAssistant_family_dob() {
        return assistant_family_dob;
    }

    public void setAssistant_family_dob(String assistant_family_dob) {
        this.assistant_family_dob = assistant_family_dob;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public String getParty_status_id() {
        return party_status_id;
    }

    public void setParty_status_id(String party_status_id) {
        this.party_status_id = party_status_id;
    }

    public String getRetailer_id() {
        return retailer_id;
    }

    public void setRetailer_id(String retailer_id) {
        this.retailer_id = retailer_id;
    }

    public String getDealer_id() {
        return dealer_id;
    }

    public void setDealer_id(String dealer_id) {
        this.dealer_id = dealer_id;
    }

    public String getBeat_id() {
        return beat_id;
    }

    public void setBeat_id(String beat_id) {
        this.beat_id = beat_id;
    }

    public String getAccount_holder() {
        return account_holder;
    }

    public void setAccount_holder(String account_holder) {
        this.account_holder = account_holder;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getIfsc_code() {
        return ifsc_code;
    }

    public void setIfsc_code(String ifsc_code) {
        this.ifsc_code = ifsc_code;
    }

    public String getUpi_id() {
        return upi_id;
    }

    public void setUpi_id(String upi_id) {
        this.upi_id = upi_id;
    }

    public String getAssigned_to_shop_id() {
        return assigned_to_shop_id;
    }

    public void setAssigned_to_shop_id(String assigned_to_shop_id) {
        this.assigned_to_shop_id = assigned_to_shop_id;
    }

    public String getActual_address() {
        return actual_address;
    }

    public void setActual_address(String actual_address) {
        this.actual_address = actual_address;
    }
}
