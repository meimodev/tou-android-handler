/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Wizard.ApplyMember;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Fragment_Intro extends Fragment{
    private static final String TAG = "Fragment_firstPage";
    private Context context;
    private View rootView;

    @BindView(R.id.card_link)
    CardView tvLink;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_apply_member_intro, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        tvLink.setOnClickListener(v -> Constant.WhatsAppSendMessage(
                context,
                "+6285756681077",
                "Pengajuan untuk Mitra TOU #TY35B12XG"
        ));

        return rootView;
    }


}
