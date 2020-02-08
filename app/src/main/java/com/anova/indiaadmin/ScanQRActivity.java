package com.anova.indiaadmin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anova.indiaadmin.R;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.utils.Constants;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, AppNetworkResponse {

    private ZXingScannerView mScannerView;

    private String qr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);
        ButterKnife.bind(this);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void handleResult(Result result) {
        qr_code = result.getText();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", qr_code);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
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
