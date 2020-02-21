package com.meimodev.sitouhandler;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Map;


public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private static final String MISSING_VALUE = "Data Is Missing!";

    private static final String SHARED_PREF_NAME = "mysharedpref12";

    public static final String KEY_USER_ID = "User_ID";
    public static final String KEY_USER_FULL_NAME = "User_Full_Name";
    public static final String KEY_USER_AGE = "User_Age";
    public static final String KEY_USER_SEX = "User_Sex";
    public static final String KEY_USER_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String KEY_MEMBER_ID = "Member_ID";
    public static final String KEY_MEMBER_BIPRA = "Member_BIPRA";
    public static final String KEY_CHURCH_ID = "Church_Id";
    public static final String KEY_CHURCH_NAME = "Church_Name";
    public static final String KEY_CHURCH_VILLAGE = "Church_Village";
    public static final String KEY_CHURCH_POSITION = "Church_Position";
    public static final String KEY_COLUMN_ID = "Column_Id";
    public static final String KEY_COLUMN_NAME_INDEX = "Column_Name_Index";

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

        editor.putString(KEY_USER_AGE, age);
        editor.putString(KEY_USER_SEX, sex);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_FULL_NAME, fullName);
        editor.putString(KEY_USER_ACCESS_TOKEN, accessToken);

        editor.apply();
        return true;
    }

    public boolean saveMemberData(int memberId, String churchPosition, String BIPRA
    ) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString(KEY_MEMBER_BIPRA, BIPRA);
        editor.putInt(KEY_MEMBER_ID, memberId);
        editor.putString(KEY_CHURCH_POSITION, churchPosition);

        editor.apply();
        return true;
    }

    public boolean saveColumnData(int columnId, String memberColumnNameIndex
    ) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putInt(KEY_COLUMN_ID, columnId);
        editor.putString(KEY_COLUMN_NAME_INDEX, memberColumnNameIndex);

        editor.apply();
        return true;
    }

    public boolean saveChurchData(int churchId,String churchName, String churchKelurahan
    ) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putInt(KEY_CHURCH_ID, churchId);
        editor.putString(KEY_CHURCH_NAME, churchName);
        editor.putString(KEY_CHURCH_VILLAGE, churchKelurahan);

        editor.apply();
        return true;
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        if (sharedpreferences.getInt(KEY_USER_ID, 0) != 0) {
            return true;
        }
        return false;
    }

    public boolean logout() {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public Object loadUserData(String key) {
        SharedPreferences sharedpreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);


        Map<String, ?> allEntries = sharedpreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
        }

        return sharedpreferences.getAll().get(key);

    }

    public static Object load(Context context, String key) {
        return SharedPrefManager.getInstance(context).loadUserData(key);
    }

}


