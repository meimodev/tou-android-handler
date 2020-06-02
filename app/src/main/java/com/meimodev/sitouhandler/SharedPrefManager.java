/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.squti.guru.Guru;

import java.util.Map;


public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private static final String MISSING_VALUE = "Data Is Missing!";

    private static final String SHARED_PREF_NAME = "mysharedpref12";

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public boolean saveUserData(int userId, String fullName,
                                String age, String sex, String accessToken
    ) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(Constant.KEY_USER_AGE, age);
        editor.putString(Constant.KEY_USER_SEX, sex);
        editor.putInt(Constant.KEY_USER_ID, userId);
        editor.putString(Constant.KEY_USER_FULL_NAME, fullName);
        editor.putString(Constant.KEY_USER_ACCESS_TOKEN, accessToken);

        editor.apply();
        return true;
    }

    public boolean saveMemberData(int memberId, String churchPosition, String BIPRA
    ) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(Constant.KEY_MEMBER_BIPRA, BIPRA);
        editor.putInt(Constant.KEY_MEMBER_ID, memberId);
        editor.putString(Constant.KEY_MEMBER_CHURCH_POSITION, churchPosition);

        editor.apply();
        return true;
    }

    public boolean saveColumnData(int columnId, String memberColumnNameIndex
    ) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putInt(Constant.KEY_COLUMN_ID, columnId);
        editor.putString(Constant.KEY_COLUMN_NAME_INDEX, memberColumnNameIndex);

        editor.apply();
        return true;
    }

    public boolean saveChurchData(int churchId, String churchName, String churchKelurahan
    ) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putInt(Constant.KEY_CHURCH_ID, churchId);
        editor.putString(Constant.KEY_CHURCH_NAME, churchName);
        editor.putString(Constant.KEY_CHURCH_KELURAHAN, churchKelurahan);

        editor.apply();
        return true;
    }

    public void saveData(String key, Object data) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if (data instanceof String) editor.putString(key, ((String) data));
        else if (data instanceof Integer) editor.putInt(key, ((Integer) data));
        else if (data instanceof Boolean) editor.putBoolean(key, ((Boolean) data));
        else if (data instanceof Float) editor.putFloat(key, ((Float) data));
        else if (data instanceof Long) editor.putLong(key, ((Long) data));
        else {
            throw new IllegalArgumentException("Object not supported! supported only String, Integer, Boolean, Float, Long");
        }

        editor.apply();
    }

    public static void saveData(Context context, String key, Object data) {
        SharedPrefManager.getInstance(context).saveData(key, data);
    }

    public boolean isLoggedIn() {
        return Guru.getInt(Constant.KEY_USER_ID, 0) != 0;
    }

    public boolean logout() {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public Object loadSharedData(String key) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        Map<String, ?> allEntries = sharedpreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }

        return sharedpreferences.getAll().get(key);
    }

    public Object loadSharedData(String key, Object defaultValue) {
        return loadSharedData(key) == null ? defaultValue : loadSharedData(key);
    }


    public static Object load(Context context, String key) {
        return SharedPrefManager.getInstance(context).loadSharedData(key);
    }

    public static Object load(Context context, String key, Object defaultValue) {
        Object obj = SharedPrefManager.getInstance(context).loadSharedData(key);
        return obj == null ? defaultValue : obj;
    }

}


