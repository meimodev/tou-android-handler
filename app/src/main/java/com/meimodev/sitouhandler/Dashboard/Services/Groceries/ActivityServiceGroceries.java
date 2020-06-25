/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.Services.Groceries;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.squti.guru.Guru;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Services.ActivityServiceLocation;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.databinding.ActivityServiceGroceriesBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static com.meimodev.sitouhandler.Constant.changeStatusColor;
import static com.meimodev.sitouhandler.Constant.convertNumberToCurrency;

public class ActivityServiceGroceries extends AppCompatActivity {

    private static final String TAG = "ActivityServiceGrocerie";

    private ActivityServiceGroceriesBinding b;
    private boolean isCartOpen = true;

    private IssueRequestHandler req;
    private boolean isChurchMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityServiceGroceriesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Picasso.get().load(R.drawable.groceries_teaser).into(b.imageViewMain);

        changeStatusColor(this, R.color.colorPrimary);

        b.layoutTeaser.setOnClickListener(v -> {
            b.layoutTeaser.setVisibility(View.GONE);
            b.layoutMain.setVisibility(View.VISIBLE);
        });

        initProductRecyclerView();
        initCartRecyclerView();
        initSearchEditText();

        isChurchMember = Guru.getInt(Constant.KEY_CHURCH_ID, -99) > 0;

        b.layoutRecent.setOnClickListener(v -> {
            if (isCartOpen) {
                closeCart();
            }
            else {
                openCart();
            }
        });
        b.layoutCartOverlay.setOnClickListener(v -> closeCart());
        b.buttonNext.setOnClickListener(nextButtonOnClickListener);
        b.progress.setVisibility(View.GONE);

        openCart();
        closeCart();

        startTransitionTimer();
    }

    private void startTransitionTimer(){
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

    private String recentDesc, recentPrice;
    private void openCart() {

        ConstraintLayout.LayoutParams params = ((ConstraintLayout.LayoutParams) b.guideCart.getLayoutParams());
        params.guidePercent = 0.4f;
        b.guideCart.setLayoutParams(params);

        b.layoutCart.setVisibility(View.VISIBLE);
        b.layoutCartOverlay.setVisibility(View.VISIBLE);
        isCartOpen = true;

        recentDesc = b.textViewRecentDesc.getText().toString();
        recentPrice = b.textViewRecentPrice.getText().toString();
        b.textViewRecentDesc.setText("Lanjut Belanja");
        b.textViewRecentPrice.setText("");

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
        }
        else {
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
        CountDownTimer countdownToFetchData = new CountDownTimer(2000, 1000) {

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
                }
                else {
                    b.textInputLayoutSearch.setError(null);
                    countdownToFetchData.start();
                }
            }
        });
    }

    private void alterRecent() {
        String desc, price;
        if (adapterCart.getItems().isEmpty()) {
            desc = "Keranjang Belanja Kosong";
            price = "  -  ";
        }
        else {
            desc = adapterCart.getItems().size() + " Bahan";
            price = Constant.convertNumberToCurrency(calculateTotalPrice());
        }

        b.textViewRecentPrice.setText(price);
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
        b.recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        b.recyclerViewProducts.setItemAnimator(new DefaultItemAnimator());
        b.recyclerViewProducts.setAdapter(adapterProduct);
        fetchProductRecommendation();
    }

    private void fetchProductRecommendation(){
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
                JSONArray data = res.getDataArray();
                products.clear();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    products.add(new HelperModelProduct(
                            obj.getInt("id"),
                            obj.getString("name"),
                            obj.getInt("price"),
                            obj.getString("unit")
                    ));
                }
                adapterProduct.notifyDataSetChanged();

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
                        .findProductRecommendation()
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
                JSONArray data = res.getDataArray();
                products.clear();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    products.add(new HelperModelProduct(
                            obj.getInt("id"),
                            obj.getString("name"),
                            obj.getInt("price"),
                            obj.getString("unit")
                    ));
                }
                adapterProduct.notifyDataSetChanged();
                if (products.size() == 0){
                    b.layoutDataNotFound.getRoot().setVisibility(View.VISIBLE);
                    b.recyclerViewProducts.setVisibility(View.GONE);
                } else {
                    b.layoutDataNotFound.getRoot().setVisibility(View.GONE);
                    b.recyclerViewProducts.setVisibility(View.VISIBLE);
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
                        dialog -> fetchProductData()
                );
            }
        });
        req.backGroundRequest(
                RetrofitClient.getInstance(null).getApiServices()
                        .findProductByName(
                                Constant.PRODUCT_TYPE_GROCERIES,
                                b.textInputLayoutSearch.getEditText().getText().toString()
                        )
        );
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


    public OnRecyclerItemOperationListener.ItemClickListener cartClickListener = data -> {
//        int id = data.getInt("ID");
//        String name = data.getString("NAME");
//        int price = data.getInt("PRICE");
//        String unit = data.getString("UNIT");
//        Toast.makeText(this, "cart clicked id = "+id, Toast.LENGTH_SHORT).show();

        //update total price
        updateCartUI();

    };
    public OnRecyclerItemOperationListener.ItemClickListener productClickListener = data -> {
        int id = data.getInt("ID");
        int price = data.getInt("PRICE");
        int pos = data.getInt("POS");
        String name = data.getString("NAME");
        String unit = data.getString("UNIT");

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
        if (!productExist) {

            if (!isChurchMember && productsInCart.size()+1 > Constant.VALUE_MAX_NON_MEMBER_PRODUCT_COUNT) {
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

            productsInCart.add(new HelperModelCart(
                    id, name, 1, price, unit));
        }
        else {
            HelperModelCart existProduct = productsInCart.get(existHelperModelIndex);
            if (!isChurchMember && existProduct.getQuantity() + 1 > Constant.VALUE_MAX_NON_MEMBER_PRODUCT_UNIT) {
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
            Constant.displayDialog(
                    this,
                    "Perhatian!",
                    "Bahan Makanan " +
                            existProduct.getName() +
                            ", Sudah ada dalam keranjang. Silahkan mengatur jumlah satuan dalam keranjang belanja",
                    true,
                    (dialog, which) -> {
                    },
                    null,
                    dialog -> openCart());
        }

        adapterCart.notifyItemChanged(pos);

        alterRecent();
    };
    public View.OnClickListener nextButtonOnClickListener = v -> {

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

        Intent intent = new Intent(this, ActivityServiceLocation.class);
        Bundle bundle = new Bundle();
        bundle.putInt("PRICE", calculateTotalPrice());
        bundle.putString("SERVICE_TYPE", Constant.PRODUCT_TYPE_GROCERIES);
        bundle.putSerializable("PRODUCTS", adapterCart.getItems());
        intent.putExtra("BUNDLE", bundle);
        startActivity(intent);
    };


    public static class HelperModelProduct {
        private int id;
        private String name;
        private int price;
        private String unit;

        HelperModelProduct(int id, String name, int price, String unit) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.unit = unit;
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

    }

    public static class HelperModelCart implements Serializable {
        private int id;
        private String name;
        private int quantity;
        private int pricePerUnit;
        private int totalPrice;
        private String unit;

        HelperModelCart(int id, String name, int quantity, int pricePerUnit, String unit) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
            this.totalPrice = pricePerUnit * quantity;
            this.unit = unit;
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
//        public void setPricePerUnit(int pricePerUnit) {
//            this.pricePerUnit = pricePerUnit;
//        }
//        public void setTotalPrice(int totalPrice) {
//            this.totalPrice = totalPrice;
//        }
    }


}
