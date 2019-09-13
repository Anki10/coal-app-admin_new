package com.anova.indiaadmin.database.subsidiary;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by iqbal on 2/5/18.
 */

public class SubsidiaryHelper {

    public static class ManageSubsidiaries extends AsyncTask<Void, Void, Void>{

        private final AppDatabase mDb;
        private final List<Subsidiary> mSubsidiaryList;
        private List<Subsidiary> getSubsidiaryList;
        private final int mAction;
        private AppDatabaseResponse mDbResponse;
        private final int mRequestCode;
        public ManageSubsidiaries(AppDatabase db, AppDatabaseResponse dbResponse, int requestCode, @Nullable List<Subsidiary> subsidiaryList, @NonNull int action) {
            mDb = db;
            mSubsidiaryList = subsidiaryList;
            mAction = action;
            mDbResponse = dbResponse;
            mRequestCode = requestCode;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            switch (mAction){
                case AppDatabase.INSERT_ACTION:
                    mDb.subsidiaryDao().insertAll(mSubsidiaryList);
                    break;

                case AppDatabase.DELETE_ALL_ACTION:
                    mDb.subsidiaryDao().deleteAll();
                    break;

                case AppDatabase.SELECT_ACTION:
                    getSubsidiaryList = mDb.subsidiaryDao().getAll();
                    break;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Logger.i("ManageSubsidiaries action complete");
            switch (mAction){
                case AppDatabase.INSERT_ACTION:
                    mDbResponse.onDatabaseResSuccess(null, mRequestCode);
                    break;

                case AppDatabase.DELETE_ALL_ACTION:
                    mDbResponse.onDatabaseResSuccess(null, mRequestCode);
                    break;

                case AppDatabase.SELECT_ACTION:
                    Gson gson = new Gson();
                    JSONArray jsonArray = null;
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonArray = new JSONArray(gson.toJson(getSubsidiaryList));
                        jsonObject.put("data", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mDbResponse.onDatabaseResSuccess(jsonObject, mRequestCode);
                    break;
            }
        }
    }
}
