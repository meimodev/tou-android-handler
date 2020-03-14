/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Member;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;
import com.meimodev.sitouhandler.Dashboard.NavFragment.Notification_Model;
import com.meimodev.sitouhandler.Dashboard.NavFragment.Notification_RecyclerAdapter;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.Issue.OnRecyclerItemOperationListener;
import com.meimodev.sitouhandler.IssueDetail.IssueDetail;
import com.meimodev.sitouhandler.R;
import com.meimodev.sitouhandler.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import static com.meimodev.sitouhandler.Constant.toggleSort;

public class NavFragment_Member_Home extends Fragment implements View.OnClickListener {
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

    @BindView(R.id.textView_dataNotFound)
    TextView tvDataNotFound;

    private ArrayList<Notification_Model> recyclerItems;
    private ArrayList<Notification_Model> recyclerItemsDefault;
    private Notification_RecyclerAdapter recyclerAdapter;

    private View progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_member_home, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        progress = Constant.makeProgressCircle(rootView);

        llSort.setVisibility(View.GONE);
        rvNotifications.setHasFixedSize(false);
        rvNotifications.setItemAnimator(new DefaultItemAnimator());
        rvNotifications.setLayoutManager(new LinearLayoutManager(context));

        tvSortAll.setOnClickListener(this);
        tvSortConfirmed.setOnClickListener(this);
        tvSortUnConfirmed.setOnClickListener(this);

        getActivity().registerReceiver(brAuthorizationConfirm, new IntentFilter(IssueDetail.KEY_ISSUE_DETAIL_CONFIRM_AUTH));

