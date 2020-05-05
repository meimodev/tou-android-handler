/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Warta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;

import com.github.clans.fab.FloatingActionMenu;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_User_Home_Warta extends Fragment {

    private static final String TAG = "User_Home_Warta";
    private View rootView;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_user_home_warta, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        if (getActivity().findViewById(R.id.layout_guide).getVisibility() == View.VISIBLE)
            context.sendBroadcast(new Intent(Dashboard.ACTION_HEADER_COLLAPSE));

        return rootView;
    }


}
