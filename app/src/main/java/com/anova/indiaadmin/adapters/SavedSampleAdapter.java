package com.anova.indiaadmin.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.HomeActivity;
import com.anova.indiaadmin.R;
import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.samplecustomer.MoredetailsEntity;
import com.anova.indiaadmin.database.samplecustomer.SampleCustomerDBHelper;
import com.anova.indiaadmin.database.samplecustomer.SampleCustomerEntity;
import com.anova.indiaadmin.database.sampleimages.SampleImageDBHelper;
import com.anova.indiaadmin.database.sampleimages.SampleImageEntity;
import com.anova.indiaadmin.database.samples.SampleEntity;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.ImageUploaderAsync;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.FormatConversionHelper;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iqbal on 9/5/18.
 */

public class SavedSampleAdapter extends RecyclerView.Adapter<SavedSampleAdapter.SavedSampleViewHolder> {

    private List<SampleEntity> dataList;
    private Context context;
    private SavedSampleAdapter savedSampleAdapter;
    private boolean clickEnabled;

    private Boolean SyncData = true;

    String challen_id = "";

    private String rrdate;

    private String quantity_status = "";

    // ivDeleteItem


    public SavedSampleAdapter(Context context, List<SampleEntity> dataList, boolean clickEnabled) {
        this.dataList = dataList;
        this.context = context;
        this.savedSampleAdapter = this;
        this.clickEnabled = clickEnabled;
    }

