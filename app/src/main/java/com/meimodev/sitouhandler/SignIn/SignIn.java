package com.meimodev.sitouhandler.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.meimodev.sitouhandler.ApiServices;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.Helper.APIError;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn";

    @BindView(R.id.editText_Username)
    CustomEditText etUsername;
    @BindView(R.id.editText_Password)
    CustomEditText etPassword;

    @BindView(R.id.layout_header)
    CardView cvHeader;

    @BindView(R.id.btn_signIn)
    Button btnSignIn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

//        Check if device already logged in
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {

            try {
                Constant.ACCOUNT_TYPE = (String) SharedPrefManager.getInstance(SignIn.this).loadUserData(SharedPrefManager.KEY_CHURCH_POSITION);
                proceed(null);
            } catch (JSONException e) {
                Log.e(TAG, "onCreate: JSON ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }

        etUsername.clearFocus();
        etPassword.clearFocus();

    }

    @OnClick(R.id.btn_signIn)
    void signIn() {

        etUsername.clearFocus();
        etPassword.clearFocus();

        Validator validator = new Validator();
        if (validator.validateEditText_isEmpty(etUsername) || validator.validateEditText_isEmpty(etPassword)) {
            validator.displayErrorMessage(SignIn.this, "Email atau Password " + Validator.MESSAGE_ERROR_IS_EMPTY);
            return;
        }

        if (etUsername.getError() != null) {
            validator.displayErrorMessage(SignIn.this, "Email " + Validator.MESSAGE_ERROR_IS_INVALID);
            return;
        }

        fetchData();

    }

    void fetchData() {
        View progress = Constant.makeProgressCircle(findViewById(R.id.layout_main));
        cvHeader.setVisibility(View.INVISIBLE);
        btnSignIn.setVisibility(View.GONE);

        ApiServices apiServices = RetrofitClient.getInstance(null).getApiServices();
        Call<ResponseBody> call = apiServices.signIn(
                etUsername.getText().toString().trim(),
                etPassword.getText().toString().trim()
        );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    APIWrapper res = APIUtils.parseWrapper(response.body());
                    try {
                        if (!res.isError()) {
                            proceed(res);
                            Log.e(TAG, "onResponse: response return success proceeding");
                        } else {
                            progress.setVisibility(View.GONE);
                            cvHeader.setVisibility(View.VISIBLE);
                            btnSignIn.setVisibility(View.VISIBLE);

                            Constant.displayDialog(
                                    SignIn.this,
                                    null,
                                    res.getMessage(),
                                    true,
                                    (dialogInterface, i) -> {
                                    },
                                    null
                            );
                            Log.e(TAG, "onResponse: response return success but error");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onResponse: JSON Exc" + e.getMessage(), e);
                    }

                } else {
                    progress.setVisibility(View.GONE);
                    cvHeader.setVisibility(View.VISIBLE);
                    btnSignIn.setVisibility(View.VISIBLE);

                    APIUtils.parseError(SignIn.this, response);

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onRetry: network fail: ", t);
                progress.setVisibility(View.GONE);
                cvHeader.setVisibility(View.INVISIBLE);
                btnSignIn.setVisibility(View.GONE);
                Constant.makeFailFetch(findViewById(R.id.layout_main), view -> {
                    cvHeader.setVisibility(View.VISIBLE);
                    btnSignIn.setVisibility(View.VISIBLE);
                });
            }
        });


    }

    private void proceed(@Nullable APIWrapper res) throws JSONException {


        if (res != null) {

            JSONObject data = res.getData();

            Constant.ACCOUNT_TYPE = data.getString("church_position");

            //save data in shared preference for further usage
            SharedPrefManager.getInstance(SignIn.this).saveUserData(
                    data.getInt("user_id"),
                    data.getInt("member_id"),
                    data.getString("church_position"),
                    data.getString("access_token"),
                    data.getString("full_name"),
                    data.getString("church_name"),
                    data.getString("kelurahan"),
                    data.getString("name_index"),
                    data.getInt("age"),
                    data.getString("sex"),
                    data.getString("index"),
                    data.getInt("church_id"),
                    data.getString("BIPRA"),
                    data.getInt("column_id")
            );

        }

        startActivity(new Intent(this, Dashboard.class));
        finishAffinity();

    }

}
