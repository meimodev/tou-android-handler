/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services.Groceries;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.squti.guru.Guru;
import com.google.android.material.card.MaterialCardView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Services.ActivityServiceOrderType;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.databinding.ActivityServiceGroceriesBinding;
import com.shuhart.stickyheader.StickyHeaderItemDecorator;
import com.squareup.picasso.Picasso;
import com.yy.mobile.rollingtextview.CharOrder;
import com.yy.mobile.rollingtextview.strategy.Strategy;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.meimodev.sitouhandler.Constant.changeStatusColor;
import static com.meimodev.sitouhandler.Constant.convertNumberToCurrency;

public class ActivityServiceGroceries extends AppCompatActivity {

    private static final String TAG = "ActivityServiceGrocerie";

    private ActivityServiceGroceriesBinding b;
    private boolean isCartOpen = true;

    private IssueRequestHandler req;
    private boolean isChurchMember;

    private int vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityServiceGroceriesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Picasso.get().load(R.drawable.groceries_teaser).into(b.imageViewMain);

        changeStatusColor(this, R.color.colorPrimary);
        handleIntentInit();

        initProductRecyclerView();
        initCartRecyclerView();
        initSearchEditText();

        isChurchMember = Guru.getInt(Constant.KEY_CHURCH_ID, -99) > 0;

        b.layoutRecent.setOnClickListener(v -> {
            if (isCartOpen) {
                closeCart();
            } else {
                openCart();
            }
        });
        b.layoutCartOverlay.setOnClickListener(v -> closeCart());
        b.buttonNext.setOnClickListener(nextButtonOnClickListener);
        b.progress.setVisibility(View.GONE);

        openCart();
        closeCart();
        registerBroadcastReceiver();

        startTeaser(getIntent().getBooleanExtra(KEY_START_TEASER, false));

