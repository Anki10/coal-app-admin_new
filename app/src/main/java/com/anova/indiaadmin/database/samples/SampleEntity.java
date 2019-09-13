package com.anova.indiaadmin.database.samples;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by iqbal on 7/5/18.
 */

@Entity(tableName = "samples")
public class SampleEntity {

    @PrimaryKey(autoGenerate = true)
    private int localSampleId;

    @ColumnInfo(name = "remote_sample_id")
    private int remoteSampleId;

    @ColumnInfo(name = "sample_user_id")
    private int sampleUserId;

    @ColumnInfo(name = "subsidiary_name")
    private String subsidiaryName;

    @ColumnInfo(name = "area")
    private String area;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "is_primary")
    private String isPrimary;

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
    }

    @ColumnInfo(name = "date_of_collection")
    private String collectionDate;

    @ColumnInfo(name = "lifting_type")
    private String liftingType;

    @ColumnInfo(name = "auction_type")
    private String auctionType;

    @ColumnInfo(name = "quantity_lifted")
    private String quantityLifted;

    @ColumnInfo(name = "quantity_sampled")
    private String quantitySampled;

    @ColumnInfo(name = "sampling_type")
    private String samplingType;

    @ColumnInfo(name = "challan_number")
    private String challanNumber;

    @ColumnInfo(name = "qci_number")
    private String qciNumber;

    public String getLocation_code() {
        return location_code;
    }

    public void setLocation_code(String location_code) {
        this.location_code = location_code;
    }

    @ColumnInfo(name = "location_code")
    private String location_code;

    public String getLattitude() {
        return lattitude;
    }

    public void setLattitude(String lattitude) {
        this.lattitude = lattitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @ColumnInfo(name = "lattitude")
    private String lattitude;

    @ColumnInfo(name = "longitude")
    private String longitude;


    public SampleEntity() {
    }

    public SampleEntity(int localSampleId, int remoteSampleId, int sampleUserId, String subsidiaryName, String area, String location, String locationCode, String isPrimary, String  collectionDate, String liftingType, String auctionType, String quantityLifted, String quantitySampled, String samplingType) {
        this.localSampleId = localSampleId;
        this.remoteSampleId = remoteSampleId;
        this.sampleUserId = sampleUserId;
        this.subsidiaryName = subsidiaryName;
        this.area = area;
        this.location = location;
        this.location_code = locationCode;
        this.isPrimary = isPrimary;
        this.collectionDate = collectionDate;
        this.liftingType = liftingType;
        this.auctionType = auctionType;
        this.quantityLifted = quantityLifted;
        this.quantitySampled = quantitySampled;
        this.samplingType = samplingType;
    }

    public int getLocalSampleId() {
        return localSampleId;
    }

    public void setLocalSampleId(int localSampleId) {
        this.localSampleId = localSampleId;
    }

    public int getRemoteSampleId() {
        return remoteSampleId;
    }

    public void setRemoteSampleId(int remoteSampleId) {
        this.remoteSampleId = remoteSampleId;
    }

    public int getSampleUserId() {
        return sampleUserId;
    }

    public void setSampleUserId(int sampleUserId) {
        this.sampleUserId = sampleUserId;
    }

    public String getSubsidiaryName() {
        return subsidiaryName;
    }

    public void setSubsidiaryName(String subsidiaryName) {
        this.subsidiaryName = subsidiaryName;
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

    public String getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(String isPrimary) {
        this.isPrimary = isPrimary;
    }



    public String getLiftingType() {
        return liftingType;
    }

    public void setLiftingType(String liftingType) {
        this.liftingType = liftingType;
    }

    public String getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(String auctionType) {
        this.auctionType = auctionType;
    }

    public String getQuantityLifted() {
        return quantityLifted;
    }

    public void setQuantityLifted(String quantityLifted) {
        this.quantityLifted = quantityLifted;
    }

    public String getQuantitySampled() {
        return quantitySampled;
    }

    public void setQuantitySampled(String quantitySampled) {
        this.quantitySampled = quantitySampled;
    }

    public String getSamplingType() {
        return samplingType;
    }

    public void setSamplingType(String samplingType) {
        this.samplingType = samplingType;
    }

    public String getChallanNumber() {
        return challanNumber;
    }

    public void setChallanNumber(String challanNumber) {
        this.challanNumber = challanNumber;
    }

    public String getQciNumber() {
        return qciNumber;
    }

    public void setQciNumber(String qciNumber) {
        this.qciNumber = qciNumber;
    }
}
