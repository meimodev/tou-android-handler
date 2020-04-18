/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.squti.guru.Guru;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.NavFragment.Notification_Model;
import com.meimodev.sitouhandler.Dashboard.NavFragment.Notification_RecyclerAdapter;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

public class NavFragment_Member_Issue extends Fragment {
    private static final String TAG = "NavFragment_Member_Home";
    private View rootView;
    private Context context;

    @BindView(R.id.recyclerView_notifications)
    RecyclerView rvNotifications;

    @BindView(R.id.textView_sortAll)
    TextView tvSortAll;
    @BindView(R.id.textView_sortConfirmed)
    TextView tvSortConfirmed;
    @BindView(R.id.textView_sortUnconfirmed)
    TextView tvSortUnConfirmed;

    @BindView(R.id.layout_sort)
    CardView llSort;

    @BindView(R.id.textView_dataNotFound)TextView tvDataNotFound;

    private ArrayList<Notification_Model> recyclerItems;
    private ArrayList<Notification_Model> recyclerItemsDefault;
    private Notification_RecyclerAdapter recyclerAdapter;

    private View progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_member_issue, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        progress = Constant.makeProgressCircle(rootView);

        llSort.setVisibility(View.GONE);
        rvNotifications.setHasFixedSize(false);
        rvNotifications.setItemAnimator(new DefaultItemAnimator());
        rvNotifications.setLayoutManager(new LinearLayoutManager(context));


//        tvSortAll.setOnClickListener(this);
//        tvSortConfirmed.setOnClickListener(this);
//        tvSortUnConfirmed.setOnClickListener(this);


        return rootView;
    }


    private void setupRecyclerView() {

        recyclerAdapter = new Notification_RecyclerAdapter(
                context,
                recyclerItems,
                null,
                null
        );
        recyclerItemsDefault = new ArrayList<>(recyclerItems);
        rvNotifications.setAdapter(recyclerAdapter);


        if (progress.getVisibility() == View.VISIBLE) {
            progress.setVisibility(View.INVISIBLE);
        }
//        if (llSort.getVisibility() != View.VISIBLE) {
//            llSort.setVisibility(View.VISIBLE);
//        }
        if (rvNotifications.getVisibility() != View.VISIBLE)
            rvNotifications.setVisibility(View.VISIBLE);

        //        Set Default Sort
        tvSortAll.callOnClick();

    }

    private void fetchData() {
        if (progress.getVisibility() != View.VISIBLE)
            progress.setVisibility(View.VISIBLE);

        if (rvNotifications.getVisibility() == View.VISIBLE)
            rvNotifications.setVisibility(View.INVISIBLE);

        IssueRequestHandler requestHandler = new IssueRequestHandler(rootView);

        Call call = RetrofitClient.getInstance(null).getApiServices().getMemberIssue(
                Guru.getInt(Constant.KEY_MEMBER_ID, 0)
        );
        requestHandler.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {
            }

            @Override
            public void onSuccess(APIWrapper res, String message) throws JSONException {

                recyclerItems = new ArrayList<>();
                recyclerItemsDefault = new ArrayList<>();

                if (res.getDataArray().length() <= 0) {
                    tvDataNotFound.setVisibility(View.VISIBLE);
                }

                for (int i = 0; i < res.getDataArray().length(); i++) {
                    JSONObject data = res.getDataArray().getJSONObject(i);
                    recyclerItems.add(new Notification_Model(
                            data.getInt("issue_id"),
                            data.getInt("auth_id"),
                            data.getString("auth_status"),
                            data.getString("issue_key"),
                            data.getString("auth_on"),
                            data.getString("issued_by_member_name"),
                            data.getString("issued_by_member_column")
                    ));
                }

                setupRecyclerView();

            }

            @Override
            public void onRetry() {
              fetchData();
            }
        });
        requestHandler.enqueue(call);

    }

    @Override
    public void onResume() {
        fetchData();
        llSort.setVisibility(View.GONE);
        super.onResume();
    }
}
