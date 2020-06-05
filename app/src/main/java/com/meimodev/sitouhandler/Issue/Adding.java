/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Issue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.hmomeni.progresscircula.ProgressCircula;
import com.meimodev.sitouhandler.ApiServices;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;
import com.meimodev.sitouhandler.databinding.ActivityAddingBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.meimodev.sitouhandler.Constant.*;

public class Adding extends AppCompatActivity {

    private final String TAG = "ADDING";

    public static final String ACTION_SELECT_ITEM_RECYCLER_VIEW = "onItemSelected";
    public static final String ACTION_ALREADY_SELECTED_RECYCLER_VIEW = "alreadySelected";

    public static final int REQUEST_CODE_PERSONAL_NAME = 1;


//    RelativeLayout layoutProgress;
//    LinearLayout layoutMain;

    private ActivityAddingBinding b;

    private BroadcastReceiver onSelectedItemRecyclerView;
    private BroadcastReceiver onSelectedItemRecyclerViewAlreadySelected;

    public static final int OPERATION_ADD_NAME_REGISTERED_ONLY = 2;
    public static final int OPERATION_ADD_NAME_WITH_UNREGISTERED = 1;
    public static final int OPERATION_FIND_USER = 3;


    public static int OPERATION_TYPE;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityAddingBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        changeStatusColor(this, R.color.colorPrimary);

        registerBroadcastReceiver();

        OPERATION_TYPE = getIntent().getIntExtra("OPERATION_TYPE", OPERATION_ADD_NAME_WITH_UNREGISTERED);

        /*
         * Init First Views
         */
//        initProgressManual();
//        layoutProgress = b.layoutProgressHolder;
//        layoutMain = b.layoutMain;

