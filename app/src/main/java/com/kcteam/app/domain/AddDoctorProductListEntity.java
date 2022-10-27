package com.kcteam.app.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kcteam.app.AppConstant;

@Entity(tableName = AppConstant.DOCTOR_VISIT_PRODUCT_TABLE)
public class AddDoctorProductListEntity {

    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    @ColumnInfo(name = "doc_visit_id")
    private String doc_visit_id = null;

    @ColumnInfo(name = "shop_id")
    private String shop_id = null;

    @ColumnInfo(name = "product_id")
    private String product_id = null;

    @ColumnInfo(name = "product_name")
    private String product_name = null;

    @ColumnInfo(name = "product_status")
    private int product_status = -1;

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

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public int getProduct_status() {
        return product_status;
    }

    public void setProduct_status(int product_status) {
        this.product_status = product_status;
    }
}
