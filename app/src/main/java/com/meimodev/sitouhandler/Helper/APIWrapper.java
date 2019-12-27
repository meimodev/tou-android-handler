package com.meimodev.sitouhandler.Helper;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIWrapper {
    private boolean error;
    private String message;
    private JSONObject data;
    private JSONArray dataArray;

    public APIWrapper (){}


    public String getMessage() {
        return message;
    }

    public JSONObject getData() {
        return data;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public JSONArray getDataArray() {
        return dataArray;
    }

    public void setDataArray(JSONArray dataArray) {
        this.dataArray = dataArray;
    }
}
