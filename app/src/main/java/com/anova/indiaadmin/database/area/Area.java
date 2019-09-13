package com.anova.indiaadmin.database.area;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by iqbal on 3/5/18.
 */

@Entity(tableName = "subsidiary_areas")
public class Area {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "sub_id")
    private int subsidiaryId;

    @ColumnInfo(name = "name")
    private String name;

    public Area() {
    }

    public Area(int id, int subsidiaryId, String name) {
        this.id = id;
        this.subsidiaryId = subsidiaryId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubsidiaryId() {
        return subsidiaryId;
    }

    public void setSubsidiaryId(int subsidiaryId) {
        this.subsidiaryId = subsidiaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
