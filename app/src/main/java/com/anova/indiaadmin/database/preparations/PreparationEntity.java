package com.anova.indiaadmin.database.preparations;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.anova.indiaadmin.UnpreparedSampleModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

@Entity
public class PreparationEntity {
    @PrimaryKey
    private int prepId;
    private String subsidiary;
    private String area;
    private String location;
    private String myJsonData;
    private int modifiedCount = 0;
    private String createdAt;

    public PreparationEntity(int prepId, String subsidiary, String area, String location, String myJsonData, int modifiedCount) {
        this.prepId = prepId;
        this.subsidiary = subsidiary;
        this.area = area;
        this.location = location;
        this.myJsonData = myJsonData;
        this.modifiedCount = modifiedCount;

        Gson gson = new Gson();
        Type objectType = new TypeToken<UnpreparedSampleModel>() {}.getType();
        UnpreparedSampleModel unpreparedSampleModelList = null;
        unpreparedSampleModelList = gson.fromJson(myJsonData.toString(), objectType);
        setCreatedAt(unpreparedSampleModelList.getCreatedAt());
    }


    public int getPrepId() {
        return prepId;
    }

    public String getSubsidiary() {
        return subsidiary;
    }

    public String getArea() {
        return area;
    }

    public String getLocation() {
        return location;
    }

    public String getMyJsonData() {
        return myJsonData;
    }

    public int getModifiedCount() {
        return modifiedCount;
    }

    public boolean isModified(){
        return modifiedCount >0;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


}