        initEditTextSearch();
    }

    private void registerBroadcastReceiver() {
        onSelectedItemRecyclerView = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().contentEquals(ACTION_SELECT_ITEM_RECYCLER_VIEW)) {
                    Intent resultIntent = new Intent();

                    if (intent.getBooleanExtra("unregistered", false)) {

                        resultIntent.putExtra("model.name", intent.getStringExtra("model.name"));
                        resultIntent.putExtra("unregistered", intent.getBooleanExtra("unregistered", false));

                    }
                    else {

                        resultIntent.putExtra("model.id", intent.getIntExtra("model.id", 0));
                        resultIntent.putExtra("model.name", intent.getStringExtra("model.name"));
                        resultIntent.putExtra("model.bod", intent.getStringExtra("model.bod"));
                        resultIntent.putExtra("model.kol", intent.getStringExtra("model.kol"));
                        resultIntent.putExtra("model.img", intent.getStringExtra("model.img"));
                        resultIntent.putExtra("model.cat", intent.getStringExtra("model.cat"));
                        resultIntent.putExtra("model.bap", intent.getBooleanExtra("model.bap", false));
                        resultIntent.putExtra("model.sid", intent.getBooleanExtra("model.sid", false));
                        resultIntent.putExtra("model.nik", intent.getBooleanExtra("model.nik", false));
                        resultIntent.putExtra("model.cathead", intent.getBooleanExtra("model.cathead", false));

                    }
                    Adding.this.setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }

        };
        registerReceiver(onSelectedItemRecyclerView, new IntentFilter(ACTION_SELECT_ITEM_RECYCLER_VIEW));

        onSelectedItemRecyclerViewAlreadySelected = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().contentEquals(ACTION_ALREADY_SELECTED_RECYCLER_VIEW)) {
                    Toast.makeText(context, "Nama tersebut telah dipilih sebelumnya", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        };
        registerReceiver(onSelectedItemRecyclerViewAlreadySelected, new IntentFilter(ACTION_ALREADY_SELECTED_RECYCLER_VIEW));
    }

    private void initEditTextSearch() {
        CountDownTimer countdownToFetchData = new CountDownTimer(1500, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (validateInput()) findDataOnServer();
            }
        };

        b.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                countdownToFetchData.cancel();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 1) {
                }
                else {
                    countdownToFetchData.cancel();
                }
                countdownToFetchData.start();
            }
        });
        b.textInputLayoutSearch.requestFocus();

    }

    private boolean validateInput() {
        if (b.editTextSearch.length() <= 1) {
            Snackbar.make(findViewById(android.R.id.content), "Kata kunci yang dicari harus melebihi 1 huruf", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void findDataOnServer() {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());

            /*
            * Find USER
            */
        if (OPERATION_TYPE == OPERATION_FIND_USER) {
            req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
                @Override
                public void onTry() {

                }

                @Override
                public void onSuccess(APIWrapper res, String message) throws JSONException {
                    InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    imm.hideSoftInputFromWindow(b.editTextSearch.getWindowToken(), 0);

                    proceedFindUser(res.getDataArray());
                }

                @Override
                public void onRetry() {
                    InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    imm.hideSoftInputFromWindow(b.editTextSearch.getWindowToken(), 0);
                    findDataOnServer();
                }
            });

            req.enqueue(RetrofitClient.getInstance(null).getApiServices().findUserByName(
                    b.editTextSearch.getText().toString()
            ));

            return;
        }


        /*
         * Find MEMBER
         */
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                imm.hideSoftInputFromWindow(b.editTextSearch.getWindowToken(), 0);

                proceedFindMember(res.getDataArray());
            }

            @Override
            public void onRetry() {
                InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                imm.hideSoftInputFromWindow(b.editTextSearch.getWindowToken(), 0);
                findDataOnServer();
            }
        });

        req.enqueue(RetrofitClient.getInstance(null).getApiServices().findMember(
                Guru.getInt(KEY_MEMBER_ID, 0),
                b.editTextSearch.getText().toString()
        ));

    }

    private void proceedFindMember(JSONArray data) throws JSONException {
        ArrayList<Adding_RecyclerModel> items = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject model = data.getJSONObject(i);
            Adding_RecyclerModel add;
            if (model.getInt("member_id") != 0) {
                add = new Adding_RecyclerModel(
                        model.getInt("member_id"),
                        model.getString("full_name"),
                        model.getString("DOB"),
                        model.getString("column"),
                        model.getString("image_url")
                );
            }
            else {
                add = new Adding_RecyclerModel(
                        model.getInt("member_id"),
                        model.getString("full_name"),
                        "",
                        "",
                        ""
                );
                add.setUnregistered(true);
            }
            add.setBaptis(!model.isNull("nomor_surat_baptis"));
            add.setSidi(!model.isNull("nomor_surat_sidi"));
            add.setNikah(!model.isNull("nomor_surat_nikah"));
            items.add(add);
        }

        b.recyclerViewAdding.setHasFixedSize(true);
        b.recyclerViewAdding.setItemAnimator(new DefaultItemAnimator());
        b.recyclerViewAdding.setLayoutManager(new LinearLayoutManager(Adding.this));
        b.recyclerViewAdding.setAdapter(new Adding_RecyclerAdapter(Adding.this, items));
        evaluateItemNotFoundLayout(items);
    }

    private void proceedFindUser(JSONArray data) throws JSONException {
        ArrayList<Adding_RecyclerModel> items = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject model = data.getJSONObject(i);
            Adding_RecyclerModel add;
            add = new Adding_RecyclerModel(
                    model.getInt("user_id"),
                    model.getString("full_name"),
                    model.getString("dob"),
                    model.getString("sex"),
                    ""
            );

            items.add(add);
        }

        b.recyclerViewAdding.setHasFixedSize(true);
        b.recyclerViewAdding.setItemAnimator(new DefaultItemAnimator());
        b.recyclerViewAdding.setLayoutManager(new LinearLayoutManager(Adding.this));
        b.recyclerViewAdding.setAdapter(new Adding_RecyclerAdapter(Adding.this, items));
        evaluateItemNotFoundLayout(items);

    }

    private void evaluateItemNotFoundLayout(ArrayList<Adding_RecyclerModel> items) {
        if (items.size() == 0){
            b.recyclerViewAdding.setVisibility(View.GONE);
            b.layoutNotFound.getRoot().setVisibility(View.VISIBLE);
        } else {
            b.recyclerViewAdding.setVisibility(View.VISIBLE);
            b.layoutNotFound.getRoot().setVisibility(View.GONE);

        }
    }
    @Override
    protected void onStop() {
        unregisterReceiver(onSelectedItemRecyclerView);
        unregisterReceiver(onSelectedItemRecyclerViewAlreadySelected);
        super.onStop();
    }


}
