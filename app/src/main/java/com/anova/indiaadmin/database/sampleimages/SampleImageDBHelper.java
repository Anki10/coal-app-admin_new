package com.anova.indiaadmin.database.sampleimages;

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

public class SampleImageDBHelper {
    private AppDatabase appDatabase;
    private AppDatabaseResponse appDatabaseResponse;

    public SampleImageDBHelper(AppDatabase appDatabase, AppDatabaseResponse appDatabaseResponse) {
        this.appDatabase = appDatabase;
        this.appDatabaseResponse = appDatabaseResponse;
    }

    public void insertSampleImage(SampleImageEntity sampleImageEntity, int reqCode){
        new InsertAction(sampleImageEntity, reqCode).execute();
    }

    public void insertAll(List<SampleImageEntity> sampleImageEntityList, int reqCode) {
        new InsertAllAction(sampleImageEntityList, reqCode).execute();
    }

    public void getSampleImageByLocalSampleId(int sampleId, int reqCode){
        new GetSampleImagesByLocalSampleId(sampleId, reqCode).execute();
    }

    public void deleteSampleImageByLocalSampleId(int sampleId, int reqCode){
        new DeleteSampleImagesByLocalSampleId(sampleId,reqCode).execute();
    }

    public void updateSampleImage(SampleImageEntity sampleImageEntity, int reqCode){
        new UpdateAction(sampleImageEntity, reqCode).execute();
    }

    public class InsertAllAction extends AsyncTask<Void, Void, Void> {
        List<SampleImageEntity> sampleImageEntityList;
        int reqCode;
        public InsertAllAction(List<SampleImageEntity> sampleImageEntityList, int reqCode) {
            this.sampleImageEntityList = sampleImageEntityList;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.sampleImageDao().insertAll(sampleImageEntityList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class InsertAction extends AsyncTask<Void, Void, Void>{
        SampleImageEntity sampleImageEntity;
        int reqCode;
        public InsertAction(SampleImageEntity sampleImageEntity, int reqCode) {
            this.sampleImageEntity = sampleImageEntity;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.sampleImageDao().insert(sampleImageEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class UpdateAction extends AsyncTask<Void, Void, Void>{
        SampleImageEntity sampleImageEntity;
        int reqCode;
        public UpdateAction(SampleImageEntity sampleImageEntity, int reqCode) {
            this.sampleImageEntity = sampleImageEntity;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.sampleImageDao().update(sampleImageEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class DeleteSampleImagesByLocalSampleId extends AsyncTask<Void, Void, Void>{
        int sampleId;
        int reqCode;
        public DeleteSampleImagesByLocalSampleId(int sampleId, int reqCode) {
            this.sampleId = sampleId;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.sampleImageDao().deleteByLocalSampleId(sampleId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class GetSampleImagesByLocalSampleId extends AsyncTask<Void, Void, Void>{
        int sampleId;
        int reqCode;
        List<SampleImageEntity> sampleImageEntityList;
        public GetSampleImagesByLocalSampleId(int sampleId, int reqCode) {
            this.sampleId = sampleId;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            sampleImageEntityList = appDatabase.sampleImageDao().getAllByLocalSampleId(sampleId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonArray = new JSONArray(gson.toJson(sampleImageEntityList));
                jsonObject.put("data", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
        }
    }
}
