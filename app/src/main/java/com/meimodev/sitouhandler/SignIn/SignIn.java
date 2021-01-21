/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.SignIn;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.BuildConfig;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.ForgetAccount.ForgetAccount;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SignUp.ConfirmAccount;
import com.meimodev.sitouhandler.SignUp.SignUp;
import com.meimodev.sitouhandler.Validator;
import com.meimodev.sitouhandler.Vendor.VendorHome;
import com.meimodev.sitouhandler.Wizard.ApplyChurch.ApplyChurch;
import com.meimodev.sitouhandler.databinding.ActivitySigninBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn";

    private ActivitySigninBinding b;

    private int tapCount = 0;
    private boolean CHURCH_CREATION = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Constant.changeStatusColor(this, R.color.background);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        checkSystemStatus();

//        Check if device already logged in
        if (Guru.getInt(Constant.KEY_USER_ID, 0) != 0) {
            startActivity(new Intent(b.getRoot().getContext(), Dashboard.class));
            finishAffinity();
        }

        b.editTextPhone.clearFocus();
        b.editTextPassword.clearFocus();

        b.btnSignIn.setOnClickListener(v -> validateSignIn());
        b.btnSignUp.setOnClickListener(v -> signUp());
        b.btnForget.setOnClickListener(v -> forget());
        b.btnConfirm.setOnClickListener(v -> confirm());

        b.buttonChurch.setOnClickListener(v -> {
                    tapCount++;
                    if (tapCount >= 4) {
                        tapCount = 0;
                        CHURCH_CREATION = true;
                    }
                }
        );

        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("y", Locale.US);
        String year = format.format(d);
        b.textViewDev.setText("TOU-System | Awesomely Possible - Meimo " + year + " | © all rights reserved");

//        initHelper();
    }

    private void checkSystemStatus() {
        b.layoutProgressHolder.setVisibility(View.INVISIBLE);
        IssueRequestHandler req = new IssueRequestHandler(this);
        req.setContext(b.getRoot().getContext());
        req.setIntention(new Throwable());
        Call<ResponseBody> call = RetrofitClient.getInstance(null).getApiServices().checkSystemStatus(
                "android",
                BuildConfig.VERSION_NAME
        );

        req.setOnRequestHandlerResponseError(message -> {
            Log.e(TAG, "checkSystemStatus: ==============================" );
            Constant.displayDialog(
                    this,
                    "Perhatian !",
                    message,
                    (dialogInterface, i) -> {
                        final String appPackageName = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
            );
        });
        req.enqueue(call);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (b.textInputLayoutPhone.getError() != null) b.textInputLayoutPhone.setError(null);
        if (b.textInputLayoutPassword.getError() != null) b.textInputLayoutPassword.setError(null);
    }

    private void signIn() {

        if (b.layoutProgressHolder.getVisibility() != View.VISIBLE)
            b.layoutProgressHolder.setVisibility(View.VISIBLE);

        IssueRequestHandler requestHandler = new IssueRequestHandler(b.getRoot());
        Call<ResponseBody> call = RetrofitClient.getInstance(null).getApiServices().signIn(
                b.editTextPhone.getText().toString().trim(),
                b.editTextPassword.getText().toString().trim()
        );
        requestHandler.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {
            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                if (message.contains("konfirmasi")) {
                    Constant.displayDialog(
                            SignIn.this,
                            "Perhatian!",
                            "Akun dengan nomor telepon " +
                                    b.editTextPhone.getText().toString()
                                    + " belum dikonfirmasi. silahkan lakukan konfirmasi akun dengan menekan tombol 'konfirmasi Akun' di bawah",
                            (dialog, which) -> {
                            }
                    );
                    b.textInputLayoutPhone.setError(message);
                    b.btnConfirm.setVisibility(View.VISIBLE);
                } else {
                    proceed(res);
                }
            }

            @Override
            public void onRetry() {
                signIn();
            }
        });
        requestHandler.enqueue(call);

    }

    private void proceed(APIWrapper res) throws JSONException {
        JSONObject data = res.getData();

        Guru.putInt(Constant.KEY_VENDOR_ID, -5);

        //save data in shared preference for further usage
        JSONObject userObj = data.getJSONObject("user");

        Guru.putInt(Constant.KEY_USER_ID, userObj.getInt("id"));
        Guru.putString(Constant.KEY_USER_FULL_NAME, userObj.getString("full_name"));
        Guru.putString(Constant.KEY_USER_AGE, userObj.getString("age"));
        Guru.putString(Constant.KEY_USER_SEX, userObj.getString("sex"));
        Guru.putString(Constant.KEY_USER_ACCESS_TOKEN, userObj.getString("access_token"));

        if (!data.isNull("member")) {

            JSONObject memberObj = data.getJSONObject("member");

            Guru.putInt(Constant.KEY_MEMBER_ID, memberObj.getInt("id"));
            Guru.putString(Constant.KEY_MEMBER_CHURCH_POSITION, memberObj.getString("church_position_full"));
            Guru.putString(Constant.KEY_MEMBER_BIPRA, memberObj.getString("BIPRA"));
            Guru.putInt(Constant.KEY_MEMBER_DUPLICATE_CHECK, memberObj.isNull("duplicate_check") ? 0 : 1);

            JSONObject columnObj = data.getJSONObject("column");

            Guru.putInt(Constant.KEY_COLUMN_ID, columnObj.getInt("id"));
            Guru.putString(Constant.KEY_COLUMN_NAME_INDEX, columnObj.getString("name_index"));

            JSONObject churchObj = data.getJSONObject("church");

            Guru.putInt(Constant.KEY_CHURCH_ID, churchObj.getInt("id"));
            Guru.putString(Constant.KEY_CHURCH_NAME, churchObj.getString("church_name"));
            Guru.putString(Constant.KEY_CHURCH_KELURAHAN, churchObj.getString("church_kelurahan"));
            Guru.putInt(Constant.KEY_CHURCH_COLUMN_COUNT, churchObj.getInt("column_count"));

        } else {
            Log.e(TAG, "proceed: member is USER");
            Guru.putString(Constant.KEY_MEMBER_CHURCH_POSITION, Constant.ACCOUNT_TYPE_USER);
        }


        startActivity(new Intent(this, Dashboard.class));
        finishAffinity();

    }

    private void validateSignIn() {

        b.editTextPhone.clearFocus();
        b.editTextPassword.clearFocus();

        b.textInputLayoutPhone.setError(null);
        b.textInputLayoutPassword.setError(null);

        Validator validator = new Validator(this);

        if (validator.validateEmpty(b.textInputLayoutPhone) != null) return;

        if (validator.validateEmpty(b.textInputLayoutPassword) != null) return;

        signIn();

    }

    private void signUp() {
        b.editTextPhone.setText("");
        b.editTextPassword.setText("");
        if (!CHURCH_CREATION) {
            startActivity(new Intent(this, SignUp.class));
            tapCount = 0;
        } else {
            startActivity(new Intent(this, ApplyChurch.class));
        }
    }

    private void forget() {
        b.editTextPhone.setText("");
        b.editTextPassword.setText("");
        startActivity(new Intent(this, ForgetAccount.class));
    }

    private void confirm() {
        Intent i = new Intent(this, ConfirmAccount.class);
        i.putExtra("phone", b.textInputLayoutPhone.getEditText().getText().toString());
        startActivity(i);
    }

}
