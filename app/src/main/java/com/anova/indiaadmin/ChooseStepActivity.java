package com.anova.indiaadmin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.area.Area;
import com.anova.indiaadmin.database.area.SubsidiaryAreaHelper;
import com.anova.indiaadmin.database.customers.CustomerDBHelper;
import com.anova.indiaadmin.database.customers.CustomerEntity;
import com.anova.indiaadmin.database.location.AreaLocationHelper;
import com.anova.indiaadmin.database.location.Location;
import com.anova.indiaadmin.database.subsidiary.Subsidiary;
import com.anova.indiaadmin.database.subsidiary.SubsidiaryHelper;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseStepActivity extends AppCompatActivity implements AppNetworkResponse, AppDatabaseResponse {

    private static final String TAG = ChooseStepActivity.class.getSimpleName();
    @BindView(R.id.ll_collection_stage)
    LinearLayout llCollectionStage;
    @BindView(R.id.ll_preparation_stage)
    LinearLayout llPreparationStage;
    @BindView(R.id.ll_mark_in_transit)
    LinearLayout llMarkInTransit;

    @BindView(R.id.tv_logout)
    TextView tv_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_step);
        ButterKnife.bind(this);
        getMetaData();
//        exportDb();

    }

    protected void exportDb() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File dataDirectory = Environment.getDataDirectory();

        FileChannel source = null;
        FileChannel destination = null;

        String currentDBPath = "/data/" + getApplicationContext().getApplicationInfo().packageName + "/databases/coal-india-database";
