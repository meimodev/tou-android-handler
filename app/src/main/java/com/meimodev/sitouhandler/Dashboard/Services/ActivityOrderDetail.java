/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.room.util.StringUtil;

import com.github.squti.guru.Guru;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.FCM_Service;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.databinding.ActivityOrderDetailBinding;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static com.meimodev.sitouhandler.Constant.changeStatusColor;
import static com.meimodev.sitouhandler.Constant.getBitmapFromVectorDrawable;

public class ActivityOrderDetail extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ActivityOrderDetail";
    private ActivityOrderDetailBinding b;

    private int orderId = -99;
    private GoogleMap gMap;

    private BroadcastReceiver brOrderUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().contentEquals(FCM_Service.NOTIFICATION_ORDER)){
                fetchData();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityOrderDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        changeStatusColor(this, R.color.colorPrimary);

        orderId = getIntent().getIntExtra("ID", -99);
        if (orderId < 0) {
            Constant.displayDialog(this, "INVALID ORDER ID!", "");
            finish();
        }
        b.layoutButtonsVendor.setVisibility(View.GONE);
        b.buttonFinish.setVisibility(View.GONE);

        fetchData();

        registerReceiver(brOrderUpdate, new IntentFilter(FCM_Service.NOTIFICATION_ORDER));
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
                initViews(res.getData());
            }

            @Override
            public void onRetry() {
                fetchData();
            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getOrder(orderId));
    }

    private void initViews(JSONObject data) throws JSONException {
        initProducts(data);
        b.textViewTransportFee.setText(
                Constant.convertNumberToCurrency(data.getString("transport_fee"))
        );
        b.textViewTotal.setText(
                Constant.convertNumberToCurrency(data.getString("total_bill"))
        );

        int transportFee = Integer.parseInt(data.getString("transport_fee"));
        b.textViewType.setText(transportFee > 0 ?
                "Pesan Antar" : "Pesan DiTempat");

        b.textViewOrderDate.setText(data.getString("order_date"));

        //status
        initStatus(data);

        //rating
        initRating(data);

        //finishButton
        initFinishButton(data);

        //set map
        initMap(data);

    }

    private void initMap(JSONObject data) throws JSONException {
        int transportFee = Integer.parseInt(data.getString("transport_fee"));
        String deliveryLocation = data.getString("delivery_location");

        String vLat = data.getString("vendor_lat");
        String vLng = data.getString("vendor_lng");

        String lat;
        String lng;

        String defaultDeliveryText =b.textViewDeliveryLocation.getText().toString();
        if (transportFee <= 0) {

            lat = vLat;
            lng = vLng;
            b.textViewDeliveryLocation.setText(deliveryLocation);
            b.textViewDeliveryLocationText.setText("CATATAN TAMBAHAN");

        }
        else {
            lat = data.getString("coordinate_lat");
            lng = data.getString("coordinate_lng");
            b.textViewDeliveryLocation.setText(deliveryLocation);
            b.textViewDeliveryLocationText.setText(defaultDeliveryText);

        }

        MySupportMapFragment mapFragment =
                ((MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.setListener(() -> b.getRoot().requestDisallowInterceptTouchEvent(true));
        mapFragment.getMapAsync(googleMap -> {
            gMap = googleMap;

            LatLng target = new LatLng(
                    Double.parseDouble(lat),
                    Double.parseDouble(lng)
            );
            gMap.addMarker(
                    new MarkerOptions()
                            .position(target)
                            .draggable(false)
                            .title("Lokasi Pengantaran")
                            .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(this, R.drawable.ic_map_marker_down_24px)))
            );
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15f));
            gMap.setMinZoomPreference(11.25f);
            gMap.getUiSettings().setCompassEnabled(false);
            gMap.getUiSettings().setRotateGesturesEnabled(false);
            gMap.getUiSettings().setTiltGesturesEnabled(false);
        });

    }

    private void initRating(JSONObject data) throws JSONException {

        b.ratingBar.setVisibility(View.GONE);

        if (data.getString("status").contentEquals("PAID")) {
            b.ratingBar.setVisibility(View.VISIBLE);
        }

        String finishDate = data.getString("finish_date");

        if (data.getString("status").contentEquals("PAID")
                && StringUtils.isNotEmpty(finishDate)) {
            b.ratingBar.setRating(data.getInt("rating"));
            b.ratingBar.setIsIndicator(true);
        }


    }

    private void initFinishButton(JSONObject data) throws JSONException {

        b.buttonFinish.setVisibility(View.GONE);

        if (data.getString("status").contentEquals("PAID")) {

            b.buttonFinish.setVisibility(View.VISIBLE);
            b.buttonFinish.setOnClickListener(this);
            boolean isFinish = StringUtils.isNotEmpty(data.getString("finish_date"));
            if (isFinish) {
                b.buttonFinish.setVisibility(View.GONE);
            }
        }

    }

    private void initStatus(JSONObject data) throws JSONException {
        String status = data.getString("status");
        boolean isFinish = StringUtils.isNotEmpty(data.getString("finish_date"));
        boolean isStatusIsPaid = status.contentEquals("PAID");

        if (isFinish || isStatusIsPaid) {
            setStatusEnable(b.textViewStatusUnconfirm, true);
            setStatusEnable(b.textViewStatusConfirm, true);
            setStatusEnable(b.textViewStatusDone, true);
            return;
        }

        if (status.contentEquals(Constant.AUTHORIZATION_STATUS_UNCONFIRMED)) {
            setStatusEnable(b.textViewStatusUnconfirm, true);
            setStatusEnable(b.textViewStatusConfirm, false);
            setStatusEnable(b.textViewStatusDone, false);

        }
        else if (status.contentEquals(Constant.AUTHORIZATION_STATUS_ACCEPTED)) {
            setStatusEnable(b.textViewStatusUnconfirm, true);
            setStatusEnable(b.textViewStatusConfirm, true);
            setStatusEnable(b.textViewStatusDone, false);

            boolean isDelivery = Integer.parseInt(data.getString("transport_fee")) != 0;
            String s = isDelivery ? String.format("Diantarkan: %s", data.getString("delivery_time"))
                    : "Ambil pesanan di " + data.getString("vendor_name");
            b.textViewStatusConfirm.setText(s);

        }
        else if (status.contentEquals(Constant.AUTHORIZATION_STATUS_REJECTED)) {
            setStatusEnable(b.textViewStatusUnconfirm, true);
            setStatusEnable(b.textViewStatusConfirm, false);
            setStatusEnable(b.textViewStatusDone, false);
            b.textViewStatusUnconfirm.setText("Pesanan dibatalkan penyedia");
            b.textViewStatusUnconfirm.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    ResourcesCompat.getDrawable(getResources(), R.drawable.ic_close_24px, null),
                    null, null, null
            );

        }

    }

    private void setStatusEnable(TextView tv, boolean isEnable) {
        Drawable drawable;
        drawable = getResources().getDrawable(R.drawable.ic_check_24px);
        tv.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);

        if (isEnable) {
            tv.setVisibility(View.VISIBLE);
        }
        else {
            tv.setVisibility(View.GONE);
        }

    }

    public void initProducts(JSONObject data) throws JSONException {

        JSONArray products = data.getJSONArray("products");

        b.layoutProducts.removeAllViews();
        for (int i = 0; i < products.length(); i++) {

            JSONObject obj = products.getJSONObject(i);

            View v = getLayoutInflater().inflate(R.layout.resource_list_item_order_detail_product, b.layoutProducts, false);
            TextView tvQuantity = v.findViewById(R.id.textView_quantity);
            TextView tvName = v.findViewById(R.id.textView_name);
            TextView tvPrice = v.findViewById(R.id.textView_price);
            ImageView imageView = v.findViewById(R.id.imageView);
            imageView.setVisibility(View.GONE);

            String quantity = obj.getInt("quantity") + " " + obj.getString("unit");
            tvQuantity.setText(quantity);
            tvName.setText(obj.getString("name"));
            tvPrice.setText(Constant.convertNumberToCurrency(obj.getInt("total_price")));

            String status = obj.getString("status");
            if (status.contentEquals("DONE")) {
                imageView.setVisibility(View.VISIBLE);
            }

            b.layoutProducts.addView(v);
        }
    }

    @Override
    public void onClick(View v) {
        Call<ResponseBody> call = null;
        if (v == b.buttonFinish) {
            call = RetrofitClient.getInstance(null).getApiServices().finishOrder(
                    orderId, ((int) b.ratingBar.getRating())
            );
            if (b.ratingBar.getRating() == 0) {
                Constant.displayDialog(
                        this,
                        "Perhatian !",
                        "Silahkan memberikan rating, dengan cara menyentuh jumlah bintang yang ingin diberikan berdasarkan pengalaman anda menggunakan jasa kami",
                        (dialog, which) -> {
                        }
                );
                return;
            }
        }
        else if (v == b.buttonAccept) {
            call = RetrofitClient.getInstance(null).getApiServices().authorizeOrder(
                    orderId,
                    Constant.AUTHORIZATION_STATUS_ACCEPTED
            );
        }
        else if (v == b.buttonReject) {
            call = RetrofitClient.getInstance(null).getApiServices().authorizeOrder(
                    orderId,
                    Constant.AUTHORIZATION_STATUS_REJECTED
            );
        }

        if (call == null) return;

        sendDataToServer(call);

    }

    private void sendDataToServer(Call<ResponseBody> call) {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setContext(this);
        req.setIntention(new Throwable());
        Call<ResponseBody> finalCall = call;
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                Constant.displayDialog(
                        ActivityOrderDetail.this,
                        "Perhatian !",
                        message,
                        true,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> finish()
                );
            }

            @Override
            public void onRetry() {
                req.enqueue(finalCall);
            }
        });
        req.enqueue(call);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(brOrderUpdate);
        super.onStop();
    }
}
