/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.SignUp;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
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
import com.meimodev.sitouhandler.databinding.ActivityConfirmAccountBinding;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ConfirmAccount extends AppCompatActivity {

    private static final String TAG = "ConfirmAccount";

    Validator validator;

    private ActivityConfirmAccountBinding b;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityConfirmAccountBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

//        ButterKnife.bind(this);

        Constant.changeStatusColor(this, R.color.background);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        String str = getIntent().getStringExtra("phone");

        validator = new Validator(this);
        if (!StringUtils.isEmpty(str)) {
            b.textInputLayoutPhone.getEditText().setText(str);
            if (Constant.coolDownMilliSecondsLeft == 0) b.btnGetCode.callOnClick();
        }

//        b.textInputLayoutCode.setVisibility(View.GONE);

        if (Constant.coolDownMilliSecondsLeft != 0) syncCoolDown();

        b.btnConfirm.setOnClickListener(onClickConfirm);
        b.btnGetCode.setOnClickListener(onClickRequestCode);
    }

    private final View.OnClickListener onClickConfirm = v -> {
        b.textInputLayoutPhone.setError(validator.validatePhone(b.textInputLayoutPhone));
        b.textInputLayoutCode.setError(validator.validateEmpty(b.textInputLayoutCode));

        if (b.textInputLayoutPhone.getError() == null && b.textInputLayoutCode.getError() == null) {
            sendDataToServer(
                    "CONFIRM",
                    b.textInputLayoutPhone.getEditText().getText().toString(),
                    b.textInputLayoutCode.getEditText().getText().toString()
            );
        }

    };

    private final View.OnClickListener onClickRequestCode = v -> {
        b.textInputLayoutPhone.setError(validator.validatePhone(b.textInputLayoutPhone));

        if (b.textInputLayoutPhone.getError() == null) {
            sendDataToServer(
                    "REQUEST",
                    b.textInputLayoutPhone.getEditText().getText().toString(),
                    ""
            );
        }
    };

    void sendDataToServer(String type, String phone, String code) {

        IssueRequestHandler req = new IssueRequestHandler(findViewById(android.R.id.content));
        Call<ResponseBody> call = RetrofitClient.getInstance(null).getApiServices().confirmAccount(
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
                            "Perhatian!",
                            message,
                            true,
                            (dialog, which) -> {
                            },
                            null,
                            dialog -> syncCoolDown()

                    );
                } else {
                    Constant.displayDialog(
                            ConfirmAccount.this,
                            "Perhatian!",
                            message,
                            true,
                            (dialog, which) -> {
                            },
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
                b.btnGetCode.setText(str);
                b.btnGetCode.setEnabled(false);
                Constant.coolDownMilliSecondsLeft = millisUntilFinished;
            }

            public void onFinish() {
                b.btnGetCode.setText("Kirim Kode");
                b.btnGetCode.setEnabled(true);
                Constant.coolDownMilliSecondsLeft = 0;
            }
        };


        timer.start();
    }

}
