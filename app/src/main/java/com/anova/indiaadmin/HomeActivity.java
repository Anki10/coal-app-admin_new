package com.anova.indiaadmin;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements SampleDetailsFragment.OpenCustomerInterface, AppNetworkResponse,CustomerFragment.OpenChallanInterface {

    BottomNavigationView bottomNavigationView;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.steptext)
    TextView steptext;

    String returnJson="";
    String editMode,sampleID="",sampleStatus,liftType="",samplingType;
    String subsidiary, area, location,auction_type;
    String challanNumber, qciNumber;
    Bundle bundle;
    boolean isNew = false;
    boolean deleteExistingCustomers;
    SampleDetailsFragment sampleDetailsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.frame_layout);
                        switch (item.getItemId()) {
                            case R.id.action_sample:
                                if (!(currentFragment instanceof SampleDetailsFragment)) {
                                    loadSampleDetailsFragment();
                                }
                                break;
                            case R.id.action_customer:
                                if (!(currentFragment instanceof CustomerFragment)) {
                                    loadCustomerFragment();
                                }
                                break;
                            case R.id.action_challan:
                                if (!(currentFragment instanceof ChallanFragment)) {
                                    loadChallanFragment();
                                }
                                break;
                        }
                        return true;
                    }
                });

        bundle = getIntent().getExtras();

        if(bundle!=null)
        {
            isNew = bundle.getBoolean("isNew");
            editMode=bundle.getString("editMode");
            if(editMode!=null && editMode.equals("true")) {
                returnJson = bundle.getString("returnJson");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(returnJson);
                    sampleID=jsonObject.getString("sample_id");
                    sampleStatus=jsonObject.getString("sample_status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (sampleStatus) {
                    case "0":
                        openFirstBar();
//                        loadSampleDetailsFragment();
//                        bottomNavigationView.setSelectedItemId(R.id.action_sample);
                        break;

                    case "1":
                        openSecondFrag();
//                        loadCustomerFragment();
//                        bottomNavigationView.setSelectedItemId(R.id.action_customer);
                        break;

                    case "2":
                        openThirdFrag();
                       /* loadChallanFragment();
                        bottomNavigationView.setSelectedItemId(R.id.action_challan);*/
                        break;
                }
            } else
            {
                loadSampleDetailsFragment();
            }
            Log.e("home_data",editMode+" : "+returnJson);
        }
        else
        {
            loadSampleDetailsFragment();
        }

    }

    private void openThirdFrag() {
        loadChallanFragment();
        bottomNavigationView.setSelectedItemId(R.id.action_challan);
    }



    private void openFirstBar() {
        loadSampleDetailsFragment();
        bottomNavigationView.setSelectedItemId(R.id.action_sample);
    }

    private void openSecondFrag() {
       /* loadCustomerFragment();
        bottomNavigationView.setSelectedItemId(R.id.action_customer);*/
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void loadSampleDetailsFragment() {
        titleText.setText("Collection Stage");
        steptext.setText("1/3");
        sampleDetailsFragment = new SampleDetailsFragment();
        if(bundle!=null) {
            Bundle bundle = new Bundle();
            bundle.putString("editMode", editMode);
            bundle.putString("returnJson", returnJson);
            sampleDetailsFragment.setArguments(bundle);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, sampleDetailsFragment);
     //   ft.addToBackStack("Sample_back");
        ft.commit();
    }

    private void loadCustomerFragment() {
        if(sampleID != null && !sampleID.isEmpty()) {
        titleText.setText("Collection Stage");
        steptext.setText("2/3");
        Bundle bundle = new Bundle();
        bundle.putString("editMode",editMode);
        bundle.putString("sampleId",sampleID);
        bundle.putString("liftType", liftType);
        bundle.putString("returnJson",returnJson);
//        CustomerFragment customerFragment = new CustomerFragment();
        CustomerListFragment customerListFragment = new CustomerListFragment();
        customerListFragment.setArguments(bundle);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.frame_layout, customerListFragment);
        ft.commit();
        }
        else
        {
   //         loadSampleDetailsFragment();
            //bottomNavigationView.setSelectedItemId(R.id.action_sample);
            Toast.makeText(HomeActivity.this,"Fill Sample Detail Form",Toast.LENGTH_SHORT).show();
        }
    }

    private void loadChallanFragment() {
        if(sampleID != null && !sampleID.isEmpty()) {
            titleText.setText("Collection Stage");
            Bundle bundle = new Bundle();
            steptext.setText("3/3");
            bundle.putString("sampleId", sampleID);
            CollectionCaptureImages collectionImageFragment = new CollectionCaptureImages();
            collectionImageFragment.setArguments(bundle);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, collectionImageFragment);
            ft.commit();
        }
        else
        {
            loadSampleDetailsFragment();
            //bottomNavigationView.setSelectedItemId(R.id.action_sample);
            Toast.makeText(HomeActivity.this,"Fill Detail Form",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick({R.id.backLayout, R.id.logoutLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backLayout:
                onBackPressed();
                break;
            case R.id.logoutLayout:
                logoutUser();
                break;
        }
    }

    @Override
    public void openCustomerFrag(String i,boolean firstTimeCheck,String liftType, boolean deleteExistingCustomers) {
        Log.e("change_frag_1",i+" : "+Boolean.valueOf(firstTimeCheck)+" : "+liftType);
        sampleID=i;
        this.deleteExistingCustomers = deleteExistingCustomers;
        if(firstTimeCheck)
        {
            Log.e("opencustofrag","1");
            editMode="true";
            this.liftType=liftType;
        }
        else
        {
            Log.e("opencustofrag","2");
            this.liftType=liftType;
            editMode="false";
        }

        //loadCustomerFragment();
        bottomNavigationView.setSelectedItemId(R.id.action_customer);
    }

    @Override
    public void openChallanFrag(String i) {
        Log.e("change_frag_2",i);
        //loadChallanFragment();
        sampleID=i;
        bottomNavigationView.setSelectedItemId(R.id.action_challan);
    }

    private void logoutUser() {
        Toast.makeText(HomeActivity.this,"Logout User",Toast.LENGTH_SHORT).show();
        Volley volley= Volley.getInstance();
        volley.get(Constants.apiUserLogout,this,Constants.REQ_GET_USER_LOGOUT);
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        Log.e("ResSuccess",jsonObject+" : "+reqCode);
        switch (reqCode)
        {
            case Constants.REQ_GET_USER_LOGOUT:
                PreferenceHelper.clearSharedPreferences();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        Log.e("ResFailure",reqCode+" : "+errMsg);
    }

 /*   @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            sampleDetailsFragment.sendSampling();
        }
    }*/
}
