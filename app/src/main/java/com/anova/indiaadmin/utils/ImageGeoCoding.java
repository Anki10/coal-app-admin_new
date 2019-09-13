package com.anova.indiaadmin.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;

import com.anova.indiaadmin.network.AppNetworkResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageGeoCoding extends AsyncTask<Void, Void, Void>{
    private String date, lat, lon, fileName;
    private Context context;
    private AppNetworkResponse appNetworkResponse;
    private int reqCode;

    public ImageGeoCoding(String fileName, String date, String lat, String lon, Context context, AppNetworkResponse appNetworkResponse, int reqCode) {
        this.fileName = fileName;
        this.date = date;
        this.lat = lat;
        this.lon = lon;
        this.context = context;
        this.appNetworkResponse = appNetworkResponse;
        this.reqCode = reqCode;
    }

    public static Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File imgFile = new File(fileName);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;

//        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), bmOptions);
        Bitmap bitmap = decodeSampledBitmapFromFile(imgFile, 300, 300);
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            Canvas canvas1 = new Canvas(mutableBitmap);
            String date_string="Date: " +date;
            String latitude_val="Lat: " +lat;
            String longitude_val="Long: " +lon;
            Paint paint= new Paint();
            paint.setColor(Color.YELLOW);
            paint.setTextSize(20);
            paint.setTextAlign(Paint.Align.CENTER);

            canvas1.drawText(date_string,150 ,30, paint);
            canvas1.drawText(latitude_val,150,55,paint);
            canvas1.drawText(longitude_val,150,80,paint);

            mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        appNetworkResponse.onResSuccess(null,reqCode);
    }
}
