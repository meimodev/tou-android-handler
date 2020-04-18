/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Wizard.ApplyMember;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.skydoves.expandablelayout.ExpandableLayout;
import com.squareup.picasso.RequestHandler;

import org.json.JSONException;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class Fragment_Designated extends Fragment {
    private static final String TAG = "Fragment_firstPage";
    private Context context;
    private View rootView;

    private Button btnSend;
    private boolean isColumnSelected;

    private RadioGroup rgCongregation;
    private RadioGroup rgColumn;

    @BindView(R.id.textView_congregation)
    TextView tvCongregation;
    @BindView(R.id.textView_column)
    TextView tvColumn;

    @BindView(R.id.expandable_jemaat)
    ExpandableLayout elCongregation;
    @BindView(R.id.expandable_column)
    ExpandableLayout elColumn;

    private TextInputLayout tilDesignatedColumn;

    private ArrayList<ApplyMember.ApplyMemberRegisteredChurchHelper> registeredChurch;
    private int selectedPos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_apply_member_designated, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);
        btnSend = getActivity().findViewById(R.id.btn_fragment);

        registeredChurch = ApplyMember.REGISTERED_CHURCH_LIST;

        initExpendable();
        return rootView;
    }

    @Override
    public void onPause() {
        ViewPager2 viewPager2 = getActivity().findViewById(R.id.viewPager);
        if (btnSend.getVisibility() != View.VISIBLE) {
            btnSend.setVisibility(View.VISIBLE);
        }
        btnSend.setText("LANJUT");
        btnSend.setOnClickListener(v -> viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1));

        super.onPause();
    }

    @Override
    public void onResume() {
        setupSendButton();
        super.onResume();
    }

    private void setupSendButton() {
        btnSend.setText("KIRIM");

        btnSend.setOnClickListener(v -> {
            validateInputs();
        });

    }

    private void validateInputs() {
        //validate if radio button is checked
        if (tvCongregation.getVisibility() != View.VISIBLE) {
            Constant.displayDialog(
                    context,
                    "Perhatian!",
                    "Silahkan pilih Jemaat Tujuan dari daftar yang ada",
                    (dialog, which) -> {
                    }
            );
            return;
        }

        //validate if designated column is "valid"
        if (tilDesignatedColumn.getError() != null || tilDesignatedColumn.getEditText().getText().length() < 1) {
            Constant.displayDialog(
                    context,
                    "Perhatian!",
                    "Kolom Tujuan tidak valid. Silahkan periksa kembali Kolom Tujuan anda",
                    (dialog, which) -> {
                    }
            );
            return;
        }

        //validate if designated column is in the designated congregation
        int designatedColumn = Integer.parseInt(tilDesignatedColumn.getEditText().getText().toString());
        if (designatedColumn > registeredChurch.get(selectedPos).getCountColumn()) {
            Constant.displayDialog(
                    context,
                    "Perhatian!",
                    "Jumlah Kolom Jemaat Tujuan yang dipilih adalah "
                            + registeredChurch.get(selectedPos).getCountColumn()
                            + ". Silahkan memilih Kolom Tujuan yang tidak melebihi jumlah kolom Jemaat Tujuan",
                    (dialog, which) -> {
                    }
            );
            tilDesignatedColumn.setError("Jumlah kolom di Jemaat Tujuan anda adalah " + registeredChurch.get(selectedPos).getCountColumn());
            return;
        }
        sendDataToServer();
    }

    private void sendDataToServer() {
//        View layoutMain = findViewById(R.id.layout_main);
//        context.sendBroadcast(new Intent(ApplyMember.ACTION_SEND_DATA_TO_SERVER));

        if (btnSend.getVisibility() == View.VISIBLE) {
            btnSend.setVisibility(View.GONE);
        }

        ApplyMember.CHURCH_ID = registeredChurch.get(selectedPos).getChurchId();
        ApplyMember.COLUMN_INDEX = tilDesignatedColumn.getEditText().getText().toString();

        Call call = RetrofitClient.getInstance(null).getApiServices().setApplyMember(
                ApplyMember.USER_ID,
                ApplyMember.USER_FIRST_NAME,
                ApplyMember.USER_MIDDLE_NAME,
                ApplyMember.USER_LAST_NAME,
                ApplyMember.USER_SEX,
                ApplyMember.USER_DATE_OF_BIRTH,
                ApplyMember.CHURCH_ID,
                ApplyMember.COLUMN_INDEX,
                ApplyMember.BAPTIS,
                ApplyMember.SIDI,
                ApplyMember.NIKAH
        );
        IssueRequestHandler req = new IssueRequestHandler(rootView);
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {
            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
//                replaceWithFinishFragment();
                btnSend.setVisibility(View.GONE);
//                context.sendBroadcast(new Intent(ApplyMember.ACTION_REPLACE_WITH_FINISH));
                Constant.displayDialog(
                        context,
                        "Perhatian!",
                        "Permintaan telah terkirim dan sedang diproses. Silahkan menunggu konfirmasi dari Jemaat Tujuan anda ",
                        false,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> getActivity().finish()
                );
            }

            @Override
            public void onRetry() {
                sendDataToServer();
            }
        });
        req.enqueue(call);

        Log.e(TAG, "==============================================================");
        Log.e(TAG, "User ID: " + ApplyMember.USER_ID);
        Log.e(TAG, "first name: " + ApplyMember.USER_FIRST_NAME);
        Log.e(TAG, "middle name: " + ApplyMember.USER_MIDDLE_NAME);
        Log.e(TAG, "last name: " + ApplyMember.USER_LAST_NAME);
        Log.e(TAG, "user sex: " + ApplyMember.USER_SEX);
        Log.e(TAG, "date of birth: " + ApplyMember.USER_DATE_OF_BIRTH);
        Log.e(TAG, "church id: " + ApplyMember.CHURCH_ID);
        Log.e(TAG, "column index: " + ApplyMember.COLUMN_INDEX);
        Log.e(TAG, "baptis: " + ApplyMember.BAPTIS);
        Log.e(TAG, "sidi: " + ApplyMember.SIDI);
        Log.e(TAG, "nikah: " + ApplyMember.NIKAH);
        Log.e(TAG, "==============================================================");
    }


    private void initExpendable() {
        ((TextView) elCongregation.getParentLayout().findViewById(R.id.textView_title)).setText("Jemaat Tujuan");
        elCongregation.setOnClickListener(v -> {
            if (!elCongregation.isExpanded()) {
                elCongregation.expand();
            }
            else {
                elCongregation.collapse();
            }
        });
        rgCongregation = elCongregation.getSecondLayout().findViewById(R.id.radioGroup);
        for (int i = 0; i < registeredChurch.size(); i++) {
            RadioButton rb = new RadioButton(context);
            rb.setText(registeredChurch.get(i).getChurchName());
            rb.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/montserrat_regular.ttf"));
            rb.setId(i);

            rgCongregation.addView(rb, i);
        }

        rgCongregation.setOnCheckedChangeListener((group, checkedId) -> {
            setupColumnExpandable();
            if (elCongregation.isExpanded()) elCongregation.collapse();
            if (!elColumn.isExpanded()) elColumn.expand();
            if (tvCongregation.getVisibility() != View.VISIBLE) {
                tvCongregation.setVisibility(View.VISIBLE);
            }
            tvCongregation.setText(registeredChurch.get(checkedId).getChurchName());
            selectedPos = checkedId;
            tilDesignatedColumn.setError(null);
        });
    }

    private void setupColumnExpandable() {

        ((TextView) elColumn.getParentLayout().findViewById(R.id.textView_title)).setText("Kolom Tujuan");
        elColumn.setOnClickListener(v -> {
            if (!elColumn.isExpanded()) {
                elColumn.expand();
            }
            else {
                elColumn.collapse();
            }
        });
        if (elColumn.getVisibility() != View.VISIBLE) elColumn.setVisibility(View.VISIBLE);
        tilDesignatedColumn = elColumn.getSecondLayout().findViewById(R.id.textInputLayout_designatedColumn);
        InputFilter[] filterArr = new InputFilter[1];
        tilDesignatedColumn.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (tvColumn.getVisibility() != View.VISIBLE) tvColumn.setVisibility(View.VISIBLE);

                if (s.length() < 1) {
                    tvColumn.setVisibility(View.GONE);
                    tilDesignatedColumn.setError("Silahkan masukkan kolom yang anda tuju");
                    return;
                }
                else {
                    tilDesignatedColumn.setError(null);
                    tvColumn.setVisibility(View.VISIBLE);
                    tvColumn.setText(String.format("Kolom %s", s));

                }

                if (s.toString().startsWith("0")) {
                    tilDesignatedColumn.setError("Tidak bisa memulai dengan angka 0");
                    filterArr[0] = new InputFilter.LengthFilter(1);
                    tilDesignatedColumn.getEditText().setFilters(filterArr);
                    tvColumn.setVisibility(View.GONE);
                }
                else {
                    filterArr[0] = new InputFilter.LengthFilter(4);
                    tilDesignatedColumn.getEditText().setFilters(filterArr);
                    tvColumn.setVisibility(View.VISIBLE);
                    tilDesignatedColumn.setError(null);
                    tvColumn.setText(String.format("Kolom %s", s));
                }
            }
        });
    }


}