        initStraightToVendor();
    }

    public static final String KEY_START_TEASER = "KEY_START_TEASER";

    private void startTeaser(boolean start) {
        if (start) {
            b.layoutTeaser.setVisibility(View.VISIBLE);
            b.layoutMain.setVisibility(View.GONE);
            b.layoutTeaser.setOnClickListener(v -> {
                b.layoutTeaser.setVisibility(View.GONE);
                b.layoutMain.setVisibility(View.VISIBLE);
            });
            CountDownTimer countdownToTransition = new CountDownTimer(10000, 1000) {

                @SuppressLint("DefaultLocale")
                public void onTick(long millisUntilFinished) {
                    b.textViewStart.setText(String.format("MULAI BERBELANJA %d", millisUntilFinished / 1000));
                }

                public void onFinish() {
                    b.layoutTeaser.performClick();
                }
            };
            countdownToTransition.start();


        }
    }

    private String recentDesc, recentPrice;

    @SuppressLint("SetTextI18n")
    private void openCart() {

        ConstraintLayout.LayoutParams params = ((ConstraintLayout.LayoutParams) b.guideCart.getLayoutParams());
        params.guidePercent = 0.3f;
        b.guideCart.setLayoutParams(params);

        b.layoutCart.setVisibility(View.VISIBLE);
        b.layoutCartOverlay.setVisibility(View.VISIBLE);
        isCartOpen = true;

        recentDesc = b.textViewRecentDesc.getText().toString();
        recentPrice = b.textViewRecentPrice.getText().toString();
        b.textViewRecentDesc.setText("Lanjut Belanja");
        b.textViewRecentPrice.setVisibility(View.INVISIBLE);

        b.layoutRecent.setVisibility(View.INVISIBLE);

        updateCartUI();
    }

    private void closeCart() {
        ConstraintLayout.LayoutParams params = ((ConstraintLayout.LayoutParams) b.guideCart.getLayoutParams());
        params.guidePercent = 0.9f;
        b.guideCart.setLayoutParams(params);

        b.layoutRecent.setVisibility(View.VISIBLE);

        b.layoutCartOverlay.setVisibility(View.GONE);
        b.layoutCart.setVisibility(View.GONE);
        isCartOpen = false;

        b.textViewRecentDesc.setText(recentDesc);
        b.textViewRecentPrice.setText(recentPrice);
        b.textViewRecentPrice.setVisibility(View.VISIBLE);
        b.cardDivider.setVisibility(View.VISIBLE);

        alterRecent();
    }

    private void updateCartUI() {
        if (productsInCart.isEmpty()) {
            b.textViewCartTotalText.setVisibility(View.INVISIBLE);
            b.textViewCartTotal.setVisibility(View.INVISIBLE);
            b.recyclerViewCart.setVisibility(View.INVISIBLE);
            b.buttonNext.setEnabled(false);
            b.buttonNext.setCardBackgroundColor(getResources().getColor(R.color.disabled_background));
            b.layoutNotFound.getRoot().setVisibility(View.VISIBLE);
            return;
        } else {
            String s = productsInCart.size() + " Produk";
            b.textViewCartTotalText.setText(s);
            b.textViewCartTotalText.setVisibility(View.VISIBLE);
            b.textViewCartTotal.setVisibility(View.VISIBLE);
            b.recyclerViewCart.setVisibility(View.VISIBLE);
            b.buttonNext.setEnabled(true);
            b.buttonNext.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
            b.layoutNotFound.getRoot().setVisibility(View.GONE);

        }
        //reset total price
        b.textViewCartTotal.setText(Constant.convertNumberToCurrency(calculateTotalPrice()));
    }

    private int calculateTotalPrice() {
        int totalPrice = 0;
        for (HelperModelCart prod : adapterCart.getItems()) {
            totalPrice += prod.getTotalPrice();
        }
        return totalPrice;
    }

    private void initSearchEditText() {
        CountDownTimer countdownToFetchData = new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                fetchProductData();
            }
        };
        b.textInputLayoutSearch.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                countdownToFetchData.cancel();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 2) {
                    b.textInputLayoutSearch.setError("Harus melebihi 2 huruf");
                } else {
                    b.textInputLayoutSearch.setError(null);
                    countdownToFetchData.start();
                }
            }
        });

        b.textViewRecentPrice.setAnimationDuration(500L);
        b.textViewRecentPrice.setCharStrategy(Strategy.NormalAnimation());
        b.textViewRecentPrice.addCharOrder(CharOrder.Alphabet);
        b.textViewRecentPrice.setAnimationInterpolator(new AccelerateDecelerateInterpolator());

        b.textViewProductCount.setAnimationDuration(500L);
        b.textViewProductCount.setCharStrategy(Strategy.NormalAnimation());
        b.textViewProductCount.addCharOrder(CharOrder.Alphabet);
        b.textViewProductCount.setAnimationInterpolator(new AccelerateDecelerateInterpolator());


        b.cardDivider.setVisibility(View.INVISIBLE);
        b.textViewRecentPrice.setVisibility(View.INVISIBLE);


    }

    private void alterRecent() {
        String desc, price;
        if (adapterCart.getItems().isEmpty()) {
            desc = "Keranjang Kosong";
            price = "  -  ";
            b.textViewProductCount.setVisibility(View.INVISIBLE);
            b.textViewRecentPrice.setVisibility(View.INVISIBLE);
        } else {
            desc = " Produk";
            price = Constant.convertNumberToCurrency(calculateTotalPrice());
            if (b.textViewProductCount.getVisibility() != View.VISIBLE) {
                b.textViewProductCount.setVisibility(View.VISIBLE);
            }
            if (b.textViewRecentPrice.getVisibility() != View.VISIBLE) {
                b.textViewRecentPrice.setVisibility(View.VISIBLE);
            }

        }

        b.textViewRecentPrice.setText(price);
        b.textViewProductCount.setText(String.valueOf(adapterCart.getItemCount()));
        b.textViewRecentDesc.setText(desc);

    }

    /*Product RecyclerView*/
    private ArrayList<HelperModelProduct> products;
    private ActivityServiceGroceries_Product_RecyclerAdapter adapterProduct;

    private void initProductRecyclerView() {
        products = new ArrayList<>();
        adapterProduct =
                new ActivityServiceGroceries_Product_RecyclerAdapter(
                        this,
                        products,
                        productClickListener
                );

        b.recyclerViewProducts.setHasFixedSize(true);
        b.recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        b.recyclerViewProducts.setItemAnimator(new DefaultItemAnimator());
        b.recyclerViewProducts.setAdapter(adapterProduct);
        fetchProductRecommendation();
    }

    private void fetchProductRecommendation() {
        b.recyclerViewProducts.setVisibility(View.GONE);
        b.progress.setVisibility(View.VISIBLE);

        req = new IssueRequestHandler(b.getRoot());
        req.setIntention(new Throwable());
        req.setContext(this);
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                b.recyclerViewProducts.setVisibility(View.VISIBLE);
                b.progress.setVisibility(View.GONE);
                JSONObject data = res.getData();
                JSONArray dataProducts = data.getJSONArray("products");
                products.clear();
                for (int i = 0; i < dataProducts.length(); i++) {
                    JSONObject obj = dataProducts.getJSONObject(i);
                    products.add(new HelperModelProduct(
                            obj.getInt("id"),
                            obj.getString("name"),
                            obj.getInt("price"),
                            obj.getString("unit"),
                            obj.getString("image"),
                            obj.getInt("vendor_id"),
                            obj.getString("vendor_name"),
                            obj.getBoolean("is_available"),
                            obj.getString("is_available_msg")
                    ));
                }

                JSONArray vendorsArray = data.getJSONArray("vendors");

                for (int i = 0; i < vendorsArray.length(); i++) {
                    JSONObject obj = vendorsArray.getJSONObject(i);
                    products.add(new HelperModelProduct(
                            obj.getInt("id"),
                            obj.getString("name"),
                            obj.getString("image"),
                            obj.getString("open"),
                            obj.getString("close"),
                            obj.getBoolean("is_open")
                    ));
                }
                adapterProduct.notifyDataSetChanged();
                searchProductWithType = "";

                if (dataProducts.length() <= 0 && vendorsArray.length() <= 0) {
                    b.layoutDataNotFound.getRoot().setVisibility(View.VISIBLE);
                } else {
                    b.layoutDataNotFound.getRoot().setVisibility(View.GONE);
                }

                if (STRAIGHT_TO_VENDOR) {
                    b.recyclerViewProducts.setVisibility(View.GONE);
                    STRAIGHT_TO_VENDOR = false;
                }
            }

            @Override
            public void onRetry() {
                b.recyclerViewProducts.setVisibility(View.VISIBLE);
                b.progress.setVisibility(View.GONE);

                Constant.displayDialog(
                        ActivityServiceGroceries.this,
                        "Perhatian !",
                        "Koneksi internet bermasalah, silahkan coba lagi",
                        true,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> fetchProductRecommendation()
                );
            }
        });
        req.backGroundRequest(
                RetrofitClient.getInstance(null).getApiServices()
                        .findProductRecommendation(searchProductWithType)
        );

    }

    private void fetchProductData() {

        b.recyclerViewProducts.setVisibility(View.GONE);
        b.progress.setVisibility(View.VISIBLE);

        if (req != null) req.getCall().cancel();

        req = new IssueRequestHandler(b.getRoot());
        req.setIntention(new Throwable());
        req.setContext(this);
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                b.recyclerViewProducts.setVisibility(View.VISIBLE);


                b.progress.setVisibility(View.GONE);

                products.clear();

                JSONObject data = res.getData();
                JSONArray productsArray = data.getJSONArray("products");
                JSONArray vendorsArray = data.getJSONArray("vendors");

                for (int i = 0; i < productsArray.length(); i++) {
                    JSONObject obj = productsArray.getJSONObject(i);
                    products.add(new HelperModelProduct(
                            obj.getInt("id"),
                            obj.getString("name"),
                            obj.getInt("price"),
                            obj.getString("unit"),
                            obj.getString("image"),
                            obj.getInt("vendor_id"),
                            obj.getString("vendor_name"),
                            obj.getBoolean("is_available"),
                            obj.getString("is_available_msg")
                    ));
                }

                for (int i = 0; i < vendorsArray.length(); i++) {
                    JSONObject obj = vendorsArray.getJSONObject(i);
                    products.add(new HelperModelProduct(
                            obj.getInt("id"),
                            obj.getString("name"),
                            obj.getString("image"),
                            obj.getString("open"),
                            obj.getString("close"),
                            obj.getBoolean("is_open")
                    ));
                }

                adapterProduct.notifyDataSetChanged();

                if (products.size() == 0) {
                    b.layoutDataNotFound.getRoot().setVisibility(View.VISIBLE);
                    b.recyclerViewProducts.setVisibility(View.GONE);
                } else {
                    b.layoutDataNotFound.getRoot().setVisibility(View.GONE);
                    b.recyclerViewProducts.setVisibility(View.VISIBLE);
                }
                if (isVendorDetailOpen) isVendorDetailOpen = false;


            }

            @Override
            public void onRetry() {
                b.recyclerViewProducts.setVisibility(View.VISIBLE);
                b.progress.setVisibility(View.GONE);

                Constant.displayDialog(
                        ActivityServiceGroceries.this,
                        "Perhatian !",
                        "Koneksi internet bermasalah, silahkan coba lagi",
                        true,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> fetchProductData()
                );
            }
        });

        // search Request goes HERE
        //vendor products
        //native vendor
        String vendor = "";
        String key = b.textInputLayoutSearch.getEditText().getText().toString();
        String type = "";

        if (isVendorDetailOpen) {
            vendor = b.textInputLayoutSearch.getEditText().getText().toString().trim();
        }

        req.backGroundRequest(RetrofitClient.getInstance(null).getApiServices().findProductWithParams(
                key, type, vendor
        ));

    }

    public static String KEY_VENDOR_PRODUCTS = "KEY_IS_NATIVE_VENDOR";
    private boolean isVendorDetailOpen = false;
    private int VENDOR_ID;

    BroadcastReceiver brSearchVendorProducts = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().contentEquals(KEY_VENDOR_PRODUCTS)) {
                VENDOR_ID = intent.getIntExtra("VENDOR_ID", 0);
                openVendorDetail();
            }
        }
    };

    private void registerBroadcastReceiver() {
        registerReceiver(brSearchVendorProducts, new IntentFilter(KEY_VENDOR_PRODUCTS));
    }

    private void unregisterBroadcastReceiver() {
        unregisterReceiver(brSearchVendorProducts);
    }

    public static String KEY_SEARCH_PRODUCT_TYPE = "KEY_SEARCH_PRODUCT_TYPE";
    public static final String KEY_STRAIGHT_TO_VENDOR_DETAIL = "TO_VENDOR_DETAIL";
    public boolean STRAIGHT_TO_VENDOR = false;
    private String searchProductWithType;
    public static boolean STOP_N_SHOP = false;

    private void handleIntentInit() {
        searchProductWithType = getIntent().getStringExtra(KEY_SEARCH_PRODUCT_TYPE);
        if (searchProductWithType != null && searchProductWithType.contentEquals("SSS")) {
            STOP_N_SHOP = true;
        }

        String straightToVendorKey = getIntent().getStringExtra(KEY_STRAIGHT_TO_VENDOR_DETAIL);
        if (straightToVendorKey != null && straightToVendorKey.contentEquals(KEY_STRAIGHT_TO_VENDOR_DETAIL)) {
            STRAIGHT_TO_VENDOR = true;
        }

    }

    /*Cart RecyclerView*/
    private ArrayList<HelperModelCart> productsInCart;
    private ActivityServiceGroceries_Cart_RecyclerAdapter adapterCart;

    private void initCartRecyclerView() {
        productsInCart = new ArrayList<>();
        adapterCart =
                new ActivityServiceGroceries_Cart_RecyclerAdapter(
                        this,
                        productsInCart,
                        cartClickListener
                );

        b.recyclerViewCart.setHasFixedSize(true);
        b.recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        b.recyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        b.recyclerViewCart.setAdapter(adapterCart);
    }

    public OnRecyclerItemOperationListener.ItemClickListener productClickListener = data -> {


        int id = data.getInt("ID");
        int price = data.getInt("PRICE");
        int pos = data.getInt("POS");
        String name = data.getString("NAME");
        String unit = data.getString("UNIT");

        String vName = data.getString("VENDOR_NAME");
        int vId = data.getInt("VENDOR_ID");

        vendorId = data.getInt("VENDOR_ID");

        //check if product to be add is already exist in arrayList
        boolean productExist = false;

        int existHelperModelIndex = 0;
        for (HelperModelCart model : productsInCart) {
            if (model.id == id) {
                productExist = true;
                break;
            }
            existHelperModelIndex++;
        }
        if (productExist) {
            HelperModelCart existProduct = productsInCart.get(existHelperModelIndex);

            if (!isChurchMember && existProduct.getQuantity() + 1 > Constant.VALUE_MAX_NON_MEMBER_PRODUCT_UNIT && !STOP_N_SHOP) {
                Constant.displayDialog(
                        ActivityServiceGroceries.this,
                        "Perhatian !",
                        "Dikarenakan akun anda tidak terdaftar sebagai anggota dari Gereja mitra TOU, " +
                                "Pemesanan Produk ini (" + existProduct.getName() + ") tidak bisa melebihi " +
                                Constant.VALUE_MAX_NON_MEMBER_PRODUCT_UNIT +
                                " " + existProduct.getUnit() +
                                System.lineSeparator() +
                                System.lineSeparator() +
                                "untuk menghilangkan batasan ini, silahkan daftarkan akun sebagai anggota gereja di gereja masing-masing, jika Gereja anda merupakan mitra TOU"
                        ,
                        (dialog, which) -> {
                        }
                );
                return;
            }
            existProduct.setQuantity(existProduct.getQuantity() + 1);
            adapterCart.notifyItemChanged(existHelperModelIndex);
            Constant.displayDialog(
                    this,
                    "Perhatian!",
                    "Produk bernama: " +
                            existProduct.getName() +
                            ", Sudah ada dalam keranjang. Silahkan mengatur jumlah satuan dalam keranjang belanja",
                    true,
                    (dialog, which) -> {
                    },
                    null,
                    dialog -> openCart());
            return;
        }

        if (!isChurchMember && productsInCart.size() + 1 > Constant.VALUE_MAX_NON_MEMBER_PRODUCT_COUNT && !STOP_N_SHOP) {
            Constant.displayDialog(
                    ActivityServiceGroceries.this,
                    "Perhatian !",
                    "Dikarenakan akun anda tidak terdaftar sebagai anggota dari Gereja mitra TOU, " +
                            "Jumlah Produk dalam keranjang ini tidak bisa melebihi " +
                            Constant.VALUE_MAX_NON_MEMBER_PRODUCT_COUNT + " Produk" +
                            System.lineSeparator() +
                            System.lineSeparator() +
                            "untuk menghilangkan batasan ini, silahkan daftarkan akun sebagai anggota gereja di gereja masing-masing, jika Gereja anda merupakan mitra TOU"
                    ,
                    (dialog, which) -> {
                    }
            );
            return;
        }

        if (!productsInCart.isEmpty()) {
            HelperModelCart lastProduct = productsInCart.get(productsInCart.size() - 1);
            if (vId != lastProduct.getVendorId()) {
                Constant.displayDialog(
                        ActivityServiceGroceries.this,
                        "Pesanan hanya bisa dari 1 tempat",
                        "Dikarenakan produk yang di masukkan dalam keranjang anda sebelumnya dari '"
                                + lastProduct.getVendorName() + "' disarankan untuk produk selanjutnya juga dari tempat yang sama" +
                                System.lineSeparator() +
                                System.lineSeparator() +
                                "Jika ingin menambahkan produk dari partner lain, silahkan lakukan pemesanan produk dengan pesanan baru",
                        (dialog, which) -> {
                        }
                );
                return;
            }
        }

        productsInCart.add(new HelperModelCart(
                id, name, 1, price, unit, vId, vName));

        adapterCart.notifyItemChanged(pos);

        alterRecent();
    };
    public OnRecyclerItemOperationListener.ItemClickListener cartClickListener = data -> {
//        int id = data.getInt("ID");
//        String name = data.getString("NAME");
//        int price = data.getInt("PRICE");
//        String unit = data.getString("UNIT");
//        Toast.makeText(this, "cart clicked id = "+id, Toast.LENGTH_SHORT).show();

        //update total price
        updateCartUI();

    };
    public View.OnClickListener nextButtonOnClickListener = v -> {

        if (vendorId == Constant.RESERVED_USER_ID)
            Guru.putBoolean(Constant.KEY_IS_DELIVERY, true);

        Log.e(TAG, "===========================LOG HEAD===========================");
        int i = 1;
        for (HelperModelCart model : adapterCart.getItems()) {
            Log.e(TAG, "Product " + i);
            Log.e(TAG, "Id " + model.getId());
            Log.e(TAG, "Name " + model.getName());
            Log.e(TAG, "Quantity " + model.getQuantity());
            Log.e(TAG, "Unit " + model.getUnit());
            Log.e(TAG, "PricePerunit " + model.getPricePerUnit());
            Log.e(TAG, "TotalPrice " + model.getTotalPrice());
            Log.e(TAG, "---------------------------------");
            i++;
        }
        Log.e(TAG, "==============================================================");

        Intent intent = new Intent(this, ActivityServiceOrderType.class);
        Bundle bundle = new Bundle();
        bundle.putInt("PRICE", calculateTotalPrice());
        bundle.putString("SERVICE_TYPE", Constant.PRODUCT_TYPE_GROCERIES);
        bundle.putSerializable("PRODUCTS", adapterCart.getItems());
        bundle.putInt("VENDOR_ID", vendorId);
        intent.putExtra("BUNDLE", bundle);
        startActivity(intent);
    };

    public static class HelperModelProduct {
        private int id;
        private String name;
        private int price;
        private String unit;
        private String imageUrl;

        private String vendorName;
        private int vendorId;

        private boolean isVendor;

        private boolean isAvailable;
        private String isAvailableMessage;

        private boolean isChecked = false;

        //For Products
        HelperModelProduct(int id, String name, int price, String unit,
                           String imageUrl, int vendorId, String vendorName,
                           boolean isAvailable, String isAvailableMessage) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.unit = unit;
            this.imageUrl = imageUrl;
            this.vendorId = vendorId;
            this.vendorName = vendorName;
            this.isAvailable = isAvailable;
            this.isAvailableMessage = isAvailableMessage;
            isVendor = false;
        }

        //For Vendors
        private String openHour;
        private String closeHour;
        private boolean isVendorOpen;

        HelperModelProduct(int vendorId, String name, String imageUrl,
                           String openHour, String closeHour,
                           boolean isVendorOpen) {
            this.vendorId = vendorId;
            this.name = name;
            this.price = 0;
            this.unit = "";
            this.imageUrl = imageUrl;
            this.openHour = openHour;
            this.closeHour = closeHour;
            this.isVendorOpen = isVendorOpen;
            isVendor = true;
        }


        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public String getUnit() {
            return unit;
        }

        public boolean isVendor() {
            return isVendor;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getVendorName() {
            return vendorName;
        }

        public int getVendorId() {
            return vendorId;
        }

        public String getIsAvailableMessage() {
            return isAvailableMessage;
        }

        public boolean isAvailable() {
            return isAvailable;
        }

        public String getOpenHour() {
            return openHour;
        }

        public void setOpenHour(String openHour) {
            this.openHour = openHour;
        }

        public String getCloseHour() {
            return closeHour;
        }

        public boolean isVendorOpen() {
            return isVendorOpen;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public static class HelperModelCart implements Serializable {
        private int id;
        private String name;
        private int quantity;
        private int pricePerUnit;
        private int totalPrice;
        private String unit;

        private int vendorId;
        private String vendorName;

        HelperModelCart(int id, String name, int quantity, int pricePerUnit, String unit, int vendorId, String vendorName) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
            this.totalPrice = pricePerUnit * quantity;
            this.unit = unit;
            this.vendorId = vendorId;
            this.vendorName = vendorName;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getPricePerUnit() {
            return pricePerUnit;
        }

        public int getTotalPrice() {
            return totalPrice;
        }

        public String getUnit() {
            return unit;
        }

        //        public void setId(int id) {
//            this.id = id;
//        }
//        public void setName(String name) {
//            this.name = name;
//        }
        public void setQuantity(int quantity) {
            this.quantity = quantity;
            totalPrice = pricePerUnit * quantity;
        }

        public int getVendorId() {
            return vendorId;
        }

        public void setVendorId(int vendorId) {
            this.vendorId = vendorId;
        }

        public String getVendorName() {
            return vendorName;
        }

        public void setVendorName(String vendorName) {
            this.vendorName = vendorName;
        }
//        public void setPricePerUnit(int pricePerUnit) {
//            this.pricePerUnit = pricePerUnit;
//        }
//        public void setTotalPrice(int totalPrice) {
//            this.totalPrice = totalPrice;
//        }
    }

    @Override
    public void onBackPressed() {
        if (isCartOpen) {
            closeCart();
            return;
        }
        if (isVendorDetailOpen) {
            closeVendorDetail();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        STOP_N_SHOP = false;
        unregisterBroadcastReceiver();
        super.onDestroy();
    }

    /*
     * Vendor Detail Functions
     */

    private void openVendorDetail() {
        Log.e(TAG, "onReceive: Open Vendor Detail");
        isVendorDetailOpen = true;

        b.textInputLayoutSearch.setVisibility(View.GONE);
        b.layoutVendorDetail.getRoot().setVisibility(View.VISIBLE);
        b.recyclerViewProducts.setVisibility(View.GONE);

        fetchVendorData();
    }

    private void closeVendorDetail() {
        isVendorDetailOpen = false;
//        Log.e(TAG, "onReceive: Close Vendor Detail");
//
//        b.recyclerViewProducts.setVisibility(View.VISIBLE);
//        b.layoutVendorDetail.getRoot().setVisibility(View.GONE);
//        b.textInputLayoutSearch.setVisibility(View.VISIBLE);
        finish();
    }

    private void fetchVendorData() {
        b.layoutVendorDetail.layoutVendorDetailMain.setVisibility(View.GONE);
        ProgressBar progressBar = b.layoutVendorDetail.progressVendorDetail;
        if (progressBar.getVisibility() != View.VISIBLE) {
            progressBar.setVisibility(View.VISIBLE);
        }

        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setIntention(new Throwable());
        req.setContext(this);
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                initVendorDetailViews(res.getData());
            }

            @Override
            public void onRetry() {
                progressBar.setVisibility(View.GONE);

                Constant.displayDialog(
                        ActivityServiceGroceries.this,
                        "Perhatian !",
                        "Koneksi internet bermasalah, silahkan coba lagi",
                        true,
                        (dialog, which) -> {
                        },
                        null,
                        dialog -> fetchVendorData()
                );

            }
        });

        if (VENDOR_ID != 0) {
            req.backGroundRequest(RetrofitClient.getInstance(null).getApiServices().getVendor(VENDOR_ID, true));
        } else {
            Constant.displayDialog(
                    ActivityServiceGroceries.this,
                    "INVALID VID",
                    "This request contain invalid VID, try again to request",
                    true,
                    (dialog, which) -> {
                    },
                    null,
                    dialog -> finish()
            );
        }
    }

    private void initVendorDetailViews(JSONObject data)
            throws JSONException {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter grayscale = new ColorMatrixColorFilter(matrix);

        int vId = data.getInt("id");
        String vName = data.getString("name");
        boolean isOpen = data.getBoolean("open");
        JSONObject operation = data.getJSONObject("operation");
        String vOpen = operation.getString("open_hour");
        String vClose = operation.getString("close_hour");
        String vImageUrl = data.getString("image");

        b.layoutVendorDetail.textViewVendorName.setText(vName);
        b.layoutVendorDetail.textViewVendorQuote.setText(isOpen ? "(BUKA)" : "(TUTUP)");
        b.layoutVendorDetail.textViewVendorOperationHour.setText(String.format("%s - %s", vOpen, vClose));
        Picasso.get().load(vImageUrl)
                .fit().transform(new CropCircleTransformation())
                .placeholder(R.drawable.ic_sss_logo_icon)
                .into(b.layoutVendorDetail.imageViewVendorImage);

        if (!isOpen) {
            b.layoutVendorDetail.imageViewVendorImage.setColorFilter(grayscale);
        }

//        LinearLayout llProducts = b.layoutVendorDetail.layoutVendorProducts;
//        llProducts.removeAllViews();


        /*
         * Setting up the recycler view with sticky header
         */
        JSONArray products = data.getJSONArray("products");
        JSONArray categories = data.getJSONArray("categories");

        initVendorDetailProducts(products, categories, vId, vName);

    }

    private int scrollCounter = 0;

    private void initVendorDetailProducts(JSONArray products,
                                          JSONArray categories,
                                          int vId, String vName)
            throws JSONException {

        ArrayList<VendorDetail_Product_Header_Model> headers = new ArrayList<>();
        ArrayList<VendorDetail_Product_Child_Model> childs = new ArrayList<>();

        ArrayList<String> headersForSpinner = new ArrayList<>();
        //gather header
        for (int i = 0; i < categories.length(); i++) {
            JSONObject category = categories.getJSONObject(i);

            VendorDetail_Product_Header_Model model =
                    new VendorDetail_Product_Header_Model(category.getInt("section"));
            int categorySize = category.getJSONArray("items").length();
            model.setName(category.getString("name") + " (" + categorySize + ")");
            model.setSize(categorySize);
            headers.add(model);
            headersForSpinner.add(model.getName());
        }

        //gather child
        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.getJSONObject(i);

            VendorDetail_Product_Child_Model model = new VendorDetail_Product_Child_Model(
                    product.getInt("id"),
                    product.getString("name"),
                    product.getString("image"),
                    product.getString("price"),
                    product.getString("unit"),
                    product.getBoolean("is_available"),
                    product.getString("is_available_message")
            );
            model.setVendorId(vId);
            model.setVendorName(vName);
            model.setSection(product.getInt("section"));
            childs.add(model);
        }


        //merge Header & child
        ArrayList<SectionVendorProducts> arrayListMain;
        arrayListMain = new ArrayList<>(childs);
        for (VendorDetail_Product_Header_Model header : headers) {
            arrayListMain.add(header.sectionPosition(), header);
        }

        SectionVendorProductsAdapter adapter = new SectionVendorProductsAdapter(
                arrayListMain,
                this,
                productClickListener
        );

        /*
         * Setup Category Spinner
         */


        RecyclerView recyclerView = b.layoutVendorDetail.recyclerViewVendorProducts;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int topItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                Log.e(TAG, "onScrolled: findFirstCompletelyVisibleItemPosition = " + topItemPosition);
