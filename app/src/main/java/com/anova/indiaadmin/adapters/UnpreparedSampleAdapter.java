package com.anova.indiaadmin.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anova.indiaadmin.R;
import com.anova.indiaadmin.UnpreparedStage1Activity;
import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.database.preparationimages.PreparationImageQrEntity;
import com.anova.indiaadmin.database.preparationimages.PreparationImageQrHelper;
import com.anova.indiaadmin.database.preparations.PreparationDbHelper;
import com.anova.indiaadmin.database.preparations.PreparationEntity;
import com.anova.indiaadmin.network.AppNetworkResponse;
import com.anova.indiaadmin.network.ImageUploaderAsync;
import com.anova.indiaadmin.network.Volley;
import com.anova.indiaadmin.utils.Constants;
import com.anova.indiaadmin.utils.PreferenceHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by iqbal on 10/5/18.
 */

public class UnpreparedSampleAdapter extends RecyclerView.Adapter<UnpreparedSampleAdapter.UnpreparedSampleViewHolder> implements AppDatabaseResponse, AppNetworkResponse {

    private static final int GET_PREPARATION_IMAGE_QR = 1001;
    private static final int DELETE_PREP_DATA_COMPLETELY = 2;

    private List<PreparationEntity> dataList;
    private Context context;
    private UnpreparedSampleAdapter unpreparedSampleAdapter;
    private boolean clickEnabled;
    private String subsidiary, area, location, subAreaLoc;
    private PreparationImageQrEntity preparationImageQrEntity;
    private JSONObject prepInsertJsonObject, qrInsertJsonObject;
    private PreparationEntity currentPrepEntity;
    private ProgressDialog progressDialog;
    private String remotePreparationImage1, remotePreparationImage2;

    public static final int REQ_IMAGE_UPLOAD_1 = 201;
    public static final int REQ_IMAGE_UPLOAD_2 = 202;
    private int currentSyncingPosition;

    String preparation_date;

    int prepID;

    String date;

    public UnpreparedSampleAdapter(Context context, List<PreparationEntity> dataList, String subsidiary, String area, String location, boolean clickEnabled) {
        this.dataList = dataList;
        this.context = context;
        this.unpreparedSampleAdapter = this;
        this.clickEnabled = clickEnabled;
        this.subsidiary = subsidiary;
        this.area = area;
        this.location = location;
        subAreaLoc = subsidiary + "/" + area + "/" + location;
    }

