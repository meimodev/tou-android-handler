/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.squti.guru.Guru;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.SphericalUtil;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.Dashboard.Services.Groceries.ActivityServiceGroceries;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.databinding.ActivityServiceLocationBinding;
import com.meimodev.sitouhandler.databinding.ActivityServiceOrderTypeBinding;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import static com.meimodev.sitouhandler.Constant.changeStatusColor;

public class ActivityServiceOrderType extends AppCompatActivity {
    private static final String TAG = "ActivityServiceOrderTyp";

    private ActivityServiceOrderTypeBinding b;

    private boolean isOnTheSpotCardOpen = false;

    private View.OnClickListener onTheSpotClickListener = v -> {
        order();
    };
    private ArrayList<ActivityServiceGroceries.HelperModelCart> productsInCart;
    private int productsPrice;
    private String serviceType;
    private int vendorId;

    private IssueRequestHandler req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityServiceOrderTypeBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        req = new IssueRequestHandler(b.getRoot());
        changeStatusColor(this, R.color.colorPrimary);
        Bundle bundle = getIntent().getBundleExtra("BUNDLE");
        initBundleData(bundle);

        b.cardViewOnTheSpot.setVisibility(View.GONE);
        b.progress.setVisibility(View.GONE);
        b.layoutNoDelivery.setVisibility(View.GONE);

        b.buttonOnTheSpot.setOnClickListener(v -> {
            openOnTheSpotCard();
        });


        if (Guru.getBoolean(Constant.KEY_IS_DELIVERY, false)){
            noDelivery();
            return;
        }

        b.buttonDeliver.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityServiceLocation.class);

            intent.putExtra("BUNDLE", bundle);
            startActivity(intent);
        });

    }

    private void initBundleData(Bundle bundle) {
        Serializable serializable = Objects.requireNonNull(bundle).getSerializable("PRODUCTS");
        productsInCart = (ArrayList<ActivityServiceGroceries.HelperModelCart>) serializable;
        productsPrice = bundle.getInt("PRICE", 0);
        serviceType = bundle.getString("SERVICE_TYPE");
        vendorId = bundle.getInt("VENDOR_ID", -1);
    }

    private void openOnTheSpotCard() {
        isOnTheSpotCardOpen = true;
        b.guideVertical.setGuidelinePercent(1.0f);
        b.cardViewOnTheSpot.setVisibility(View.VISIBLE);

        //set the views according to data passing from prev activity
        b.textViewProductsPrice.setText(Constant.convertNumberToCurrency(productsPrice));
        b.textViewTransportFee.setText("-");
        b.textViewTotal.setText(Constant.convertNumberToCurrency(productsPrice));

        b.buttonOnTheSpot.setText("PESAN");
        b.buttonOnTheSpot.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        b.buttonOnTheSpot.setOnClickListener(onTheSpotClickListener);
        b.buttonDeliver.setVisibility(View.GONE);
        b.imageViewDeliver.setVisibility(View.GONE);
        b.layoutOnTheSpot.setBackgroundColor(getResources().getColor(R.color.colorAccent4End));
        b.textInputLayoutNote.getEditText().setText("");
        b.textInputLayoutNote.setError(null);

    }

    private void closeOnTheSpotCard() {
        isOnTheSpotCardOpen = false;
        b.guideVertical.setGuidelinePercent(0.5f);
        b.cardViewOnTheSpot.setVisibility(View.GONE);

        b.buttonOnTheSpot.setText("Pesan ditempat");
        b.buttonOnTheSpot.setBackgroundColor(getResources().getColor(R.color.colorAccent4End));
        b.buttonOnTheSpot.setOnClickListener(v -> openOnTheSpotCard());
        b.buttonDeliver.setVisibility(View.VISIBLE);
        b.imageViewDeliver.setVisibility(View.VISIBLE);
        b.layoutOnTheSpot.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

    }

    private void order() {
        // validate
        String note = b.textInputLayoutNote.getEditText().getText().toString();
        b.textInputLayoutNote.setError(null);

        if (note.length() <= 0) {
            b.textInputLayoutNote.setError("Tidak bisa kosong");
            return;
        }

        if (!StringUtils.isNumeric(note)) {
            b.textInputLayoutNote.setError("Harus di isi dengan angka");
            return;
        }

        postDataToServer();
    }

    private void postDataToServer() {

        b.imageOnTheSpot.setVisibility(View.GONE);
        b.buttonOnTheSpot.setVisibility(View.GONE);
        b.cardViewOnTheSpot.setVisibility(View.GONE);
        b.progress.setVisibility(View.VISIBLE);

        req.setContext(this);
        req.setIntention(new Throwable());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            public void onSuccess(APIWrapper res, String message) throws JSONException {
                int oID = res.getData().getInt("id");
                Constant.displayDialog(
                        ActivityServiceOrderType.this,
                        "Perhatian !",
                        message,
                        true,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> {
                            startActivity(new Intent(ActivityServiceOrderType.this, Dashboard.class));

                            Intent i = new Intent(ActivityServiceOrderType.this, ActivityOrderDetail.class);
                            i.putExtra("ID", oID);
                            startActivity(i);

                            finishAffinity();
                        }
                );

            }

            @Override
            public void onRetry() {
                Constant.displayDialog(
                        ActivityServiceOrderType.this,
                        "Terjadi Kesalahan -_- !",
                        "Koneksi internet bermasalah, Silahkan coba sesaat lagi.",
                        true,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> finish()
                );
            }
        });

        JSONObject products = new JSONObject();
        JSONArray arr = new JSONArray();
        int index = 0;
        try {
            for (ActivityServiceGroceries.HelperModelCart p : productsInCart) {
                JSONObject prod = new JSONObject();
                prod.put("id", p.getId());
                prod.put("name", p.getName());
                prod.put("price", p.getPricePerUnit());
                prod.put("unit", p.getUnit());
                prod.put("quantity", p.getQuantity());
                prod.put("total_price", p.getTotalPrice());
                prod.put("status", "DOING");
                prod.put("type", serviceType);
                arr.put(index, prod);
                index++;
            }
            products.put("products", arr);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "sendDataToServer: ERROR while composing JSON products ", e);
        }

        req.backGroundRequest(
                RetrofitClient.getInstance(null).getApiServices().setOrder(
                        Guru.getInt(Constant.KEY_USER_ID, -1),
                        "Meja " + b.textInputLayoutNote.getEditText().getText().toString(),
                        "",
                        0,
                        Constant.convertCurrencyToNumber(b.textViewTotal.getText().toString()),
                        serviceType,
                        "",
                        products.toString()
                ));
    }

    @Override
    public void onBackPressed() {
        if (isOnTheSpotCardOpen) {
            closeOnTheSpotCard();
            return;
        }
        if (b.progress.getVisibility() == View.VISIBLE){
            Snackbar.make(b.getRoot(), "Mohon tunggu sebentar -_- Sedang memuat ...", Snackbar.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    private void noDelivery() {
        b.layoutNoDelivery.setVisibility(View.VISIBLE);
        b.buttonDeliver.setEnabled(false);
    }

}
