package com.anova.indiaadmin.database.location;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by iqbal on 6/5/18.
 */

@Dao
public interface LocationDao {
    @Query("SELECT * FROM area_location")
    List<Location> getAll();

    @Query("SELECT * FROM area_location where area_id=:areaId")
    List<Location> getLocationByArea(int areaId);

    @Insert
    void insertAll(List<Location> locationList);

    @Delete
    void delete(Location location);

    @Query("DELETE FROM area_location")
    void deleteAll();
}
