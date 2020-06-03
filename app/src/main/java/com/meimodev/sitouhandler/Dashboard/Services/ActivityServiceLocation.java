/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.squti.guru.Guru;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.Dashboard.Services.Groceries.ActivityServiceGroceries;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.databinding.ActivityServiceLocationBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import static com.meimodev.sitouhandler.Constant.changeStatusColor;

public class ActivityServiceLocation extends AppCompatActivity {
    private static final String TAG = "ActivityServiceLocation";
    private ActivityServiceLocationBinding b;

    private ArrayList<ActivityServiceGroceries.HelperModelCart> productsInCart;
    private int productsPrice = 0;
    private String serviceType;

    private int transportFee = 0;
    private String deliveryTime;

    private GoogleMap map;
    private LatLng tondanoHQ = new LatLng(1.296218, 124.901507);
    private LatLng target;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityServiceLocationBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        changeStatusColor(this, R.color.colorPrimary);

        req = new IssueRequestHandler(b.getRoot());
        b.layoutMain.setVisibility(View.INVISIBLE);
        b.card.setVisibility(View.GONE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleMap -> {
            b.layoutMain.setVisibility(View.VISIBLE);
            map = googleMap;

            LatLng northSulawesi = new LatLng(1.2508572, 124.8396606);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(northSulawesi));
            googleMap.setMinZoomPreference(11.25f);
            initLocationViews();
            initTimeCards();
            b.buttonOrder.setOnClickListener(btnOrderOnClickListener);

            Bundle bundle = getIntent().getBundleExtra("BUNDLE");
            Serializable serializable = Objects.requireNonNull(bundle).getSerializable("PRODUCTS");
            productsInCart = (ArrayList<ActivityServiceGroceries.HelperModelCart>) serializable;
            productsPrice = bundle.getInt("PRICE", 0);
            serviceType = bundle.getString("SERVICE_TYPE");

            initPayment();
            b.buttonLocation.setOnClickListener(btnLocationOnClickListener);

            googleMap.setOnCameraMoveStartedListener(i -> {
                b.buttonLocation.setVisibility(View.INVISIBLE);
                closeCard();
            });
            googleMap.setOnMapClickListener(latLng -> {
                b.buttonLocation.setVisibility(View.INVISIBLE);
                closeCard();
            });
            googleMap.setOnCameraIdleListener(() -> {
                b.buttonLocation.setVisibility(View.VISIBLE);
                target = new LatLng(googleMap.getCameraPosition().target.latitude, googleMap.getCameraPosition().target.longitude);
            });

//            googleMap.addCircle(new CircleOptions().center(tondanoHQ).radius(3000).strokeColor(Color.RED));
//            googleMap.addCircle(new CircleOptions().center(tondanoHQ).radius(8000).strokeColor(Color.RED));

        });
    }

    private void initLocationViews() {

        b.textInputLayoutLandmark.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                b.textInputLayoutLandmark.setError(null);
            }
        });

    }

    private int selectedDeliveryTimeCardIndex = 0;

    private void initTimeCards() {

        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
            if (isChecked) {
                ((CardView) buttonView.getParent().getParent()).setCardBackgroundColor(getResources().getColor(R.color.ButtonText_LightGrey));
            }
            else {
                ((CardView) buttonView.getParent().getParent()).setCardBackgroundColor(getResources().getColor(android.R.color.white));
            }
        };

        b.radioMorning.setOnCheckedChangeListener(onCheckedChangeListener);
        b.radioMorning.setClickable(false);

        b.radioNoon.setOnCheckedChangeListener(onCheckedChangeListener);
        b.radioNoon.setClickable(false);

        b.radioEvening.setOnCheckedChangeListener(onCheckedChangeListener);
        b.radioEvening.setClickable(false);

        View.OnClickListener onClickListener = v -> {
            if (v == b.layoutMorning) {
                selectedDeliveryTimeCardIndex = 1;
                b.radioMorning.setChecked(true);
                b.radioNoon.setChecked(false);
                b.radioEvening.setChecked(false);
                transportFee = Constant.convertCurrencyToNumber(b.textViewMorningFee.getText().toString());
                deliveryTime = b.textViewMorningTime.getText().toString();
            }
            else if (v == b.layoutNoon) {
                selectedDeliveryTimeCardIndex = 2;
                b.radioMorning.setChecked(false);
                b.radioNoon.setChecked(true);
                b.radioEvening.setChecked(false);
                transportFee = Constant.convertCurrencyToNumber(b.textViewNoonFee.getText().toString());
                deliveryTime = b.textViewNoonTime.getText().toString();
            }
            else if (v == b.layoutEvening) {
                selectedDeliveryTimeCardIndex = 3;
                b.radioMorning.setChecked(false);
                b.radioNoon.setChecked(false);
                b.radioEvening.setChecked(true);
                transportFee = Constant.convertCurrencyToNumber(b.textViewEveningFee.getText().toString());
                deliveryTime = b.textViewEveningTime.getText().toString();
            }
            initPayment();
        };

        b.layoutMorning.setOnClickListener(onClickListener);
        b.layoutNoon.setOnClickListener(onClickListener);
        b.layoutEvening.setOnClickListener(onClickListener);
    }

    private View.OnClickListener btnLocationOnClickListener = v -> {
        Log.e(TAG, " hq = " + tondanoHQ.latitude + "," + tondanoHQ.longitude);
        int distance = (int) SphericalUtil.computeDistanceBetween(tondanoHQ, target);
        Log.e(TAG, "target = " + target.latitude + "," + target.longitude);
        Log.e(TAG, "DISTANCE = from HQ to Target = " + distance);

        openCard(distance);
    };
    private View.OnClickListener btnOrderOnClickListener = v -> {
//
//        if (b.textInputLayoutKelurahan.getEditText().getText().length() < 3 || selectedKelurahanPosition < 0) {
//            b.textInputLayoutKelurahan.setError("Silahkan pilih dari daftar yang muncul saat mengetik kelurahan");
//            return;
//        }

        if (b.textInputLayoutLandmark.getEditText().getText().length() < 3) {
            b.textInputLayoutLandmark.setError("Masukkan petunjuk lokasi pengantaran");
            return;
        }

        if (selectedDeliveryTimeCardIndex == 0) {
            Constant.displayDialog(this, "Perhatian !", "Silahkan pilih waktu pengantaran yang tersedia", (dialog, which) -> {
            });
            return;
        }

        //send Data to Server
        sendDataToServer();
    };

    private void initPayment() {
        int totalPrice = productsPrice + transportFee;
        b.textViewTransportFee.setText(transportFee == 0 ? "-" : Constant.convertNumberToCurrency(transportFee));
        b.textViewProductsPrice.setText(productsPrice == 0 ? "-" : Constant.convertNumberToCurrency(productsPrice));
        b.textViewTotal.setText(totalPrice == 0 ? "-" : Constant.convertNumberToCurrency(totalPrice));

    }

    IssueRequestHandler req;

    private void openCard(int distance) {
        b.layoutInfo.setVisibility(View.GONE);
        b.card.setVisibility(View.VISIBLE);

        b.nestedScrollView.setVisibility(View.INVISIBLE);
        b.progress.setVisibility(View.VISIBLE);


        req.setIntention(new Throwable());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                //set time card
                JSONObject data = res.getData();

                JSONObject times = data.getJSONObject("times");
                JSONObject fee = data.getJSONObject("fee");

                b.textViewMorningTime.setText(times.getString("morning"));
                b.textViewNoonTime.setText(times.getString("noon"));
                b.textViewEveningTime.setText(times.getString("evening"));

                b.textViewMorningFee.setText(fee.getString("morning"));
                b.textViewNoonFee.setText(fee.getString("noon"));
                b.textViewEveningFee.setText(fee.getString("evening"));

                b.nestedScrollView.setVisibility(View.VISIBLE);
                b.progress.setVisibility(View.GONE);
            }

            @Override
            public void onRetry() {
                b.nestedScrollView.setVisibility(View.VISIBLE);
                b.progress.setVisibility(View.GONE);
                Constant.displayDialog(
                        ActivityServiceLocation.this,
                        "Perhatian !",
                        "Koneksi internet bermasalah, silahkan coba lagi",
                        true,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> req.backGroundRequest(RetrofitClient.getInstance(null).getApiServices().getTransportAndTime(distance))
                );

            }
        });

        req.backGroundRequest(RetrofitClient.getInstance(null).getApiServices().getTransportAndTime(distance));
    }

    private void closeCard() {
        b.layoutInfo.setVisibility(View.VISIBLE);
        b.card.setVisibility(View.GONE);
    }

    private void sendDataToServer() {
        b.nestedScrollView.setVisibility(View.INVISIBLE);
        b.progress.setVisibility(View.VISIBLE);

        req.setIntention(new Throwable());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {

                Constant.displayDialog(
                        ActivityServiceLocation.this,
                        "Perhatian !",
                        message,
                        true,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> {
                            startActivity(new Intent(ActivityServiceLocation.this, Dashboard.class));
                            finishAffinity();
                        }
                );

//                b.nestedScrollView.setVisibility(View.VISIBLE);
//                b.progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onRetry() {
                b.nestedScrollView.setVisibility(View.VISIBLE);
                b.progress.setVisibility(View.GONE);
                Constant.displayDialog(
                        ActivityServiceLocation.this,
                        "Perhatian !",
                        "Koneksi internet bermasalah, silahkan coba lagi",
                        true,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> sendDataToServer()
                );
            }
        });

        JSONObject products = new JSONObject();
        try {
            for (ActivityServiceGroceries.HelperModelCart p : productsInCart) {
                JSONObject prod = new JSONObject();
                prod.put("id", p.getId());
                prod.put("name", p.getName());
                prod.put("price", p.getPricePerUnit());
                prod.put("unit", p.getUnit());
                prod.put("quantity", p.getQuantity());
                prod.put("total_price", p.getTotalPrice());
                prod.put("type", serviceType);
                products.accumulate("products", prod);
            }
//            Log.e(TAG, "sendDataToServer: " + products.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "sendDataToServer: ERROR while composing JSON products ", e);
        }

        req.backGroundRequest(RetrofitClient.getInstance(null).getApiServices().setOrder(
                Guru.getInt(Constant.KEY_USER_ID, -1),
                b.textInputLayoutLandmark.getEditText().getText().toString(),
                deliveryTime,
                transportFee,
                Constant.convertCurrencyToNumber(b.textViewTotal.getText().toString()),
                serviceType,
                target.latitude + "," + target.longitude,
                products.toString()
        ));

    }
}
