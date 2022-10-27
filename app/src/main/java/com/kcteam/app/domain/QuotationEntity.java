package com.kcteam.app.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kcteam.app.AppConstant;

/**
 * Created by Saikat on 17-Jun-20.
 */
@Entity(tableName = AppConstant.QUOTATION_TABLE)
public class QuotationEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id = 0;

    @ColumnInfo(name = "quo_id")
    private String quo_id = null;

    @ColumnInfo(name = "quo_no")
    private String quo_no = null;

    @ColumnInfo(name = "date")
    private String date = null;

    @ColumnInfo(name = "hypothecation")
    private String hypothecation = null;

    @ColumnInfo(name = "account_no")
    private String account_no = null;

    @ColumnInfo(name = "model_id")
    private String model_id = null;

    @ColumnInfo(name = "bs_id")
    private String bs_id = null;

    @ColumnInfo(name = "gearbox")
    private String gearbox = null;

    @ColumnInfo(name = "number1")
    private String number1 = null;

    @ColumnInfo(name = "value1")
    private String value1 = null;

    @ColumnInfo(name = "value2")
    private String value2 = null;

    @ColumnInfo(name = "tyres1")
    private String tyres1 = null;

    @ColumnInfo(name = "number2")
    private String number2 = null;

    @ColumnInfo(name = "value3")
    private String value3 = null;

    @ColumnInfo(name = "value4")
    private String value4 = null;

    @ColumnInfo(name = "tyres2")
    private String tyres2 = null;

    @ColumnInfo(name = "amount")
    private String amount = null;

    @ColumnInfo(name = "discount")
    private String discount = null;

    @ColumnInfo(name = "cgst")
    private String cgst = null;

    @ColumnInfo(name = "sgst")
    private String sgst = null;

    @ColumnInfo(name = "tcs")
    private String tcs = null;

    @ColumnInfo(name = "insurance")
    private String insurance = null;

    @ColumnInfo(name = "net_amount")
    private String net_amount = null;

    @ColumnInfo(name = "remarks")
    private String remarks = null;

    @ColumnInfo(name = "shop_id")
    private String shop_id = null;

    @ColumnInfo(name = "isUploaded")
    private boolean isUploaded = false;

    @ColumnInfo(name = "isEditUpdated")
    private int isEditUpdated = -1;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuo_id() {
        return quo_id;
    }

    public void setQuo_id(String quo_id) {
        this.quo_id = quo_id;
    }

    public String getQuo_no() {
        return quo_no;
    }

    public void setQuo_no(String quo_no) {
        this.quo_no = quo_no;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHypothecation() {
        return hypothecation;
    }

    public void setHypothecation(String hypothecation) {
        this.hypothecation = hypothecation;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getModel_id() {
        return model_id;
    }

    public void setModel_id(String model_id) {
        this.model_id = model_id;
    }

    public String getBs_id() {
        return bs_id;
    }

    public void setBs_id(String bs_id) {
        this.bs_id = bs_id;
    }

    public String getGearbox() {
        return gearbox;
    }

    public void setGearbox(String gearbox) {
        this.gearbox = gearbox;
    }

    public String getNumber1() {
        return number1;
    }

    public void setNumber1(String number1) {
        this.number1 = number1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getTyres1() {
        return tyres1;
    }

    public void setTyres1(String tyres1) {
        this.tyres1 = tyres1;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }

    public String getValue4() {
        return value4;
    }

    public void setValue4(String value4) {
        this.value4 = value4;
    }

    public String getTyres2() {
        return tyres2;
    }

    public void setTyres2(String tyres2) {
        this.tyres2 = tyres2;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCgst() {
        return cgst;
    }

    public void setCgst(String cgst) {
        this.cgst = cgst;
    }

    public String getSgst() {
        return sgst;
    }

    public void setSgst(String sgst) {
        this.sgst = sgst;
    }

    public String getTcs() {
        return tcs;
    }

    public void setTcs(String tcs) {
        this.tcs = tcs;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getNet_amount() {
        return net_amount;
    }

    public void setNet_amount(String net_amount) {
        this.net_amount = net_amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public int getIsEditUpdated() {
        return isEditUpdated;
    }

    public void setIsEditUpdated(int isEditUpdated) {
        this.isEditUpdated = isEditUpdated;
    }
}
