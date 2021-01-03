package com.meimodev.sitouhandler.ForgetAccount;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.meimodev.sitouhandler.databinding.ActivityForgetBinding;
import com.meimodev.sitouhandler.databinding.ActivitySigninBinding;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ForgetAccount extends AppCompatActivity {

    private ActivityForgetBinding b;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityForgetBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Constant.changeStatusColor(this, R.color.background);

        b.btnForget.setOnClickListener(forgetClickListener);

    }

    View.OnClickListener forgetClickListener = view -> {

        EditText etPhone =b.textInputLayoutPhone.getEditText();

        if (etPhone!= null) etPhone.clearFocus();

        b.textInputLayoutPhone.setError(null);

        String phone = etPhone.getText().toString();


        if (phone.length() < 11 || phone.length() > 13) {
            b.textInputLayoutPhone.setError("Jumlah nomor tidak valid");
            return;
        }

        if (!phone.startsWith("0")) {
            b.textInputLayoutPhone.setError("Nomor Telepon dimulai dengan angka 0");
            return;
        }

        fetchData();

    };

    private void fetchData() {
        String email = b.textInputLayoutPhone.getEditText().getText().toString().trim();
        IssueRequestHandler requestHandler = new IssueRequestHandler(findViewById(android.R.id.content));
        Call<ResponseBody> call = RetrofitClient.getInstance(null).getApiServices().findUserByPhone(
                email
        );
        requestHandler.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {
            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                if (message.equals("OK")) {
                    b.textInputLayoutPhone.setError(null);
                    b.btnForget.setEnabled(true);

                    String id = res.getData().getString("user_id");
                    String selectedReason = ((RadioButton) findViewById(b.radioGroupReasons.getCheckedRadioButtonId())).getText().toString();

                   String WA_message = "TOU-" + selectedReason + "-(KEY_FORGET_ACCOUNT)" + id;

                    Constant.WhatsAppSendMessage(
                            ForgetAccount.this,
                            Constant.DEV_WA_NUMBER,
                            WA_message
                    );
                    finish();

                } else {
                    b.textInputLayoutPhone.setError(message);
                    Constant.displayDialog(ForgetAccount.this, null, message, (dialog, which) -> {
                    });
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
