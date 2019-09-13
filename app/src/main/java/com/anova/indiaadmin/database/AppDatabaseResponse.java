package com.anova.indiaadmin.database;

import org.json.JSONObject;

/**
 * Created by iqbal on 3/5/18.
 */

public interface AppDatabaseResponse {
    /**
     * On res success.
     *
     * @param jsonObject the json object
     * @param reqCode    the req code
     */
    public void onDatabaseResSuccess(JSONObject jsonObject, int reqCode);


}
