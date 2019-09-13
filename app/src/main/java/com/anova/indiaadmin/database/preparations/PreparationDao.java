package com.anova.indiaadmin.database.preparations;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface PreparationDao {
    @Query("select * from preparationentity where subsidiary=:subsidiary and area=:area and location=:location")
    List<PreparationEntity> getFilteredPrepSample(String subsidiary, String area, String location);

    @Query("select * from preparationentity where prepId=:prepId limit 1")
    PreparationEntity getPrepSampleByLocalPrepId(int prepId);

    @Query("delete from preparationentity where prepId=:prepId")
    void deleteByPLocalPrepId(int prepId);

    @Insert
    void insert(PreparationEntity preparationEntity);

    @Query("update preparationentity set myJsonData=:myJSONData where prepId=:prepId")
    void updatePrepSampleJson(int prepId, String myJSONData);

    @Query("update preparationentity set modifiedCount=:modifiedCount where prepId=:prepId")
    void markModified(int prepId, int modifiedCount);
}