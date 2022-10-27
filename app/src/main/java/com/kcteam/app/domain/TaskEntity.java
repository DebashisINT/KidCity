package com.kcteam.app.domain;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kcteam.app.AppConstant;

/**
 * Created by Saikat on 13-Aug-20.
 */
@Entity(tableName = AppConstant.TASK_TABLE)
public class TaskEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id = 0;

    @ColumnInfo(name = "task_id")
    private String task_id = null;

    @ColumnInfo(name = "date")
    private String date = null;

    @ColumnInfo(name = "task_name")
    private String task_name = null;

    @ColumnInfo(name = "details")
    private String details = null;

    @ColumnInfo(name = "isUploaded")
    private boolean isUploaded = false;

    @ColumnInfo(name = "isCompleted")
    private boolean isCompleted = false;

    @ColumnInfo(name = "isStatusUpdated")
    private int isStatusUpdated = -1;

    @ColumnInfo(name = "eventId")
    private String eventId = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public int getIsStatusUpdated() {
        return isStatusUpdated;
    }

    public void setIsStatusUpdated(int isStatusUpdated) {
        this.isStatusUpdated = isStatusUpdated;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
