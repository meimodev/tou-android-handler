/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Wizard.ApplyMember;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.meimodev.sitouhandler.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_MemberData extends Fragment {
    private static final String TAG = "Fragment_secondPage";
    private Context context;
    private View rootView;


    @BindView(R.id.checkbox_baptis)
    MaterialCheckBox cbBaptis;
    @BindView(R.id.checkbox_sidi)
    MaterialCheckBox cbSidi;
    @BindView(R.id.checkbox_nikah)
    MaterialCheckBox cbNikah;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_apply_member_member_data, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        cbBaptis.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ApplyMember.BAPTIS = isChecked ? "SB" : "";
        });

        cbSidi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ApplyMember.SIDI = isChecked ? "SS" : "";
        });

        cbNikah.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ApplyMember.NIKAH = isChecked ? "SN" : "";
        });

        return rootView;
    }

}