    @Override
    public SavedSampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.item_row_saved_sample, parent, false);
        // set the view's size, margins, paddings and layout parameters
        SavedSampleViewHolder vh = new SavedSampleViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final SavedSampleViewHolder holder, final int position) {
        final SampleEntity currentSample = dataList.get(position);

        if (currentSample.getQciNumber() != null){
            holder.tvSampleId.setVisibility(View.VISIBLE);
            holder.tvSampleId.setText("Sample Id : " + currentSample.getQciNumber());
        }else {
            holder.tvSampleId.setVisibility(View.GONE);
        }

        holder.tvData.setText(currentSample.getSubsidiaryName() + "/" + currentSample.getArea() + "/" + currentSample.getLocation());
         if (currentSample.getCollectionDate() != null)
        holder.tvDate.setText("Collection Date : " + FormatConversionHelper.getFormatedDateTime(currentSample.getCollectionDate().toString(), "EEE MMMM d hh:mm:ss z yyyy", "dd-MM-yyyy"));
         else
             holder.tvDate.setText("Collection Date : " + "");


        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickEnabled) {
                    PreferenceHelper.putInteger(Constants.SP_KEY_CURRENT_COLLECTION_ID, currentSample.getLocalSampleId());
                    context.startActivity(new Intent(context, HomeActivity.class));
                    //TODO finish the parent activity if required
                }
            }
        });

        holder.ivDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete sample")
                        .setMessage("Are you sure you want to delete this sample?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DeleteSampleBYID deleteSavedSampleDataByID = new DeleteSampleBYID(
                                        context,
                                        dataList.get(position).getLocalSampleId(),
                                        position
                                );
                                deleteSavedSampleDataByID.execute();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        holder.ivSyncItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickEnabled) {
                    if (!checkConnection()) {
                        Toast.makeText(context, "Please check your Internet Connection!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    final AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(context);
                    builder.setTitle("Quantity Alert")
                            .setMessage("Is the sample quantity final ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (SyncData){

                                        quantity_status = "1";

                                        holder.ivSyncItem.setVisibility(View.GONE);
                                        holder.pbSyncProgressbar.setVisibility(View.VISIBLE);
                                        SyncSampleAsyncTask syncSampleAsyncTask = new SyncSampleAsyncTask(holder, context, savedSampleAdapter, currentSample, position);
                                        syncSampleAsyncTask.execute();

                                        challen_id = currentSample.getQciNumber();

                                        SyncData = false;

                                    }else {
                                        Toast.makeText(context,"Please sync all the record one by one",Toast.LENGTH_LONG).show();

                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (SyncData){

                                        quantity_status = "0";

                                        holder.ivSyncItem.setVisibility(View.GONE);
                                        holder.pbSyncProgressbar.setVisibility(View.VISIBLE);
                                        SyncSampleAsyncTask syncSampleAsyncTask = new SyncSampleAsyncTask(holder, context, savedSampleAdapter, currentSample, position);
                                        syncSampleAsyncTask.execute();

                                        challen_id = currentSample.getQciNumber();

                                        SyncData = false;



                                    }else {
                                        Toast.makeText(context,"Please sync all the record one by one",Toast.LENGTH_LONG).show();

                                    }
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void add(int position, SampleEntity item) {
        dataList.add(position, item);
        notifyItemInserted(position);

    }

    public void remove(int position) {

        try {
            dataList.remove(position);
            notifyItemRemoved(position);
        }catch (Exception e){
            e.printStackTrace();
        }



    }

    public boolean checkConnection() {
        boolean flag = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                Logger.d("Network Type " + info.getTypeName());
                flag = true;
            }
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                Logger.d("Network Type " + info.getTypeName());
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public class SavedSampleViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSampleId;
        public TextView tvData;
        public TextView tvDate;
        public LinearLayout llRoot;
        public ImageView ivSyncItem;
        public ImageView ivDeleteItem;
        public ProgressBar pbSyncProgressbar;

        public SavedSampleViewHolder(View itemView) {
            super(itemView);
            tvSampleId = (TextView) itemView.findViewById(R.id.sampleId);
            tvData = (TextView) itemView.findViewById(R.id.data);
            tvDate = (TextView) itemView.findViewById(R.id.date);
            llRoot = (LinearLayout) itemView.findViewById(R.id.llRoot);
            ivSyncItem = (ImageView) itemView.findViewById(R.id.ivSyncItem);
            ivDeleteItem = (ImageView)itemView.findViewById(R.id.ivDeleteItem);
            pbSyncProgressbar = (ProgressBar) itemView.findViewById(R.id.pbSyncProgressbar);
        }
    }

    public class SyncSampleAsyncTask extends AsyncTask<Void, Void, Void> implements AppDatabaseResponse, AppNetworkResponse {
        SavedSampleViewHolder holder;
        Context rootContext;
        SavedSampleAdapter savedSampleAdapter;
        int position;
        SampleEntity selectedSample;
        List<SampleCustomerEntity> sampleCustomerEntityArrayList = new ArrayList<>();
        List<SampleImageEntity> selectedSampleImagesList;
        String remoteSampleId;
        List<String> remoteImagePathsList = new ArrayList<>();

        SyncSampleAsyncTask(SavedSampleViewHolder holder, Context rootContext, SavedSampleAdapter savedSampleAdapter, SampleEntity selecteSample, int position) {
            this.holder = holder;
            this.rootContext = rootContext;
            this.savedSampleAdapter = savedSampleAdapter;
            this.selectedSample = selecteSample;
            this.position = position;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SampleCustomerDBHelper sampleCustomerDBHelper = new SampleCustomerDBHelper(AppDatabase.getAppDatabase(rootContext), this);
            sampleCustomerDBHelper.getSamplCustomerByLocalSampleId(selectedSample.getLocalSampleId(), Constants.SELECT_SAMPLE_CUSTOMER_BY_ID);

            SampleImageDBHelper sampleImageDBHelper = new SampleImageDBHelper(AppDatabase.getAppDatabase(rootContext), this);
            sampleImageDBHelper.getSampleImageByLocalSampleId(selectedSample.getLocalSampleId(), Constants.SELECT_ALL_SAMPLE_IMAGES_BY_SAMPLE_ID);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
            switch (reqCode) {
                case Constants.SELECT_SAMPLE_CUSTOMER_BY_ID: {
                    try {
                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<SampleCustomerEntity>>() {
                        }.getType();
                        sampleCustomerEntityArrayList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);

                            if (selectedSampleImagesList != null && selectedSampleImagesList.size() > 0) {
                                saveSampleDetailsToServer();
                            }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    break;
                }
                case Constants.SELECT_ALL_SAMPLE_IMAGES_BY_SAMPLE_ID: {
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<SampleImageEntity>>() {
                    }.getType();
                    try {
                        selectedSampleImagesList = gson.fromJson(jsonObject.getJSONArray("data").toString(), listType);

                        saveSampleDetailsToServer();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case Constants.DELETE_ALL_SAVED_SAMPLE_DATA: {
                    syncComplete();
                    break;
                }
            }

        }

        @Override
        public void onResSuccess(JSONObject jsonObject, int reqCode) {
            switch (reqCode) {
                case Constants.REQ_POST_SAMPLING: {
                    try {
                        remoteSampleId = jsonObject.getString("data");
                        saveSampleCustomerToServer();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        syncInterrupted();
                    }
                    break;
                }

                case Constants.REQ_POST_SAMPLING_CUSTOMER: {
                    //TODO this guy here is hardcoding - fix image uploading
                    try {
                        if (selectedSampleImagesList.get(0).getLocalFilePath() == null & selectedSampleImagesList.get(1).getLocalFilePath() == null & selectedSampleImagesList.get(2).getLocalFilePath() == null){
                            saveSampleImagesToServer(false);
                        }else {
                            saveSampleImagesUploadToServer(false);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        syncInterrupted();
                    }


                    break;
                }
                case Constants.REQ_POST_SAMPLING_IMAGE_UPLOAD: {
                    try {
                        remoteImagePathsList.add(jsonObject.getJSONObject("data").getString("name"));
                        saveSampleImagesToServer(false);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        syncInterrupted();
                    }
                    break;
                }
                case Constants.REQ_POST_SAMPLING_IMAGE_UPLOAD_ASYNC:{
                    try {
                        if(jsonObject!=null) {
                            remoteImagePathsList.add(jsonObject.getJSONObject("data").getString("name"));
                            int length = remoteImagePathsList.size();

                            int image_length = 0;

                            if (selectedSampleImagesList.get(0).getLocalFilePath() != null){
                                image_length = 1;
                            }
                            if (selectedSampleImagesList.get(0).getLocalFilePath() != null && selectedSampleImagesList.get(1).getLocalFilePath() != null){
                                image_length = 2;
                            }
                            if (selectedSampleImagesList.get(0).getLocalFilePath() != null && selectedSampleImagesList.get(1).getLocalFilePath() != null && selectedSampleImagesList.get(2).getLocalFilePath() != null){
                                image_length = 3;
                            }



                            if (image_length == 1 && length == 1){
                                singleImage(false);
                            }
                            else if (image_length == 2 && length == 2){
                                doubleImage(false);
                            }
                            else if (image_length == 3 && length == 3){
                                thirdImage(false);
                            }else if (image_length == 0){
                                NoImage(false);
                            }

   //                         saveSampleImagesToServer(false);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        syncInterrupted();
                    }
                    break;
                }
                case Constants.REQ_POST_SAMPLING_IMAGES_LIST: {
                    //TODO finalize and implement below
                    DeleteSavedSampleDataByID deleteSavedSampleDataByID = new DeleteSavedSampleDataByID(
                            context,
                            selectedSample.getLocalSampleId(),
                            this,
                            Constants.DELETE_ALL_SAVED_SAMPLE_DATA
                    );
                    deleteSavedSampleDataByID.execute();
                    break;
                }
            }
        }



        @Override
        public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
            switch (reqCode) {
                case Constants.REQ_POST_SAMPLING: {
                    try {
                        if (jsonObject.has("data")) {
                            Toast.makeText(rootContext, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();

                            SyncData = true;

                            holder.pbSyncProgressbar.setVisibility(View.GONE);
                            holder.ivSyncItem.setVisibility(View.VISIBLE);

                            /*Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PreferenceHelper.clearSharedPreferences();
                            context.startActivity(intent);*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(rootContext, "Couldn't post Sample", Toast.LENGTH_SHORT).show();
                        syncInterrupted();
                    }
      //              Toast.makeText(rootContext, "Couldn't post Sample", Toast.LENGTH_SHORT).show();
     //               syncInterrupted();
                    break;
                }
                case Constants.REQ_POST_SAMPLING_CUSTOMER: {
                    try {
                        if (jsonObject.has("data")) {
                            Toast.makeText(rootContext, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();

                            SyncData = true;

                            holder.pbSyncProgressbar.setVisibility(View.GONE);
                            holder.ivSyncItem.setVisibility(View.VISIBLE);

                          /*  Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PreferenceHelper.clearSharedPreferences();
                            context.startActivity(intent);*/
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(rootContext, "Couldn't post Sample Customer", Toast.LENGTH_SHORT).show();
                        syncInterrupted();
                        return;
                    }
         /*           Toast.makeText(rootContext, "Couldn't post Sample Customer", Toast.LENGTH_SHORT).show();
                    syncInterrupted();*/
                    break;
                }
                case Constants.REQ_POST_SAMPLING_IMAGE_UPLOAD_ASYNC: {
                    try {

                        if (jsonObject!=null) {
                            Toast.makeText(rootContext, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();

                            SyncData = true;

                            holder.pbSyncProgressbar.setVisibility(View.GONE);
                            holder.ivSyncItem.setVisibility(View.VISIBLE);

                          /*  Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PreferenceHelper.clearSharedPreferences();
                            context.startActivity(intent);*/
                        }
                        else {
                            Toast.makeText(rootContext, errMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(rootContext, "Couldn't upload Sample Image", Toast.LENGTH_SHORT).show();
                        syncInterrupted();
                        return;
                    }
                  /*  Toast.makeText(rootContext, "Couldn't upload Sample Image", Toast.LENGTH_SHORT).show();
                    syncInterrupted();*/
                    break;
                }
                case Constants.REQ_POST_SAMPLING_IMAGES_LIST: {
                    try {
                        if (!jsonObject.getBoolean("success")) {

                            JSONObject data = jsonObject.getJSONObject("data");

                            JSONObject error = data.getJSONObject("errors");

                            String Qci_number = error.getString("qci_number");

                            if (Qci_number != null) {
                                Toast.makeText(rootContext, "Data not sync properly Can u pls try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
       //                 Toast.makeText(rootContext, "Couldn't post Sample Images List", Toast.LENGTH_SHORT).show();
                        syncInterrupted();
                        return;
                    }
  //                  Toast.makeText(rootContext, "Couldn't post Sample Images List", Toast.LENGTH_SHORT).show();
    //                syncInterrupted();
                    break;
                }
            }

        }

        private void saveSampleDetailsToServer() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("subsidiary", selectedSample.getSubsidiaryName());
                jsonObject.put("area", selectedSample.getArea());
                jsonObject.put("location", selectedSample.getLocation());
                jsonObject.put("location_code",selectedSample.getLocation_code());
                jsonObject.put("is_primary", selectedSample.getIsPrimary());
                jsonObject.put("collection_date", FormatConversionHelper.getFormatedDateTime(selectedSample.getCollectionDate().toString(), "EEE MMMM d hh:mm:ss z yyyy", "dd-MM-yyyy"));
                jsonObject.put("lifting_type", selectedSample.getLiftingType());
                jsonObject.put("lifted_quantity", selectedSample.getQuantityLifted());
                jsonObject.put("sampled_quantity", selectedSample.getQuantitySampled());
                jsonObject.put("sampling_type", selectedSample.getSamplingType());
                jsonObject.put("challan_number", selectedSample.getChallanNumber());
                jsonObject.put("qci_number", selectedSample.getQciNumber());
                jsonObject.put("quantity_status", quantity_status);
                jsonObject.put("lattitude",selectedSample.getLattitude());
                jsonObject.put("longitude",selectedSample.getLongitude());



                Volley volley = Volley.getInstance();
                volley.postSession(Constants.apiSampling, jsonObject, this,getFromPrefs(Constants.USER_SESSION) , Constants.REQ_POST_SAMPLING);

            } catch (Exception e) {
                e.printStackTrace();
                syncInterrupted();
            }
        }

        private void saveSampleCustomerToServer() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("sample_id", remoteSampleId);
                jsonObject.put("lifting_type", selectedSample.getLiftingType());
                //TODO remove the below hardcode
                jsonObject.put("lifting_type", "Rail");

                JSONArray jsonArray = new JSONArray();
                for(SampleCustomerEntity sampleCustomerEntity : sampleCustomerEntityArrayList){
                    JSONObject customerJsonObject = new JSONObject();
                    customerJsonObject.put("customer_id", sampleCustomerEntity.getCustomerId());

                    ArrayList<MoredetailsEntity> list=new ArrayList<MoredetailsEntity>();

                    if (sampleCustomerEntity.getMore_details() != null){
                        JSONArray jsonArray1 = new JSONArray(sampleCustomerEntity.getMore_details());

                        for (int i=0;i<jsonArray1.length();i++){

                            JSONObject object = jsonArray1.getJSONObject(i);

                            String fsa_number = object.getString("fsa_number");
                            String do_details = object.getString("do_details");
                            String quantity = object.getString("quantity");

                            MoredetailsEntity obj = new MoredetailsEntity();

                            obj.setFsa_number(fsa_number);
                            obj.setDo_details(do_details);
                            obj.setQuantity(quantity);

                            list.add(obj);
                        }
                    }

                    JSONArray jsonArray2 = new JSONArray();
                    for (int j= 0; j<list.size();j++){
                        JSONObject details_object = new JSONObject();
                        if (!list.get(j).getDo_details().equalsIgnoreCase("0")){
                            details_object.put("do_details",list.get(j).getDo_details());
                        }else {
                            details_object.put("do_details","");
                        }

                        details_object.put("quantity",list.get(j).getQuantity());
                        if (!list.get(j).getFsa_number().equalsIgnoreCase("0")){
                            details_object.put("fsa_number",list.get(j).getFsa_number());
                        }else {
                            details_object.put("fsa_number","");
                        }

                        jsonArray2.put(details_object);
                    }

        //            jsonArray.put(jsonArray2);

                    if (sampleCustomerEntity.getRrdate() != null){

                        rrdate = sampleCustomerEntity.getRrdate();
                    }else {
                        rrdate = "";
                    }


                    customerJsonObject.put("more_details", jsonArray2);
                    customerJsonObject.put("declared_grade", sampleCustomerEntity.getDeclaredGrade());
                    customerJsonObject.put("total_vehicles", sampleCustomerEntity.getTotalVehicles());
                    customerJsonObject.put("auction_type", sampleCustomerEntity.getAuction_type());
                    customerJsonObject.put("challan_code",sampleCustomerEntity.getChallanCode());
                    customerJsonObject.put("total_vehicles_sampled", sampleCustomerEntity.getTotalVehiclesSampled());

                    jsonArray.put(customerJsonObject);
                }


                jsonObject.put("rrdate", rrdate);
                jsonObject.put("customers", jsonArray);

                Volley volley = Volley.getInstance();
                volley.postSession(Constants.apiSamplingCustomer, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_CUSTOMER);

            } catch (Exception e) {
                e.printStackTrace();
                syncInterrupted();
            }
        }

        private void saveSampleImagesUploadToServer(boolean hardCode) {
            if (hardCode) {
                saveSampleImagesToServer(hardCode);
                return;
            } else {
                if (selectedSampleImagesList.get(0).getLocalFilePath() != null){
                    new ImageUploaderAsync(Constants.apiSamplingImageUpload, selectedSampleImagesList.get(0).getLocalFilePath(),context,this,Constants.REQ_POST_SAMPLING_IMAGE_UPLOAD_ASYNC).execute();
                }

                if (selectedSampleImagesList.get(1).getLocalFilePath() != null){
                    new ImageUploaderAsync(Constants.apiSamplingImageUpload, selectedSampleImagesList.get(1).getLocalFilePath(),context,this,Constants.REQ_POST_SAMPLING_IMAGE_UPLOAD_ASYNC).execute();
                }

                if (selectedSampleImagesList.get(2).getLocalFilePath() != null){
                    new ImageUploaderAsync(Constants.apiSamplingImageUpload, selectedSampleImagesList.get(2).getLocalFilePath(),context,this,Constants.REQ_POST_SAMPLING_IMAGE_UPLOAD_ASYNC).execute();
                }

                return;
            }
//            Volley volley = Volley.getInstance();
//            volley.postMultipartData(Constants.apiSamplingImageUpload, selectedSampleImagesList.get(0).getLocalFilePath(), null, this, Constants.REQ_POST_SAMPLING_IMAGE_UPLOAD);
//            volley.postMultipartData(Constants.apiSamplingImageUpload, selectedSampleImagesList.get(1).getLocalFilePath(), null, this, Constants.REQ_POST_SAMPLING_IMAGE_UPLOAD);
//            volley.postMultipartData(Constants.apiSamplingImageUpload, selectedSampleImagesList.get(2).getLocalFilePath(), null, this, Constants.REQ_POST_SAMPLING_IMAGE_UPLOAD);

        }

        private void singleImage(boolean hardCode){
            JSONObject jsonObject = new JSONObject();
            try {

                //TODO Remove the hard code here
                if (hardCode) {
                    jsonObject.put("collection_image_1", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_2", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_3", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                } else {

                        jsonObject.put("collection_image_1", remoteImagePathsList.get(0));
                        jsonObject.put("collection_image_2", "");
                        jsonObject.put("collection_image_3", "");

                        jsonObject.put("sample_id", remoteSampleId);

                        Volley volley = Volley.getInstance();
                        volley.postSession(Constants.apiSamplingImages, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_IMAGES_LIST);

                }


            } catch (JSONException e) {
                e.printStackTrace();
 //               syncInterrupted();
            }
        }

        private void doubleImage(boolean hardCode){
            JSONObject jsonObject = new JSONObject();
            try {

                //TODO Remove the hard code here
                if (hardCode) {
                    jsonObject.put("collection_image_1", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_2", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_3", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                } else {

                        jsonObject.put("collection_image_1", remoteImagePathsList.get(0));
                        jsonObject.put("collection_image_2", remoteImagePathsList.get(1));
                        jsonObject.put("collection_image_3", "");

                        jsonObject.put("sample_id", remoteSampleId);


                            Volley volley = Volley.getInstance();
                            volley.postSession(Constants.apiSamplingImages, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_IMAGES_LIST);

                }


            } catch (JSONException e) {
                e.printStackTrace();
  //              syncInterrupted();
            }
        }
        private void thirdImage(boolean hardCode){
            JSONObject jsonObject = new JSONObject();
            try {

                //TODO Remove the hard code here
                if (hardCode) {
                    jsonObject.put("collection_image_1", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_2", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_3", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                } else {

                        jsonObject.put("collection_image_1", remoteImagePathsList.get(0));

                        jsonObject.put("collection_image_2", remoteImagePathsList.get(1));
                        jsonObject.put("collection_image_3", remoteImagePathsList.get(2));

                        jsonObject.put("sample_id", remoteSampleId);


                        Volley volley = Volley.getInstance();
                        volley.postSession(Constants.apiSamplingImages, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_IMAGES_LIST);
                }


            } catch (JSONException e) {
                e.printStackTrace();
  //              syncInterrupted();
            }
        }

        private void NoImage(boolean hardCode){
            JSONObject jsonObject = new JSONObject();
            try {

                //TODO Remove the hard code here
                if (hardCode) {
                    jsonObject.put("collection_image_1", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_2", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_3", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                } else {

                        jsonObject.put("collection_image_1", "");

                        jsonObject.put("collection_image_2", "");
                        jsonObject.put("collection_image_3", "");

                        jsonObject.put("sample_id", remoteSampleId);

                        Volley volley = Volley.getInstance();
                        volley.postSession(Constants.apiSamplingImages, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_IMAGES_LIST);


                }


            } catch (JSONException e) {
                e.printStackTrace();
 //               syncInterrupted();
            }
        }

        private void saveSampleImagesToServer(boolean hardCode) {
            JSONObject jsonObject = new JSONObject();
            try {

                //TODO Remove the hard code here
                if (hardCode) {
                    jsonObject.put("collection_image_1", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_2", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                    jsonObject.put("collection_image_3", "f57d9cf7d34bbdf4acb950a7f8a3a81b.jpeg");
                } else {


                    int length = 0;

                    if (selectedSampleImagesList.get(0).getLocalFilePath() != null){
                        length = 1;
                    }
                    if (selectedSampleImagesList.get(0).getLocalFilePath() != null && selectedSampleImagesList.get(1).getLocalFilePath() != null){
                       length = 2;
                    }
                    if (selectedSampleImagesList.get(0).getLocalFilePath() != null && selectedSampleImagesList.get(1).getLocalFilePath() != null && selectedSampleImagesList.get(2).getLocalFilePath() != null){
                       length = 3;
                    }

                    if (length == 1){

                        jsonObject.put("collection_image_1", remoteImagePathsList.get(0));
                        jsonObject.put("collection_image_2", "");
                        jsonObject.put("collection_image_3", "");

                        jsonObject.put("sample_id", remoteSampleId);



                            Volley volley = Volley.getInstance();
                            volley.postSession(Constants.apiSamplingImages, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_IMAGES_LIST);

                    }
                    else if (length == 2){

                          jsonObject.put("collection_image_1", remoteImagePathsList.get(0));
                          jsonObject.put("collection_image_2", remoteImagePathsList.get(1));
                          jsonObject.put("collection_image_3", "");

                          jsonObject.put("sample_id", remoteSampleId);


                            Volley volley = Volley.getInstance();
                            volley.postSession(Constants.apiSamplingImages, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_IMAGES_LIST);


                    }

                    else if (length == 3){

                        jsonObject.put("collection_image_1", selectedSampleImagesList.get(0));

                        jsonObject.put("collection_image_2", selectedSampleImagesList.get(1));
                        jsonObject.put("collection_image_3", selectedSampleImagesList.get(2));

                        jsonObject.put("sample_id", remoteSampleId);


                            Volley volley = Volley.getInstance();
                            volley.postSession(Constants.apiSamplingImages, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_IMAGES_LIST);
                    } else {
                        jsonObject.put("collection_image_1", "");

                        jsonObject.put("collection_image_2", "");
                        jsonObject.put("collection_image_3", "");

                        jsonObject.put("sample_id", remoteSampleId);


                        Volley volley = Volley.getInstance();
                        volley.postSession(Constants.apiSamplingImages, jsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAMPLING_IMAGES_LIST);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
                syncInterrupted();
            }
        }

        private void syncInterrupted() {
            holder.pbSyncProgressbar.setVisibility(View.GONE);
            holder.ivSyncItem.setVisibility(View.VISIBLE);
            Toast.makeText(rootContext, " Sync was interrupted ", Toast.LENGTH_SHORT).show();

            SyncData = true;
        }


        private void syncComplete() {
            holder.pbSyncProgressbar.setVisibility(View.GONE);
            holder.ivSyncItem.setVisibility(View.VISIBLE);
            Toast.makeText(rootContext, "Sample details Synced", Toast.LENGTH_SHORT).show();
            savedSampleAdapter.remove(position);
            notifyDataSetChanged();

            SyncData = true;
        }

    }

    public class DeleteSampleBYID extends AsyncTask<Void,Void,Void>{
        private ProgressDialog progressDialog;
        Context context;
        int sampleId;
        int position;

        public DeleteSampleBYID(Context context,int SampleId,int pos){
            this.context = context;
           this.sampleId = SampleId;
           this.position = pos;

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            appDatabase.sampleDao().deleteByLocalSampleId(sampleId);
            appDatabase.sampleCustomerDao().deleteByLocalSampleId(sampleId);
            appDatabase.sampleImageDao().deleteByLocalSampleId(sampleId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            savedSampleAdapter.remove(position);
            notifyDataSetChanged();

            progressDialog.dismiss();
        }
    }


    public class DeleteSavedSampleDataByID extends AsyncTask<Void, Void, Void> {
        Context context;
        int sampleId;
        AppDatabaseResponse appDatabaseResponse;
        int reqCode;

        public DeleteSavedSampleDataByID(Context context, int sampleId, AppDatabaseResponse appDatabaseResponse, int reqCode) {
            this.context = context;
            this.sampleId = sampleId;
            this.appDatabaseResponse = appDatabaseResponse;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deleteTheSamplingDataFromDB();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }

        private void deleteTheSamplingDataFromDB() {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            appDatabase.sampleDao().deleteByLocalSampleId(sampleId);
            appDatabase.sampleCustomerDao().deleteByLocalSampleId(sampleId);
            appDatabase.sampleImageDao().deleteByLocalSampleId(sampleId);
        }
    }

    public String getFromPrefs(String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }
}
