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
    private OnRequestHandlerFailure onRequestHandlerFailure;
    private Call call;

    public IssueRequestHandler(@Nullable View rootView) {
        if (rootView != null) {
            this.rootView = rootView;
            context = rootView.getContext();
            progress = Constant.makeProgressCircle(rootView);
        }
    }

    public IssueRequestHandler(Context context) {
            this.context = context;
            progress = Constant.makeProgressCircle(context);
    }

    public interface OnRequestHandler {
        void onTry();

        void onSuccess(APIWrapper res, String message) throws JSONException;

        void onRetry();
    }

    public interface  OnRequestHandlerFailure{
        void onFailure();
    }

    public void setOnRequestHandlerFailure(OnRequestHandlerFailure onRequestHandlerFailure){
        this.onRequestHandlerFailure = onRequestHandlerFailure;
    }

    public void setOnRequestHandler(OnRequestHandler onRequestHandler) {
        this.onRequestHandler = onRequestHandler;
    }

    public void setIntention(String intention) {
        this.intention = intention;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setIntention(Throwable throwableForMethodMame) {
        setIntention(throwableForMethodMame.getStackTrace()[0].getMethodName());
    }

    public void enqueue(Call call) {
        this.call = call;
        View mainView = null;

        if (rootView != null) {
            mainView = rootView.findViewById(R.id.layout_main);
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
        View finalMainView = mainView;
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    APIWrapper res = APIUtils.parseWrapper(context, response.body());

                    if (!res.isError()) {
//                        Log.e(TAG, "onResponse: "
//                                + "(Class)" + context.getClass().getSimpleName() + ": "
//                                + intention + ": "
//                                + "response ERROR = FALSE -> "
//                                + "response message: " + res.getMessage());

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

                    if (finalMainView != null && finalMainView.getVisibility() != View.VISIBLE) {
                        finalMainView.setVisibility(View.VISIBLE);
                    }

                }
                else {
                    APIUtils.intention = intention;
                    APIUtils.parseError(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "onFailure: "
                        + "(Class)" + context.getClass().getSimpleName() + ": "
                        + intention + ": "
                        + t.getMessage(), t);

                if (progress != null && progress.getVisibility() == View.VISIBLE) {
                    progress.setVisibility(View.GONE);
                }

                if (onRequestHandlerFailure != null){
                    onRequestHandlerFailure.onFailure();
                }

                Constant.makeFailFetch(context, view -> {
                    progress = Constant.makeProgressCircle(context);
                    if (onRequestHandler != null) {
                        onRequestHandler.onRetry();
                    }
                });
            }
        });
    }

    public void backGroundRequest(Call call) {
        this.call = call;

        if (onRequestHandler != null) {
            onRequestHandler.onTry();
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
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
                    APIUtils.intention = intention;
                    APIUtils.parseError(context, response);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e(TAG, "BACKGROUND REQUEST: onFailure: "
                        + "(Class)" + context.getClass().getSimpleName() + ": "
                        + intention + ": "
                        + t.getMessage(),t);
                if (onRequestHandler != null) {
                    Log.e(TAG, "BACKGROUND REQUEST: onFailure: "
                            + "(Class)" + context.getClass().getSimpleName() + ": "
                            + intention + ": !!! RETRYING !!!");
                    onRequestHandler.onRetry();
                }
            }
        });

    }

    public Call getCall(){return call;}

    public void cancel(){
        call.cancel();
    }

}
