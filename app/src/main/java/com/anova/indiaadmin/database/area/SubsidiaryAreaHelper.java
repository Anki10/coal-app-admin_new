package com.anova.indiaadmin.database.area;

import android.os.AsyncTask;

import com.anova.indiaadmin.database.AppDatabase;
import com.anova.indiaadmin.database.AppDatabaseResponse;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by iqbal on 3/5/18.
 */

public class SubsidiaryAreaHelper {

    public static final int SELECT_BY_SUBSIDIARY_ACTION = 21;

    public static class ManageSubsidiaryArea extends AsyncTask<Void, Void, Void>{

        private final AppDatabase mDb;
        private final List<Area> mAreaList;
        private List<Area> getAreaList;
        private final int mAction;
        private AppDatabaseResponse mDbResponse;
        private final int mRequestCode;
        private final int mSubsidiaryId;
        private List<Area> getAreasListBySubsidiaryId;

        public ManageSubsidiaryArea(AppDatabase db, AppDatabaseResponse dbResponse, int requestCode, @Nullable List<Area> areaList, int subsidiaryId, int action) {
            mDb = db;
            mAreaList = areaList;
            mAction = action;
            mDbResponse = dbResponse;
            mRequestCode = requestCode;
            mSubsidiaryId = subsidiaryId;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            switch (mAction){
                case AppDatabase.INSERT_ACTION:
                    mDb.areaDao().insertAll(mAreaList);
                    break;

                case SELECT_BY_SUBSIDIARY_ACTION:
                    getAreasListBySubsidiaryId = mDb.areaDao().getAreasBySubsidiaryId(mSubsidiaryId);
                    break;

                case AppDatabase.DELETE_ALL_ACTION:
                    mDb.areaDao().deleteAll();
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch (mAction){
                case AppDatabase.INSERT_ACTION:
                    mDbResponse.onDatabaseResSuccess(null, mRequestCode);
                    break;

                case SELECT_BY_SUBSIDIARY_ACTION:
                    Gson gson = new Gson();
                    JSONArray jsonArray = null;
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonArray = new JSONArray(gson.toJson(getAreasListBySubsidiaryId));
                        jsonObject.put("data", jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mDbResponse.onDatabaseResSuccess(jsonObject, mRequestCode);
                    break;

                case AppDatabase.DELETE_ALL_ACTION:
                    mDbResponse.onDatabaseResSuccess(null, mRequestCode);
                    break;
            }
        }
    }
}
