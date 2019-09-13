package com.anova.indiaadmin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.area.Area;
import com.anova.indiaadmin.database.area.SubsidiaryAreaHelper;
import com.anova.indiaadmin.database.location.AreaLocationHelper;
import com.anova.indiaadmin.database.location.Location;
import com.anova.indiaadmin.database.preparations.PreparationDbHelper;
import com.anova.indiaadmin.database.preparations.PreparationEntity;
import com.anova.indiaadmin.database.subsidiary.Subsidiary;
import com.anova.indiaadmin.database.subsidiary.SubsidiaryHelper;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.NetworkHelper;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PrepFilterActivity extends AppCompatActivity implements AppDatabaseResponse, AdapterView.OnItemSelectedListener, AppNetworkResponse {

    @BindView(R.id.backLayout)
    RelativeLayout backLayout;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.steptext)
    TextView steptext;
    @BindView(R.id.logoutLayout)
    RelativeLayout logoutLayout;
    @BindView(R.id.subsidiaryName)
    Spinner subsidiaryName;
    @BindView(R.id.areaName)
    Spinner areaName;
    @BindView(R.id.locationName)
    Spinner locationName;
    @BindView(R.id.submit)
    Button btnNext;

    private Context mContext;

    private String location_code;

    List<Subsidiary> subsidiaryList;
    List<Area> areaList;
    List<Location> locationsList;

    List<String> subsidiaryNameList = new ArrayList<String>();
    List<String> areaNameList = new ArrayList<String>();
    List<String> locationNameList = new ArrayList<String>();
    List<String> locationCodeList = new ArrayList<String>();

    boolean subsidiaryCheck = false, areaCheck = false;

    private ArrayAdapter<String> subsidiaryAdapter;
    private ArrayAdapter<String> areaNameAdapter;
    private ArrayAdapter<String> locationNameAdapter;
    private Bundle bundle;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prep_filter);
        ButterKnife.bind(this);
        titleText.setText("Preparation Stage");
        this.mContext = this;
    }

    @OnClick({R.id.backLayout, R.id.logoutLayout, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backLayout:
                onBackPressed();
                break;
            case R.id.logoutLayout:
                logoutUser();
                break;
            case R.id.submit:
                if (NetworkHelper.checkConnection(this)) {
                    Volley volley = Volley.getInstance();
                    Uri.Builder builder;
                    builder = Uri.parse(Constants.apiGetUnpreparedSampledList).buildUpon();
                    Uri uri = builder.appendQueryParameter("subsidiary_name", subsidiaryName.getSelectedItem().toString())
                            .appendQueryParameter("area_name", areaName.getSelectedItem().toString())
                            .appendQueryParameter("location_name", locationName.getSelectedItem().toString())
                            .build();
                    String url = uri.toString();
                    Logger.d(url);
                    String session;
                    if (PreferenceHelper.contains("session")) {
                        session = (PreferenceHelper.getString("session", "NA"));
                        progressDialog = new ProgressDialog(this);
                        progressDialog.setMessage("Loading mark Samples...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        volley.getSession(url, this, session, Constants.REQ_GET_UNPREPARED_SAMPLES);
                    }
                } else {
                    PreparationDbHelper preparationDbHelper = new PreparationDbHelper(AppDatabase.getAppDatabase(this), this, subsidiaryName.getSelectedItem().toString(), areaName.getSelectedItem().toString(), locationName.getSelectedItem().toString());
                    preparationDbHelper.getFilteredPrepSample(Constants.SELECT_UNPREPARED_SAMPLES);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setAdapters();
    }

    public void setAdapters() {
        subsidiaryNameList.clear();
        subsidiaryNameList.add("Name of Subsidiary");
        SubsidiaryHelper.ManageSubsidiaries selectAllSubsidiaries = new SubsidiaryHelper.ManageSubsidiaries(AppDatabase.getAppDatabase(this), this, Constants.SELECT_ALL_SUBSIDIARIES, null, AppDatabase.SELECT_ACTION);
        selectAllSubsidiaries.execute();

        subsidiaryAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, subsidiaryNameList);
        subsidiaryAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        subsidiaryName.setAdapter(subsidiaryAdapter);
        subsidiaryName.setOnItemSelectedListener(this);

        areaNameList.clear();
        areaNameList.add("Name of Area");
        areaNameAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, areaNameList);
        areaNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        areaName.setAdapter(areaNameAdapter);
        areaName.setOnItemSelectedListener(this);

        locationNameList.clear();
        locationNameList.add("Name of Location");
        locationNameAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, locationNameList);
        locationNameAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        locationName.setAdapter(locationNameAdapter);
        locationName.setOnItemSelectedListener(this);

    }

    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode) {
            case Constants.SELECT_ALL_SUBSIDIARIES: {
                try {
                    if (jsonObject != null) {
                        Logger.json(jsonObject.toString());
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Subsidiary>>() {
                        }.getType();
                        subsidiaryList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        for (Subsidiary subsidiary : subsidiaryList) {
                            subsidiaryNameList.add(subsidiary.getName());
                        }
                        subsidiaryCheck = true;
                        subsidiaryAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case Constants.SELECT_AREA_BY_SUBSIDIARY: {
                try {
                    if (jsonObject != null) {
                        Logger.json(jsonObject.toString());
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Area>>() {
                        }.getType();
                        areaList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        for (Area area : areaList) {
                            areaNameList.add(area.getName());
                        }
                        areaNameAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case Constants.SELECT_LOCATIONS_BY_AREA: {
                try {
                    if (jsonObject != null) {
                        Logger.json(jsonObject.toString());
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<Location>>() {
                        }.getType();
                        locationsList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        locationCodeList.clear();
                        locationCodeList.add("code");
                        for (Location location : locationsList) {
                            locationNameList.add(location.getName());
                            locationCodeList.add(location.getLocation_code());
                        }
                        locationNameAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case Constants.SYNC_SERVER_LOCAL_PREPARATION_SAMPLES: {
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                bundle = new Bundle();
                bundle.putString("subsidiary", subsidiaryName.getSelectedItem().toString());
                bundle.putString("area", areaName.getSelectedItem().toString());
                bundle.putString("location", locationName.getSelectedItem().toString());
                bundle.putString("location_code", location_code);
                Intent intent = new Intent(this,UnpreparedSampleListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            }
            case Constants.SELECT_UNPREPARED_SAMPLES:{
                Gson gson = new Gson();
                Type listType = new TypeToken<List<PreparationEntity>>() {
                }.getType();
                try {
                    List<PreparationEntity> preparationEntityList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                    if(preparationEntityList.size()>0){
                        bundle = new Bundle();
                        bundle.putString("subsidiary", subsidiaryName.getSelectedItem().toString());
                        bundle.putString("area", areaName.getSelectedItem().toString());
                        bundle.putString("location", locationName.getSelectedItem().toString());
                        bundle.putString("location_code", location_code);
                        Intent intent = new Intent(this,UnpreparedSampleListActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "No saved data found, Please connect to internet!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        Log.e("ResSuccess", jsonObject + " : " + reqCode);
        switch (reqCode) {
            case Constants.REQ_GET_USER_LOGOUT:
                PreferenceHelper.clearSharedPreferences();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

            case Constants.REQ_GET_UNPREPARED_SAMPLES: {
                if (jsonObject.has("data")) {
                    PreparationDbHelper preparationDbHelper = new PreparationDbHelper(AppDatabase.getAppDatabase(this), this,
                            subsidiaryName.getSelectedItem().toString(),
                            areaName.getSelectedItem().toString(),
                            locationName.getSelectedItem().toString());
                    try {
                        preparationDbHelper.syncReceivedPrepSamples(jsonObject.getJSONArray("data"), Constants.SYNC_SERVER_LOCAL_PREPARATION_SAMPLES);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        switch (reqCode) {
            case Constants.REQ_GET_UNPREPARED_SAMPLES: {
                Toast.makeText(this, "No Sample Found", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        switch (parent.getId()) {
            case R.id.subsidiaryName: {
                if (!item.equals("Name of Subsidiary")){
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));
                }
                if (subsidiaryList == null || position <= 0) {
                    break;
                }
                int selectedSubsidiaryId = subsidiaryList.get(position - 1).getId();
                if (subsidiaryCheck) {
                    if (!item.equals("Name of Subsidiary")) {
                        getAreaList(selectedSubsidiaryId);
                        areaCheck = true;
                    }
                } else {
                    subsidiaryCheck = true;
                }
                break;
            }
            case R.id.areaName: {
                if (!item.equals("Name of Area")){
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));
                }
                if (areaList == null || position <= 0) {
                    break;
                }
                int selectedAreaId = areaList.get(position - 1).getId();
                if (areaCheck) {
                    if (!item.equals("Name of Area")) {
                        getLocationList(selectedAreaId);
                    }
                } else {
                    areaCheck = true;
                }
                break;
            }
            case R.id.locationName:
                if (!item.equals("Name of Location")){
                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.colorDarkGrey));

                    String location_name = locationNameList.get(position);

                    location_code = locationCodeList.get(position);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getAreaList(int subsidiaryId) {
        areaNameList.clear();
        areaNameList.add("Name of Area");
        areaName.setSelection(0);
        locationNameList.clear();
        locationNameList.add("Name of Location");
        locationName.setSelection(0);

        SubsidiaryAreaHelper.ManageSubsidiaryArea getSubsidiaryAreas = new SubsidiaryAreaHelper.ManageSubsidiaryArea(AppDatabase.getAppDatabase(this), this, Constants.SELECT_AREA_BY_SUBSIDIARY, null, subsidiaryId, SubsidiaryAreaHelper.SELECT_BY_SUBSIDIARY_ACTION);
        getSubsidiaryAreas.execute();
    }

    private void getLocationList(int areaId) {
        locationNameList.clear();
        locationNameList.add("Name of Location");
        locationName.setSelection(0);
        AreaLocationHelper.ManageAreaLocation getAreaLocations = new AreaLocationHelper.ManageAreaLocation(AppDatabase.getAppDatabase(this), this, Constants.SELECT_LOCATIONS_BY_AREA, null, areaId, AreaLocationHelper.SELECT_BY_AREA_ACTION);
        getAreaLocations.execute();
    }

    private void logoutUser() {
        Toast.makeText(this, "Logout User", Toast.LENGTH_SHORT).show();
        Volley volley = Volley.getInstance();
        volley.get(Constants.apiUserLogout, this, Constants.REQ_GET_USER_LOGOUT);
    }

}
