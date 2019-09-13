package com.anova.indiaadmin.database.samples;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by iqbal on 7/5/18.
 */

@Dao
public interface SampleDao {
    @Query("SELECT * FROM samples")
    List<SampleEntity> getAll();

    @Query("SELECT * FROM samples where localSampleId=:localSampleId")
    SampleEntity getSampleById(int localSampleId);

    @Insert
    void insertAll(List<SampleEntity> sampleEntityList);

    @Insert
    Long insert(SampleEntity sampleEntity);

    @Query("UPDATE samples SET subsidiary_name = :subsidiary, area = :area, location = :location, date_of_collection = :collectionDate, lifting_type = :liftingType, auction_type = :auctionType, quantity_lifted = :quantityLifted, quantity_sampled = :quantitySampled, sampling_type = :samplingType WHERE localSampleId = :localSampleId")
    void update(int localSampleId, String subsidiary, String area, String location, String collectionDate, String liftingType, String auctionType, String quantityLifted, String quantitySampled, String samplingType);

    @Query("update samples set challan_number = :challanNumber, qci_number = :qciNumber,lattitude = :latitude,longitude = :longitude  where localSampleId = :localSampleId")
    void updateChallanDetails(int localSampleId, String challanNumber, String qciNumber, String latitude,String longitude);

    @Delete
    void delete(SampleEntity sampleEntity);

    @Query("DELETE FROM samples")
    void deleteAll();

    @Query("DELETE FROM samples where localSampleId=:localSampleId")
    void deleteByLocalSampleId(int localSampleId);
}
