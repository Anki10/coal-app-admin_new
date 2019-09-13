package com.anova.indiaadmin.database.preparationimages;

import android.os.AsyncTask;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class PreparationImageQrHelper {
    private AppDatabase appDatabase;
    private AppDatabaseResponse appDatabaseResponse;
    private int prepId;

    public PreparationImageQrHelper(AppDatabase appDatabase, AppDatabaseResponse appDatabaseResponse, int prepId) {
        this.appDatabase = appDatabase;
        this.appDatabaseResponse = appDatabaseResponse;
        this.prepId = prepId;
    }

    public void insertAction(int reqCode, PreparationImageQrEntity preparationImageQrEntity){
        new InsertAction(reqCode, preparationImageQrEntity).execute();
    }

    public void updateImages(int reqCode, String image4, String image5){
        new UpdateImages(reqCode, image4, image5).execute();
    }

    public void updateQrs(int reqCode, String qciQr, String subsidiaryQr, String customerQr, String refreeQr){
        new UpdateQrs(reqCode, qciQr, subsidiaryQr, customerQr, refreeQr).execute();
    }

    public void deleteAction(int reqCode,int prepId){
        new DeleteAction(reqCode,prepId).execute();
    }

    public void getPrepImageQr(int reqCode,int prep_id){
        new GetPrepImageQr(reqCode,prep_id).execute();
    }

    private class GetPrepImageQr extends AsyncTask<Void, Void, Void>{

        private int reqCode;
        int prep_id;
        private PreparationImageQrEntity prepImageQrEntity;

        public GetPrepImageQr(int reqCode,int prepID) {
            this.reqCode = reqCode;
            this.prep_id = prepID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            prepImageQrEntity = appDatabase.preparationImageQrDao().getPrepImageQrByPrepId(prep_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Gson gson = new Gson();
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(gson.toJson(prepImageQrEntity));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
        }
    }

    private class DeleteAction extends AsyncTask<Void, Void, Void>{
        int reqCode;
        int prep_id;
        public DeleteAction(int reqCode,int prepId) {
            this.reqCode = reqCode;
            this.prep_id = prepId;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.preparationImageQrDao().deleteByPrepId(prep_id);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    private class UpdateQrs extends AsyncTask<Void, Void, Void>{
        private int reqCode;
        private String qciQr, subsidiaryQr, customerQr, refreeQr;

        public UpdateQrs(int reqCode, String qciQr, String subsidiaryQr, String customerQr, String refreeQr) {
            this.reqCode = reqCode;
            this.qciQr = qciQr;
            this.subsidiaryQr = subsidiaryQr;
            this.customerQr = customerQr;
            this.refreeQr = refreeQr;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                appDatabase.preparationImageQrDao().updateQrs(prepId, qciQr, subsidiaryQr, customerQr, refreeQr);
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    private class UpdateImages extends AsyncTask<Void, Void, Void>{
        private int reqCode;
        private String image4, image5;

        public UpdateImages(int reqCode, String image4, String image5) {
            this.reqCode = reqCode;
            this.image4 = image4;
            this.image5 = image5;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.preparationImageQrDao().updateImages(prepId, image4, image5);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    private class InsertAction extends AsyncTask<Void, Void, Void>{

        private int reqCode;
        private PreparationImageQrEntity preparationImageQrEntity;

        public InsertAction(int reqCode, PreparationImageQrEntity preparationImageQrEntity) {
            this.reqCode = reqCode;
            this.preparationImageQrEntity = preparationImageQrEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.preparationImageQrDao().insert(preparationImageQrEntity);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }
}
