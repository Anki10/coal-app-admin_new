package com.anova.indiaadmin.network;

import org.json.JSONObject;

public interface AppNetworkResponse {
    /**
     * On res success.
     *
     * @param jsonObject the json object
     * @param reqCode    the req code
     */
    public void onResSuccess(JSONObject jsonObject, int reqCode);


    /**
     * On res failure.
     * @param errCode the err code
     * @param errMsg  the err msg
     * @param reqCode
     * @param jsonObject
     */
    public void onResFailure(String errCode, String errMsg, int reqCode, JSONObject jsonObject);


    //void onUnauthorizedRequest();

}
