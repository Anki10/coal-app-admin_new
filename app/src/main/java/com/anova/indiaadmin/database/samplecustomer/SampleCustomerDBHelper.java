package com.anova.indiaadmin.database.samplecustomer;

import android.os.AsyncTask;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by iqbal on 9/5/18.
 */

public class SampleCustomerDBHelper {
    private AppDatabase appDatabase;
    private AppDatabaseResponse appDatabaseResponse;

    public SampleCustomerDBHelper(AppDatabase appDatabase, AppDatabaseResponse appDatabaseResponse) {
        this.appDatabase = appDatabase;
        this.appDatabaseResponse = appDatabaseResponse;
    }

    public void insertSample(SampleCustomerEntity sampleCustomerEntity, int reqCode){
        new InsertAction(sampleCustomerEntity, reqCode).execute();
    }

    public void insertAll(List<SampleCustomerEntity> sampleCustomerEntityList, int reqCode) {
        new InsertAllAction(sampleCustomerEntityList, reqCode).execute();
    }

    public void updateAll(SampleCustomerEntity sampleCustomerEntity, int reqCode){
        new UpdateAllAction(sampleCustomerEntity, reqCode).execute();
    }

    public void getSamplCustomerByLocalSampleId(int sampleId, int reqCode){
        new GetSampleCustomerByLocalSampleId(sampleId, reqCode).execute();
    }

    public void deleteSampleCustomersByLocalSampleId(int sampleId, int reqCode){
        new DeleteSampleCustomersByLocalSampleId(sampleId,reqCode).execute();
    }

    public class UpdateAllAction extends AsyncTask<Void,Void,Void>{
        SampleCustomerEntity sampleCustomerEntity;
        int reqCode;

        public UpdateAllAction(SampleCustomerEntity sampleCustomerEntity, int reqCode){
            this.sampleCustomerEntity = sampleCustomerEntity;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {

                SampleCustomerEntity pojo = sampleCustomerEntity;
                appDatabase.sampleCustomerDao().updateList(pojo.getAuction_type(),pojo.getCustomerId(),pojo.getCustomerName(),pojo.getLiftingType(),pojo.getMore_details(),pojo.getDeclaredGrade(),pojo.getTotalVehicles(),pojo.getTotalVehiclesSampled(),pojo.getId());


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);

        }
    }

    public class InsertAllAction extends AsyncTask<Void, Void, Void> {
        List<SampleCustomerEntity> sampleCustomerEntityList;
        int reqCode;
        public InsertAllAction(List<SampleCustomerEntity> sampleCustomerEntityList, int reqCode) {
            this.sampleCustomerEntityList = sampleCustomerEntityList;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.sampleCustomerDao().insertAll(sampleCustomerEntityList);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class InsertAction extends AsyncTask<Void, Void, Void>{
        SampleCustomerEntity sampleCustomerEntity;
        int reqCode;
        Long sampleCustomerId;
        public InsertAction(SampleCustomerEntity sampleCustomerEntity, int reqCode) {
            this.sampleCustomerEntity = sampleCustomerEntity;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.sampleCustomerDao().insert(sampleCustomerEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class GetSampleCustomerByLocalSampleId extends AsyncTask<Void, Void, Void>{
        int sampleId;
        int reqCode;
        List<SampleCustomerEntity> mSample;
        public GetSampleCustomerByLocalSampleId(int sampleId, int reqCode) {
            this.sampleId = sampleId;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mSample = appDatabase.sampleCustomerDao().getOneSampleCustomerByLocalSampleId(sampleId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonArray = new JSONArray(gson.toJson(mSample));
                jsonObject.put("data", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
        }
    }

    public class DeleteSampleCustomersByLocalSampleId extends AsyncTask<Void, Void, Void>{
        int sampleId;
        int reqCode;
        public DeleteSampleCustomersByLocalSampleId(int sampleId, int reqCode) {
            this.sampleId = sampleId;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.sampleCustomerDao().deleteByLocalSampleId(sampleId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

}
