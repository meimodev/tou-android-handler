/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_Treasurer;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.squti.guru.Guru;
import com.google.android.material.snackbar.Snackbar;
import com.meimodev.sitouhandler.Constant;
import com.meimodev.sitouhandler.Dashboard.Dashboard;
import com.meimodev.sitouhandler.Helper.APIWrapper;
import com.meimodev.sitouhandler.Issue.IssueRequestHandler;
import com.meimodev.sitouhandler.RetrofitClient;
import com.meimodev.sitouhandler.databinding.NavActivityTreasurerFinancialBinding;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class NavFragment_Treasurer_Financial extends Fragment {

    private static final String TAG = "Treasurer";

    private View rootView;
    private Context context;
    private String fileName;
    private long downloadId;

    private ArrayList<NavFragment_Treasurer_Financial_RecyclerModel> items;
    private NavActivityTreasurerFinancialBinding b;

    private Fetch fetch;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        b = NavActivityTreasurerFinancialBinding.inflate(inflater, container, false);
        rootView = b.getRoot();
        context = requireContext();

        Constant.verifyStoragePermissions(getActivity());
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            fetchData();
        }

        context.registerReceiver(brPermissionGranted, new IntentFilter(Dashboard.ACTION_REQUEST_PERMISSION_GRANTED));

        return rootView;
    }

    private void fetchData() {

        IssueRequestHandler req = new IssueRequestHandler(rootView);
        Call call = RetrofitClient.getInstance(null).getApiServices().getIssuedFinancial(
                Guru.getInt(Constant.KEY_MEMBER_ID, 0)
        );
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

            }
        });
        req.enqueue(call);

    }

    private void proceed(APIWrapper res) throws JSONException {
        items = new ArrayList<>();

        JSONObject data = res.getData();

        if (!data.isNull("bku")) {
            JSONArray bku = data.getJSONArray("bku");

            for (int i = 0; i < bku.length(); i++) {
                JSONObject obj = bku.getJSONObject(i);
                items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                        obj.getInt("id"),
                        obj.getString("month"),
                        obj.getString("year")
                ));
            }
        }

        if (!data.isNull("eval")) {
            JSONArray eval = data.getJSONArray("eval");
            for (int i = 0; i < eval.length(); i++) {
                JSONObject obj = eval.getJSONObject(i);
                items.add(new NavFragment_Treasurer_Financial_RecyclerModel(
                        obj.getInt("id"),
                        obj.getString("period"),
                        obj.getString("year"),
                        obj.getString("semester")
                ));
            }
        }

        if (!data.isNull("current_balance")){
            b.textViewCurrentBalance.setText(data.getString("current_balance"));
        }

        setupRecyclerView();
    }

    private boolean needToDisplayDialog = true;

    private void setupRecyclerView() {
        if (items == null || items.size() == 0) {
            if (b.layoutNotFound.getVisibility() != View.VISIBLE) {
                b.layoutNotFound.setVisibility(View.VISIBLE);
            }
            return;
        }

        if (b.layoutNotFound.getVisibility() == View.VISIBLE) {
            b.layoutNotFound.setVisibility(View.GONE);
        }

        b.recyclerViewMain.setHasFixedSize(false);
        b.recyclerViewMain.setLayoutManager(new LinearLayoutManager(context));
        b.recyclerViewMain.setItemAnimator(new DefaultItemAnimator());

        NavFragment_Treasurer_Financial_RecyclerAdapter adapter =
                new NavFragment_Treasurer_Financial_RecyclerAdapter(items, context);
        adapter.setOnItemClickListener(this::downloadData);

        b.recyclerViewMain.setAdapter(adapter);
    }

    private void downloadData(Bundle data) {

        int reportId = data.getInt("id");
        fileName = data.getString("file_name");

        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(3)
                .build();

        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        String url = Constant.ROOT_URL_DOWNLOAD_REPORT + reportId;

        final Request request = new Request(url, "/storage/emulated/0/Download/" + fileName);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);

        fetch.addListener(new FetchListener() {
            @Override
            public void onAdded(@NotNull Download download) {

            }

            @Override
            public void onQueued(@NotNull Download download, boolean bool) {

            }

            @Override
            public void onWaitingNetwork(@NotNull Download download) {
                Log.e(TAG, "onCompleted: download is waiting network");
            }

            @Override
            public void onCompleted(@NotNull Download download) {
                if (needToDisplayDialog) {
                    needToDisplayDialog = false;
                    Constant.displayDialog(
                            context,
                            "OK",
                            "Selesai mengunduh. silahkan sentuh tombol 'OK' untuk melihat file hasil unduhan",
                            false,
                            (dialog, which) -> {
                                needToDisplayDialog = true;
                                context.startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                            },
                            null
                    );
                }
            }

            @Override
            public void onError(@NotNull Download download, @NotNull Error error, @org.jetbrains.annotations.Nullable Throwable throwable) {

            }

            @Override
            public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {
                Log.e(TAG, "onStarted: download started");
            }

            @Override
            public void onProgress(@NotNull Download download, long l, long l1) {
                Log.e(TAG, "onProgress: download progress long l = " + l + " long l1 = " + l1);
            }

            @Override
            public void onPaused(@NotNull Download download) {

            }

            @Override
            public void onResumed(@NotNull Download download) {
            }

            @Override
            public void onCancelled(@NotNull Download download) {

            }

            @Override
            public void onRemoved(@NotNull Download download) {

            }

            @Override
            public void onDeleted(@NotNull Download download) {

            }
        });
        fetch.enqueue(
                request,
                result -> Constant.displayDialog(
                        context,
                        "Perhatian !",
                        "Sedang mengunduh",
                        false,
                        (dialog, which) -> {
                        },
                        null
                ),
                result -> {
                    Log.e(TAG, "onError: yup an error :D :", result.getThrowable());
                    Constant.displayDialog(
                            context,
                            "Perhatian !",
                            "Terjadi kesalahan silahkan coba lagi",
                            true,
                            (dialog, which) -> {
                            },
                            null
                    );
                }
        );
    }

    private BroadcastReceiver brPermissionGranted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Dashboard.ACTION_REQUEST_PERMISSION_GRANTED.contentEquals(action)) {
                fetchData();
            }
        }
    };


}
