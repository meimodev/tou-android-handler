/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Vendor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SignUp.SignUp;
import com.meimodev.sitouhandler.Validator;
import com.meimodev.sitouhandler.databinding.ActivityVendorProductDetailBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class VendorProductDetail extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "VendorProductDetail";

    private ActivityVendorProductDetailBinding b;

    private IssueRequestHandler req;

    private ArrayList<HelperProduct> products;
    private boolean isEdit = false;
    private int productId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityVendorProductDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        Constant.changeStatusColor(this, R.color.colorPrimary);
        req = new IssueRequestHandler(b.getRoot());
        ((CustomEditText) b.textInputLayoutPrice.getEditText()).setAsThousandSeparator();
        fetchAvailableProducts();

    }

    private void fetchAvailableProducts() {
        req.setIntention(new Throwable());
        req.setContext(this);
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                products = new ArrayList<>();
                JSONArray data = res.getDataArray();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    products.add(new HelperProduct(
                            obj.getInt("id"),
                            Integer.parseInt(obj.getString("price")),
                            obj.getString("name"),
                            obj.getString("unit"),
                            obj.getString("type")
                    ));
                }
                initViews();
            }

            @Override
            public void onRetry() {
                fetchAvailableProducts();
            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getAllProducts());

    }

    private void initViews() {
        ArrayList<String> productNames = new ArrayList<>();
        for (HelperProduct p : products) {
            productNames.add(p.getName());
        }
        b.autoCompleteTextView.setAdapter(
                new ArrayAdapter<>(
                        this,
                        R.layout.resoruce_dropdown_popup_large,
                        productNames));
        b.autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            isEdit = true;
            b.textViewStatus.setText("EDIT");

            int index = 0;
            for (HelperProduct p : products) {
                if (p.getName().contentEquals(((TextView) view).getText())) {
                    productId = p.getId();
                    break;
                }
                index++;
            }

            b.textInputLayoutPrice.getEditText().setText(String.valueOf(products.get(index).getPrice()));
            b.textInputLayoutUnit.getEditText().setText(products.get(index).getUnit());

        });
        b.autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isEdit = false;
                b.textViewStatus.setText("ADD");
                b.textInputLayoutPrice.getEditText().setText("");
                b.textInputLayoutUnit.getEditText().setText("");
            }
        });

        b.textViewStatus.setText("ADD");
        b.buttonSave.setOnClickListener(this);
        b.buttonDelete.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Call<ResponseBody> call = null;
        if (v == b.buttonSave) {
            if (!validateInput()) return;
            if (isEdit) {
                call = RetrofitClient.getInstance(null).getApiServices().editProduct(
                        productId,
                        b.textInputLayoutName.getEditText().getText().toString(),
                        Integer.parseInt(b.textInputLayoutPrice.getEditText().getText().toString().replace(",", "")),
                        b.textInputLayoutUnit.getEditText().getText().toString()
                );
            }
            else {
                call = RetrofitClient.getInstance(null).getApiServices().setProduct(
                        b.textInputLayoutName.getEditText().getText().toString(),
                        Integer.parseInt(b.textInputLayoutPrice.getEditText().getText().toString().replace(",", "")),
                        b.textInputLayoutUnit.getEditText().getText().toString()
                );

            }
        }
        else if (v == b.buttonDelete) {
            if (isEdit) {
                call = RetrofitClient.getInstance(null).getApiServices().deleteProduct(productId);
            }
            else {
                Constant.displayDialog(
                        VendorProductDetail.this,
                        "Perhatian !",
                        "No Existing Product is choosen",
                        (dialog, which) -> {
                        }
                );
                return;
            }
        }

        req.setIntention(new Throwable());
        Call<ResponseBody> finalCall = call;
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                if (v == b.buttonDelete) {
                    b.textInputLayoutName.getEditText().setText("");
                    b.textInputLayoutPrice.getEditText().setText("");
                    b.textInputLayoutUnit.getEditText().setText("");
                }

                Constant.displayDialog(
                        VendorProductDetail.this,
                        "Perhatian !",
                        message,
                        true,
                        (dialog, which) -> {},
                        null,
                        dialog -> fetchAvailableProducts()
                );

            }

            @Override
            public void onRetry() {
                req.enqueue(finalCall);
            }
        });
        req.enqueue(call);
    }

    private boolean validateInput() {

        Validator validator = new Validator(this);
        if (validator.validateEmpty(b.textInputLayoutName) != null) return false;
        if (validator.validateEmpty(b.textInputLayoutPrice) != null) return false;
        if (validator.validateEmpty(b.textInputLayoutUnit) != null) return false;
//        if (validator.validateEmpty(b.textInputLayoutType) != null) return false;

        return true;
    }

    public static class HelperProduct {
        private int id, price;
        private String name, unit, type;

        public HelperProduct(int id, int price, String name, String unit, String type) {
            this.id = id;
            this.price = price;
            this.name = name;
            this.unit = unit;
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public int getPrice() {
            return price;
        }

        public String getName() {
            return name;
        }

        public String getUnit() {
            return unit;
        }

        public String getType() {
            return type;
        }
    }

}
