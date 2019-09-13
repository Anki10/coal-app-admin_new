package com.anova.indiaadmin.database.customers;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by iqbal on 7/5/18.
 */

@Entity(tableName = "customers")
public class CustomerEntity {

    @NonNull
    @PrimaryKey
    private String userId;

    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "fsa_no")
    private String fsaNo;

    @ColumnInfo(name = "subsidiary")
    private String subsidiary;

    @ColumnInfo(name = "area")
    private String area;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "mode")
    private String mode;

    @ColumnInfo(name = "auction_type")
    private String auctionType;

    public CustomerEntity() {
    }

    public CustomerEntity(String userId, String userName, String fsaNo, String subsidiary, String area, String location, String mode, String auctionType) {
        this.userId = userId;
        this.userName = userName;
        this.fsaNo = fsaNo;
        this.subsidiary = subsidiary;
        this.area = area;
        this.location = location;
        this.mode = mode;
        this.auctionType = auctionType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFsaNo() {
        return fsaNo;
    }

    public void setFsaNo(String fsaNo) {
        this.fsaNo = fsaNo;
    }

    public String getSubsidiary() {
        return subsidiary;
    }

    public void setSubsidiary(String subsidiary) {
        this.subsidiary = subsidiary;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }
}
