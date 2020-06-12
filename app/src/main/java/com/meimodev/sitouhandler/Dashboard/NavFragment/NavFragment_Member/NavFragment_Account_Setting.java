/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomDialog;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SignIn.SignIn;
import com.meimodev.sitouhandler.Validator;
import com.meimodev.sitouhandler.databinding.NavFragmentAccountSettingBinding;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class NavFragment_Account_Setting extends Fragment {

    private static final String TAG = "Fragment_User_Home";

    private Context context;

    private NavFragmentAccountSettingBinding b;

    private Validator validator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        b = NavFragmentAccountSettingBinding.inflate(inflater, container, false);
        context = b.getRoot().getContext();

        b.textInputLayoutPhone.setEnabled(false);
        b.editTextDob.setAsDatePicker(((FragmentActivity) context).getSupportFragmentManager());
        b.buttonSavePhone.setEnabled(false);
        b.buttonSavePhone.setBackgroundColor(getResources().getColor(R.color.disabled_background));

//        setup buttons and validate on each according views

        validator = new Validator(context);

        fetchData();

        return b.getRoot();
    }

    private void fetchData() {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setIntention(new Throwable());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                initViews(res.getData());
            }

            @Override
            public void onRetry() {

            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getAccount(
                Guru.getInt(Constant.KEY_USER_ID, 0)
        ));
    }

    private void initViews(JSONObject data) throws JSONException {

        b.layoutMembership.setVisibility(View.VISIBLE);

        JSONObject user = data.getJSONObject("user");
        b.editTextFisrtName.setText(user.getString("first_name"));
        b.editTextMiddleName.setText(user.isNull("middle_name") ? "" : user.getString("middle_name"));
        b.editTextLastName.setText(user.getString("last_name"));
        b.editTextDob.setText(user.getString("dob"));

        String sex = user.getString("sex");
        if (StringUtils.contains(sex, "laki")) {
            b.radioButtonMale.setChecked(true);
        }
        else {
            b.radioButtonFemale.setChecked(true);
        }

        b.buttonSavePassword.setText("UBAH PASSWORD");
        b.textInputLayoutPasswordNew.setVisibility(View.GONE);
        b.textInputLayoutPasswordNewConfirm.setVisibility(View.GONE);
        b.buttonSavePassword.setOnClickListener(v -> {
            if (b.textInputLayoutPasswordNew.getVisibility() != View.VISIBLE) {
                b.textInputLayoutPasswordNew.setVisibility(View.VISIBLE);
                b.textInputLayoutPasswordNewConfirm.setVisibility(View.VISIBLE);
                b.buttonSavePassword.setText("SIMPAN PASSWORD");
            }
            else {
                handleSavePassword();
            }
        });
        b.buttonSaveIdentity.setOnClickListener(v -> handleSaveIdentity());

        if (data.isNull("member")) {
            b.layoutMembership.setVisibility(View.GONE);
            return;
        }

        JSONObject member = data.getJSONObject("member");
        String cName = member.getString("church_name");
        String cKel = member.getString("kelurahan");
        String col = member.getString("column");

        b.textViewChurchInfo.setText(String.format("%s, %s %s", cName.toUpperCase(), cKel, col));
        b.textViewChurchPosition.setText(member.getString("church_position").replace("\\n", System.lineSeparator()));
        b.checkboxBaptis.setChecked(!member.isNull("baptize"));
        b.checkboxSidi.setChecked(!member.isNull("sidi"));
        b.checkboxNikah.setChecked(!member.isNull("marriage"));

        b.buttonSaveMembership.setOnClickListener(v -> handleSaveMembership());
    }

    private void handleSavePassword() {
        if (validator.validatePassword(b.textInputLayoutPasswordNew) != null) return;

        if (!b.editTextPasswordNew.getText().toString().contentEquals(
                b.editTextPasswordNewConfirm.getText().toString())) {
            b.textInputLayoutPasswordNewConfirm.setError("Pasword baru tidak sama");
            return;
        }

        displayDialog(OPERATION_TYPE_PASSWORD);

        b.textInputLayoutPasswordNew.setError(null);
        b.textInputLayoutPasswordNewConfirm.setError(null);
    }

    private void handleSaveIdentity() {

        if (validator.validateName(b.textInputLayoutFirstName) != null) return;
//        if (validator.validateEmpty(b.textInputLayoutMiddleName) != null) return;
        if (validator.validateName(b.textInputLayoutLastName) != null) return;
        if (validator.validateEmpty(b.textInputLayoutDob) != null) return;

        displayDialog(OPERATION_TYPE_IDENTITY);

        b.textInputLayoutFirstName.setError(null);
        b.textInputLayoutMiddleName.setError(null);
        b.textInputLayoutLastName.setError(null);
        b.textInputLayoutDob.setError(null);
    }

    private void handleSaveMembership() {
        displayDialog(OPERATION_TYPE_MEMBERSHIP);
    }

    private final int OPERATION_TYPE_PASSWORD = 1;
    private final int OPERATION_TYPE_IDENTITY = 2;
    private final int OPERATION_TYPE_MEMBERSHIP = 3;

    private void requestOperationToServer(int operationType, String pass) {
        Call call = null;
        switch (operationType) {
            case OPERATION_TYPE_PASSWORD:
                call = RetrofitClient.getInstance(null).getApiServices().setAccountPassword(
                        Guru.getInt(Constant.KEY_USER_ID, 0),
                        pass,
                        b.editTextPasswordNewConfirm.getText().toString().trim()
                );
                break;
            case OPERATION_TYPE_IDENTITY:
                call = RetrofitClient.getInstance(null).getApiServices().setAccountIdentity(
                        Guru.getInt(Constant.KEY_USER_ID, 0),
                        pass,
                        b.editTextFisrtName.getText().toString().trim(),
                        b.editTextMiddleName.getText().toString().trim().replace(" ", "XXX"),
                        b.editTextLastName.getText().toString().trim(),
                        b.editTextDob.getText().toString().trim(),
                        b.radioButtonMale.isChecked() ? "Laki-laki" : "perempuan"
                );
                break;
            case OPERATION_TYPE_MEMBERSHIP:
                call = RetrofitClient.getInstance(null).getApiServices().setAccountMembership(
                        Guru.getInt(Constant.KEY_USER_ID, 0),
                        pass,
                        b.checkboxBaptis.isChecked() ? "1" : "0",
                        b.checkboxSidi.isChecked() ? "1" : "0",
                        b.checkboxNikah.isChecked() ? "1" : "0"
                );
                break;
        }

        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setIntention(new Throwable());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {
            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {

                if (operationType == OPERATION_TYPE_PASSWORD) {
                    Constant.displayDialog(
                            context,
                            "OK, Berhasil Diubah",
                            "Silahkan masuk kembali menggunakan password baru",
                            false,
                            (dialog, which) -> {
                            },
                            null,
                            dialog -> {
                                Constant.signOut(context);
                                ((Activity) context).finishAffinity();
                                startActivity(new Intent(context, SignIn.class));
                            }
                    );
                    return;
                }
                Constant.displayDialog(
                        context,
                        "OK, Data telah diubah",
                        "Data berhasil diubah ! Silahkan masuk kembali untuk melihat perubahan data",
                        (dialog, which) -> {
                        }
                );
            }

            @Override
            public void onRetry() {
                requestOperationToServer(operationType, pass);
            }
        });
        req.enqueue(call);


    }

    private void displayDialog(int operationType) {
        CustomDialog dialog = new CustomDialog();
        dialog.setTitle("Konfirmasi Perubahan !");
        dialog.setContent("Silahkan masukkan password akun anda untuk melanjutkan perubahan");
        CustomEditText etPassword = new CustomEditText(context);
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        etPassword.setHint("Password");
        dialog.setCustomView(etPassword);
        dialog.setOnClickPositive((dialog1, which) -> requestOperationToServer(
                operationType,
                etPassword.getText().toString().trim()
        ));
        dialog.setOnClickNegative(dialogInterface -> {
        });
        dialog.show(context);
    }

}
