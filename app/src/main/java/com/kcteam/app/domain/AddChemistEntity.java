package com.kcteam.app.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kcteam.app.AppConstant;

@Entity(tableName = AppConstant.CHEMIST_VISIT_LIST_TABLE)
public class AddChemistEntity {

    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "chemist_visit_id")
    private String chemist_visit_id = null;

    @ColumnInfo(name = "shop_id")
    private String shop_id = null;

    @ColumnInfo(name = "pob")
    private int pob = -1;

    @ColumnInfo(name = "volume")
    private String volume = null;

    @ColumnInfo(name = "remarks")
    private String remarks = null;

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

    public String getChemist_visit_id() {
        return chemist_visit_id;
    }

    public void setChemist_visit_id(String chemist_visit_id) {
        this.chemist_visit_id = chemist_visit_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public int getPob() {
        return pob;
    }

    public void setPob(int pob) {
        this.pob = pob;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
