/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.SignUp.SignUp;
import com.meimodev.sitouhandler.Validator;
import com.meimodev.sitouhandler.databinding.ActivitySigninFirebaseBinding;
import com.squareup.picasso.RequestHandler;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class SignInFirebase extends AppCompatActivity {

    private static final String TAG = "SignInFirebase";

    private ActivitySigninFirebaseBinding b;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    private int currState = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivitySigninFirebaseBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Constant.changeStatusColor(this, R.color.background);
        Constant.checkSystemStatus(this);

//        Check if device already logged in
        mAuth = FirebaseAuth.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (currentUser != null) {
            b.layoutHeader.setVisibility(View.INVISIBLE);
            setLoading(true);
            currentUser.getIdToken(true).addOnCompleteListener(task -> {
                String token = task.getResult().getToken();
                signInToBackEnd(currentUser.getPhoneNumber(), token);
            });
        } else {
            firstState();
        }


    }

    private void firstState() {
        currState = 1;
        b.textInputLayoutPhone.setVisibility(View.VISIBLE);
        b.textInputLayoutPhone.setError(null);
        b.layoutReSend.setVisibility(View.GONE);
        b.textInputLayoutCode.setVisibility(View.GONE);
        b.layoutPhone.setVisibility(View.GONE);
        b.layoutSignUp.setVisibility(View.GONE);

        b.progressButton.setVisibility(View.GONE);

        b.btnContinue.setOnClickListener(view -> {
            String phone = b.textInputLayoutPhone.getEditText().getText().toString();
            if (StringUtils.isEmpty(phone)) {
                b.textInputLayoutPhone.setError("Tidak bisa kosong");
                b.textInputLayoutPhone.requestFocus();
                return;
            }
            if (StringUtils.length(phone) < 12 && StringUtils.length(phone) > 13) {
                b.textInputLayoutPhone.setError("Nomor telepon terdiri dari 12 - 13 angka");
                b.textInputLayoutPhone.requestFocus();
                return;
            }
            if (!phone.substring(0, 1).contentEquals("0")) {
                b.textInputLayoutPhone.setError("Nomor telepon dimulai dengan 0");
                b.textInputLayoutPhone.requestFocus();
                return;
            }

            setLoading(true);

            phone = phone.replaceFirst("[0]", "+62");
            sendVerificationCode(phone);
            secondState();

        });

    }

    private void secondState() {
        currState = 2;
        b.textInputLayoutPhone.setVisibility(View.GONE);
        b.layoutSignUp.setVisibility(View.GONE);

        b.textInputLayoutCode.setVisibility(View.VISIBLE);
        b.layoutReSend.setVisibility(View.VISIBLE);
        b.textInputLayoutCode.setError(null);
        b.layoutPhone.setVisibility(View.VISIBLE);

        if (Constant.coolDownMilliSecondsLeft <= 0) syncCoolDown();

        String phone = b.textInputLayoutPhone.getEditText().getText().toString();
        b.textViewPhone.setText(phone);

        b.layoutReSend.setOnClickListener(view -> {
            resendVerificationCode(
                    phone.replaceFirst("[0]", "+62"),
                    mResendToken);
            syncCoolDown();
        });

        b.btnContinue.setOnClickListener(view -> {
            String code = b.textInputLayoutCode.getEditText().getText().toString();

            if (StringUtils.isEmpty(code)) {
                b.textInputLayoutCode.setError("Tidak bisa kosong");
                return;
            }

            if (code.length() < 6) {
                b.textInputLayoutCode.setError("Cek kembali kode yang dikirimkan ");
                return;
            }

            verifyVerificationCode(code);

        });
    }

    private void thirdState() {
        currState = 3;
        b.textInputLayoutSex.setError(null);
        b.textInputLayoutFirstName.setError(null);
        b.textInputLayoutLastName.setError(null);

        b.textInputLayoutCode.getEditText().setText("");

        b.textInputLayoutCode.setVisibility(View.GONE);
        b.layoutPhone.setVisibility(View.GONE);
        b.layoutReSend.setVisibility(View.GONE);
        b.textInputLayoutPhone.setVisibility(View.GONE);
        b.textInputLayoutCode.setVisibility(View.GONE);

        b.layoutHeader.setVisibility(View.VISIBLE);
        b.layoutSignUp.setVisibility(View.VISIBLE);
        //setup drop down
        b.textInputLayoutSex.getEditText().setShowSoftInputOnFocus(false);
        b.textInputLayoutSex.getEditText().setFocusable(false);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.resoruce_dropdown_popup_large,
                new String[]{"Cowo Gaga", "Cewe Pasung"});
        b.dropdown.setAdapter(adapter);

        b.btnContinue.setOnClickListener(view -> {
            //validate input
            Validator v = new Validator(SignInFirebase.this);
            if (v.validateName(b.textInputLayoutFirstName) != null) return;
            if (v.validateName(b.textInputLayoutLastName) != null) return;

            String phone, fName, lName, sex;
            phone = b.textInputLayoutPhone.getEditText().getText().toString();
            fName = b.textInputLayoutFirstName.getEditText().getText().toString();
            lName = b.textInputLayoutLastName.getEditText().getText().toString();
            sex = b.textInputLayoutSex.getEditText().getText().toString();

            if (sex.contentEquals(adapter.getItem(0))) {
                sex = "Laki-laki";
                b.textInputLayoutSex.setError(null);
            } else if (sex.contentEquals(adapter.getItem(1))) {
                sex = "Perempuan";
                b.textInputLayoutSex.setError(null);
            } else {
                b.textInputLayoutSex.setError("Jenis apa ?");
                return;
            }


            if (StringUtils.isEmpty(phone)) {
                phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            }
            signUpToBackend(phone, fName, lName, sex);

        });
    }

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private void sendVerificationCode(String phone) {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                String code = credential.getSmsCode();
                if (StringUtils.isNotEmpty(code)) {

                    verifyVerificationCode(code);
                    b.textInputLayoutCode.getEditText().setText(code);
                }

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                String msg = "Terjadi kesalahan, silahkan kembali sesaat lagi";
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    msg = "Invalid Request! ".concat(msg);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    msg = "-_- QUOTA EXCEEDED, silahkan coba sesaat lagi";
                } else if (e instanceof FirebaseNetworkException) {
                    // Network Failed
                    msg = "Koneksi Internet bermasalah, silahkan pastikan anda terhubung internet dan coba lagi";
                }

                Constant.displayDialog(SignInFirebase.this,
                        "Perhatian !",
                        msg,
                        false,
                        (dialogInterface, i) -> {
                        },
                        null,
                        dialogInterface -> finish()
                );

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                setLoading(false);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private String mVerificationId;

    private void verifyVerificationCode(String code) {
        setLoading(true);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        FirebaseUser user = task.getResult().getUser();

                        // Update UI
                        signInToBackEnd(user.getPhoneNumber(), user.getIdToken(false).getResult().getToken());
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                            setLoading(false);
                            b.textInputLayoutCode.setError("Kode yang dimasukkan salah");
                        }
                    }
                });
    }

    private void signInToBackEnd(String phone, String idToken) {
        setLoading(true);
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setContext(this);
        req.setIntention(new Throwable());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {

                JSONObject data = res.getData();

                if (StringUtils.contains(res.getMessage(), "not exist")) {
                    /*
                     * sign-up
                     */

                    setLoading(false);
                    thirdState();

                    return;
                }
//
                Guru.putInt(Constant.KEY_USER_ID, data.getInt("id"));
                Guru.putString(Constant.KEY_USER_FULL_NAME, data.getString("full_name"));
//                Guru.putString(Constant.KEY_USER_AGE, data.getString("age"));
                Guru.putString(Constant.KEY_USER_SEX, data.getString("sex"));
                Guru.putString(Constant.KEY_USER_ACCESS_TOKEN, data.getString("access_token"));

                Intent i = new Intent(SignInFirebase.this, Dashboard.class);
                finish();
                startActivity(i);


            }

            @Override
            public void onRetry() {

            }
        });
        req.setOnRequestHandlerFailure(() -> {
            Constant.displayDialog(
                    this,
                    "Perhatian !",
                    "Koneksi internet bermasalah, Silahakan kembali sesaat lagi",
                    false,
                    (dialogInterface, i) -> {
                    },
                    null,
                    dialogInterface -> finishAffinity()
            );
        });
        req.setOnRequestHandlerResponseError(message -> {
            if (message.contains("not exist")) {
                thirdState();
            }

        });

        Call<ResponseBody> call = RetrofitClient.getInstance(null).getApiServices().signInFirebase(
                phone, idToken
        );
        req.backGroundRequest(call);
    }

    private void signUpToBackend(String phone, String fName, String lName, String sex) {
        setLoading(true);
        IssueRequestHandler req = new IssueRequestHandler(this);
        req.setIntention(new Throwable());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {

                JSONObject data = res.getData();

                Guru.putInt(Constant.KEY_USER_ID, data.getInt("uid"));
                Guru.putString(Constant.KEY_USER_FULL_NAME, data.getString("name"));
                Guru.putString(Constant.KEY_USER_SEX, data.getString("sex"));
                Guru.putString(Constant.KEY_USER_ACCESS_TOKEN, data.getString("token"));

                Intent i = new Intent(SignInFirebase.this, Dashboard.class);
                finish();
                startActivity(i);
            }

            @Override
            public void onRetry() {

            }
        });
        req.setOnRequestHandlerResponseError(message -> setLoading(true));
        req.setOnRequestHandlerFailure(() -> setLoading(true));

        req.backGroundRequest(
                RetrofitClient.getInstance(null).getApiServices().signUpFirebase(
                        phone, fName, lName, sex)
        );
    }

    private void resendVerificationCode(String phone,
                                        PhoneAuthProvider.ForceResendingToken token) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)
                .setForceResendingToken(token)// OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void syncCoolDown() {
        long timeLeft = Constant.coolDownMilliSecondsLeft != 0 ?
                Constant.coolDownMilliSecondsLeft : 60000;

        TextView tvResend = ((TextView) b.layoutReSend.getChildAt(0));

        if (b.progressSend.getVisibility() != View.VISIBLE)
            b.progressSend.setVisibility(View.VISIBLE);

        CountDownTimer timer = new CountDownTimer(timeLeft, 1000) {
            public void onTick(long millisUntilFinished) {

                Constant.coolDownMilliSecondsLeft = millisUntilFinished;
                b.layoutReSend.setEnabled(false);
                b.layoutReSend.setClickable(false);
                tvResend.setText("Kirim Ulang kode (".concat(String.valueOf(millisUntilFinished / 1000)).concat(")"));
                tvResend.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            public void onFinish() {
                b.layoutReSend.setEnabled(true);
                b.layoutReSend.setClickable(true);
                tvResend.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvResend.setText("Kirim Ulang kode");
                Constant.coolDownMilliSecondsLeft = 0;
                b.progressSend.setVisibility(View.GONE);
            }
        };


        timer.start();
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            b.btnContinue.setEnabled(false);
            b.btnContinue.setVisibility(View.INVISIBLE);

            b.progressButton.setVisibility(View.VISIBLE);
        } else {
            b.btnContinue.setEnabled(true);
            b.btnContinue.setVisibility(View.VISIBLE);

            b.progressButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (currState == 2) {
            firstState();
            return;
        }
        if (currState == 3) {
            firstState();
            FirebaseAuth.getInstance().signOut();
            Guru.clear();
            SharedPrefManager.getInstance(this).logout();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (b.textInputLayoutPhone.getError() != null) b.textInputLayoutPhone.setError(null);

//        String phone = Guru.getString("TEMP_PHONE", "");
//        String pass = Guru.getString("TEMP_PASS", "");
//        if (StringUtils.isNotEmpty(phone) && StringUtils.isNotEmpty(pass)) {
//            b.textInputLayoutPhone.getEditText().setText(phone);
//            b.btnContinue.callOnClick();
//            Guru.remove("TEMP_PHONE");
//            Guru.remove("TEMP_PASS");
//        }

    }


}
