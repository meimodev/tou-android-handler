/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.squti.guru.Guru;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
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

public class Fragment_User_Home_Order extends Fragment {

    private static final String TAG = "User_Home_Kidung";
    private View rootView;
    private Context context;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_user_home_orders, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        if (!Dashboard.IS_HEADER_COLLAPSE) {
            context.sendBroadcast(new Intent(Dashboard.ACTION_HEADER_COLLAPSE));
        }

        Constant.handleOnBackPressedFragment(requireActivity(), this);

//        fetchData();


        return rootView;
    }

    private Fragment_User_Home_Order_RecyclerAdapter recyclerAdapter;
    private ArrayList<RecyclerViewHelperModel> items;

    private void initRecyclerView() {
        recyclerAdapter = new Fragment_User_Home_Order_RecyclerAdapter(requireContext(), items);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void fetchData() {
        IssueRequestHandler req = new IssueRequestHandler(rootView);
        req.setIntention(new Throwable());
        req.setContext(context);
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                items = new ArrayList<>();
                JSONArray data = res.getDataArray();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    items.add(new RecyclerViewHelperModel(
                            obj.getInt("id"),
                            obj.getString("type"),
                            obj.getString("status"),
                            obj.getString("order_date"),
                            obj.getString("delivery_time"),
                            obj.getString("finish_date")
                    ));
                }
                initRecyclerView();
            }

            @Override
            public void onRetry() {
                fetchData();
            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getOrders(
                Guru.getInt(Constant.KEY_USER_ID, -9)
        ));
    }

    public static class RecyclerViewHelperModel {
        private int id;
        private String type, status, orderDate, deliveryTime, finishDate;

        public RecyclerViewHelperModel(int id, String type,
                                       String status, String orderDate,
                                       String deliveryTime, String finishDate) {
            this.id = id;
            this.type = type;
            this.status = status;
            this.orderDate = orderDate;
            this.deliveryTime = deliveryTime;
            this.finishDate = finishDate;
        }

        public int getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getStatus() {
            return status;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public String getDeliveryTime() {
            return deliveryTime;
        }

        public String getFinishDate() {
            return finishDate;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();

    }
}
