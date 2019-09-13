package com.anova.indiaadmin.database.preparations;

import android.os.AsyncTask;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreparationDbHelper {
    private AppDatabase appDatabase;
    private AppDatabaseResponse appDatabaseResponse;
    private String subsidiary, area, location;

    public PreparationDbHelper(AppDatabase appDatabase, AppDatabaseResponse appDatabaseResponse, String subsidiary, String area, String location) {
        this.appDatabase = appDatabase;
        this.appDatabaseResponse = appDatabaseResponse;
        this.subsidiary = subsidiary;
        this.area = area;
        this.location = location;
    }

    public void syncReceivedPrepSamples(JSONArray prepSamplesList, int reqCode){
        new SyncReceivedPrepSamples(prepSamplesList, reqCode).execute();
    }

    public void getFilteredPrepSample(int reqCode){
        new GetFilteredPrepSample(reqCode).execute();
    }

    public void getPrepSampleByLocalPrepId(int reqCode, int prepId){
        new GetPrepSampleByLocalPrepId(reqCode, prepId).execute();
    }

    public void markModified(int modificationCount, int prepId, int reqCode){
        new MarkModified(modificationCount, prepId, reqCode).execute();
    }

    public void deletePrepItemImageQrData(int prepId, int reqCode){
        new DeletePrepItemImageQRData(prepId, reqCode).execute();
    }

    public class DeletePrepItemImageQRData extends AsyncTask<Void, Void, Void>{
        private int prepId, reqCode;

        public DeletePrepItemImageQRData(int prepId, int reqCode) {
            this.prepId = prepId;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.preparationDao().deleteByPLocalPrepId(prepId);
            appDatabase.preparationImageQrDao().deleteByPrepId(prepId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
            super.onPostExecute(aVoid);
        }
    }

    public class MarkModified extends AsyncTask<Void, Void, Void>{
        private int modificationCount;
        private int prepId;
        private int reqCode;

        public MarkModified(int modificationCount, int prepId, int reqCode) {
            this.modificationCount = modificationCount;
            this.prepId = prepId;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.preparationDao().markModified(prepId, modificationCount);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class GetPrepSampleByLocalPrepId extends AsyncTask<Void, Void, Void>{
        int reqCode;
        int prepId;
        private PreparationEntity preparationEntity;

        public GetPrepSampleByLocalPrepId(int reqCode, int prepId) {
            this.reqCode = reqCode;
            this.prepId = prepId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            preparationEntity = appDatabase.preparationDao().getPrepSampleByLocalPrepId(prepId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(gson.toJson(preparationEntity));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
        }
    }

    public class GetFilteredPrepSample extends AsyncTask<Void, Void, Void>{
        int reqCode;
        private List<PreparationEntity> preparationEntityList;

        public GetFilteredPrepSample(int reqCode) {
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            preparationEntityList = appDatabase.preparationDao().getFilteredPrepSample(subsidiary, area, location);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonArray = new JSONArray(gson.toJson(preparationEntityList));
                jsonObject.put("data", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
        }
    }

    public class SyncReceivedPrepSamples extends AsyncTask<Void, Void, Void>{
        JSONArray prepSamplesList;
        Map<Integer, PreparationEntity> prepServerHashMap, prepLocalHashMap;
        int reqCode;

        public SyncReceivedPrepSamples(JSONArray prepSamplesList, int reqCode) {
            this.prepSamplesList = prepSamplesList;
            this.reqCode = reqCode;
            prepServerHashMap = new HashMap<Integer, PreparationEntity>();
            prepLocalHashMap = new HashMap<Integer, PreparationEntity>();
            try {
                if (prepSamplesList != null) {
                    for (int i = 0; i < prepSamplesList.length(); i++) {
                        JSONObject prepSampleJSON = prepSamplesList.getJSONObject(i);
                        int prepId = Integer.parseInt(prepSampleJSON.getString("sample_id"));
                        PreparationEntity preparationEntity = new PreparationEntity(prepId,subsidiary, area, location, prepSampleJSON.toString(), 0);
                        prepServerHashMap.put(prepId, preparationEntity);
                    }
                }
            } catch (JSONException jsonException){
                jsonException.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            List<PreparationEntity> prepEntityList = appDatabase.preparationDao().getFilteredPrepSample(subsidiary, area, location);
            for(PreparationEntity preparationEntity : prepEntityList){
                if(!prepServerHashMap.containsKey(preparationEntity.getPrepId())){
                    appDatabase.preparationDao().deleteByPLocalPrepId(preparationEntity.getPrepId());
                } else{
                    prepLocalHashMap.put(preparationEntity.getPrepId(), preparationEntity);
                }
            }
            for(Map.Entry<Integer, PreparationEntity> entry : prepServerHashMap.entrySet()){
                if(prepLocalHashMap.containsKey(entry.getKey())){
                    appDatabase.preparationDao().updatePrepSampleJson(entry.getKey(), entry.getValue().getMyJsonData());
                } else {
                    appDatabase.preparationDao().insert(entry.getValue());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }
}
