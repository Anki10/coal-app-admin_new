package com.anova.indiaadmin;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.sampleimages.SampleImageDBHelper;
import com.anova.indiaadmin.database.sampleimages.SampleImageEntity;
import com.anova.indiaadmin.database.samples.SampleDBHelper;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.ImageGeoCoding;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;


public class CollectionCaptureImages extends Fragment implements LocationListener, AppDatabaseResponse, AppNetworkResponse {


    private static final int REQUEST_PICTURE_CAPTURE_1 = 1;
    private static final int REQUEST_PICTURE_CAPTURE_2 = 2;
    private static final int REQUEST_PICTURE_CAPTURE_3 = 3;
    private static final int REQUEST_PICTURE_EDIT_1 = 4;
    private static final int REQUEST_PICTURE_EDIT_2 = 5;
    private static final int REQUEST_PICTURE_EDIT_3 = 6;
    private static final int UPDATE_CHALLAN_DETAILS = 7;
    Unbinder unbinder;
    Bundle bundle;
    String sampleID = "";
    String session;
    final String TAG = "CustomerFrag";

    private static final String CAMERA_DIR = "/dcim/";

    @BindView(R.id.challanNumber)
    EditText challanNumber;
    @BindView(R.id.qciNumber)
    TextView qciNumber;
    @BindView(R.id.ivImageCollection1)
    ImageView ivImageCollection1;
    @BindView(R.id.ivImageCollection1Status)
    ImageView ivImageCollection1Status;
    @BindView(R.id.llImageCollection1)
    LinearLayout llImageCollection1;
    @BindView(R.id.ivImageCollection2)
    ImageView ivImageCollection2;
    @BindView(R.id.ivImageCollection2Status)
    ImageView ivImageCollection2Status;
    @BindView(R.id.llImageCollection2)
    LinearLayout llImageCollection2;
    @BindView(R.id.ivImageCollection3)
    ImageView ivImageCollection3;
    @BindView(R.id.ivImageCollection3Status)
    ImageView ivImageCollection3Status;
    @BindView(R.id.llImageCollection3)
    LinearLayout llImageCollection3;
    @BindView(R.id.submit)
    Button submit;

     @BindView(R.id.tv_location)
     Button tv_location;

    HomeActivity mCallback;

    private String pictureFilePath1;
    private String pictureFilePath2;
    private String pictureFilePath3;

    private Uri picUri;
    private File imageF;

    private List<SampleImageEntity> sampleImageEntityList;

    LocationManager mlocManager;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    Location location; // location
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    double lat, lon;
    String subsidiary, area, areaLocation,locationCode;

    private CollectionCaptureImages collectionCaptureImages;

    String challan_number,latitude,longitude,qci_number;

    private String click = "no";

    private String image_click = "Yes";


