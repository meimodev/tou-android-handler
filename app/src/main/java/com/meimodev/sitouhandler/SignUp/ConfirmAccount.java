package com.meimodev.sitouhandler.SignUp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.Constant;
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

public class ConfirmAccount extends AppCompatActivity {

    private static final String TAG = "ConfirmAccount";


    @BindView(R.id.textInputLayout_phone)
    TextInputLayout tilPhone;
    @BindView(R.id.textInputLayout_code)
    TextInputLayout tilCode;

    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.btn_getCode)
    Button btnCode;

    Validator validator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_account);
        ButterKnife.bind(this);

        String str = getIntent().getStringExtra("phone");

        validator = new Validator();
        if (!StringUtils.isEmpty(str)) {
            tilPhone.getEditText().setText(str);
            if (Constant.coolDownMilliSecondsLeft == 0) btnCode.callOnClick();
        }

//        tilCode.setVisibility(View.GONE);

        if (Constant.coolDownMilliSecondsLeft != 0) syncCoolDown();

    }

    @OnClick(R.id.btn_confirm)
    void onClickConfirm() {
        tilPhone.setError(validator.validatePhone(tilPhone));
        tilCode.setError(validator.validateEmpty(tilCode));

        if (tilPhone.getError() == null && tilCode.getError() == null) {
            sendDataToServer(
                    "CONFIRM",
                    tilPhone.getEditText().getText().toString(),
                    tilCode.getEditText().getText().toString()
            );
        }

    }

    @OnClick(R.id.btn_getCode)
    void onClickRequestCode() {
        tilPhone.setError(validator.validatePhone(tilPhone));

        if (tilPhone.getError() == null) {
            sendDataToServer(
                    "REQUEST",
                    tilPhone.getEditText().getText().toString(),
                    ""
            );
        }
    }

    void sendDataToServer(String type, String phone, String code) {

        IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
        Call call = RetrofitClient.getInstance(null).getApiServices().confirmAccount(
                type, phone, code);

        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                if (type.equals("REQUEST")) {
                    Constant.displayDialog(
                            ConfirmAccount.this,
                            "Perhatian",
                            message,
                            true,
                            (dialog, which) -> dialog.dismiss(),
                            null,
                            dialog -> syncCoolDown()

                    );
                } else {
                    Constant.displayDialog(
                            ConfirmAccount.this,
                            "Perhatian",
                            message,
                            true,
                            (dialog, which) -> dialog.dismiss(),
                            null,
                            dialog -> finish()
                    );
                }
            }

            @Override
            public void onRetry() {

            }
        });
        req.enqueue(call);
    }

    void syncCoolDown() {

        long timeLeft = Constant.coolDownMilliSecondsLeft != 0 ? Constant.coolDownMilliSecondsLeft : 60000;
        CountDownTimer timer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {
                String str =
                        "Kirim Kode"
                                .concat(" ("
                                        .concat(String.valueOf(millisUntilFinished / 1000))
                                        .concat(")"));
                btnCode.setText(str);
                btnCode.setEnabled(false);
                Constant.coolDownMilliSecondsLeft = millisUntilFinished;
            }

            public void onFinish() {
                btnCode.setText("Kirim Kode");
                btnCode.setEnabled(true);
                Constant.coolDownMilliSecondsLeft = 0;
            }
        };


        timer.start();
    }

}
