package com.anova.indiaadmin.utils;

/**
 * Created by owner on 2/15/2018.
 */

public class Constants {
    public static final boolean IS_APP_PROD = false;
    private static final String baseDomainProd = "https://qrcoal-api.qcin.org";
   // private static final String baseDomainBeta = "http://api.coal.editsoftnerds.com";

    private static final String baseDomainBeta = "https://qrcoaltest-api.qcin.org";


    // http://qrcoal-api.qcin.org//api/v1/admin/sampling/

    public static final String baseDomain = IS_APP_PROD ? baseDomainProd : baseDomainBeta;
    public static final String apiDomain = baseDomain + "/api";
    public static final String apiLoginUser=apiDomain + "/account/login";
    public static final String apiCheckLogin=apiDomain + "/account/check_login/1";
    public static final String apiUserLogout = apiDomain + "/account/logout";
    public static final String apiSampling = apiDomain + "/admin/sampling"; // sampling
    public static final String apiSamplingImageUpload = apiDomain + "/admin/image_upload";
    public static final String apiSamplingImages = apiDomain + "/admin/sample_collection_image";
    public static final String apiSamplingCustomer = apiDomain + "/admin/sampling_customer"; // sampling
    public static final String apiSamplingCollection = apiDomain + "/admin/sampling_collection";
    public static final String apiSamplingPreparation = apiDomain + "/admin/sampling_preparation";
    public static final String apiDependeableData= apiDomain + "/admin/dependable_data/";
    public static final String apiGetSubsidiaryData= apiDomain + "/admin/get_subsidiary_data/";
    public static final String apiGetCustomersData= apiDomain + "/admin/get_all_customers/";
    public static final String apiGetUnpreparedSampledList = apiDomain + "/admin/all_preparation_stage_data/";
    public static final String apiSavePreparationStageChallan = apiDomain + "/admin/preparation_stage_save/";
    public static final String apiSavePreparationStageQR = apiDomain + "/admin/sampling_preparation/";
    public static final String apiGetMarInTransitList = apiDomain + "/admin/mark_in_transit_data/";
    public static final String apiPostMarkInTransit = apiDomain + "/admin/mark_in_transit_save/";

    public static final String apiCheckScanned = apiDomain + "/admin/check_scanned_qrcode/";

    public static final String basePathImage = baseDomain + "/upload/attachments/";


    public static final String TIME_STAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DOB_FORMAT = "yyyy-MM-dd";

    /*
    * Shared Preference Constants
    * */
    public static final String SHARED_PREFERENCE_NAME = "CoalAppAdmin";
    public static final String SP_KEY_SESSION = "session";
    public static final String SP_KEY_CURRENT_COLLECTION_ID = "current_collection_id";
    public static final String SP_KEY_CURRENT_PREPARATION_ID = "current_preparation_id";



    public static final int splashTimeOut=5000;

    //Network request codes
    public static final int REQ_GET_UPDATE_FLAG = 101;
    public static final int REQ_POST_LOGIN_USER = 102;
    public static final int REQ_POST_CHECK_LOGIN = 103;
    public static final int REQ_GET_USER_LOGOUT = 106;
    public static final int REQ_POST_SAMPLING = 107;
    public static final int REQ_POST_SAMPLING_CUSTOMER = 108;
    public static final int REQ_POST_SAMPLING_COllECTION = 109;
    public static final int REQ_POST_SAMPLING_PREPARATION = 110;
    public static final int REQ_CUSTOMER_QR = 111;
    public static final int REQ_SUBSIDIARY_QR = 112;
    public static final int REQ_QCI_QR = 113;
    public static final int REQ_REFEREE_QR = 114;
    public static final int REQ_GET_DEPENDEABLE_DATA_AREA = 115;
    public static final int REQ_GET_DEPENDEABLE_DATA_LOCATION = 116;
    public static final int REQ_GET_SUBSIDIARY_AREA_LOCATION = 117;
    public static final int REQ_ALL_CUSTOMERS = 118;
    public static final int REQ_POST_SAMPLING_IMAGE_UPLOAD = 119;
    public static final int REQ_POST_SAMPLING_IMAGES_LIST = 120;
    public static final int REQ_GET_UNPREPARED_SAMPLES = 121;
    public static final int REQ_POST_SAVE_PREPARATION_CHALLAN = 122;
    public static final int REQ_POST_SAVE_PREPARATION_QR = 123;
    public static final int REQ_GET_MARK_IN_TRANSIT_LIST = 124;
    public static final int REQ_POST_MARK_IN_TRANSIT = 125;
    public static final int REQ_POST_SAMPLING_IMAGE_UPLOAD_ASYNC = 126;
    public static final int REQ_POST_QR_CODE_CHECK = 127;


