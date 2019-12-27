package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;

import butterknife.ButterKnife;

public class NavFragment_Member_Setting extends Fragment implements View.OnClickListener {

    private static final String TAG = "Fragment_User_Home";

    private View rootView;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_user_setting, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        //re enable important-date button on action bar
        if (getActivity().findViewById(R.id.action_importantDates) != null) {
            getActivity().findViewById(R.id.action_importantDates).setEnabled(true);
        }

        //send broadcast to dashboard that this fragment is clicked
        rootView.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {

        context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));

    }

}
