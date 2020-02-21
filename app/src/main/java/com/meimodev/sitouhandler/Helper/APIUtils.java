package com.meimodev.sitouhandler.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class APIUtils {
    private static final String TAG = "APIUtils";

    public static APIError parseError(Context context, Response<?> response) {
        Converter<ResponseBody, APIError> converter =
                RetrofitClient.getInstance(null).getRetrofit()
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }

        // handle the error according to response HTTP code
        if (response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            Log.e(TAG, "parseError: packageName = " + ((Activity) context).getClass().getSimpleName());
//            if (((Activity) context).getClass().getSimpleName().contentEquals("Dashboard")) {
//                context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_USER_UNATHENTICATED));
//            } else {
//                context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_USER_UNATHENTICATED));
//            }

            Constant.displayDialog(
                    context,
                    "Sesi kadaluarsa",
                    "Silahkan masuk kembali dengan mengisi email dan password dari akun anda",
                    false,
                    (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        Constant.signOut(context);
                    }, null
            );

        } else {
            Constant.displayDialog(
                    context,
                    "Error: "+response.code(),
                    error.getMessage(),
                    true,
                    (dialogInterface, i) -> {
                        ((Activity) context).finish();
                    },
                    null
            );
        }
        Log.e(TAG, "parseError: error response code: " + response.code() + " message: " + error.getMessage());

        return error;
    }

    public static APIWrapper parseWrapper(ResponseBody response) {

        APIWrapper apiWrapper = new APIWrapper();

        JSONObject obj;
        try {
            obj = new JSONObject(response.string());
            apiWrapper.setError(obj.getBoolean("error"));
            apiWrapper.setMessage(obj.getString("message"));

            if (obj.toString().contains("\"data\":[")) {
                apiWrapper.setDataArray(obj.getJSONArray("data"));
                Log.e(TAG, "parseWrapper: JSON data is Array");
            } else {
                apiWrapper.setData(obj.getJSONObject("data"));
                Log.e(TAG, "parseWrapper: JSON data is Object");
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        if (apiWrapper.getMessage().isEmpty()) {
            try {
                obj = new JSONObject(response.string());
                apiWrapper.setError(obj.getBoolean("error"));
                apiWrapper.setMessage(obj.getString("message"));

                if (obj.toString().contains("\"data\":[")) {
                    apiWrapper.setDataArray(obj.getJSONArray("data"));
                    Log.e(TAG, "parseWrapper: JSON data is Array");
                } else {
                    apiWrapper.setData(obj.getJSONObject("data"));
                    Log.e(TAG, "parseWrapper: JSON data is Object");
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }


        return apiWrapper;
    }

}
