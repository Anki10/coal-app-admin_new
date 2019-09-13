package com.anova.indiaadmin;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.preparationimages.PreparationImageQrEntity;
import com.anova.indiaadmin.database.preparationimages.PreparationImageQrHelper;
import com.anova.indiaadmin.database.preparations.PreparationDbHelper;
import com.anova.indiaadmin.database.preparations.PreparationEntity;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UnpreparedStage3Activity extends AppCompatActivity implements LocationListener, AppDatabaseResponse {

    @BindView(R.id.backLayout)
    RelativeLayout backLayout;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.steptext)
    TextView steptext;
    @BindView(R.id.logoutLayout)
    RelativeLayout logoutLayout;
    @BindView(R.id.customerQRImage)
    ImageView customerQRImage;
    @BindView(R.id.customerQRStatus)
    ImageView customerQRStatus;
    @BindView(R.id.tv_customer)
    TextView tvCustomer;
    @BindView(R.id.customerQR)
    LinearLayout customerQR;
    @BindView(R.id.subsidiaryQRImage)
    ImageView subsidiaryQRImage;
    @BindView(R.id.subsidiaryQRStatus)
    ImageView subsidiaryQRStatus;
    @BindView(R.id.subsidiaryQR)
    LinearLayout subsidiaryQR;
    @BindView(R.id.qciQRImage)
    ImageView qciQRImage;
    @BindView(R.id.qciQRStatus)
    ImageView qciQRStatus;
    @BindView(R.id.qciQR)
    LinearLayout qciQR;
    @BindView(R.id.refereeQRImage)
    ImageView refereeQRImage;
    @BindView(R.id.refereeQRStatus)
    ImageView refereeQRStatus;
    @BindView(R.id.refereeQR)
    LinearLayout refereeQR;
    @BindView(R.id.next)
    Button next;

    private static final int GET_PREPARATION_IMAGE_QR = 1001;
    private static final int EMPTY_REQUEST_PREPARATION_IMAGE_QR = 1002;
    private static final int UPDATE_REQUEST_PREPARATION_IMAGE_QR = 1003;


    private Bundle bundle;
    private String localPreparationImage1, localPreparationImage2, challanNumber, qciNumber;
    private String remotePreparationImage1, remotePreparationImage2;

    double lat, lon;
    LocationManager mlocManager;
    protected LocationManager locationManager;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    Location location; // location
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    String customerQRText = "", subsidiaryQRText = "", qciQRText = "", refereeQRText = "";
    private JSONObject prepInsertJsonObject, qrInsertJsonObject;
    private UnpreparedSampleModel currentUnpreparedSample;

    public static final int REQ_IMAGE_UPLOAD_1 = 201;
    public static final int REQ_IMAGE_UPLOAD_2 = 202;

    private ProgressDialog progressDialog;
    private int currentPrepID;
    private PreparationDbHelper preparationDbHelper;
    private PreparationEntity currentPrepEntity;
    PreparationImageQrEntity preparationImageQrEntity;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unprepared_stage3);
        ButterKnife.bind(this);

        titleText.setText("Preparation Stage");
        steptext.setText("3/3");
        bundle = getIntent().getExtras();
        localPreparationImage1 = bundle.getString("preparation_image_1");
        localPreparationImage2 = bundle.getString("preparation_image_2");
        challanNumber = bundle.getString("challan_number");
        qciNumber = bundle.getString("qci_number");
        progressDialog = new ProgressDialog(this);

        getLocation();
        currentPrepID = PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_PREPARATION_ID);
        preparationDbHelper = new PreparationDbHelper(AppDatabase.getAppDatabase(this), this, bundle.getString("subsidiary"), bundle.getString("area"), bundle.getString("location"));
        preparationDbHelper.getPrepSampleByLocalPrepId(Constants.SELECT_UNPREPARED_SAMPLE_BY_PREP_ID, currentPrepID);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {

        mlocManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Log.e("loc", "1");
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        try {
            Log.e("loc", "2");
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            Log.e("loc", "3");
            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.e("activity", "No");

                showGPSDisabledAlertToUser();
            } else {

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION},
                                1);
                    }

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    Log.e("activity", "LOC Network Enabled");

                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.e("activity", "LOC by Network");
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                            Log.e("Loc Lat", Double.toString(lat) + " : " + Double.toString(lon));

                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                else {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.e("activity", "RLOC: GPS Enabled");

                        if (locationManager != null) {

                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (location != null) {
                                Log.e("activity", "RLOC: loc by GPS");

                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                Log.e("Loc Lat", Double.toString(lat) + " : " + Double.toString(lon));

                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("loc", e.toString());
        }

    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto GPS Setting",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @OnClick({R.id.backLayout, R.id.logoutLayout, R.id.customerQR, R.id.subsidiaryQR, R.id.qciQR, R.id.refereeQR, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backLayout:
                onBackPressed();
                break;
            case R.id.logoutLayout:
                break;
            case R.id.customerQR:
                Intent i = new Intent(this, ScanQRActivity.class);
                startActivityForResult(i, Constants.REQ_CUSTOMER_QR);
                break;
            case R.id.subsidiaryQR:
                Intent j = new Intent(this, ScanQRActivity.class);
                startActivityForResult(j, Constants.REQ_SUBSIDIARY_QR);
                break;
            case R.id.qciQR:
                Intent k = new Intent(this, ScanQRActivity.class);
                startActivityForResult(k, Constants.REQ_QCI_QR);
                break;
            case R.id.refereeQR:
                Intent l = new Intent(this, ScanQRActivity.class);
                startActivityForResult(l, Constants.REQ_REFEREE_QR);
                break;
            case R.id.next:
                if (qciQRText.length() > 0){
                    next();
                }else {
                    Toast.makeText(UnpreparedStage3Activity.this,"Scan QCI QR Code",Toast.LENGTH_LONG).show();
                }

               /* if (validateCheck()) {

                }*/
                break;
        }
    }

    private boolean validateCheck() {

        if (customerQRText.length() == 0) {
            Toast.makeText(this, "Scan Customer QR Code", Toast.LENGTH_SHORT).show();
        } else if (subsidiaryQRText.length() == 0) {
            Toast.makeText(this, "Scan Coal Company QR Code", Toast.LENGTH_SHORT).show();
        } else if (qciQRText.length() == 0) {
            Toast.makeText(this, "Scan QCI QR Code", Toast.LENGTH_SHORT).show();
        } else if (refereeQRText.length() == 0) {
            Toast.makeText(this, "Scan Referee QR Code", Toast.LENGTH_SHORT).show();

        } else {
            return true;
        }
        return false;
    }

    private void next() {
//        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        try {
            prepInsertJsonObject = new JSONObject();
            prepInsertJsonObject.put("sample_id", Integer.parseInt(currentUnpreparedSample.getSampleId()));
            prepInsertJsonObject.put("challan_number", challanNumber);
            prepInsertJsonObject.put("qci_number", qciNumber);

            qrInsertJsonObject = new JSONObject();
            qrInsertJsonObject.put("sample_id", Integer.parseInt(currentUnpreparedSample.getSampleId()));
            qrInsertJsonObject.put("customer_qr", customerQRText);
            qrInsertJsonObject.put("qci_qr", qciQRText);
            qrInsertJsonObject.put("subsidiary_qr", subsidiaryQRText);
            qrInsertJsonObject.put("referee_qr", refereeQRText);
            qrInsertJsonObject.put("lattitude", lat);
            qrInsertJsonObject.put("longitude", lon);

            progressDialog.setMessage("Uploading Information...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            PreparationImageQrHelper preparationImageQrHelper = new PreparationImageQrHelper(AppDatabase.getAppDatabase(this), this, currentPrepID);
            preparationImageQrHelper.updateQrs(UPDATE_REQUEST_PREPARATION_IMAGE_QR, qciQRText, subsidiaryQRText, customerQRText, refereeQRText);

            //This is where the call starts
//            new ImageUploaderAsync(Constants.apiSamplingImageUpload, localPreparationImage1,this,this,REQ_IMAGE_UPLOAD_1).execute();
//            new ImageUploaderAsync(Constants.apiSamplingImageUpload, localPreparationImage2,this,this,REQ_IMAGE_UPLOAD_2).execute();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO replicate this in adapter
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode) {
            case REQ_IMAGE_UPLOAD_1: {
                try {
                    remotePreparationImage1 = jsonObject.getString("data");
                    if (remotePreparationImage2 != null && remotePreparationImage2.length() > 0) {
                        prepInsertJsonObject.put("collection_image_1", remotePreparationImage1);
                        prepInsertJsonObject.put("collection_image_2", remotePreparationImage2);
//                        Volley volley = Volley.getInstance();
//                        volley.postSession(Constants.apiSavePreparationStageChallan, prepInsertJsonObject, this, ((App) getApplicationContext()).getSessionID(), Constants.REQ_POST_SAVE_PREPARATION_CHALLAN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case REQ_IMAGE_UPLOAD_2: {
                try {
                    remotePreparationImage2 = jsonObject.getString("data");
                    if (remotePreparationImage1 != null && remotePreparationImage1.length() > 0) {
                        prepInsertJsonObject.put("collection_image_1", remotePreparationImage1);
                        prepInsertJsonObject.put("collection_image_2", remotePreparationImage2);
//                        Volley volley = Volley.getInstance();
//                        volley.postSession(Constants.apiSavePreparationStageChallan, prepInsertJsonObject, this, ((App) getApplicationContext()).getSessionID(), Constants.REQ_POST_SAVE_PREPARATION_CHALLAN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case Constants.REQ_POST_SAVE_PREPARATION_CHALLAN: {
//                Volley volley = Volley.getInstance();
//                volley.postSession(Constants.apiSavePreparationStageQR, qrInsertJsonObject, this, ((App) getApplicationContext()).getSessionID(), Constants.REQ_POST_SAVE_PREPARATION_QR);
                break;
            }
            case Constants.REQ_POST_SAVE_PREPARATION_QR: {
                progressDialog.dismiss();
                Intent intent = new Intent(this, UnpreparedSampleListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtras(bundle);
                startActivity(intent);
                Toast.makeText(this, "Sample is now available for transit", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //TODO replicate this in adapter
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        progressDialog.dismiss();
        Toast.makeText(this, "API Error - code" + reqCode + " : " + jsonObject.toString(), Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (data.getStringExtra("result") != null && isQRAlreadyScanned(data.getStringExtra("result"))) {
                Toast.makeText(this, "QR is already Scanned", Toast.LENGTH_SHORT).show();
            } else if (requestCode == Constants.REQ_CUSTOMER_QR) {
                if (resultCode == Activity.RESULT_OK) {
                    customerQRText = data.getStringExtra("result");
                    Log.e("customer qr", customerQRText);


                    customerQRStatus.setImageResource(R.drawable.tick);

                    saveIntoPrefs(Constants.customerQr+currentPrepID, customerQRText);
                }
                if (resultCode == Activity.RESULT_CANCELED) {

                }
            } else if (requestCode == Constants.REQ_SUBSIDIARY_QR) {
                if (resultCode == Activity.RESULT_OK) {
                    subsidiaryQRText = data.getStringExtra("result");
                    Log.e("subsidiary qr", subsidiaryQRText);
                    subsidiaryQRStatus.setImageResource(R.drawable.tick);

                    saveIntoPrefs(Constants.subsidiary_qr+currentPrepID,subsidiaryQRText);
                }
                if (resultCode == Activity.RESULT_CANCELED) {

                }
            } else if (requestCode == Constants.REQ_QCI_QR) {
                if (resultCode == Activity.RESULT_OK) {
                    qciQRText = data.getStringExtra("result");
                    Log.e("qci qr", qciQRText);
                    qciQRStatus.setImageResource(R.drawable.tick);

                    saveIntoPrefs(Constants.qciQr+currentPrepID, qciQRText);
                }
                if (resultCode == Activity.RESULT_CANCELED) {

                }
            } else if (requestCode == Constants.REQ_REFEREE_QR) {
                if (resultCode == Activity.RESULT_OK) {
                    refereeQRText = data.getStringExtra("result");
                    Log.e("referee qr", refereeQRText);
                    refereeQRStatus.setImageResource(R.drawable.tick);

                    saveIntoPrefs(Constants.referee_qr+currentPrepID, refereeQRText);
                }
                if (resultCode == Activity.RESULT_CANCELED) {

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isQRAlreadyScanned(String qrText) {
        if (qrText.equals(customerQRText) || qrText.equals(subsidiaryQRText) || qrText.equals(qciQRText) || qrText.equals(refereeQRText)) {
            return true;
        }
        return false;
    }

    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode){
            case Constants.SELECT_UNPREPARED_SAMPLE_BY_PREP_ID:{
                Gson gson = new Gson();
                Type prepType = new TypeToken<PreparationEntity>(){}.getType();
                Type unpreparedSampleType = new TypeToken<UnpreparedSampleModel>(){}.getType();
                currentPrepEntity = gson.fromJson(jsonObject.toString(),prepType);
                currentUnpreparedSample = gson.fromJson(currentPrepEntity.getMyJsonData(), unpreparedSampleType);

                if(currentPrepEntity.isModified()){
                    PreparationImageQrHelper preparationImageQrHelper = new PreparationImageQrHelper(AppDatabase.getAppDatabase(this), this, currentPrepID);
                    preparationImageQrHelper.getPrepImageQr(GET_PREPARATION_IMAGE_QR,currentPrepID);
                    preparationDbHelper.markModified(currentPrepEntity.getModifiedCount()+1, currentPrepID, EMPTY_REQUEST_PREPARATION_IMAGE_QR);
                }
                break;
            }
            case GET_PREPARATION_IMAGE_QR:{
                if(jsonObject!=null) {
                    Type prepImageQrType = new TypeToken<PreparationImageQrEntity>() {
                    }.getType();
                    preparationImageQrEntity = new Gson().fromJson(jsonObject.toString(), prepImageQrType);
                    if(preparationImageQrEntity.getCustomerQr()!=null && preparationImageQrEntity.getCustomerQr().length()>0){
                        customerQRStatus.setImageResource(R.drawable.tick);
                        customerQRText = preparationImageQrEntity.getCustomerQr();
                    }
                    if(preparationImageQrEntity.getSubsidiaryQr()!=null && preparationImageQrEntity.getSubsidiaryQr().length()>0){
                        subsidiaryQRStatus.setImageResource(R.drawable.tick);
                        subsidiaryQRText = preparationImageQrEntity.getSubsidiaryQr();
                    }
                    if(preparationImageQrEntity.getQciQr()!=null && preparationImageQrEntity.getQciQr().length()>0){
                        qciQRStatus.setImageResource(R.drawable.tick);
                        qciQRText = preparationImageQrEntity.getQciQr();
                    }
                    if(preparationImageQrEntity.getRefreeQr()!=null && preparationImageQrEntity.getRefreeQr().length()>0){
                        refereeQRStatus.setImageResource(R.drawable.tick);
                        refereeQRText = preparationImageQrEntity.getRefreeQr();
                    }
                }
                break;
            }
            case UPDATE_REQUEST_PREPARATION_IMAGE_QR:{
                if(progressDialog!=null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                    Intent intent = new Intent(this, UnpreparedSampleListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Something went wrong! Try again", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void saveIntoPrefs(String key, String value) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.commit();
    }

}
