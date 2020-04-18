/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Wizard.ApplyChurch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.CustomWidget.CustomEditText;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.Adding;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.Validator;
import com.meimodev.sitouhandler.databinding.ActivityApplyChurchBinding;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApplyChurch extends AppCompatActivity {
    private static final String TAG = "ApplyChurch";

    private ActivityApplyChurchBinding b;
    private int priestCount = 0;
    private int columnCount = 0;

    private int churchId = 0;

    private Integer positionIndex = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityApplyChurchBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        Constant.changeStatusColor(this, R.color.colorPrimary);

        b.layoutMembers.setVisibility(View.GONE);
//        b.layoutChurchInfo.setVisibility(View.GONE);

        b.buttonCancel.setOnClickListener(v -> finish());

        initChurchInfo();
    }

    private void initChurchInfo() {
        b.customEditTextBalance.setAsThousandSeparator();
        b.buttonProceed.setOnClickListener(v -> {
            if (validateInput()) {
                registerChurchInServer();
            }
        });
    }

    private boolean validateInput() {
        Validator v = new Validator(this);
        if (v.validateEmpty(b.textInputLayoutName) != null) return false;
        if (v.validateEmpty(b.textInputLayoutKelurahan) != null) return false;
        if (v.validateEmpty(b.textInputLayoutKecamatan) != null) return false;
        if (v.validateEmpty(b.textInputLayoutKabupaten) != null) return false;
        if (v.validateEmpty(b.textInputLayoutTerritory) != null) return false;
        if (v.validateEmpty(b.textInputLayoutAddress) != null) return false;
        if (v.validateEmpty(b.textInputLayoutPhone) != null) return false;
        if (v.validateEmpty(b.textInputLayoutBalance) != null) return false;
        if (v.validateEmpty(b.textInputLayoutColumnCount) != null) return false;
        if (v.validateEmpty(b.textInputLayoutPriestCount) != null) return false;
        return true;
    }

    private void registerChurchInServer() {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                JSONObject data = res.getData();
                proceedFromChurchInfo(data);
            }

            @Override
            public void onRetry() {

            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().churchCreation_setChurchInfo(
                b.textInputLayoutName.getEditText().getText().toString().trim(),
                b.textInputLayoutKelurahan.getEditText().getText().toString().trim(),
                b.textInputLayoutKecamatan.getEditText().getText().toString().trim(),
                b.textInputLayoutKabupaten.getEditText().getText().toString().trim(),
                b.textInputLayoutTerritory.getEditText().getText().toString().trim(),
                b.textInputLayoutAddress.getEditText().getText().toString().trim(),
                b.textInputLayoutPhone.getEditText().getText().toString().trim(),
                b.textInputLayoutBalance.getEditText().getText().toString().trim(),
                b.textInputLayoutColumnCount.getEditText().getText().toString().trim(),
                b.textInputLayoutPriestCount.getEditText().getText().toString().trim()
        ));
        priestCount = Integer.parseInt(b.textInputLayoutPriestCount.getEditText().getText().toString().trim());
        columnCount = Integer.parseInt(b.textInputLayoutColumnCount.getEditText().getText().toString().trim());
    }

    private void proceedFromChurchInfo(JSONObject data) throws JSONException {
        b.layoutChurchInfo.setVisibility(View.GONE);

        for (int i = 1; i < 11; i++) {
//            Log.e(TAG, "proceedFromChurchInfo: looping main index = " + i);
            View view = getLayoutInflater().inflate(R.layout.resource_list_item_church_creation_member, b.layoutMembers, false);
            TextView tvPosition = view.findViewById(R.id.textView_position),
                    tvName = view.findViewById(R.id.textView_name),
                    tvMemberInfo = view.findViewById(R.id.textView_memberInfo);
            ImageButton btn = view.findViewById(R.id.button_edit);

            int finalI = i - 1;
            View.OnClickListener onClickListener = v -> {
                Intent intent = new Intent(ApplyChurch.this, Adding.class);
                intent.putExtra("OPERATION_TYPE", Adding.OPERATION_FIND_USER);
                positionIndex = finalI;
                Log.e(TAG, "proceedFromChurchInfo: clicked pos = " + positionIndex);
                startActivityForResult(intent, Adding.REQUEST_CODE_PERSONAL_NAME);
            };
            btn.setOnClickListener(onClickListener);

            @ColorInt int backgroundColor = i % 2 == 0 ?
                    getResources().getColor(R.color.disabled_background)
                    : getResources().getColor(android.R.color.transparent);

            view.setBackgroundColor(backgroundColor);
            final int pos;
            tvName.setText("-");
            tvMemberInfo.setText("-");
            switch (i) {
                case 1:
                    tvPosition.setText(Constant.ACCOUNT_TYPE_CHIEF);
                    b.layoutMembers.addView(view);

                    break;
                case 2:
                    tvPosition.setText(Constant.ACCOUNT_TYPE_SECRETARY);
                    b.layoutMembers.addView(view);

                    break;
                case 3:
                    tvPosition.setText(Constant.ACCOUNT_TYPE_TREASURER);
                    b.layoutMembers.addView(view);

                    break;
                case 4:
                    //priests
                    for (int j = 1; j < (priestCount + 1); j++) {

                        View viewPriest = getLayoutInflater().inflate(R.layout.resource_list_item_church_creation_member, b.layoutMembers, false);
                        TextView tvPositionPriest = viewPriest.findViewById(R.id.textView_position);
                        ImageButton btnPriest = viewPriest.findViewById(R.id.button_edit);
                        final int priestPos = (i - 1) + (j - 1);
                        btnPriest.setOnClickListener(v -> {
                            Intent intent = new Intent(ApplyChurch.this, Adding.class);
                            intent.putExtra("OPERATION_TYPE", Adding.OPERATION_FIND_USER);
                            positionIndex = priestPos;
                            startActivityForResult(intent, Adding.REQUEST_CODE_PERSONAL_NAME);
                        });
                        String holder = Constant.ACCOUNT_TYPE_PRIEST + " " + j;
                        tvPositionPriest.setText(holder);

                        @ColorInt int priestBackgroundColor = j % 2 == 0 ?
                                getResources().getColor(android.R.color.transparent)
                                : getResources().getColor(R.color.disabled_background);

                        viewPriest.setBackgroundColor(priestBackgroundColor);

                        b.layoutMembers.addView(viewPriest);
                    }

                    break;
                case 5:

                    tvPosition.setText(Constant.ACCOUNT_TYPE_PENATUA_PKB);
                    b.layoutMembers.addView(view);
                    pos = (i - 1) + (priestCount - 1);
                    btn.setOnClickListener(v -> {
                        Intent intent = new Intent(ApplyChurch.this, Adding.class);
                        intent.putExtra("OPERATION_TYPE", Adding.OPERATION_FIND_USER);
                        positionIndex = pos;
                        startActivityForResult(intent, Adding.REQUEST_CODE_PERSONAL_NAME);
                    });
                    break;
                case 6:
                    tvPosition.setText(Constant.ACCOUNT_TYPE_PENATUA_WKI);
                    b.layoutMembers.addView(view);
                    pos = (i - 1) + (priestCount - 1);
                    btn.setOnClickListener(v -> {
                        Intent intent = new Intent(ApplyChurch.this, Adding.class);
                        intent.putExtra("OPERATION_TYPE", Adding.OPERATION_FIND_USER);
                        positionIndex = pos;
                        startActivityForResult(intent, Adding.REQUEST_CODE_PERSONAL_NAME);
                    });

                    break;
                case 7:
                    tvPosition.setText(Constant.ACCOUNT_TYPE_PENATUA_YOUTH);
                    b.layoutMembers.addView(view);
                    pos = (i - 1) + (priestCount - 1);
                    btn.setOnClickListener(v -> {
                        Intent intent = new Intent(ApplyChurch.this, Adding.class);
                        intent.putExtra("OPERATION_TYPE", Adding.OPERATION_FIND_USER);
                        positionIndex = pos;
                        startActivityForResult(intent, Adding.REQUEST_CODE_PERSONAL_NAME);
                    });
                    break;
                case 8:
                    tvPosition.setText(Constant.ACCOUNT_TYPE_PENATUA_REMAJA);
                    b.layoutMembers.addView(view);
                    pos = (i - 1) + (priestCount - 1);
                    btn.setOnClickListener(v -> {
                        Intent intent = new Intent(ApplyChurch.this, Adding.class);
                        intent.putExtra("OPERATION_TYPE", Adding.OPERATION_FIND_USER);
                        positionIndex = pos;
                        startActivityForResult(intent, Adding.REQUEST_CODE_PERSONAL_NAME);
                    });
                    break;
                case 9:
                    tvPosition.setText(Constant.ACCOUNT_TYPE_PENATUA_ANAK);
                    b.layoutMembers.addView(view);
                    pos = (i - 1) + (priestCount - 1);
                    btn.setOnClickListener(v -> {
                        Intent intent = new Intent(ApplyChurch.this, Adding.class);
                        intent.putExtra("OPERATION_TYPE", Adding.OPERATION_FIND_USER);
                        positionIndex = pos;
                        startActivityForResult(intent, Adding.REQUEST_CODE_PERSONAL_NAME);
                    });
                    break;
                case 10:
                    int indexer = 0;
                    for (int j = 1; j < (columnCount + 1); j++) {

                        View viewPenatua = getLayoutInflater().inflate(R.layout.resource_list_item_church_creation_member, b.layoutMembers, false);
                        TextView tvPositionPenatua = viewPenatua.findViewById(R.id.textView_position);
                        ImageButton btnPenatua = viewPenatua.findViewById(R.id.button_edit);
                        viewPenatua.setBackgroundColor(backgroundColor);
                        final int pntPos = (i - 1) + (priestCount - 1) + indexer;
                        btnPenatua.setOnClickListener(v -> {
                            Intent intent = new Intent(ApplyChurch.this, Adding.class);
                            intent.putExtra("OPERATION_TYPE", Adding.OPERATION_FIND_USER);
                            positionIndex = pntPos;
                            startActivityForResult(intent, Adding.REQUEST_CODE_PERSONAL_NAME);
                        });
                        String holderPenatua = Constant.ACCOUNT_TYPE_PENATUA + " " + j;
                        tvPositionPenatua.setText(holderPenatua);
                        b.layoutMembers.addView(viewPenatua);

                        indexer++;

                        View viewSyamas = getLayoutInflater().inflate(R.layout.resource_list_item_church_creation_member, b.layoutMembers, false);
                        TextView tvPositionSyamas = viewSyamas.findViewById(R.id.textView_position);
                        ImageButton btnSyamas = viewSyamas.findViewById(R.id.button_edit);
//                        viewSyamas.setBackgroundColor(backgroundColor);
                        final int symPos = pntPos + 1;
                        btnSyamas.setOnClickListener(v -> {
                            Intent intent = new Intent(ApplyChurch.this, Adding.class);
                            intent.putExtra("OPERATION_TYPE", Adding.OPERATION_FIND_USER);
                            positionIndex = symPos;
                            startActivityForResult(intent, Adding.REQUEST_CODE_PERSONAL_NAME);
                        });
//                        btnSyamas.setOnClickListener(onClickListener);
                        String holderSyamas = Constant.ACCOUNT_TYPE_SYAMAS + " " + j;
                        tvPositionSyamas.setText(holderSyamas);
                        b.layoutMembers.addView(viewSyamas);

                        indexer++;
                    }
                    break;
            }

        }

        JSONObject church = data.getJSONObject("church");
        churchId = church.getInt("id");

        b.layoutMembers.setVisibility(View.VISIBLE);

        b.buttonProceed.setOnClickListener(v -> appointChurchPosition());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            // get the child view that needed to alter
            // alter name & info

            View view = b.layoutMembers.getChildAt(positionIndex);
            TextView
                    tvName = view.findViewById(R.id.textView_name),
                    tvMemberInfo = view.findViewById(R.id.textView_memberInfo);

            Integer userId = data.getIntExtra("model.id", 0);
            view.setTag(userId);
            String name = data.getStringExtra("model.name");
            String dob = data.getStringExtra("model.bod");
            String sex = data.getStringExtra("model.kol");

            tvName.setText(name);
            tvMemberInfo.setText(String.format("%s\n%s", dob, sex));

        }
    }

    private void appointChurchPosition() {
        // read all the data inside b.layoutMembers
        // submit data to server

        JSONObject obj = new JSONObject();
        try {

            for (int i = 0; i < b.layoutMembers.getChildCount(); i++) {

                View view = b.layoutMembers.getChildAt(i);

                TextView tvPosition = view.findViewById(R.id.textView_position),
//                        tvName = view.findViewById(R.id.textView_name),
//                        tvMemberInfo = view.findViewById(R.id.textView_memberInfo),
                        etColumn = view.findViewById(R.id.editText_column);


//                String[] strs = tvMemberInfo.getText().toString().split("\\s+");
                String position = tvPosition.getText().toString();
                String column = etColumn.getText().toString();
                if (StringUtils.isEmpty(column)) {
                    throw new NullPointerException("Empty Column " + position);
                }
                int columnIndex = Integer.parseInt(column);
                if (columnIndex > columnCount) {
                    throw new NumberFormatException(position + " - Max Index = " + columnCount);
                }

                JSONObject pos = new JSONObject();
                pos.put("position", tvPosition.getText().toString());
                pos.put("user_id", view.getTag());
//                pos.put("name", tvName.getText().toString());
//                pos.put("dob", strs[0]);
//                pos.put("sex", strs[1]);
                pos.put("column_index", columnIndex);

                obj.accumulate("users", pos);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "appointChurchPosition: EXCEPTION ", e);
            if (e.getMessage() != null) {
                Snackbar.make(b.getRoot(), e.getMessage(), Snackbar.LENGTH_LONG).show();
                return;
            }
            Snackbar.make(b.getRoot(), "Unidentified Error", Snackbar.LENGTH_LONG).show();
            return;
        }

        Log.e(TAG, "appointChurchPosition: church id " + churchId);
        Log.e(TAG, "appointChurchPosition: JSON " + obj.toString());

        Snackbar.make(b.getRoot(), "OK", Snackbar.LENGTH_LONG).show();

        /*
         * Fetch data to server
         */
        sendPositionsDataToServer(churchId, obj);

    }

    private void sendPositionsDataToServer(int churchId, JSONObject userData) {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                Constant.displayDialog(ApplyChurch.this,
                        "OK",
                        res.getMessage(),
                        (dialog, which) -> {
                        }
                );
            }

            @Override
            public void onRetry() {
                sendPositionsDataToServer(churchId, userData);
            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().churchCreation_setChurchPositions(
                churchId, userData.toString()
        ));
    }



}
