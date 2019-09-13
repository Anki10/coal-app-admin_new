package com.anova.indiaadmin.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.anova.indiaadmin.database.area.Area;
import com.anova.indiaadmin.database.area.AreaDao;
import com.anova.indiaadmin.database.customers.CustomerDao;
import com.anova.indiaadmin.database.customers.CustomerEntity;
import com.anova.indiaadmin.database.location.Location;
import com.anova.indiaadmin.database.location.LocationDao;
import com.anova.indiaadmin.database.preparationimages.PreparationImageQrDao;
import com.anova.indiaadmin.database.preparationimages.PreparationImageQrEntity;
import com.anova.indiaadmin.database.preparations.PreparationDao;
import com.anova.indiaadmin.database.preparations.PreparationEntity;
import com.anova.indiaadmin.database.samplecustomer.SampleCustomerDao;
import com.anova.indiaadmin.database.samplecustomer.SampleCustomerEntity;
import com.anova.indiaadmin.database.sampleimages.SampleImageDao;
import com.anova.indiaadmin.database.sampleimages.SampleImageEntity;
import com.anova.indiaadmin.database.samples.SampleDao;
import com.anova.indiaadmin.database.samples.SampleEntity;
import com.anova.indiaadmin.database.subsidiary.SubsidiaryDao;
import com.anova.indiaadmin.database.subsidiary.Subsidiary;
import com.anova.indiaadmin.utils.Constants;

/**
 * Created by iqbal on 2/5/18.
 */

@Database(entities = {Subsidiary.class,
            Area.class,
            Location.class,
            SampleEntity.class,
            CustomerEntity.class,
            SampleCustomerEntity.class,
            SampleImageEntity.class,
            PreparationEntity.class,
            PreparationImageQrEntity.class
        },
        version = 7)
public abstract class AppDatabase extends RoomDatabase {

    public static final int INSERT_ACTION = 1;
    public static final int SELECT_ACTION = 2;
    public static final int UPDATE_ACTION = 3;
    public static final int DELETE_ACTION = 4;
    public static final int DELETE_ALL_ACTION = 4;


    private static AppDatabase INSTANCE;

    public abstract SubsidiaryDao subsidiaryDao();
    public abstract AreaDao areaDao();
    public abstract LocationDao locationDao();
    public abstract SampleDao sampleDao();
    public abstract CustomerDao customerDao();
    public abstract SampleCustomerDao sampleCustomerDao();
    public abstract SampleImageDao sampleImageDao();
    public abstract PreparationDao preparationDao();
    public abstract PreparationImageQrDao preparationImageQrDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, Constants.DATABASE_NAME).fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }



}
