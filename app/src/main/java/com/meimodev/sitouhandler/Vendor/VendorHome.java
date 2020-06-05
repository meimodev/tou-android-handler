/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Vendor;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.databinding.ActivityVendorHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VendorHome extends AppCompatActivity {

    private static final String TAG = "VendorHome";

    private ActivityVendorHomeBinding b;

    private ArrayList<HelperModel> orders;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityVendorHomeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Constant.changeStatusColor(this, R.color.colorPrimary);
        b.buttonManage.setOnClickListener(v -> startActivity(new Intent(VendorHome.this, VendorProductDetail.class)));

        fetchData();

    }

    private void fetchData() {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setIntention(new Throwable());
        req.setContext(this);
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                orders = new ArrayList<>();

                JSONArray data = res.getDataArray();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    orders.add(new HelperModel(
                            obj.getInt("id"),
                            obj.getString("type"),
                            obj.getString("status"),
                            obj.getString("delivery_time"),
                            obj.getString("order_date"),
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
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getOrders(0));
    }

    public void initRecyclerView(){
        b.recyclerViewOrders.setHasFixedSize(true);
        b.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        b.recyclerViewOrders.setItemAnimator(new DefaultItemAnimator());
        VendorHome_RecyclerAdapter adapter = new VendorHome_RecyclerAdapter(this, orders);
        b.recyclerViewOrders.setAdapter(adapter);
    }

    public static class HelperModel {
        private int id;
        private String type, status, deliveryTime, orderTime, finishDate;

        public HelperModel(int id, String type, String status, String deliveryTime, String orderTime, String finishDate) {
            this.id = id;
            this.type = type;
            this.status = status;
            this.deliveryTime = deliveryTime;
            this.orderTime = orderTime;
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

        public String getDeliveryTime() {
            return deliveryTime;
        }

        public String getOrderTime() {
            return orderTime;
        }

        public String getFinishDate() {
            return finishDate;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }
}
