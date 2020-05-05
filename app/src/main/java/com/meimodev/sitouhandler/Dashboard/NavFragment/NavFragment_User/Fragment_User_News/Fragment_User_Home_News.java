/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionMenu;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_User_Home_News extends Fragment {

    private static final String TAG = "User_Home_News";
    private View rootView;
    private Context context;

    @BindView(R.id.recyclerView_news)
    RecyclerView rvNews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_user_home_news, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        if (getActivity().findViewById(R.id.layout_guide).getVisibility() != View.VISIBLE)
            context.sendBroadcast(new Intent(Dashboard.ACTION_HEADER_EXPAND));


//        Fetch Data From Server
        fetchData();

        return rootView;
    }


    private void fetchData() {
        // fetch data if succeed setup recyclerView
        rvNews.setHasFixedSize(true);
        rvNews.setItemAnimator(new DefaultItemAnimator());
        rvNews.setLayoutManager(new LinearLayoutManager(context));
        ArrayList<Fragment_User_Home_News_RecyclerModel> models = new ArrayList<>();

        models.add(new Fragment_User_Home_News_RecyclerModel(
                0,
                "",
                null,
                "little description about this news",
                "tittle 1 news",
                "6 hari yang lalu"
        ));
        models.add(new Fragment_User_Home_News_RecyclerModel(
                0,
                "",
                null,
                "little description about this news",
                "tittle 1 news",
                "6 hari yang lalu"
        ));
        models.add(new Fragment_User_Home_News_RecyclerModel(
                0,
                "",
                null,
                "little description about this news",
                "tittle 1 news",
                "6 hari yang lalu"
        ));
        models.add(new Fragment_User_Home_News_RecyclerModel(
                0,
                "",
                null,
                "little description about this news",
                "tittle 1 news",
                "6 hari yang lalu"
        ));

        rvNews.setAdapter(new Fragment_User_Home_News_RecyclerAdapter(models, context));


    }

}
