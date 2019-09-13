package com.anova.indiaadmin.database.area;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by iqbal on 3/5/18.
 */

@Dao
public interface AreaDao {
    @Query("SELECT * FROM subsidiary_areas")
    List<Area> getAll();

    @Query("SELECT * FROM subsidiary_areas where sub_id=:subsidiaryId")
    List<Area> getAreasBySubsidiaryId(int subsidiaryId);

    @Insert
    void insertAll(List<Area> areaList);

    @Delete
    void delete(Area area);

    @Query("DELETE FROM subsidiary_areas")
    void deleteAll();
}
