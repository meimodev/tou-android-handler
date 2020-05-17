/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionMenu;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.Dashboard.Services.Cookies.ActivityServiceCookies;
import com.meimodev.sitouhandler.Dashboard.Services.Electronics.ActivityServiceElectronics;
import com.meimodev.sitouhandler.Dashboard.Services.Gas.ActivityServiceGas;
import com.meimodev.sitouhandler.Dashboard.Services.Groceries.ActivityServiceGroceries;
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
    @BindView(R.id.layout_services)
    CardView layoutServices;

    @BindView(R.id.layout_groceries)
    LinearLayout layoutGroceries;
    @BindView(R.id.layout_gas)
    LinearLayout layoutGas;
    @BindView(R.id.layout_cookies)
    LinearLayout layoutCookies;
    @BindView(R.id.layout_electronics)
    LinearLayout layoutElectronics;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_user_home_news, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        if (Dashboard.IS_HEADER_COLLAPSE) {
            context.sendBroadcast(new Intent(Dashboard.ACTION_HEADER_EXPAND));
        }


//        Fetch Data From Server
        fetchData();

        setupServicesButton();

        return rootView;
    }

    private void fetchData() {
        // fetch data if succeed setup recyclerView
        rvNews.setHasFixedSize(true);
        rvNews.setItemAnimator(new DefaultItemAnimator());
        rvNews.setLayoutManager(new LinearLayoutManager(context));
        ArrayList<Fragment_User_Home_News_RecyclerModel> models = new ArrayList<>();

        models.add(new Fragment_User_Home_News_RecyclerModel(
                1,
                "",
                null,
                "Selamat datang di TOU-SYSTEM",
                "<strong>TOU-System</strong> merupakan sebuah kombinasi integrasi dari aplikasi " +
                        "handphone & beberapa aplikasi <i>web</i> yang disatukan dalam <i>cloud base environment</i>"+
                " masuk dalam kategori SaaS (<i>Software as a Service</i>)." +
                        "<br/><br/>" +
                        "Didesain spesifik untuk membantu meningkatkan kualitas 'kehidupan' kita, dengan cara memangkas hal-hal yang tidak efektif dalam layanan-layanan yang sering kita gunakan sehari-hari." +
//                        "<br/><br/>"+
//                        "<a href='"+Constant.ROOT_PROTOCOL_IP_PORT+"'>www.tousystem.com<a/>"+
                        "<br/><br/><br/><br/>"+
                        "<i>'SiTou Timou Tumou Tou'</i>  <strong>- G.S.S.J.Ratulangi -<strong/>" ,
                ""
        ));

        rvNews.setAdapter(new Fragment_User_Home_News_RecyclerAdapter(models, context));
    }

    private void setupServicesButton() {
        View.OnClickListener onClickListener = v -> {
            Intent intent;
            if (v == layoutGroceries) {
                intent = new Intent(context, ActivityServiceGroceries.class);
            }
            else if (v == layoutGas) {
                intent = new Intent(context, ActivityServiceGas.class);
            }
            else if (v == layoutCookies) {
                intent = new Intent(context, ActivityServiceCookies.class);
            }
            else {
                intent = new Intent(context, ActivityServiceElectronics.class);
            }
            context.startActivity(intent);
        };
        layoutGroceries.setOnClickListener(onClickListener);
        layoutGas.setOnClickListener(onClickListener);
        layoutCookies.setOnClickListener(onClickListener);
        layoutElectronics.setOnClickListener(onClickListener);
    }

}
