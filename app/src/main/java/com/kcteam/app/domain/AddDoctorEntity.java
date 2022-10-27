package com.kcteam.app.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kcteam.app.AppConstant;

@Entity(tableName = AppConstant.DOCTOR_VISIT_LIST_TABLE)
public class AddDoctorEntity {

    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "doc_visit_id")
    private String doc_visit_id = null;

    @ColumnInfo(name = "shop_id")
    private String shop_id = null;

    @ColumnInfo(name = "doc_remark")
    private String doc_remark = null;

    @ColumnInfo(name = "prescribe_status")
    private int prescribe_status = -1;

    @ColumnInfo(name = "qty_status")
    private int qty_status = -1;

    @ColumnInfo(name = "qty_text")
    private String qty_text = null;

    @ColumnInfo(name = "sample_status")
    private int sample_status = -1;

    @ColumnInfo(name = "crm_status")
    private int crm_status = -1;

    @ColumnInfo(name = "money_status")
    private int money_status = -1;

    @ColumnInfo(name = "amount")
    private String amount = null;

    @ColumnInfo(name = "what")
    private String what = null;

    @ColumnInfo(name = "crm_from_date")
    private String crm_from_date = null;

    @ColumnInfo(name = "crm_to_date")
    private String crm_to_date = null;

    @ColumnInfo(name = "volume")
    private String volume = null;

    @ColumnInfo(name = "gift_status")
    private int gift_status = -1;

    @ColumnInfo(name = "which_kind")
    private String which_kind = null;

    @ColumnInfo(name = "visit_date")
    private String visit_date = null;

    @ColumnInfo(name = "remarks_mr")
    private String remarks_mr = null;

    @ColumnInfo(name = "isUploaded")
    private boolean isUploaded = false;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDoc_visit_id() {
        return doc_visit_id;
    }

    public void setDoc_visit_id(String doc_visit_id) {
        this.doc_visit_id = doc_visit_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getDoc_remark() {
        return doc_remark;
    }

    public void setDoc_remark(String doc_remark) {
        this.doc_remark = doc_remark;
    }

    public int getPrescribe_status() {
        return prescribe_status;
    }

    public void setPrescribe_status(int prescribe_status) {
        this.prescribe_status = prescribe_status;
    }

    public int getQty_status() {
        return qty_status;
    }

    public void setQty_status(int qty_status) {
        this.qty_status = qty_status;
    }

    public String getQty_text() {
        return qty_text;
    }

    public void setQty_text(String qty_text) {
        this.qty_text = qty_text;
    }

    public int getSample_status() {
        return sample_status;
    }

    public void setSample_status(int sample_status) {
        this.sample_status = sample_status;
    }

    public int getCrm_status() {
        return crm_status;
    }

    public void setCrm_status(int crm_status) {
        this.crm_status = crm_status;
    }

    public int getMoney_status() {
        return money_status;
    }

    public void setMoney_status(int money_status) {
        this.money_status = money_status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getCrm_from_date() {
        return crm_from_date;
    }

    public void setCrm_from_date(String crm_from_date) {
        this.crm_from_date = crm_from_date;
    }

    public String getCrm_to_date() {
        return crm_to_date;
    }

    public void setCrm_to_date(String crm_to_date) {
        this.crm_to_date = crm_to_date;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public int getGift_status() {
        return gift_status;
    }

    public void setGift_status(int gift_status) {
        this.gift_status = gift_status;
    }

    public String getWhich_kind() {
        return which_kind;
    }

    public void setWhich_kind(String which_kind) {
        this.which_kind = which_kind;
    }

    public String getVisit_date() {
        return visit_date;
    }

    public void setVisit_date(String visit_date) {
        this.visit_date = visit_date;
    }

    public String getRemarks_mr() {
        return remarks_mr;
    }

    public void setRemarks_mr(String remarks_mr) {
        this.remarks_mr = remarks_mr;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}
