package com.anova.indiaadmin.network;

import android.content.Context;
import android.os.AsyncTask;

import com.orhanobut.logger.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by iqbal on 12/5/18.
 */

public class ImageUploaderAsync extends AsyncTask<Void, Void, Void> {
    private String apiLink;
    private String imagePath;
    private Context context;
    private AppNetworkResponse appNetworkResponse;
    private int reqCode;
    private JSONObject finalResult;
    private String errorMessage;

    public ImageUploaderAsync(String apiLink, String imagePath, Context context, AppNetworkResponse appNetworkResponse, int reqCode) {
        this.apiLink = apiLink;
        this.imagePath = imagePath;
        this.context = context;
        this.appNetworkResponse = appNetworkResponse;
        this.reqCode = reqCode;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(apiLink);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        /* example for setting a HttpMultipartMode */
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        /* example for adding an image part */
        FileBody fileBody = new FileBody(new File(imagePath)); //image should be a String
        builder.addPart("file", fileBody);
        builder.addTextBody("latitude","45.576");
        builder.addTextBody("longitude","34.576");
        HttpEntity entity = builder.build();

        httppost.setEntity(entity);
        try {
            HttpResponse response = httpclient.execute(httppost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String json = reader.readLine();
            JSONTokener tokener = new JSONTokener(json);

            finalResult = new JSONObject(tokener);
            Logger.e("response : " + finalResult.toString());


        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        } catch (JSONException e) {
            e.printStackTrace();
            errorMessage = e.getMessage();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(finalResult!=null && errorMessage == null) {
            appNetworkResponse.onResSuccess(finalResult, reqCode);
        } else {
            appNetworkResponse.onResFailure("1234","Couldn't upload Image File : " + errorMessage, reqCode, null);
        }
    }
}
