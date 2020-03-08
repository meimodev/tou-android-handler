/**************************************************************************************************
 * Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   *
 **************************************************************************************************/

package com.meimodev.sitouhandler.Wizard.ApplyMember;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

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
                    "Perhatian",
                    "Silahkan pilih Jemaat Tujuan dari daftar yang ada",
                    true,
                    (dialog, which) -> dialog.dismiss(),
                    null
            );
            return;
        }

        //validate if designated column is "valid"
        if (tilDesignatedColumn.getError() != null) {
            Constant.displayDialog(
                    context,
                    "Perhatian",
                    "Silahkan periksa kembali Kolom Tujuan anda",
                    true,
                    (dialog, which) -> dialog.dismiss(),
                    null
            );
            return;
        }

        //validate if designated column is in the designated congregation
        int designatedColumn = Integer.parseInt(tilDesignatedColumn.getEditText().getText().toString());
        if (designatedColumn > registeredChurch.get(selectedPos).getCountColumn()) {
            Constant.displayDialog(
                    context,
                    "Perhatian",
                    "Jumlah Kolom Jemaat Tujuan yang dipilih adalah "
                            + registeredChurch.get(selectedPos).getCountColumn()
                            + ". Silahkan memilih Kolom Tujuan yang tidak melebihi jumlah kolom Jemaat Tujuan",
                    true,
                    (dialog, which) -> dialog.dismiss(),
                    null
            );
            tilDesignatedColumn.setError("Jumlah kolom di Jemaat Tujuan anda adalah " + registeredChurch.get(selectedPos).getCountColumn());
            return;
        }
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
