package com.anova.indiaadmin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.anova.indiaadmin.App;

/**
 * Created by iqbal on 23/4/18.
 */

public class PreferenceHelper {
    public static final String PREFS_NAME = Constants.SHARED_PREFERENCE_NAME;

    private static SharedPreferences getSharedPreferences() {
        return App.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    public static boolean contains(String key){
        return getSharedPreferences().contains(key);
    }

    public static void putString(String key, String value) {
        getEditor().putString(key, value).commit();
    }

    public static String getString(String key) {
        return getSharedPreferences().getString(key, "");
    }

    public static String getString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }

    public static void putInteger(String key, int value) {
        getEditor().putInt(key, value).commit();
    }

    public static int getInteger(String key) {
        return getSharedPreferences().getInt(key, 0);
    }

    public static int getInteger(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return getSharedPreferences().getBoolean(key, defValue);
    }

    public static void putFloat(String key, float value) {
        getEditor().putFloat(key, value).commit();
    }

    public static float getFloat(String key) {
        return getSharedPreferences().getFloat(key, 0.0f);
    }

    public static float getFloat(String key, float defValue) {
        return getSharedPreferences().getFloat(key, defValue);
    }

    public static void putLong(String key, long value) {
        getEditor().putLong(key, value).commit();
    }

    public static long getLong(String key) {
        return getSharedPreferences().getLong(key, 0);
    }

    public static long getLong(String key, long defValue) {
        return getSharedPreferences().getLong(key, defValue);
    }

    public static void putDouble(String key, double value) {
        getEditor().putLong(key, Double.doubleToLongBits(value)).commit();
    }

    public static double getDouble(String key) {
        return Double.longBitsToDouble(getSharedPreferences().getLong(key, 0));
    }

    public static double getDouble(String key, double defValue) {
        return Double.longBitsToDouble(getSharedPreferences().getLong(key, Double.doubleToLongBits(defValue)));
    }

    public static void clearValue(String key) {
        getEditor().remove(key).commit();
    }

    public static void clearSharedPreferences() {
        getEditor().clear().commit();
    }
}
