package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Secretary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.IssueDetail.IssueDetail;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NavFragment_Secretary_Papers extends Fragment implements View.OnClickListener {

    private static final String TAG = "NavFragment_Secretary_P";

    private View rootView;
    private Context context;

    @BindView(R.id.textView_sortOut)
    TextView tvSortOut;
    @BindView(R.id.textView_sortIn)
    TextView tvSortIn;
    @BindView(R.id.layout_main)
    RecyclerView rvLetters;

    @BindView(R.id.textView_dataNotFound)
    TextView tvNotFound;


    private ArrayList<NavFragment_Secretary_Papers_RecyclerModel> inboundLetter;
    private ArrayList<NavFragment_Secretary_Papers_RecyclerModel> outboundLetter;
    private OnRecyclerItemOperationListener.ItemClickListener recyclerItemClickListener = data -> {
        Intent intent = new Intent(context, IssueDetail.class);
        if (data != null)
            intent.putExtra("ISSUE_ID", data.getInt("ISSUE_ID"));
        startActivity(intent);
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.nav_activity_secretary_papers, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        inboundLetter = new ArrayList<>();
        outboundLetter = new ArrayList<>();

        tvSortOut.setOnClickListener(this);
        tvSortIn.setOnClickListener(this);
        rvLetters.setOnClickListener(this);

        rvLetters.setHasFixedSize(false);
        rvLetters.setLayoutManager(new LinearLayoutManager(context));
        rvLetters.setItemAnimator(new DefaultItemAnimator());
        rvLetters.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        tvSortOut.performClick();

        return rootView;

    }


    private void fetchData(boolean isOutbound) {

        IssueRequestHandler req = new IssueRequestHandler(rootView);
        req.enqueue(RetrofitClient.getInstance(null).getApiServices().getIssuedLetters(
                ((int) SharedPrefManager.load(context, SharedPrefManager.KEY_MEMBER_ID))
        ));

        req.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {

            }

            @Override
            public void onSuccess(APIWrapper res, String message) {
                inboundLetter = new ArrayList<>();
                outboundLetter = new ArrayList<>();

                try {
                    JSONArray arr = res.getDataArray();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        if (obj.getString("letter_type").contentEquals(Constant.LETTER_TYPE_OUTBOUND)) {
                            outboundLetter.add(new NavFragment_Secretary_Papers_RecyclerModel(
                                    obj.getInt("issue_id"),
                                    obj.getString("key_issue"),
                                    obj.getString("letter_entry_number")
                            ));
                        } else if (obj.getString("letter_type").contentEquals(Constant.LETTER_TYPE_INBOUND)) {
                            inboundLetter.add(new NavFragment_Secretary_Papers_RecyclerModel(
                                    obj.getInt("issue_id"),
                                    obj.getString("key_issue"),
                                    obj.getString("letter_entry_number")
                            ));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onSuccess: JSON ERROR " + e.getMessage());
                }

                if (isOutbound) {
                    rvLetters.setAdapter(new NavFragment_Secretary_Papers_RecyclerAdapter(context, outboundLetter, recyclerItemClickListener));
                    if (outboundLetter.size() == 0) tvNotFound.setVisibility(View.VISIBLE);
                    else tvNotFound.setVisibility(View.GONE);
                } else {
                    rvLetters.setAdapter(new NavFragment_Secretary_Papers_RecyclerAdapter(context, inboundLetter, recyclerItemClickListener));
                    if (inboundLetter.size() == 0) tvNotFound.setVisibility(View.VISIBLE);
                    else tvNotFound.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRetry() {
                req.enqueue(RetrofitClient.getInstance(null).getApiServices().getIssuedLetters(
                        ((int) SharedPrefManager.load(context, SharedPrefManager.KEY_MEMBER_ID))
                ));
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_sortOut:
                Constant.toggleSort(context, tvSortOut, true);
                Constant.toggleSort(context, tvSortIn, false);
                fetchData(true);
                break;
            case R.id.textView_sortIn:
                Constant.toggleSort(context, tvSortOut, false);
                Constant.toggleSort(context, tvSortIn, true);
                fetchData(false);
                break;
        }
    }

    @Override
    public void onResume() {
        tvSortOut.performClick();
        super.onResume();
    }
}
