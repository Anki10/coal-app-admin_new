package com.anova.indiaadmin;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;

import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity implements AppNetworkResponse {

    Thread thread;
    private final String TAG = getClass().getSimpleName();
    String session;
    ConnectivityManager connectivityManager;
    NetworkInfo info;

    String returnJosn;
    boolean editMode = false;
    JSONObject response;

    double lat, lon;
    LocationManager mlocManager;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    boolean gpsCheck = false;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    private Context mContext;
    private TextView tv_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mContext = this;
        getLocation();

        tv_version = (TextView) findViewById(R.id.tv_version);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;

            tv_version.setText("App Version : "+ version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gpsCheck) {
            checkInternet();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void checkInternet() {
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(Constants.splashTimeOut);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
               /* if (checkConnection()) {
                    session = (PreferenceHelper.getString("session", "NA"));
                    checkSession(session);
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "No Internet Connection Found", Toast.LENGTH_SHORT).show();
                        }
                    });
                }*/
                checkAppVersion();
            }
        };
        thread.start();
    }

    public boolean checkConnection() {
        boolean flag = false;
        try {
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            info = connectivityManager.getActiveNetworkInfo();

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.e(TAG, "Network Type " + info.getTypeName());
                flag = true;
            }
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                Log.e(TAG, "Network Type " + info.getTypeName());
                flag = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }
        return flag;
    }


    private void checkAppVersion() {
        if (PreferenceHelper.contains("session")) {
            session = (PreferenceHelper.getString("session", "NA"));
            Log.i(TAG, "session " + session);
            Intent i = new Intent(mContext, ChooseStepActivity.class);
            startActivity(i);
            finish();

        } else {
            Log.i(TAG, "session NA");
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }


    public void checkSession(String session) {
        Volley volley = Volley.getInstance();
        JSONObject jsonObject = null;
        volley.postSession(Constants.apiCheckLogin, jsonObject, this, session, Constants.REQ_POST_CHECK_LOGIN);
    }

    private String getAppVersion(Context context) {
        String versionCode = "";
        return versionCode;
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        Log.e("ResSuccess", jsonObject + " : " + reqCode);

        switch (reqCode) {
            case Constants.REQ_GET_UPDATE_FLAG:
                break;

            //TODO Modify checkSession function to simply check if the current session is valid or not
            case Constants.REQ_POST_CHECK_LOGIN:
                ((App) getApplication()).setSessionID(session);
//                try {
//                    response = jsonObject;
//                    editMode = response.getBoolean("editMode");
//                    if (editMode) {
//                        returnJosn = response.getString("return");
//                        Log.e("splash_data", Boolean.toString(editMode) + " : " + returnJosn);
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                Intent i = new Intent(this, ChooseStepActivity.class);
//                Bundle bundle = new Bundle();
//                i.putExtra("editMode", Boolean.toString(editMode));
//                i.putExtra("returnJson", returnJosn);

                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        Log.e("ResFailure", reqCode + " : " + errMsg);
        switch (reqCode) {
            case Constants.REQ_GET_UPDATE_FLAG:
                break;

            case Constants.REQ_POST_CHECK_LOGIN:
//                startActivity(new Intent(this, LoginActivity.class));
//                finish();
                break;
        }
    }

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
                checkInternet();
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
                                gpsCheck = true;
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        /*alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });*/
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
