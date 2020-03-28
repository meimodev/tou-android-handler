/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Wizard.DuplicateCheck;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.squti.guru.Guru;
import com.google.gson.JsonObject;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.databinding.ActivityDuplicateCheckBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;

public class DuplicateCheck extends AppCompatActivity {
    private static final String TAG = "DuplicateCheck";

    private ActivityDuplicateCheckBinding b;
    private int selectedMemberId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityDuplicateCheckBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        Constant.changeStatusColor(this, R.color.colorPrimary);
        Constant.justify(b.text);

        fetchData();
        initButton();
    }

    @Override
    public void onBackPressed() {
        Constant.displayDialog(
                this,
                "Perhatian !",
                "Anda akan melewati 'Periksa Duplikasi', artinya anda tidak menemukan data anggota yang kemungkinan adalah duplikasi dari data anda\n\nSilahkan sentuh tombol 'OK' untuk melanjutkan LEWATI",
                (dialog, which) -> skip()
        );
    }

    private void fetchData() {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                proceed(res);
            }

            @Override
            public void onRetry() {
                fetchData();
            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getDuplicateCheck(
                Guru.getInt(Constant.KEY_MEMBER_ID, 0)
        ));
        //TODO API services ?
    }

    private void proceed(APIWrapper res) throws JSONException {
        if (res.getData().isNull("similar_members")) {
            Constant.displayDialog(
                    this,
                    "OK",
                    "Pemeriksaan duplikasi data selesai, tidak menemukan duplikasi",
                    false,
                    (dialog, which) -> {},
                    null,
                    dialog -> skip()
                    );
            return;
        }
        initMemberList(res.getData().getJSONArray("similar_members"));
    }

    private void initMemberList(JSONArray memberList) throws JSONException {
        //Radio Buttons Listener
        CompoundButton.OnCheckedChangeListener rbListener = (buttonView, isChecked) -> {
            if (b.textViewSelectedMember.getVisibility() != View.VISIBLE) {
                b.textViewSelectedMember.setVisibility(View.VISIBLE);
            }

            if (isChecked) {
                buttonView.setBackgroundColor(getResources().getColor(R.color.backGround_fragment));
                b.textViewSelectedMember.setText(buttonView.getText().toString().replace("\n", " | "));
                selectedMemberId = buttonView.getId();
            }
            else {
                buttonView.setBackgroundColor(getResources().getColor(R.color.background));
            }
        };
        //Radio Buttons Layout Params
        LinearLayout.MarginLayoutParams params = new LinearLayout.MarginLayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // Init
        for (int i = 0; i < memberList.length(); i++) {
            JSONObject obj = memberList.getJSONObject(i);
            RadioButton rb = new RadioButton(this);

            String text = String.format(
                    "%s\n%s\n%s\n%s",
                    obj.getString("full_name"),
                    obj.getString("dob"),
                    obj.getString("sex"),
                    obj.getString("column")
            );

            rb.setText(text);
            rb.setMaxLines(20);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                TypedValue outValue = new TypedValue();
                getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                rb.setForeground(getDrawable(outValue.resourceId));
                rb.setFocusable(true);
                rb.setClickable(true);
            }
            rb.setId(obj.getInt("member_id"));
            rb.setBackgroundColor(getResources().getColor(R.color.background));
            rb.setPaddingRelative(32, 16, 32, 16);
            rb.setLayoutParams(params);
            rb.setOnCheckedChangeListener(rbListener);
            b.radioGroupMembers.addView(rb);
        }
    }

    private void initButton() {
        View.OnClickListener onClickListener = v -> {

            if (v == b.buttonConfirm) {
                if (b.textViewSelectedMember.getText().length() < 1) {
                    Constant.displayDialog(
                            DuplicateCheck.this,
                            "Perhatian !",
                            "Untuk konfirmasi anda harus memilih satu data anggota"
                    );
                    return;
                }
                String selectedMemberString = b.textViewSelectedMember.getText().toString().replace(" | ", "\n");
                Constant.displayDialog(
                        this,
                        "Perhatian !",
                        "Anda telah memilih:\n\n"
                                + selectedMemberString + "\n\n"
                                + "Silahkan sentuh tombol 'OK' untuk melanjutkan KONFIRMASI",
                        (dialog, which) -> confirm()
                );
            }
            else if (v == b.buttonSkip) {
                Constant.displayDialog(
                        this,
                        "Perhatian !",
                        "Anda akan melewati 'Periksa Duplikasi', artinya anda tidak menemukan data anggota yang kemungkinan adalah duplikasi dari data anda\n\nSilahkan sentuh tombol 'OK' untuk melanjutkan LEWATI",
                        (dialog, which) -> skip()
                );
            }
        };
        b.buttonConfirm.setOnClickListener(onClickListener);
        b.buttonSkip.setOnClickListener(onClickListener);
    }

    private void confirm() {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                String selectedMemberString = b.textViewSelectedMember.getText().toString().replace(" | ", "\n");
                Guru.putInt(Constant.KEY_MEMBER_DUPLICATE_CHECK, 1);
                Constant.displayDialog(
                        DuplicateCheck.this,
                        "OK, Klaim berhasil",
                        "Klaim duplikasi data atas nama:\n\n"
                                + selectedMemberString + "\n\n"
                                + "Berhasil, data tersebut telah berhasil diklaim, Silahkan sentuh 'OK' untuk melanjutkan",
                        false,
                        (dialog, which) -> {},
                        null,
                        dialog -> finish()
                );

            }

            @Override
            public void onRetry() {
                confirm();
            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().setDuplicateCheck(
                Guru.getInt(Constant.KEY_MEMBER_ID, 0),
                "CONFIRM",
                selectedMemberId
        ));
    }

    private void skip() {
        IssueRequestHandler req = new IssueRequestHandler(b.getRoot());
        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {
                Guru.putInt(Constant.KEY_MEMBER_DUPLICATE_CHECK, 1);
                finish();
            }

            @Override
            public void onRetry() {
                skip();
            }
        });
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().setDuplicateCheck(
                Guru.getInt(Constant.KEY_MEMBER_ID, 0),
                "SKIP"
        ));
    }


}
