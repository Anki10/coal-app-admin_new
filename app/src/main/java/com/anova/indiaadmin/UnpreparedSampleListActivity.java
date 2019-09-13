package com.anova.indiaadmin;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anova.indiaadmin.adapters.UnpreparedSampleAdapter;
import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.preparations.PreparationDbHelper;
import com.anova.indiaadmin.database.preparations.PreparationEntity;
import com.anova.indiaadmin.database.samples.SampleEntity;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UnpreparedSampleListActivity extends AppCompatActivity implements AppNetworkResponse, AppDatabaseResponse {

    @BindView(R.id.backLayout)
    RelativeLayout backLayout;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.steptext)
    TextView steptext;
    @BindView(R.id.logoutLayout)
    RelativeLayout logoutLayout;
    @BindView(R.id.rvSamplelist)
    RecyclerView rvSamplelist;
    private UnpreparedSampleAdapter unpreparedSampleAdapter;
    private LinearLayoutManager layoutManager;
    private String subsidiary, area, location,location_code;
    private List<PreparationEntity> preparationEntityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unprepared_sample_list);
        ButterKnife.bind(this);

        titleText.setText("Preparation Stage");
        Bundle bundle = getIntent().getExtras();
        subsidiary = bundle.getString("subsidiary");
        area = bundle.getString("area");
        location = bundle.getString("location");
        location_code = bundle.getString("location_code");

        rvSamplelist.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvSamplelist.setLayoutManager(layoutManager);

        List<SampleEntity> sampleEntityList = new ArrayList<>();

        PreparationDbHelper preparationDbHelper = new PreparationDbHelper(AppDatabase.getAppDatabase(this), this, subsidiary, area, location);
        preparationDbHelper.getFilteredPrepSample(Constants.SELECT_UNPREPARED_SAMPLES);

//        if(bundle.getString("response")==null || bundle.getString("response").length() == 0){
//            Volley volley = Volley.getInstance();
//            Uri.Builder builder;
//            builder = Uri.parse(Constants.apiGetUnpreparedSampledList).buildUpon();
//            Uri uri = builder.appendQueryParameter("subsidiary_name", subsidiary)
//                    .appendQueryParameter("area_name", area)
//                    .appendQueryParameter("location_name", location)
//                    .build();
//            String url = uri.toString();
//            String session;
//            if (PreferenceHelper.contains("session")) {
//                session = (PreferenceHelper.getString("session", "NA"));
//                volley.getSession(url, this, session, Constants.REQ_GET_UNPREPARED_SAMPLES);
//            }
//        } else {
//            try {
//                JSONObject responseJson = new JSONObject(bundle.getString("response"));
//                Gson gson = new Gson();
//                Type listType = new TypeToken<List<UnpreparedSampleModel>>() {
//                }.getType();
//                List<UnpreparedSampleModel> unpreparedSampleModelList = gson.fromJson(responseJson.getJSONArray("data").toString(), listType);
//                String subAreaLoc = subsidiary + "/" + area + "/" + location;
//                unpreparedSampleAdapter = new UnpreparedSampleAdapter(this, unpreparedSampleModelList, subsidiary, area, location, true);
//                rvSamplelist.setAdapter(unpreparedSampleAdapter);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @OnClick({R.id.backLayout, R.id.logoutLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backLayout:
                onBackPressed();
                break;
            case R.id.logoutLayout:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        switch(reqCode){
            case Constants.REQ_GET_UNPREPARED_SAMPLES: {
//                try {
//                    Gson gson = new Gson();
//                    Type listType = new TypeToken<List<UnpreparedSampleModel>>() {}.getType();
//                    List<UnpreparedSampleModel> unpreparedSampleModelList = null;
//                    unpreparedSampleModelList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
//                    String subAreaLoc = subsidiary + "/" + area + "/" + location;
//                    unpreparedSampleAdapter = new UnpreparedSampleAdapter(this, unpreparedSampleModelList,subsidiary, area, location,true);
//                    rvSamplelist.setAdapter(unpreparedSampleAdapter);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                break;
            }
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {

    }

    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode){
            case Constants.SELECT_UNPREPARED_SAMPLES:{
                Gson gson = new Gson();
                Type listType = new TypeToken<List<PreparationEntity>>() {
                }.getType();
                try {
                    preparationEntityList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                    unpreparedSampleAdapter = new UnpreparedSampleAdapter(this, preparationEntityList, subsidiary, area, location, true);
                    rvSamplelist.setAdapter(unpreparedSampleAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
