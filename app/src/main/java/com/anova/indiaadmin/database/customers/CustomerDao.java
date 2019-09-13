package com.anova.indiaadmin.database.customers;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by iqbal on 7/5/18.
 */

@Dao
public interface CustomerDao {
    @Query("SELECT * FROM customers")
    List<CustomerEntity> getAll();

    @Query("SELECT * FROM customers where subsidiary=:subsidiary and auction_type=:auctionType")
    List<CustomerEntity> getFiltered(String subsidiary, String auctionType);

    @Query("SELECT * FROM customers where subsidiary=:subsidiary and area=:area and location=:location and mode=:mode and auction_type=:auctionType")
    List<CustomerEntity> filterCustomers(String subsidiary, String area, String location, String mode, String auctionType);

    @Insert
    void insertAll(List<CustomerEntity> customerEntityList);

    @Delete
    void delete(CustomerEntity customerEntity);

    @Query("DELETE FROM customers")
    void deleteAll();
}
