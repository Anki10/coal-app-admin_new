package com.anova.indiaadmin.network;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anova.indiaadmin.App;
import com.orhanobut.logger.Logger;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Volley {
    private static Volley instance;

    private Volley() {
    }

    static {
        instance = new Volley();
    }

    public static Volley getInstance() {
        return instance;
    }

    App app;
    // Tag used to cancel the request
    final String tag_json_obj = "tag_json_obj";
    private final String TAG = Volley.class.getSimpleName();

    private final int TIMEOUT_TIME = 30000;

    JSONObject resJsonObject;

    public void get(String url, final AppNetworkResponse networkResponse, final int reqCode) {
        Logger.d("getURL" + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        resJsonObject = response;


                        try {
                            Logger.json(response.toString());

                            if (resJsonObject.getString("success").equals(true) || resJsonObject.getString("success").equals("true"))
                                networkResponse.onResSuccess(resJsonObject, reqCode);
                            else
                                networkResponse.onResFailure("", resJsonObject.getString("data"), reqCode, resJsonObject);

                        } catch (JSONException e) {
                            networkResponse.onResFailure("", e.getMessage(), reqCode, null);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("Error: " + error.getMessage());
                checkUnAuthRequest(error);
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                networkResponse.onResFailure("", message, reqCode, null);
                // networkResponse.onResFailure(error.getMessage(), error.getMessage(), reqCode, null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return getHeader();
            }
        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_TIME,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        app.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void getSession(String url, final AppNetworkResponse networkResponse, final String session, final int reqCode) {
        Logger.d(" getUrl " + url);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        resJsonObject = response;

                        try {
                            Logger.json(response.toString());

                            if (resJsonObject.getString("success").equals(true) || resJsonObject.getString("success").equals("true"))
                                networkResponse.onResSuccess(resJsonObject, reqCode);
                            else
                                networkResponse.onResFailure("", resJsonObject.getString("data"), reqCode, resJsonObject);

                        } catch (JSONException e) {
                            networkResponse.onResFailure("", e.getMessage(), reqCode, null);
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("Error: " + error.getMessage());
                checkUnAuthRequest(error);
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                networkResponse.onResFailure("", message, reqCode, null);
                // networkResponse.onResFailure(error.getMessage(), error.getMessage(), reqCode, null);
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-IW-SESSION", session);
                return headers;
            }

        };
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_TIME,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        app.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void post(String url, final JSONObject jsonObject, final AppNetworkResponse networkResponse, final int reqCode) {
        Logger.d("Url " + url);
        Logger.json(jsonObject==null ? null : jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        resJsonObject = response;

                        try {
                            Logger.json(response.toString());

                            if (resJsonObject.getString("success").equals(true) || resJsonObject.getString("success").equals("true"))
                                networkResponse.onResSuccess(resJsonObject, reqCode);
                            else
                                networkResponse.onResFailure("", resJsonObject.getString("data"), reqCode, resJsonObject);

                        } catch (JSONException e) {
                            networkResponse.onResFailure("", e.getMessage(), reqCode, null);
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("Error: " + error.getMessage());
                checkUnAuthRequest(error);
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                networkResponse.onResFailure("", message, reqCode, null);
                //networkResponse.onResFailure("", error.getMessage(), reqCode, null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader();
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_TIME,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        app.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    Map<String, String> getHeader() {
        Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "application/json");
        return params;
    }

    public void postSession(String url, final JSONObject jsonObject, final AppNetworkResponse networkResponse, final String session, final int reqCode) {
        Logger.d("Url " + url);
        Logger.json(jsonObject==null ? null :jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        resJsonObject = response;

                        try {
                            Logger.json(response.toString());

                            if (resJsonObject.getString("success").equals(true) || resJsonObject.getString("success").equals("true"))
                                networkResponse.onResSuccess(resJsonObject, reqCode);
                            else
                                networkResponse.onResFailure("", resJsonObject.getString("data"), reqCode, resJsonObject);

                        } catch (JSONException e) {
                            networkResponse.onResFailure("", e.getMessage(), reqCode, null);
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("Error: " + error.getMessage());
                System.out.println("xxxx server"+error.getMessage());
                checkUnAuthRequest(error);
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                networkResponse.onResFailure("", message, reqCode, null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-IW-SESSION", session);
                return headers;
            }

        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_TIME,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        app.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }


    public void put(String url, final JSONObject jsonObject, final AppNetworkResponse networkResponse, final int reqCode) {
        try {
            jsonObject.put("app_id", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Logger.d("Url " + url);
        Logger.json(jsonObject==null ? null :jsonObject.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        resJsonObject = response;

                        try {
                            Logger.json(response.toString());

                            if (resJsonObject.getString("status").equalsIgnoreCase("1") || resJsonObject.getString("status").equalsIgnoreCase("success"))
                                networkResponse.onResSuccess(resJsonObject, reqCode);
                            else
                                networkResponse.onResFailure("", resJsonObject.getString("message"), reqCode, resJsonObject);

                        } catch (JSONException e) {
                            networkResponse.onResFailure("", e.getMessage(), reqCode, null);
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("Error: " + error.getMessage());
                //NetworkResponse payuResponse = error.networkResponse;
                checkUnAuthRequest(error);
                networkResponse.onResFailure("", error.getMessage(), reqCode, null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader();
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_TIME,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        app.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }

    public void delete(String url, final AppNetworkResponse networkResponse, final int reqCode) {
        Logger.d("Url " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        resJsonObject = response;

                        try {
                            Logger.json(response.toString());

                            if (resJsonObject.getString("status").equalsIgnoreCase("1") || resJsonObject.getString("status").equalsIgnoreCase("success"))
                                networkResponse.onResSuccess(resJsonObject, reqCode);
                            else
                                networkResponse.onResFailure("", resJsonObject.getString("message"), reqCode, resJsonObject);

                        } catch (JSONException e) {
                            networkResponse.onResFailure("", e.getMessage(), reqCode, null);
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("Error: " + error.getMessage());
                //NetworkResponse payuResponse = error.networkResponse;
                checkUnAuthRequest(error);
                networkResponse.onResFailure("", error.getMessage(), reqCode, null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader();
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_TIME,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        app.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }


    public void delete(String url, JSONObject jsonObject, final AppNetworkResponse networkResponse, final int reqCode) {
        Logger.d("Url : " + url);
        Logger.json(jsonObject==null ? null :jsonObject.toString());
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.DELETE,
                url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        resJsonObject = response;

                        try {
                            Logger.json(response.toString());

                            if (resJsonObject.getString("status").equalsIgnoreCase("1") || resJsonObject.getString("status").equalsIgnoreCase("success"))
                                networkResponse.onResSuccess(resJsonObject, reqCode);
                            else
                                networkResponse.onResFailure("", resJsonObject.getString("message"), reqCode, resJsonObject);

                        } catch (JSONException e) {
                            networkResponse.onResFailure("", e.getMessage(), reqCode, null);
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {


            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("Error: " + error.getMessage());
                //NetworkResponse payuResponse = error.networkResponse;
                checkUnAuthRequest(error);
                networkResponse.onResFailure("", error.getMessage(), reqCode, null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getHeader();
            }
        };

        app.getInstance().addToRequestQueue(sr, tag_json_obj);
    }


    int bytesRead, bytesAvailable, bufferSize;
    DataOutputStream dos = null;
    byte[] buffer;

    public void postMultipartData(String url, final String photoPath, Bitmap postbitmap, final AppNetworkResponse networkResponse, final int reqCode) {
        final String lineEnd = "\r\n";
        final String boundary = "apiclient-" + System.currentTimeMillis();
        final String twoHyphens = "--";
        final Bitmap bitmap;// = null;
        final byte[][] bitmapData = new byte[1][1];
        final String mimeType = "multipart/form-data;boundary=" + boundary;
        //Drawable background = ContextCompat.getDrawable(mContext, R.background.ic_menu_share);
        Logger.d("URL : " + url);
        Log.d(TAG, " Timestamp 4: " + System.currentTimeMillis() + "");
        if (postbitmap == null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(photoPath, options);
            //selected_photo.setImageBitmap(bitmap);
        } else {
            bitmap = postbitmap;
        }


        final int maxBufferSize = 1024 * 1024;

        Log.d(TAG, " Timestamp 7: " + System.currentTimeMillis() + "");

        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(Request.Method.POST, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //String str = payuResponse.toString() + "";

                resJsonObject = response;

                try {
                    Logger.json(response.toString());
                    if (resJsonObject.getString("status").equalsIgnoreCase("1") || resJsonObject.getString("status").equalsIgnoreCase("success"))
                        networkResponse.onResSuccess(resJsonObject, reqCode);
                    else
                        networkResponse.onResFailure("", resJsonObject.getString("message"), reqCode, resJsonObject);

                } catch (JSONException e) {
                    networkResponse.onResFailure("", e.getMessage(), reqCode, null);
                    e.printStackTrace();
                }

                //networkResponse.onResSuccess(resJsonObject, reqCode);

                //networkResponse.onResSuccess();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.e("Error: " + error.getMessage());
                checkUnAuthRequest(error);
                networkResponse.onResFailure("", "" + error.getMessage(), reqCode, null);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return mimeType;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "multipart/form-data");
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                dos = new DataOutputStream(bos);
                try {
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\""
                            + "image.jpg" + "\"" + lineEnd); //filename
                    //dos.writeBytes("Content-Disposition: form-hashResponse; name=\"file\";" + lineEnd);
                    dos.writeBytes(lineEnd);

                    Log.d(TAG, " Timestamp 5: " + System.currentTimeMillis() + "");
                    /*Bitmap bitmap = ((BitmapDrawable) d).getBitmap();*/
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                    bitmapData[0] = byteArrayOutputStream.toByteArray();
                    Log.d(TAG, " Timestamp 6: " + System.currentTimeMillis() + "");


                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bitmapData[0]);
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

                    // send multipart form hashResponse necesssary after file hashResponse...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    return bos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmapData[0];
            }
        };
        baseVolleyRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT_TIME,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        app.getInstance().addToRequestQueue(baseVolleyRequest);
    }

    void checkUnAuthRequest(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
            // HTTP Status Code: 401 Unauthorized
            //networkResponse.onUnauthorizedRequest();
            Intent intent = new Intent("unAuthRequest");
            intent.putExtra("key", "testKey");
            //Put your all data using put extra
            LocalBroadcastManager.getInstance(app.getInstance().getApplicationContext()).sendBroadcast(intent);
            return;
        }
    }

}
