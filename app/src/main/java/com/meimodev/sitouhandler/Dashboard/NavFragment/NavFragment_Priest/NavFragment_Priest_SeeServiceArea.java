package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Priest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Cheif.ReadJSONNavFragmentChiefManageServiceArea;
import com.meimodev.sitouhandler.Helper.APIUtils;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Optional;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NavFragment_Priest_SeeServiceArea extends Fragment implements View.OnClickListener {

    private static final String TAG = "NavFragment_Priest_SeeS";

    private Context context;
    private View rootView;


    @BindView(R.id.textView_title)
    TextView tvTitle;
    @BindView(R.id.layout_cardHolder)
    LinearLayout llCardHolder;

    private View progress;
    private ArrayList<ManageServiceAreaModel> priests = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_fragment_chief_manage_service_area, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        tvTitle.setText("WILAYAH PELAYANAN");

        progress = Constant.makeProgressCircle(rootView);

        rootView.setOnClickListener(this);
        return rootView;
    }

    private void setupCardViews() {
        ArrayList<View> cardViews = new ArrayList<>();

        for (int i = 0; i < priests.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.resource_manage_service_area, llCardHolder, false);
            CardView cvViewHolder = view.findViewById(R.id.layout_cardHolder);
            TextView tvName = view.findViewById(R.id.textView_name);
//            TextView tvAge = view.findViewById(R.id.textView_age);
            TextView tvDomicile = view.findViewById(R.id.textView_domicile);

            CustomEditText etFrom = view.findViewById(R.id.editText_fromColumn);
            etFrom.setText(priests.get(i).getFromColumn());
            etFrom.setAsNoLeadingZero();
            etFrom.clearFocus();
            etFrom.setFocusable(false);

            CustomEditText etTo = view.findViewById(R.id.editText_toColumn);
            etTo.setText(priests.get(i).getToColumn());
            etTo.setAsNoLeadingZero();
            etTo.clearFocus();
            etTo.setFocusable(false);

            View.OnClickListener clickListener = view1 -> {
                context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));
                Snackbar.make(rootView, "Perubahan hanya bisa dilakukan oleh KETUA JEMAAT", Snackbar.LENGTH_SHORT).show();
            };
            cvViewHolder.setOnClickListener(clickListener);
            etFrom.setOnClickListener(clickListener);
            etTo.setOnClickListener(clickListener);

            tvName.setText(priests.get(i).getName());
//            tvAge.setText(priests.get(i).getAge());
            tvDomicile.setText(priests.get(i).getDomicile());
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
    }

    private void fetchData() {

        if (progress.getVisibility() != View.VISIBLE)
            progress.setVisibility(View.VISIBLE);

        RetrofitClient.getInstance(null).getApiServices().getServiceArea(
                ((int) SharedPrefManager.getInstance(context).loadUserData(SharedPrefManager.KEY_MEMBER_ID)))
                .enqueue(new Callback<ReadJSONNavFragmentChiefManageServiceArea>() {
            @Override
            public void onResponse(Call<ReadJSONNavFragmentChiefManageServiceArea> call, Response<ReadJSONNavFragmentChiefManageServiceArea> response) {
                if (response.isSuccessful()) {
                    ReadJSONNavFragmentChiefManageServiceArea res = response.body();

                    if (progress.getVisibility() == View.VISIBLE)
                        progress.setVisibility(View.INVISIBLE);

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
                Constant.toggleViewVisibility(progress);
                Constant.makeFailFetch(rootView, view -> {
                    progress = Constant.makeProgressCircle(rootView);
                });
            }
        });

//        priests.add(new ManageServiceAreaModel(
//                0,
//                0,
//                "Pdt. Mama Jhon - Mambao, SH, M.Teol",
//                "Kolom 15",
//                1,
//                4241
//        ));
//        priests.add(new ManageServiceAreaModel(
//                0,
//                0,
//                "Pdt. Tole Batule - Batolo, SH",
//                "Kolom 15",
//                4,
//                204
//
//        ));
//        priests.add(new ManageServiceAreaModel(
//                0,
//                0,
//                "Pdt. Richard Itam - Mambao, SH, M.Teol",
//                "Kolom 560",
//                2,
//                92425
//        ));

    }

    @Override
    public void onClick(View view) {
        context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));
    }

    @Override
    public void onResume() {
        fetchData();
        super.onResume();
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
