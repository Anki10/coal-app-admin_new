package com.anova.indiaadmin.database.location;

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
 * Created by iqbal on 6/5/18.
 */

public class AreaLocationHelper {

    public static final int SELECT_BY_AREA_ACTION = 31;

    public static class ManageAreaLocation extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private final List<Location> mLocationList;
        private List<Location> getLocationList;
        private final int mAction;
        private AppDatabaseResponse mDbResponse;
        private final int mRequestCode;
        private final int mAreaId;
        public ManageAreaLocation(AppDatabase db, AppDatabaseResponse dbResponse, int requestCode, @Nullable List<Location> locationList, int areaId, int action) {
            mDb = db;
            mLocationList = locationList;
            mAction = action;
            mDbResponse = dbResponse;
            mRequestCode = requestCode;
            mAreaId = areaId;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            switch (mAction){
                case AppDatabase.INSERT_ACTION:
                    mDb.locationDao().insertAll(mLocationList);
                    break;

                case SELECT_BY_AREA_ACTION:
                    getLocationList = mDb.locationDao().getLocationByArea(mAreaId);
                    break;

                case AppDatabase.DELETE_ALL_ACTION:
                    mDb.locationDao().deleteAll();
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

                case SELECT_BY_AREA_ACTION:
                    Gson gson = new Gson();
                    JSONArray jsonArray = null;
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonArray = new JSONArray(gson.toJson(getLocationList));
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