//
                int findFirst = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                Log.e(TAG, "onScrolled: findFirstItemPosition = " + findFirst);

                int findLastComplete = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                Log.e(TAG, "onScrolled: findLastCompletelyVisibleItemPosition = " + findLastComplete);

                int findLast = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                Log.e(TAG, "onScrolled: findLastVisibleItemPosition = " + findLast);


                if (adapter.getItemCount() < 5) {
                    return;
                }
                if (topItemPosition > 4 && dy > 5) {
                    if (b.layoutVendorDetail.layoutVendorInfo.getVisibility() == View.VISIBLE)
                        b.layoutVendorDetail.layoutVendorInfo.setVisibility(View.GONE);
                    return;
                }
                if (topItemPosition == 0 || topItemPosition == 2 && dy < -60) {
                    if (b.layoutVendorDetail.layoutVendorInfo.getVisibility() != View.VISIBLE)
                        b.layoutVendorDetail.layoutVendorInfo.setVisibility(View.VISIBLE);
                    return;
                }
            }
        };

        recyclerView.addOnScrollListener(onScrollListener);
        StickyHeaderItemDecorator decorator = new StickyHeaderItemDecorator(adapter);
        decorator.attachToRecyclerView(recyclerView);


        MaterialSpinner spinner = b.layoutVendorDetail.spinnerCategory;

        spinner.setItems(headersForSpinner);
        spinner.setOnItemSelectedListener((view, position, id, item) -> {

            for (SectionVendorProducts p : arrayListMain) {
                if (p.getName().equals(item.toString())) {

                    Log.e(TAG, "initVendorDetailProducts: Scroll Listener Added");
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            int topItemPositionComplete = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                            int topItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();


//                            Log.e(TAG, "onScrolled: topItemPosition "
//                                    + topItemPositionComplete
//                                    + " Stop " + p.sectionPosition() + " dy " + dy);

                            if (topItemPositionComplete >= p.sectionPosition() && dy > 0) {
                                Log.e(TAG, "onScrolled: UPWARD");
                                recyclerView.stopScroll();
                            } else if ((topItemPositionComplete - 1) <= p.sectionPosition() && dy < 0) {
                                Log.e(TAG, "onScrolled: DOWNWARD");
                                recyclerView.stopScroll();
                            }

                            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                                recyclerView.removeOnScrollListener(this);
                            }
                        }
                    });

                    int topItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                    int offset = Math.max(p.getSize(), 10);
                    if (p.sectionPosition() + p.getSize() > topItemPosition) {
                        recyclerView.smoothScrollToPosition(p.sectionPosition() + offset);
                    } else if (p.sectionPosition() < topItemPosition) {
                        recyclerView.smoothScrollToPosition(p.sectionPosition() - offset);
                    }

                }
            }
            /*
             * Iterate trough all child+header model to find correct object
             * model by name. then get its position saved in model object
             * than rv.scroll to that retrieved position
             */

//            recyclerView.scrollToPosition(position);
        });

        b.layoutVendorDetail.layoutVendorDetailMain.setVisibility(View.VISIBLE);
        b.layoutVendorDetail.progressVendorDetail.setVisibility(View.GONE);

    }

    private void initStraightToVendor() {
        if (STRAIGHT_TO_VENDOR) {

            Intent i = new Intent(KEY_VENDOR_PRODUCTS);
            i.putExtra("VENDOR_NAME", getIntent().getStringExtra("VENDOR_NAME"));
            i.putExtra("VENDOR_ID", getIntent().getIntExtra("VENDOR_ID", 0));
            sendBroadcast(i);
//            STRAIGHT_TO_VENDOR = false;

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isVendorDetailOpen) {
//            productsInCart.clear();
//            adapterCart.notifyDataSetChanged();
//            updateCartUI();
//            closeCart();
            fetchVendorData();

        }
    }


}
