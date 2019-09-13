package com.anova.indiaadmin.database.customers;

import android.os.AsyncTask;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by iqbal on 7/5/18.
 */

public class CustomerDBHelper {
    private AppDatabase appDatabase;
    private AppDatabaseResponse appDatabaseResponse;

    public CustomerDBHelper(AppDatabase appDatabase, AppDatabaseResponse appDatabaseResponse) {
        this.appDatabase = appDatabase;
        this.appDatabaseResponse = appDatabaseResponse;
    }

    public void insertAllCustomers(List<CustomerEntity> customerEntityList, int reqCode){
        new InsertAllAction(customerEntityList, reqCode).execute();
    }

    public void getAllCustomers(int reqCode){
        new GetAllAction(reqCode).execute();
    }

    public void getFilteredCustomers(int reqCode, String subsidiary, String auction_type){
        new GetFiltered(reqCode, subsidiary, auction_type).execute();
    }

    public void deleteAllCustomers(int reqCode){
        new DeleteAllAction(reqCode).execute();
    }


    public class InsertAllAction extends AsyncTask<Void, Void, Void> {
        List<CustomerEntity> customerEntityList;
        int reqCode;
        public InsertAllAction(List<CustomerEntity> customerEntityList, int reqCode) {
            this.customerEntityList = customerEntityList;
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.customerDao().insertAll(customerEntityList);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

    public class GetFiltered extends AsyncTask<Void, Void, Void>{
        int reqCode;
        String subsidiary,auction_type; ;
        List<CustomerEntity> customerEntityList;
        public GetFiltered(int reqCode, String subsidiary, String auction_type) {
            this.reqCode = reqCode;
            this.subsidiary = subsidiary;
            this.auction_type = auction_type;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            customerEntityList = appDatabase.customerDao().getFiltered(subsidiary,auction_type);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonArray = new JSONArray(gson.toJson(customerEntityList));
                jsonObject.put("data", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
        }
    }

    public class GetAllAction extends AsyncTask<Void, Void, Void>{
        int reqCode;
        List<CustomerEntity> customerEntityList;
        public GetAllAction(int reqCode) {
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            customerEntityList = appDatabase.customerDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Gson gson = new Gson();
            JSONArray jsonArray = null;
            JSONObject jsonObject = new JSONObject();
            try {
                jsonArray = new JSONArray(gson.toJson(customerEntityList));
                jsonObject.put("data", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            appDatabaseResponse.onDatabaseResSuccess(jsonObject, reqCode);
        }
    }

    public class DeleteAllAction extends AsyncTask<Void, Void, Void> {
        int reqCode;
        public DeleteAllAction(int reqCode) {
            this.reqCode = reqCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.customerDao().deleteAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            appDatabaseResponse.onDatabaseResSuccess(null, reqCode);
        }
    }

}
