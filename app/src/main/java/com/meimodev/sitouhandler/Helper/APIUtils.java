/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
    public static String intention = "";

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
            Constant.displayDialog(
                    context,
                    "Sesi kadaluarsa!",
                    "Silahkan masuk kembali dengan mengisi nomor dan password dari akun anda",
                    false,
                    (dialog, which) -> {
                    },
                    null,
                    dialog -> Constant.signOut(context)
            );
        } else if (response.code() == HttpURLConnection.HTTP_UNAVAILABLE){
            Constant.displayDialog(
                    context,
                    "Sistem Dalam Perbaikan",
                    "Tidak dapat mengakses data dikarenakan system sedang dalam perawatan, silahkan kembali beberapa saat lagi",
                    false,
                    (dialog, which) -> {
                    },
                    null,
                    dialog -> Constant.signOut(context)
            );
        }
        else {
            Constant.displayDialog(
                    context,
                    "Error! " + response.code(),
                    error.getMessage(),
                    false,
                    (dialog, which) -> ((Activity) context).finish(),
                    null,
                    dialog -> ((Activity) context).finish()
            );
        }
        Log.e(TAG, "parseError: " + "(Class)" + context.getClass().getSimpleName() + ": " + intention + ": response code: " + response.code() + " message: " + error.getMessage());

        return error;
    }

    public static APIWrapper parseWrapper(Context context, ResponseBody response) {

        APIWrapper apiWrapper = new APIWrapper();
        apiWrapper.setDataArray(null);
        apiWrapper.setData(null);

        JSONObject obj;
        try {
            obj = new JSONObject(response.string());
            apiWrapper.setError(obj.getBoolean("error"));
            apiWrapper.setMessage(obj.getString("message"));


            if (obj.toString().contains("\"data\":[")) {
                apiWrapper.setDataArray(obj.getJSONArray("data"));
                apiWrapper.setData(null);
                Log.e(TAG, "parseWrapper: " + "(Class)" + context.getClass().getSimpleName() + ": " + intention + ": JSON data is ARRAY");
            }
            else {
                apiWrapper.setData(obj.getJSONObject("data"));
                apiWrapper.setDataArray(null);
                Log.e(TAG, "parseWrapper: " + "(Class)" + context.getClass().getSimpleName() + ": " + intention + ": JSON data is OBJECT");
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

//        if (apiWrapper.getMessage().isEmpty()) {
//            try {
//                obj = new JSONObject(response.string());
//                apiWrapper.setError(obj.getBoolean("error"));
//                apiWrapper.setMessage(obj.getString("message"));
//
//                if (obj.toString().contains("\"data\":[")) {
//                    apiWrapper.setDataArray(obj.getJSONArray("data"));
//                    Log.e(TAG, "parseWrapper: " + "(Class)" + context.getClass().getSimpleName() + ": " + intention + ": JSON data is ARRAY");
//                }
//                else {
//                    apiWrapper.setData(obj.getJSONObject("data"));
//                    Log.e(TAG, "parseWrapper: " + "(Class)" + context.getClass().getSimpleName() + ": " + intention + ": JSON data is OBJECT");
//                }
//            } catch (JSONException | IOException e) {
//                e.printStackTrace();
//            }
//        }


        return apiWrapper;
    }

}
