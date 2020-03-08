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

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.Validator;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";


    //    @BindView(R.id.textInputLayout_email)
//    TextInputLayout tilEmail;
    @BindView(R.id.textInputLayout_phone)
    TextInputLayout tilPhone;
    @BindView(R.id.textInputLayout_password)
    TextInputLayout tilPassword;
    @BindView(R.id.textInputLayout_passwordConfirm)
    TextInputLayout tilPasswordConfirm;

    @BindView(R.id.textInputLayout_firstName)
    TextInputLayout tilFirstName;
    @BindView(R.id.textInputLayout_lastName)
    TextInputLayout tilLastName;
    @BindView(R.id.textInputLayout_dob)
    TextInputLayout tilDOB;
    @BindView(R.id.textInputLayout_sex)
    TextInputLayout tilSex;

    @BindView(R.id.btn_signUp)
    Button btnSignUp;

    private boolean IS_SIGNUP_OK = false;

    Validator validator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        Constant.changeStatusColor(this, R.color.background);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                SignUp.this,
                R.layout.resoruce_dropdown_popup_large,
                new String[]{"Laki-laki", "Perempuan"});

        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.filled_exposed_dropdown);
        editTextFilledExposedDropdown.setAdapter(adapter);

        CustomEditText cetDob = findViewById(R.id.editText_dob);
        cetDob.setAsDatePicker(getSupportFragmentManager());
        Constant.justify(findViewById(R.id.text));

        validator = new Validator();


        tilSex.getEditText().setShowSoftInputOnFocus(false);
        tilSex.getEditText().setFocusable(false);

    }

    @OnClick(R.id.cardView_termsCondition)
    void onClickTermsConditions() {
        Uri url = Uri.parse(Constant.ROOT_URL_TERMS);
        Intent intent = new Intent(Intent.ACTION_VIEW, url);

        if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);

    }

    @OnClick(R.id.btn_signUp)
    void onClickSignUp() {
        validateForm();

        if (!IS_SIGNUP_OK) {
            Snackbar.make(findViewById(android.R.id.content), "Data belum valid !", Snackbar.LENGTH_LONG).show();
            return;
        }

        sendData(
                tilPhone.getEditText().getText().toString(),
                tilPassword.getEditText().getText().toString(),
                tilFirstName.getEditText().getText().toString(),
                tilLastName.getEditText().getText().toString(),
                tilDOB.getEditText().getText().toString(),
                tilSex.getEditText().getText().toString()
        );
    }

    void validateForm() {

        IS_SIGNUP_OK = false;

//        tilEmail.setError(validator.validateEmail(tilEmail));

        tilPhone.setError(validator.validatePhone(tilPhone));

        tilPassword.setError(validator.validatePassword(tilPassword));

        tilPasswordConfirm.setError(
                StringUtils.equals(
                        tilPassword.getEditText().getText().toString(),
                        tilPasswordConfirm.getEditText().getText().toString())
                        ? null : "tidak sama dengan password"
        );

        tilFirstName.setError(validator.validateName(tilFirstName));

        tilLastName.setError(validator.validateName(tilLastName));

        tilDOB.setError(validator.validateEmpty(tilDOB));

        tilSex.setError(validator.validateSex(tilSex));

//        if (tilEmail.getError() != null) return;

        if (tilPhone.getError() != null) return;

        if (tilPassword.getError() != null) return;

        if (tilPasswordConfirm.getError() != null) return;

        if (tilFirstName.getError() != null) return;

        if (tilLastName.getError() != null) return;

        if (tilDOB.getError() != null) return;

        if (tilSex.getError() != null) return;


        IS_SIGNUP_OK = true;

    }

    void sendData(String phone, String password, String firstName, String lastName, String dob, String sex) {

        IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
        Call call = RetrofitClient.getInstance(null).getApiServices().signUpAccount(
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
                    tilPhone.setError(message);
                    Constant.displayDialog(
                            SignUp.this,
                            "Perhatian!",
                            message,
                            (dialog, which) -> {
                            }
                    );
                }
                else if (message.contains("Tanggal tidak valid")) {
                    tilDOB.setError(message);
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

            }
        });
        req.enqueue(call);

    }

    void startConfirmationActivity() {
        Intent i = new Intent(SignUp.this, ConfirmAccount.class);
        i.putExtra("phone", tilPhone.getEditText().getText().toString());
        startActivity(i);
        finish();
    }

}
