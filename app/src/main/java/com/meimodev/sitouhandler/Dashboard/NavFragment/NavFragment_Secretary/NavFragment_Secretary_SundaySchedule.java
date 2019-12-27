package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Secretary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;

import butterknife.ButterKnife;

public class NavFragment_Secretary_SundaySchedule extends Fragment implements View.OnClickListener {

    private View rootView;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_fragment_secretary_sunday_schedule, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        Toast.makeText(context, "Toastingggggggg", Toast.LENGTH_SHORT).show();
        rootView.setOnClickListener(this);
        return rootView;

    }


    @Override
    public void onClick(View view) {
        context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));


    }
}
