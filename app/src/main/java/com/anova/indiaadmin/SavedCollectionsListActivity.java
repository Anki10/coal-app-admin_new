package com.anova.indiaadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.adapters.SavedSampleAdapter;
import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.samples.SampleDBHelper;
import com.anova.indiaadmin.database.samples.SampleEntity;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SavedCollectionsListActivity extends AppCompatActivity implements AppDatabaseResponse, AppNetworkResponse {

    @BindView(R.id.imageButton)
    ImageView imageButton;
    @BindView(R.id.backLayout)
    RelativeLayout backLayout;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.steptext)
    TextView steptext;
    @BindView(R.id.logoutButton)
    ImageView logoutButton;
    @BindView(R.id.logoutLayout)
    RelativeLayout logoutLayout;
    @BindView(R.id.topBar)
    RelativeLayout topBar;
    @BindView(R.id.rvSamplelist)
    RecyclerView rvSamplelist;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.ll_NoData)
    LinearLayout ll_NoData;
    public LinearLayoutManager layoutManager;
    public SavedSampleAdapter savedSampleAdapter;
    public List<SampleEntity> sampleEntityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_collections_list);
        ButterKnife.bind(this);

        rvSamplelist.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvSamplelist.setLayoutManager(layoutManager);
        titleText.setText("Saved Collections");

        SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(this), this);
        sampleDBHelper.getAllSamples(Constants.SELECT_ALL_SAMPLES);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(this), this);
        sampleDBHelper.getAllSamples(Constants.SELECT_ALL_SAMPLES);
    }

    @OnClick({R.id.imageButton, R.id.backLayout, R.id.logoutButton, R.id.logoutLayout, R.id.fab})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageButton:
            case R.id.backLayout:
                onBackPressed();
                break;
            case R.id.logoutButton:
            case R.id.logoutLayout:
                logoutUser();
                break;
            case R.id.fab:
                PreferenceHelper.clearValue(Constants.SP_KEY_CURRENT_COLLECTION_ID);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isNew", true);
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                //TODO finish the parent activity if required
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode) {
            case Constants.SELECT_ALL_SAMPLES: {
                try {
                    if (jsonObject != null) {
                        Logger.json(jsonObject.toString());

                           Gson gson = new Gson();
                            Type listType = new TypeToken<List<SampleEntity>>() {
                            }.getType();
                            sampleEntityList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                            if (sampleEntityList.size() > 0){
                                ll_NoData.setVisibility(View.GONE);
                                savedSampleAdapter = new SavedSampleAdapter(this, sampleEntityList, true);
                                rvSamplelist.setAdapter(savedSampleAdapter);
                            }


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
        Log.e("ResSuccess",jsonObject+" : "+reqCode);
        switch (reqCode)
        {
            case Constants.REQ_GET_USER_LOGOUT:
                PreferenceHelper.clearSharedPreferences();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {

    }

    private void logoutUser() {
        Toast.makeText(this,"Logout User",Toast.LENGTH_SHORT).show();
        Volley volley= Volley.getInstance();
        volley.get(Constants.apiUserLogout,this,Constants.REQ_GET_USER_LOGOUT);
    }
}
