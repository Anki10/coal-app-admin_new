package com.anova.indiaadmin.database.samplecustomer;

/**
 * Created by raj on 6/7/2018.
 */

public class MoredetailsEntity {
    private String do_details;
    private String quantity;
    private String fsa_number;

    public String getFsa_number() {
        return fsa_number;
    }

    public void setFsa_number(String fsa_number) {
        this.fsa_number = fsa_number;
    }



    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private int position;

    public String getDo_details() {
        return do_details;
    }

    public void setDo_details(String do_details) {
        this.do_details = do_details;
    }

    public String getQuantity() {
        return quantity;
    }
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


}
