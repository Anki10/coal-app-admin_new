package com.anova.indiaadmin;



import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.devs.acr.AutoErrorReporter;
import com.facebook.stetho.Stetho;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import io.fabric.sdk.android.Fabric;

public class App extends Application {

    private RequestQueue mRequestQueue;
    private final String TAG = this.getClass().getSimpleName();
    public static App app;
    public String sessionID;
    private static Context mContext;

   /* public static App getInstance(){
        return app;
    }*/

    @Override
    public void onCreate(){
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        this.app =this;
        mContext = getApplicationContext();

        //Stetho initialization - Facebook debugger
        Stetho.initializeWithDefaults(this);

        //Logger initialization
        Logger.addLogAdapter(new AndroidLogAdapter(){
            @Override public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });

        //ErrorReporting initialization and setup
        if(!BuildConfig.DEBUG) {
            AutoErrorReporter.get(this)
                    .setEmailAddresses("")
                    .setEmailSubject("")
                    .start();
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public static App get(Context context){
        return (App) context.getApplicationContext();
    }


    public static synchronized App getInstance() {
        return app;
    }

}