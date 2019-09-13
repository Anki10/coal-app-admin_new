package com.anova.indiaadmin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anova.indiaadmin.adapters.UnpreparedSampleCustomerAdapter;
import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.preparations.PreparationDbHelper;
import com.anova.indiaadmin.database.preparations.PreparationEntity;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UnpreparedStage1Activity extends AppCompatActivity implements AppDatabaseResponse {

    @BindView(R.id.backLayout)
    RelativeLayout backLayout;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.steptext)
    TextView steptext;
    @BindView(R.id.logoutLayout)
    RelativeLayout logoutLayout;
    @BindView(R.id.topBar)
    RelativeLayout topBar;
    @BindView(R.id.challan_number)
    TextView challan_number;
    @BindView(R.id.typeSampling)
    TextView typeSampling;
    @BindView(R.id.rvCustomerList)
    RecyclerView rvCustomerList;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.mode)
    TextView mode;
    private LinearLayoutManager layoutManager;
    private Bundle bundle;
    UnpreparedSampleModel currentUnpreparedSample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unprepared_stage1);
        ButterKnife.bind(this);

        titleText.setText("Preparation Stage");
        steptext.setText("1/3");

        bundle = getIntent().getExtras();
        int currentPrepID = PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_PREPARATION_ID);
        PreparationDbHelper preparationDbHelper = new PreparationDbHelper(AppDatabase.getAppDatabase(this), this, bundle.getString("subsidiary"), bundle.getString("area"), bundle.getString("location"));
        preparationDbHelper.getPrepSampleByLocalPrepId(Constants.SELECT_UNPREPARED_SAMPLE_BY_PREP_ID, currentPrepID);

    }

    @OnClick({R.id.backLayout, R.id.logoutLayout, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backLayout:
                onBackPressed();
                break;
            case R.id.logoutLayout:
                break;
            case R.id.submit:
                Intent intent = new Intent(this, UnpreparedStage2Activity.class);
                intent.putExtras(bundle);
                startActivity(intent);
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
        switch (reqCode){
            case Constants.SELECT_UNPREPARED_SAMPLE_BY_PREP_ID:{
                if(jsonObject!=null){
                    Gson gson = new Gson();
                    Type prepType = new TypeToken<PreparationEntity>(){}.getType();
                    Type unpreparedSampleType = new TypeToken<UnpreparedSampleModel>(){}.getType();
                    PreparationEntity currentPrepEntity = gson.fromJson(jsonObject.toString(),prepType);
                    currentUnpreparedSample = gson.fromJson(currentPrepEntity.getMyJsonData(), unpreparedSampleType);
                    if(currentUnpreparedSample.getCustomers() != null){
                        List<UnpreparedSampleModel.CustomersBean> customersList = currentUnpreparedSample.getCustomers();

                    if(currentUnpreparedSample.getQci()!=null && currentUnpreparedSample.getQci().length()>0) {
                        challan_number.setText(currentUnpreparedSample.getQci());
                    }
                    if(currentUnpreparedSample.getTypeOfSampling()!=null && currentUnpreparedSample.getTypeOfSampling().length()>0) {
                        typeSampling.setText(currentUnpreparedSample.getTypeOfSampling());
                    }
                    if (currentUnpreparedSample.getMode()!= null && currentUnpreparedSample.getMode().length() > 0){
                        mode.setText(currentUnpreparedSample.getMode());
                    }

                    rvCustomerList.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(this);
                    layoutManager.setMeasurementCacheEnabled(false);
                    rvCustomerList.setLayoutManager(layoutManager);

                    UnpreparedSampleCustomerAdapter customerAdapter = new UnpreparedSampleCustomerAdapter(customersList, this);
                    rvCustomerList.setAdapter(customerAdapter);

                    }
                }
                break;
            }
        }
    }
}
