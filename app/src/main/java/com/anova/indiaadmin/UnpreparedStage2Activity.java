package com.anova.indiaadmin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.FormatConversionHelper;
import com.anova.indiaadmin.utils.ImageGeoCoding;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UnpreparedStage2Activity extends AppCompatActivity implements LocationListener, AppNetworkResponse, AppDatabaseResponse {

    private static final int REQUEST_PICTURE_CAPTURE_4 = 4;
    private static final int REQUEST_PICTURE_CAPTURE_5 = 5;
    private static final int REQUEST_PICTURE_EDIT_4 = 44;
    private static final int REQUEST_PICTURE_EDIT_5 = 55;
    private static final int GET_PREPARATION_IMAGE_QR = 1001;
    private static final int EMPTY_REQUEST_PREPARATION_IMAGE_QR = 1002;

    private static final String CAMERA_DIR = "/dcim/";

    @BindView(R.id.backLayout)
    RelativeLayout backLayout;
    @BindView(R.id.titleText)
    TextView titleText;
    @BindView(R.id.steptext)
    TextView steptext;
    @BindView(R.id.logoutLayout)
    RelativeLayout logoutLayout;
//    @BindView(R.id.challanNumber)
//    EditText challanNumber;
//    @BindView(R.id.qciNumber)
//    TextView qciNumber;
    @BindView(R.id.ivImageCollection1)
    ImageView ivImageCollection1;
    @BindView(R.id.ivImageCollection2)
    ImageView ivImageCollection2;
    @BindView(R.id.ivImageCollection3)
    ImageView ivImageCollection3;
    @BindView(R.id.ivImageCollection4)
    ImageView ivImageCollection4;
    @BindView(R.id.ivImageCollection4Status)
    ImageView ivImageCollection4Status;
    @BindView(R.id.llImageCollection4)
    LinearLayout llImageCollection4;
    @BindView(R.id.ivImageCollection5)
    ImageView ivImageCollection5;
    @BindView(R.id.ivImageCollection5Status)
    ImageView ivImageCollection5Status;
    @BindView(R.id.llImageCollection5)
    LinearLayout llImageCollection5;
    @BindView(R.id.next)
    Button next;
    @BindView(R.id.tv_preparationDate)
    TextView tv_preparationDate;
    private Bundle bundle;

    private String pictureFilePath4;
    private String pictureFilePath5;
    private String subsidiary, area, location;

    LocationManager mlocManager;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    Location loc; // location
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    double lat, lon;

    UnpreparedSampleModel currentUnpreparedSample;
    private int currentPrepID;
    private PreparationEntity currentPrepEntity;
    PreparationImageQrEntity preparationImageQrEntity;
    private PreparationDbHelper preparationDbHelper;

    private Uri picUri;
    private File imageF;

    int day = 0, month = 0, year = 0;
    Calendar myCalendar = Calendar.getInstance();

    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unprepared_stage2);
        ButterKnife.bind(this);

        bundle = getIntent().getExtras();
        subsidiary = bundle.getString("subsidiary");
        area = bundle.getString("area");
        location = bundle.getString("location");

        titleText.setText("Preparation Stage");
        steptext.setText("2/3");



    }

    @Override
    protected void onResume() {
        super.onResume();

        currentPrepID = PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_PREPARATION_ID);
        preparationDbHelper = new PreparationDbHelper(AppDatabase.getAppDatabase(this), this, bundle.getString("subsidiary"), bundle.getString("area"), bundle.getString("location"));
        preparationDbHelper.getPrepSampleByLocalPrepId(Constants.SELECT_UNPREPARED_SAMPLE_BY_PREP_ID, currentPrepID);

        if (getFromPrefs(Constants.preparation_date+currentPrepID).length() > 0){
            selectedDate = getFromPrefs(Constants.preparation_date+currentPrepID);

            tv_preparationDate.setText(selectedDate);
        }
    }

    @OnClick({R.id.backLayout, R.id.logoutLayout, R.id.llImageCollection4, R.id.llImageCollection5, R.id.next, R.id.tv_preparationDate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backLayout:
                onBackPressed();
                break;
            case R.id.logoutLayout:
                break;
            case R.id.llImageCollection4:
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    sendTakePictureIntent(REQUEST_PICTURE_CAPTURE_4);
                }
                break;
            case R.id.llImageCollection5:
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    sendTakePictureIntent(REQUEST_PICTURE_CAPTURE_5);
                }
                break;
            case R.id.next:
                if(validateCheck()){
                    preparationDbHelper.markModified(currentPrepEntity.getModifiedCount()+1, currentPrepID, EMPTY_REQUEST_PREPARATION_IMAGE_QR);
                    PreparationImageQrHelper preparationImageQrHelper = new PreparationImageQrHelper(AppDatabase.getAppDatabase(this), this, currentPrepID);
                    if(preparationImageQrEntity != null){
                        preparationImageQrHelper.updateImages(EMPTY_REQUEST_PREPARATION_IMAGE_QR, pictureFilePath4, pictureFilePath5);

                        saveIntoPrefs(Constants.image4+currentPrepID,pictureFilePath4);
                        saveIntoPrefs(Constants.image5+currentPrepID,pictureFilePath5);
                        saveIntoPrefs(Constants.preparation_date+currentPrepID,selectedDate);
                    } else {
                        PreparationImageQrEntity newPrepImageQrEntity = new PreparationImageQrEntity(currentPrepID);
                        newPrepImageQrEntity.setImage4(pictureFilePath4);
                        newPrepImageQrEntity.setImage5(pictureFilePath5);;
                        preparationImageQrHelper.insertAction(EMPTY_REQUEST_PREPARATION_IMAGE_QR, newPrepImageQrEntity);


                        saveIntoPrefs(Constants.image4+currentPrepID,newPrepImageQrEntity.getImage4());
                        saveIntoPrefs(Constants.image5+currentPrepID,newPrepImageQrEntity.getImage5());
                        saveIntoPrefs(Constants.preparation_date+currentPrepID,selectedDate);
                    }
                    Intent intent = new Intent(this, UnpreparedStage3Activity.class);
                    bundle.putString("preparation_image_1", pictureFilePath4);
                    bundle.putString("preparation_image_2", pictureFilePath5);


                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.tv_preparationDate:
                DatePickerDialog datePickerDialog =
                        new DatePickerDialog(UnpreparedStage2Activity.this, R.style.DateDialogTheme, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH));
                Calendar c = Calendar.getInstance();


                c.setTime(new Date());
                datePickerDialog.getDatePicker().setMaxDate(c.getTime().getTime());

                c.add(Calendar.MONTH, -3);

                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
                break;

        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            year = year;
            month = monthOfYear + 1;
            day = dayOfMonth;

            selectedDate = FormatConversionHelper.getFormatedDateTime(day + "-" + month + "-" + year, "dd-MM-yyyy", "dd-MM-yyyy");
            Log.e("date", "selectDate after change " + selectedDate);

            tv_preparationDate.setText(selectedDate);
            tv_preparationDate.setTextColor(getResources().getColor(R.color.colorDarkGrey));
        }

    };

    public void saveIntoPrefs(String key, String value) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getFromPrefs(String key) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        return prefs.getString(key, "");
    }

    private boolean validateCheck() {

        if (pictureFilePath4 != null && pictureFilePath4.length() == 0) {
            Toast.makeText(this, "Please select picture 4", Toast.LENGTH_SHORT).show();
        } else if (pictureFilePath5!= null && pictureFilePath5.length() == 0) {
            Toast.makeText(this, "Please select picture 5", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private void sendTakePictureIntent(int reqCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "bmh" + timeStamp + "_";
            File albumF = getAlbumDir();
            imageF = File.createTempFile(imageFileName, "bmh", albumF);
            picUri = Uri.fromFile(imageF);

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageF));
            } else {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(UnpreparedStage2Activity.this, getPackageName() + ".provider", imageF));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        startActivityForResult(takePictureIntent, reqCode);

    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraPicture");
            } else {
                storageDir = new File(Environment.getExternalStorageDirectory() + CAMERA_DIR + "CameraPicture");
            }

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        //		Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            //		Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    public File getPictureFile(int reqCode) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "QCI_" + timeStamp;
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/QCI");
        myDir.mkdirs();
        File image = File.createTempFile(pictureFile, ".png", myDir);
        if (reqCode == REQUEST_PICTURE_CAPTURE_4) {
            if(pictureFilePath4 != null && pictureFilePath4.length()>0) {
                File file = new File(pictureFilePath4);
                if (file.exists()) file.delete();
            }
            pictureFilePath4 = image.getAbsolutePath();
        } else if (reqCode == REQUEST_PICTURE_CAPTURE_5) {
            if(pictureFilePath5 != null && pictureFilePath5.length()>0) {
                File file = new File(pictureFilePath5);
                if (file.exists()) file.delete();
            }
            pictureFilePath5 = image.getAbsolutePath();
        }

        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_PICTURE_CAPTURE_4 && resultCode == RESULT_OK) {
            Uri Image = picUri;
            pictureFilePath4 = compressImage(Image.toString());
            File imgFile = new File(pictureFilePath4);
            if (imgFile.exists()) {
                Picasso.with(this).load(imgFile).into(ivImageCollection4);
                ivImageCollection4Status.setImageResource(R.drawable.tick);
                getLocation();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
                String date_string="" + sdf.format(new Date());
                new ImageGeoCoding(pictureFilePath4, date_string, String.valueOf(lat), String.valueOf(lon), this, this, REQUEST_PICTURE_EDIT_4).execute();

            }
        } else if (requestCode == REQUEST_PICTURE_CAPTURE_5 && resultCode == RESULT_OK) {
            Uri Image = picUri;
            pictureFilePath5 = compressImage(Image.toString());
            File imgFile = new File(pictureFilePath5);
            if (imgFile.exists()) {
                Picasso.with(this).load(imgFile).into(ivImageCollection5);
                ivImageCollection5Status.setImageResource(R.drawable.tick);
                getLocation();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
                String date_string="" + sdf.format(new Date());
                new ImageGeoCoding(pictureFilePath5, date_string, String.valueOf(lat), String.valueOf(lon), this, this, REQUEST_PICTURE_EDIT_5).execute();
            }
        }
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
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null) {
                            Log.e("activity", "LOC by Network");
                            lat = loc.getLatitude();
                            lon = loc.getLongitude();
                            Log.e("Loc Lat", Double.toString(lat) + " : " + Double.toString(lon));

                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                else {
                    if (loc == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        Log.e("activity", "RLOC: GPS Enabled");

                        if (locationManager != null) {

                            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (loc != null) {
                                Log.e("activity", "RLOC: loc by GPS");

                                lat = loc.getLatitude();
                                lon = loc.getLongitude();
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

    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 320.0f;
        float maxWidth = 320.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Coal/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {

    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {

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

                Picasso.with(this).load(Constants.basePathImage+currentUnpreparedSample.getCollectionImage1()).into(ivImageCollection1);
                Picasso.with(this).load(Constants.basePathImage+currentUnpreparedSample.getCollectionImage2()).into(ivImageCollection2);
                Picasso.with(this).load(Constants.basePathImage+currentUnpreparedSample.getCollectionImage3()).into(ivImageCollection3);

                if(currentPrepEntity.isModified()){
                    PreparationImageQrHelper preparationImageQrHelper = new PreparationImageQrHelper(AppDatabase.getAppDatabase(this), this, currentPrepID);
                    preparationImageQrHelper.getPrepImageQr(GET_PREPARATION_IMAGE_QR,currentPrepID);
                }
                break;
            }
            case GET_PREPARATION_IMAGE_QR:{
                if(jsonObject!=null) {
                    Type prepImageQrType = new TypeToken<PreparationImageQrEntity>() {
                    }.getType();
                    preparationImageQrEntity = new Gson().fromJson(jsonObject.toString(), prepImageQrType);

                    if (preparationImageQrEntity.getImage4() != null){
                        pictureFilePath4 = preparationImageQrEntity.getImage4();
                        Picasso.with(this).load(new File(preparationImageQrEntity.getImage4())).into(ivImageCollection4);
                        ivImageCollection4Status.setImageResource(R.drawable.tick);
                    }

                    if (preparationImageQrEntity.getImage5() != null){
                        pictureFilePath5 = preparationImageQrEntity.getImage5();
                        Picasso.with(UnpreparedStage2Activity.this).load(new File(preparationImageQrEntity.getImage5())).into(ivImageCollection5);
                        ivImageCollection5Status.setImageResource(R.drawable.tick);
                    }

                  /*  if (preparationImageQrEntity.getPreparationDate() != null){
                        tv_preparationDate.setText(preparationImageQrEntity.getPreparationDate());
                    }*/
                }
            }
        }
    }
}
