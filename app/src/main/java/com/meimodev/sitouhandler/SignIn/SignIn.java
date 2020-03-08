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

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;


public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn";


    @BindView(R.id.textInputLayout_phone)
    TextInputLayout tilPhone;
    @BindView(R.id.textInputLayout_password)
    TextInputLayout tilPassword;
    @BindView(R.id.editText_phone)
    CustomEditText etPhone;
    @BindView(R.id.editText_Password)
    CustomEditText etPassword;

    @BindView(R.id.layout_header)
    LinearLayout llHeader;

    @BindView(R.id.btn_signIn)
    Button btnSignIn;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_signUp)
    Button btnSignUp;
    @BindView(R.id.btn_forget)
    Button btnForget;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);
        Constant.changeStatusColor(this, R.color.background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

//        Check if device already logged in
        if (Guru.getInt(Constant.KEY_USER_ID, 0) != 0) {
            startActivity(new Intent(this, Dashboard.class));
            finishAffinity();
        }

        etPhone.clearFocus();
        etPassword.clearFocus();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tilPhone.getError() != null) tilPhone.setError(null);
        if (tilPassword.getError() != null) tilPassword.setError(null);
    }

    @OnClick(R.id.btn_signIn)
    void signIn() {

        etPhone.clearFocus();
        etPassword.clearFocus();

        tilPhone.setError(null);
        tilPassword.setError(null);

        Validator validator = new Validator();

        tilPhone.setError(validator.validatePhone(tilPhone));

        tilPassword.setError(validator.validateEmpty(tilPassword));

        if (tilPhone.getError() == null && tilPassword.getError() == null) {
            fetchData();
        }

    }


    @OnClick(R.id.btn_signUp)
    void signUp() {
        etPhone.setText("");
        etPassword.setText("");
        startActivity(new Intent(this, SignUp.class));
    }

    @OnClick(R.id.btn_forget)
    void forget() {
        etPhone.setText("");
        etPassword.setText("");
        startActivity(new Intent(this, ForgetAccount.class));
    }

    @OnClick(R.id.btn_confirm)
    void confirm() {
        Intent i = new Intent(this, ConfirmAccount.class);
        i.putExtra("phone", tilPhone.getEditText().getText().toString());
        startActivity(i);
    }

    void fetchData() {
        IssueRequestHandler requestHandler = new IssueRequestHandler(findViewById(android.R.id.content));
        Call call = RetrofitClient.getInstance(null).getApiServices().signIn(
                etPhone.getText().toString().trim(),
                etPassword.getText().toString().trim()
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
                            "Perhatian",
                            "Akun dengan nomor telepon " +
                                    etPhone.getText().toString()
                                    + " belum dikonfirmasi. silahkan lakukan konfirmasi akun dengan menekan tombol 'konfirmasi Akun' di bawah",
                            true,
                            (dialog, which) -> dialog.dismiss(),
                            null
                    );
                    tilPhone.setError(message);
                    btnConfirm.setVisibility(View.VISIBLE);
                } else {
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

        Log.e(TAG, "proceed: Saving User Data" );
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
            Log.e(TAG, "proceed: Saving Member Data" );
            Guru.putInt(Constant.KEY_MEMBER_ID, memberObj.getInt("id"));
            Guru.putString(Constant.KEY_MEMBER_CHURCH_POSITION, memberObj.getString("church_position_full"));
            Guru.putString(Constant.KEY_MEMBER_BIPRA, memberObj.getString("BIPRA"));
            JSONObject columnObj = data.getJSONObject("column");
//                getInstance(this).saveColumnData(
//                        columnObj.getInt("id"),
//                        columnObj.getString("name_index")
//                );

            Log.e(TAG, "proceed: Saving Column Data" );
            Guru.putInt(Constant.KEY_COLUMN_ID, columnObj.getInt("id"));
            Guru.putString(Constant.KEY_COLUMN_NAME_INDEX, columnObj.getString("name_index"));

            JSONObject churchObj = data.getJSONObject("church");
//                getInstance(this).saveChurchData(
//                        churchObj.getInt("id"),
//                        churchObj.getString("church_name"),
//                        churchObj.getString("church_kelurahan")
//                );
            Log.e(TAG, "proceed: Saving Church Data" );
            Guru.putInt(Constant.KEY_CHURCH_ID, churchObj.getInt("id"));
            Guru.putString(Constant.KEY_CHURCH_NAME, churchObj.getString("church_name"));
            Guru.putString(Constant.KEY_CHURCH_KELURAHAN, churchObj.getString("church_kelurahan"));

        } else {
            Log.e(TAG, "proceed: member is USER");
            Guru.putString(Constant.KEY_MEMBER_CHURCH_POSITION, Constant.ACCOUNT_TYPE_USER);
        }


        startActivity(new Intent(this, Dashboard.class));
        finishAffinity();

    }

}
