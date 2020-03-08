/**************************************************************************************************
 * Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   *
 **************************************************************************************************/

package com.meimodev.sitouhandler.Wizard.ApplyMember;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.meimodev.sitouhandler.R;

import butterknife.ButterKnife;

public class Fragment_Finish extends Fragment{
    private static final String TAG = "Fragment_firstPage";
    private Context context;
    private View rootView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_apply_member_finish, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        return rootView;
    }




}
