package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Cheif;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.meimodev.sitouhandler.ApiServices;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Dashboard.NavFragment.Notification_Model;
import com.meimodev.sitouhandler.Helper.APIError;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;

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

public class NavFragment_Chief_ManageServiceArea extends Fragment {

    private static final String TAG = "_Chief_Manage";

    private Context context;
    private View rootView;

    @BindView(R.id.layout_cardHolder)
    LinearLayout llCardHolder;


    private ArrayList<ManageServiceAreaModel> priests;

    private View progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_fragment_chief_manage_service_area, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        progress = Constant.makeProgressCircle(rootView);


        fetchData();

        return rootView;
    }

    private void fetchData() {
        progress.setVisibility(View.VISIBLE);

        ApiServices api = RetrofitClient.getInstance(SharedPrefManager.load(context, SharedPrefManager.KEY_ACCESS_TOKEN).toString()).getApiServices();
        Call<ReadJSONNavFragmentChiefManageServiceArea> call = api.getServiceArea(((int) SharedPrefManager.getInstance(context).loadUserData(SharedPrefManager.KEY_MEMBER_ID)));
        Callback<ReadJSONNavFragmentChiefManageServiceArea> callback;
        callback = new Callback<ReadJSONNavFragmentChiefManageServiceArea>() {

            @Override
            public void onResponse(Call<ReadJSONNavFragmentChiefManageServiceArea> call, Response<ReadJSONNavFragmentChiefManageServiceArea> response) {
                if (response.isSuccessful()) {
                    ReadJSONNavFragmentChiefManageServiceArea res = response.body();

                    if (!res.isError()) {

                        // proceed
                        priests = new ArrayList<>();

                        for (ReadJSONNavFragmentChiefManageServiceArea.Priest priest : res.getData().getPriests()) {
                            priests.add(new ManageServiceAreaModel(
                                    priest.getServiceAreaId(),
                                    priest.getMemberId(),
                                    priest.getName(),
                                    priest.getDomicileColumn(),
                                    Integer.valueOf(priest.getFromColumn()),
                                    Integer.valueOf(priest.getToColumn())
                            ));
                        }

                        setupCardViews();

                        Log.e(TAG, "onResponse: response return success proceeding");
                    } else {

                        Constant.displayDialog(
                                context,
                                null,
                                res.getMessage(),
                                true,
                                (dialogInterface, i) -> {
                                },
                                null
                        );
                        Log.e(TAG, "onResponse: response return success but error");
                    }

                } else {
                    APIUtils.parseError(context, response);
                }
            }

            @Override
            public void onFailure(Call<ReadJSONNavFragmentChiefManageServiceArea> call, Throwable t) {
                Log.e(TAG, "onRetry: ", t);
                progress.setVisibility(View.INVISIBLE);
                Constant.makeFailFetch(rootView, view -> {
                    progress = Constant.makeProgressCircle(rootView);
                    fetchData();
                });
            }
        };
        call.enqueue(callback);


    }

    private void setupCardViews() {
        ArrayList<View> cardViews = new ArrayList<>();
        if (llCardHolder.getChildCount() != 0) llCardHolder.removeAllViews();

        for (int i = 0; i < priests.size(); i++) {
            ManageServiceAreaModel model = priests.get(i);
            View view = getLayoutInflater().inflate(R.layout.resource_manage_service_area, llCardHolder, false);
            CardView cvViewHolder = view.findViewById(R.id.layout_cardHolder);
            TextView tvName = view.findViewById(R.id.textView_name);
            TextView tvDomicile = view.findViewById(R.id.textView_domicile);

            CustomEditText etFrom = view.findViewById(R.id.editText_fromColumn);
            etFrom.setText(model.getFromColumn());
            etFrom.setAsNoLeadingZero();
            etFrom.clearFocus();
            etFrom.setOnFocusChangeListener((view12, b) -> context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED)));

            CustomEditText etTo = view.findViewById(R.id.editText_toColumn);
            etTo.setText(model.getToColumn());
            etTo.setAsNoLeadingZero();
            etTo.clearFocus();
            etTo.setOnFocusChangeListener((view13, b) -> context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED)));


            cvViewHolder.setOnClickListener(view1 -> {
                context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));
                if (etFrom.isFocused()) {
                    etTo.requestFocus();
                } else if (etTo.isFocused()) {
                    etFrom.requestFocus();
                } else {
                    etFrom.requestFocus();
                }
            });

            tvName.setText(model.getName());
            tvDomicile.setText(model.getDomicile());

            Button btnSave = view.findViewById(R.id.button_save);
            btnSave.setVisibility(View.VISIBLE);
            btnSave.setOnClickListener(view1 -> {

                if (etFrom.getError() != null
                        || etTo.getError() != null
                        || etTo.getText().toString().length() == 0
                        || etFrom.getText().toString().length() == 0
                ) {
                    Constant.displayDialog(context,
                            "Ups...",
                            "Silahkan pastikan 'Dari Kolom' / 'Hingga Kolom' tidak kosong atau berawalan 0 ",
                            true,
                            (dialogInterface, i1) -> {
                            },
                            null
                    );
                } else {
                    sendData(
                            model.getId(),
                            Integer.valueOf(etFrom.getText().toString()),
                            Integer.valueOf(etTo.getText().toString())
                    );
                }
            });

            if (i == priests.size() - 1) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 450);
                cvViewHolder.setLayoutParams(params);
            }

            cardViews.add(view);
        }

        for (View view : cardViews) {
            llCardHolder.addView(view);
        }

        if (progress.getVisibility() == View.VISIBLE) progress.setVisibility(View.INVISIBLE);
        if (llCardHolder.getVisibility() == View.GONE) llCardHolder.setVisibility(View.VISIBLE);
    }

    private void sendData(int serviceAreaId, int fromColumn, int toColumn) {

        if (llCardHolder.getVisibility() == View.VISIBLE) llCardHolder.setVisibility(View.GONE);

        ApiServices api = RetrofitClient.getInstance(
                SharedPrefManager.load(context, SharedPrefManager.KEY_ACCESS_TOKEN).toString()
        ).getApiServices();

        Call<ResponseBody> call = api.setServiceArea(serviceAreaId, fromColumn, toColumn);
        Callback<ResponseBody> callback = new Callback<ResponseBody>() {

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    try {
                        JSONObject obj = new JSONObject(response.body().string());

                        if (!obj.getBoolean("error")) {

                            // proceed
                            fetchData();

                            Log.e(TAG, "onResponse: response return success proceeding");
                        } else {

                            Constant.displayDialog(
                                    context,
                                    null,
                                    obj.getString("message"),
                                    true,
                                    (dialogInterface, i) -> {
                                    },
                                    null
                            );
                            Log.e(TAG, "onResponse: response return success but error");
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    APIUtils.parseError(context, response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onRetry: ", t);
                Constant.toggleViewVisibility(progress);
                Constant.makeFailFetch(rootView, view -> {
                    progress = Constant.makeProgressCircle(rootView);
                    sendData(serviceAreaId, fromColumn, toColumn);
                });
            }
        };
        call.enqueue(callback);
    }


    ///////////////////////////////////////////////////////////////////////////
    // Helper Class
    ///////////////////////////////////////////////////////////////////////////

    class ManageServiceAreaModel {

        private String name, domicile;
        private int id;
        private int memberId;
        private int fromColumn, toColumn;

        ManageServiceAreaModel(int id, int memberId, String name, String domicile, int fromColumn, int toColumn) {
            this.id = id;
            this.memberId = memberId;
            this.name = name;
            this.domicile = domicile;
            this.fromColumn = fromColumn;
            this.toColumn = toColumn;
        }

        int getId() {
            return id;
        }

        String getName() {
            return name;
        }

        String getDomicile() {
            return "Berdomisili di " + domicile;
        }

        String getFromColumn() {
            if (fromColumn != 0)
                return String.valueOf(fromColumn);
            else return "";
        }

        String getToColumn() {
            if (toColumn != 0)
                return String.valueOf(toColumn);
            else return "";
        }

        public int getMemberId() {
            return memberId;
        }
    }
}
