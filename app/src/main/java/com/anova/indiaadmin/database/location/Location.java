package com.anova.indiaadmin.database.location;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by iqbal on 6/5/18.
 */

@Entity(tableName = "area_location")
public class Location {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "area_id")
    private int areaId;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "location_code")
    private String location_code;

    @ColumnInfo(name = "declared_grade")
    private String declared_grade;

    public String getDeclared_grade() {
        return declared_grade;
    }

    public void setDeclared_grade(String declared_grade) {
        this.declared_grade = declared_grade;
    }



    public String getLocation_code() {
        return location_code;
    }

    public void setLocation_code(String location_code) {
        this.location_code = location_code;
    }



    public Location() {
    }

    public Location(int id, int areaId, String name,String locationCode,String declaredgrade) {
        this.id = id;
        this.areaId = areaId;
        this.name = name;
        this.location_code = locationCode;
        this.declared_grade = declaredgrade;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
