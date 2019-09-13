package com.anova.indiaadmin.database.sampleimages;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by iqbal on 9/5/18.
 */

@Entity(tableName = "sample_images")
public class SampleImageEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "localSampleId")
    private int localSampleId;
    @ColumnInfo(name = "localFilePath")
    private String localFilePath;
    @ColumnInfo(name = "remoteFileName")
    private String remoteFileName;

    public SampleImageEntity() {
    }

    public SampleImageEntity(int id, int localSampleId, String localFilePath, String remoteFileName) {
        this.id = id;
        this.localSampleId = localSampleId;
        this.localFilePath = localFilePath;
        this.remoteFileName = remoteFileName;
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

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public String getRemoteFileName() {
        return remoteFileName;
    }

    public void setRemoteFileName(String remoteFileName) {
        this.remoteFileName = remoteFileName;
    }
}
