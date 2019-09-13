package com.anova.indiaadmin.database.samplecustomer;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by iqbal on 9/5/18.
 */

@Dao
public interface SampleCustomerDao {
    @Query("SELECT * FROM sample_customers")
    List<SampleCustomerEntity> getAll();

    @Query("SELECT * FROM sample_customers where localSampleId=:localSampleId")
    List<SampleCustomerEntity> getOneSampleCustomerByLocalSampleId(int localSampleId);

    @Insert
    void insertAll(List<SampleCustomerEntity> sampleCustomerEntityList);

    @Query("UPDATE sample_customers SET auction_type = :auction_type, customerId = :customerId, customer_name = :customer_name,lifting_type = :lifting_type, more_details = :more_details, declared_grade = :declared_grade, total_vehicles = :total_vehicles,total_vehicles_sampled =:total_vehicles_sampled where id=:id ")
    void updateList(String auction_type,int customerId, String customer_name,String lifting_type,String more_details,String declared_grade,String total_vehicles,String total_vehicles_sampled,int id);

    @Insert
    Long insert(SampleCustomerEntity sampleCustomerEntity);

    @Query("DELETE FROM sample_customers where id=:id")
    void deleteByLocalSampleId(int id);

    @Delete
    void delete(SampleCustomerEntity sampleCustomerEntity);

    @Query("DELETE FROM sample_customers")
    void deleteAll();
}
