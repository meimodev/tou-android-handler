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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.material.textfield.TextInputLayout;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.Dashboard.Services.Cookies.ActivityServiceCookies;
import com.meimodev.sitouhandler.Dashboard.Services.Electronics.ActivityServiceElectronics;
import com.meimodev.sitouhandler.Dashboard.Services.Gas.ActivityServiceGas;
import com.meimodev.sitouhandler.Dashboard.Services.Groceries.ActivityServiceGroceries;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_User_Home_News extends Fragment {

    private static final String TAG = "User_Home_News";
    private View rootView;
    private Context context;

    @BindView(R.id.recyclerView_news)
    RecyclerView rvNews;

    @BindView(R.id.layout_click_sss)
    RelativeLayout layoutSSS;

//    @BindView(R.id.textInputLayout_search)
//    TextInputLayout tilSearch;

    @BindView(R.id.recyclerView_vendors)
    RecyclerView rvVendors;

    private Fragment_User_Home_Vendors_RecyclerAdapter adapterVendors;
    private ArrayList<Fragment_User_Home_Vendors_RecyclerModel> modelVendors;

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

        setupServicesButton();
        setupVendorRecyclerView();
        fetchVendorRecyclerView();

        return rootView;
    }


    private void fetchVendorRecyclerView() {

        IssueRequestHandler req = new IssueRequestHandler(rvVendors);
        req.setIntention(new Throwable());
        req.setContext(context);
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                JSONArray arr = res.getDataArray();

                if (modelVendors.size() != 0) {
                    modelVendors.clear();
                }


                for (int i = 0; i < arr.length(); i++) {
                    JSONObject model = arr.getJSONObject(i);
                    modelVendors.add(new Fragment_User_Home_Vendors_RecyclerModel(
                            model.getInt("id"),
                            model.getString("image"),
                            model.getString("name"),
                            model.getString("open"),
                            model.getString("close"),
                            model.getBoolean("is_open")
                    ));
                }
                adapterVendors.notifyDataSetChanged();


            }

            @Override
            public void onRetry() {
                fetchVendorRecyclerView();
            }
        });

        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getVendors(true));

    }

    private void setupServicesButton() {
        View.OnClickListener onClickListener = v -> {
            Intent intent;
//            if (v == layoutGroceries) {
//                intent = new Intent(context, ActivityServiceGroceries.class);
//                intent.putExtra(ActivityServiceGroceries.KEY_SEARCH_PRODUCT_TYPE, "Groceries");
//                intent.putExtra(ActivityServiceGroceries.KEY_START_TEASER, true);
//            }
//            else if (v == layoutGas) {
//                intent = new Intent(context, ActivityServiceGas.class);
//            }
//            else if (v == layoutCookies) {
//                intent = new Intent(context, ActivityServiceCookies.class);
//            }
            if (v == layoutSSS) {
                intent = new Intent(context, ActivityServiceGroceries.class);
                intent.putExtra(ActivityServiceGroceries.KEY_SEARCH_PRODUCT_TYPE, "SSS");
                context.startActivity(intent);
            }
//            else {
//                intent = new Intent(context, ActivityServiceElectronics.class);
//            }
        };
//        layoutGroceries.setOnClickListener(onClickListener);
//        layoutGas.setOnClickListener(onClickListener);
//        layoutCookies.setOnClickListener(onClickListener);
//        layoutElectronics.setOnClickListener(onClickListener);
        layoutSSS.setOnClickListener(onClickListener);

//        EditText et = tilSearch.getEditText();
//        et.setOnClickListener(v -> {
//            Intent intent = new Intent(context, ActivityServiceGroceries.class);
//            intent.putExtra(ActivityServiceGroceries.KEY_SEARCH_PRODUCT_TYPE, "SSS");
//            context.startActivity(intent);
//        });
//        et.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                Intent intent = new Intent(context, ActivityServiceGroceries.class);
//                intent.putExtra(ActivityServiceGroceries.KEY_SEARCH_PRODUCT_TYPE, "SSS");
//                context.startActivity(intent);
//            }
//        });

    }

    private void setupVendorRecyclerView() {
        rvVendors.setHasFixedSize(true);
        rvVendors.setLayoutManager(new LinearLayoutManager(context));
        rvVendors.setItemAnimator(new DefaultItemAnimator());

        modelVendors = new ArrayList<>();
        adapterVendors = new Fragment_User_Home_Vendors_RecyclerAdapter(modelVendors, context);
        rvVendors.setAdapter(adapterVendors);

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchVendorRecyclerView();
    }
}
