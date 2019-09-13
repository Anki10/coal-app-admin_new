package com.anova.indiaadmin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PermissionUtil;
import com.anova.indiaadmin.utils.PreferenceHelper;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements AppNetworkResponse {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;

    String emailID, userType, userSession, data, returnJosn = "";
    boolean editMode = false;
    JSONObject response;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    boolean emailFlag = false;
    Typeface light, regular, bold;

    private static final int REQUEST_PER = 1;
    private static String[] REQUEST_AUDIO_CASHBACK = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    @BindView(R.id.signIn)
    Button signIn;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.subTitleText)
    TextView subTitleText;
    @BindView(R.id.forgetPassword)
    TextView forgetPassword;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        light = Typeface.createFromAsset(getAssets(), "fonts/avenir_nextltpro_cn.otf");
        regular = Typeface.createFromAsset(getAssets(), "fonts/avenir_nextltpro_regular.otf");
        bold = Typeface.createFromAsset(getAssets(), "fonts/avenir_nextltpro_demi.otf");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean requestPermission = PermissionUtil.idreamPermissions(this);
            if (requestPermission == true) {
                ActivityCompat.requestPermissions(LoginActivity.this, REQUEST_AUDIO_CASHBACK,
                        REQUEST_PER);
            } else {
                ActivityCompat.requestPermissions(this, REQUEST_AUDIO_CASHBACK, REQUEST_PER);
            }
        }

       /* email.setText(String.valueOf("gyanesh.pandey04@gmail.com"));
        password.setText(String.valueOf("123456"));*/

      /*  if(BuildConfig.DEBUG){
            email.setText(String.valueOf("awadheshsingh5544@gmail.com"));
            password.setText(String.valueOf("123456"));
        }*/

        titleText.setTypeface(regular);
        subTitleText.setTypeface(regular);
        email.setTypeface(regular);
        password.setTypeface(regular);
        signIn.setTypeface(regular);
        forgetPassword.setTypeface(light);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_PER) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                ActivityCompat.requestPermissions(this, REQUEST_AUDIO_CASHBACK, REQUEST_PER);

                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }


        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @OnClick(R.id.signIn)
    public void onViewClicked() {

        if (email.getText().toString().length() == 0) {
            email.setError("Enter valid email");
        } else if (password.getText().length() == 0) {
            password.setError("Enter Password");
        } else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("email", email.getText().toString());
                jsonObject.put("password", password.getText().toString());

                Volley volley = Volley.getInstance();
                volley.post(Constants.apiLoginUser, jsonObject, this, Constants.REQ_POST_LOGIN_USER);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        Log.e("ResSuccess", jsonObject + " : " + reqCode);

        try {

            response = jsonObject;
            userType = response.getString("type");

            if (userType.equals("1") || userType.equals("7")) {
                userSession = response.getString("session");
                data = response.getString("data");
                editMode = response.getBoolean("editMode");
                if (editMode) {
                    returnJosn = response.getString("return");
                    Log.e("login_data", Boolean.toString(editMode) + " : " + returnJosn);
                }

                if (response.getString("success").equalsIgnoreCase("true")){
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();

                    saveIntoPrefs(Constants.USER_SESSION,userSession);

      //             ((App) getApplication()).setSessionID(userSession);

                    PreferenceHelper.putString(Constants.SP_KEY_SESSION, userSession);
                    Intent i = new Intent(this, ChooseStepActivity.class);
                    Bundle bundle = new Bundle();
                    i.putExtra("editMode", Boolean.toString(editMode));
                    i.putExtra("returnJson", returnJosn);

                    startActivity(i);
                    finish();

                }else {
          //          Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
                    Toast.makeText(this,"Login Failed. Please try again",Toast.LENGTH_LONG).show();
                }

            } else {
                email.setText("");
                password.setText("");
                Toast.makeText(this, "You have to login from Lab", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        Log.e("ResFailure", reqCode + " : " + errMsg);
        try {
            JSONObject object = new JSONObject(errMsg);
            String errors = object.getString("errors");
            JSONObject json = new JSONObject(errors);
            String email = json.getString("email");
            Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveIntoPrefs(String key, String value) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.commit();
    }
}
