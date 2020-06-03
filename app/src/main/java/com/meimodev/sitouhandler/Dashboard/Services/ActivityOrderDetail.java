/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

    private LatLng target;


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

        MySupportMapFragment mapFragment = ((MySupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        mapFragment.setListener(() -> b.getRoot().requestDisallowInterceptTouchEvent(true));
        mapFragment.getMapAsync(googleMap -> {
            gMap = googleMap;
            fetchData();
        });
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

        String type, fetchType = data.getString("type");
        if (fetchType.contentEquals(Constant.PRODUCT_TYPE_GROCERIES)) {
            type = "Bahan Makanan";
        }
        else if (fetchType.contentEquals(Constant.PRODUCT_TYPE_ELECTRONICS)) {
            type = "Servis Elektronik";
        }
        else {
            type = "Kukis";
        }
        b.textViewType.setText(type);

        b.textViewOrderDate.setText(data.getString("order_date"));

        //status
        initStatus(data);

        boolean isVendor = Guru.getInt(Constant.KEY_VENDOR_ID, -99) > 0;
        //rating
        String finishDate = data.getString("finish_date");
        if (!StringUtils.isEmpty(finishDate)
                || isVendor
                || !data.getString("status").contentEquals(Constant.AUTHORIZATION_STATUS_ACCEPTED)) {
            b.ratingBar.setRating(data.getInt("rating"));
            b.ratingBar.setIsIndicator(true);
        }

        //button finish
        Log.e(TAG, "initViews: is Vendor = " + isVendor + " id = " + Guru.getInt(Constant.KEY_VENDOR_ID, -99));
        if (isVendor) {
            b.buttonFinish.setVisibility(View.GONE);
            if (StringUtils.isEmpty(finishDate) && data.getString("status").contentEquals(Constant.AUTHORIZATION_STATUS_UNCONFIRMED)) {
                b.layoutButtonsVendor.setVisibility(View.VISIBLE);
                b.buttonAccept.setOnClickListener(this);
                b.buttonReject.setOnClickListener(this);
            }
        }
        else {
            if (StringUtils.isEmpty(finishDate) &&
                    data.getString("status")
                            .contentEquals(Constant.AUTHORIZATION_STATUS_ACCEPTED)) {
                b.buttonFinish.setVisibility(View.VISIBLE);
                b.buttonFinish.setOnClickListener(this);
            }
        }

        //set map
        LatLng target = new LatLng(
                Double.parseDouble(data.getString("coordinate_lat")),
                Double.parseDouble(data.getString("coordinate_lng"))
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



    }

    private void initStatus(JSONObject data) throws JSONException {
        String fetchStatus = data.getString("status");
        boolean isFinish = !StringUtils.isEmpty(data.getString("finish_date"));

        if (isFinish) {
            setStatusEnable(b.textViewStatusUnconfirm, true);
            setStatusEnable(b.textViewStatusConfirm, true);
            setStatusEnable(b.textViewStatusDone, true);
            return;
        }

        if (fetchStatus.contentEquals(Constant.AUTHORIZATION_STATUS_UNCONFIRMED)) {
            setStatusEnable(b.textViewStatusUnconfirm, true);
            setStatusEnable(b.textViewStatusConfirm, false);
            setStatusEnable(b.textViewStatusDone, false);

        }
        else if (fetchStatus.contentEquals(Constant.AUTHORIZATION_STATUS_ACCEPTED)) {
            setStatusEnable(b.textViewStatusUnconfirm, true);
            setStatusEnable(b.textViewStatusConfirm, true);
            setStatusEnable(b.textViewStatusDone, false);
            b.textViewStatusConfirm.setText(String.format("Diantarkan pada %s", data.getString("delivery_time")));

        }
        else if (fetchStatus.contentEquals(Constant.AUTHORIZATION_STATUS_REJECTED)) {
            setStatusEnable(b.textViewStatusUnconfirm, true);
            setStatusEnable(b.textViewStatusConfirm, false);
            setStatusEnable(b.textViewStatusDone, false);
            b.textViewStatusUnconfirm.setText("Pesanan ditolak penyedia");
            b.textViewStatusUnconfirm.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    getResources().getDrawable(R.drawable.ic_close_24px),
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
        for (int i = 0; i < products.length(); i++) {
            JSONObject obj = products.getJSONObject(i);
            View v = getLayoutInflater().inflate(R.layout.resource_list_item_order_detail_product, b.layoutProducts, false);
            TextView tvQuantity = v.findViewById(R.id.textView_quantity);
            TextView tvName = v.findViewById(R.id.textView_name);
            TextView tvPrice = v.findViewById(R.id.textView_price);
            String quantity = obj.getInt("quantity") + " " + obj.getString("unit");
            tvQuantity.setText(quantity);
            tvName.setText(obj.getString("name"));
            tvPrice.setText(Constant.convertNumberToCurrency(obj.getInt("total_price")));
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

}
