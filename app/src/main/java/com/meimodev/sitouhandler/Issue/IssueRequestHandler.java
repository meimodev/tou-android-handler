package com.meimodev.sitouhandler.Issue;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.meimodev.sitouhandler.ApiServices;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueRequestHandler {

    private static final String TAG = "IssueRequestHandler: ";

    private View rootView;
    private Context context;
    private View progress;

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

    public void enqueue(Call call) {

        View mainView;
        try {
            mainView = rootView.findViewById(R.id.layout_main);
        } catch (NullPointerException e) {
            Log.e(TAG, "enqueue: class initialize with null rootView " + e.getMessage());
            return;
        }

        if (progress != null && progress.getVisibility() != View.VISIBLE)
            progress.setVisibility(View.VISIBLE);

        if (mainView != null && mainView.getVisibility() == View.VISIBLE)
            mainView.setVisibility(View.INVISIBLE);

        if (onRequestHandler != null) onRequestHandler.onTry();

        if (call.isExecuted()) {
            call = call.clone();
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    APIWrapper res = APIUtils.parseWrapper(response.body());

                    if (!res.isError()) {
                        Log.e(TAG, "onResponse: "
                                + context.getClass().getSimpleName()
                                + ": response SUCCESS -> proceeding -> "
                                + " response message: " + res.getMessage());

                        if (onRequestHandler != null) {
                            try {
                                onRequestHandler.onSuccess(res, res.getMessage());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, context.getClass().getSimpleName() + ": JSON ERROR " + e.getMessage());
                            }

                        }

                    } else {
                        Log.e(TAG, "onResponse:"
                                + context.getClass().getSimpleName()
                                + ": response return SUCCESS but Error:true, with message = "
                                + res.getMessage());

                        Constant.displayDialog(
                                context,
                                "Peringatan",
                                res.getMessage(),
                                true,
                                (dialogInterface, i) -> dialogInterface.dismiss(),
                                null
                        );


                    }
                    if (progress != null && progress.getVisibility() == View.VISIBLE)
                        progress.setVisibility(View.INVISIBLE);

                    if (mainView != null && mainView.getVisibility() != View.VISIBLE)
                        mainView.setVisibility(View.VISIBLE);

                } else {
                    APIUtils.parseError(context, response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onRetry: "
                        + context.getClass().getSimpleName()
                        + ": ", t);

                if (progress != null && progress.getVisibility() == View.VISIBLE)
                    progress.setVisibility(View.GONE);

                Constant.makeFailFetch(rootView, view -> {
                    progress = Constant.makeProgressCircle(rootView);
                    if (onRequestHandler != null)
                        onRequestHandler.onRetry();
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

                    APIWrapper res = APIUtils.parseWrapper(response.body());

                    if (!res.isError()) {
                        Log.e(TAG, "BACKGROUND REQUEST: onResponse: "
                                + ": response SUCCESS -> proceeding -> "
                                + " response message: " + res.getMessage());

                        if (onRequestHandler != null) {
                            try {
                                onRequestHandler.onSuccess(res, res.getMessage());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, context.getClass().getSimpleName() + ": onResponse: JSON ERROR " + e.getMessage());
                            }
                        }

                    } else {

                        Log.e(TAG, "BACKGROUND REQUEST: onResponse: "
                                + ": response return SUCCESS but Error = true, message = "
                                + res.getMessage());
                    }

                } else {
                    Log.e(TAG, "BACKGROUND REQUEST onResponse: response NOT success, Error Code = " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onRetry: "
                        + ": ", t);
                if (onRequestHandler != null)
                    onRequestHandler.onRetry();
            }
        });

    }

}
