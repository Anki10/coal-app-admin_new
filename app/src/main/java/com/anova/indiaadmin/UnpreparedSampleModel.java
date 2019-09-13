package com.anova.indiaadmin;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iqbal on 10/5/18.
 */

public class UnpreparedSampleModel implements Parcelable {

    /**
     * sample_id : 105
     * auction_type : Spot
     * total_vehicles : null
     * type_of_sampling : Gross
     * collection_image_1 : null
     * collection_image_2 : null
     * collection_image_3 : null
     * customers : [{"customer_id":"13","do_details":"22","declared_grade":"13","lifting_type":null,"total_vehicles":"20","total_vehicles_sampled":"12"},{"customer_id":"14","do_details":"22","declared_grade":"11","lifting_type":null,"total_vehicles":"18","total_vehicles_sampled":"12"},{"customer_id":"13","do_details":"22","declared_grade":"13","lifting_type":null,"total_vehicles":"20","total_vehicles_sampled":"12"},{"customer_id":"16","do_details":"22","declared_grade":"11","lifting_type":null,"total_vehicles":"18","total_vehicles_sampled":"12"},{"customer_id":"13","do_details":"22","declared_grade":"13","lifting_type":null,"total_vehicles":"20","total_vehicles_sampled":"12"},{"customer_id":"16","do_details":"22","declared_grade":"11","lifting_type":null,"total_vehicles":"18","total_vehicles_sampled":"12"},{"customer_id":"13","do_details":"22","declared_grade":"13","lifting_type":null,"total_vehicles":"20","total_vehicles_sampled":"12"},{"customer_id":"16","do_details":"22","declared_grade":"11","lifting_type":null,"total_vehicles":"18","total_vehicles_sampled":"12"}]
     */

    @SerializedName("sample_id")
    private String sampleId;

    @SerializedName("total_vehicles_sum")
    private String totalVehicles;
    @SerializedName("type_of_sampling")
    private String typeOfSampling;
    @SerializedName("collection_image_1")
    private String collectionImage1;
    @SerializedName("collection_image_2")
    private String collectionImage2;
    @SerializedName("collection_image_3")
    private String collectionImage3;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @SerializedName("mode")
    private String mode;

    public String getQci() {
        return qci;
    }

    public void setQci(String qci) {
        this.qci = qci;
    }

    @SerializedName("qci")
    private String qci;

