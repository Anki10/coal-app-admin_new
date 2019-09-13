package com.anova.indiaadmin.database.preparationimages;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface PreparationImageQrDao {
    @Query("select * from preparationimageqrentity where prepId=:prepId")
    PreparationImageQrEntity getPrepImageQrByPrepId(int prepId);

    @Query("delete from preparationimageqrentity where prepId=:prepId")
    void deleteByPrepId(int prepId);

    @Insert
    void insert(PreparationImageQrEntity preparationImageQrEntity);

    @Query("update preparationimageqrentity set image4=:image4, image5=:image5 where prepId=:prepId")
    void updateImages(int prepId, String image4, String image5);

    @Query("update preparationimageqrentity set qciQr=:qciQr, subsidiaryQr=:subsidiaryQr, customerQr=:customerQr, refreeQr=:refreeQr where prepId=:prepId")
    void updateQrs(int prepId, String qciQr, String subsidiaryQr, String customerQr, String refreeQr);
}
