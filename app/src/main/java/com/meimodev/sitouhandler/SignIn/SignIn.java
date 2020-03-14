/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.squti.guru.Guru;
import com.google.android.material.textfield.TextInputLayout;
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
import com.meimodev.sitouhandler.databinding.ActivitySigninBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;


public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn";

    private ActivitySigninBinding b;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Constant.changeStatusColor(this, R.color.background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        Check if device already logged in
        if (Guru.getInt(Constant.KEY_USER_ID, 0) != 0) {
            startActivity(new Intent(this, Dashboard.class));
            finishAffinity();
        }

        b.editTextPhone.clearFocus();
        b.editTextPassword.clearFocus();

        b.btnSignIn.setOnClickListener(v -> signIn());
        b.btnSignUp.setOnClickListener(v -> signUp());
        b.btnForget.setOnClickListener(v -> forget());
        b.btnConfirm.setOnClickListener(v -> confirm());


        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("YYYY", Locale.US);
        String year = format.format(d);
        b.textViewDev.setText("TOU-System | Awesomely Possible - Meimo " + year + " | © all rights reserved");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (b.textInputLayoutPhone.getError() != null) b.textInputLayoutPhone.setError(null);
        if (b.textInputLayoutPassword.getError() != null) b.textInputLayoutPassword.setError(null);
    }

    void fetchData() {
        IssueRequestHandler requestHandler = new IssueRequestHandler(findViewById(android.R.id.content));
        Call call = RetrofitClient.getInstance(null).getApiServices().signIn(
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
                }
                else {
                    proceed(res);
                }
            }

            @Override
            public void onRetry() {
                fetchData();
            }
        });
        requestHandler.enqueue(call);

    }

    private void proceed(APIWrapper res) throws JSONException {
        JSONObject data = res.getData();


        //save data in shared preference for further usage
        JSONObject userObj = data.getJSONObject("user");
//            SharedPrefManager.getInstance(this).saveUserData(
//                    userObj.getInt("id"),
//                    userObj.getString("full_name"),
//                    userObj.getString("age"),
//                    userObj.getString("sex"),
//                    userObj.getString("access_token")
//            );

        Log.e(TAG, "proceed: Saving User Data");
        Guru.putInt(Constant.KEY_USER_ID, userObj.getInt("id"));
        Guru.putString(Constant.KEY_USER_FULL_NAME, userObj.getString("full_name"));
        Guru.putString(Constant.KEY_USER_AGE, userObj.getString("age"));
        Guru.putString(Constant.KEY_USER_SEX, userObj.getString("sex"));
        Guru.putString(Constant.KEY_USER_ACCESS_TOKEN, userObj.getString("access_token"));

        if (!data.isNull("member")) {
            JSONObject memberObj = data.getJSONObject("member");
//                SharedPrefManager.getInstance(this).saveMemberData(
//                        memberObj.getInt("id"),
//                        memberObj.getString("church_position_full"),
//                        memberObj.getString("BIPRA")
//                );
            Log.e(TAG, "proceed: Saving Member Data");
            Guru.putInt(Constant.KEY_MEMBER_ID, memberObj.getInt("id"));
            Guru.putString(Constant.KEY_MEMBER_CHURCH_POSITION, memberObj.getString("church_position_full"));
            Guru.putString(Constant.KEY_MEMBER_BIPRA, memberObj.getString("BIPRA"));
            JSONObject columnObj = data.getJSONObject("column");
//                getInstance(this).saveColumnData(
//                        columnObj.getInt("id"),
//                        columnObj.getString("name_index")
//                );

            Log.e(TAG, "proceed: Saving Column Data");
            Guru.putInt(Constant.KEY_COLUMN_ID, columnObj.getInt("id"));
            Guru.putString(Constant.KEY_COLUMN_NAME_INDEX, columnObj.getString("name_index"));

            JSONObject churchObj = data.getJSONObject("church");
//                getInstance(this).saveChurchData(
//                        churchObj.getInt("id"),
//                        churchObj.getString("church_name"),
//                        churchObj.getString("church_kelurahan")
//                );
            Log.e(TAG, "proceed: Saving Church Data");
            Guru.putInt(Constant.KEY_CHURCH_ID, churchObj.getInt("id"));
            Guru.putString(Constant.KEY_CHURCH_NAME, churchObj.getString("church_name"));
            Guru.putString(Constant.KEY_CHURCH_KELURAHAN, churchObj.getString("church_kelurahan"));

        }
        else {
            Log.e(TAG, "proceed: member is USER");
            Guru.putString(Constant.KEY_MEMBER_CHURCH_POSITION, Constant.ACCOUNT_TYPE_USER);
        }


        startActivity(new Intent(this, Dashboard.class));
        finishAffinity();

    }

    void signIn() {

        b.editTextPhone.clearFocus();
        b.editTextPassword.clearFocus();

        b.textInputLayoutPhone.setError(null);
        b.textInputLayoutPassword.setError(null);

        Validator validator = new Validator();

        b.textInputLayoutPhone.setError(validator.validatePhone(b.textInputLayoutPhone));

        b.textInputLayoutPassword.setError(validator.validateEmpty(b.textInputLayoutPassword));

        if (b.textInputLayoutPhone.getError() == null && b.textInputLayoutPassword.getError() == null) {
            fetchData();
        }

    }


    void signUp() {
        b.editTextPhone.setText("");
        b.editTextPassword.setText("");
        startActivity(new Intent(this, SignUp.class));
    }

    void forget() {
        b.editTextPhone.setText("");
        b.editTextPassword.setText("");
        startActivity(new Intent(this, ForgetAccount.class));
    }

    void confirm() {
        Intent i = new Intent(this, ConfirmAccount.class);
        i.putExtra("phone", b.textInputLayoutPhone.getEditText().getText().toString());
        startActivity(i);
    }

}
