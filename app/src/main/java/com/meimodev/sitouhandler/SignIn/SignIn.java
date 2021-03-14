/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.ForgetAccount.ForgetAccount;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SignUp.ConfirmAccount;
import com.meimodev.sitouhandler.SignUp.SignUp;
import com.meimodev.sitouhandler.Validator;
import com.meimodev.sitouhandler.Wizard.ApplyChurch.ApplyChurch;
import com.meimodev.sitouhandler.databinding.ActivitySigninBinding;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

//        setupFirebasePhoneAuth();

        Constant.changeStatusColor(this, R.color.background);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        b.layoutProgressHolder.setVisibility(View.INVISIBLE);
        Constant.checkSystemStatus(this);

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

    private static final int RC_SIGN_IN = 123;
//
//    private void setupFirebasePhoneAuth() {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        if (auth.getCurrentUser() != null) {
//            Snackbar.make(b.getRoot(), "Already Signed in", Snackbar.LENGTH_LONG).show();
//        } else {
//            startActivityForResult(
//                    // Get an instance of AuthUI based on the default app
//                    AuthUI.getInstance()
//                            .createSignInIntentBuilder()
//                            .setAvailableProviders(Arrays.asList(
//                                    new AuthUI.IdpConfig.PhoneBuilder().build()))
//                            .build(),
//                    RC_SIGN_IN);
//        }
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
//        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
//
//            // Successfully signed in
//            if (resultCode == RESULT_OK) {
//                Snackbar.make(b.getRoot(), "SIGNIN SUCCESS", Snackbar.LENGTH_INDEFINITE).show();
//            } else {
//                // Sign in failed
//                if (response == null) {
//                    // User pressed back button
//                    Snackbar.make(b.getRoot(), "USER CANCELED", Snackbar.LENGTH_INDEFINITE).show();
//                    return;
//                }
//
//                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    Snackbar.make(b.getRoot(), "No Internet "+response.getError().getErrorCode(), Snackbar.LENGTH_INDEFINITE).show();
//                    return;
//                }
//
//                Snackbar.make(b.getRoot(), "UNKNOWN ERROR", Snackbar.LENGTH_INDEFINITE).show();
//                Log.e(TAG, "Sign-in error: ", response.getError());
//            }
//        }
//    }
//

    @Override
    protected void onResume() {
        super.onResume();
        if (b.textInputLayoutPhone.getError() != null) b.textInputLayoutPhone.setError(null);
        if (b.textInputLayoutPassword.getError() != null) b.textInputLayoutPassword.setError(null);

        String phone = Guru.getString("TEMP_PHONE", "");
        String pass = Guru.getString("TEMP_PASS", "");
        if (StringUtils.isNotEmpty(phone) && StringUtils.isNotEmpty(pass)) {
            b.textInputLayoutPhone.getEditText().setText(phone);
            b.textInputLayoutPassword.getEditText().setText(pass);
            b.btnSignIn.callOnClick();
            Guru.remove("TEMP_PHONE");
            Guru.remove("TEMP_PASS");
        }

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