    //Database request codes
    public static final int INSERT_SUBSIDIARIES = 101;
    public static final int SELECT_ALL_SUBSIDIARIES = 102;
    public static final int DELETE_ALL_SUBSIDIARIES = 103;
    public static final int DELETE_SUBSIDIARIES = 104;
    public static final int INSERT_AREAS = 105;
    public static final int DELETE_ALL_AREAS = 106;
    public static final int SELECT_AREA_BY_SUBSIDIARY = 107;
    public static final int INSERT_LOCATIONS = 108;
    public static final int DELETE_ALL_LOCATIONS = 109;
    public static final int SELECT_LOCATIONS_BY_AREA = 110;
    public static final int INSERT_SAMPLE = 111;
    public static final int UPDATE_SAMPLE = 112;
    public static final int INSERT_SAMPLE_LIST = 113;
    public static final int SELECT_SAMPLE_BY_ID = 114;
    public static final int SELECT_ALL_SAMPLES = 115;
    public static final int INSERT_CUSTOMERS_LIST = 116;
    public static final int DELETE_ALL_CUSTOMERS = 117;
    public static final int SELECT_ALL_CUSTOMERS = 118;
    public static final int INSERT_SAMPLE_CUSTOMER = 119;
    public static final int INSERT_SAMPLE_CUSTOMER_LIST = 120;
    public static final int SELECT_SAMPLE_CUSTOMER_BY_ID = 121;
    public static final int DELETE_ALL_SAMPLE_CUSTOMERS_SAMPLE_ID = 122;
    public static final int INSERT_SAMPLE_IMAGE = 123;
    public static final int UPDATE_SAMPLE_IMAGE = 124;
    public static final int INSERT_SAMPLE_IMAGE_LIST = 125;
    public static final int SELECT_ALL_SAMPLE_IMAGES_BY_SAMPLE_ID = 126;
    public static final int DELETE_ALL_SAMPLE_IMAGES_SAMPLE_ID = 127;
    public static final int DELETE_ALL_SAVED_SAMPLE_DATA = 128;
    public static final int SYNC_SERVER_LOCAL_PREPARATION_SAMPLES = 129;
    public static final int SELECT_UNPREPARED_SAMPLES = 130;
    public static final int SELECT_UNPREPARED_SAMPLE_BY_PREP_ID = 130;
    public static final int SELECT_CUSTOMER = 131;

    public final static String PREF_NAME = "com.Coal.prefs";

    public final static String SubsidiaryName = "subsidiaryname";
    public final static String Area_Location = "location";
    public final static String Location_code = "location_code";
    public final static String declared_grade = "declared_grade";
    public final static String date = "date";

    public final static String LOCAlSAVE = "Localsave";

    public final static String customerQr = "CustomerQR";
    public final static String qciQr = "QCIQR";
    public final static String subsidiary_qr = "SUBsidary_qr";
    public final static String referee_qr = "Refree_qr";
    public final static String image4 = "Image4";
    public final static String image5 = "Image5";
    public final static String preparation_date = "preparationdate";

    public final static String USER_SESSION = "UserSession";

    public final static String LocalPath1 = "localpath1";
    public final static String LocalPath2 = "localpath2";
    public final static String localPath3 = "localpath3";



    /*
    * Local Database Constants
    * */
    public static final String DATABASE_NAME = "coal-india-database";

}
