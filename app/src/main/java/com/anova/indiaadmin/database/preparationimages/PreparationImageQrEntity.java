package com.anova.indiaadmin.database.preparationimages;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class PreparationImageQrEntity {
    @PrimaryKey
    private int prepId;
    private String image4;
    private String image5;
    private String qciQr;
    private String subsidiaryQr;
    private String customerQr;
    private String refreeQr;

    public PreparationImageQrEntity(int prepId) {
        this.prepId = prepId;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }

    public void setImage5(String image5) {
        this.image5 = image5;
    }

    public void setQciQr(String qciQr) {
        this.qciQr = qciQr;
    }

    public void setSubsidiaryQr(String subsidiaryQr) {
        this.subsidiaryQr = subsidiaryQr;
    }

    public void setCustomerQr(String customerQr) {
        this.customerQr = customerQr;
    }

    public void setRefreeQr(String refreeQr) {
        this.refreeQr = refreeQr;
    }

    public int getPrepId() {
        return prepId;
    }

    public String getImage4() {
        return image4;
    }

    public String getImage5() {
        return image5;
    }

    public String getQciQr() {
        return qciQr;
    }

    public String getSubsidiaryQr() {
        return subsidiaryQr;
    }

    public String getCustomerQr() {
        return customerQr;
    }

    public String getRefreeQr() {
        return refreeQr;
    }
}
