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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.hmomeni.progresscircula.ProgressCircula;
import com.meimodev.sitouhandler.ApiServices;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;

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

    @BindView(R.id.editText_search)
    EditText etSearch;
    @BindView(R.id.recyclerView_adding)
    RecyclerView rvAdding;
    @BindView(R.id.layout_progressHolder)
    RelativeLayout layoutProgress;
    @BindView(R.id.layout_main)
    LinearLayout llMain;

    private BroadcastReceiver onSelectedItemRecyclerView;
    private BroadcastReceiver onSelectedItemRecyclerViewAlreadySelected;

    public static final int OPERATION_ADD_NAME_REGISTERED_ONLY = 2;
    public static final int OPERATION_ADD_NAME_WITH_UNREGISTERED = 1;
    public static int OPERATION_TYPE;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding);
        ButterKnife.bind(this);
        registerBroadcastReceiver();

        View v = LayoutInflater.from(Adding.this).inflate(R.layout.resource_custom_progress_bar_progressing, layoutProgress, false);
        ProgressCircula p = v.findViewById(R.id.pc);
        p.setSpeed(3);
        p.setIndeterminate(true);
        p.setRimWidth(6);
        p.startRotation();
        p.setRimColor(getResources().getColor(R.color.colorAccent));
        p.setShowProgress(false);
        layoutProgress.addView(v);
        layoutProgress.setVisibility(View.GONE);

        OPERATION_TYPE = getIntent().getIntExtra("OPERATION_TYPE", OPERATION_ADD_NAME_WITH_UNREGISTERED);

        rvAdding.setHasFixedSize(true);
        rvAdding.setItemAnimator(new DefaultItemAnimator());
        rvAdding.setLayoutManager(new LinearLayoutManager(Adding.this));


        CountDownTimer countdownToFetchData = new CountDownTimer(1500, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                fetchData();
            }
        };
        etSearch.addTextChangedListener(new TextWatcher() {
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
                    layoutProgress.setVisibility(View.VISIBLE);
                    llMain.setVisibility(View.GONE);
                } else {
                    countdownToFetchData.cancel();
                    layoutProgress.setVisibility(View.GONE);
                    llMain.setVisibility(View.VISIBLE);

                }
                countdownToFetchData.start();
            }
        });


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

                    } else {

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

    private void fetchData() {
        if (etSearch.length() <= 1) {
            Snackbar.make(findViewById(android.R.id.content), "Kata kunci yang dicari harus melebihi 1 huruf", Snackbar.LENGTH_SHORT).show();
            return;
        }

        ApiServices apiServices = RetrofitClient.getInstance(null).getApiServices();
        Call<ResponseBody> call = apiServices.findMember(
                Guru.getInt(KEY_MEMBER_ID, 0),
                etSearch.getText().toString()
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());

                        if (!jsonObject.getBoolean("error")) {
                            Log.e(TAG, "onResponse: response error = false");

                            JSONArray data = jsonObject.getJSONArray("data");

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
                                } else {
                                    add = new Adding_RecyclerModel(
                                            model.getInt("member_id"),
                                            model.getString("full_name"),
                                            "",
                                            "",
                                            ""
                                    );
                                    add.setUnregistered(true);
                                }
                                add.setBaptis(!model.getString("nomor_surat_baptis").isEmpty());
                                add.setSidi(!model.getString("nomor_surat_sidi").isEmpty());
                                add.setNikah(!model.getString("nomor_surat_nikah").isEmpty());

                                items.add(add);

                            }

                            if (layoutProgress.getVisibility() == View.VISIBLE)
                                layoutProgress.setVisibility(View.GONE);
                            if (llMain.getVisibility() != View.VISIBLE)
                                llMain.setVisibility(View.VISIBLE);

                            Log.e(TAG, "onResponse: attaching recyclerview adapter");
                            Log.e(TAG, "onResponse: items size = " + items.size());
                            rvAdding.setAdapter(new Adding_RecyclerAdapter(Adding.this, items));

                        }


                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    APIUtils.parseError(Adding.this, response);
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                Log.e(TAG, "onRetry: "
                        + getApplicationInfo().className
                        + ": ", t);
                makeFailFetch(findViewById(android.R.id.content), view -> {
                    makeProgressCircle(findViewById(android.R.id.content)).setVisibility(View.VISIBLE);
                    fetchData();
                });
            }
        });


        // hide search result is visible
        // show loading screen while data searching on back-end
        // hide loading when response returned
        // show search result if response is success
        // show error if response is error
    }

    @Override
    protected void onStop() {
        unregisterReceiver(onSelectedItemRecyclerView);
        unregisterReceiver(onSelectedItemRecyclerViewAlreadySelected);
        super.onStop();
    }


}
