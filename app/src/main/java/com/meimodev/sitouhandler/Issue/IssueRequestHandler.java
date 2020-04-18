/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Issue;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.R;

import org.json.JSONException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueRequestHandler {

    private static final String TAG = "IssueRequestHandler: ";

    private View rootView;
    private Context context;
    private View progress;

    private String intention = "";

    private OnRequestHandler onRequestHandler;

    public IssueRequestHandler(@Nullable View rootView) {
        if (rootView != null) {
            this.rootView = rootView;
            context = rootView.getContext();
            progress = Constant.makeProgressCircle(rootView);
        }
    }

    public interface OnRequestHandler {
        void onTry();

        void onSuccess(APIWrapper res, String message) throws JSONException;

        void onRetry();
    }

    public void setOnRequestHandler(OnRequestHandler onRequestHandler) {
        this.onRequestHandler = onRequestHandler;
    }

    public void setIntention(String intention) {
        this.intention = intention;
    }

    public void setContext (Context context){this.context = context;}

    public void setIntention(Throwable throwableForMethodMame) {
        setIntention(throwableForMethodMame.getStackTrace()[0].getMethodName());
    }

    public void enqueue(Call call) {

        View mainView;
        try {
            mainView = rootView.findViewById(R.id.layout_main);
        } catch (NullPointerException e) {
            Log.e(TAG, "enqueue: " + intention + ": class initialize with null rootView " + e.getMessage());
            return;
        }

        if (progress != null && progress.getVisibility() != View.VISIBLE) {
            progress.setVisibility(View.VISIBLE);
        }

        if (mainView != null && mainView.getVisibility() == View.VISIBLE) {
            mainView.setVisibility(View.INVISIBLE);
        }

        if (onRequestHandler != null) onRequestHandler.onTry();

        if (call.isExecuted()) {
            call = call.clone();
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    APIWrapper res = APIUtils.parseWrapper(context, response.body());

                    if (!res.isError()) {
                        Log.e(TAG, "onResponse: "
                                + "(Class)" + context.getClass().getSimpleName() + ": "
                                + intention + ": "
                                + "response ERROR = FALSE -> "
                                + "response message: " + res.getMessage());

                        if (onRequestHandler != null) {
                            try {
                                onRequestHandler.onSuccess(res, res.getMessage());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "onResponse: "
                                        + "(Class)" + context.getClass().getSimpleName() + ": "
                                        + intention + ": "
                                        + "JSON ERROR: "
                                        + e.getMessage());
                            }

                        }

                    }
                    else {
                        Log.e(TAG, "onResponse: "
                                + "(Class)" + context.getClass().getSimpleName() + ": "
                                + intention + ": "
                                + "response ERROR = TRUE -> "
                                + "response message: " + res.getMessage());

                        Constant.displayDialog(
                                context,
                                "Perhatian!",
                                res.getMessage(),
                                (dialog, which) -> {
                                }
                        );


                    }
                    if (progress != null && progress.getVisibility() == View.VISIBLE) {
                        progress.setVisibility(View.INVISIBLE);
                    }

                    if (mainView != null && mainView.getVisibility() != View.VISIBLE) {
                        mainView.setVisibility(View.VISIBLE);
                    }

                }
                else {
                    APIUtils.intention = intention;
                    APIUtils.parseError(context, response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: "
                        + "(Class)" + context.getClass().getSimpleName() + ": "
                        + intention + ": "
                        + t.getMessage());

                if (progress != null && progress.getVisibility() == View.VISIBLE) {
                    progress.setVisibility(View.GONE);
                }

                Constant.makeFailFetch(rootView, view -> {
                    progress = Constant.makeProgressCircle(rootView);
                    if (onRequestHandler != null) {
                        onRequestHandler.onRetry();
                    }
                });
            }
        });
    }

    public void backGroundRequest(Call call) {

        if (onRequestHandler != null) {
            onRequestHandler.onTry();
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    APIWrapper res = APIUtils.parseWrapper(context, response.body());

                    if (!res.isError()) {

                        Log.e(TAG, "BACKGROUND REQUEST: onResponse: "
                                + "(Class)" + context.getClass().getSimpleName() + ": "
                                + intention + ": "
                                + "response ERROR = FALSE -> "
                                + "response message: " + res.getMessage());

                        if (onRequestHandler != null) {
                            try {
                                onRequestHandler.onSuccess(res, res.getMessage());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "onResponse: "
                                        + "(Class)" + context.getClass().getSimpleName() + ": "
                                        + intention + ": "
                                        + "JSON ERROR: "
                                        + e.getMessage());
                            }
                        }

                    }
                    else {
                        Log.e(TAG, "BACKGROUND REQUEST: onResponse: "
                                + "(Class)" + context.getClass().getSimpleName() + ": "
                                + intention + ": "
                                + "response ERROR = TRUE -> "
                                + "response message: " + res.getMessage());
                    }

                }
                else {
                    Log.e(TAG, "BACKGROUND REQUEST: onResponse: "
                            + "(Class)" + context.getClass().getSimpleName() + ": "
                            + intention + ": "
                            + "Error Code: "
                            + response.code()
                    );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "BACKGROUND REQUEST: onFailure: "
                        + "(Class)" + context.getClass().getSimpleName() + ": "
                        + intention + ": "
                        + t.getMessage());
                if (onRequestHandler != null) {
                    onRequestHandler.onRetry();
                }
            }
        });

    }

}
