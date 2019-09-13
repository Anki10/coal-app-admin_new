package com.anova.indiaadmin.database.subsidiary;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by iqbal on 2/5/18.
 */

@Dao
public interface SubsidiaryDao {
    @Query("SELECT * FROM subsidiaries")
    List<Subsidiary> getAll();

    @Insert
    void insertAll(List<Subsidiary> subsidiaryList);

    @Delete
    void delete(Subsidiary subsidiary);

    @Query("DELETE FROM subsidiaries")
    void deleteAll();
}
