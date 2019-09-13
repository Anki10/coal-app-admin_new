package com.anova.indiaadmin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.utils.Constants;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanQRActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener,AppNetworkResponse {

    private static QRCodeReaderView qrCodeReaderView;

    private String qr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        ButterKnife.bind(this);

        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(1000);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Log.e("yo", text);
        // qrCodeReaderView.stopCamera();

        qr_code = text;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", qr_code);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

     /*   JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("qr_string", text);
            Volley volley = Volley.getInstance();
            volley.postSession(Constants.apiCheckScanned,jsonObject,ScanQRActivity.this, getFromPrefs(Constants.USER_SESSION),Constants.REQ_POST_QR_CODE_CHECK);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @OnClick(R.id.backLayout)
    public void onViewClicked() {
        onBackPressed();
    }

    public String getFromPrefs(String key) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode){
            case Constants.REQ_POST_QR_CODE_CHECK:{
                try {
                    if (jsonObject.getBoolean("success")){
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("result", qr_code);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        System.out.println("xxx failed");

        Toast.makeText(ScanQRActivity.this,errMsg,Toast.LENGTH_SHORT).show();

        finish();
    }


}
