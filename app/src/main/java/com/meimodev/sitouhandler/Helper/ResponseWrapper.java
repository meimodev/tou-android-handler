package com.meimodev.sitouhandler.Helper;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseWrapper {
    private static final String TAG = "ResponseWrapper: ";
    private Boolean error;
    private String message;
    private JSONObject data;


    public ResponseWrapper(JSONObject obj) {
        try {
            error = obj.getBoolean("error");
            message = obj.getString("message");

            if (!error)
                data = obj.getJSONObject("data");


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Constructor -> the response doesn't match wrapper :: " + e.getMessage(), e);
        }
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public JSONObject getData() {
        return data;
    }
}
