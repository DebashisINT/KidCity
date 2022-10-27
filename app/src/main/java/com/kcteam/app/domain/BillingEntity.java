package com.kcteam.app.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kcteam.app.AppConstant;

/**
 * Created by Saikat on 19-02-2019.
 */
@Entity(tableName = AppConstant.BILLING_TABLE)
public class BillingEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "bill_id")
    private String bill_id = null;

    @ColumnInfo(name = "invoice_no")
    private String invoice_no = null;

    @ColumnInfo(name = "invoice_date")
    private String invoice_date = null;

    @ColumnInfo(name = "invoice_amount")
    private String invoice_amount = null;

    @ColumnInfo(name = "remarks")
    private String remarks = null;

    @ColumnInfo(name = "order_id")
    private String order_id = null;

    @ColumnInfo(name = "isUploaded")
    private boolean isUploaded = false;

    @ColumnInfo(name = "isEditUploaded")
    private int isEditUploaded = -1;

    @ColumnInfo(name = "isDeleteUploaded")
    private int isDeleteUploaded = -1;

    @ColumnInfo(name = "attachment")
    private String attachment = "";

    @ColumnInfo(name = "patient_no")
    private String patient_no = null;

    @ColumnInfo(name = "patient_name")
    private String patient_name = null;

    @ColumnInfo(name = "patient_address")
    private String patient_address = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBill_id() {
        return bill_id;
    }

    public void setBill_id(String bill_id) {
        this.bill_id = bill_id;
    }

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getInvoice_amount() {
        return invoice_amount;
    }

    public void setInvoice_amount(String invoice_amount) {
        this.invoice_amount = invoice_amount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public int getIsEditUploaded() {
        return isEditUploaded;
    }

    public void setIsEditUploaded(int isEditUploaded) {
        this.isEditUploaded = isEditUploaded;
    }

    public int getIsDeleteUploaded() {
        return isDeleteUploaded;
    }

    public void setIsDeleteUploaded(int isDeleteUploaded) {
        this.isDeleteUploaded = isDeleteUploaded;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getPatient_no() {
        return patient_no;
    }

    public void setPatient_no(String patient_no) {
        this.patient_no = patient_no;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPatient_address() {
        return patient_address;
    }

    public void setPatient_address(String patient_address) {
        this.patient_address = patient_address;
    }
}
