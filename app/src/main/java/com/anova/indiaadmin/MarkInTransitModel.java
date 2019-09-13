package com.anova.indiaadmin;

import com.anova.indiaadmin.utils.FormatConversionHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by iqbal on 11/5/18.
 */

public class MarkInTransitModel implements Serializable {

    /**
     * sample_id : 17
     * timestamp : 2018-05-11 04:11:30
     */

    @SerializedName("sample_id")
    private String sampleId;
    @SerializedName("challan_number")
    private String challanNumber;

    public String getQci() {
        return qci;
    }

    public void setQci(String qci) {
        this.qci = qci;
    }

    @SerializedName("qci")
    private String qci;
    @SerializedName("created_at")
    private String timeStamp;

    public static MarkInTransitModel objectFromData(String str) {

        return new Gson().fromJson(str, MarkInTransitModel.class);
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getChallanNumber() {
        return challanNumber;
    }

    public void setChallanNumber(String challanNumber) {
        this.challanNumber = challanNumber;
    }

    public String getDate(){
        return FormatConversionHelper.getFormatedDateTime(timeStamp, "yyyy-MM-dd hh:mm:ss", "dd-MM-yyyy");
    }
}
