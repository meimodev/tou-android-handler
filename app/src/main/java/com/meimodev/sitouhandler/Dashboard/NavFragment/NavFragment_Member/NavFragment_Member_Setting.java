package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.Wizard.ApplyMember.ApplyMember;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavFragment_Member_Setting extends Fragment {

    private static final String TAG = "Fragment_User_Home";

    private View rootView;
    private Context context;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_user_setting, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);



        return rootView;
    }

}