//        String backupDBPath = "SampleDB.sqlite";
        String backupDBPath  = "/QCI/SampleDB.sqlite";
        File currentDB = new File(dataDirectory, currentDBPath);
        File backupDB = new File(externalStorageDirectory, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());

            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (source != null) source.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (destination != null) destination.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}
    private void getMetaData() {
        String session;
        if (PreferenceHelper.contains("session")) {
            session = (PreferenceHelper.getString("session", "NA"));
            Logger.d("session " + session);

            Volley volley = Volley.getInstance();
            volley.getSession(Constants.apiGetSubsidiaryData, this, session, Constants.REQ_GET_SUBSIDIARY_AREA_LOCATION);

            Volley volley2 = Volley.getInstance();
            volley2.getSession(Constants.apiGetCustomersData,this, session, Constants.REQ_ALL_CUSTOMERS);
        } else {
            Logger.d("session NA");
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    @OnClick({R.id.ll_collection_stage, R.id.ll_preparation_stage, R.id.ll_mark_in_transit,R.id.tv_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_collection_stage:
                startActivity(new Intent(this, SavedCollectionsListActivity.class));
                break;
            case R.id.ll_preparation_stage:
                startActivity(new Intent(this, PrepFilterActivity.class));
                break;
            case R.id.ll_mark_in_transit:
                startActivity(new Intent(this, TransitFilterActivity.class));
                break;
            case R.id.tv_logout:
                logoutUser();
        }
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        Logger.i("onResSuccess" + " : " + reqCode);
        switch (reqCode) {
            case Constants.REQ_GET_SUBSIDIARY_AREA_LOCATION: {
                new SubsidiaryHelper.ManageSubsidiaries(AppDatabase.getAppDatabase(this),
                        this,
                        Constants.DELETE_ALL_SUBSIDIARIES,
                        null,
                        AppDatabase.DELETE_ALL_ACTION).execute();

                new SubsidiaryAreaHelper.ManageSubsidiaryArea(AppDatabase.getAppDatabase(this),
                        this,
                        Constants.DELETE_ALL_AREAS,
                        null,
                        -1,
                        AppDatabase.DELETE_ALL_ACTION).execute();

                new AreaLocationHelper.ManageAreaLocation(AppDatabase.getAppDatabase(this),
                        this,
                        Constants.DELETE_ALL_LOCATIONS,
                        null,
                        -1,
                        AppDatabase.DELETE_ALL_ACTION).execute();

                List<Subsidiary> subsidiaryList = new ArrayList<>();
                List<Area> areaList = new ArrayList<>();
                List<Location> locationList = new ArrayList<>();

                Logger.json(jsonObject.toString());
                try {
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject subsidiaryJsonObject = dataArray.getJSONObject(i);
                        subsidiaryList.add(new Subsidiary(Integer.parseInt(subsidiaryJsonObject.getString("id")), subsidiaryJsonObject.getString("name")));
                        JSONArray areaJSONArray = subsidiaryJsonObject.getJSONArray("area");
                        for (int j = 0; j < areaJSONArray.length(); j++) {
                            JSONObject areaJSONObject = areaJSONArray.getJSONObject(j);
                            areaList.add(new Area(Integer.parseInt(areaJSONObject.getString("id")), Integer.parseInt(subsidiaryJsonObject.getString("id")), areaJSONObject.getString("name")));
                            JSONArray locationJSONArray = areaJSONObject.getJSONArray("location");
                            for (int k = 0; k < locationJSONArray.length(); k++) {
                                JSONObject locationJSONObject = locationJSONArray.getJSONObject(k);
                                locationList.add(new Location(Integer.parseInt(locationJSONObject.getString("id")), Integer.parseInt(areaJSONObject.getString("id")), locationJSONObject.getString("name"),locationJSONObject.getString("location_code"),locationJSONObject.getString("declared_grade")));
                            }
                        }
                    }
                    SubsidiaryHelper.ManageSubsidiaries insertSubsidiary = new SubsidiaryHelper.ManageSubsidiaries(AppDatabase.getAppDatabase(this), this, Constants.INSERT_SUBSIDIARIES, subsidiaryList, AppDatabase.INSERT_ACTION);
                    insertSubsidiary.execute();
                    SubsidiaryAreaHelper.ManageSubsidiaryArea insertSubsidiaryArea = new SubsidiaryAreaHelper.ManageSubsidiaryArea(AppDatabase.getAppDatabase(this), this, Constants.INSERT_AREAS, areaList, -1, AppDatabase.INSERT_ACTION);
                    insertSubsidiaryArea.execute();
                    AreaLocationHelper.ManageAreaLocation insertAreaLocation = new AreaLocationHelper.ManageAreaLocation(AppDatabase.getAppDatabase(this), this, Constants.INSERT_AREAS, locationList, -1, AppDatabase.INSERT_ACTION);
                    insertAreaLocation.execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                break;
            }
            case Constants.REQ_ALL_CUSTOMERS:{
                if(jsonObject != null) {
                    CustomerDBHelper customerDBHelper = new CustomerDBHelper(AppDatabase.getAppDatabase(this), this);
                    customerDBHelper.deleteAllCustomers(Constants.DELETE_ALL_CUSTOMERS);
                    List<CustomerEntity> customerEntityList = new ArrayList<>();
                    ArrayList<String> auctionlist = new ArrayList<>();
                    try {
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < dataArray.length(); i++){
                            JSONObject customerJsonObject = dataArray.getJSONObject(i);
                            CustomerEntity pojo = new CustomerEntity();
                            pojo.setUserId(customerJsonObject.getString("user_id"));
                            pojo.setUserName(customerJsonObject.getString("user_name"));
                            pojo.setFsaNo(customerJsonObject.getString("fsa_no"));
                            pojo.setSubsidiary(customerJsonObject.getString("subsidiary"));
                            pojo.setArea(customerJsonObject.getString("area"));
                            pojo.setLocation(customerJsonObject.getString("location"));
                            pojo.setMode(customerJsonObject.getString("mode"));
                            JSONArray jsonArray = new JSONArray(customerJsonObject.getString("auction_type"));
                              auctionlist.clear();
                            for (int j= 0; j< jsonArray.length();j++){
                                auctionlist.add(jsonArray.get(j).toString());
                                pojo.setAuctionType(auctionlist.get(j));
                            }

                            customerEntityList.add(pojo);

                       /*     Gson gson = new Gson();
                            String json = gson.toJson(auctionlist);*/

                          /*  customerEntityList.add(new CustomerEntity(
                                    customerJsonObject.getString("user_id"),
                                    customerJsonObject.getString("user_name"),
                                    customerJsonObject.getString("fsa_no"),
                                    customerJsonObject.getString("subsidiary"),
                                    customerJsonObject.getString("area"),
                                    customerJsonObject.getString("location"),
                                    customerJsonObject.getString("mode"),
                                    customerJsonObject.getString("auction_type")
                            ));*/

                        }

                        customerDBHelper.insertAllCustomers(customerEntityList, Constants.INSERT_CUSTOMERS_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
            case Constants.REQ_GET_USER_LOGOUT:
                PreferenceHelper.clearSharedPreferences();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        Logger.e(reqCode + " : " + errMsg);
    }

    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        if (jsonObject != null) {
            Logger.json(jsonObject.toString());
        }
    }

    private void logoutUser() {
        Toast.makeText(this,"Logout User",Toast.LENGTH_SHORT).show();
        Volley volley= Volley.getInstance();
        volley.get(Constants.apiUserLogout,this,Constants.REQ_GET_USER_LOGOUT);
    }
}
