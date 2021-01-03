/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.SignUp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.Validator;
import com.meimodev.sitouhandler.databinding.ActivitySignupBinding;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";


    //    @BindView(R.id.textInputLayout_email)
//    TextInputLayout tilEmail;
//    @BindView(R.id.textInputLayout_phone)
//    TextInputLayout tilPhone;
//    @BindView(R.id.textInputLayout_password)
//    TextInputLayout tilPassword;
//    @BindView(R.id.textInputLayout_passwordConfirm)
//    TextInputLayout tilPasswordConfirm;
//
//    @BindView(R.id.textInputLayout_firstName)
//    TextInputLayout tilFirstName;
//    @BindView(R.id.textInputLayout_lastName)
//    TextInputLayout tilLastName;
//    @BindView(R.id.textInputLayout_dob)
//    TextInputLayout tilDOB;
//    @BindView(R.id.textInputLayout_sex)
//    TextInputLayout tilSex;
//
//    @BindView(R.id.btn_signUp)
//    Button btnSignUp;

//    private boolean IS_SIGNUP_OK = false;

    Validator validator;

    private ActivitySignupBinding b;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Constant.changeStatusColor(this, R.color.background);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                SignUp.this,
                R.layout.resoruce_dropdown_popup_large,
                new String[]{"Laki-laki", "Perempuan"});

        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);

        CustomEditText cetDob = findViewById(R.id.editText_dob);
        cetDob.setAsDatePicker(getSupportFragmentManager());
        Constant.justify(findViewById(R.id.text));

        validator = new Validator(this);

        b.textInputLayoutSex.getEditText().setShowSoftInputOnFocus(false);
        b.textInputLayoutSex.getEditText().setFocusable(false);
    }

    @OnClick(R.id.cardView_termsCondition)
    void onClickTermsConditions() {
        Uri url = Uri.parse(Constant.ROOT_URL_TERMS);
        Intent intent = new Intent(Intent.ACTION_VIEW, url);

        if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);

    }

    @OnClick(R.id.btn_signUp)
    void onClickSignUp() {

        if (validator.validatePhone(b.textInputLayoutPhone) != null) return;

        if (validator.validatePassword(b.textInputLayoutPassword) != null) return;

        b.textInputLayoutPasswordConfirm.setError(
                StringUtils.equals(
                        b.textInputLayoutPassword.getEditText().getText().toString(),
                        b.textInputLayoutPasswordConfirm.getEditText().getText().toString())
                        ? null : "tidak sama dengan password"
        );
        if (b.textInputLayoutPasswordConfirm.getError() != null) return;

        if (validator.validateName(b.textInputLayoutFirstName) != null) return;

        if (validator.validateName(b.textInputLayoutLastName) != null) return;

        if (validator.validateEmpty(b.textInputLayoutDob) != null) return;

        if (validator.validateSex(b.textInputLayoutSex) != null) return;

        sendData(
                b.textInputLayoutPhone.getEditText().getText().toString(),
                b.textInputLayoutPassword.getEditText().getText().toString(),
                b.textInputLayoutFirstName.getEditText().getText().toString(),
                b.textInputLayoutLastName.getEditText().getText().toString(),
                b.textInputLayoutDob.getEditText().getText().toString(),
                b.textInputLayoutSex.getEditText().getText().toString()
        );
    }

    void sendData(String phone, String password, String firstName, String lastName, String dob, String sex) {

        IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
        Call<ResponseBody> call = RetrofitClient.getInstance(null).getApiServices().signUpAccount(
                phone, password, firstName, lastName, dob, sex
        );
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {

                if (message.contentEquals("OK")) {
                    Constant.displayDialog(
                            SignUp.this,
                            "OK",
                            "Pendaftaran akun BERHASIL dilakukan. Silahkan lakukan aktivasi akun di halaman berikut",
                            true,
                            (dialog, which) -> {
                            },
                            null,
                            dialog -> startConfirmationActivity()
                    );


                }
                else if (message.contains("digunakan")) {
                    b.textInputLayoutPhone.setError(message);
                    Constant.displayDialog(
                            SignUp.this,
                            "Perhatian!",
                            message,
                            (dialog, which) -> {
                            }
                    );
                }
                else if (message.contains("Tanggal tidak valid")) {
                    b.textInputLayoutDob.setError(message);
                    Constant.displayDialog(
                            SignUp.this,
                            "Perhatian!",
                            message,
                            (dialog, which) -> {
                            }
                    );
                }
            }

            @Override
            public void onRetry() {
                sendData(phone, password, firstName, lastName, dob, sex);
            }
        });
        req.enqueue(call);

    }

    void startConfirmationActivity() {
        Intent i = new Intent(SignUp.this, ConfirmAccount.class);
        i.putExtra("phone", b.textInputLayoutPhone.getEditText().getText().toString());
        startActivity(i);
        finish();
    }

}
