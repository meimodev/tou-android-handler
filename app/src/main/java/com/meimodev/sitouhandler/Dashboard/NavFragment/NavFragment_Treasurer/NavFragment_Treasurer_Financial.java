package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Treasurer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavFragment_Treasurer_Financial extends Fragment implements View.OnClickListener {

    private View rootView;
    private Context context;

    @BindView(R.id.layout_main)
    RecyclerView rvMain;
    private ArrayList<NavFragment_Treasurer_Financial_RecyclerModel> items = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_activity_treasurer_financial, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

//        Fetch this Data
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "Januari", "2018"
        ));
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "Februari", "2018"
        ));
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "Maret", "2018"
        ));
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "April", "2018"
        ));
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "Mei", "2018"
        ));
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "Juni", "2018"
        ));
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "Juli", "2018"
        ));
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "Januari-Juli", "2018", "I"
        ));
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "Agustus-Desember", "2018", "II"
        ));
        items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                0, "Agustus-Desember", "2018", "II"
        ));


        setupRecyclerView();

        rootView.setOnClickListener(this);
        return rootView;
    }

    private void setupRecyclerView() {
        rvMain.setHasFixedSize(false);
        rvMain.setLayoutManager(new LinearLayoutManager(context));
        rvMain.setItemAnimator(new DefaultItemAnimator());
        rvMain.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));


        NavFragment_Treasurer_Financial_RecyclerAdapter adapter = new NavFragment_Treasurer_Financial_RecyclerAdapter(items, context);
        rvMain.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        context.sendBroadcast(new Intent(Constant.ACTION_CONTENT_IN_FRAGMENT_IS_CLICKED));

    }
}
