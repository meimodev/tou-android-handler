package com.meimodev.sitouhandler.ForgetAccount;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;

public class ForgetAccount extends AppCompatActivity {

    private static final String TAG = "ForgetAccount";

    @BindView(R.id.editText_email)
    CustomEditText etEmail;
    @BindView(R.id.textInputLayout_email)
    TextInputLayout tilEmail;
    @BindView(R.id.btn_forget)
    Button btnForget;
    @BindView(R.id.radioGroup_reasons)
    RadioGroup rgReasons;

    String WA_number = "+6285756681077";
    String WA_message;

//    View progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.btn_forget)
    void onClick() {

        etEmail.clearFocus();

        tilEmail.setError(null);

        String email = etEmail.getText().toString();

        int countAtSymbol = StringUtils.countMatches(email, "@");
        int countDotSymbol = StringUtils.countMatches(email, ".");


        if (email.length() < 1) {
            tilEmail.setError("Email tidak bisa kosong");
            return;
        }

        if (countAtSymbol != 1 || (countDotSymbol != 1 && countDotSymbol != 2)) {
            tilEmail.setError("Email tidak valid");
            return;
        }

        fetchData();

    }

    private void fetchData() {
        String email = etEmail.getText().toString().trim();
        IssueRequestHandler requestHandler = new IssueRequestHandler(findViewById(android.R.id.content));
        Call call = RetrofitClient.getInstance(null).getApiServices().findUserByPhone(
                email
        );
        requestHandler.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {
            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                if (message.equals("OK")) {
                    tilEmail.setError(null);
                    btnForget.setEnabled(true);

                    String id = res.getData().getString("user_id");
                    String selectedReason = ((RadioButton) findViewById(rgReasons.getCheckedRadioButtonId())).getText().toString();
//                    Log.e(TAG, "onSuccess: "+selectedReason );
                    WA_message = "TOU-"+selectedReason + "-(KEY_FORGET_ACCOUNT)" + id;

                    Constant.WhatsAppSendMessage(
                            ForgetAccount.this,
                            WA_number,
                            WA_message
                    );
                    finish();

                } else {
                    tilEmail.setError(message);
                    Constant.displayDialog(
                            ForgetAccount.this,
                            null,
                            message,
                            true,
                            (dialog, which) -> {
                                dialog.dismiss();
                            },
                            null
                    );
                }
            }

            @Override
            public void onRetry() {
                fetchData();
            }
        });

        requestHandler.enqueue(call);


    }


}