    public CollectionCaptureImages() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collection_capture_images, container, false);
        unbinder = ButterKnife.bind(this, view);

        collectionCaptureImages = this;

        bundle = getArguments();
        Log.e("sample_data", "1");
        if (bundle != null) {
            sampleID = bundle.getString("sampleId");

            if (sampleID != null && !sampleID.isEmpty()) {
                Log.e("challan_sampleID", sampleID);
            }
        }

        getChallanDataFromDB(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));

        try {
            subsidiary = getFromPrefs(Constants.SubsidiaryName);
  //          area = mCallback.area;
            locationCode = getFromPrefs(Constants.Location_code);

            qciNumber.setText("QCI/COAL/" +subsidiary + "/"+locationCode+"/");

        }catch (Exception e){
            e.printStackTrace();
        }



        challanNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                qciNumber.setText("QCI/COAL/" +subsidiary + "/"+locationCode+"/" + charSequence);

                qci_number = "QCI/COAL/" +subsidiary + "/"+locationCode+"/" + charSequence;

                challan_number = String.valueOf(charSequence);

                image_click = "No";
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }



    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach context");
        try {
            mCallback = (HomeActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach activity");
        try {
            mCallback = (HomeActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "onDeatach");
        mCallback = null;
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkSession();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick({R.id.llImageCollection1, R.id.llImageCollection2, R.id.llImageCollection3, R.id.submit,R.id.tv_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llImageCollection1:
                if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                    image_click = "yes";

                    sendTakePictureIntent(REQUEST_PICTURE_CAPTURE_1);


                }
                break;

            case R.id.llImageCollection2:
                if (pictureFilePath1 != null){
                    if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                        image_click = "yes";

                        sendTakePictureIntent(REQUEST_PICTURE_CAPTURE_2);


                    }
                }else {
                    Toast.makeText(getActivity(),"Please Capture first image before capture second image ",Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.llImageCollection3:
                if (pictureFilePath1 != null && pictureFilePath2 != null){
                    if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {

                        image_click = "yes";

                        sendTakePictureIntent(REQUEST_PICTURE_CAPTURE_3);
                    }
                }else {
                    Toast.makeText(getActivity(),"Please Capture first and second image before capture third image ",Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.submit:
                if(validationCheck()){
                    save();
                }
                break;
            case R.id.tv_location:
               getLocation("click");
        }
    }



    private void save() {
        Toast.makeText(getActivity(), "Submit button clicked", Toast.LENGTH_SHORT).show();

        SampleImageDBHelper sampleImageDBHelper = new SampleImageDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        sampleImageDBHelper.deleteSampleImageByLocalSampleId(Integer.parseInt(sampleID), Constants.DELETE_ALL_SAMPLE_IMAGES_SAMPLE_ID);
    }

    private void insert(){
        SampleImageEntity sampleImageEntity1 = new SampleImageEntity();
        sampleImageEntity1.setLocalSampleId(Integer.parseInt(sampleID));
        sampleImageEntity1.setLocalFilePath(pictureFilePath1);

        SampleImageEntity sampleImageEntity2 = new SampleImageEntity();
        sampleImageEntity2.setLocalSampleId(Integer.parseInt(sampleID));
        sampleImageEntity2.setLocalFilePath(pictureFilePath2);

        SampleImageEntity sampleImageEntity3 = new SampleImageEntity();
        sampleImageEntity3.setLocalSampleId(Integer.parseInt(sampleID));
        sampleImageEntity3.setLocalFilePath(pictureFilePath3);

        List<SampleImageEntity> sampleImageEntityList = new ArrayList<>();
        sampleImageEntityList.add(sampleImageEntity1);
        sampleImageEntityList.add(sampleImageEntity2);
        sampleImageEntityList.add(sampleImageEntity3);

        SampleImageDBHelper sampleImageDBHelper = new SampleImageDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        sampleImageDBHelper.insertAll(sampleImageEntityList,Constants.INSERT_SAMPLE_IMAGE_LIST);
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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", imageF));
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
        File image = File.createTempFile(pictureFile, ".jpg", myDir);
        if (reqCode == REQUEST_PICTURE_CAPTURE_1) {
            if(pictureFilePath1 != null && pictureFilePath1.length()>0) {
                File file = new File(pictureFilePath1);
                if (file.exists()) file.delete();
            }
            pictureFilePath1 = image.getAbsolutePath();
        } else if (reqCode == REQUEST_PICTURE_CAPTURE_2) {
            if(pictureFilePath2 != null && pictureFilePath2.length()>0) {
                File file = new File(pictureFilePath2);
                if (file.exists()) file.delete();
            }
            pictureFilePath2 = image.getAbsolutePath();
        } else {
            if(pictureFilePath3 != null && pictureFilePath3.length()>0) {
                File file = new File(pictureFilePath3);
                if (file.exists()) file.delete();
            }
            pictureFilePath3 = image.getAbsolutePath();
        }

        return image;
    }


    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode){
            case Constants.SELECT_ALL_SAMPLE_IMAGES_BY_SAMPLE_ID:{
                try {
                    if (jsonObject != null && jsonObject.length()>0) {
                        Logger.json(jsonObject.toString());
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<SampleImageEntity>>() {
                        }.getType();
                        sampleImageEntityList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);
                        if(sampleImageEntityList.size()>0) {
                            pictureFilePath1 = sampleImageEntityList.get(0).getLocalFilePath();
                            Picasso.with(getActivity()).load(new File(pictureFilePath1)).into(ivImageCollection1);
                            ivImageCollection1Status.setImageResource(R.drawable.tick);

                            pictureFilePath2 = sampleImageEntityList.get(1).getLocalFilePath();
                            Picasso.with(getActivity()).load(new File(pictureFilePath2)).into(ivImageCollection2);
                            ivImageCollection2Status.setImageResource(R.drawable.tick);

                            pictureFilePath3 = sampleImageEntityList.get(2).getLocalFilePath();
                            Picasso.with(getActivity()).load(new File(pictureFilePath3)).into(ivImageCollection3);
                            ivImageCollection3Status.setImageResource(R.drawable.tick);
                        }
                        if(mCallback.challanNumber!=null && mCallback.challanNumber.length()>0){
                            challanNumber.setText(mCallback.challanNumber);
                        }
                        if(mCallback.qciNumber!=null && mCallback.qciNumber.length()>0){
                            qciNumber.setText(mCallback.qciNumber);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();

                }
                break;
            }
            case Constants.DELETE_ALL_SAMPLE_IMAGES_SAMPLE_ID:{
                Logger.d(jsonObject);
                insert();
                break;
            }

            case Constants.INSERT_SAMPLE_IMAGE_LIST:{
                Logger.d(jsonObject);
                SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
                sampleDBHelper.updateChallanDetails(UPDATE_CHALLAN_DETAILS, Integer.parseInt(sampleID), challan_number, qci_number,String.valueOf(lat),String.valueOf(lon));
                break;
            }

            case UPDATE_CHALLAN_DETAILS:{

                if (!click.equalsIgnoreCase("stop")){
                    getActivity().finish();
                }

                break;
            }

            case Constants.UPDATE_SAMPLE_IMAGE:{
                getSampleDataFromDB(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));


                break;
            }
            case Constants.SELECT_SAMPLE_BY_ID: {
                if (jsonObject != null) {
                    Logger.d(jsonObject);

                    try {
                        challan_number = jsonObject.getString("challanNumber");

                        challanNumber.setText(challan_number);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        latitude = jsonObject.getString("lattitude");
                        longitude = jsonObject.getString("longitude");

                        lat = Double.parseDouble(latitude);
                        lon = Double.parseDouble(longitude);

                        if (!latitude.equalsIgnoreCase("0.0") & !longitude.equalsIgnoreCase("0.0")){
                            tv_location.setText("YOUR LOCATION SAVED");
                        }else {
                            tv_location.setText("SAVE YOUR LOCATION");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
                break;
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        SampleImageEntity sampleImageEntity;
        SampleImageDBHelper sampleImageDBHelper = new SampleImageDBHelper(AppDatabase.getAppDatabase(getActivity()),this);
        if (requestCode == REQUEST_PICTURE_CAPTURE_1 && resultCode == RESULT_OK) {
            Uri Image = picUri;
            pictureFilePath1 = compressImage(Image.toString());
            saveIntoPrefs(Constants.LocalPath1,pictureFilePath1);
            File imgFile = new File(pictureFilePath1);
            image_click = "no";
            if(imgFile.exists()){
                Picasso.with(getActivity()).load(imgFile).into(ivImageCollection1);
                ivImageCollection1Status.setImageResource(R.drawable.tick);
                getLocation("image");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
                String date_string="" + sdf.format(new Date());
                new ImageGeoCoding(pictureFilePath1, date_string, String.valueOf(lat), String.valueOf(lon), getActivity(), collectionCaptureImages, REQUEST_PICTURE_EDIT_1).execute();
                if(sampleImageEntityList != null &&sampleImageEntityList.size()>0) {
                    sampleImageEntity = sampleImageEntityList.get(0);
                    sampleImageEntity.setLocalFilePath(pictureFilePath1);
                    sampleImageEntity.setRemoteFileName("");
                    sampleImageDBHelper.updateSampleImage(sampleImageEntity, Constants.UPDATE_SAMPLE_IMAGE);
                }
            }

        } else if (requestCode == REQUEST_PICTURE_CAPTURE_2 && resultCode == RESULT_OK) {
            Uri Image = picUri;
            pictureFilePath2 = compressImage(Image.toString());
            saveIntoPrefs(Constants.LocalPath2,pictureFilePath2);
            File imgFile = new File(pictureFilePath2);
            image_click = "no";
            if (imgFile.exists()) {
                Picasso.with(getActivity()).load(imgFile).into(ivImageCollection2);
                ivImageCollection2Status.setImageResource(R.drawable.tick);
                getLocation("image");
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
                String date_string="" + sdf.format(new Date());
                new ImageGeoCoding(pictureFilePath2, date_string, String.valueOf(lat), String.valueOf(lon), getActivity(), collectionCaptureImages, REQUEST_PICTURE_EDIT_2).execute();
                if(sampleImageEntityList != null &&sampleImageEntityList.size()>0) {
                    sampleImageEntity = sampleImageEntityList.get(1);
                    sampleImageEntity.setLocalFilePath(pictureFilePath2);
                    sampleImageEntity.setRemoteFileName("");
                    sampleImageDBHelper.updateSampleImage(sampleImageEntity, Constants.UPDATE_SAMPLE_IMAGE);
                }
            }
        } else if (requestCode == REQUEST_PICTURE_CAPTURE_3 && resultCode == RESULT_OK) {
            Uri Image = picUri;
            pictureFilePath3 = compressImage(Image.toString());
            saveIntoPrefs(Constants.localPath3,pictureFilePath3);
            image_click = "no";
            File imgFile = new File(pictureFilePath3);
            if (imgFile.exists()) {
                Picasso.with(getActivity()).load(imgFile).into(ivImageCollection3);
                ivImageCollection3Status.setImageResource(R.drawable.tick);
                getLocation("image");

                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.ENGLISH);
                String date_string="" + sdf.format(new Date());
                new ImageGeoCoding(pictureFilePath3, date_string, String.valueOf(lat), String.valueOf(lon), getActivity(), collectionCaptureImages, REQUEST_PICTURE_EDIT_3).execute();
                if(sampleImageEntityList != null &&sampleImageEntityList.size()>0) {
                    sampleImageEntity = sampleImageEntityList.get(2);
                    sampleImageEntity.setLocalFilePath(pictureFilePath3);
                    sampleImageEntity.setRemoteFileName("");
                    sampleImageDBHelper.updateSampleImage(sampleImageEntity, Constants.UPDATE_SAMPLE_IMAGE);
                }
            }
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
        Cursor cursor = getActivity().getContentResolver().query(contentUri, null, null, null, null);
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


    private boolean validationCheck() {

        if (challanNumber.getText().length() == 0) {
            challanNumber.setError("Enter Challan Number");
        } /*else if (pictureFilePath1 == null || pictureFilePath1.length() <= 0) {
            Toast.makeText(getActivity(), "Please capture first image", Toast.LENGTH_SHORT).show();
        } else if (pictureFilePath2 == null || pictureFilePath2.length() <= 0) {
            Toast.makeText(getActivity(), "Please capture second image", Toast.LENGTH_SHORT).show();
        } else if (pictureFilePath3 == null || pictureFilePath3.length() <= 0) {
            Toast.makeText(getActivity(), "Please capture third image", Toast.LENGTH_SHORT).show();
        }*/
        else {
            return true;
        }

        return false;

    }

    private void checkSession() {
        if(PreferenceHelper.contains("session"))
        {
            session = (PreferenceHelper.getString("session", "NA"));
            Log.i(TAG,"session "+ session);
            if(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID,0) > 0){
                sampleID = String.valueOf(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
                getSampleDataFromDB(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));

   //             getChallanDataFromDB(PreferenceHelper.getInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID));
            }
//            getDataFromSession(session);
        }
        else
        {
            Log.i(TAG,"session NA");
            Toast.makeText(getActivity(),"Please Login",Toast.LENGTH_SHORT).show();
            Intent in = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
            startActivity(in);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getActivity().finish();
        }
    }

    private void getChallanDataFromDB(int localSampleId) {
        SampleDBHelper sampleDBHelper = new SampleDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        sampleDBHelper.getSampleById(localSampleId, Constants.SELECT_SAMPLE_BY_ID);
    }


    private void getSampleDataFromDB(int localSampleId) {
        SampleImageDBHelper sampleImageDBHelper = new SampleImageDBHelper(AppDatabase.getAppDatabase(getActivity()), this);
        sampleImageDBHelper.getSampleImageByLocalSampleId(localSampleId, Constants.SELECT_ALL_SAMPLE_IMAGES_BY_SAMPLE_ID);
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getLocation(String from) {

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

                            if (from.equalsIgnoreCase("click")){
                                tv_location.setText("YOUR LOCATION SAVED");
                            }

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

                                if (from.equalsIgnoreCase("click")){
                                    tv_location.setText("YOUR LOCATION SAVED");
                                }

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
        switch (reqCode){
            case REQUEST_PICTURE_EDIT_1:{
                File imgFile = new File(pictureFilePath1);
                if(imgFile.exists()){
                    Picasso.with(getActivity()).load(imgFile).into(ivImageCollection1);
                    ivImageCollection1Status.setImageResource(R.drawable.tick);

                }
                break;
            }
            case REQUEST_PICTURE_EDIT_2:{
                File imgFile = new File(pictureFilePath2);
                if(imgFile.exists()){
                    Picasso.with(getActivity()).load(imgFile).into(ivImageCollection2);
                    ivImageCollection2Status.setImageResource(R.drawable.tick);
                }
                break;
            }
            case REQUEST_PICTURE_EDIT_3:{
                File imgFile = new File(pictureFilePath3);
                if(imgFile.exists()){
                    Picasso.with(getActivity()).load(imgFile).into(ivImageCollection3);
                    ivImageCollection3Status.setImageResource(R.drawable.tick);
                }
                break;
            }

        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {

    }


    @Override
    public void onStop() {
        super.onStop();

        if (!image_click.equalsIgnoreCase("yes")){
            click = "stop";

            save();
        }

    }

    public void saveIntoPrefs(String key, String value) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREF_NAME, getActivity().MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getFromPrefs(String key) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.PREF_NAME, getActivity().MODE_PRIVATE);
        return prefs.getString(key, "");
    }
}