    private List<CustomersBean> customers;

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }


    public String getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(String totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public String getTypeOfSampling() {
        return typeOfSampling;
    }

    public void setTypeOfSampling(String typeOfSampling) {
        this.typeOfSampling = typeOfSampling;
    }

    public String getCollectionImage1() {
        return collectionImage1;
    }

    public void setCollectionImage1(String collectionImage1) {
        this.collectionImage1 = collectionImage1;
    }

    public String getCollectionImage2() {
        return collectionImage2;
    }

    public void setCollectionImage2(String collectionImage2) {
        this.collectionImage2 = collectionImage2;
    }

    public String getCollectionImage3() {
        return collectionImage3;
    }

    public void setCollectionImage3(String collectionImage3) {
        this.collectionImage3 = collectionImage3;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<CustomersBean> getCustomers() {
        return customers;
    }

    public void setCustomers(List<CustomersBean> customers) {
        this.customers = customers;
    }

    public static class CustomersBean implements Parcelable {
        /**
         * id : 13
         * do_details : 22
         * declared_grade : 13
         * lifting_type : null
         * total_vehicles : 20
         * total_vehicles_sampled : 12
         */

        @SerializedName("id")
        private String customerId;
        @SerializedName("do_details")
        private String doDetails;
        @SerializedName("fsa_no")
        private String fsaNumber;
        @SerializedName("declared_grade")
        private String declaredGrade;
        @SerializedName("lifting_type")
        private String liftingType;
        @SerializedName("total_vehicles")
        private String totalVehicles;
        @SerializedName("total_vehicles_sampled")
        private String totalVehiclesSampled;
        @SerializedName("user_name")
        private String userName;
        @SerializedName("quantity")
        private String quantity;

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }



        public String getAuctionType() {
            return auctionType;
        }

        public void setAuctionType(String auctionType) {
            this.auctionType = auctionType;
        }

        @SerializedName("auction_type")
        private String auctionType;

        public static CustomersBean objectFromData(String str) {

            return new Gson().fromJson(str, CustomersBean.class);
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getDoDetails() {
            return doDetails;
        }

        public void setDoDetails(String doDetails) {
            this.doDetails = doDetails;
        }

        public String getFsaNumber(){
            return fsaNumber;
        }

        public void setFsaNumber(String fsaNumber){
            this.fsaNumber = fsaNumber;
        }

        public String getDeclaredGrade() {
            return declaredGrade;
        }

        public void setDeclaredGrade(String declaredGrade) {
            this.declaredGrade = declaredGrade;
        }

        public String getLiftingType() {
            return liftingType;
        }

        public void setLiftingType(String liftingType) {
            this.liftingType = liftingType;
        }

        public String getTotalVehicles() {
            return totalVehicles;
        }

        public void setTotalVehicles(String totalVehicles) {
            this.totalVehicles = totalVehicles;
        }

        public String getTotalVehiclesSampled() {
            return totalVehiclesSampled;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void setTotalVehiclesSampled(String totalVehiclesSampled) {
            this.totalVehiclesSampled = totalVehiclesSampled;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.customerId);
            dest.writeString(this.userName);
            dest.writeString(this.doDetails);
            dest.writeString(this.fsaNumber);
            dest.writeString(this.declaredGrade);
            dest.writeString(this.liftingType);
            dest.writeString(this.totalVehicles);
            dest.writeString(this.totalVehiclesSampled);
            dest.writeString(this.auctionType);
            dest.writeString(this.quantity);

        }

        public CustomersBean() {
        }

        protected CustomersBean(Parcel in) {
            this.customerId = in.readString();
            this.userName = in.readString();
            this.doDetails = in.readString();
            this.fsaNumber = in.readString();
            this.declaredGrade = in.readString();
            this.liftingType = in.readString();
            this.totalVehicles = in.readString();
            this.totalVehiclesSampled = in.readString();
            this.auctionType = in.readString();
            this.quantity = in.readString();
        }

        public static final Creator<CustomersBean> CREATOR = new Creator<CustomersBean>() {
            @Override
            public CustomersBean createFromParcel(Parcel source) {
                return new CustomersBean(source);
            }

            @Override
            public CustomersBean[] newArray(int size) {
                return new CustomersBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sampleId);
        dest.writeString(this.totalVehicles);
        dest.writeString(this.typeOfSampling);
        dest.writeString(this.collectionImage1);
        dest.writeString(this.collectionImage2);
        dest.writeString(this.collectionImage3);
        dest.writeString(this.createdAt);
        dest.writeString(this.updatedAt);
        dest.writeList(this.customers);
        dest.writeString(this.qci);
        dest.writeString(this.mode);

    }

    public UnpreparedSampleModel() {
    }

    protected UnpreparedSampleModel(Parcel in) {
        this.sampleId = in.readString();
        this.totalVehicles = in.readString();
        this.typeOfSampling = in.readString();
        this.collectionImage1 = in.readString();
        this.collectionImage2 = in.readString();
        this.collectionImage3 = in.readString();
        this.createdAt = in.readString();
        this.updatedAt = in.readString();
        this.customers = new ArrayList<CustomersBean>();
        this.qci = in.readString();
        this.mode = in.readString();
        in.readList(this.customers, CustomersBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<UnpreparedSampleModel> CREATOR = new Parcelable.Creator<UnpreparedSampleModel>() {
        @Override
        public UnpreparedSampleModel createFromParcel(Parcel source) {
            return new UnpreparedSampleModel(source);
        }

        @Override
        public UnpreparedSampleModel[] newArray(int size) {
            return new UnpreparedSampleModel[size];
        }
    };
}
