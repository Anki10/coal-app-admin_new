package com.anova.indiaadmin.database.samples;

import android.os.AsyncTask;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.anova.indiaadmin.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by iqbal on 7/5/18.
 */

public class SampleDBHelper {
    private AppDatabase appDatabase;
    private AppDatabaseResponse appDatabaseResponse;

    public SampleDBHelper(AppDatabase appDatabase, AppDatabaseResponse appDatabaseResponse) {
        this.appDatabase = appDatabase;
        this.appDatabaseResponse = appDatabaseResponse;
    }

    public void insertSample(SampleEntity sampleEntity, int reqCode){
        new InsertAction(sampleEntity, reqCode).execute();
    }

    public void insertAll(List<SampleEntity> sampleEntityList, int reqCode) {
        new InsertAllAction(sampleEntityList, reqCode).execute();
    }

    public void getSampleById(int sampleId, int reqCode){
        new GetSampleById(sampleId, reqCode).execute();
    }

    public void getAllSamples(int reqCode){
        new GetAllAction(reqCode).execute();
    }

    public void updateSample(SampleEntity sampleEntity, int reqCode) {
        new UpdateSampleStage1(sampleEntity, reqCode).execute();
    }

    public void updateChallanDetails(int reqCode, int localSampleId, String challanNumber, String qciNumber,String latitude,String longitude){
        new UpdateChallanDetails(reqCode, localSampleId, challanNumber, qciNumber,latitude,longitude).execute();
    }

    public class UpdateChallanDetails extends AsyncTask<Void, Void, Void>{
        int reqCode, localSampleId;
        String challanNumber, qciNumber;
        String latitude,longitude;

        public UpdateChallanDetails(int reqCode, int localSampleId, String challanNumber, String qciNumber,String latitude,String longitude) {
            this.reqCode = reqCode;
            this.localSampleId = localSampleId;
            this.challanNumber = challanNumber;
            this.qciNumber = qciNumber;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.sampleDao().updateChallanDetails(localSampleId, challanNumber, qciNumber,latitude,longitude);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class InsertAllAction extends AsyncTask<Void, Void, Void>{
        List<SampleEntity> sampleEntityList;
        int reqCode;
        public InsertAllAction(List<SampleEntity> sampleEntityList, int reqCode) {
            this.sampleEntityList = sampleEntityList;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.sampleDao().insertAll(sampleEntityList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class InsertAction extends AsyncTask<Void, Void, Void>{
        SampleEntity sampleEntity;
        int reqCode;
        Long sampleId;
        public InsertAction(SampleEntity sampleEntity, int reqCode) {
            this.sampleEntity = sampleEntity;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sampleId = appDatabase.sampleDao().insert(sampleEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("sampleId", sampleId);
                appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class UpdateSampleStage1 extends AsyncTask<Void, Void, Void>{
        SampleEntity sampleEntity;
        int reqCode;
        Object test;
        public UpdateSampleStage1(SampleEntity sampleEntity, int reqCode) {
            this.sampleEntity = sampleEntity;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            SimpleDateFormat df = new SimpleDateFormat(Constants.DOB_FORMAT);

            appDatabase.sampleDao().update(sampleEntity.getLocalSampleId(),
                    sampleEntity.getSubsidiaryName(),
                    sampleEntity.getArea(),
                    sampleEntity.getLocation(),
                    sampleEntity.getCollectionDate(),
                    sampleEntity.getLiftingType(),
                    sampleEntity.getAuctionType(),
                    sampleEntity.getQuantityLifted(),
                    sampleEntity.getQuantitySampled(),
                    sampleEntity.getSamplingType());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            JSONObject jsonObject = new JSONObject();
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class GetSampleById extends AsyncTask<Void, Void, Void>{
        int sampleId;
        int reqCode;
        SampleEntity mSample;
        public GetSampleById(int sampleId, int reqCode) {
            this.sampleId = sampleId;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mSample = appDatabase.sampleDao().getSampleById(sampleId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(gson.toJson(mSample));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
        }
    }

    public class GetAllAction extends AsyncTask<Void, Void, Void>{
        int reqCode;
        List<SampleEntity> sampleEntityList;
        public GetAllAction(int reqCode) {
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sampleEntityList = appDatabase.sampleDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonArray = new JSONArray(gson.toJson(sampleEntityList));
                jsonObject.put("data", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
        }
    }


}