        return rootView;
    }

    private void setupRecyclerView() {
        OnRecyclerItemOperationListener.AcceptItemListener acceptItemListener =
                data -> sendData(data.getInt("id"), data.getInt("status"));
        OnRecyclerItemOperationListener.RejectItemListener rejectItemListener  =
                data -> sendData(data.getInt("id"), data.getInt("status"));

        recyclerAdapter = new Notification_RecyclerAdapter(
                context,
                recyclerItems,
                acceptItemListener,
                rejectItemListener
        );

        recyclerItemsDefault = new ArrayList<>(recyclerItems);
        rvNotifications.setAdapter(recyclerAdapter);


        if (progress.getVisibility() == View.VISIBLE) {
            progress.setVisibility(View.INVISIBLE);
        }
        if (llSort.getVisibility() != View.VISIBLE) {
            llSort.setVisibility(View.VISIBLE);
        }
        if (rvNotifications.getVisibility() != View.VISIBLE)
            rvNotifications.setVisibility(View.VISIBLE);

        //        Set Default Sort
        tvSortAll.callOnClick();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_sortAll:
                toggleSort(context, tvSortAll, true);
                toggleSort(context, tvSortConfirmed, false);
                toggleSort(context, tvSortUnConfirmed, false);
                Snackbar.make(rootView, "menampilkan SEMUA data", Snackbar.LENGTH_LONG).show();
                resetRecyclerView(recyclerItemsDefault);
                break;
            case R.id.textView_sortConfirmed:
                toggleSort(context, tvSortAll, false);
                toggleSort(context, tvSortConfirmed, true);
                toggleSort(context, tvSortUnConfirmed, false);
                Snackbar.make(rootView, "sortir data yang SUDAH DIKONFIRMASI", Snackbar.LENGTH_LONG).show();
                Collections.sort(recyclerItems);
                Collections.reverse(recyclerItems);
                resetRecyclerView(recyclerItems);
                break;
            case R.id.textView_sortUnconfirmed:
                toggleSort(context, tvSortAll, false);
                toggleSort(context, tvSortConfirmed, false);
                toggleSort(context, tvSortUnConfirmed, true);
                Snackbar.make(rootView, "sortir data yang BELUM DIKONFIRMASI", Snackbar.LENGTH_LONG).show();
                Collections.sort(recyclerItems);
                resetRecyclerView(recyclerItems);
                break;

        }
    }

    private void fetchData() {

        if (progress.getVisibility() != View.VISIBLE)
            progress.setVisibility(View.VISIBLE);

        if (rvNotifications.getVisibility() == View.VISIBLE)
            rvNotifications.setVisibility(View.INVISIBLE);

        if (tvDataNotFound.getVisibility() == View.VISIBLE)
            tvDataNotFound.setVisibility(View.INVISIBLE);

        IssueRequestHandler requestHandler = new IssueRequestHandler(rootView);
        Call call = RetrofitClient.getInstance(null).getApiServices().getMemberHome(
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
                } else {
                    tvDataNotFound.setVisibility(View.INVISIBLE);
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
//                if (progress.getVisibility() == View.VISIBLE) {
//                    progress.setVisibility(View.INVISIBLE);
//                }
//                if (llSort.getVisibility() == View.VISIBLE) {
//                    llSort.setVisibility(View.INVISIBLE);
//                }
//                if (rvNotifications.getVisibility() == View.VISIBLE)
//                    rvNotifications.setVisibility(View.INVISIBLE);

                fetchData();
            }
        });

        requestHandler.enqueue(call);


    }

    private void sendData(int authorizeId, int authorizeCode) {
        if (progress.getVisibility() != View.VISIBLE)
            progress.setVisibility(View.VISIBLE);

        if (rvNotifications.getVisibility() == View.VISIBLE)
            rvNotifications.setVisibility(View.INVISIBLE);

        IssueRequestHandler requestHandler = new IssueRequestHandler(rootView);

        Call call = RetrofitClient.getInstance(null).getApiServices().authorizeIssue(
                authorizeId,
                authorizeCode == Constant.AUTHORIZATION_STATUS_CODE_ACCEPTED
                        ? Constant.AUTHORIZATION_STATUS_ACCEPTED
                        : Constant.AUTHORIZATION_STATUS_REJECTED
        );
        requestHandler.enqueue(call);
        requestHandler.setOnRequestHandler(new IssueRequestHandler.OnRequestHandler() {
            @Override
            public void onTry() {
            }

            @Override
            public void onSuccess(APIWrapper res, String message) {
                fetchData();
                Constant.displayDialog(context, "OK", res.getMessage(), (dialog, which) -> {});
            }

            @Override
            public void onRetry() {
                requestHandler.enqueue(call);
            }
        });

    }

    private void resetRecyclerView(ArrayList<Notification_Model> items) {
        rvNotifications.setVisibility(View.GONE);
        recyclerAdapter = new Notification_RecyclerAdapter(
                context,
                items,
                data -> {
                    if (data != null) {
//                        Snackbar.make(rootView, "ACCEPT " + data.getString("name") + " " + data.getString("column"), Snackbar.LENGTH_SHORT).show();
                        sendData(
                                data.getInt("id"),
                                data.getInt("status")
                        );
                    }
                },
                data -> {
                    if (data != null) {
//                        Snackbar.make(rootView, "DENIED " + data.getString("name") + " " + data.getString("column"), Snackbar.LENGTH_SHORT).show();
                        sendData(
                                data.getInt("id"),
                                data.getInt("status")
                        );
                    }
                }
        );
        rvNotifications.setVisibility(View.VISIBLE);
        rvNotifications.setAdapter(recyclerAdapter);
    }

    private BroadcastReceiver brAuthorizationConfirm = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().contentEquals(IssueDetail.KEY_ISSUE_DETAIL_CONFIRM_AUTH)){
                Log.e(TAG, "onReceive: auth confirm broadcast received, sending data...." );

                sendData(intent.getIntExtra("AUTH_ID",0), intent.getIntExtra("AUTH_CODE", 99));
            }
        }
    };

    @Override
    public void onResume() {
        fetchData();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(brAuthorizationConfirm);

        super.onDestroy();
    }
}
