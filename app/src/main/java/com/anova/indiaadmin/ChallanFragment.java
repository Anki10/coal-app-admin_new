package com.anova.indiaadmin;


import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChallanFragment extends Fragment implements LocationListener, AppNetworkResponse {

    double lat, lon;
    LocationManager mlocManager;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    Location location; // location
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    // Declaring a Location Manager
    protected LocationManager locationManager;
    Unbinder unbinder;
    String customerQRText = "", labQRText = "", qciQRText = "", refereeQRText = "";
    @BindView(R.id.customerQRImage)
    ImageView customerQRImage;

    String sampleID = "";
    @BindView(R.id.challanNumber)
    EditText challanNumber;
    @BindView(R.id.qciNumber)
    TextView qciNumber;
    Bundle bundle;
    @BindView(R.id.customerQRStatus)
    ImageView customerQRStatus;
    @BindView(R.id.labQRStatus)
    ImageView labQRStatus;
    @BindView(R.id.qciQRStatus)
    ImageView qciQRStatus;
    @BindView(R.id.refereeQRStatus)
    ImageView refereeQRStatus;

    public ChallanFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challan, container, false);
        unbinder = ButterKnife.bind(this, view);

        bundle = getArguments();
        getLocation();
        Log.e("sample_data", "1");
        if (bundle != null) {
            sampleID = bundle.getString("sampleId");

            if (sampleID != null && !sampleID.isEmpty()) {
                Log.e("challan_sampleID", sampleID);
            }
        }

        challanNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                qciNumber.setText("QCI/COAL/SUBSIDIARY/LOC/" + charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation() {

        mlocManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Log.e("loc", "1");
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        try {
            Log.e("loc", "2");
            locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

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
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.customerQR, R.id.labQR, R.id.qciQR, R.id.refereeQR, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.customerQR:
                Intent i = new Intent(getActivity(), ScanQRActivity.class);
                startActivityForResult(i, Constants.REQ_CUSTOMER_QR);
                break;
            case R.id.labQR:
                Intent j = new Intent(getActivity(), ScanQRActivity.class);
                startActivityForResult(j, Constants.REQ_SUBSIDIARY_QR);
                break;
            case R.id.qciQR:
                Intent k = new Intent(getActivity(), ScanQRActivity.class);
                startActivityForResult(k, Constants.REQ_QCI_QR);
                break;
            case R.id.refereeQR:
                Intent l = new Intent(getActivity(), ScanQRActivity.class);
                startActivityForResult(l, Constants.REQ_REFEREE_QR);
                break;
            case R.id.next:
                if (validateCheck()) {
                    next();
                }
                break;
        }
    }

    private boolean validateCheck() {

        if (challanNumber.getText().length() == 0) {
            challanNumber.setError("Enter Challan Number");
        } else if (customerQRText.length() == 0) {
            Toast.makeText(getActivity(), "Scan Customer QR Code", Toast.LENGTH_SHORT).show();
        } else if (labQRText.length() == 0) {
            Toast.makeText(getActivity(), "Scan Lab QR Code", Toast.LENGTH_SHORT).show();
        } else if (qciQRText.length() == 0) {
            Toast.makeText(getActivity(), "Scan QCI QR Code", Toast.LENGTH_SHORT).show();
        } else if (refereeQRText.length() == 0) {
            Toast.makeText(getActivity(), "Scan Referee QR Code", Toast.LENGTH_SHORT).show();

        } else {
            return true;
        }
        return false;
    }

    private void next() {
            /*"sample_id":"1",
            "":"ASD123GHB12222",
            "":"6738383838",
            "":"www.google.com",
            "":"www.facebook.com",
            "":"www.instagram.com",
            "":"www.twitter.com",
            "":"28.583932",
            "longitude":"77.323109"*/

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sample_id", sampleID);
            jsonObject.put("challan_number", challanNumber.getText().toString());
            jsonObject.put("qci", qciNumber.getText().toString());
            jsonObject.put("customer_qr", customerQRText);
            jsonObject.put("lab_qr", labQRText);
            jsonObject.put("qci_qr", qciQRText);
            jsonObject.put("referee_qr", refereeQRText);
            jsonObject.put("lattitude", lat);
            jsonObject.put("longitude", lon);

            Log.e("json", jsonObject.toString());
            Volley volley = Volley.getInstance();
            volley.postSession(Constants.apiSamplingPreparation, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_PREPARATION);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if(data.getStringExtra("result") != null && isQRAlreadyScanned(data.getStringExtra("result"))){
                Toast.makeText(getActivity(), "QR is already Scanned", Toast.LENGTH_SHORT).show();
            }
            else if (requestCode == Constants.REQ_CUSTOMER_QR) {
                if (resultCode == Activity.RESULT_OK) {
                    customerQRText = data.getStringExtra("result");
                    Log.e("customer qr", customerQRText);
                    customerQRStatus.setImageResource(R.drawable.tick);
                }
                if (resultCode == Activity.RESULT_CANCELED) {

                }
            } else if (requestCode == Constants.REQ_SUBSIDIARY_QR) {
                if (resultCode == Activity.RESULT_OK) {
                    labQRText = data.getStringExtra("result");
                    Log.e("lab qr", labQRText);
                    labQRStatus.setImageResource(R.drawable.tick);
                }
                if (resultCode == Activity.RESULT_CANCELED) {

                }
            } else if (requestCode == Constants.REQ_QCI_QR) {
                if (resultCode == Activity.RESULT_OK) {
                    qciQRText = data.getStringExtra("result");
                    Log.e("qci qr", qciQRText);
                    qciQRStatus.setImageResource(R.drawable.tick);
                }
                if (resultCode == Activity.RESULT_CANCELED) {

                }
            } else if (requestCode == Constants.REQ_REFEREE_QR) {
                if (resultCode == Activity.RESULT_OK) {
                    refereeQRText = data.getStringExtra("result");
                    Log.e("referee qr", refereeQRText);
                    refereeQRStatus.setImageResource(R.drawable.tick);
                }
                if (resultCode == Activity.RESULT_CANCELED) {

                }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isQRAlreadyScanned(String qrText){
        if(qrText.equals(customerQRText) || qrText.equals(labQRText) || qrText.equals(qciQRText) || qrText.equals(refereeQRText)){
            return true;
        }
        return false;
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        Log.e("ResSuccess", jsonObject + " : " + reqCode);
        switch (reqCode) {
            case Constants.REQ_POST_SAMPLING_PREPARATION:
                startActivity(new Intent(getActivity(), SuccessActivity.class));
                getActivity().finish();
                break;
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        Log.e("ResFailure", reqCode + " : " + errMsg);
        switch (reqCode) {
            case Constants.REQ_POST_SAMPLING_PREPARATION:
                if(isJSONValid(errMsg))
                {
                    try {
                         JSONObject jsonObject1= new JSONObject(errMsg);
                        if(jsonObject1.has("errors"))
                        {
                            JSONObject jsonObject2= jsonObject1.getJSONObject("errors");
                            if(jsonObject2.has("customer_qr"))
                            {
                                customerQRText="";
                                customerQRStatus.setImageResource(R.drawable.add);
                                Toast.makeText(getActivity(),jsonObject2.getString("customer_qr"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("lab_qr"))
                            {
                                labQRText="";
                                labQRStatus.setImageResource(R.drawable.add);
                                Toast.makeText(getActivity(),jsonObject2.getString("lab_qr"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("qci_qr"))
                            {
                                qciQRText="";
                                qciQRStatus.setImageResource(R.drawable.add);
                                Toast.makeText(getActivity(),jsonObject2.getString("qci_qr"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("referee_qr"))
                            {
                                refereeQRText="";
                                refereeQRStatus.setImageResource(R.drawable.add);
                                Toast.makeText(getActivity(),jsonObject2.getString("referee_qr"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("sample_id"))
                            {
                                Toast.makeText(getActivity(),jsonObject2.getString("sample_id"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("challan_number"))
                            {
                                Toast.makeText(getActivity(),jsonObject2.getString("challan_number"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("lattitude"))
                            {
                                Toast.makeText(getActivity(),jsonObject2.getString("lattitude"),Toast.LENGTH_SHORT).show();
                            }
                            if(jsonObject2.has("longitude"))
                            {
                                Toast.makeText(getActivity(),jsonObject2.getString("longitude"),Toast.LENGTH_SHORT).show();
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        Toast.makeText(getActivity(), errMsg, Toast.LENGTH_SHORT).show();
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public String getFromPrefs(String key) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREF_NAME, getActivity().MODE_PRIVATE);
        return prefs.getString(key, "");
    }

}