    @Override
    public UnpreparedSampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.item_row_saved_sample, parent, false);
        // set the view's size, margins, paddings and layout parameters
        UnpreparedSampleViewHolder vh = new UnpreparedSampleViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final UnpreparedSampleViewHolder holder, final int position) {
        currentPrepEntity = dataList.get(position);

        holder.ivDeleteItem.setVisibility(View.GONE);

 //      holder.tvSampleId.setVisibility(View.GONE);
        holder.tvData.setTextSize(15);
        holder.tvDate.setTextSize(15);
        holder.tvData.setText(subAreaLoc);
        try {
            JSONObject json = new JSONObject(currentPrepEntity.getMyJsonData());

             date = json.getString("date_of_collection");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject json = new JSONObject(currentPrepEntity.getMyJsonData());

            String challan_code = json.getString("qci");

            if (!challan_code.equalsIgnoreCase("null")){
                holder.tvSampleId.setText("Sample ID : " + challan_code);
            }else {
                holder.tvSampleId.setText("Sample ID : " + "");
            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        holder.tvDate.setText("Collection Date : " + date);
        if(!currentPrepEntity.isModified()){
            holder.ivSyncItem.setVisibility(View.GONE);
        } else {
            holder.ivSyncItem.setVisibility(View.VISIBLE);
        }
        holder.pbSyncProgressbar.setVisibility(View.GONE);
        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickEnabled) {
                    try {
                        PreferenceHelper.putInteger(Constants.SP_KEY_CURRENT_PREPARATION_ID, dataList.get(position).getPrepId());
                        Bundle bundle = new Bundle();
                        bundle.putString("subsidiary",subsidiary);
                        bundle.putString("area",area);
                        bundle.putString("location",location);
                        Intent intent = new Intent(context, UnpreparedStage1Activity.class);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });
        if(holder.ivSyncItem!=null){
            holder.ivSyncItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO put the Prep API calls here using RxJAVA
                    currentSyncingPosition = position;
                    PreparationImageQrHelper preparationImageQrHelper = new PreparationImageQrHelper(AppDatabase.getAppDatabase(context),unpreparedSampleAdapter, dataList.get(position).getPrepId());
                    preparationImageQrHelper.getPrepImageQr(GET_PREPARATION_IMAGE_QR,dataList.get(position).getPrepId());

                    prepID = dataList.get(position).getPrepId();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void add(int position, PreparationEntity item) {
        dataList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position) {

        dataList.remove(position);
        notifyItemRemoved(position);

       notifyDataSetChanged();
    }

    @Override
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode){
            case GET_PREPARATION_IMAGE_QR:{
                if(jsonObject!=null) {
                    try {
                        Type prepImageQrType = new TypeToken<PreparationImageQrEntity>() {
                        }.getType();
                        preparationImageQrEntity = new Gson().fromJson(jsonObject.toString(), prepImageQrType);
                        prepInsertJsonObject = new JSONObject();
                        prepInsertJsonObject.put("sample_id", prepID);
                        //TODO : get the fields removed from the API and remove this hard code values
                        prepInsertJsonObject.put("challan_number", "1234");
                        prepInsertJsonObject.put("qci_number", "QCI/COAL/"+currentPrepEntity.getSubsidiary()+"/"+currentPrepEntity.getLocation()+"/"+1234);

                        if (getFromPrefs(Constants.preparation_date+prepID).length() > 0){
                            preparation_date = getFromPrefs(Constants.preparation_date+prepID);
                        }else {
                            preparation_date = "";
                        }

                        qrInsertJsonObject = new JSONObject();
                        qrInsertJsonObject.put("sample_id", prepID);
                        qrInsertJsonObject.put("customer_qr", preparationImageQrEntity.getCustomerQr());
                        qrInsertJsonObject.put("qci_qr", preparationImageQrEntity.getQciQr());
                        qrInsertJsonObject.put("subsidiary_qr", preparationImageQrEntity.getSubsidiaryQr());
                        qrInsertJsonObject.put("referee_qr", preparationImageQrEntity.getRefreeQr());
                        qrInsertJsonObject.put("prepration_date", preparation_date);


                            progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage("Uploading Information...");
                            progressDialog.setCancelable(false);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();

                            if (preparationImageQrEntity.getImage4() !=  null & preparationImageQrEntity.getImage5() != null){
                                new ImageUploaderAsync(Constants.apiSamplingImageUpload, preparationImageQrEntity.getImage4(),context,unpreparedSampleAdapter,REQ_IMAGE_UPLOAD_1).execute();
                                new ImageUploaderAsync(Constants.apiSamplingImageUpload, preparationImageQrEntity.getImage5(),context,unpreparedSampleAdapter,REQ_IMAGE_UPLOAD_2).execute();
                            }else if (preparationImageQrEntity.getImage4() != null & preparationImageQrEntity.getImage5() == null){
                                new ImageUploaderAsync(Constants.apiSamplingImageUpload, preparationImageQrEntity.getImage4(),context,unpreparedSampleAdapter,REQ_IMAGE_UPLOAD_1).execute();
                            }else if (preparationImageQrEntity.getImage4() == null && preparationImageQrEntity.getImage5() != null){
                                new ImageUploaderAsync(Constants.apiSamplingImageUpload, preparationImageQrEntity.getImage5(),context,unpreparedSampleAdapter,REQ_IMAGE_UPLOAD_2).execute();
                            }
                            else {
                                Volley volley = Volley.getInstance();
                                volley.postSession(Constants.apiSavePreparationStageQR, qrInsertJsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAVE_PREPARATION_QR);
                            }

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    try {
                        Type prepImageQrType = new TypeToken<PreparationImageQrEntity>() {
                        }.getType();
                        if (getFromPrefs(Constants.customerQr+prepID).length() > 0){
                            preparationImageQrEntity.setCustomerQr(getFromPrefs(Constants.customerQr+prepID));
                        }else {
                            preparationImageQrEntity.setCustomerQr("");
                        }
                        if (getFromPrefs(Constants.qciQr+prepID).length() >0){
                            preparationImageQrEntity.setQciQr(getFromPrefs(Constants.qciQr+prepID));
                        }else {
                            preparationImageQrEntity.setQciQr("");
                        }
                        if (getFromPrefs(Constants.subsidiary_qr+prepID).length() > 0){
                            preparationImageQrEntity.setSubsidiaryQr(getFromPrefs(Constants.subsidiary_qr+prepID));
                        }else {
                            preparationImageQrEntity.setSubsidiaryQr("");
                        }
                        if (getFromPrefs(Constants.referee_qr+prepID).length() > 0){
                            preparationImageQrEntity.setRefreeQr(getFromPrefs(Constants.referee_qr+prepID));
                        }else {
                            preparationImageQrEntity.setRefreeQr("");
                        }
                        if (getFromPrefs(Constants.image4+prepID).length() > 0){
                            preparationImageQrEntity.setImage4(getFromPrefs(Constants.image4+prepID));
                        }else {
                            preparationImageQrEntity.setImage4(null);
                        }
                        if (getFromPrefs(Constants.image5+prepID).length() > 0){
                            preparationImageQrEntity.setImage5(getFromPrefs(Constants.image5+prepID));
                        }else {
                            preparationImageQrEntity.setImage5(null);
                        }

                        if (getFromPrefs(Constants.preparation_date+prepID).length() > 0){
                            preparation_date = getFromPrefs(Constants.preparation_date+prepID);
                        }else {
                            preparation_date = "";
                        }

                        prepInsertJsonObject = new JSONObject();
                        prepInsertJsonObject.put("sample_id", prepID);
                        //TODO : get the fields removed from the API and remove this hard code values
                        prepInsertJsonObject.put("challan_number", "1234");
                        prepInsertJsonObject.put("qci_number", "QCI/COAL/"+currentPrepEntity.getSubsidiary()+"/"+currentPrepEntity.getLocation()+"/"+1234);

                        qrInsertJsonObject = new JSONObject();
                        qrInsertJsonObject.put("sample_id", prepID);
                        qrInsertJsonObject.put("customer_qr", preparationImageQrEntity.getCustomerQr());
                        qrInsertJsonObject.put("qci_qr", preparationImageQrEntity.getQciQr());
                        qrInsertJsonObject.put("subsidiary_qr", preparationImageQrEntity.getSubsidiaryQr());
                        qrInsertJsonObject.put("referee_qr", preparationImageQrEntity.getRefreeQr());
                        qrInsertJsonObject.put("prepration_date", preparation_date);


                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Uploading Information...");
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        if (preparationImageQrEntity.getImage4() !=  null & preparationImageQrEntity.getImage5() != null){
                            new ImageUploaderAsync(Constants.apiSamplingImageUpload, preparationImageQrEntity.getImage4(),context,unpreparedSampleAdapter,REQ_IMAGE_UPLOAD_1).execute();
                            new ImageUploaderAsync(Constants.apiSamplingImageUpload, preparationImageQrEntity.getImage5(),context,unpreparedSampleAdapter,REQ_IMAGE_UPLOAD_2).execute();
                        }else if (preparationImageQrEntity.getImage4() != null & preparationImageQrEntity.getImage5() == null){
                            new ImageUploaderAsync(Constants.apiSamplingImageUpload, preparationImageQrEntity.getImage4(),context,unpreparedSampleAdapter,REQ_IMAGE_UPLOAD_1).execute();
                        }else if (preparationImageQrEntity.getImage4() == null && preparationImageQrEntity.getImage5() != null){
                            new ImageUploaderAsync(Constants.apiSamplingImageUpload, preparationImageQrEntity.getImage5(),context,unpreparedSampleAdapter,REQ_IMAGE_UPLOAD_2).execute();
                        }
                        else {
                            Volley volley = Volley.getInstance();
                            volley.postSession(Constants.apiSavePreparationStageQR, qrInsertJsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAVE_PREPARATION_QR);
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            }
            case DELETE_PREP_DATA_COMPLETELY:{
                if(progressDialog!=null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    remove(currentSyncingPosition);

                } else {
                    Toast.makeText(context, "There was some error, try again!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onResSuccess(JSONObject jsonObject, int reqCode) {
        switch (reqCode) {
            case REQ_IMAGE_UPLOAD_1: {
                try {
                    remotePreparationImage1 = jsonObject.getJSONObject("data").getString("name");
                    if (remotePreparationImage1 != null && remotePreparationImage2 != null) {
                        prepInsertJsonObject.put("collection_image_1", remotePreparationImage1);
                        prepInsertJsonObject.put("collection_image_2", remotePreparationImage2);
                        Volley volley = Volley.getInstance();
                        volley.postSession(Constants.apiSavePreparationStageChallan, prepInsertJsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAVE_PREPARATION_CHALLAN);
                    }else if (remotePreparationImage1 != null & remotePreparationImage2 == null & preparationImageQrEntity.getImage5() == null){
                        prepInsertJsonObject.put("collection_image_1", remotePreparationImage1);
                        prepInsertJsonObject.put("collection_image_2", "");
                        Volley volley = Volley.getInstance();
                        volley.postSession(Constants.apiSavePreparationStageChallan, prepInsertJsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAVE_PREPARATION_CHALLAN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case REQ_IMAGE_UPLOAD_2: {
                try {
                    remotePreparationImage2 = jsonObject.getJSONObject("data").getString("name");
                    if (remotePreparationImage1 != null && remotePreparationImage2  != null) {
                        prepInsertJsonObject.put("collection_image_1", remotePreparationImage1);
                        prepInsertJsonObject.put("collection_image_2", remotePreparationImage2);
                        Volley volley = Volley.getInstance();
                        volley.postSession(Constants.apiSavePreparationStageChallan, prepInsertJsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAVE_PREPARATION_CHALLAN);
                    }else if (remotePreparationImage2 != null && remotePreparationImage1 == null){
                        prepInsertJsonObject.put("collection_image_1", "");
                        prepInsertJsonObject.put("collection_image_2", remotePreparationImage2);
                        Volley volley = Volley.getInstance();
                        volley.postSession(Constants.apiSavePreparationStageChallan, prepInsertJsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAVE_PREPARATION_CHALLAN);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
            case Constants.REQ_POST_SAVE_PREPARATION_CHALLAN: {
                Volley volley = Volley.getInstance();
                volley.postSession(Constants.apiSavePreparationStageQR, qrInsertJsonObject, this, getFromPrefs(Constants.USER_SESSION), Constants.REQ_POST_SAVE_PREPARATION_QR);
                break;
            }
            case Constants.REQ_POST_SAVE_PREPARATION_QR: {
                //TODO : DELETE ITEM here
                PreparationDbHelper preparationDbHelper = new PreparationDbHelper(AppDatabase.getAppDatabase(context), unpreparedSampleAdapter, subsidiary, area, location);
                preparationDbHelper.deletePrepItemImageQrData(prepID, DELETE_PREP_DATA_COMPLETELY);
                break;
            }
        }
    }

    @Override
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject) {
        try {
            progressDialog.dismiss();

 //           Toast.makeText(context,jsonObject.getString("data"),Toast.LENGTH_LONG).show();
  //          Toast.makeText(context, "API Error - code" + reqCode + " : " + jsonObject.toString(), Toast.LENGTH_SHORT).show();
    //        Toast.makeText(context,"Already Transit this sample,Please check on dashboard",Toast.LENGTH_LONG).show();

            if (!jsonObject.getBoolean("success")) {

                JSONObject data = jsonObject.getJSONObject("data");

                JSONObject error = data.getJSONObject("errors");

                String prepration_date = error.getString("prepration_date");

                if (prepration_date != null) {
                    Toast.makeText(context, "Prepration Date field is required.", Toast.LENGTH_SHORT).show();
                }
            }


        } catch (Exception e){
            e.printStackTrace();

            Toast.makeText(context, " Sync was interrupted ", Toast.LENGTH_SHORT).show();
        }
    }




    public class UnpreparedSampleViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSampleId;
        public TextView tvData;
        public TextView tvDate;
        public LinearLayout llRoot;
        public ImageView ivSyncItem;
        public ProgressBar pbSyncProgressbar;
        private ImageView ivDeleteItem;

        public UnpreparedSampleViewHolder(View itemView) {
            super(itemView);
            tvSampleId = (TextView) itemView.findViewById(R.id.sampleId);
            tvData = (TextView) itemView.findViewById(R.id.data);
            tvDate = (TextView) itemView.findViewById(R.id.date);
            llRoot = (LinearLayout) itemView.findViewById(R.id.llRoot);
            ivSyncItem = (ImageView) itemView.findViewById(R.id.ivSyncItem);
            pbSyncProgressbar = (ProgressBar) itemView.findViewById(R.id.pbSyncProgressbar);

            ivDeleteItem = (ImageView) itemView.findViewById(R.id.ivDeleteItem);
        }
    }

    public String getFromPrefs(String key) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, context.MODE_PRIVATE);
        return prefs.getString(key, "");
    }


}

