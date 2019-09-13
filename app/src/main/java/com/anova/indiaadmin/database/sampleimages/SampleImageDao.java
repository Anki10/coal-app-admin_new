package com.anova.indiaadmin.database.sampleimages;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by iqbal on 9/5/18.
 */

@Dao
public interface SampleImageDao {
    @Query("SELECT * FROM sample_images")
    List<SampleImageEntity> getAll();

    @Query("SELECT * FROM sample_images where localSampleId=:localSampleId")
    List<SampleImageEntity> getAllByLocalSampleId(int localSampleId);

    @Insert
    void insertAll(List<SampleImageEntity> sampleImageEntityList);

    @Insert
    Long insert(SampleImageEntity sampleImageEntity);

    @Update
    void update(SampleImageEntity sampleImageEntity);

    @Query("DELETE FROM sample_images where localSampleId=:localSampleId")
    void deleteByLocalSampleId(int localSampleId);

    @Delete
    void delete(SampleImageEntity sampleImageEntity);

    @Query("DELETE FROM sample_images")
    void deleteAll();
}
