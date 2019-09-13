package com.anova.indiaadmin.database.samplecustomer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by iqbal on 9/5/18.
 */

@Entity(tableName = "sample_customers")
public class SampleCustomerEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "localSampleId")
    private int localSampleId;

    public String getAuction_type() {
        return auction_type;
    }

    public void setAuction_type(String auction_type) {
        this.auction_type = auction_type;
    }

    @ColumnInfo(name = "auction_type")
    private String auction_type;

    @ColumnInfo(name = "customerId")
    private int customerId;

    @ColumnInfo(name = "customer_name")
    private String customerName;

    @ColumnInfo(name = "lifting_type")
    private String liftingType;

    public String getMore_details() {
        return more_details;
    }

    public void setMore_details(String more_details) {
        this.more_details = more_details;
    }

    @ColumnInfo(name = "more_details")
    private String more_details;

    @ColumnInfo(name = "declared_grade")
    private String declaredGrade;

    @ColumnInfo(name = "total_vehicles")
    private String totalVehicles;

    @ColumnInfo(name = "total_vehicles_sampled")
    private String totalVehiclesSampled;


    @ColumnInfo(name = "challan_code")
    private String challanCode;


    @ColumnInfo(name = "rrdate")
    private String rrdate;

    public SampleCustomerEntity() {
    }

    public SampleCustomerEntity(int id, int localSampleId,String auctionType, int customerId, String customerName, String liftingType, String details, String declaredGrade, String totalVehicles, String totalVehiclesSampled,String challan_code) {
        this.id = id;
        this.localSampleId = localSampleId;
        this.auction_type = auctionType;
        this.customerId = customerId;
        this.customerName = customerName;
        this.liftingType = liftingType;
        this.more_details = details;
        this.declaredGrade = declaredGrade;
        this.totalVehicles = totalVehicles;
        this.totalVehiclesSampled = totalVehiclesSampled;
        this.challanCode = challan_code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocalSampleId() {
        return localSampleId;
    }

    public void setLocalSampleId(int localSampleId) {
        this.localSampleId = localSampleId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getLiftingType() {
        return liftingType;
    }

    public void setLiftingType(String liftingType) {
        this.liftingType = liftingType;
    }

    public String getDeclaredGrade() {
        return declaredGrade;
    }

    public void setDeclaredGrade(String declaredGrade) {
        this.declaredGrade = declaredGrade;
    }

    public String getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(String totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public String getTotalVehiclesSampled() {
        return totalVehiclesSampled;
    }

    public void setTotalVehiclesSampled(String totalVehiclesSampled) {
        this.totalVehiclesSampled = totalVehiclesSampled;
    }

    public String getChallanCode() {
        return challanCode;
    }

    public void setChallanCode(String challanCode) {
        this.challanCode = challanCode;
    }

    public String getRrdate() {
        return rrdate;
    }

    public void setRrdate(String rrdate) {
        this.rrdate = rrdate;
    }

}
