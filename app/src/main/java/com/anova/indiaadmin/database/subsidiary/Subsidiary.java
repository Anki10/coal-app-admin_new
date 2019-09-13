package com.anova.indiaadmin.database.subsidiary;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by iqbal on 2/5/18.
 */

@Entity(tableName = "subsidiaries")
public class Subsidiary {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    public Subsidiary() {
    }

    public Subsidiary(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
